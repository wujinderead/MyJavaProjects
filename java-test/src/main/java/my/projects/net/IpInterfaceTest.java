package my.projects.net;

import javax.xml.bind.DatatypeConverter;
import java.io.File;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class IpInterfaceTest {
    public static void main(String[] args) throws Exception {
        //testInetAddress();
        //testInterfaces();
        testUrl();
    }

    // list and display net interfaces
    private static void testInterfaces() throws SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint: Collections.list(nets)) {
            System.out.printf("display name: %s\n", netint.getDisplayName());
            System.out.printf("name: %s\n", netint.getName());
            System.out.printf("index: %s\n", netint.getIndex());
            byte[] haddr;
            System.out.printf("hardware addr: %s\n",
                    (haddr = netint.getHardwareAddress()) == null ? "null" : DatatypeConverter.printHexBinary(haddr));
            System.out.printf("mtu: %s\n", netint.getMTU());
            NetworkInterface parent;
            System.out.printf("parent: %s\n", (parent = netint.getParent()) == null ? "null" : parent.getName());
            List<InterfaceAddress> intAddrs = netint.getInterfaceAddresses();
            for (InterfaceAddress intAddr: intAddrs) {
                System.out.println("interface addr: " + intAddr.getAddress());
                System.out.println("interface broadcast: " + intAddr.getBroadcast());
                System.out.println("interface prefix: " + intAddr.getNetworkPrefixLength());
            }
            Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
            for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                System.out.printf("InetAddress: %s, class: %s\n", inetAddress, inetAddress.getClass());
                System.out.printf("inet canonical host: %s\n", inetAddress.getCanonicalHostName());
                System.out.printf("inet hostname: %s\n", inetAddress.getHostName());
                System.out.printf("inet host addr: %s\n", inetAddress.getHostAddress());
            }
            System.out.println("\n");
        }
    }

    // list and display net addresses
    private static void testInetAddress() throws Exception {
        InetAddress local = InetAddress.getLocalHost();
        System.out.printf("InetAddress: %s\n", local);
        System.out.printf("inet canonical host: %s\n", local.getCanonicalHostName());
        System.out.printf("inet hostname: %s\n", local.getHostName());
        System.out.printf("inet host addr: %s\n", local.getHostAddress());
        System.out.println();

        InetAddress loopback = InetAddress.getLoopbackAddress();
        System.out.printf("InetAddress: %s\n", loopback);
        System.out.printf("inet canonical host: %s\n", loopback.getCanonicalHostName());
        System.out.printf("inet hostname: %s\n", loopback.getHostName());
        System.out.printf("inet host addr: %s\n", loopback.getHostAddress());
        System.out.println();

        InetAddress[] baidus = InetAddress.getAllByName("www.baidu.com");
        for (InetAddress inetAddress: baidus) {
            System.out.printf("InetAddress: %s\n", inetAddress);
            System.out.printf("inet canonical host: %s\n", inetAddress.getCanonicalHostName());
            System.out.printf("inet hostname: %s\n", inetAddress.getHostName());
            System.out.printf("inet host addr: %s\n", inetAddress.getHostAddress());
            System.out.println();
        }

        InetAddress[] tubes = InetAddress.getAllByName("www.youtube.com");
        for (InetAddress inetAddress: tubes) {
            System.out.printf("InetAddress: %s\n", inetAddress);
            System.out.printf("inet canonical host: %s\n", inetAddress.getCanonicalHostName());
            System.out.printf("inet hostname: %s\n", inetAddress.getHostName());
            System.out.printf("inet host addr: %s\n", inetAddress.getHostAddress());
            System.out.println();
        }
    }

    private static void testUrl() throws Exception {
        File file = new File("/home/xzy/.m2/repository/com/google/guava/guava/27.0-jre/guava-27.0-jre.jar");
        JarFile jarFile = new JarFile(file);
        Enumeration<JarEntry> entries = jarFile.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            System.out.println(entry.getName());
        }
        System.out.println();

        // default classloader to get loaded resource in jar as stream
        InputStream stream = int.class.getResourceAsStream("/META-INF/maven/com.google.guava/guava/pom.xml");
        if (stream != null) {
            byte[] buf = new byte[300];
            while (stream.read(buf) != -1) {
                System.out.println("== " + new String(buf));
            }
            stream.close();
        }
        URL resourceUrl = int.class.getResource("/META-INF/maven/com.google.guava/guava/pom.xml");
        System.out.println("resource: " + resourceUrl);
        System.out.println("package: " + com.google.common.base.Charsets.class.getPackage());

        // get resource from unloaded jar, it does not work, because the jar is not in classpath
        file = new File("/home/xzy/.m2/repository/org/apache/maven/maven-core/3.1.1/maven-core-3.1.1.jar");
        URI uri = file.toURI();
        URL url = uri.toURL();
        System.out.println("uri: " + uri);
        System.out.println("url: " + url);
        URL resource = int.class.getResource("META-INF/maven/org.apache.maven/maven-core/pom.xml");
        System.out.println("resource: " + resource);
        System.out.println();

        // load class using URLClassLoader
        URLClassLoader loader = new URLClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader());
        URL[] urls = loader.getURLs();  // get the urls when construct it
        for (URL urll: urls) {
            System.out.println("urls: " + urll);
        }
        System.out.println("get resource: " + loader.getResource("META-INF/maven/org.apache.maven/maven-core/pom.xml"));
        System.out.println("find resource: " + loader.findResource("META-INF/maven/org.apache.maven/maven-core/pom.xml"));
        Class<?> clazz = loader.loadClass("org.apache.maven.plugin.PluginRealmCache$CacheRecord");
        System.out.println("loaded class: " + clazz);
        System.out.println("package: " + clazz.getPackage());
        System.out.println();
    }
}
