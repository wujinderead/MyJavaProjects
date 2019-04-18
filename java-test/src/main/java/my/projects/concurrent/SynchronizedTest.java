package my.projects.concurrent;

import java.util.Date;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;

public class SynchronizedTest {
    public static void main(String[] args) throws Exception {
        testSync();
    }

    private static void testSync() throws Exception {
        class Tester {
            int a = 1;
            void act1() throws Exception {
                synchronized (this) {
                    out.println(currentThread().getName() + " enter sync at " + new Date());
                    this.a += 3;
                    Thread.sleep(2000);   // don't release lock
                    this.wait();               // release lock, wait other to notify
                }
                out.println(currentThread().getName() + " exit at " + new Date());
            }
            void act2() throws Exception {
                synchronized (this) {
                    out.println(currentThread().getName() + " enter sync at " + new Date());
                    this.a += 2;
                    this.notify();             // notify
                    Thread.sleep(2000);   // release lock
                }
                Thread.sleep(1000);
                out.println(currentThread().getName() + " exit at " + new Date());
            }
        }
        Tester tester = new Tester();
        Thread t1 = new Thread(() -> {try {tester.act1();}catch (Exception e){e.printStackTrace();}}, "T1");
        t1.start();
        Thread.sleep(100);
        Thread t2 = new Thread(() -> {try {tester.act2();}catch (Exception e){e.printStackTrace();}}, "T2");
        t2.start();
        t1.join();
        t2.join();
        out.println(tester.a);
    }
}
