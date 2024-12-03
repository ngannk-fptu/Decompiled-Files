/*
 * Decompiled with CFR 0.152.
 */
package com.google.inject.internal.util;

import com.google.inject.internal.util.$Nullable;
import com.google.inject.internal.util.$Preconditions;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.AbstractSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class $Sets {
    private $Sets() {
    }

    public static <E> HashSet<E> newHashSet() {
        return new HashSet();
    }

    public static <E> LinkedHashSet<E> newLinkedHashSet() {
        return new LinkedHashSet();
    }

    public static <E> Set<E> newSetFromMap(Map<E, Boolean> map) {
        return new SetFromMap<E>(map);
    }

    static int hashCodeImpl(Set<?> s) {
        int hashCode = 0;
        for (Object o : s) {
            hashCode += o != null ? o.hashCode() : 0;
        }
        return hashCode;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    private static class SetFromMap<E>
    extends AbstractSet<E>
    implements Set<E>,
    Serializable {
        private final Map<E, Boolean> m;
        private transient Set<E> s;
        static final long serialVersionUID = 0L;

        SetFromMap(Map<E, Boolean> map) {
            $Preconditions.checkArgument(map.isEmpty(), "Map is non-empty");
            this.m = map;
            this.s = map.keySet();
        }

        @Override
        public void clear() {
            this.m.clear();
        }

        @Override
        public int size() {
            return this.m.size();
        }

        @Override
        public boolean isEmpty() {
            return this.m.isEmpty();
        }

        @Override
        public boolean contains(Object o) {
            return this.m.containsKey(o);
        }

        @Override
        public boolean remove(Object o) {
            return this.m.remove(o) != null;
        }

        @Override
        public boolean add(E e) {
            return this.m.put(e, Boolean.TRUE) == null;
        }

        @Override
        public Iterator<E> iterator() {
            return this.s.iterator();
        }

        @Override
        public Object[] toArray() {
            return this.s.toArray();
        }

        @Override
        public <T> T[] toArray(T[] a) {
            return this.s.toArray(a);
        }

        @Override
        public String toString() {
            return this.s.toString();
        }

        @Override
        public int hashCode() {
            return ((Object)this.s).hashCode();
        }

        @Override
        public boolean equals(@$Nullable Object object) {
            return this == object || ((Object)this.s).equals(object);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return this.s.containsAll(c);
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            return this.s.removeAll(c);
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            return this.s.retainAll(c);
        }

        private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
            stream.defaultReadObject();
            this.s = this.m.keySet();
        }
    }
}

