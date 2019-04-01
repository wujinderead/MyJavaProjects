package my.projects.util;

import sun.misc.Unsafe;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Arrays;

public class BigIntegerTest {
    public static void main(String[] args) throws Exception {
        //testBigInteger();
        testBigIntegerMag();
        //testShift();
    }

    public static void testBigInteger() {
        BigInteger p = BigInteger.valueOf(1).shiftLeft(255).subtract(BigInteger.valueOf(19));
        System.out.println(DatatypeConverter.printHexBinary(p.toByteArray()));
        BigInteger d = BigInteger.valueOf(121666).modInverse(p);
        d = BigInteger.valueOf(-121665).multiply(d).mod(p);
        System.out.println(d.toString());

    }

    private static void testBigIntegerMag() throws Exception {
        BigInteger p = new BigInteger("3976292833650478493946274414963605588507971231667974845467637", 10);
        System.out.println(p.toString(10));
        System.out.println(p.toString(16));
        System.out.println(p.toString(2));
        System.out.println(DatatypeConverter.printHexBinary(p.toByteArray())); // 027975C23AC603DA2B5CE1A9D7A6B513F0E5D2DFDB5055AB8BF5

        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        Class clazz = BigInteger.class;
        Field field = clazz.getDeclaredField("mag");
        field.setAccessible(true);
        long offset = unsafe.objectFieldOffset(field);

        // the big integer is underlying stored in int[] in big-endian form
        int[] mag = (int[]) unsafe.getObject(p, offset);
        for (int i=0; i<mag.length; i++) {
            System.out.print(intToBin(mag[i]) + " ");
        }
        System.out.println();
        System.out.println("mag: " + Arrays.toString(mag));
        System.out.println();

        // test negative integer
        p = p.negate();
        System.out.println(p.toString(10));
        System.out.println(p.toString(16));
        System.out.println(p.toString(2));
        // pos: 027975C23AC603DA2B5CE1A9D7A6B513F0E5D2DFDB5055AB8BF5
        // neg: FD868A3DC539FC25D4A31E5628594AEC0F1A2D2024AFAA54740B
        // 负数补码表示，即负数是正数取反加一
        System.out.println(DatatypeConverter.printHexBinary(p.toByteArray()));
        mag = (int[]) unsafe.getObject(p, offset);
        for (int i=0; i<mag.length; i++) {
            System.out.print(intToBin(mag[i]) + " ");
        }
        System.out.println();
        // the underlying 'mag' for negative number is the same as positive, just different in 'signum'
        System.out.println("mag: " + Arrays.toString(mag));

        // the values can be modifies by changing 'mag'
        System.out.println();
        mag[2] = 6;
        System.out.println(p.toString(10));
    }

    private static void testShift() {
        // java '<<' is logical left shift: left shift, ditch highest, lowest fill 0
        //      '>>>' is logical right shift: right shift, ditch lowest, highest fill 0
        //      '>>' is arithmetical right shift, when right shift a negative number, insert 1 to the highest bit;
        //           when right shift a positive number, insert 0 to the highest bit;
        // when shift parameter>=32, shift (parameter%32) bits,
        // e.g., left shift 32 means shift 0 bit(no move); right shift 34 means shift 2 bits.
        for (int a:new int[]{96754441, -206107026, 803362655, Integer.MIN_VALUE+1}) {
            System.out.println("<<");
            for (int i=0;i<=32;i++) {
                System.out.println(intToBin(a<<i) + " " + (a<<i));
            }
            System.out.println();
            System.out.println(">>");
            for (int i=0;i<=32;i++) {
                System.out.println(intToBin(a>>i) + " " + (a>>i));
            }
            System.out.println();
            System.out.println(">>>");
            for (int i=0;i<=32;i++) {
                System.out.println(intToBin(a>>>i) + " " + (a>>>i));
            }
            System.out.println();
        }
    }

    private static String intToBin(int inter) {
        String str = Integer.toBinaryString(inter);
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<32-str.length(); i++) {
            if (inter>=0) {
                builder.append('0');
            } else {
                builder.append('1');
            }
        }
        return builder.append(str).toString();
    }

    private static String longToBin(long longer) {
        String str = Long.toBinaryString(longer);
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<64-str.length(); i++) {
            if (longer>=0) {
                builder.append('0');
            } else {
                builder.append('1');
            }
        }
        return builder.append(str).toString();
    }
}
