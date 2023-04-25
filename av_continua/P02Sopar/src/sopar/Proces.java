package sopar;

public class Proces extends Thread {

    private final int id;
    private final int tipus;
    private final MonitorSopar mon;

    public Proces(int id, int tipus, MonitorSopar mon) {
        this.id = id;
        this.mon = mon;
        this.tipus = tipus;
    }

    @Override
    public void run() {
        while (true) {
            espera(1000);
            mon.entrarUSB(tipus, id);

            espera(100);
            mon.sortirUSB(tipus, id);

        }
    }

    private void espera(int ms) {
        try {
            sleep((int) (Math.random() * ms));
        } catch (InterruptedException ex) {

        }
    }

}
