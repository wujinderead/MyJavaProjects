package my.projects.concurrent;

import java.util.concurrent.SynchronousQueue;
import static java.lang.System.out;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;

public class SynchronousQueueTest {
    public static void main(String[] args) throws Exception {
        testSynchronousQueue();
    }

    // one 'put', one 'take'. if no 'take', 'put' hangs; if no 'put', 'take' hangs.
    private static void testSynchronousQueue() throws Exception {
        SynchronousQueue<String> queue = new SynchronousQueue<>(true);
        final long start = currentTimeMillis();
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            String a = "aa";
            try {
                queue.put(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(currentThread().getName() + " add at " + (currentTimeMillis()-start));
        }, "P1").start();
        Thread.sleep(100);
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            String a = "bb";
            try {
                queue.put(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(currentThread().getName() + " add at " + (currentTimeMillis()-start));
        }, "P2").start();
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            String a = "cc";
            try {
                queue.put(a);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(currentThread().getName() + " add at " + (currentTimeMillis()-start));
        }, "P3").start();
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            String a = "";
            try {
                a = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(currentThread().getName() + " get '" + a + "' at " + (currentTimeMillis()-start));
        }, "T1").start();
        Thread.sleep(100);
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            String a = "";
            try {
                a = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(currentThread().getName() + " get '" + a + "' at " + (currentTimeMillis()-start));
        }, "T2").start();
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            String a = "";
            try {
                a = queue.take();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            out.println(currentThread().getName() + " get '" + a + "' at " + (currentTimeMillis()-start));
        }, "T3").start();
    }
}
