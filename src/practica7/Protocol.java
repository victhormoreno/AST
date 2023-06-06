package practica7;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

  protected Protocol(SimNet net) {
    super(net);
  }

  public void ipInput(TCPSegment segment) {
    TSocket_base socketRec = getMatchingTSocket(segment.getSourcePort(),segment.getDestinationPort());
    if(socketRec != null) socketRec.processReceivedSegment(segment);
  }

  protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
    lk.lock();
    try {
        for (TSocket_base as: activeSockets) 
            if((as.localPort == remotePort)&&(as.remotePort == localPort)){
                    return as;
            }
        for (TSocket_base as: listenSockets) 
            if(as.localPort == remotePort){
                    return as;
            }
        return null;    
    } finally {
      lk.unlock();
    }
  }
}
        