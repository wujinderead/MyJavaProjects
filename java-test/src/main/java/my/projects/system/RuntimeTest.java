package my.projects.system;

import java.util.concurrent.ForkJoinPool;

import static java.lang.System.out;

public class RuntimeTest {
    public static void main(String[] args) throws Exception {
        testRuntime();
        testConcurrent();
    }

    private static void testRuntime() {
        out.println("processor: " + Runtime.getRuntime().availableProcessors());
        out.println("free: " + Runtime.getRuntime().freeMemory()*1.0/1024/1024/1024);
        out.println("max: " + Runtime.getRuntime().maxMemory()*1.0/1024/1024/1024);
        out.println("total: " + Runtime.getRuntime().totalMemory()*1.0/1024/1024/1024);
    }

    private static void testConcurrent() {
        out.println("common pool parallel: " + ForkJoinPool.getCommonPoolParallelism());
    }
}
