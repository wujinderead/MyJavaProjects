package my.projects.structure;

import java.util.EnumMap;
import java.util.EnumSet;

public class EnumMapTest {
    public static void main(String[] args) {
        testEnum();
    }

    enum Mes {
        Enero(1),
        Febrero(2),
        Marzo(3),
        Abril(4),
        Mayo(5),
        Junio(6),
        Julio(7),
        Agosto(8),
        Septiembre(9),
        Octubre(10),
        Noviembre(11),
        Diciembre(12);

        int mes;
        Mes(int mes) {
            this.mes = mes;
        }
        @Override
        public String toString() {
            return this.name();
        }

        public int getMes() {
            return mes;
        }
    }

    enum Month {
        January(1),
        February(2),
        March(3),
        April(4),
        May(5),
        June(6),
        July(7),
        August(8),
        September(9),
        October(10),
        November(11),
        December(12);

        int month;
        Month(int month) {
            this.month = month;
        }
        @Override
        public String toString() {
            return this.name();
        }
    }

    private static void testEnum() {
        Month month1 = Month.January;
        Mes mes1 = Mes.Enero;
        System.out.println(month1.month + " " + month1.toString() + " " + month1.ordinal() + " " + month1.name());
        System.out.println(mes1.mes + " " + mes1.toString() + " " + mes1.ordinal() + " " + mes1.name());

        EnumMap<Month, String> map = new EnumMap<>(Month.class);
        map.put(Month.January, "一月");
        map.put(Month.February, "二月");
        map.put(Month.March, "三月");
        map.put(Month.April, "四月");
        map.put(Month.May, "五月");
        map.put(Month.June, "六月");
        map.put(Month.July, "七月");
        map.put(Month.August, "八月");
        map.put(Month.September, "九月");
        map.put(Month.October, "十月");
        map.put(Month.November, "十一月");
        map.put(Month.December, "十二月");
        System.out.println(map.get(Month.January));
        System.out.println(map.get(Month.February));
        System.out.println(map.get(Month.March));

        EnumSet<Mes> set = EnumSet.range(Mes.Marzo, Mes.Julio);
        for (Mes mes: set) {
            System.out.println(mes.toString());
            System.out.println(mes.name());
            System.out.println(mes.ordinal());
            System.out.println(mes.getMes());
        }
    }

}
