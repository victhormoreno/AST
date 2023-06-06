package teoUtil;

import java.util.HashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class Teo_Protocol_base {

    protected SimNet network;
    protected Lock lk;
    protected HashMap<String, TSocket_base> listenSockets;//<-- connexió/descon
    //<-- llista de sockets que formen part d'una connexió
    protected HashMap<String, TSocket_base> activeSockets;

    protected Log log;

    public Teo_Protocol_base(SimNet net) {
        network = net;
        lk = new ReentrantLock();
        listenSockets = new HashMap(); // <-- sockets listen (ServerSocket)
        activeSockets = new HashMap<>(); // <-- sockets actius (Socket)
        log = Log.getLog();
        new Thread(new ReceiverTask()).start();
    }

    protected abstract void ipInput(TCPSegment segment);

    protected abstract TSocket_base getMatchingTSocket(int localPort, int remotePort);

    public SimNet getNetwork() {
        return network;
    }

    //-------------------------------------------
    /**
     * Add a TSock to list of listen TSocks. We assume the TSock is in state
     * LISTEN.
     */
    public void addListenTSocket(TSocket_base sc) {
        lk.lock();
        listenSockets.put(sc.localPort + "/0", sc);
        lk.unlock();
    }

    /**
     * Add a TSock to list of active TSocks. We assume the TSock is in an active
     * state.
     */
    public void addActiveTSocket(TSocket_base sc) {
        lk.lock();
        try {
            activeSockets.put(sc.localPort + "/" + sc.remotePort, sc);
        } finally {
            lk.unlock();
        }
    }

    /**
     * Remove a TSock from list of listen TSocks. We assume the TSock is in
     * CLOSED state.
     */
    public void removeListenTSocket(TSocket_base sc) {
        lk.lock();
        listenSockets.remove(sc.localPort + "/0");
        lk.unlock();
    }

    /**
     * Remove a TSock from list of active TSocks. We assume the TSock is in
     * CLOSED state.
     */
    public void removeActiveTSocket(TSocket_base sc) {
        lk.lock();
        activeSockets.remove(sc.localPort + "/" + sc.remotePort);
        lk.unlock();
    }

    //-------------------------------------------
    class ReceiverTask implements Runnable {

        @Override
        public void run() {
            while (true) {
                TCPSegment rseg = network.receive();
                ipInput(rseg);
            }
        }
    }

}
