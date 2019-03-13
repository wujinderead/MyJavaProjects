package my.projects.system;

import java.util.Map;
import java.util.Properties;

public class SystemTest {
    public static void main(String[] args) {
        testSystem();
    }

    private static void testSystem() {
        // currentTimeMillis(): time in millis second since utc
        // nanoTime(): JVM's high resolution time measure
        System.out.println("nano   time: " + System.nanoTime());
        System.out.println("millis time: " + System.currentTimeMillis());

        // line separator, in my ubuntu, '\n'
        String separator = System.lineSeparator();
        for (char ch: separator.toCharArray()) {
            System.out.println("separator: " + (int) ch);
        }

        // get system environment variables
        Map<String, String> envs = System.getenv();
        for (String key: envs.keySet()) {
            System.out.printf("key: %s, values: %s\n", key, envs.get(key));
        }
        System.out.println();

        // get system properties
        Properties properties = System.getProperties();
        for (String key: properties.stringPropertyNames()) {
            System.out.printf("key: %s, values: %s\n", key, properties.getProperty(key));
        }
        System.out.println();

        // system native array copy, can apply to any type
        byte[] origin = "abcdefgh".getBytes();
        byte[] dest = new byte[6];
        System.arraycopy(origin, 2, dest, 1, 3);
        System.out.println(new String(dest));  // dest :[\0, c, d, e, \0, \0]'

        // load pre-compiled library, like 'xxx.dll' on windows, or 'libxxx.so.1' on linux.
        // to load 'libxxx' in java.library.path, use System.loadLibrary(libName);
        // to load library file 'libxxx.so', use System.loadLibrary(fileName);
    }
}
