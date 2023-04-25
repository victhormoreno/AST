package P01;

import java.util.Random;

public class Verificador extends Thread {

    private final Monitor mon;
    private final int id;
    private final int S = 13;
    private final Random r = new Random(10);

    public Verificador(int id, Monitor mon) {
        this.id = id;
        this.mon = mon;
    }

    @Override
    public void run() {
        while (true) {
            int[] sq = new int[S];
            
            //<-- obté la seqüència que s'ha de verificar
            int idSq = mon.verificar(sq);
            System.out.println("Verificador " + id + " verifica " + sq[0] + " dades");
            //<-- verifica la seqüència
            boolean ok = verificar(sq);
            
            //<-- passa el resultat del procés al monitor
            mon.resVerificar(ok, idSq);
        }
    }

    private boolean verificar(int[] sq) {
        int sumPar = 0;
        int sumSen = 0;
        //<-- recordar que sq[0] és la longitud de la seqüència a verificar
        for (int i = 0; i < sq[0] - 2; i++) {
            if (i % 2 == 0) {
                sumPar = (sumPar + sq[i + 1]);
            } else {
                sumSen = (sumSen + sq[i + 1]);
            }
        }

        boolean ret = sumPar == sq[sq[0] - 1] && sumSen == sq[sq[0]];
        return ret;
    }

}
