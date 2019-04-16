package my.projects.concurrent;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class TimerTest {
    public static void main(String[] args) throws Exception {
        testTimer();
        //testDaemonTrue();
        //testDaemonFalse();
    }

    // TimerTask has a lock, needed when change task state.
    // Timer has a task queue (a priority queue, or a min heap, nextExecutionTime is the key),
    // need synchronize when change queue.
    // timer use single worker thread, if task delayed, it won't run task concurrently;
    // which is different from ScheduledThreadPoolExecutor
    private static void testTimer() throws Exception {
        Timer timer = new Timer();
        ThreadLocal<DateFormat> format = ThreadLocal.withInitial(() -> new SimpleDateFormat("hh:mm:ss.SSS"));
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task1 done at " + format.get().format(new Date()));
            }
        };
        TimerTask task2 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task2 done at " + format.get().format(new Date()));
            }
        };
        TimerTask task3 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task3 start at " + format.get().format(new Date()));
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<1400) {
                    //Thread.yield();
                }
                System.out.println("task3 done  at " + format.get().format(new Date()));
            }
        };
        TimerTask task4 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("task4 start at " + format.get().format(new Date()));
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<1400) {
                    //Thread.yield();
                }
                System.out.println("task4 done  at " + format.get().format(new Date()));
            }
        };
        System.out.println("now: " + new Date());
        timer.schedule(task1, 1000);
        timer.schedule(task2, 2000);
        timer.schedule(task3, 3000, 1000);
        Thread.sleep(10000);
        task3.cancel();
        timer.scheduleAtFixedRate(task4, 500, 1000);
        Thread.sleep(10000);
        timer.cancel();
    }

    // frontend thread, continue to run even main thread done.
    private static void testDaemonFalse() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start at " + new Date());
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<3000) {
                    Thread.yield();
                }
                System.out.println("done at " + new Date());
            }
        });
        thread.setDaemon(false);  // default is false
        thread.start();
    }

    // daemon (backend) thread, exit when main thread exit.
    private static void testDaemonTrue() throws Exception {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("start at " + new Date());
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<3000) {
                    Thread.yield();
                }
                System.out.println("done at " + new Date());
            }
        });
        thread.setDaemon(true);
        thread.start();
    }
}
