package my.projects.concurrent;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;

public class ReentrantReadWriteLockTest {
    public static void main(String[] args) {
        testReadWrite();
    }

    // 'read read' is inclusive, 'read write' and 'write write' are exclusive
    private static void testReadWrite() {
        ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
        ReentrantReadWriteLock.ReadLock read = lock.readLock();
        ReentrantReadWriteLock.WriteLock write = lock.writeLock();
        Condition condition = write.newCondition();
        AtomicInteger a = new AtomicInteger(10);
        long start = currentTimeMillis();
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            read.lock();
            try {
                int tmp = a.get();
                out.println(currentThread().getName() + " get " + tmp + " at " + (currentTimeMillis()-start));
                out.println(currentThread().getName() + " hold by " + lock.getReadHoldCount());
                sleep(1000);
            } finally {
                out.println(currentThread().getName() + " release rlock at " + (currentTimeMillis()-start));
                read.unlock();
            }
        }, "R1").start();
        sleep(50);
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            read.lock();
            try {
                int tmp = a.get();
                out.println(currentThread().getName() + " get " + tmp + " at " + (currentTimeMillis()-start));
                out.println(currentThread().getName() + " hold by " + lock.getReadHoldCount());
                sleep(1000);
            } finally {
                out.println(currentThread().getName() + " release rlock at " + (currentTimeMillis()-start));
                read.unlock();
            }
        }, "R2").start();
        sleep(50);
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            write.lock();
            try {
                int tmp = a.addAndGet(3);
                out.println(currentThread().getName() + " add " + tmp + " at " + (currentTimeMillis()-start));
                sleep(1000);
                wait(condition);
            } finally {
                out.println(currentThread().getName() + " release wlock at " + (currentTimeMillis()-start));
                write.unlock();
            }
        }, "W1").start();
        sleep(50);
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            write.lock();
            try {
                int tmp = a.addAndGet(4);
                out.println(currentThread().getName() + " add " + tmp + " at " + (currentTimeMillis()-start));
                condition.signal();
                sleep(1000);
            } finally {
                out.println(currentThread().getName() + " release wlock at " + (currentTimeMillis()-start));
                write.unlock();
            }
        }, "W2").start();
        sleep(50);
        new Thread(() -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            read.lock();
            try {
                int tmp = a.get();
                out.println(currentThread().getName() + " get " + tmp + " at " + (currentTimeMillis()-start));
                out.println(currentThread().getName() + " hold by " + lock.getReadHoldCount());
                sleep(1000);
            } finally {
                out.println(currentThread().getName() + " release rlock at " + (currentTimeMillis()-start));
                read.unlock();
            }
        }, "R3").start();
        sleep(6000);
    }

    private static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // ignore
        }
    }

    private static void wait(Condition condition) {
        try {
            condition.await();
        } catch (InterruptedException e) {
            // ignore
        }
    }
}
