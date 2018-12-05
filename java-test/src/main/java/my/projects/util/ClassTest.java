package my.projects.util;

import java.util.ArrayList;
import java.util.List;

public class ClassTest {
    public static void main(String[] args) {
        classTest();
        String database = ".test...test..aa.g".replaceAll("[\\s.]", "_");
        System.out.println(database);
    }

    public static void classTest() {
        List<String> list = new ArrayList<>();
        Class<?> cls = list.getClass();
        System.out.println("cls.getName() = " + cls.getName());
        System.out.println("cls.getName() = " + cls.getCanonicalName());
        System.out.println("cls.getName() = " + cls.getSimpleName());
        System.out.println("cls.getName() = " + cls.getTypeName());
    }
}
