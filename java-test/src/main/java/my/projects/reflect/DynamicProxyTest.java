package my.projects.reflect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class DynamicInvocationHandler implements InvocationHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(
            DynamicInvocationHandler.class);

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LOGGER.info("Invoked method: {}", method.getName());
        return 42;
    }
}

class TimingDynamicInvocationHandler implements InvocationHandler {

    private static Logger LOGGER = LoggerFactory.getLogger(
            TimingDynamicInvocationHandler.class);

    private final Map<String, Method> methods = new HashMap<>();

    private Object target;

    public TimingDynamicInvocationHandler(Object target) {
        this.target = target;

        for(Method method: target.getClass().getDeclaredMethods()) {
            this.methods.put(method.getName(), method);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {
        long start = System.nanoTime();
        Object result = method.invoke(target, args);
        long elapsed = System.nanoTime() - start;

        LOGGER.info("Executing {} finished in {} ns", method.getName(),
                elapsed);

        return result;
    }
}

class MyClass {
    public Map<String, List<Long>> map;
    public int inter;
}

public class DynamicProxyTest {
    public static void main(String[] args) throws Exception {
        Map proxyInstance = (Map) Proxy.newProxyInstance(
                DynamicProxyTest.class.getClassLoader(),
                new Class[] { Map.class },
                new DynamicInvocationHandler());
        System.out.println(proxyInstance.put("hello", "world"));

        Map mapProxyInstance = (Map) Proxy.newProxyInstance(
                DynamicProxyTest.class.getClassLoader(),
                new Class[] { Map.class },
                new TimingDynamicInvocationHandler(new HashMap<>()));

        System.out.println(mapProxyInstance.put("hello", "world"));
        System.out.println(mapProxyInstance.get("hello"));

        CharSequence csProxyInstance = (CharSequence) Proxy.newProxyInstance(
                DynamicProxyTest.class.getClassLoader(),
                new Class[] { CharSequence.class },
                new TimingDynamicInvocationHandler("Hello World"));

        System.out.println(csProxyInstance.length());

        {
            Field field = MyClass.class.getField("inter");
            Type fieldType = field.getType();
            System.out.println("===" + fieldType.getTypeName());
            Type genericFieldType = field.getGenericType();
            System.out.println("===" + genericFieldType.getTypeName());
        }
        Field field = MyClass.class.getField("map");
        Type fieldType = field.getType();
        System.out.println("===" + fieldType.getTypeName());
        Type genericFieldType = field.getGenericType();
        System.out.println("===" + genericFieldType.getTypeName());
        if(genericFieldType instanceof ParameterizedType){
            ParameterizedType aType = (ParameterizedType) genericFieldType;
            Type[] fieldArgTypes = aType.getActualTypeArguments();
            for(Type fieldArgType : fieldArgTypes){
                //Class fieldArgClass = (Class) fieldArgType;
                Class fieldArgClass = fieldArgType.getClass();
                System.out.println("fieldArgClass = " + fieldArgClass.getCanonicalName());
                System.out.println("tyep name = " + fieldArgType.getTypeName());
            }
        }
    }
}
