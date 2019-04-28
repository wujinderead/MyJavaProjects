package my.projects.reflect;

import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class ReferenceTest {
    static class Tester {
        int a = 1;

        @Override
        public String toString() {
            return "a="+a;
        }
        @Override
        public void finalize() {
            System.out.println("recycled");
        }
    }
    public static void main(String[] args) throws Exception {
        //testReference();
        testReferenceQueue();
    }

    private static void testReference() throws Exception {
        WeakReference<Tester> weak = new WeakReference<>(new Tester());
        SoftReference<Tester> soft = new SoftReference<>(new Tester());
        Tester strong1  = new Tester();
        Tester strong2  = new Tester();
        System.out.println("weak="+weak.get());
        System.out.println("soft="+soft.get());
        System.out.println("strong1="+strong1);
        System.out.println("strong2="+strong2);
        strong2 = null;
        System.gc();
        Thread.sleep(1000);
        System.out.println("weak="+weak.get());  // weak reference got GCed when gc is performing
        System.out.println("soft="+soft.get());  // weak reference got GCed only when memory is not plenty
        System.out.println("strong1="+strong1);
        System.out.println("strong2="+strong2);  // null trigger gc
    }

    private static void testReferenceQueue() throws Exception {
        ReferenceQueue<Tester> queue = new ReferenceQueue<>();
        WeakReference<Tester> weak1 = new WeakReference<>(new Tester(), queue);
        WeakReference<Tester> weak2 = new WeakReference<>(new Tester(), queue);
        WeakReference<Tester> weak3 = new WeakReference<>(new Tester(), queue);
        System.out.println("weak="+ weak1.get().hashCode());
        System.out.println("weak="+ weak2.get().hashCode());
        System.out.println("weak="+ weak3.get().hashCode());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.out.println(queue.poll());
        System.gc();
        Thread.sleep(1000);
        System.out.println("weak="+weak1.get());  // weak got GCed
        System.out.println("weak="+weak2.get());
        System.out.println("weak="+weak3.get());
        System.out.println(queue.poll());         // when object get GCed, add the reference to the queue
        System.out.println(queue.poll());
        System.out.println(queue.poll());
    }
}
