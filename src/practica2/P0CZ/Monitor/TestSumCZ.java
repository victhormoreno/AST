package practica2.P0CZ.Monitor;

public class TestSumCZ {

    public static void main(String[] args) throws InterruptedException {
    
        MonitorCZ m = new MonitorCZ();
        CounterThreadCZ ct = new CounterThreadCZ(m);
        
        Thread t1 = new Thread(ct);
        Thread t2 = new Thread(ct);
        t1.start();
        t2.start();
        try{
            t1.join();
            t2.join();
        }catch(InterruptedException e){}
        
        System.out.println("x: "+ m.getX());

    }
}
