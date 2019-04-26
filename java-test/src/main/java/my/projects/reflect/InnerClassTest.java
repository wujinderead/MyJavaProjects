package my.projects.reflect;

import java.lang.reflect.Modifier;

import static java.lang.System.out;

public class InnerClassTest {
    public static void main(String[] args) {
        testInnerClassStatic();
    }

    int y = 0;
    private class A{int a=0;void print(){out.println(y);}}
    public class B{int a=0;void print(){out.println(y);}}  // can access non-static field
    static class C{int a=0;}
    public static class D{int a=0;}
    private static class E{int a=0;}

    private void testInnerClass() {
        class F{int a=0;}
        A a = new A();
        B b = new B();
        C c = new C();
        D d = new D();
        E e = new E();
        F f = new F();
        G g = new G();
        for (Object obj: new Object[]{a ,b, c, d, e, f, g}) {
            Class<?> clazz = obj.getClass();
            out.println(clazz.getCanonicalName() + ", " + clazz.getName());
            int mod = clazz.getModifiers();
            out.println(Modifier.toString(mod));
            out.println();
        }
    }

    private static void testInnerClassStatic() {
        class F{int a=0;}
        A a = new InnerClassTest().new A();
        B b = new InnerClassTest().new B();
        C c = new C();
        D d = new D();
        E e = new E();
        F f = new F();
        G g = new G();
        for (Object obj: new Object[]{a, b, c, d, e, f, g}) {
            Class<?> clazz = obj.getClass();
            out.println(clazz.getCanonicalName() + ", " + clazz.getName());
            int mod = clazz.getModifiers();
            out.println(Modifier.toString(mod));
            out.println();
        }
    }
}

class G{int a=0;}