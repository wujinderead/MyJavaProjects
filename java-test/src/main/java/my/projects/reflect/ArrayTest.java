package my.projects.reflect;

import java.lang.reflect.Array;

public class ArrayTest {
    public static void main(String[] args) {
        //testArrayType();
        testArray();
    }

    private static void testArrayType() {
        Object strings = new int[3];
        Class stringArrayClass = strings.getClass();
        System.out.println(stringArrayClass + ", " + stringArrayClass.getCanonicalName());
        Class stringArrayComponentType = stringArrayClass.getComponentType();
        System.out.println(stringArrayComponentType);
    }

    private static void testArray() {
        int[] intArray = (int[]) Array.newInstance(int.class, 3);

        Array.set(intArray, 0, 123);
        Array.set(intArray, 1, 456);
        Array.set(intArray, 2, 789);

        System.out.println("intArray[0] = " + Array.get(intArray, 0));
        System.out.println("intArray[1] = " + Array.get(intArray, 1));
        System.out.println("intArray[2] = " + Array.get(intArray, 2));
    }
}
