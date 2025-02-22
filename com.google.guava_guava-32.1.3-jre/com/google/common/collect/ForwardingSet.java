/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ForwardingCollection;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public abstract class ForwardingSet<E>
extends ForwardingCollection<E>
implements Set<E> {
    protected ForwardingSet() {
    }

    @Override
    protected abstract Set<E> delegate();

    @Override
    public boolean equals(@CheckForNull Object object) {
        return object == this || this.delegate().equals(object);
    }

    @Override
    public int hashCode() {
        return this.delegate().hashCode();
    }

    @Override
    protected boolean standardRemoveAll(Collection<?> collection) {
        return Sets.removeAllImpl(this, Preconditions.checkNotNull(collection));
    }

    protected boolean standardEquals(@CheckForNull Object object) {
        return Sets.equalsImpl(this, object);
    }

    protected int standardHashCode() {
        return Sets.hashCodeImpl(this);
    }
}

