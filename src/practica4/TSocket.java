package practica4;

import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.TSocket_base;

public class TSocket extends TSocket_base {

  //sender variable:
  protected int MSS;

  //receiver variables:
  protected CircularQueue<TCPSegment> rcvQueue;
  protected int rcvSegConsumedBytes;

  protected TSocket(Protocol p, int localPort, int remotePort) {
    super(p.getNetwork());
    this.localPort  = localPort;
    this.remotePort = remotePort;
    p.addActiveTSocket(this);
    MSS = network.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
    rcvQueue = new CircularQueue<>(Const.RCV_QUEUE_SIZE);
    rcvSegConsumedBytes = 0;
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    int i;
    for (i = 0; i < (length / this.MSS); i++) {
        network.send(this.segmentize(data, offset + i * this.MSS, this.MSS));
    }

    if (length % this.MSS != 0) {
        network.send(this.segmentize(data, offset + i * this.MSS, length % this.MSS));
    }  
  }

  protected TCPSegment segmentize(byte[] data, int offset, int length) {
        TCPSegment seg = new TCPSegment();
        seg.setData(data, offset, length);
        seg.setPsh(true);
        seg.setSourcePort(localPort);
        seg.setDestinationPort(remotePort);
        printSndSeg(seg);
        return seg;  
  }

  @Override
  public int receiveData(byte[] buf, int offset, int length) {
    lock.lock();
    try {
        while(this.rcvQueue.empty())
        try{
            appCV.await();
        }catch(Exception e){}
        int a = 0;
        while(a<length&&(!this.rcvQueue.empty()))
            a += consumeSegment(buf,offset+a,length-a); 
        return a;
    } finally {
      lock.unlock();
    }
  }

  protected int consumeSegment(byte[] buf, int offset, int length) {
    TCPSegment seg = rcvQueue.peekFirst();
    int a_agafar = Math.min(length, seg.getDataLength() - rcvSegConsumedBytes);
    System.arraycopy(seg.getData(), rcvSegConsumedBytes, buf, offset, a_agafar);
    rcvSegConsumedBytes += a_agafar;
    if (rcvSegConsumedBytes == seg.getDataLength()) {
      rcvQueue.get();
      rcvSegConsumedBytes = 0;
    }
    return a_agafar;
  }

  protected void sendAck() {
        TCPSegment ack = new TCPSegment();
        ack.setSourcePort(localPort);
        ack.setDestinationPort(remotePort);
        ack.setAck(true);
        network.send(ack);
  }

  @Override
  public void processReceivedSegment(TCPSegment rseg) {

    lock.lock();
    try {
        printRcvSeg(rseg);
        if((!this.rcvQueue.full()&&(rseg.isPsh()))){
            this.rcvQueue.put(rseg);
            sendAck();
            appCV.signal();
        } 
        if (rseg.isAck()){    
        //nothing to be done in this exercise.
        }
    } finally {
      lock.unlock();
    }
  }

}
