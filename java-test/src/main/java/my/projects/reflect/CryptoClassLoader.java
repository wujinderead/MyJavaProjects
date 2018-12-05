package my.projects.reflect;

import com.google.common.primitives.Bytes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

import static javax.xml.bind.DatatypeConverter.parseHexBinary;
import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class CryptoClassLoader extends ClassLoader {
    Cipher cipher;

    public CryptoClassLoader() throws Exception {
        DESKeySpec desKey = new DESKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey key = keyFactory.generateSecret(desKey);
        System.out.println("key: " + printHexBinary(key.getEncoded()));

        cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        System.out.println("iv : " + printHexBinary(cipher.getIV()));
    }

    public synchronized Class<?> findClass(String name, String filename) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int len;
        byte[] buf = new byte[1024];
        try {
            InputStream is = new FileInputStream(filename);
            while ((len = is.read(buf)) != -1) {
                bos.write(cipher.update(buf, 0, len));
            }
            bos.write(cipher.doFinal());
            is.close();
            bos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        byte[] data = bos.toByteArray();
        return defineClass(name, data, 0, data.length);
    }

    public static void main(String[] args) {
        cryptFile("/home/xzy/bcprov", "/home/xzy/end", Cipher.ENCRYPT_MODE);
        cryptFile("/home/xzy/end", "/home/xzy/ded", Cipher.DECRYPT_MODE);
        try {
            CryptoClassLoader loader = new CryptoClassLoader();
            System.out.println(loader);
            Class<?> klass = loader.findClass("cn.enn.com.ftp.source.FtpSourceConfig", "/home/xzy/DesFtpSourceConfig.class");
            System.out.println(klass.getCanonicalName());
            Method[] methods = klass.getMethods();
            for (Method method: methods) {
                System.out.println(method.getName());
            }
            System.out.println(klass.getClassLoader());
            System.out.println(Cipher.class.getClassLoader());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static byte[] keyBytes = parseHexBinary("E10ADC3949BA64AC");
    static byte[] ivBytes = parseHexBinary("6D8040B05E6E2C93");

    private static void cryptFile(String inFile, String outFile, int mode) {
        try {
            DESKeySpec desKey = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(desKey);
            System.out.println("key: " + printHexBinary(key.getEncoded()));

            Cipher encypter = Cipher.getInstance("DES/CBC/PKCS5Padding");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);
            encypter.init(mode, key, ivParameterSpec);
            System.out.println("iv : " + printHexBinary(encypter.getIV()));

            InputStream input = new FileInputStream(inFile);
            File outFiler = new File(outFile);
            if (!outFiler.exists()) {
                outFiler.createNewFile();
            }
            OutputStream output = new FileOutputStream(outFiler);
            byte[] buf = new byte[1024];
            byte[] encrypted;
            int offset;
            while ((offset = input.read(buf)) != -1) {
                encrypted = encypter.update(buf, 0, offset);
                output.write(encrypted);
            }
            encrypted = encypter.doFinal();
            output.write(encrypted);
            output.flush();
            output.close();
            input.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void testDes() {
        Charset charset = Charset.forName("UTF-8");
        byte[] data1 = "abcdefghijklmnopqrstuvwxyz".getBytes(charset);
        byte[] data2 = "zyxwvutsrqponmlkjihgfedcba".getBytes(charset);
        byte[] data3 = "01234567890123456789012345".getBytes(charset);
        byte[] keyBytes = parseHexBinary("E10ADC3949BA64AC");
        try {
            DESKeySpec desKey = new DESKeySpec(keyBytes);
            SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
            SecretKey key = keyFactory.generateSecret(desKey);
            System.out.println("key: " + printHexBinary(key.getEncoded()));

            {
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                byte[] cipherText1 = cipher.update(data1);
                byte[] cipherText2 = cipher.update(data2);
                byte[] cipherText3 = cipher.doFinal(data3);
                System.out.println("t1: " + printHexBinary(cipherText1));
                System.out.println("t2: " + printHexBinary(cipherText2));
                System.out.println("t3: " + printHexBinary(cipherText3));
            }
            System.out.println();
            {
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                byte[] cipherText1 = cipher.doFinal(data1);
                byte[] cipherText2 = cipher.update(data2);
                byte[] cipherText3 = cipher.doFinal(data3);
                System.out.println("t1: " + printHexBinary(cipherText1));
                System.out.println("t2: " + printHexBinary(cipherText2));
                System.out.println("t3: " + printHexBinary(cipherText3));
            }
            System.out.println();
            {
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                byte[] cipherText1 = cipher.update(data1);
                byte[] cipherText2 = cipher.doFinal(data2);
                byte[] cipherText3 = cipher.doFinal(data3);
                System.out.println("t1: " + printHexBinary(cipherText1));
                System.out.println("t2: " + printHexBinary(cipherText2));
                System.out.println("t3: " + printHexBinary(cipherText3));
            }
            System.out.println();
            {
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                byte[] cipherText1 = cipher.doFinal(data1);
                byte[] cipherText2 = cipher.doFinal(data2);
                byte[] cipherText3 = cipher.doFinal(data3);
                System.out.println("t1: " + printHexBinary(cipherText1));
                System.out.println("t2: " + printHexBinary(cipherText2));
                System.out.println("t3: " + printHexBinary(cipherText3));
            }
            System.out.println();
            {
                Cipher cipher = Cipher.getInstance("DES");
                cipher.init(Cipher.ENCRYPT_MODE, key);

                System.out.println("123:" + printHexBinary(cipher.doFinal(Bytes.concat(data1, data2, data3))));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
