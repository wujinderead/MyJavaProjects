package my.projects.regex;

import java.util.regex.Pattern;

public class RegexTest {
    public static void main(String[] args) {
        testRegex1();
    }

    private static void testRegex1() {
        String[] strs = {"wzw_2018-10-19", "wzw_2018-12-04", "wzw_2018-08-21",};
        Pattern pattern = Pattern.compile("^wzw.*");
        for (String str: strs) {
            System.out.println(str + " " + pattern.matcher(str).matches());
        }
    }
}
