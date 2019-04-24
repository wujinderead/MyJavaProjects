package my.projects.concurrent;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.lang.System.currentTimeMillis;
import static java.lang.System.out;
import static java.lang.Thread.currentThread;

public class SpinLockTest {
    // spin lock, unfair
    public static class SpinLock {
        private AtomicReference<Thread> owner = new AtomicReference<>();

        public void lock() {
            Thread currentThread = currentThread();
            // If the lock is not occupied, then set the current thread for the lock owner
            while (!owner.compareAndSet(null, currentThread)) {
            }
        }

        public void unlock() {
            Thread currentThread = currentThread();
            // Only the lock owner can release the lock
            owner.compareAndSet(currentThread, null);
        }
    }

    // fair lock, data race is fierce
    public static class TicketLock {
        private AtomicInteger serviceNum = new AtomicInteger(); // Service number
        private AtomicInteger ticketNum = new AtomicInteger();  // Queue number

        public int lock() {
            // The first atomic access to a queue number
            int myTicketNum = ticketNum.getAndIncrement();

            // As long as the current service number is not his, polling
            while (serviceNum.get() != myTicketNum) {
            }
            return myTicketNum;
        }

        public void unlock(int myTicket) {
            // Only the current thread owner to release the lock
            int next = myTicket + 1;
            serviceNum.compareAndSet(myTicket, next);
        }
    }

    // the clh lock is exclusive and not reentrantable
    public static class ClhLock {
        class Node {
            // the pred pointer is actually contained in tail
            // no need to specify a 'Node pred' field
            volatile boolean locked = true;
        }
        Node tail;

        public Node lock() {
            Node cur = new Node();    // can use ThreadLocal instead, which do not need to return the node
            Node pred = (Node) U.getAndSetObject(this, offset, cur);
            if (pred != null) {
                cur.locked = true;
                while (pred.locked) ;
            }
            return cur;
        }
        public void unlock(Node cur) {
            // if tail==cur, cur is the last in queue, we can set tail null, and GC cur
            if (!U.compareAndSwapObject(this, offset, cur, null)) {
                cur.locked = false;
            }
        }
        private static final sun.misc.Unsafe U;
        private static final long offset;
        static {
            try {
                Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                U = (sun.misc.Unsafe) field.get(null);
                offset = U.objectFieldOffset(ClhLock.class.getDeclaredField("tail"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public static class McsLock {
        class Node {
            // no need to specify a 'Node pred' field
            volatile boolean locked = true;
            volatile Node next = null;
        }
        Node tail;

        public Node lock() {
            Node cur = new Node();
            Node pred = (Node) U.getAndSetObject(this, offset, cur);
            if (pred != null) {
                cur.locked = true;
                pred.next = cur;
                while (cur.locked);
            }
            return cur;
        }
        public void unlock(Node cur) {
            // if tail==cur, cur is the last in queue, we can set tail null, and GC cur
            if (!U.compareAndSwapObject(this, offset, cur, null)) {
                // if CAS failed, either cur is not last (cur.next != null),
                // or cur is last but another thread is entering the queue (cur.next will soon be non-null)
                // so spin here to wait next non-null.
                while (cur.next == null);
                cur.next.locked = false;
            }
        }
        private static final sun.misc.Unsafe U;
        private static final long offset;
        static {
            try {
                Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
                field.setAccessible(true);
                U = (sun.misc.Unsafe) field.get(null);
                offset = U.objectFieldOffset(McsLock.class.getDeclaredField("tail"));
            } catch (Exception e) {
                throw new Error(e);
            }
        }
    }

    public static void main(String[] args) {
        //testSpinLock();
        //testTicketLock();
        testClhLock();
        testMcsLock();
    }

    private static void testSpinLock() {
        SpinLock lock = new SpinLock();
        long start = currentTimeMillis();
        Runnable runnable = () -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            lock.lock();
            out.println(currentThread().getName() + " acquire lock at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
            } finally {
                out.println(currentThread().getName() + " release lock at " + (currentTimeMillis()-start));
                lock.unlock();
            }
        };
        for (int i=0; i<10; i++) {
            new Thread(runnable, "t"+i).start();
        }
    }

    private static void testTicketLock() {
        TicketLock lock = new TicketLock();
        long start = currentTimeMillis();
        Runnable runnable = () -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            int ticket = lock.lock();
            out.println(currentThread().getName() + " acquire lock at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
            } finally {
                out.println(currentThread().getName() + " release lock at " + (currentTimeMillis()-start));
                lock.unlock(ticket);
            }
        };
        for (int i=0; i<10; i++) {
            new Thread(runnable, "t"+i).start();
        }
    }

    private static void testClhLock() {
        ClhLock lock = new ClhLock();
        long start = currentTimeMillis();
        Runnable doubleLock = () -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            ClhLock.Node node = lock.lock();
            out.println(currentThread().getName() + " acquire lock at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
            } finally {
                out.println(currentThread().getName() + " release lock at " + (currentTimeMillis()-start));
                lock.unlock(node);
            }
            node = lock.lock();
            out.println(currentThread().getName() + " acquire lock at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
            } finally {
                out.println(currentThread().getName() + " release lock at " + (currentTimeMillis()-start));
                lock.unlock(node);
            }
        };
        // clh lock is not reentrant, this would cause spin forever
        Runnable reentrantLock = () -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            ClhLock.Node outer = lock.lock();
            out.println(currentThread().getName() + " acquire outer at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
                ClhLock.Node inner = lock.lock();
                out.println(currentThread().getName() + " acquire inner at " + (currentTimeMillis()-start));
                try {
                    cur = currentTimeMillis();
                    while (currentTimeMillis()-cur<500);
                } finally {
                    out.println(currentThread().getName() + " release inner at " + (currentTimeMillis()-start));
                    lock.unlock(inner);
                }
            } finally {
                out.println(currentThread().getName() + " release outer at " + (currentTimeMillis()-start));
                lock.unlock(outer);
            }
        };
        for (int i=0; i<5; i++) {
            new Thread(doubleLock, "t"+i).start();
        }
        try{Thread.sleep(5000);}catch (InterruptedException e){}
        new Thread(doubleLock, "t6").start();
        try{Thread.sleep(1200);}catch (InterruptedException e){}
        new Thread(doubleLock, "t7").start();
    }

    private static void testMcsLock() {
        McsLock lock = new McsLock();
        long start = currentTimeMillis();
        Runnable doubleLock = () -> {
            out.println(currentThread().getName() + " start at " + (currentTimeMillis()-start));
            McsLock.Node node = lock.lock();
            out.println(currentThread().getName() + " acquire lock at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
            } finally {
                out.println(currentThread().getName() + " release lock at " + (currentTimeMillis()-start));
                lock.unlock(node);
            }
            node = lock.lock();
            out.println(currentThread().getName() + " acquire lock at " + (currentTimeMillis()-start));
            try {
                long cur = currentTimeMillis();
                while (currentTimeMillis()-cur<500);
            } finally {
                out.println(currentThread().getName() + " release lock at " + (currentTimeMillis()-start));
                lock.unlock(node);
            }
        };
        for (int i=0; i<5; i++) {
            new Thread(doubleLock, "t"+i).start();
        }
        try{Thread.sleep(5100);}catch (InterruptedException e){}
        new Thread(doubleLock, "t6").start();
        try{Thread.sleep(1100);}catch (InterruptedException e){}
        new Thread(doubleLock, "t7").start();
    }
}
