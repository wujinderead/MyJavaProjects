package my.projects.structure;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static my.projects.structure.StructureUtil.getRandomKeys;

public class MyHashMapTest {
    public static void main(String[] args) {
        testHashmap();
    }

    private static void testHashmap() {
        MyHashMap<String, String> map = new MyHashMap<>();
        for (String str: getRandomKeys(25, 8, 2)) {
            map.put(str, "");
            System.out.println(map + "\n");
        }

        // key will be iterated as the order in hash table
        for (String key: map.keySet()) {
            int h, g;
            System.out.printf("key=%s, strhash=%d, hashed=%d, pos=%d=%d\n",
                    key, key.hashCode(), (h = MyHashMap.hash(key)), (map.table.length - 1) & h,
                    (g=h%map.table.length) <0 ? g+map.table.length : g);
        }

        List<String> keys = new ArrayList<>();
        keys.addAll(map.keySet());
        System.out.println("unsorted keys: " + map.keySet());
        Collections.sort(keys);
        System.out.println("sorted keys: " + keys);
    }
}
