package practica5;

import practica1.CircularQ.CircularQueue;
import practica4.Protocol;
import util.Const;
import util.TSocket_base;
import util.TCPSegment;

public class TSocket extends TSocket_base {

  // Sender variables:
  protected int MSS;
  protected int snd_sndNxt;
  protected int snd_rcvWnd;
  protected int snd_rcvNxt;
  protected TCPSegment snd_UnacknowledgedSeg;
  protected boolean zero_wnd_probe_ON;

  // Receiver variables:
  protected CircularQueue<TCPSegment> rcv_Queue;
  protected int rcv_SegConsumedBytes;
  protected int rcv_rcvNxt;

  protected TSocket(Protocol p, int localPort, int remotePort) {
    super(p.getNetwork());
    this.localPort = localPort;
    this.remotePort = remotePort;
    p.addActiveTSocket(this);
    // init sender variables
    MSS = p.getNetwork().getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
    // init receiver variables
    //rcv_Queue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    rcv_Queue = new CircularQueue<>(5);
    snd_rcvWnd = Const.RCV_QUEUE_SIZE;
    snd_sndNxt = 0;
    snd_rcvNxt = 0;
    rcv_rcvNxt = 0;
    snd_UnacknowledgedSeg = null;
    zero_wnd_probe_ON = false;
  }

  // -------------  SENDER PART  ---------------
  @Override
  public void sendData(byte[] data, int offset, int length) {
    lock.lock();
    try {           
        int consume_bytes = 0, consume = 0; 
        while(consume_bytes<length){
            
            while(this.snd_sndNxt!=this.snd_rcvNxt)
            try{
                appCV.await();
            }catch(Exception e){}
            
            TCPSegment seg;
            if(this.snd_rcvWnd > 0){
                consume = Math.min((length - consume_bytes), this.MSS);
                this.zero_wnd_probe_ON = false;
                seg = this.segmentize(data, offset + consume_bytes, consume);
                network.send(seg);
                consume_bytes += consume;
            } else{
                consume = 1;
                this.zero_wnd_probe_ON = true;
                seg = this.segmentize(data, offset + consume_bytes, consume);
                consume_bytes += consume;
            }
            snd_UnacknowledgedSeg = seg;
            super.startRTO();
            snd_sndNxt++;  
        }
    }finally {
      lock.unlock();
    }
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setData(data, offset, length);
        seg.setPsh(true);
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        seg.setSeqNum(this.snd_sndNxt);
        if(this.snd_rcvWnd > 0) printSndSeg(seg);
        return seg;    
}

  @Override
  protected void timeout() {
    lock.lock();
    try{
        if(snd_UnacknowledgedSeg != null) {
            if(zero_wnd_probe_ON) {
                log.printPURPLE("0−wnd probe: " + snd_UnacknowledgedSeg) ;
            }else{
                log.printPURPLE("retrans: " + snd_UnacknowledgedSeg) ;
            }
        network.send(snd_UnacknowledgedSeg);
        startRTO();
        }
    } finally {
      lock.unlock();
    }
  }

  // -------------  RECEIVER PART  ---------------
  @Override
  public int receiveData(byte[] buf, int offset, int maxlen) {
    lock.lock();
    try {
        while(this.rcv_Queue.empty())
        try{
            appCV.await();
        }catch(Exception e){}
        int a = 0;
        while(a<maxlen&&(!this.rcv_Queue.empty()))
            a += consumeSegment(buf,offset+a,maxlen-a); 
        return a;
    } finally {
      lock.unlock();
    }
  }

  protected int consumeSegment(byte[] buf, int offset, int length) {
    TCPSegment seg = rcv_Queue.peekFirst();
    int a_agafar = Math.min(length, seg.getDataLength() - rcv_SegConsumedBytes);
    System.arraycopy(seg.getData(), rcv_SegConsumedBytes, buf, offset, a_agafar);
    rcv_SegConsumedBytes += a_agafar;
    if (rcv_SegConsumedBytes == seg.getDataLength()) {
      rcv_Queue.get();
      rcv_SegConsumedBytes = 0;
    }
    return a_agafar;
  }

  protected void sendAck() {
    TCPSegment ack = new TCPSegment();
    ack.setSourcePort(localPort);
    ack.setDestinationPort(remotePort);
    ack.setAck(true);
    ack.setAckNum(this.rcv_rcvNxt);
    ack.setWnd(this.rcv_Queue.free());
    network.send(ack);  
  }

  // -------------  SEGMENT ARRIVAL  -------------
  @Override
  public void processReceivedSegment(TCPSegment rseg) {

    lock.lock();
    try {
        printRcvSeg(rseg);
        if((!this.rcv_Queue.full()&&(rseg.isPsh()))){
            // Puting segment in the rcv_Queue
            this.rcv_Queue.put(rseg);
            
            // Updating protocol params and sending ack
            this.rcv_rcvNxt++;
            sendAck();
            
            // wake-up process
            appCV.signal();
        } 
        if (rseg.isAck()){ 
            this.snd_rcvNxt++;
            snd_UnacknowledgedSeg = null;
            this.snd_rcvWnd = rseg.getWnd(); 
            appCV.signal();
        }
    } finally {
      lock.unlock();
    }
  }
}
