package my.projects.reflect;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.ClassPath;
import com.google.common.reflect.Invokable;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.connect.connector.Connector;
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
        //testClassLoader();
        //testReflections();
        //test1();
        //test2();
        testReflections1();
        testReflections2();
    }

    private static void testClassLoader() {
        System.out.println(int.class.getClassLoader());
        System.out.println(ObjectMapper.class.getClassLoader());
        System.out.println(ArrayListMultimap.class.getClassLoader());
        System.out.println(ClassLoader.getSystemClassLoader());
    }

    private static String printObj(Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return null;
        }
    }

    private static void testReflections() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        URL url = Paths
                .get("/home/xzy/software/confluent-3.3.0/share/java/kafka-connect-activemq/kafka-connect-activemq-1.0-SNAPSHOT.jar")
                .toAbsolutePath()
                .toUri()
                .toURL();
        builder.addUrls(url);
        URL url1 = Paths
                .get("/home/xzy/software/confluent-3.3.0/share/java/kafka-connect-rabbitmq/kafka-connect-rabbitmq-1.0-SNAPSHOT.jar")
                .toAbsolutePath()
                .toUri()
                .toURL();
        builder.addUrls(url1);
        ClassLoader loader = new URLClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader());
        /*URL url = Paths
                .get("/home/xzy/software/confluent-4.0.0/share/java/kafka-connect-superjar/super-connector.jar")
                .toAbsolutePath()
                .toUri()
                .toURL();
        builder.addUrls(url);
        ClassLoader loader = new URLClassLoader(new URL[]{url}, ClassLoader.getSystemClassLoader());*/
        builder.setClassLoaders(new ClassLoader[]{loader});
        Reflections reflections = new Reflections(builder);
        Set<Class<? extends Connector>> plugins = reflections.getSubTypesOf(Connector.class);
        for (Class<? extends Connector> klass: plugins) {
            System.out.println(klass.getCanonicalName());
        }
        Set<Class<? extends AbstractConfig>> configs = reflections.getSubTypesOf(AbstractConfig.class);
        for (Class<? extends AbstractConfig> klass: configs) {
            System.out.println(klass.getCanonicalName());
        }
        Set<Class<? extends Driver>> drivers = reflections.getSubTypesOf(Driver.class);
        for (Class<? extends Driver> klass: drivers) {
            System.out.println(klass.getCanonicalName());
        }

        ClassPath classpath = ClassPath.from(loader);
        ImmutableSet<ClassPath.ClassInfo> infos = classpath.getAllClasses();
        System.out.println(infos.size());
    }

    private static void test1() throws Exception {
        Invokable<String, String> invokable = Invokable.from(String.class.getConstructor(char[].class));
        char[] chars = new char[]{'c', 'a', 'g'};
        String str = invokable.invoke(null, chars);
        System.out.println(str);
    }

    private static void test2() throws Exception {
        URL url = Paths
                .get("/home/xzy/software/confluent-3.3.0/share/java/kafka-connect-jdbc/postgresql-42.2.2.jar")
                .toAbsolutePath()
                .toUri()
                .toURL();
        URL url1 = Paths
                .get("/home/xzy/software/confluent-3.3.0/share/java/kafka-connect-jdbc/ngdbc-2.2.16.jar")
                .toAbsolutePath()
                .toUri()
                .toURL();
        ClassLoader loader = new URLClassLoader(new URL[]{url, url1}, ClassLoader.getSystemClassLoader());
        ServiceLoader<Driver> serviceLoader = ServiceLoader.load(Driver.class, ClassLoader.getSystemClassLoader());
        serviceLoader.iterator().forEachRemaining((driver -> System.out.println(driver.getClass().getCanonicalName())));
    }

    private static void testReflections1() throws Exception {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        for (String file: new String[]{
                "/home/xzy/confluent-4.0.0/share/java/kafka-connect-superjar/super-connector.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka_2.11-1.0.0-cp1.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka-clients-1.0.0-cp1.jar", 
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka-log4j-appender-1.0.0-cp1.jar", 
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka-streams-1.0.0-cp1.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/connect-runtime-1.0.2-SNAPSHOT.jar", 
                "/home/xzy/confluent-4.0.0/share/java/kafka/connect-transforms-1.0.0.jar",
        }) {
            builder.addUrls(Paths.get(file).toAbsolutePath().toUri().toURL());
        }
        ClassLoader loader = new URLClassLoader(builder.getUrls().toArray(new URL[]{}), ClassLoader.getSystemClassLoader());
        builder.setClassLoaders(new ClassLoader[]{loader});
        Reflections reflections = new Reflections(builder);
        Set<Class<? extends AbstractConfig>> plugins = reflections.getSubTypesOf(AbstractConfig.class);
        System.out.println(plugins.size());
        for (Class<? extends AbstractConfig> klass: plugins) {
            System.out.println(klass.getCanonicalName());
        }
    }

    private static void testReflections2() throws Exception {
        int size = 0;
        for (String file: new String[]{
                "/home/xzy/confluent-4.0.0/share/java/kafka-connect-superjar/super-connector.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka_2.11-1.0.0-cp1.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka-clients-1.0.0-cp1.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka-log4j-appender-1.0.0-cp1.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/kafka-streams-1.0.0-cp1.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/connect-runtime-1.0.2-SNAPSHOT.jar",
                "/home/xzy/confluent-4.0.0/share/java/kafka/connect-transforms-1.0.0.jar",
        }) {
            ConfigurationBuilder builder = new ConfigurationBuilder();
            System.out.println("\n==="+file);
            builder.addUrls(Paths.get(file).toAbsolutePath().toUri().toURL());
            ClassLoader loader = new URLClassLoader(builder.getUrls().toArray(new URL[]{}), ClassLoader.getSystemClassLoader());
            builder.setClassLoaders(new ClassLoader[]{loader});
            Reflections reflections = new Reflections(builder);
            Set<Class<? extends AbstractConfig>> plugins = reflections.getSubTypesOf(AbstractConfig.class);
            for (Class<? extends AbstractConfig> klass: plugins) {
                System.out.println(klass.getCanonicalName());
            }
            size += plugins.size();
        }
        System.out.println(size);
    }
}
