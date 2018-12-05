package my.projects.util;

import sun.misc.Unsafe;

import javax.xml.bind.DatatypeConverter;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

public class BigIntegerTest {
    public static void main(String[] args) throws Exception {
        //testBigInteger();
        //testBigIntegerMag();
        //testShift();
        //mulLongBigInt("fefefefed7d8d9da", "efefefef7d6c5b4a");
        //mulLongString("fefefefed7d8d9da", "efefefef7d6c5b4a");
        //bn_GF2m_mul_1x1(0xfefefefed7d8d9daL, 0xefefefef7d6c5b4aL);
        //mulLongString("efefefef7d6c5b4afefefefed7d8d9da", "fefefefed7d8d9daefefefef7d6c5b4a");
        //bn_GF2m_mul_2x2(0xefefefef7d6c5b4aL, 0xfefefefed7d8d9daL, 0xfefefefed7d8d9daL, 0xefefefef7d6c5b4aL);
        mulLongString("Efefefef7d6c5b4aFefefefed7d8d9daEfefefef7d6c5b4aFefefefed7d8d9daEfefefef7d6c5b4a",
                "Fefefefed7d8d9daEfefefef7d6c5b4aFefefefed7d8d9daEfefefef7d6c5b4aFefefefed7d8d9da");
        BN_GF2m_mul_arr(new long[]{0xefefefef7d6c5b4aL, 0xfefefefed7d8d9daL, 0xefefefef7d6c5b4aL, 0xfefefefed7d8d9daL, 0xefefefef7d6c5b4aL},
                new long[]{0xfefefefed7d8d9daL, 0xefefefef7d6c5b4aL, 0xfefefefed7d8d9daL, 0xefefefef7d6c5b4aL, 0xfefefefed7d8d9daL});
    }

    public static void testBigInteger() {
        BigInteger p = BigInteger.valueOf(1).shiftLeft(255).subtract(BigInteger.valueOf(19));
        System.out.println(DatatypeConverter.printHexBinary(p.toByteArray()));
        BigInteger d = BigInteger.valueOf(121666).modInverse(p);
        d = BigInteger.valueOf(-121665).multiply(d).mod(p);
        System.out.println(d.toString());

    }

    private static void testBigIntegerMag() throws Exception {
        BigInteger p = BigInteger.probablePrime(102, new Random());
        System.out.println(p.toString(10));
        System.out.println(p.toString(16));
        System.out.println(p.toString(2));
        System.out.println(DatatypeConverter.printHexBinary(p.toByteArray()));
        Field f = Unsafe.class.getDeclaredField("theUnsafe");
        f.setAccessible(true);
        Unsafe unsafe = (Unsafe) f.get(null);
        Class clazz = BigInteger.class;
        Field field = clazz.getDeclaredField("mag");
        field.setAccessible(true);
        int[] mag1 = (int[]) field.get(p);
        long offset = unsafe.objectFieldOffset(field);
        int[] mag = (int[]) unsafe.getObject(p, offset);
        for (int i=0; i<mag.length; i++) {
            System.out.print(Integer.toBinaryString(mag[i]) + " ");
        }
        System.out.println();
        for (int i=0; i<mag.length; i++) {
            System.out.print(Integer.toHexString(mag[i]) + " ");
        }
        System.out.println();
        System.out.println("mag: " + Arrays.toString(mag));
        System.out.println("mag1: " + Arrays.toString(mag1));
    }

    private static void testShift() {
        // java '<<' is logical left shift
        //      '>>>' is logical right shift
        //      '>>' is arithmetical right shift, when right shift a negative number, insert 1 to the highest bit;
        //           when right shift a positive number, insert 0 to the highest bit;
        for (int a:new int[]{96754441, -206107026, 803362655, Integer.MIN_VALUE+1}) {
            System.out.println("<<");
            for (int i=0;i<=32;i++) {
                System.out.println(fillBinStr(Integer.toBinaryString(a<<i)) + " " + (a<<i));
            }
            System.out.println();
            System.out.println(">>");
            for (int i=0;i<=32;i++) {
                System.out.println(fillBinStr(Integer.toBinaryString(a>>i)) + " " + (a>>i));
            }
            System.out.println();
            System.out.println(">>>");
            for (int i=0;i<=32;i++) {
                System.out.println(fillBinStr(Integer.toBinaryString(a>>>i)) + " " + (a>>>i));
            }
            System.out.println();
        }
    }

