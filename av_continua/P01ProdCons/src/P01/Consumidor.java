package P01;

public class Consumidor extends Thread {

    private final Monitor mon;
    private final int id;
    private final int S = 20;
    private final int maxOFF = 15;

    public Consumidor(int id, Monitor mon) {
        this.id = id;
        this.mon = mon;
    }

    @Override
    public void run() {
        while (true) {
            int[] sq = new int[S];
            int offset = (int) (Math.random() * maxOFF);
            int len = 1 + (int) (Math.random() * (maxOFF - offset));
            mon.get(sq, offset, len);
            System.out.println("Consumidor " + id + " consumeix "+len+" dades");
        }
    }
}
