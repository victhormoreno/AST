package practica3;

import util.Const;
import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketSend extends TSocket_base {

    protected int MSS;       // Maximum Segment Size

    public TSocketSend(SimNet net) {
        super(net);
        MSS = net.getMTU() - Const.IP_HEADER - Const.TCP_HEADER;
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
        printSndSeg(seg);
        return seg;
    }

}
