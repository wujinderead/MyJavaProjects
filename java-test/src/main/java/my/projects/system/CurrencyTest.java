package my.projects.system;

import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public class CurrencyTest {
    public static void main(String[] args) {
        testConcurrency();
    }

    private static void testConcurrency() {
        Set<Currency> set = Currency.getAvailableCurrencies();
        System.out.println("currency size: " + set.size());
        for (Currency currency: set) {
            System.out.println("code: " + currency.getCurrencyCode());
            System.out.println("cn name: " + currency.getDisplayName());
            System.out.println("us name: " + currency.getDisplayName(Locale.US));
            System.out.println("de name: " + currency.getDisplayName(Locale.GERMANY));
            System.out.println("symbol: " + currency.getSymbol());
            System.out.println("digits: " + currency.getDefaultFractionDigits());
            System.out.println("numeric: " + currency.getNumericCode());
            System.out.println();
        }
    }
}