    private static String fillBinStr(String str) {
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<32-str.length(); i++) {
            builder.append('0');
        }
        return builder.append(str).toString();
    }

    private static String fillLongStr(long a) {
        String str = Long.toBinaryString(a);
        StringBuilder builder = new StringBuilder();
        for (int i=0; i<64-str.length(); i++) {
            builder.append('0');
        }
        return builder.append(str).toString();
    }

    private static void mulLongBigInt(String a, String b) {
        BigInteger aa = new BigInteger(a, 16);
        BigInteger bb = new BigInteger(b, 16);
        BigInteger re = BigInteger.ZERO;
        for (int i=0; i<64; i++) {
            if (bb.testBit(i)) {
                re = re.xor(aa);
            }
            aa = aa.shiftLeft(1);
        }
        System.out.println(re.toString(2));
    }

    private static void mulLongString(String aa, String bb) {
        String a = new BigInteger(aa, 16).toString(2);
        String b = new BigInteger(bb, 16).toString(2);
        int[] re = new int[a.length()+b.length()-1];
        char[] res = new char[a.length()+b.length()-1];
        for (int i=0; i<a.length(); i++) {
            for (int j=0; j<b.length(); j++) {
                re[i+j] += (a.charAt(i)-'0')*(b.charAt(j)-'0');
            }
        }
        for (int i=0; i<a.length()+b.length()-1; i++) {
            res[i] = re[i]%2==0 ? '0' : '1';
        }
        System.out.println(new String(res));
    }

    private static long[] bn_GF2m_mul_1x1(long a, long b) {
        long h, l, s;
        long[] tab = new long[16];
        long top3b = a >>> 61;
        long a1, a2, a4, a8;

        a1 = a & (0x1FFFFFFFFFFFFFFFL);
        a2 = a1 << 1;
        a4 = a2 << 1;
        a8 = a4 << 1;

        tab[0] = 0;
        tab[1] = a1;
        tab[2] = a2;
        tab[3] = a1 ^ a2;
        tab[4] = a4;
        tab[5] = a1 ^ a4;
        tab[6] = a2 ^ a4;
        tab[7] = a1 ^ a2 ^ a4;
        tab[8] = a8;
        tab[9] = a1 ^ a8;
        tab[10] = a2 ^ a8;
        tab[11] = a1 ^ a2 ^ a8;
        tab[12] = a4 ^ a8;
        tab[13] = a1 ^ a4 ^ a8;
        tab[14] = a2 ^ a4 ^ a8;
        tab[15] = a1 ^ a2 ^ a4 ^ a8;

        s = tab[(int)(b & 0xF)];
        l = s;
        s = tab[(int)(b >>> 4 & 0xF)];
        l ^= s << 4;
        h = s >>> 60;
        s = tab[(int)(b >>> 8 & 0xF)];
        l ^= s << 8;
        h ^= s >>> 56;
        s = tab[(int)(b >>> 12 & 0xF)];
        l ^= s << 12;
        h ^= s >>> 52;
        s = tab[(int)(b >>> 16 & 0xF)];
        l ^= s << 16;
        h ^= s >>> 48;
        s = tab[(int)(b >>> 20 & 0xF)];
        l ^= s << 20;
        h ^= s >>> 44;
        s = tab[(int)(b >>> 24 & 0xF)];
        l ^= s << 24;
        h ^= s >>> 40;
        s = tab[(int)(b >>> 28 & 0xF)];
        l ^= s << 28;
        h ^= s >>> 36;
        s = tab[(int)(b >>> 32 & 0xF)];
        l ^= s << 32;
        h ^= s >>> 32;
        s = tab[(int)(b >>> 36 & 0xF)];
        l ^= s << 36;
        h ^= s >>> 28;
        s = tab[(int)(b >>> 40 & 0xF)];
        l ^= s << 40;
        h ^= s >>> 24;
        s = tab[(int)(b >>> 44 & 0xF)];
        l ^= s << 44;
        h ^= s >>> 20;
        s = tab[(int)(b >>> 48 & 0xF)];
        l ^= s << 48;
        h ^= s >>> 16;
        s = tab[(int)(b >>> 52 & 0xF)];
        l ^= s << 52;
        h ^= s >>> 12;
        s = tab[(int)(b >>> 56 & 0xF)];
        l ^= s << 56;
        h ^= s >>> 8;
        s = tab[(int)(b >>> 60)];
        l ^= s << 60;
        h ^= s >>> 4;

        /* compensate for the top three bits of a */

        if ((top3b & 01) != 0) {
            l ^= b << 61;
            h ^= b >>> 3;
        }
        if ((top3b & 02) != 0) {
            l ^= b << 62;
            h ^= b >>> 2;
        }
        if ((top3b & 04) != 0) {
            l ^= b << 63;
            h ^= b >>> 1;
        }
        //System.out.println(Long.toBinaryString(h));
        //System.out.println(Long.toBinaryString(l));
        return new long[]{h, l};
    }

    private static long[] bn_GF2m_mul_2x2(long a1, long a0, long b1, long b0) {
        /* r[3] = h1, r[2] = h0; r[1] = l1; r[0] = l0 */
        long[] rh = bn_GF2m_mul_1x1(a1, b1);
        long[] rl = bn_GF2m_mul_1x1(a0, b0);
        long[] m = bn_GF2m_mul_1x1(a0 ^ a1, b0 ^ b1);
        /* Correction on m1 ^= l1 ^ h1; m0 ^= l0 ^ h0; */
        rh[1] ^= m[0] ^ rl[0] ^ rh[0];   /* h0 ^= m1 ^ l1 ^ h1; */
        rl[0] = rh[0] ^ rh[1] ^ rl[1] ^ m[0] ^ m[1]; /* l1 ^= l0 ^ h0 ^ m0; */
        //System.out.println(fillLongStr(rh[0]));
        //System.out.println(fillLongStr(rh[1]));
        //System.out.println(fillLongStr(rl[0]));
        //System.out.println(fillLongStr(rl[1]));
        return new long[]{rl[0], rl[1], rh[0], rh[1]};
    }

    private static void BN_GF2m_mul_arr(long[] a, long[] b) {
        int zlen, i, j, k;
        long x1, x0, y1, y0;
        long[] zz;

        zlen = a.length + b.length + 2;
        long[] s = new long[zlen];

        for (i = 0; i < zlen; i++)
            s[i] = 0;

        for (j = 0; j < b.length; j += 2) {
            y0 = b[j];
            y1 = (j + 1) == b.length ? 0 : b[j + 1];
            for (i = 0; i < a.length; i += 2) {
                x0 = a[i];
                x1 = (i + 1) == a.length ? 0 : a[i + 1];
                zz = bn_GF2m_mul_2x2(x1, x0, y1, y0);
                for (k = 0; k < 4; k++)
                    s[i + j + k] ^= zz[k];
            }
        }
        for (i=0; i<s.length; i++) {
            System.out.println(s[i]);
        }
        System.out.println();
    }
}
