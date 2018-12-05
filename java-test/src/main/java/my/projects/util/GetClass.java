package my.projects.util;

import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class GetClass {
    public static void main(String[] args) {
        JarFile jfile;
        try {
            jfile = new JarFile("/home/xzy/stream/KafkaConnector/target/super-connector.jar");
            Enumeration<JarEntry> files = jfile.entries();
            while (files.hasMoreElements()) {
                JarEntry entry = files.nextElement();
                if (entry.getName().endsWith(".class")) {
                    System.out.println(entry.getName());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
