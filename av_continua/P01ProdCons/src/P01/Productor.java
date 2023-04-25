package P01;

public class Productor extends Thread {

    private final Monitor mon;
    private final int id;
    private final int I = 2;
    private final int S = 10;

    public Productor(int id, Monitor mon) {
        this.id = id;
        this.mon = mon;
    }

    @Override
    public void run() {
        for (int i = 0; i < I; i++) {
            //<-- genera la seqüència de dades
            int[] seq = generarSeq();
            System.out.println("Productor " + id + " produeix " + seq.length + " dades");
            //<-- afegeix els valors de control
            int[] seqCon = afegirControl(seq);

            mon.put(seqCon);
        }
    }

    private int[] generarSeq() {
        int len = 1 + (int) (Math.random() * S);
        int[] sq = new int[len];
        for (int i = 0; i < len; i++) {
            //<-- obté una dada de forma aleatòria
            sq[i] = obtenirDada();
        }
        return sq;
    }

    private int obtenirDada() {
        return (int) (Math.random() * 100);
    }

    private int[] afegirControl(int[] seq) {
        int[] sq = new int[seq.length + 2];
        int sumPar = 0;
        int sumSen = 0;
        for (int i = 0; i < seq.length; i++) {
            sq[i] = seq[i];
            if (i % 2 == 0) {
                sumPar = (sumPar + seq[i]);
            } else {
                sumSen = (sumSen + seq[i]);
            }
        }
        sq[seq.length] = sumPar;
        sq[seq.length + 1] = sumSen;

        //<-- per a simular errors en el càlcul dels valors de control
        if (Math.random() < 0.1) {
            sq[seq.length + 1] = sumSen + 1;
        }

        return sq;
    }
}
