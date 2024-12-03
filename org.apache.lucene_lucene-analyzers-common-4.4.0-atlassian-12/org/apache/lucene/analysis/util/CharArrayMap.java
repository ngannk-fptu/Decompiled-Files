/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.CharacterUtils;
import org.apache.lucene.util.Version;

public class CharArrayMap<V>
extends AbstractMap<Object, V> {
    private static final CharArrayMap<?> EMPTY_MAP = new EmptyCharArrayMap();
    private static final int INIT_SIZE = 8;
    private final CharacterUtils charUtils;
    private boolean ignoreCase;
    private int count;
    final Version matchVersion;
    char[][] keys;
    V[] values;
    private EntrySet entrySet = null;
    private CharArraySet keySet = null;

    public CharArrayMap(Version matchVersion, int startSize, boolean ignoreCase) {
        int size;
        this.ignoreCase = ignoreCase;
        for (size = 8; startSize + (startSize >> 2) > size; size <<= 1) {
        }
        this.keys = new char[size][];
        this.values = new Object[size];
        this.charUtils = CharacterUtils.getInstance(matchVersion);
        this.matchVersion = matchVersion;
    }

    public CharArrayMap(Version matchVersion, Map<?, ? extends V> c, boolean ignoreCase) {
        this(matchVersion, c.size(), ignoreCase);
        this.putAll(c);
    }

    private CharArrayMap(CharArrayMap<V> toCopy) {
        this.keys = toCopy.keys;
        this.values = toCopy.values;
        this.ignoreCase = toCopy.ignoreCase;
        this.count = toCopy.count;
        this.charUtils = toCopy.charUtils;
        this.matchVersion = toCopy.matchVersion;
    }

    @Override
    public void clear() {
        this.count = 0;
        Arrays.fill((Object[])this.keys, null);
        Arrays.fill(this.values, null);
    }

    public boolean containsKey(char[] text, int off, int len) {
        return this.keys[this.getSlot(text, off, len)] != null;
    }

    public boolean containsKey(CharSequence cs) {
        return this.keys[this.getSlot(cs)] != null;
    }

    @Override
    public boolean containsKey(Object o) {
        if (o instanceof char[]) {
            char[] text = (char[])o;
            return this.containsKey(text, 0, text.length);
        }
        return this.containsKey(o.toString());
    }

    public V get(char[] text, int off, int len) {
        return this.values[this.getSlot(text, off, len)];
    }

    public V get(CharSequence cs) {
        return this.values[this.getSlot(cs)];
    }

    @Override
    public V get(Object o) {
        if (o instanceof char[]) {
            char[] text = (char[])o;
            return this.get(text, 0, text.length);
        }
        return this.get(o.toString());
    }

    private int getSlot(char[] text, int off, int len) {
        int code = this.getHashCode(text, off, len);
        int pos = code & this.keys.length - 1;
        char[] text2 = this.keys[pos];
        if (text2 != null && !this.equals(text, off, len, text2)) {
            int inc = (code >> 8) + code | 1;
            while ((text2 = this.keys[pos = (code += inc) & this.keys.length - 1]) != null && !this.equals(text, off, len, text2)) {
            }
        }
        return pos;
    }

    private int getSlot(CharSequence text) {
        int code = this.getHashCode(text);
        int pos = code & this.keys.length - 1;
        char[] text2 = this.keys[pos];
        if (text2 != null && !this.equals(text, text2)) {
            int inc = (code >> 8) + code | 1;
            while ((text2 = this.keys[pos = (code += inc) & this.keys.length - 1]) != null && !this.equals(text, text2)) {
            }
        }
        return pos;
    }

    @Override
    public V put(CharSequence text, V value) {
        return this.put(text.toString(), value);
    }

    @Override
    public V put(Object o, V value) {
        if (o instanceof char[]) {
            return this.put((char[])o, value);
        }
        return this.put(o.toString(), value);
    }

    @Override
    public V put(String text, V value) {
        return this.put(text.toCharArray(), value);
    }

    @Override
    public V put(char[] text, V value) {
        int slot;
        if (this.ignoreCase) {
            this.charUtils.toLowerCase(text, 0, text.length);
        }
        if (this.keys[slot = this.getSlot(text, 0, text.length)] != null) {
            V oldValue = this.values[slot];
            this.values[slot] = value;
            return oldValue;
        }
        this.keys[slot] = text;
        this.values[slot] = value;
        ++this.count;
        if (this.count + (this.count >> 2) > this.keys.length) {
            this.rehash();
        }
        return null;
    }

    private void rehash() {
        assert (this.keys.length == this.values.length);
        int newSize = 2 * this.keys.length;
        char[][] oldkeys = this.keys;
        V[] oldvalues = this.values;
        this.keys = new char[newSize][];
        this.values = new Object[newSize];
        for (int i = 0; i < oldkeys.length; ++i) {
            char[] text = oldkeys[i];
            if (text == null) continue;
            int slot = this.getSlot(text, 0, text.length);
            this.keys[slot] = text;
            this.values[slot] = oldvalues[i];
        }
    }

    private boolean equals(char[] text1, int off, int len, char[] text2) {
        if (len != text2.length) {
            return false;
        }
        int limit = off + len;
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = 0; i < len; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text1, off + i, limit);
                if (Character.toLowerCase(codePointAt) == this.charUtils.codePointAt(text2, i, text2.length)) continue;
                return false;
            }
        } else {
            for (int i = 0; i < len; ++i) {
                if (text1[off + i] == text2[i]) continue;
                return false;
            }
        }
        return true;
    }

    private boolean equals(CharSequence text1, char[] text2) {
        int len = text1.length();
        if (len != text2.length) {
            return false;
        }
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = 0; i < len; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text1, i);
                if (Character.toLowerCase(codePointAt) == this.charUtils.codePointAt(text2, i, text2.length)) continue;
                return false;
            }
        } else {
            for (int i = 0; i < len; ++i) {
                if (text1.charAt(i) == text2[i]) continue;
                return false;
            }
        }
        return true;
    }

    private int getHashCode(char[] text, int offset, int len) {
        if (text == null) {
            throw new NullPointerException();
        }
        int code = 0;
        int stop = offset + len;
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = offset; i < stop; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text, i, stop);
                code = code * 31 + Character.toLowerCase(codePointAt);
            }
        } else {
            for (int i = offset; i < stop; ++i) {
                code = code * 31 + text[i];
            }
        }
        return code;
    }

    private int getHashCode(CharSequence text) {
        if (text == null) {
            throw new NullPointerException();
        }
        int code = 0;
        int len = text.length();
        if (this.ignoreCase) {
            int codePointAt;
            for (int i = 0; i < len; i += Character.charCount(codePointAt)) {
                codePointAt = this.charUtils.codePointAt(text, i);
                code = code * 31 + Character.toLowerCase(codePointAt);
            }
        } else {
            for (int i = 0; i < len; ++i) {
                code = code * 31 + text.charAt(i);
            }
        }
        return code;
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        for (Map.Entry entry : this.entrySet()) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(entry);
        }
        return sb.append('}').toString();
    }

    EntrySet createEntrySet() {
        return new EntrySet(true);
    }

    public final EntrySet entrySet() {
        if (this.entrySet == null) {
            this.entrySet = this.createEntrySet();
        }
        return this.entrySet;
    }

    final Set<Object> originalKeySet() {
        return super.keySet();
    }

    public final CharArraySet keySet() {
        if (this.keySet == null) {
            this.keySet = new CharArraySet(this){

                @Override
                public boolean add(Object o) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean add(CharSequence text) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean add(String text) {
                    throw new UnsupportedOperationException();
                }

                @Override
                public boolean add(char[] text) {
                    throw new UnsupportedOperationException();
                }
            };
        }
        return this.keySet;
    }

    public static <V> CharArrayMap<V> unmodifiableMap(CharArrayMap<V> map) {
        if (map == null) {
            throw new NullPointerException("Given map is null");
        }
        if (map == CharArrayMap.emptyMap() || map.isEmpty()) {
            return CharArrayMap.emptyMap();
        }
        if (map instanceof UnmodifiableCharArrayMap) {
            return map;
        }
        return new UnmodifiableCharArrayMap<V>(map);
    }

    public static <V> CharArrayMap<V> copy(Version matchVersion, Map<?, ? extends V> map) {
        if (map == EMPTY_MAP) {
            return CharArrayMap.emptyMap();
        }
        if (map instanceof CharArrayMap) {
            CharArrayMap<V> m = (CharArrayMap<V>)map;
            char[][] keys = new char[m.keys.length][];
            System.arraycopy(m.keys, 0, keys, 0, keys.length);
            Object[] values = new Object[m.values.length];
            System.arraycopy(m.values, 0, values, 0, values.length);
            m = new CharArrayMap<V>(m);
            m.keys = keys;
            m.values = values;
            return m;
        }
        return new CharArrayMap<V>(matchVersion, map, false);
    }

    public static <V> CharArrayMap<V> emptyMap() {
        return EMPTY_MAP;
    }

    private static final class EmptyCharArrayMap<V>
    extends UnmodifiableCharArrayMap<V> {
        EmptyCharArrayMap() {
            super(new CharArrayMap(Version.LUCENE_CURRENT, 0, false));
        }

        @Override
        public boolean containsKey(char[] text, int off, int len) {
            if (text == null) {
                throw new NullPointerException();
            }
            return false;
        }

        @Override
        public boolean containsKey(CharSequence cs) {
            if (cs == null) {
                throw new NullPointerException();
            }
            return false;
        }

        @Override
        public boolean containsKey(Object o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return false;
        }

        @Override
        public V get(char[] text, int off, int len) {
            if (text == null) {
                throw new NullPointerException();
            }
            return null;
        }

        @Override
        public V get(CharSequence cs) {
            if (cs == null) {
                throw new NullPointerException();
            }
            return null;
        }

        @Override
        public V get(Object o) {
            if (o == null) {
                throw new NullPointerException();
            }
            return null;
        }
    }

    static class UnmodifiableCharArrayMap<V>
    extends CharArrayMap<V> {
        UnmodifiableCharArrayMap(CharArrayMap<V> map) {
            super(map);
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public V put(Object o, V val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V put(char[] text, V val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V put(CharSequence text, V val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V put(String text, V val) {
            throw new UnsupportedOperationException();
        }

        @Override
        public V remove(Object key) {
            throw new UnsupportedOperationException();
        }

        @Override
        EntrySet createEntrySet() {
            return new EntrySet(false);
        }
    }

    public final class EntrySet
    extends AbstractSet<Map.Entry<Object, V>> {
        private final boolean allowModify;

        private EntrySet(boolean allowModify) {
            this.allowModify = allowModify;
        }

        public EntryIterator iterator() {
            return new EntryIterator(this.allowModify);
        }

        @Override
        public boolean contains(Object o) {
            if (!(o instanceof Map.Entry)) {
                return false;
            }
            Map.Entry e = (Map.Entry)o;
            Object key = e.getKey();
            Object val = e.getValue();
            Object v = CharArrayMap.this.get(key);
            return v == null ? val == null : v.equals(val);
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public int size() {
            return CharArrayMap.this.count;
        }

        @Override
        public void clear() {
            if (!this.allowModify) {
                throw new UnsupportedOperationException();
            }
            CharArrayMap.this.clear();
        }
    }

    private final class MapEntry
    implements Map.Entry<Object, V> {
        private final int pos;
        private final boolean allowModify;

        private MapEntry(int pos, boolean allowModify) {
            this.pos = pos;
            this.allowModify = allowModify;
        }

        @Override
        public Object getKey() {
            return CharArrayMap.this.keys[this.pos].clone();
        }

        @Override
        public V getValue() {
            return CharArrayMap.this.values[this.pos];
        }

        @Override
        public V setValue(V value) {
            if (!this.allowModify) {
                throw new UnsupportedOperationException();
            }
            Object old = CharArrayMap.this.values[this.pos];
            CharArrayMap.this.values[this.pos] = value;
            return old;
        }

        public String toString() {
            return CharArrayMap.this.keys[this.pos] + '=' + (CharArrayMap.this.values[this.pos] == CharArrayMap.this ? "(this Map)" : CharArrayMap.this.values[this.pos]);
        }
    }

    public class EntryIterator
    implements Iterator<Map.Entry<Object, V>> {
        private int pos = -1;
        private int lastPos;
        private final boolean allowModify;

        private EntryIterator(boolean allowModify) {
            this.allowModify = allowModify;
            this.goNext();
        }

        private void goNext() {
            this.lastPos = this.pos++;
            while (this.pos < CharArrayMap.this.keys.length && CharArrayMap.this.keys[this.pos] == null) {
                ++this.pos;
            }
        }

        @Override
        public boolean hasNext() {
            return this.pos < CharArrayMap.this.keys.length;
        }

        public char[] nextKey() {
            this.goNext();
            return CharArrayMap.this.keys[this.lastPos];
        }

        public String nextKeyString() {
            return new String(this.nextKey());
        }

        public V currentValue() {
            return CharArrayMap.this.values[this.lastPos];
        }

        public V setValue(V value) {
            if (!this.allowModify) {
                throw new UnsupportedOperationException();
            }
            Object old = CharArrayMap.this.values[this.lastPos];
            CharArrayMap.this.values[this.lastPos] = value;
            return old;
        }

        @Override
        public Map.Entry<Object, V> next() {
            this.goNext();
            return new MapEntry(this.lastPos, this.allowModify);
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

