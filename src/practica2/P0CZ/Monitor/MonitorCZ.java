package practica2.P0CZ.Monitor;

import java.util.concurrent.locks.ReentrantLock;

public class MonitorCZ {

    private int x = 0;
    ReentrantLock rl = new ReentrantLock();

    public void inc() {
        //Incrementa en una unitat el valor d'x
        rl.lock();
        try{
            x++;
        }     
        catch(Exception e){}
        finally {
            rl.unlock();
        }
    }

    public int getX() {
        //Ha de retornar el valor d'x
        rl.lock();
        try{
            return x;    
        }finally{
            rl.unlock();
        }    
    }

}
