package practica1.LinkedQ;

import java.util.Arrays;

public class TestLQ {

  public static void main(String[] args) {
    LinkedQueue<Integer> q = new LinkedQueue<>();
    System.out.println("Size = " + q.size());
    for (int i = 1; i<=6; i++){
        try {
            q.put(i);
        }catch(Exception e){
            System.out.println(e.toString());
        }
        System.out.println("Size = " + q.size());
        if(q.empty())
            System.out.println("empty!");
        else if(q.full())
            System.out.println("full!");
    }
    
    System.out.println("Fisrt: "+q.peekFirst());
    
    for (int i = 5; i>=0; i--){
        int j = 0;
        try{
            j = q.get();
        }catch(Exception e){
            System.out.println(e.toString());
        }
        System.out.println("Get: " + j + " Size = " + q.size());
        if(q.empty())
            System.out.println("empty!");
        else if(q.full())
            System.out.println("full!");
        try{
            System.out.println("First: "+q.peekFirst());
        }catch(Exception e){
            System.out.println(e.toString());
        }
    }
  }
}
