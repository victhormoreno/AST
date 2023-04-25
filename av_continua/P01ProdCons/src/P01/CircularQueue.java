package P01;

import java.util.Iterator;
import util.Queue;

public class CircularQueue<E> implements Queue<E> {

    private final E[] queue;
    private final int N;
    private int n;
    private int P;
    private int G;

    public CircularQueue(int N) {
        this.N = N;
        queue = (E[]) (new Object[N]);
        this.n = 0;
        this.G = 0;
        this.P = 0;
    }

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public int free() {
        return (N - this.n);
    }

    @Override
    public boolean empty() {
        return this.n == 0;
    }

    @Override
    public boolean full() {
        return this.n == this.N;
    }

    @Override
    public E peekFirst() {
        return empty() ? null : queue[this.G];
    }

    @Override
    public E get() {
        E head = peekFirst();
        if (empty()) {
            throw new IllegalStateException("queue is empty");
        }

        if ((this.G + 1) % this.N == 0) {
            this.G = 0;
        } else {
            this.G++;
        }

        this.n--;

        return head;
    }

    @Override
    public void put(E e) {

        if (full()) {
            throw new IllegalStateException("queue is full");
        }
        queue[P] = e;
        if ((this.P + 1) % this.N == 0) {
            this.P = 0;
        } else {
            this.P++;
        }
        this.n++;
    }

    @Override
    public String toString() {

        String s = "[";
        for (int i = 0; i < this.n; i++) {
            s = s + queue[(i + G) % N].toString();
            if (i != this.n - 1) {
                s = s.concat(",");
            }
        }
        s = s.concat("]");
        return s;
    }

    public int getG() {
        return this.G;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        private int it = 0;
        private boolean flag = false;

        @Override
        public boolean hasNext() {
            return this.it < n;
        }

        @Override
        public E next() {
            this.it++;
            this.flag = true;
            return queue[(this.it + G - 1) % N];//<-- hauria de ser:[(this.it + G - 1 + N) % N]
        }

        @Override
        public void remove() {
            if (this.flag) {
                for (int i = 0; i < (n - this.it); i++) {
                    //<-- hauria de ser: queue[(this.it + G + i - 1+ N) % N] = ...
                    queue[(this.it + G + i - 1) % N] = queue[(this.it + G + i) % N];
                }
                n--;
                P = (P - 1 + N) % N;
                this.flag = false;
                this.it--;
            }
        }

    }
}
