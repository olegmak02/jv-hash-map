package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_CAPACITY = 16;
    private static final double DEFAULT_LOAD_FACTOR = .75;
    private static final int INCREASING_COEFFICIENT = 2;

    private int size;
    private int capacity = DEFAULT_CAPACITY;
    private Entry<K, V>[] table = new Entry[capacity];

    public MyHashMap() {
    }

    @Override
    public void put(K key, V value) {
        if (size + 1 > capacity * DEFAULT_LOAD_FACTOR) {
            increaseBuckets();
        }

        int keyHash = getHash(key);
        int bucketIndex = keyHash & (capacity - 1);

        putInBucket(key, value, keyHash, bucketIndex);
    }

    @Override
    public V getValue(K key) {
        int keyHash = getHash(key);
        int bucketIndex = keyHash & (capacity - 1);
        Entry<K, V> entry = table[bucketIndex];
        if (entry == null) {
            return null;
        }

        while (entry != null && (entry.hash != keyHash || !Objects.equals(entry.key, key))) {
            entry = entry.next;
        }
        return entry == null ? null : entry.value;
    }

    @Override
    public int getSize() {
        return size;
    }

    private void increaseBuckets() {
        size = 0;
        capacity *= INCREASING_COEFFICIENT;
        Entry<K, V>[] oldEntries = table;
        table = new Entry[capacity];
        for (Entry<K, V> entry : oldEntries) {
            if (entry != null) {
                do {
                    put(entry.key, entry.value);
                    entry = entry.next;
                } while (entry != null);
            }
        }
    }

    private void putInBucket(K key, V value, int keyHash, int bucketIndex) {
        Entry<K, V> entry = table[bucketIndex];
        Entry<K, V> prevEntry = entry;

        if (entry == null) {
            table[bucketIndex] = new Entry<>(key, value);
            size++;
            return;
        }

        while (entry != null) {
            if (entry.hash == keyHash && Objects.equals(entry.key, key)) {
                entry.value = value;
                return;
            }
            prevEntry = entry;
            entry = entry.next;
        }
        prevEntry.next = new Entry<>(key, value);
        size++;
    }

    private static <K> int getHash(K key) {
        return key == null ? 0 : key.hashCode();
    }

    private static class Entry<K, V> {
        private final K key;
        private final int hash;
        private V value;
        private Entry<K, V> next;

        public Entry(K key, V value) {
            this.key = key;
            this.hash = getHash(key);
            this.value = value;
        }
    }
}
