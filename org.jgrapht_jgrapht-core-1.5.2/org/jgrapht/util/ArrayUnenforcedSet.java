/*
 * Decompiled with CFR 0.152.
 */
package org.jgrapht.util;

import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class ArrayUnenforcedSet<E>
extends ArrayList<E>
implements Set<E> {
    private static final long serialVersionUID = -7413250161201811238L;

    public ArrayUnenforcedSet() {
    }

    public ArrayUnenforcedSet(Collection<? extends E> c) {
        super(c);
    }

    public ArrayUnenforcedSet(int n) {
        super(n);
    }

    @Override
    public boolean equals(Object o) {
        return new SetForEquality().equals(o);
    }

    @Override
    public int hashCode() {
        return new SetForEquality().hashCode();
    }

    private class SetForEquality
    extends AbstractSet<E> {
        private SetForEquality() {
        }

        @Override
        public Iterator<E> iterator() {
            return ArrayUnenforcedSet.this.iterator();
        }

        @Override
        public int size() {
            return ArrayUnenforcedSet.this.size();
        }
    }
}

