package TCP;

import teoUtil.CircularQueue;
import teoUtil.TCPSegment;
import teoUtil.TSocket_base;

public class TSocket extends TSocket_base {

    //sender variable:
    protected int MSS;//<-- a teoria no fragmentarem
    private boolean ack = true;
    private TCPSegment segRet = null;
    private int numSeq = 0; //<-- l'identificador del
    //<-- proper segment a enviar
    private int finestraRec = 10;
    //<-- Variables per a estadístiques
    private int numSegEnviats = 0;
    private int numSegmentsSondeig = 0;

    //Variables de recepció
    private final CircularQueue<Byte> rcvQueue = new CircularQueue<>(10);
    private int numSegSeq = 0;//<-- l'identificador del proper segment a rebre

    //<-- Variables per a estadístiques
    private int numDescartats = 0;
    private long start_time = 0;

    //Completar
    protected TSocket(Protocol p, int localPort, int remotePort) {
        super(p.getNetwork());
        this.localPort = localPort;
        this.remotePort = remotePort;
        p.addActiveTSocket(this);
        MSS = 13;
        //Completar
    }

    //<-- executat per Sender
    @Override
    public void sendData(byte[] data, int offset, int length) {
        lock.lock();
        try {
            int enviats = 0;
            while (enviats < length) {
                mostrarEstadistiquesSnd();
                while (!ack) {//<-- stop & wait
                    appCV.awaitUninterruptibly();
                }
                int enviatsAct = Math.min(length - enviats, Math.max(finestraRec, 1));
                TCPSegment s = segmentize(data, offset + enviats, enviatsAct);
                //System.out.println("Enviat:" + s);
                enviats = enviats + enviatsAct;
                numSegEnviats = numSegEnviats + 1;
                if (finestraRec > 0) {
                    start_time = System.currentTimeMillis();
                    network.send(s);
                } else {
                    numSegmentsSondeig = numSegmentsSondeig + 1;
                }
                segRet = s;
                startRTO();
                ack = false;
                float byte_send_percent = enviats / length;
            }
        } finally {
            lock.unlock();
        }
    }

    protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment s = new TCPSegment();
        s.setData(data, offset, length);
        s.setPsh(true);
        s.setSeqNum(numSeq);
        numSeq = numSeq + length;
        //--------------
        s.setSourcePort(localPort);
        s.setDestinationPort(remotePort);
        return s;
    }

    @Override
    protected void timeout() {
        lock.lock();
        try {
            if (segRet != null) {
                //log.printPURPLE(segRet.toString());
                network.send(segRet);
                startRTO();
            }
        } finally {
            lock.unlock();
        }

    }

    //<-- executat per receiver
    @Override
    public int receiveData(byte[] buf, int offset, int length) {
        lock.lock();
        try {
            while (rcvQueue.empty()) {
                appCV.awaitUninterruptibly();
            }
            int i = 0;
            for (; i < length && !rcvQueue.empty(); i++) {
                buf[offset + i] = rcvQueue.get();
            }
            return i;
        } finally {
            lock.unlock();
        }
    }

    protected void sendAck() {
        TCPSegment sAck = new TCPSegment();
        sAck.setSourcePort(localPort);
        sAck.setDestinationPort(remotePort);
        sAck.setAck(true);
        sAck.setWnd(rcvQueue.free());
        network.send(sAck);
    }

    //<-- executat per ReceiverTask
    @Override
    public void processReceivedSegment(TCPSegment rseg) {
        lock.lock();
        try {
            //printRcvSeg(rseg);
            if (rseg.isAck()) {
                ack = true;
                appCV.signal();
                stopRTO();
                segRet = null;
                float velocity_rcv = estimationVelocityConsumeRcv(rseg.getSeqNum(),numSegSeq); // mitjana de 10 segments/segon
                System.out.println("Velocitat de recepció: " + velocity_rcv + " segments/s"); 
                finestraRec = rseg.getWnd();                
            }
            if (rseg.isPsh()) {
                if (rseg.getDataLength() > rcvQueue.free()) {
                    log.printRED("\t\t\t\t\t\t\t\tSegment descartat");
                    numDescartats = numDescartats + 1;
                    return;
                }
                if (rseg.getSeqNum() == numSegSeq) {
                    for (int i = 0; i < rseg.getDataLength(); i++) {
                        rcvQueue.put(rseg.getData()[i]);
                    }
                    appCV.signal();
                    numSegSeq = numSegSeq + rseg.getDataLength();
                }
                sendAck();
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void mostrarEstadistiquesSnd() {
        System.out.println("Estadístiques Socket Emissor:");
        System.out.println("S'han enviat " + numSegEnviats + " segments, ");
        System.out.println("dels quals " + numSegmentsSondeig + " són de sondeig");
    }

    @Override
    public void mostrarEstadistiquesRcv() {
        System.out.println("\t\t\t\t\t\t\t\tEstadístiques Socket Receptor:");
        System.out.println("\t\t\t\t\t\t\t\tS'han descartat " + numDescartats + " segments de sondeig ");
    }

    private float estimationVelocityConsumeRcv(int finestraActual, int finestraAnterior) {
        float elapsed_time = (System.currentTimeMillis() - start_time)*0.001f;
        float consumed_seg_rcv = finestraActual - finestraAnterior + 1; //diferencia entre finestra actual i la anterior
        float velocity_rcv = (float) (consumed_seg_rcv / elapsed_time); // segments per segon
        return velocity_rcv;
    }

}
