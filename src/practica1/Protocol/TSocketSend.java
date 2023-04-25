package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

  public TSocketSend(SimNet net) {
    super(net);
  }

  @Override
  public void sendData(byte[] data, int offset, int length) {
    
      TCPSegment seg = new TCPSegment(); 
      seg.setData(data,offset,length);
      seg.setPsh(true);
      printSndSeg(seg);
      network.send(seg);
  }
}
