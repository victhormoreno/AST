package practica4;

import util.Protocol_base;
import util.TCPSegment;
import util.SimNet;
import util.TSocket_base;

public class Protocol extends Protocol_base {

    public Protocol(SimNet net) {
      super(net);
    }

    protected void ipInput(TCPSegment seg) {
        TSocket_base socketRec = getMatchingTSocket(seg.getSourcePort(),seg.getDestinationPort());
        if(socketRec != null) socketRec.processReceivedSegment(seg);
    }

    protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
        lk.lock();
        try {
            for (TSocket_base as: activeSockets) 
                if((as.localPort == remotePort)&&(as.remotePort == localPort)){
                    return as;
                }
            return null;
        } finally {
            lk.unlock();
        }
    }
}
