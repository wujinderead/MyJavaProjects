package my.projects.concurrent;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Date;
import java.util.concurrent.locks.LockSupport;

import static java.lang.System.currentTimeMillis;
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

    private static void testInterrupt() throws Exception {
        long start = currentTimeMillis();
        Thread thread = new Thread(() -> {
            try {
                out.println("start at " + (currentTimeMillis()-start));
                Thread.sleep(3000);
                out.println("sleep end: " + (currentTimeMillis()-start));
            } catch (InterruptedException e) {
                out.println("interrupted: " + (currentTimeMillis()-start));
                e.printStackTrace();
            }
            out.println("end: " + (currentTimeMillis()-start));
        });
        thread.start();
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        ThreadInfo info = bean.getThreadInfo(thread.getId());
        out.println("== info: " + info);
        Thread.sleep(1000);
        thread.interrupt();     // interrpted thread while sleeping
        out.println("main inter: " + (currentTimeMillis()-start));
    }

    private static void testPark() throws Exception {
        long start = currentTimeMillis();
        Thread thread = new Thread(() -> {
            while ((currentTimeMillis()-start<200)) { }
            out.println("park at " + (currentTimeMillis()-start));
            LockSupport.park();       // no wait here, as there is permit (unpark before)
            out.println("park at " + (currentTimeMillis()-start));
            LockSupport.park();       // wait here, as permit has been consumed; wait ends when unpark
            out.println("end: " + (currentTimeMillis()-start));
        });
        thread.start();
        Thread.sleep(100);
        out.println("main unpark at: " + (currentTimeMillis()-start));
        LockSupport.unpark(thread);
        // this take no effect, there is only one permit
        out.println("main unpark at: " + (currentTimeMillis()-start));
        LockSupport.unpark(thread);

        Thread.sleep(500);
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        ThreadInfo info = bean.getThreadInfo(thread.getId());
        out.println("== info: " + info);

        out.println("main unpark at: " + (currentTimeMillis()-start));
        LockSupport.unpark(thread);
    }

    private static void testParkInterrupt() throws Exception {
        long start = currentTimeMillis();
        Thread thread = new Thread(() -> {
            out.println("park at " + (currentTimeMillis()-start));
            // park can be interrupted
            // blocker is to record what block this thread
            LockSupport.park(new Object());
            out.println("end: " + (currentTimeMillis()-start));
        });
        thread.start();

        Thread.sleep(300);
        ThreadMXBean bean = ManagementFactory.getThreadMXBean();
        ThreadInfo info = bean.getThreadInfo(thread.getId());
        Object obj = LockSupport.getBlocker(thread);
        out.println("== info: " + info);
        out.println("== blocker: " + obj.getClass());

        out.println("main interrupt at: " + (currentTimeMillis()-start));
        thread.interrupt();

        Thread.sleep(200);
        out.println("main unpark at: " + (currentTimeMillis()-start));
        LockSupport.unpark(thread);
    }
}
