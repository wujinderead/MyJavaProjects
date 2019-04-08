package my.projects.system;

import java.lang.management.ClassLoadingMXBean;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Arrays;
import java.util.List;

public class ManagementFactoryTest {
    public static void main(String[] args) {
        testClassLoading();
        testGc();
        testOs();
        testRt();
        testThread();
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

    private static void testOs() {
        OperatingSystemMXBean os = ManagementFactory.getOperatingSystemMXBean();
        System.out.println("arch: " + os.getArch());
        System.out.println("name: " + os.getName());
        System.out.println("version: " + os.getVersion());
        System.out.println("processor: " + os.getAvailableProcessors());
        System.out.println("load: " + os.getSystemLoadAverage());
        System.out.println("objname: " + os.getObjectName().getCanonicalName());
    }

    private static void testRt() {
        RuntimeMXBean rt = ManagementFactory.getRuntimeMXBean();
        System.out.println("boot cp: " + rt.getBootClassPath());
        System.out.println("cp: " + rt.getClassPath());
        System.out.println("lib cp: " + rt.getLibraryPath());
        System.out.println("version: " + rt.getManagementSpecVersion());
        System.out.println("name: " + rt.getName());
        System.out.println("spec: " + rt.getSpecName());
        System.out.println("spec vendor: " + rt.getSpecVendor());
        System.out.println("spec version: " + rt.getSpecVersion());
        System.out.println("vm: " + rt.getVmName());
        System.out.println("vm vendor: " + rt.getVmVendor());
        System.out.println("vm version: " + rt.getVmVersion());
        System.out.println("start: " + rt.getStartTime());
        System.out.println("up: " + rt.getUptime());
        System.out.println("arguments: " + rt.getInputArguments());
    }

    private static void testThread() {
        ThreadMXBean td = ManagementFactory.getThreadMXBean();
        System.out.println("count: " + td.getThreadCount());
        System.out.println("ids: " + Arrays.toString(td.getAllThreadIds()));
        ThreadInfo[] infos = td.dumpAllThreads(true, true);
        for (ThreadInfo info: infos) {
            System.out.println(info.toString());
        }
    }
}
