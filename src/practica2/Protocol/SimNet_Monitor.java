package practica2.Protocol;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import practica1.CircularQ.CircularQueue;
import util.Const;
import util.TCPSegment;
import util.SimNet;

public class SimNet_Monitor implements SimNet {

    protected CircularQueue<TCPSegment> queue;
    private ReentrantLock lock;
    private Condition full, empty;

    public SimNet_Monitor() {
        this.queue = new CircularQueue<>(Const.SIMNET_QUEUE_SIZE);
        this.lock = new ReentrantLock();
        this.empty = this.lock.newCondition();
        this.full = this.lock.newCondition();
    }

    @Override
    public void send(TCPSegment seg) {
        this.lock.lock();
        try {
            while (this.queue.full())
            try {
                this.full.await();
            } catch (Exception e) {}
            
            this.empty.signal();
            this.queue.put(seg);
        }finally {
            this.lock.unlock();
        }
    }

    @Override
    public TCPSegment receive() {
        this.lock.lock();
        try {
            while (this.queue.empty())
            try {
                this.empty.await();
            } catch (Exception e) {}
            this.full.signal();
            return this.queue.get();
        } finally {
            this.lock.unlock();
        }
    }

    @Override
    public int getMTU() {
        throw new UnsupportedOperationException("Not supported yet. NO cal completar fins a la pràctica 3...");
    }

}
