package practica2.P1Sync.Monitor;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class MonitorSync {

    private final int N;
    private int turnID;
    private ReentrantLock lock = new ReentrantLock();
    private Condition[] turn;

    public MonitorSync(int N) {
        this.N = N;
        turnID = 0;
        this.turn = new Condition[N];
        for (int i = 0; i < N; i++) {
            turn[i] = this.lock.newCondition();
        }
    }

    //<-- aquesta solució no funciona pel cas en que hi hagi més d'un procés amb el mateix id.
    public void waitForTurn(int id) {
        lock.lock();
        while (this.turnID != id)
            try {
            this.turn[id].await();
        } catch (InterruptedException ex) {
        }
        lock.unlock();
    }

    public void transferTurn() {
        lock.lock();
        this.turnID = (turnID + 1) % N;
        turn[this.turnID].signal();
        lock.unlock();
    }
}
