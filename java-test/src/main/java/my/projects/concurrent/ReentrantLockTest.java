package my.projects.concurrent;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ReentrantLockTest {
    public static void main(String[] args) throws InterruptedException {
        testCondition();
    }

    private static void testCondition() throws InterruptedException {
        // use a class to contain value
        class A {
            volatile int a;
        }
        A a= new A();
        a.a = 0;
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss.SSS");

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " get lock, " + format.format(new Date()));
                while (a.a!=2) {
                    System.out.println(Thread.currentThread().getName() + " condition unmet, continue wait, " + format.format(new Date()));
                    // await for condition, give up the lock, so other thread can acquire the lock
                    // when other thread invoke 'condition.signal()', current thread can continue to run
                    // however, current thread won't run immediately when signaled, because it don't has lock
                    // after other thread release the lock, and current thread acquire the lock, it can continue to run
                    condition.await();
                }
                a.a = 3;
                System.out.println(Thread.currentThread().getName() + " condition met, set a 3, " + format.format(new Date()));
            } catch (InterruptedException e) {
                // ignore
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " release lock, " + format.format(new Date()));
            }
        }, "thread0").start();

        Thread.sleep(500);

        new Thread(() -> {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " get lock, " + format.format(new Date()));
                a.a = 1;
                condition.signal();
                System.out.println(Thread.currentThread().getName() + " set 1, signaled, " + format.format(new Date()));
            } finally {
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " release lock, " + format.format(new Date()));
            }
        }, "thread1").start();

        Thread.sleep(500);

        new Thread(() ->  {
            try {
                lock.lock();
                System.out.println(Thread.currentThread().getName() + " get lock, " + format.format(new Date()));
                a.a = 2;
                // signal the awaiting thread, however, the awaiting thread won't run until lock released
                condition.signal();
                System.out.println(Thread.currentThread().getName() + " set 2, signaled, " + format.format(new Date()));
            } finally {
                // lazily release the lock
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    // ignore
                }
                lock.unlock();
                System.out.println(Thread.currentThread().getName() + " release lock, " + format.format(new Date()));
            }
        }, "thread2").start();

        Thread.sleep(1600);
        System.out.println("main end, a=" + a.a);
    }
}
