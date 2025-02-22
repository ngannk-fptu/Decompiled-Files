/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.IndexedImmutableSet;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true)
final class JdkBackedImmutableSet<E>
extends IndexedImmutableSet<E> {
    private final Set<?> delegate;
    private final ImmutableList<E> delegateList;

    JdkBackedImmutableSet(Set<?> delegate, ImmutableList<E> delegateList) {
        this.delegate = delegate;
        this.delegateList = delegateList;
    }

    @Override
    E get(int index) {
        return this.delegateList.get(index);
    }

    @Override
    public boolean contains(@CheckForNull Object object) {
        return this.delegate.contains(object);
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public int size() {
        return this.delegateList.size();
    }
}

