package my.projects.reflect;

import java.util.Random;

public class RandomTest {
    public static void main(String[] args) {
        Random random = new Random(System.currentTimeMillis());
        for (int i=0; i<40; i++) {
            System.out.print(random.nextInt(1));
        }
    }
}
