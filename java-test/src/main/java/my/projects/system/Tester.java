package my.projects.system;

public class Tester {
    public static int a = 666;
    private long b = 2;
    private double c = 3.4;

    public Tester(long b, double c) {
        this.b = b;
        this.c = c;
    }

    public Tester() {
        this.b = 77;
    }

    @Override
    public String toString() {
        return "["+b+", "+c+"]";
    }
}
