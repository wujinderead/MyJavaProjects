package my.projects.util;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.EnumSet;

public class ColorPrinter extends PrintStream {
    private static final String Reset = "\033[0m";

    private static String[] modes = {"\033[0m", "\033[1m", "\033[4m", "\033[9m"};
    private static String[][] mode_fore;
    private static String[][][] mode_fore_back;
    static {
        mode_fore = new String[4][16];
        for (int i=0; i<4; i++) {
            String prefix = modes[i].substring(0, modes[i].length()-1) + ";";
            for (int j=0; j<8; j++) {
                mode_fore[i][j] = prefix + String.valueOf(j+30) + "m";
            }
            for (int j=8; j<16; j++) {
                mode_fore[i][j] = prefix + String.valueOf(j+82) + "m";
            }
        }
        mode_fore_back = new String[4][16][16];
        for (int i=0; i<4; i++) {
            for (int j=0; j<16; j++) {
                String prefix = mode_fore[i][j].substring(0, mode_fore[i][j].length()-1) + ";";
                for (int k=0; k<8; k++) {
                    mode_fore_back[i][j][k] = prefix + String.valueOf(k+40) + "m";
                }
                for (int k=8; k<16; k++) {
                    mode_fore_back[i][j][k] = prefix + String.valueOf(k+92) + "m";
                }
            }
        }
    }

    public enum Mode {
        Normal,  // escape code 0
        Bold,  // escape code 1
        Underline,  // escape code 4
        Crossout;  // escape code 9
    }

    public enum Color {
        Black, // escape code 30
        Red,
        Green,
        Yellow,
        Blue,
        Magenta,
        Cyan,
        White,  // escape code 37
        Bright_Black,  // escape code 90
        Bright_Red,
        Bright_Green,
        Bright_Yellow,
        Bright_Blue,
        Bright_Magenta,
        Bright_Cyan,
        Bright_White;  // escape code 97
    }

    enum ColorPrinterEnum {
        RED {
            @Override
            public void println(String s) {
                System.out.println("\033[0;31m" + s + Reset);
            }
        },
        BLUE {
            @Override
            public void println(String s) {
                System.out.println("\033[0;34m" + s + Reset);
            }
        },
        GREEN {
            @Override
            public void println(String s) {
                System.out.println("\033[0;32m" + s + Reset);
            }
        };
        public abstract void println(String s);
    }

    public ColorPrinter(OutputStream out) {
        super(out, false);
    }
    
    public void println(Mode mode, String x) {
        super.println(modes[mode.ordinal()] + x + Reset);
    }

    public void println(Color foreground, String x) {
        String a = mode_fore[0][foreground.ordinal()] + x + Reset;
        System.out.println(a);
        super.println(a);
    }

    public void println(Color foreground, Color background, String x) {
        super.println(mode_fore_back[0][foreground.ordinal()][background.ordinal()] + x + Reset);
    }

    public void println(Mode mode, Color foreground, String x) {
        super.println(mode_fore[mode.ordinal()][foreground.ordinal()] + x + Reset);
    }

    public void println(Mode mode, Color foreground, Color background, String x) {
        super.println(mode_fore_back[mode.ordinal()][foreground.ordinal()][background.ordinal()] + x + Reset);
    }

    public static void testColorPrinter() {
        ColorPrinter printer = new ColorPrinter(System.out);
        for (Mode mode: EnumSet.allOf(Mode.class)) {
            printer.println(mode, "xxx  xx");
        }
        for (Mode mode: EnumSet.allOf(Mode.class)) {
            for (Color color: EnumSet.allOf(Color.class)) {
                printer.println(mode, color, "yyy  yyy");
            }
        }
        for (Mode mode: EnumSet.allOf(Mode.class)) {
            for (Color color: EnumSet.allOf(Color.class)) {
                for (Color color1: EnumSet.allOf(Color.class)) {
                    printer.println(mode, color, color1, "zzz zz");
                }
            }
        }
        for (Color color: EnumSet.allOf(Color.class)) {
            printer.println(color, "ccc cc");
        }
        for (Color color: EnumSet.allOf(Color.class)) {
            for (Color color1: EnumSet.allOf(Color.class)) {
                printer.println(color, color1, "ddd dd");
            }
        }
        for (int i=0; i<4; i++) {
            for (int j=0; j<16; j++) {
                System.out.println(mode_fore[i][j].substring(1));
            }
        }
        for (int i=0; i<4; i++) {
            for (int j=0; j<16; j++) {
                for (int k=0; k<16; k++) {
                    System.out.println(mode_fore_back[i][j][k].substring(1));
                }
            }
        }
    }

    public static void testColorPrinterEnum() {
        ColorPrinterEnum.BLUE.println("lalala lla lal d");
        ColorPrinterEnum.RED.println("lalala lla lal d");
        ColorPrinterEnum.GREEN.println("lalala lla lal d");
    }

    public static void main(String[] args) {
        testColorPrinterEnum();
    }
}
