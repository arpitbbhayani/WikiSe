package com.wikise.util;

import java.util.HashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by Arpit Bhayani on 7/2/14.
 */
public class Main {
    public static void main(String[] args) {

        ConcurrentLinkedQueue<String> q = new ConcurrentLinkedQueue<String>();
        SimpleThread t1 = new SimpleThread("Jamaica",q);
        SimpleThread t2 = new SimpleThread("Fiji",q);

        t1.start();
        t2.start();


        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(q.size());
        while ( !q.isEmpty() ) {
            System.out.println(q.poll());
        }

    }
}

class SimpleThread extends Thread {
    ConcurrentLinkedQueue<String> q = null;
    String str = null;
    public SimpleThread(String str , ConcurrentLinkedQueue<String> q) {
        this.q = q;
        this.str = str;
    }
    public void run() {
        for (int i = 0; i < 10; i++) {
            //System.out.println(i + " " + getName());
            q.add(str);
        }
        //System.out.println("DONE! " + getName());
    }
}
