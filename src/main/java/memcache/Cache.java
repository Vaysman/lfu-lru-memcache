package memcache;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Cache<K, V> {
    private final int capacity;
    private final Map<K, Node<K, V>> cacheStorage;
    private final Node<K, V> head;
    private TrimStrategy trimStrategy;

    static class Node<K, V> {
        Node next;
        Node previous;
        K key;
        V value;

        Node(K key, V value) {
            this.key = key;
            this.value = value;
        }
    }

    interface TrimStrategy {
        Node trimCache(Node head);
    }

    class LruTrimStrategy implements TrimStrategy {
        public Node trimCache(Node head) {
            Node node = head.previous;
            head.previous = node.previous;
            node.previous.next = head;
            return node;
        }
    }

    class LfuTrimStrategy implements TrimStrategy {
        public Node trimCache(Node head) {
            Node node = head.next;
            head.next = node.next;
            node.next.previous = head;
            return node;
        }
    }

    public enum Strategy {
        LRU,
        LFU
    }

    public Cache(Strategy strategy, int capacity) {
        final int MIN_CAPACITY = 3;

        if(capacity < MIN_CAPACITY) {
            this.capacity = MIN_CAPACITY;
        } else {
            this.capacity = capacity;
        }

        switch (strategy) {
            case LFU: trimStrategy = new LfuTrimStrategy();
                      break;
            case LRU: trimStrategy = new LruTrimStrategy();
                      break;
        }

        head = new Node<>(null, null);
        head.previous = head;
        head.next = head;

        cacheStorage = new HashMap<>(capacity);
    }

    private void putToHead(Node node) {
        head.next.previous = node;
        node.next = head.next;
        node.previous = head;
        head.next = node;
    }

    private void moveToHead(Node node){
        node.next.previous = node.previous;
        node.previous.next = node.next;
        putToHead(node);
    }

    public void put(K key, V value) {
        if(cacheStorage.size() >= capacity) {
            cacheStorage.remove(trimStrategy.trimCache(head).key);
        }

        Node<K, V> node = new Node<>(key, value);
        putToHead(node);
        cacheStorage.put(key, node);
    }

    public V get(K key) {
        Node node = cacheStorage.get(key);

        if(Objects.isNull(node)) {
            return null;
        } else {
            if(node.previous != head) {
                moveToHead(node);
            }
        }

        return (V) node.value;
    }
}
