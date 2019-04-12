package my.projects.concurrent;

import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import static java.lang.Thread.currentThread;
import static java.lang.System.currentTimeMillis;

public class ReentrantLockTest {
    public static void main(String[] args) throws Exception {
        //testCondition();
        testReentrantLock();
    }

    private static void testCondition() throws Exception {
        // use a class to contain value
        class A {
            volatile int a;
        }
        A a= new A();
        a.a = 0;
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        long start = currentTimeMillis();

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(currentThread().getName() + " get lock, " + (currentTimeMillis()-start));
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<600) {
                    // do nothing,
                }
                while (a.a!=2) {
                    System.out.println(currentThread().getName() + " condition unmet, continue wait, " + (currentTimeMillis()-start));
                    // await for condition, give up the lock, so other thread can acquire the lock.
                    // when other thread invoke 'condition.signal()', current thread can continue to run.
                    // however, current thread won't run immediately when signaled, because it don't has lock.
                    // after other thread release the lock, and current thread acquire the lock, it can continue to run.
                    condition.await();
                }
                a.a = 3;
                System.out.println(currentThread().getName() + " condition met, set a 3, " + (currentTimeMillis()-start));
            } catch (InterruptedException e) {
                // ignore
            } finally {
                lock.unlock();
                System.out.println(currentThread().getName() + " release lock, " + (currentTimeMillis()-start));
            }
        }, "thread0").start();

        Thread.sleep(300);

        new Thread(() -> {
            try {
                System.out.println(currentThread().getName() + " start at, " + (currentTimeMillis()-start));
                lock.lock();
                System.out.println(currentThread().getName() + " get lock, " + (currentTimeMillis()-start));
                a.a = 1;
                condition.signal();
                System.out.println(currentThread().getName() + " set 1, signaled, " + (currentTimeMillis()-start));
            } finally {
                // lazily release the lock
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<200) { }
                lock.unlock();
                System.out.println(currentThread().getName() + " release lock, " + (currentTimeMillis()-start));
            }
        }, "thread1").start();

        Thread.sleep(600);

        new Thread(() ->  {
            try {
                System.out.println(currentThread().getName() + " start at, " + (currentTimeMillis()-start));
                lock.lock();
                System.out.println(currentThread().getName() + " get lock, " + (currentTimeMillis()-start));
                a.a = 2;
                // signal the awaiting thread, however, the awaiting thread won't run until lock released
                condition.signal();
                System.out.println(currentThread().getName() + " set 2, signaled, " + (currentTimeMillis()-start));
            } finally {
                // lazily release the lock
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<200) { }
                lock.unlock();
                System.out.println(currentThread().getName() + " release lock, " + (currentTimeMillis()-start));
            }
        }, "thread2").start();

        Thread.sleep(1600);
        System.out.println("main end, a=" + a.a);
    }

    private static void testReentrantLock() throws Exception {
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss.SSS");
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        long start = System.currentTimeMillis();
        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(currentThread().getName() + " acquire lock, " + (currentTimeMillis()-start));
                long cur = System.currentTimeMillis();
                while (System.currentTimeMillis()-cur<500) { }
            } finally {
                System.out.println(currentThread().getName() + " release lock, " + (currentTimeMillis()-start));
                lock.unlock();
            }
        }, "t1").start();

        Thread.sleep(50);

        Runnable runnable = () -> {
            try {
                if (lock.tryLock(700, TimeUnit.MILLISECONDS)) {
                    System.out.println(currentThread().getName() + " acquire lock, " + (currentTimeMillis()-start));
                    long cur = System.currentTimeMillis();
                    while (System.currentTimeMillis()-cur<300) { }
                    System.out.println(currentThread().getName() + "- lockHold: " + lock.isHeldByCurrentThread());
                    System.out.println(currentThread().getName() + "- holdCnt : " + lock.getHoldCount());
                    System.out.println(currentThread().getName() + "- queueLen: " + lock.getQueueLength());
                    System.out.println(currentThread().getName() + "- hasQueue: " + lock.hasQueuedThreads());
                    condition.await();
                    System.out.println(currentThread().getName() + " awake.");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    System.out.println(currentThread().getName() + " release lock, " + (currentTimeMillis()-start));
                    lock.unlock();
                }
            }
        };
        new Thread(runnable, "t2a").start();
        new Thread(runnable, "t2b").start();

        Thread.sleep(50);
        System.out.println("\ncurk time: " + (currentTimeMillis()-start));
        System.out.println("main- lockHold: " + lock.isHeldByCurrentThread());
        System.out.println("main- holdCnt : " + lock.getHoldCount());
        System.out.println("main- queueLen: " + lock.getQueueLength());
        System.out.println("main- hasQueue: " + lock.hasQueuedThreads());
        System.out.println();

        Thread.sleep(700);

        new Thread(() -> {
            try {
                if (lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                    System.out.println(currentThread().getName() + " acquire lock, " + (currentTimeMillis()-start));
                    long cur = System.currentTimeMillis();
                    while (System.currentTimeMillis()-cur<300) { }
                    System.out.println(currentThread().getName() + "- lockHold: " + lock.isHeldByCurrentThread());
                    System.out.println(currentThread().getName() + "- holdCnt : " + lock.getHoldCount());
                    System.out.println(currentThread().getName() + "- queueLen: " + lock.getQueueLength());
                    System.out.println(currentThread().getName() + "- hasQueue: " + lock.hasQueuedThreads());
                    condition.signal();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                if (lock.isHeldByCurrentThread()) {
                    System.out.println(currentThread().getName() + " release lock, " + (currentTimeMillis()-start));
                    lock.unlock();
                }
            }
        }, "t3").start();

        Thread.sleep(200);
        System.out.println("\ncurk time: " + (currentTimeMillis()-start));
        System.out.println("main- lockHold: " + lock.isHeldByCurrentThread());
        System.out.println("main- holdCnt : " + lock.getHoldCount());
        System.out.println("main- queueLen: " + lock.getQueueLength());
        System.out.println("main- hasQueue: " + lock.hasQueuedThreads());
        System.out.println();

    }
}
