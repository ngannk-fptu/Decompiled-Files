/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import java.util.AbstractSet;

public abstract class _UnmodifiableSet<E>
extends AbstractSet<E> {
    @Override
    public boolean add(E o) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
        if (this.contains(o)) {
            throw new UnsupportedOperationException();
        }
        return false;
    }

    @Override
    public void clear() {
        if (!this.isEmpty()) {
            throw new UnsupportedOperationException();
        }
    }
}

