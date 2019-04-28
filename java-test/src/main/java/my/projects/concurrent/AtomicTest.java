package my.projects.concurrent;

import java.lang.reflect.Field;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import static java.lang.System.out;

public class AtomicTest {
    public static void main(String[] args) {
        //testUpdateArray();
        testLongAdder();
    }

    private static void testUpdateArray() {
        class Tester {
            int a = 1;
            String b = "aaa";
        }
        Tester[] testers = {new Tester(), new Tester(), new Tester()};
        int scale = U.arrayIndexScale(Tester[].class);
        int baseOffset = U.arrayBaseOffset(Tester[].class);
        try {
            long aOff = U.objectFieldOffset(Tester.class.getDeclaredField("a"));
            long bOff = U.objectFieldOffset(Tester.class.getDeclaredField("b"));
            U.putInt(testers[0], aOff, 123);
            U.putInt(testers[1], aOff, 456);
            U.putInt(testers[2], aOff, 789);
            U.putObject(testers[0], bOff, "bbb");
            U.putObject(testers[1], bOff, "ccc");
            U.putObject(testers[2], bOff, "ddd");
            for (int i=0; i<testers.length; i++) {
                out.println(testers[i].a);
                out.println(testers[i].b);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testLongAdder() {
        LongAdder adder = new LongAdder();
        AtomicLong atomic = new AtomicLong();
        Runnable adderAdd = () -> {
            for (int i=0;i<100000; i++) {
                adder.increment();
            }
        };
        Runnable atomicAdd = () -> {
            for (int i=0;i<100000; i++) {
                atomic.incrementAndGet();
            }
        };
        for (int i=0; i<10; i++) {
            new Thread(adderAdd).start();
        }
        for (int i=0; i<10; i++) {
            new Thread(atomicAdd).start();
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // ignore
        }
        out.println(adder.longValue());
        out.println(atomic.longValue());
    }

    private static final sun.misc.Unsafe U;
    static {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            U = (sun.misc.Unsafe) field.get(null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }
}
