package my.projects.concurrent;

import java.util.concurrent.atomic.AtomicLong;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;

public class ThreadLocalTest {
    // every thread has a field: ThreadLocalMap threadLocals
    // ThreadLocalMap is a hash map, the key is the ThreadLocal object
    private AtomicLong longer = new AtomicLong();
    private ThreadLocal<Object> inter = ThreadLocal.withInitial(() -> longer.getAndAdd(0x1a2b3c4d));
    int a = 99;
    public static void main(String[] args) {
        testThreadLocalGet();
    }

    private static void testThreadLocalGet() {
        ThreadLocalTest test = new ThreadLocalTest();
        Runnable runnable = () -> {
            ///out.println(currentThread().getName() + " h: " + test.hashCode());
            //out.println(currentThread().getName() + " a: " + test.a);
            out.println(currentThread().getName() + " l: " + test.inter.get());
        };
        for (int i=0; i<10; i++) {
            new Thread(runnable, "t"+i).start();
        }
    }
}
