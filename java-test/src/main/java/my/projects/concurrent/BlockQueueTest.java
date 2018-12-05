package my.projects.concurrent;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class BlockQueueTest {
    public static void main(String[] args) {
        BlockingQueue<String> queue = new ArrayBlockingQueue<>(3);
        for (int i=0; i<5; i++) {
            Thread producer = new Thread(new Producer(queue, i, 3));
            producer.setName("Producer-"+i);
            producer.start();
        }
        for (int i=0; i<4; i++) {
            Thread consumer = new Thread(new Consumer(queue, i, 10));
            consumer.setName("Consumer-"+i);
            consumer.start();
        }
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            System.out.println("main is interrupted.");
        }
        System.out.println("queue remained: " + queue.size());
    }

    static class Producer implements Runnable {
        String name;
        BlockingQueue<String> queue;
        int number;

        public Producer(BlockingQueue<String> queue, int index, int number) {
            this.name = "Producer-" + index;
            this.queue = queue;
            this.number = number;
        }
        @Override
        public void run() {
            for (int i=0; i<number; i++) {
                String message = name + "-" + i;
                while (true) {
                    boolean flag = queue.offer(message);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(name + " is interrupted.");
                        break;
                    }
                    if (flag) {
                        System.out.println("message '" + message + "' produced at " + System.currentTimeMillis());
                        break;
                    }
                }
            }
        }
    }

    static class Consumer implements Runnable {
        String name;
        BlockingQueue<String> queue;
        int number;

        public Consumer(BlockingQueue<String> queue, int index, int number) {
            this.name = "Consumer-" + index;
            this.queue = queue;
            this.number = number;
        }
        @Override
        public void run() {
            String message;
            for (int i=0; i<number; i++) {
                message = queue.poll();
                if (message != null) {
                    System.out.println("message '" + message + "' consumed at " + System.currentTimeMillis() + " by " + name);
                    continue;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println(name + " is interrupted.");
                    break;
                }
            }
        }
    }
}