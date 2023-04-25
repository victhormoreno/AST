package practica1.LinkedQ;

import java.util.Iterator;
import util.Queue;

public class LinkedQueue<E> implements Queue<E> {

    private int n = 0;
    private Node<E> first;
    private Node<E> end;

    @Override
    public int size() {
        return this.n;
    }

    @Override
    public int free() {
        return -1;
    }

    @Override
    public boolean empty() {
        return (this.n == 0);
    }

    @Override
    public boolean full() {
        return false;
    }

    @Override
    public E peekFirst() {
        if (this.empty()) {
            return null;
        } else {
            return first.getValue();
        }
    }

    @Override
    public E get() {
        if (empty()) {
            throw new IllegalStateException("Linked Queue is empty");
        }
        this.n--;
        first = first.getNext();
        return peekFirst();//<-- com que ja has avançat first, no retorna el valor que toca!!!!
    }

    @Override
    public void put(E e) {
        Node<E> node = new Node<>();
        node.setValue(e);
        if (this.n == 0) {
            this.first = node;
        } else {
            end.setNext(node);
        }
        this.end = node;
        this.n++;
    }

    @Override
    public String toString() {
        String s = "[";
        Node<E> tmp = this.first;
        for (int i = 0; i < this.size(); i++) {
            s = s.concat(tmp.getValue().toString());
            tmp = tmp.getNext();
        }
        s = s.concat("]");
        return s;
    }

    @Override
    public Iterator<E> iterator() {
        return new MyIterator();
    }

    class MyIterator implements Iterator {

        private int it = 0;
        private Node<E> n;
        private Node<E> prev;

        @Override
        public boolean hasNext() {
            //return it < LinkedQueue.this.size();
            return it < size();
        }

        @Override
        public E next() {
            if (!this.hasNext()) {
                throw new IllegalStateException("No hi ha més elements a la Cua!");
            }
            if (it == 0) {
                //this.n = LinkedQueue.this.first;
                n = first;
            } else {
                this.prev = this.n;
                this.n = this.n.getNext();
            }
            E elem = this.n.getValue();
            this.it++;
            return elem;
        }

        @Override
        public void remove() {
            if (this.it == 0) {
                throw new IllegalStateException("No s'ha fet Next()");
            } else if (!this.hasNext()) {
                //LinkedQueue.this.end = this.prev;
                end = this.prev;
            } else if (this.it == 1) {
                //LinkedQueue.this.first = this.n.getNext();
                first = this.n.getNext();
            } else {
                this.prev.setNext(this.n.getNext());
            }
            this.n = this.prev;
            LinkedQueue.this.n--;           
            this.it--;

        }

    }
}
