package my.projects.structure;

import java.util.concurrent.atomic.AtomicLong;

public class RandomTester {
    private static final long GAMMA = 0x9e3779b97f4a7c15L;
    private static final long SEEDER_INCREMENT = 0xbb67ae8584caa73bL;

    /*
    ThreadLocalRandom static: seeder = mix64(millis) ^ mix64(nano)
    Thread1 local: localSeed = mix64(seeder); seeder += SEEDER_INCREMENT;
        localseed += GAMMA; nextLong = mix64(localseed)
        localseed += GAMMA; nextLong = mix64(localseed)
        localseed += GAMMA; nextLong = mix64(localseed)
        ......
    Thread2 local: localSeed = mix64(seeder); seeder += SEEDER_INCREMENT;
        localseed += GAMMA; nextLong = mix64(localseed)
        localseed += GAMMA; nextLong = mix64(localseed)
        localseed += GAMMA; nextLong = mix64(localseed)
        ......
    Thread3 local: localSeed = mix64(seeder); seeder += SEEDER_INCREMENT;
        localseed += GAMMA; nextLong = mix64(localseed)
        localseed += GAMMA; nextLong = mix64(localseed)
        localseed += GAMMA; nextLong = mix64(localseed)
        ......
     */
    public static void main(String[] args) throws Exception {
        //testRandom();
        //testRandom1();
        //testMix64();
        //testSecondary();
        //testSecondary1();
        testLevel();
    }

    private static void testRandom() throws Exception {
        Runnable rander = () -> {
            System.out.println(MyThreadLocalRandom.current().nextLong());
            System.out.println(MyThreadLocalRandom.current().nextLong());
            System.out.println(MyThreadLocalRandom.current().nextLong());
            System.out.println(MyThreadLocalRandom.current().nextLong());
            System.out.println(MyThreadLocalRandom.current().nextLong());
        };
        new Thread(rander).start();
        Thread.sleep(100);
        System.out.println();

        new Thread(rander).start();
        Thread.sleep(100);
        System.out.println();

        new Thread(rander).start();
        Thread.sleep(100);
        System.out.println();
    }

    private static void testRandom1() {
        /*
          millis: 1539762482675, nano: 270334977794717
          3512948931558481302
          -6462096448536386497
          2623694875141853854
          3495763835852576298
          4482422509786971639

          8422619027577088422
          6844679510362101049
          -5625067849978307484
          7389746545084448034
          -5772225781846392926

          1556787238853196933
          4401832433247036911
          -576759389693050766
          7905372026659277908
          -6357977962544632882
         */
        long millis = 1539762482675L, nano = 270334977794717L;
        final AtomicLong seeder = new AtomicLong(mix64(millis) ^ mix64(nano));
        System.out.println(seeder);
        long localSeed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        for (int i=0; i<5; i++) {
            localSeed = localSeed + GAMMA;
            System.out.println(mix64(localSeed));
        }
        System.out.println();

        localSeed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        for (int i=0; i<5; i++) {
            localSeed = localSeed + GAMMA;
            System.out.println(mix64(localSeed));
        }
        System.out.println();

        localSeed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        for (int i=0; i<5; i++) {
            localSeed = localSeed + GAMMA;
            System.out.println(mix64(localSeed));
        }
        System.out.println();
    }

    private static long mix64(long z) {
        z = (z ^ (z >>> 33)) * 0xff51afd7ed558ccdL;
        z = (z ^ (z >>> 33)) * 0xc4ceb9fe1a85ec53L;
        return z ^ (z >>> 33);
    }

    public static void testMix64() {
        long[] longs = {0, Long.MAX_VALUE, Long.MIN_VALUE, -1, 1, 2, 3, Long.MAX_VALUE - Integer.MAX_VALUE, System.currentTimeMillis()};
        for (long along: longs) {
            System.out.println(Long.toHexString(along) + ", " + Long.toHexString(mix64(along)));
        }
        System.out.println(Long.toBinaryString(GAMMA));
        System.out.println(Long.toBinaryString(SEEDER_INCREMENT));
        System.out.println(Long.toBinaryString(0xff51afd7ed558ccdL));
        System.out.println(Long.toBinaryString(0xc4ceb9fe1a85ec53L));
    }

    private static void testSecondary() throws Exception {
        Runnable rander = () -> {
            System.out.println(MyThreadLocalRandom.nextSecondarySeed());
            System.out.println(MyThreadLocalRandom.nextSecondarySeed());
            System.out.println(MyThreadLocalRandom.nextSecondarySeed());
            System.out.println(MyThreadLocalRandom.nextSecondarySeed());
            System.out.println(MyThreadLocalRandom.nextSecondarySeed());
        };
        new Thread(rander).start();
        Thread.sleep(100);
        System.out.println();

        new Thread(rander).start();
        Thread.sleep(100);
        System.out.println();

        new Thread(rander).start();
        Thread.sleep(100);
        System.out.println();
    }

    public static void testSecondary1() {
        /*
          millis: 1539787523075L, nano: 7224228541195L
          -123202685
          -1579560177
          -323509069
          471391427
          -141074589

          -1041934388
          142649858
          -1512277854
          1942852870
          -322206800

          -930704758
          1076634561
          1322802315
          -328541233
          1434413190
         */
        long millis = 1539787523075L, nano = 7224228541195L;
        final AtomicLong seeder = new AtomicLong(mix64(millis) ^ mix64(nano));
        System.out.println("seeder: " + seeder);
        long localSeed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        int secondary = (int) localSeed;
        System.out.println("localseed: " + localSeed + ", secondary: " + secondary);
        for (int i=0; i<4; i++) {
            secondary ^= secondary << 13;   // xorshift
            secondary ^= secondary >>> 17;
            secondary ^= secondary << 5;
            System.out.println(secondary);
        }
        System.out.println();

        localSeed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        secondary = (int) localSeed;
        System.out.println("localseed: " + localSeed + ", secondary: " + secondary);
        for (int i=0; i<4; i++) {
            secondary ^= secondary << 13;   // xorshift
            secondary ^= secondary >>> 17;
            secondary ^= secondary << 5;
            System.out.println(secondary);
        }
        System.out.println();

        localSeed = mix64(seeder.getAndAdd(SEEDER_INCREMENT));
        secondary = (int) localSeed;
        System.out.println("localseed: " + localSeed + ", secondary: " + secondary);
        for (int i=0; i<4; i++) {
            secondary ^= secondary << 13;   // xorshift
            secondary ^= secondary >>> 17;
            secondary ^= secondary << 5;
            System.out.println(secondary);
        }
        System.out.println();
    }

    private static void testLevel() {
        for (int i = 0; i < 20; i++) {
            int rnd = MyThreadLocalRandom.nextSecondarySeed();
            // test highest bit = 0 and lowest bit = 0, which means rate=1/4
            if ((rnd & 0x80000001) == 0) {
                System.out.println(Integer.toBinaryString(rnd));
                System.out.println(rnd);
                int level = 1, max;
                // test how many consecutive 1's right before lowest bits, for example:
                // 10000000101010110 return 2
                // 10000000000011110 return 4
                while (((rnd >>>= 1) & 1) != 0) {
                    System.out.println(Integer.toBinaryString(rnd));
                    ++level;
                }
                System.out.println("level=" + level);
                System.out.println();
            }
        }
    }
}
