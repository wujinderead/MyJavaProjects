package my.projects.concurrent;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;

public class LatchBarrierTest {
    public static void main(String[] args) {
        //testLatch();
        testBarrier();
    }

    private static void testLatch() {
        int number = 5;
        CountDownLatch latch = new CountDownLatch(number);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < number; i++) {
            new Thread(new LatchSleeper("thread"+i, random, latch)).start();
        }
        try {
            latch.await();
        } catch (InterruptedException e) {
            // should not happen
        }
        System.out.printf("main finish wait at %s\n", new Date());
    }

    private static void testBarrier() {
        int number = 5;
        Random random = new Random(System.currentTimeMillis());
        CyclicBarrier barrier = new CyclicBarrier(number, () -> System.out.println("thread group finish"));
        for (int i = 0; i < number; i++) {
            new Thread(new BarrierSleeper("thread"+i, random, barrier)).start();
        }
        try {
            barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("main finish wait at %s\n", new Date());
        barrier.reset();

        for (int i = 0; i < number; i++) {
            new Thread(new BarrierSleeper("thread"+i, random, barrier)).start();
        }
        try {
            barrier.await();
        } catch (BrokenBarrierException | InterruptedException e) {
            e.printStackTrace();
        }
        System.out.printf("main finish wait at %s\n", new Date());

    }

    private static class LatchSleeper implements Runnable {
        Random random;
        String name;
        CountDownLatch latch;

        public LatchSleeper(String name, Random random, CountDownLatch latch) {
            this.random = random;
            this.name = name;
            this.latch = latch;
        }

        @Override
        public void run() {
            int n = random.nextInt(10)+1;
            try {
                System.out.printf("%s start job at %s\n", name, new Date());
                Thread.sleep(n * 1000L);
                System.out.printf("%s finish job at %s\n", name, new Date());
                latch.countDown();
                latch.await();
                System.out.printf("%s finish wait at %s\n", name, new Date());
            } catch (InterruptedException e) {
                // should not happen
            }
        }
    }

    private static class BarrierSleeper implements Runnable {
        Random random;
        String name;
        CyclicBarrier barrier;

        public BarrierSleeper(String name, Random random, CyclicBarrier barrier) {
            this.random = random;
            this.name = name;
            this.barrier = barrier;
        }

        @Override
        public void run() {
            int n = random.nextInt(10)+1;
            try {
                System.out.printf("%s start job at %s\n", name, new Date());
                Thread.sleep(n * 1000L);
                System.out.printf("%s finish job at %s\n", name, new Date());
                barrier.await();
                System.out.printf("%s finish wait at %s\n", name, new Date());
            } catch (InterruptedException | BrokenBarrierException e) {
                // should not happen
            }
        }
    }
}
