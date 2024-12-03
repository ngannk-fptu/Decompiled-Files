/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.util.StreamUtils;

@Deprecated
public class CollectionUtils {
    @Deprecated
    public static final List EMPTY_LIST = Collections.EMPTY_LIST;

    @Deprecated
    public static boolean equals(Vector<?> v1, Vector<?> v2) {
        return Objects.equals(v1, v2);
    }

    @Deprecated
    public static boolean equals(Dictionary<?, ?> d1, Dictionary<?, ?> d2) {
        if (d1 == d2) {
            return true;
        }
        if (d1 == null || d2 == null) {
            return false;
        }
        if (d1.size() != d2.size()) {
            return false;
        }
        return StreamUtils.enumerationAsStream(d1.keys()).allMatch(key -> d1.get(key).equals(d2.get(key)));
    }

    @Deprecated
    public static String flattenToString(Collection<?> c) {
        return c.stream().map(String::valueOf).collect(Collectors.joining(","));
    }

    @Deprecated
    public static <K, V> void putAll(Dictionary<? super K, ? super V> m1, Dictionary<? extends K, ? extends V> m2) {
        StreamUtils.enumerationAsStream(m2.keys()).forEach(key -> m1.put((Object)key, (Object)m2.get(key)));
    }

    @Deprecated
    public static <E> Enumeration<E> append(Enumeration<E> e1, Enumeration<E> e2) {
        return new CompoundEnumeration<E>(e1, e2);
    }

    @Deprecated
    public static <E> Enumeration<E> asEnumeration(final Iterator<E> iter) {
        return new Enumeration<E>(){

            @Override
            public boolean hasMoreElements() {
                return iter.hasNext();
            }

            @Override
            public E nextElement() {
                return iter.next();
            }
        };
    }

    @Deprecated
    public static <E> Iterator<E> asIterator(final Enumeration<E> e) {
        return new Iterator<E>(){

            @Override
            public boolean hasNext() {
                return e.hasMoreElements();
            }

            @Override
            public E next() {
                return e.nextElement();
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Deprecated
    public static <T> Collection<T> asCollection(Iterator<? extends T> iter) {
        ArrayList l = new ArrayList();
        iter.forEachRemaining(l::add);
        return l;
    }

    @Deprecated
    public static int frequency(Collection<?> c, Object o) {
        return c == null ? 0 : Collections.frequency(c, o);
    }

    private CollectionUtils() {
    }

    private static final class CompoundEnumeration<E>
    implements Enumeration<E> {
        private final Enumeration<E> e1;
        private final Enumeration<E> e2;

        public CompoundEnumeration(Enumeration<E> e1, Enumeration<E> e2) {
            this.e1 = e1;
            this.e2 = e2;
        }

        @Override
        public boolean hasMoreElements() {
            return this.e1.hasMoreElements() || this.e2.hasMoreElements();
        }

        @Override
        public E nextElement() throws NoSuchElementException {
            if (this.e1.hasMoreElements()) {
                return this.e1.nextElement();
            }
            return this.e2.nextElement();
        }
    }

    @Deprecated
    public static final class EmptyEnumeration<E>
    implements Enumeration<E> {
        @Override
        public boolean hasMoreElements() {
            return false;
        }

        @Override
        public E nextElement() throws NoSuchElementException {
            throw new NoSuchElementException();
        }
    }
}

