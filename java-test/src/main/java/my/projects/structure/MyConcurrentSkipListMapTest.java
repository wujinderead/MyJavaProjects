package my.projects.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import static my.projects.structure.StructureUtil.getRandomKeys;

public class MyConcurrentSkipListMapTest {
    public static void main(String[] args) {
        testSkipListMap();
    }

    private static void testSkipListMap() {
        MyConcurrentSkipListMap<String, String> map = new MyConcurrentSkipListMap<>();
        List<String> added = new LinkedList<>();
        List<String> list = getRandomKeys(30, 8, 3);
        int firstAdded = 10;
        for (int i = 0; i < firstAdded; i++) {
            map.put(list.get(i), "");
            added.add(list.get(i));
            System.out.println(map + "\n");
        }
        System.out.println(map + "\n");
        Random random = new Random(System.currentTimeMillis());
        int removed = 0;
        for (int i = firstAdded; i < list.size(); i++) {
            float rate = random.nextFloat();
            if (rate < 0.2f) {
                int index = random.nextInt(added.size());
                String str = added.remove(index);
                System.out.println("remove '" + str + "'");
                map.remove(str);
                removed++;
                System.out.println(map + "\n");
            }
            added.add(list.get(i));
            map.put(list.get(i), "");
            System.out.println(map + "\n");
        }
        System.out.printf("all %d, removed %d, size %d\n", list.size(), removed, map.size());

        // key is sorted in natural order in map
        List<String> keys = new ArrayList<>(map.keySet());
        System.out.println("unsorted keys: " + map.keySet());
        Collections.sort(keys);
        System.out.println("sorted keys: " + keys);
    }
}
