package my.projects.concurrent;

import java.util.Arrays;
import java.util.Date;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class FixedSizeThreadPollTest {
    public static void main(String[] args) {
        testFixedPool();
    }

    private static void testFixedPool() {
        int number = 5;
        ExecutorService service = Executors.newCachedThreadPool();
        Setter[] setters = new Setter[number];
        CountDownLatch latch = new CountDownLatch(number);
        System.out.println("==="+System.currentTimeMillis());
        Random random = new Random(System.currentTimeMillis());
        for (int i=0; i<number; i++) {
            setters[i] = new Setter();
            int index = i;
            service.submit(() -> {
                int rand = random.nextInt(8)+1;
                System.out.printf("setter-%d start at %s, rand: %d\n", index, new Date(), rand);
                try {
                    Thread.sleep(rand*1000L);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                setters[index].setSet(rand);
                System.out.printf("setter-%d end at %s\n", index, new Date());
                latch.countDown();
            });
        }
        try {
            latch.await(5000, TimeUnit.MILLISECONDS);
            service.shutdownNow();
        } catch (InterruptedException e) {
            // should not happen
        }
        System.out.println("==="+System.currentTimeMillis());
        System.out.println(Arrays.toString(setters));

    }

    private static class Setter {
        int set = 0;
        public void setSet(int set) {
            this.set = set;
        }
        public String toString() {
            return "["+set+"]";
        }
    }
}
