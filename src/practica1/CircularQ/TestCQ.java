package practica1.CircularQ;

import java.util.Arrays;

public class TestCQ {

  public static void main(String[] args) {
    CircularQueue<Integer> q = new CircularQueue<>(5);
    q.put(3);
    q.put(4);
    System.out.println(q.toString());
    
    q.get();
    q.put(2);
    System.out.println(q.toString());

    q.get();
    q.put(50);
    q.put(20);
    q.put(30);
    q.put(10);
    System.out.println(q.toString());
    
    q.get();
    q.get();
    System.out.println(q.toString());

    q.get();
    q.get();
    q.get();
    
    System.out.println(q.toString());
    q.get();


  }
}
