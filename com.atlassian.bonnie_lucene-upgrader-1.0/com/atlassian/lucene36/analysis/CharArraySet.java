/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.lucene36.analysis;

import com.atlassian.lucene36.analysis.CharArrayMap;
import com.atlassian.lucene36.util.Version;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class CharArraySet
extends AbstractSet<Object> {
    public static final CharArraySet EMPTY_SET = new CharArraySet(CharArrayMap.<Object>emptyMap());
    private static final Object PLACEHOLDER = new Object();
    private final CharArrayMap<Object> map;

    public CharArraySet(Version matchVersion, int startSize, boolean ignoreCase) {
        this(new CharArrayMap<Object>(matchVersion, startSize, ignoreCase));
    }

    public CharArraySet(Version matchVersion, Collection<?> c, boolean ignoreCase) {
        this(matchVersion, c.size(), ignoreCase);
        this.addAll(c);
    }

    @Deprecated
    public CharArraySet(int startSize, boolean ignoreCase) {
        this(Version.LUCENE_30, startSize, ignoreCase);
    }

    @Deprecated
    public CharArraySet(Collection<?> c, boolean ignoreCase) {
        this(Version.LUCENE_30, c.size(), ignoreCase);
        this.addAll(c);
    }

    CharArraySet(CharArrayMap<Object> map) {
        this.map = map;
    }

    @Override
    public void clear() {
        this.map.clear();
    }

    public boolean contains(char[] text, int off, int len) {
        return this.map.containsKey(text, off, len);
    }

    public boolean contains(CharSequence cs) {
        return this.map.containsKey(cs);
    }

    @Override
    public boolean contains(Object o) {
        return this.map.containsKey(o);
    }

    @Override
    public boolean add(Object o) {
        return this.map.put(o, PLACEHOLDER) == null;
    }

    @Override
    public boolean add(CharSequence text) {
        return this.map.put(text, PLACEHOLDER) == null;
    }

    @Override
    public boolean add(String text) {
        return this.map.put(text, PLACEHOLDER) == null;
    }

    @Override
    public boolean add(char[] text) {
        return this.map.put(text, PLACEHOLDER) == null;
    }

    @Override
    public int size() {
        return this.map.size();
    }

    public static CharArraySet unmodifiableSet(CharArraySet set) {
        if (set == null) {
            throw new NullPointerException("Given set is null");
        }
        if (set == EMPTY_SET) {
            return EMPTY_SET;
        }
        if (set.map instanceof CharArrayMap.UnmodifiableCharArrayMap) {
            return set;
        }
        return new CharArraySet(CharArrayMap.unmodifiableMap(set.map));
    }

    @Deprecated
    public static CharArraySet copy(Set<?> set) {
        if (set == EMPTY_SET) {
            return EMPTY_SET;
        }
        return CharArraySet.copy(Version.LUCENE_30, set);
    }

    public static CharArraySet copy(Version matchVersion, Set<?> set) {
        if (set == EMPTY_SET) {
            return EMPTY_SET;
        }
        if (set instanceof CharArraySet) {
            CharArraySet source = (CharArraySet)set;
            return new CharArraySet(CharArrayMap.copy(source.map.matchVersion, source.map));
        }
        return new CharArraySet(matchVersion, set, false);
    }

    @Deprecated
    public Iterator<String> stringIterator() {
        return new CharArraySetIterator();
    }

    @Override
    public Iterator<Object> iterator() {
        return this.map.matchVersion.onOrAfter(Version.LUCENE_31) ? this.map.originalKeySet().iterator() : this.stringIterator();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (Object item : this) {
            if (sb.length() > 1) {
                sb.append(", ");
            }
            if (item instanceof char[]) {
                sb.append((char[])item);
                continue;
            }
            sb.append(item);
        }
        return sb.append(']').toString();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @Deprecated
    public class CharArraySetIterator
    implements Iterator<String> {
        int pos = -1;
        char[] next;

        private CharArraySetIterator() {
            this.goNext();
        }

        private void goNext() {
            this.next = null;
            ++this.pos;
            while (this.pos < ((CharArraySet)CharArraySet.this).map.keys.length && (this.next = ((CharArraySet)CharArraySet.this).map.keys[this.pos]) == null) {
                ++this.pos;
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        public char[] nextCharArray() {
            char[] ret = this.next;
            this.goNext();
            return ret;
        }

        @Override
        public String next() {
            return new String(this.nextCharArray());
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

