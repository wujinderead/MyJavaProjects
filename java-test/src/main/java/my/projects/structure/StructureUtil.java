package my.projects.structure;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class StructureUtil {
    public static List<String> getRandomKeys(int size, int maxlen, int minlen) {
        if (size < 1) {
            return new ArrayList<>();
        }
        List<String> list = new ArrayList<>(size);
        Random random = new Random(System.currentTimeMillis());
        for (int i = 0; i < size; i++) {
            int len = random.nextInt(maxlen - minlen) + minlen;
            char[] chars = new char[len];
            for (int j = 0; j < len; j++) {
                chars[j] = (char) ('a' + random.nextInt(26));
            }
            list.add(new String(chars));
        }
        return list;
    }
}
