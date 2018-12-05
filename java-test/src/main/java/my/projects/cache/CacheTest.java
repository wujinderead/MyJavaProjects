package my.projects.cache;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.util.BitSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class CacheTest {
    public static void main(String[] args) {
        //cacheTest();
        bitSetTest();
    }

    public static void cacheTest() {
        LoadingCache<String, String> cache = CacheBuilder.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build(new CacheLoader<String, String>() {
                    @Override
                    public String load(String key) throws Exception {
                        return DatatypeConverter.printHexBinary(key.getBytes(Charset.forName("UTF-8")));
                    }
                });
        try {
            System.out.println(cache.get("my name is van."));
            System.out.println(cache.get("中文翻譯 英漢字典."));
            System.out.println(cache.get("my name is van."));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public static void bitSetTest() {
        long a = 23123L;
        long b = 80834417301L;
        BitSet bitSet = BitSet.valueOf(new long[]{a, b});
        System.out.println(Long.toBinaryString(a));
        System.out.println(Long.toBinaryString(b));
        for (int i=bitSet.length()-1; i>=0; i--) {
            System.out.print(bitSet.get(i) ? 1 : 0);
        }
    }
}
