/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.base.Predicate;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.Multimap;
import java.util.Map;

@ElementTypesAreNonnullByDefault
@GwtCompatible
interface FilteredMultimap<K, V>
extends Multimap<K, V> {
    public Multimap<K, V> unfiltered();

    public Predicate<? super Map.Entry<K, V>> entryPredicate();
}

