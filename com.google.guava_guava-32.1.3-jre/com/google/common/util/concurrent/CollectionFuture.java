/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.google.common.util.concurrent;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.Lists;
import com.google.common.util.concurrent.AggregateFuture;
import com.google.common.util.concurrent.ElementTypesAreNonnullByDefault;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ParametricNullness;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.CheckForNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@ElementTypesAreNonnullByDefault
@GwtCompatible(emulated=true)
abstract class CollectionFuture<V, C>
extends AggregateFuture<V, C> {
    @CheckForNull
    private List<@Nullable Present<V>> values;

    CollectionFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
        super(futures, allMustSucceed, true);
        List<@Nullable T> values = futures.isEmpty() ? Collections.emptyList() : Lists.newArrayListWithCapacity(futures.size());
        for (int i = 0; i < futures.size(); ++i) {
            values.add(null);
        }
        this.values = values;
    }

    @Override
    final void collectOneValue(int index, @ParametricNullness V returnValue) {
        List<@Nullable Present<V>> localValues = this.values;
        if (localValues != null) {
            localValues.set(index, new Present<V>(returnValue));
        }
    }

    @Override
    final void handleAllCompleted() {
        List<@Nullable Present<V>> localValues = this.values;
        if (localValues != null) {
            this.set(this.combine(localValues));
        }
    }

    @Override
    void releaseResources(AggregateFuture.ReleaseResourcesReason reason) {
        super.releaseResources(reason);
        this.values = null;
    }

    abstract C combine(List<@Nullable Present<V>> var1);

    private static final class Present<V> {
        @ParametricNullness
        final V value;

        Present(@ParametricNullness V value) {
            this.value = value;
        }
    }

    static final class ListFuture<V>
    extends CollectionFuture<V, List<V>> {
        ListFuture(ImmutableCollection<? extends ListenableFuture<? extends V>> futures, boolean allMustSucceed) {
            super(futures, allMustSucceed);
            this.init();
        }

        @Override
        public List<@Nullable V> combine(List<@Nullable Present<V>> values) {
            ArrayList<@Nullable Object> result = Lists.newArrayListWithCapacity(values.size());
            for (Present<V> element : values) {
                result.add(element != null ? (Object)element.value : null);
            }
            return Collections.unmodifiableList(result);
        }
    }
}

