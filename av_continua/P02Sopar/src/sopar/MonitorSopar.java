package sopar;

import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.Condition;


public class MonitorSopar {

    private final int N, S, P;
    private HashMap<Integer,Integer> cua, comensals, sortir;
    private ReentrantLock lk;
    private Condition suficients_comensals,esperant_sortir, sopant;

    public MonitorSopar(int N, int S, int P) {
        this.N = N;
        this.S = S;
        this.P = P;
        this.comensals = new HashMap<Integer,Integer>();
        this.cua = new HashMap<Integer,Integer>();
        this.sortir = new HashMap<Integer,Integer>();
        this.lk = new ReentrantLock();
        this.esperant_sortir = this.lk.newCondition();
        this.sopant = this.lk.newCondition();      
        this.suficients_comensals = this.lk.newCondition();  
    }
    public void entrarUSB(int tipus, int id) {
        this.lk.lock();
        try{
            this.cua.put(id, tipus);
            System.out.print("El comensal " + id + " del tipus " + tipus + " entra a la cua   / ");
            System.out.println("Cua: " + this.cua.size() + " Comensals: " + this.comensals.size() + " Sortir: " + this.sortir.size());

            while(this.cua.size() < this.N)
            try{
                this.suficients_comensals.await();
            } catch(Exception e){}
            this.suficients_comensals.signalAll();

            while(((this.comensals.size()+this.sortir.size()) == this.S) || (!this.condicio(tipus)))
            try{
                if(!this.condicio(tipus)) System.out.println("El comensal " + id + " del tipus " + tipus + " no pot entrar a sopar");
                this.sopant.await();
            } catch(Exception e){}

            this.cua.remove(id);
            this.comensals.put(id, tipus);
            System.out.print("El comensal " + id + " del tipus " + tipus + " entra a sopar   / ");  
            System.out.println("Cua: " + this.cua.size() + " Comensals: " + this.comensals.size() + " Sortir: " + this.sortir.size());

        } finally{
            this.lk.unlock();
        }
    }

    public void sortirUSB(int tipus, int id) {
        this.lk.lock();
        try{
            this.comensals.remove(id);
            this.sortir.put(id, tipus);
            System.out.print("El comensal " + id + " del tipus " + tipus + " ha acabat de sopar   / ");
            System.out.println("Cua: " + this.cua.size() + " Comensals: " + this.comensals.size() + " Sortir: " + this.sortir.size());
            
            while(!this.comensals.isEmpty())
            try{
                this.esperant_sortir.await();
            } catch(Exception e){}

            this.sortir.remove(id);
            System.out.print("El comensal " + id + " del tipus " + tipus + " ha sortit   / ");
            System.out.println("Cua: " + this.cua.size() + " Comensals: " + this.comensals.size() + " Sortir: " + this.sortir.size());

            
            this.esperant_sortir.signal();
            if(this.sortir.isEmpty()) this.sopant.signalAll();
        } finally{
            this.lk.unlock();
        }
    }

    private boolean condicio(int tipus){
        boolean res = this.comensals.containsValue(tipus);
        if(!res) {
            Set<Integer> valorsDiferents = new HashSet<>(this.comensals.values());
            res = (valorsDiferents.size() < this.P-1);            
        }
        return res;
    }
}
