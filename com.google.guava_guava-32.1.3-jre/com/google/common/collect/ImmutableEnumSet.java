/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.concurrent.LazyInit
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.annotations.J2ktIncompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.UnmodifiableIterator;
import com.google.errorprone.annotations.concurrent.LazyInit;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Spliterator;
import java.util.function.Consumer;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible(serializable=true, emulated=true)
final class ImmutableEnumSet<E extends Enum<E>>
extends ImmutableSet<E> {
    private final transient EnumSet<E> delegate;
    @LazyInit
    private transient int hashCode;

    static <E extends Enum<E>> ImmutableSet<E> asImmutable(EnumSet<E> set) {
        switch (set.size()) {
            case 0: {
                return ImmutableSet.of();
            }
            case 1: {
                return ImmutableSet.of((Enum)Iterables.getOnlyElement(set));
            }
        }
        return new ImmutableEnumSet<E>(set);
    }

    private ImmutableEnumSet(EnumSet<E> delegate) {
        this.delegate = delegate;
    }

    @Override
    boolean isPartialView() {
        return false;
    }

    @Override
    public UnmodifiableIterator<E> iterator() {
        return Iterators.unmodifiableIterator(this.delegate.iterator());
    }

    @Override
    public Spliterator<E> spliterator() {
        return this.delegate.spliterator();
    }

    @Override
    public void forEach(Consumer<? super E> action) {
        this.delegate.forEach(action);
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public boolean contains(@CheckForNull Object object) {
        return this.delegate.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        if (collection instanceof ImmutableEnumSet) {
            collection = ((ImmutableEnumSet)collection).delegate;
        }
        return this.delegate.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return this.delegate.isEmpty();
    }

    @Override
    public boolean equals(@CheckForNull Object object) {
        if (object == this) {
            return true;
        }
        if (object instanceof ImmutableEnumSet) {
            object = ((ImmutableEnumSet)((Object)object)).delegate;
        }
        return this.delegate.equals(object);
    }

    @Override
    boolean isHashCodeFast() {
        return true;
    }

    @Override
    public int hashCode() {
        int result = this.hashCode;
        return result == 0 ? (this.hashCode = this.delegate.hashCode()) : result;
    }

    @Override
    public String toString() {
        return this.delegate.toString();
    }

    @Override
    @J2ktIncompatible
    Object writeReplace() {
        return new EnumSerializedForm<E>(this.delegate);
    }

    @J2ktIncompatible
    private void readObject(ObjectInputStream stream) throws InvalidObjectException {
        throw new InvalidObjectException("Use SerializedForm");
    }

    @J2ktIncompatible
    private static class EnumSerializedForm<E extends Enum<E>>
    implements Serializable {
        final EnumSet<E> delegate;
        private static final long serialVersionUID = 0L;

        EnumSerializedForm(EnumSet<E> delegate) {
            this.delegate = delegate;
        }

        Object readResolve() {
            return new ImmutableEnumSet((EnumSet)this.delegate.clone());
        }
    }
}

