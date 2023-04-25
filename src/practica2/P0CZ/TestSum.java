package practica2.P0CZ;

public class TestSum {

    public static void main(String[] args) throws InterruptedException {

        CounterThread ct = new CounterThread();
        Thread t1 = new Thread(ct);
        Thread t2 = new Thread(ct);
        t1.start();
        t2.start();
        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {}
        System.out.println(ct.x);
    }
}
