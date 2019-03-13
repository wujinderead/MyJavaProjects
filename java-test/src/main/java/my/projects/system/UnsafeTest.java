package my.projects.system;

import java.lang.reflect.Field;
import static java.lang.System.out;

public class UnsafeTest {
    private static final sun.misc.Unsafe unsafe;
    static {
        try {
            Field field = sun.misc.Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            unsafe = (sun.misc.Unsafe) field.get(null);
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public static void main(String[] args) throws Exception {
        testMemory();
        testGetSet();
        testAllocate();
        testWrapperClass();
        testArray();
    }

    private static void testMemory() {
        out.println("addr size: " + unsafe.addressSize());   // 8 means 64-bit machine
        out.println("page size: " + unsafe.pageSize());
    }

    private static void testGetSet() throws Exception {
        Tester t1 = new Tester();
        Tester t2 = new Tester(4, 7.8);
        Tester t3 = Tester.class.newInstance();  // reflection, should be same as 'new Tester()'
        Tester t4 = (Tester) unsafe.allocateInstance(Tester.class);  // fields use zero values
        out.println("t1: " + t1);
        out.println("t2: " + t2);
        out.println("t3: " + t3);
        out.println("t4: " + t4);
        out.println("static: " + Tester.a);

        // set static
        Object base = unsafe.staticFieldBase(Tester.class.getDeclaredField("a"));
        long sOff = unsafe.staticFieldOffset(Tester.class.getDeclaredField("a"));
        out.println("static: " + unsafe.getInt(base, sOff));
        unsafe.putInt(base, sOff, 777);
        out.println("static: " + Tester.a);

        // get
        long bOff = unsafe.objectFieldOffset(Tester.class.getDeclaredField("b"));
        long cOff = unsafe.objectFieldOffset(Tester.class.getDeclaredField("c"));
        out.println("t1.b: " + unsafe.getLong(t1, bOff));
        out.println("t1.c: " + unsafe.getDouble(t1, cOff));
        out.println("t2.b: " + unsafe.getLong(t2, bOff));
        out.println("t2.c: " + unsafe.getDouble(t2, cOff));

        // getAndAdd, getAndSet
        out.println("t1.b: " + unsafe.getAndSetLong(t1, bOff, 99));
        out.println("t1: " + t1);
        out.println("t2.c: " + unsafe.getAndAddLong(t2, bOff, 7));
        out.println("t2: " + t2);

        // put
        unsafe.putDouble(t1, cOff, 95.46);
        out.println("t1: " + t1);

        // compare and swap
        out.println("eq: " + unsafe.compareAndSwapLong(t1, bOff, 98, 107));
        out.println("t1: " + t1);
        out.println("eq: " + unsafe.compareAndSwapLong(t1, bOff, 99, 107));
        out.println("t1: " + t1);
    }

    private static void testAllocate() {
        // allocate memory and put and get
        // the allocated memory is in little-endian?
        long size = 4;
        // for example, size=4 means to allocate 4 longs,
        // i.e., 4*8bytes
        final long addr = unsafe.allocateMemory(size);
        System.out.println("addr : " + addr);
        long toPut = 0x12345678abcdef12L;
        long off = 0;
        for (int i=0; i<size; i++) {
            unsafe.putAddress(addr+off, toPut);
            off += 8;
            toPut += 99;
        }
        // get longs
        off = 0;
        for (int i=0; i<size; i++) {
            out.println(Long.toHexString(unsafe.getAddress(addr+off)));
            off += 8;
        }
        // get ints
        off = 0;
        for (int i=0; i<size*2; i++) {
            out.println(Integer.toHexString(unsafe.getInt(addr+off)));
            off += 4;
        }
        // get shorts
        off = 0;
        for (int i=0; i<size*4; i++) {
            out.println(Long.toHexString(Short.toUnsignedLong(unsafe.getShort(addr+off))));
            off += 2;
        }
        // get bytes
        off = 0;
        for (int i=0; i<size*8; i++) {
            out.println(Long.toHexString(Byte.toUnsignedLong(unsafe.getByte(addr+off))));
            off += 1;
        }
        // free memory failed, why?
        // unsafe.freeMemory(addr);
    }

    private static void testArray() throws Exception {
        int longOff = unsafe.arrayBaseOffset(long[].class);
        int longScale = unsafe.arrayIndexScale(long[].class);
        out.println("long off: " + longOff);
        out.println("long scale: " + longScale);
        long[] longs = new long[3];
        unsafe.putLong(longs, (long) longOff, 123);
        unsafe.putLong(longs, (long) longOff+longScale, 234);
        unsafe.putLong(longs, (long) longOff+longScale*2, 567);
        out.println(longs[0]);
        out.println(longs[1]);
        out.println(longs[2]);

        short[] shorts = new short[4];
        int shortOff = unsafe.arrayBaseOffset(shorts.getClass());
        int shortScale = unsafe.arrayIndexScale(shorts.getClass());
        out.println("short off: " + shortOff);
        out.println("short scale: " + shortScale);
        unsafe.putShort(shorts, (long) shortOff, (short) 123);
        unsafe.putShort(shorts, (long) shortOff+shortScale, (short) 456);
        unsafe.putShort(shorts, (long) shortOff+shortScale*2, (short) -32000);
        unsafe.putShort(shorts, (long) shortOff+shortScale*3, (short) 32000);
        out.println(shorts[0]);
        out.println(shorts[1]);
        out.println(shorts[2]);
        out.println(shorts[3]);

        Tester[] testers = new Tester[4];
        int off = unsafe.arrayBaseOffset(testers.getClass());
        int scale = unsafe.arrayIndexScale(testers.getClass());
        out.println("off: " + off);
        out.println("scale: " + scale);
        unsafe.putObject(testers, (long) off, new Tester());
        unsafe.putObject(testers, (long) off+scale, new Tester(999, 8.7));
        unsafe.putObject(testers, (long) off+scale*2, Tester.class.newInstance());
        unsafe.putObject(testers, (long) off+scale*3, unsafe.allocateInstance(Tester.class));
        out.println(testers[0]==null);
        out.println(testers[1]==null);
        out.println(testers[2]==null);
        out.println(testers[3]==null);
        out.println(testers[0]);
        out.println(testers[1]);
        out.println(testers[2]);
        out.println(testers[3]);
    }

    private static void testWrapperClass() {
        long pos = 0x1234567890abcdefL;
        long neg = 0xfedcba0987654321L;
        out.println("pos: " + pos + ", hex: " + Long.toHexString(pos));
        out.println("neg: " + neg + ", hex: " + Long.toHexString(neg));
        out.println("pos>neg: " + Long.compare(pos, neg));
        out.println("upos<uneg: " + Long.compareUnsigned(pos, neg));
        out.println("unsigned decimal: " + Long.toUnsignedString(pos));
        out.println("unsigned decimal: " + Long.toUnsignedString(neg));

        int ipos = 0x12345678;
        int ineg = 0xfedcba09;
        out.println("pos: " + ipos + ", hex: " + Integer.toHexString(ipos));
        out.println("neg: " + ineg + ", hex: " + Integer.toHexString(ineg));
        out.println("pos>neg: " + Integer.compare(ipos, ineg));
        out.println("upos<uneg: " + Integer.compareUnsigned(ipos, ineg));
        out.println("unsigned decimal: " + Integer.toUnsignedString(ipos));
        out.println("unsigned decimal: " + Integer.toUnsignedString(ineg));
        out.println("toUnsignedLong: " + Integer.toUnsignedLong(ipos));
        out.println("toUnsignedLong: " + Integer.toUnsignedLong(ineg));

        double db1 = +1.234E123;
        double db2 = -2.345E34;
        out.println("pos: " + db1);
        out.println("neg: " + db2);
        out.println("db1 underlying: " + Long.toHexString(Double.doubleToLongBits(db1)));
        out.println("db2 underlying: " + Long.toHexString(Double.doubleToLongBits(db2)));
        out.println("db1 hex: " + Double.toHexString(db1));
        out.println("db2 hex: " + Double.toHexString(db2));
        out.println("long to double: " + Double.longBitsToDouble(0x1234567890abcdefL));
        out.println("long to double: " + Double.longBitsToDouble(0xfedcba0987654321L));
    }
}