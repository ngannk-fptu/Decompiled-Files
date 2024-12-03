/*
 * Decompiled with CFR 0.152.
 */
package org.eclipse.jetty.util;

import java.nio.ByteBuffer;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import org.eclipse.jetty.util.AbstractTrie;
import org.eclipse.jetty.util.ArrayTrie;
import org.eclipse.jetty.util.EmptyTrie;
import org.eclipse.jetty.util.TreeTrie;

public interface Index<V> {
    public V get(String var1);

    public V get(ByteBuffer var1);

    public V get(String var1, int var2, int var3);

    public V get(ByteBuffer var1, int var2, int var3);

    public V getBest(String var1, int var2, int var3);

    public V getBest(String var1);

    public V getBest(ByteBuffer var1, int var2, int var3);

    default public V getBest(ByteBuffer b) {
        return this.getBest(b, 0, b.remaining());
    }

    public V getBest(byte[] var1, int var2, int var3);

    default public V getBest(byte[] b) {
        return this.getBest(b, 0, b.length);
    }

    public boolean isEmpty();

    public int size();

    public Set<String> keySet();

    public static <V> Mutable<V> buildMutableVisibleAsciiAlphabet(boolean caseSensitive, int maxCapacity) {
        if (maxCapacity < 0 || maxCapacity > ArrayTrie.MAX_CAPACITY) {
            return new TreeTrie(caseSensitive);
        }
        if (maxCapacity == 0) {
            return EmptyTrie.instance(caseSensitive);
        }
        return new ArrayTrie(caseSensitive, maxCapacity);
    }

    public static <V> Index<V> empty(boolean caseSensitive) {
        return EmptyTrie.instance(caseSensitive);
    }

    public static class Builder<V> {
        Map<String, V> contents;
        boolean caseSensitive;

        public Builder() {
            this.caseSensitive = false;
            this.contents = null;
        }

        Builder(boolean caseSensitive, Map<String, V> contents) {
            this.caseSensitive = caseSensitive;
            this.contents = contents;
        }

        private Map<String, V> contents() {
            if (this.contents == null) {
                this.contents = new LinkedHashMap<String, V>();
            }
            return this.contents;
        }

        public Builder<V> caseSensitive(boolean caseSensitive) {
            this.caseSensitive = caseSensitive;
            return this;
        }

        public Builder<V> withAll(V[] values, Function<V, String> keyFunction) {
            for (V value : values) {
                String key = keyFunction.apply(value);
                this.contents().put(key, value);
            }
            return this;
        }

        public Builder<V> withAll(Supplier<Map<String, V>> entriesSupplier) {
            Map<String, V> map = entriesSupplier.get();
            this.contents().putAll(map);
            return this;
        }

        public Builder<V> with(V value) {
            this.contents().put(value.toString(), value);
            return this;
        }

        public Builder<V> with(String key, V value) {
            this.contents().put(key, value);
            return this;
        }

        public Mutable.Builder<V> mutable() {
            return new Mutable.Builder<V>(this.caseSensitive, this.contents);
        }

        public Index<V> build() {
            if (this.contents == null) {
                return EmptyTrie.instance(this.caseSensitive);
            }
            int capacity = AbstractTrie.requiredCapacity(this.contents.keySet(), this.caseSensitive);
            AbstractTrie trie = ArrayTrie.from(capacity, this.caseSensitive, this.contents);
            if (trie != null) {
                return trie;
            }
            trie = TreeTrie.from(this.caseSensitive, this.contents);
            if (trie != null) {
                return trie;
            }
            throw new IllegalStateException("No suitable Trie implementation : " + this);
        }

        public String toString() {
            return String.format("%s{c=%d,cs=%b}", super.toString(), this.contents == null ? 0 : this.contents.size(), this.caseSensitive);
        }
    }

    public static interface Mutable<V>
    extends Index<V> {
        public boolean put(String var1, V var2);

        public boolean put(V var1);

        public V remove(String var1);

        public void clear();

        public static class Builder<V>
        extends org.eclipse.jetty.util.Index$Builder<V> {
            private int maxCapacity = -1;

            Builder(boolean caseSensitive, Map<String, V> contents) {
                super(caseSensitive, contents);
            }

            public Builder<V> maxCapacity(int capacity) {
                this.maxCapacity = capacity;
                return this;
            }

            @Override
            public Builder<V> mutable() {
                return this;
            }

            @Override
            public Mutable<V> build() {
                int capacity;
                if (this.maxCapacity == 0) {
                    return EmptyTrie.instance(this.caseSensitive);
                }
                int n = capacity = this.contents == null ? 0 : AbstractTrie.requiredCapacity(this.contents.keySet(), this.caseSensitive);
                if (this.maxCapacity >= 0 && capacity > this.maxCapacity) {
                    throw new IllegalStateException("Insufficient maxCapacity for contents");
                }
                AbstractTrie trie = ArrayTrie.from(this.maxCapacity, this.caseSensitive, this.contents);
                if (trie != null) {
                    return trie;
                }
                trie = TreeTrie.from(this.caseSensitive, this.contents);
                if (trie != null) {
                    return trie;
                }
                throw new IllegalStateException("No suitable Trie implementation: " + this);
            }
        }
    }
}

