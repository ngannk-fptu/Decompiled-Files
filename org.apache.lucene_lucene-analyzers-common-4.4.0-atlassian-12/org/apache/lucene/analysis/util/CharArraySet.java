/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import org.apache.lucene.analysis.util.CharArrayMap;
import org.apache.lucene.util.Version;

public class CharArraySet
extends AbstractSet<Object> {
    public static final CharArraySet EMPTY_SET = new CharArraySet(CharArrayMap.emptyMap());
    private static final Object PLACEHOLDER = new Object();
    private final CharArrayMap<Object> map;

    public CharArraySet(Version matchVersion, int startSize, boolean ignoreCase) {
        this(new CharArrayMap<Object>(matchVersion, startSize, ignoreCase));
    }

    public CharArraySet(Version matchVersion, Collection<?> c, boolean ignoreCase) {
        this(matchVersion, c.size(), ignoreCase);
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

    @Override
    public Iterator<Object> iterator() {
        return this.map.originalKeySet().iterator();
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
}

