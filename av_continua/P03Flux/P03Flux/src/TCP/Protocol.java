package TCP;

import teoUtil.Teo_Protocol_base;
import teoUtil.TCPSegment;
import teoUtil.SimNet;
import teoUtil.TSocket_base;

public class Protocol extends Teo_Protocol_base {

    public Protocol(SimNet net) {
        super(net);
    }

    //<-- inici demultiplexat
    @Override
    protected void ipInput(TCPSegment seg) {
        int portOrigen = seg.getSourcePort();
        int portDestí = seg.getDestinationPort();
        TSocket_base t = getMatchingTSocket(portDestí, portOrigen);
        if(t!=null){
            t.processReceivedSegment(seg);
        }
    }

    @Override
    protected TSocket_base getMatchingTSocket(int localPort, int remotePort) {
        return activeSockets.get(localPort+"/"+remotePort);
    }
}
