package my.projects.structure;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class MapTest {
    public static void main(String[] args) {
        String[] strs = new String[]{"6", "rfghjh", "8", "a", "4", "2", "ddasd", "1", "7"};
        // normal hash set: hash table order
        {
            Set<String> set = new HashSet<>();
            for (String str: strs) {
                set.add(str);
            }
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // linked hash set: insertion order
        {
            Set<String> set = new LinkedHashSet<>();
            for (String str: strs) {
                set.add(str);
            }
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // tree set: value order
        {
            Set<String> set = new TreeSet<>();
            for (String str: strs) {
                set.add(str);
            }
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // concurrent skip list set: value order
        {
            Set<String> set = new ConcurrentSkipListSet<>();
            for (String str: strs) {
                set.add(str);
            }
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // priority queue: is a heap underline, each poll() return the least value
        {
            PriorityQueue<String> queue = new PriorityQueue<>();
            for (String str: strs) {
                queue.add(str);
            }
            for (int i=0; i<strs.length; i++) {
                System.out.print(queue.poll() + " ");
            }
            System.out.println();
        }
    }
}
