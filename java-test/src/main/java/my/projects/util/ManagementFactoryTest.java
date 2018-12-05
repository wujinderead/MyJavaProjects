package my.projects.util;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.List;

public class ManagementFactoryTest {
    public static void main(String[] args) {
        testClassLoading();
        testGc();
    }

    private static void testClassLoading() {
        ClassLoadingMXBean bean = ManagementFactory.getClassLoadingMXBean();
        System.out.println("loaded: " + bean.getLoadedClassCount());
        System.out.println("total: " + bean.getTotalLoadedClassCount());
        System.out.println("unloaded: " + bean.getUnloadedClassCount());
        System.out.println("verbose: " + bean.isVerbose());
        System.out.println("objname: " + bean.getObjectName().getCanonicalName());
    }

    private static void testGc() {
        List<GarbageCollectorMXBean> gcs = ManagementFactory.getGarbageCollectorMXBeans();
        for (GarbageCollectorMXBean bean: gcs) {
            System.out.println("name: " + bean.getName());
            System.out.println("gc count: " + bean.getCollectionCount());
            System.out.println("gc time: " + bean.getCollectionTime());
            System.out.println("mem pools: " + Arrays.asList(bean.getMemoryPoolNames()));
            System.out.println("objname: " + bean.getObjectName().getCanonicalName());
        }
    }
}
