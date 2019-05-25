package my.projects.concurrent;

import static java.lang.System.out;
import static java.lang.Thread.currentThread;

public class SingletonTest {
    public static void main(String[] args) {
        //test0();
        //test1();
        //test2();
        //test33();
        //test4();
        //test5();
        test6();
    }

    // actually test, it's lazily loaded? compiler optimization?
    private static void test0() {
        new Thread(()->out.println(Singleton0.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton0.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton0.getInstance().hashCode()), "t3").start();
    }

    // duplicated initialization
    private static void test1() {
        new Thread(()->out.println(Singleton1.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton1.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton1.getInstance().hashCode()), "t3").start();
    }

    private static void test2() {
        new Thread(()->out.println(Singleton2.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton2.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton2.getInstance().hashCode()), "t3").start();
    }

    private static void test3() {
        new Thread(()->out.println(Singleton2.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton2.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton2.getInstance().hashCode()), "t3").start();
    }

    private static void test33() {
        new Thread(()->out.println(Singleton33.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton33.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton33.getInstance().hashCode()), "t3").start();
    }

    private static void test4() {
        new Thread(()->out.println(Singleton4.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton4.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton4.getInstance().hashCode()), "t3").start();
    }

    private static void test5() {
        new Thread(()->out.println(Singleton5Enum.UNIQUE.getInstance().hashCode()), "t1").start();
        new Thread(()->out.println(Singleton5Enum.UNIQUE.getInstance().hashCode()), "t2").start();
        new Thread(()->out.println(Singleton5Enum.UNIQUE.getInstance().hashCode()), "t3").start();
    }

    private static void test6() {
        new Thread(()->out.println(Singleton6.INSTANCE.hashCode()), "t1").start();
        new Thread(()->out.println(Singleton6.INSTANCE.hashCode()), "t2").start();
        new Thread(()->out.println(Singleton6.INSTANCE.hashCode()), "t3").start();
    }

    // initialized during class-loading, increase loading time
    // actually test, it's lazily loaded? compiler optimization?
    static class Singleton0 {
        private static Singleton0 instance = new Singleton0();
        private Singleton0(){
            out.println("Singleton0 initialized in "+currentThread().getName());
        }
        public static Singleton0 getInstance() {
            return instance;
        }
    }

    // multi thread problem
    static class Singleton1 {
        private static Singleton1 instance;
        private Singleton1(){out.println("Singleton1 initialized in "+currentThread().getName());}
        public static Singleton1 getInstance() {
            if (instance == null) {
                instance =  new Singleton1();
            }
            return instance;
        }
    }

    // every get after creation are synced, which is unnecessary
    static class Singleton2 {
        private static Singleton2 instance;
        private Singleton2(){out.println("Singleton2 initialized in "+currentThread().getName());}
        public static synchronized Singleton2 getInstance() {
            if (instance == null) {
                instance =  new Singleton2();
            }
            return instance;
        }
    }

    // double null check, instance need to be volatile, otherwise, for example:
    // thread1 enters sync block and creates instance, then thread1 exits sync block,
    // thread2 also enters sync block, and thread2 may see instance still null.
    // if instance is volatile, thread1's modification to instance will be visible by thread2.
    static class Singleton3 {
        private static volatile Singleton3 instance;
        private Singleton3(){out.println("Singleton3 initialized in "+currentThread().getName());}
        public static Singleton3 getInstance() {
            if (instance == null) {
                synchronized (Singleton3.class) {     // A
                    if (instance == null) {           // B
                        instance = new Singleton3();
                    }
                }
            }
            return instance;
        }
    }

    // double check non volatile version
    static class Singleton33 {
        private static Singleton33 instance;
        private Singleton33(){out.println("Singleton33 initialized in "+currentThread().getName());}
        public static Singleton33 getInstance() {
            if (instance == null) {
                synchronized (Singleton33.class) {    // A
                    if (instance == null) {           // B
                        instance = new Singleton33();
                    }
                }
            }
            return instance;
        }
    }

    // java's class loader will guarantee instance initialized only once
    static class Singleton4 {
        private Singleton4(){out.println("Singleton4 initialized in "+currentThread().getName());}
        public static Singleton4 getInstance() {
            return Singleton4Container.instance;
        }
        private static class Singleton4Container {
            public static Singleton4 instance = new Singleton4();
        }
    }

    static class Singleton5 {
        private Singleton5(){out.println("Singleton5 initialized in "+currentThread().getName());}
    }

    // use enum's only enumeration to guarantee uniqueness
    enum Singleton5Enum {
        UNIQUE;               // enum's unique enumeration
        Singleton5 instance;  // unique enumeration's field
        public Singleton5 getInstance() {
            return UNIQUE.instance;
        }
        Singleton5Enum() {
            instance = new Singleton5();
        }
    }

    // do not wrap, use enum as class directly
    enum Singleton6 {
        INSTANCE(1, "aaa");
        int a;
        String b;
        Singleton6(int a, String b) {
            out.println("Singleton6 initialized in "+currentThread().getName());
            this.a = a;
            this.b = b;
        }
    }
}