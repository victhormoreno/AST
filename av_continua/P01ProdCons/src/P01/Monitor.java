package P01;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Monitor {
     
    private final int N; //<-- grandària de l'espai compartit (si cal es pot considerar infinit)
    CircularQueue<Vector<Integer>> queue_a_verificar, queue_consumidor;
    HashMap<Integer,Vector<Integer>> hashmap_verificat;
    private ReentrantLock lk;
    private Condition full, empty, full2, empty2;

    
    public Monitor(int N) {
        this.N = N;
        this.queue_a_verificar = new CircularQueue<>(this.N);
        this.queue_consumidor = new CircularQueue<>(this.N);
        this.hashmap_verificat = new HashMap<Integer,Vector<Integer>>();
        this.lk = new ReentrantLock();
        this.empty = this.lk.newCondition();
        this.full = this.lk.newCondition();
        this.empty2 = this.lk.newCondition();
        this.full2 = this.lk.newCondition();

    }
    
    public void put(int[] sq) {
        this.lk.lock();
        try{
            while(this.queue_a_verificar.full())
            try{
                this.full.await();
            } catch(Exception e){}
            Vector<Integer> vec = new Vector<Integer>(sq.length);
            for(int i = 0; i < sq.length; i++){
                vec.addElement(sq[i]);
            }
            this.empty.signal();
            this.queue_a_verificar.put(vec);
        } finally{
            this.lk.unlock();
        }
    }
    
    public int verificar(int[] sq) {
        this.lk.lock();
        try{
            while(this.queue_a_verificar.empty())
            try{
                this.empty.await();
            } catch(Exception e){}
            int G = this.queue_a_verificar.getG();
            Vector<Integer> vec = this.queue_a_verificar.get();
            this.full.signal();
            sq[0] = vec.size();
            for(int i=0; i<vec.size(); i++){
                sq[i+1] = vec.elementAt(i);
            }
            hashmap_verificat.put(G, vec);
            return G;
        }finally{
            this.lk.unlock();
        }
    }
    
    public void resVerificar(boolean ok, int id) {
        this.lk.lock();
        try{
            try{
                while(this.queue_consumidor.full())
                this.full2.await();
            } catch(Exception e){
            }
            if(ok){
                this.queue_consumidor.put(hashmap_verificat.get(id));
                this.empty2.signal();
            } else {
                hashmap_verificat.get(id);
            }
        }finally{
            this.lk.unlock();
        }
    }
    // No ho he sapigut implementar tenint en compte el length
    public void get(int[] sq, int offset, int length) {
        this.lk.lock();
        try{
            while(this.queue_consumidor.empty())
            try{
                this.empty2.await();
            } catch(Exception e){
            }
            Vector<Integer> vec = this.queue_consumidor.get();
            for(int i=offset; i<offset+vec.size(); i++){
                sq[i] = vec.elementAt(i-offset);
            }
            this.full2.signal();
            
        }finally{
            this.lk.unlock();
        }    
    }
    
}
