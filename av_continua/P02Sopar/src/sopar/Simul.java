package sopar;

public class Simul {

    public static void main(String[] args) {
        int P = 5; //<-- tipus de processos
        int S = 12;//<-- Places disponibles a l'USB
        int N = 10;//<-- Mínim número de processos necessaris per a començar un sopar
        MonitorSopar mon = new MonitorSopar(N, S, P);
        for (int i = 0; i < 300; i++) {//<-- en aquesta simulació hi ha 300 processos i 300/P de cada tipus
            new Proces(i, i % P, mon).start();
        }
        //<-- notar que la simulació no acaba, ja que els processos no acaben
    }

}
