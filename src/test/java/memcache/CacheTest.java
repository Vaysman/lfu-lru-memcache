package memcache;

import org.junit.Test;

import static org.junit.Assert.*;

public class CacheTest {

    @Test
    public void lruStrategyTest() {
        Cache<String, Integer> cache = new Cache<>(Cache.Strategy.LRU, 3);

        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);
        cache.put("four", 4);
        cache.put("five", 5);

        assertNull(cache.get("one"));
        assertNull(cache.get("two"));

        assertEquals(cache.get("three"), new Integer(3));
        assertEquals(cache.get("four"), new Integer(4));
        assertEquals(cache.get("five"), new Integer(5));

        cache.get("three");
        cache.put("six", 6);

        assertNull(cache.get("four"));
        assertEquals(cache.get("three"), new Integer(3));
    }

    @Test
    public void lfuStrategyTest() {
        Cache<String, Integer> cache = new Cache<>(Cache.Strategy.LFU, 3);

        cache.put("one", 1);
        cache.put("two", 2);
        cache.put("three", 3);
        cache.put("four", 4);
        cache.put("five", 5);

        assertNull(cache.get("three"));
        assertNull(cache.get("four"));

        assertEquals(cache.get("one"), new Integer(1));
        assertEquals(cache.get("two"), new Integer(2));
        assertEquals(cache.get("five"), new Integer(5));

        cache.get("one");
        cache.put("six", 6);

        assertNull(cache.get("one"));
        assertEquals(cache.get("six"), new Integer(6));
    }
}
