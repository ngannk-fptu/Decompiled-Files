/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  com.google.errorprone.annotations.CompatibleWith
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Preconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Multisets;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.errorprone.annotations.CompatibleWith;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;
import java.util.function.ObjIntConsumer;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface Multiset<E>
extends Collection<E> {
    @Override
    public int size();

    public int count(@CheckForNull @CompatibleWith(value="E") Object var1);

    @CanIgnoreReturnValue
    public int add(@ParametricNullness E var1, int var2);

    @Override
    @CanIgnoreReturnValue
    public boolean add(@ParametricNullness E var1);

    @CanIgnoreReturnValue
    public int remove(@CheckForNull @CompatibleWith(value="E") Object var1, int var2);

    @Override
    @CanIgnoreReturnValue
    public boolean remove(@CheckForNull Object var1);

    @CanIgnoreReturnValue
    public int setCount(@ParametricNullness E var1, int var2);

    @CanIgnoreReturnValue
    public boolean setCount(@ParametricNullness E var1, int var2, int var3);

    public Set<E> elementSet();

    public Set<Entry<E>> entrySet();

    default public void forEachEntry(ObjIntConsumer<? super E> action) {
        Preconditions.checkNotNull(action);
        this.entrySet().forEach((? super T entry) -> action.accept((Object)entry.getElement(), entry.getCount()));
    }

    @Override
    public boolean equals(@CheckForNull Object var1);

    @Override
    public int hashCode();

    public String toString();

    @Override
    public Iterator<E> iterator();

    @Override
    public boolean contains(@CheckForNull Object var1);

    @Override
    public boolean containsAll(Collection<?> var1);

    @Override
    @CanIgnoreReturnValue
    public boolean removeAll(Collection<?> var1);

    @Override
    @CanIgnoreReturnValue
    public boolean retainAll(Collection<?> var1);

    @Override
    default public void forEach(Consumer<? super E> action) {
        Preconditions.checkNotNull(action);
        this.entrySet().forEach((? super T entry) -> {
            Object elem = entry.getElement();
            int count = entry.getCount();
            for (int i = 0; i < count; ++i) {
                action.accept((Object)elem);
            }
        });
    }

    @Override
    default public Spliterator<E> spliterator() {
        return Multisets.spliteratorImpl(this);
    }

    public static interface Entry<E> {
        @ParametricNullness
        public E getElement();

        public int getCount();

        public boolean equals(@CheckForNull Object var1);

        public int hashCode();

        public String toString();
    }
}

