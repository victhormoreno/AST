package TCP;

import teoUtil.SimNet_FullDuplex;
import teoUtil.Receiver;
import teoUtil.Sender;
import teoUtil.SimNet;

public class Test {

    public static void main(String[] args) {

        SimNet_FullDuplex net = new SimNet_FullDuplex(0.05, 0.05);

        new Thread(new HostRcv(net.getRcvEnd())).start();
        new Thread(new HostSnd(net.getSndEnd())).start();
    }
}

class HostRcv implements Runnable {

    public static final int PORT = 80;

    protected Protocol proto;

    public HostRcv(SimNet net) {
        this.proto = new Protocol(net);
    }

    @Override
    public void run() {
        new Receiver(new TSocket(proto, HostRcv.PORT, HostSnd.PORT1), 1, 50).start();
    }
}

class HostSnd implements Runnable {

    public static final int PORT1 = 20;
    public static final int PORT2 = 30;

    protected Protocol proto;

    public HostSnd(SimNet c) {
        this.proto = new Protocol(c);
    }

    @Override
    public void run() {
        new Sender(new TSocket(proto, HostSnd.PORT1, HostRcv.PORT), 1, 500, 1).start();
    }
}
