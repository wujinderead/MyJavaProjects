package my.projects.system;

import java.nio.charset.Charset;
import java.util.SortedMap;

import static javax.xml.bind.DatatypeConverter.printHexBinary;

public class CharsetTest {
    public static void main(String[] args) {
        testCharset();
        testCharsets();
        testString();
    }

    private static void testCharset() {
        SortedMap<String, Charset> map = Charset.availableCharsets();
        System.out.println("class :" + map.getClass());
        for (Charset cs: new Charset[]{Charset.defaultCharset(), Charset.forName("Unicode")}) {
            System.out.println("charset: " + cs.name());
            System.out.println("display: " + cs.displayName());
            System.out.println("alias: " + cs.aliases());
            System.out.println("can encode: " + cs.canEncode());
        }
    }

    private static void testCharsets() {
        SortedMap<String, Charset> map = Charset.availableCharsets();
        for (String key: map.keySet()) {
            Charset cs = map.get(key);
            System.out.println("key: " + key);
            System.out.println("charset: " + cs.name());
            System.out.println("display: " + cs.displayName());
            System.out.println("alias: " + cs.aliases());
            System.out.println("can encode: " + cs.canEncode());
            System.out.println();
        }
    }

    private static void testString() {
        // 😀 unicode \u1f660, exceeds the limit of char (16 bits) in java;
        // thus, to specify a char '😀', should use UTF-16 encode \ud83d\ude00.
        // therefore, the length of string "😀" is actually 2.
        String str = "商 9 ࿗ ࿘ ᐇ ᑥ \uD83D\uDE00 \uD834\uDF01 \u262F";
        System.out.println(printHexBinary(str.getBytes(Charset.forName("UTF-16"))));
        System.out.println(printHexBinary(str.getBytes(Charset.forName("UTF-8"))));
        //              商      9    ࿗     ࿘      ᐇ      ᑥ     😀        𝌁       ☯
        // utf16 = FEFF 5546   0039 0FD7   0FD8   1407   1465   D83DDE00 D834DF01 262F
        // utf8 =       E59586 39   E0BF97 E0BF98 E19087 E191A5 F09F9880 F09D8C81 E298AF
        String str1 = "商\u0FD7\u0FD8\u1407\u1465\uD83D\uDE00\uD834\uDF01\u262F";
        System.out.println(str + ", " + str1);
        System.out.println(str1.length());
        for (char ch: str1.toCharArray()) {
            System.out.println("'" + ch + "': " + Integer.toHexString((int) ch));
        }
        System.out.println("😀".length());  // 2
    }
}
