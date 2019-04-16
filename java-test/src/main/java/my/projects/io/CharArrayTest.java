package my.projects.io;

import java.io.CharArrayReader;
import java.io.CharArrayWriter;
import java.io.IOException;

public class CharArrayTest {
    public static void main(String[] args) {
        testReader();
        testWriter();
    }

    private static void testReader() {
        String xiaKeXing = "赵客缦胡缨，吴钩霜雪明。🀀🀗😀";
        System.out.println(xiaKeXing);
        CharArrayReader reader = new CharArrayReader(xiaKeXing.toCharArray());
        try {
            int ch = reader.read();
            System.out.println("read: " + (char) ch);

            char[] chars = new char[2];
            int n = reader.read(chars);
            System.out.println("read char[]: " + n + " " + chars[0] + " " + chars[1]);

            reader.skip(2);

            chars = new char[5];
            n = reader.read(chars, 2, 3);
            System.out.println("read char[]: " + n + " " + chars[0] + " " + chars[1] + " " + chars[2] + " " + chars[3] + " " + chars[4]);

            reader.mark(0);  // parameters has no effect
            ch = reader.read();
            System.out.println("read: " + (char) ch);
            ch = reader.read();
            System.out.println("read: " + (char) ch);

            reader.reset();  // reset to marked position
            ch = reader.read();
            System.out.println("reset read: " + (char) ch);

            // '🀀🀗😀' can be encoded with single-char, they are encoded in double-char
            while ((ch = reader.read()) != -1) {
                System.out.println("read: " + (char) ch);
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void testWriter() {
        CharArrayWriter writer = new CharArrayWriter(20);
        try {
            writer.write('赵');
            writer.write("客缦".toCharArray());
            writer.write("胡缨，吴钩".toCharArray(), 1, 3);
            writer.write("霜雪明。🀀", 1, 2);
            writer.write("🀀");
            writer.append('H');

            System.out.println(writer.toString() + " " + writer.toString().length());

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
