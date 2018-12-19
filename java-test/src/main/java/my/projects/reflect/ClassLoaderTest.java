package my.projects.reflect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Invokable;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.sql.Driver;
import java.util.ServiceLoader;
import java.util.Set;

public class ClassLoaderTest {
    private static final Logger log = LoggerFactory.getLogger(ClassLoaderTest.class);
    public static void main(String[] args) throws Exception {
        testReflections1(Driver.class,
                "/home/xzy/.m2/repository/com/oracle/ojdbc6/11.2.0.4/ojdbc6-11.2.0.4.jar",
                "/home/xzy/.m2/repository/org/postgresql/postgresql/42.2.2/postgresql-42.2.2.jar",
                "/home/xzy/.m2/repository/com/sap/cloud/db/jdbc/ngdbc/2.2.16/ngdbc-2.2.16.jar");
        testReflections2(Driver.class,
                "/home/xzy/.m2/repository/com/oracle/ojdbc6/11.2.0.4/ojdbc6-11.2.0.4.jar",
                "/home/xzy/.m2/repository/org/postgresql/postgresql/42.2.2/postgresql-42.2.2.jar",
                "/home/xzy/.m2/repository/com/sap/cloud/db/jdbc/ngdbc/2.2.16/ngdbc-2.2.16.jar");
        testServiceLoader(Driver.class,
                "/home/xzy/.m2/repository/com/oracle/ojdbc6/11.2.0.4/ojdbc6-11.2.0.4.jar",
                "/home/xzy/.m2/repository/org/postgresql/postgresql/42.2.2/postgresql-42.2.2.jar",
                "/home/xzy/.m2/repository/com/sap/cloud/db/jdbc/ngdbc/2.2.16/ngdbc-2.2.16.jar");
    }

    private static void testClassLoader() {
        System.out.println(int.class.getClassLoader());
        System.out.println(ObjectMapper.class.getClassLoader());
        System.out.println(ArrayListMultimap.class.getClassLoader());
        System.out.println(ClassLoader.getSystemClassLoader());
    }

    private static void testClassLoaded(ClassLoader loader) throws Exception {
        ClassPath classpath = ClassPath.from(loader);
        ImmutableSet<ClassPath.ClassInfo> infos = classpath.getAllClasses();
        System.out.println(infos.size());
    }

    private static <T> void testReflections1(Class<T> clazz, String... paths) throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        URL[] urls = new URL[paths.length];
        for (int i=0; i< paths.length; i++) {
            URL url = Paths
                    .get(paths[i])
                    .toAbsolutePath()
                    .toUri()
                    .toURL();
            builder.addUrls(url);
            urls[i] = url;
        }
        ClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        System.out.println("loader: " + loader);
        builder.setClassLoaders(new ClassLoader[]{loader});
        Reflections reflections = new Reflections(builder);
        Set<Class<? extends T>> klasses = reflections.getSubTypesOf(clazz);
        for (Class<?> klass: klasses) {
            System.out.println("class: " + klass.getCanonicalName());
            System.out.println("loader: " + klass.getClassLoader());
        }
        System.out.println();
    }

    private static <T> void testReflections2(Class<T> clazz, String... paths) throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        for (String path: paths) {
            builder.addUrls(Paths.get(path).toAbsolutePath().toUri().toURL());
        }
        ClassLoader loader = new URLClassLoader(builder.getUrls().toArray(new URL[]{}), ClassLoader.getSystemClassLoader());
        System.out.println("loader: " + loader);
        builder.setClassLoaders(new ClassLoader[]{loader});
        Reflections reflections = new Reflections(builder);
        Set<Class<? extends T>> klasses = reflections.getSubTypesOf(clazz);
        for (Class<?> klass: klasses) {
            System.out.println("class: " + klass.getCanonicalName());
            System.out.println("loader: " + klass.getClassLoader());
        }
        System.out.println();
    }

    private static void testInvokable() throws Exception {
        Invokable<String, String> invokable = Invokable.from(String.class.getConstructor(char[].class));
        char[] chars = new char[]{'c', 'a', 'g'};
        String str = invokable.invoke(null, (Object) chars);
        System.out.println(str);
    }

    private static <T> void testServiceLoader(Class<T> clazz, String... paths) throws Exception {
        URL[] urls = new URL[paths.length];
        for (int i=0; i< paths.length; i++) {
            URL url = Paths
                    .get(paths[i])
                    .toAbsolutePath()
                    .toUri()
                    .toURL();
            urls[i] = url;
        }
        ClassLoader loader = new URLClassLoader(urls, ClassLoader.getSystemClassLoader());
        System.out.println("loader: " + loader);
        ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz, loader);
        serviceLoader.iterator().forEachRemaining((driver -> {
            System.out.println(driver.getClass().getCanonicalName());
            System.out.println(driver.getClass().getClassLoader());
        }));
    }
}
