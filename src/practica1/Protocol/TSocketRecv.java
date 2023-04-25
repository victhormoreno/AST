package practica1.Protocol;

import util.TCPSegment;
import util.TSocket_base;
import util.SimNet;

public class TSocketRecv extends TSocket_base {

    public TSocketRecv(SimNet net) {
        super(net);
    }

    @Override
    public int receiveData(byte[] data, int offset, int length) {
        TCPSegment seg = network.receive();
        printRcvSeg(seg);
        for (int i = offset; i < (offset + length); i++) {
            if ((i - offset) < seg.getDataLength()) {
                data[i] = seg.getData()[i - offset];
            }
        }
        return (length < seg.getDataLength()) ? length : seg.getDataLength();
    }
}
