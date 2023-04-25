package P01;

public class Principal {

    public static void main(String[] args) throws InterruptedException {
        int NumProd = 2;
        int NumVer = 2;
        int NumCons = 2;
        Monitor mon = new Monitor(100);

        for (int i = 0; i < NumProd; i++) {
            new Productor(i, mon).start();
        }
        for (int i = 0; i < NumVer; i++) {
            new Verificador(i, mon).start();
        }
        for (int i = 0; i < NumCons; i++) {
            new Consumidor(i, mon).start();
        }

    }
}
