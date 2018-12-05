package my.projects.structure;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentSkipListSet;

public class MapTest {
    public static void main(String[] args) {
        // normal hash set: hash table order
        {
            Set<String> set = new HashSet<>();
            set.add("6");
            set.add("rfghjh");
            set.add("8");
            set.add("a");
            set.add("4");
            set.add("2");
            set.add("ddasd");
            set.add("1");
            set.add("7");
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // linked hash set: insertion order
        {
            Set<String> set = new TreeSet<>();
            set.add("6");
            set.add("rfghjh");
            set.add("8");
            set.add("a");
            set.add("4");
            set.add("2");
            set.add("ddasd");
            set.add("1");
            set.add("7");
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // tree set: value order
        {
            Set<String> set = new LinkedHashSet<>();
            set.add("6");
            set.add("rfghjh");
            set.add("8");
            set.add("a");
            set.add("4");
            set.add("2");
            set.add("ddasd");
            set.add("1");
            set.add("7");
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // concurrent skip list set: value order
        {
            Set<String> set = new ConcurrentSkipListSet<>();
            set.add("6");
            set.add("rfghjh");
            set.add("8");
            set.add("a");
            set.add("4");
            set.add("2");
            set.add("ddasd");
            set.add("1");
            set.add("7");
            for (String i : set) {
                System.out.print(i + " ");
            }
            System.out.println();
        }
        // priority queue: is a heap underline, each poll() return the least value
        {
            PriorityQueue<Integer> queue = new PriorityQueue<>();
            queue.add(5);
            queue.add(3);
            queue.add(6);
            queue.add(1);
            queue.add(2);
            queue.add(7);
            queue.add(4);
            for (int i=0; i<7; i++) {
                System.out.println(queue.poll());
            }
        }
    }
}
