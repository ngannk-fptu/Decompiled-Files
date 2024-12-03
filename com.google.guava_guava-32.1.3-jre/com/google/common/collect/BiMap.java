/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.errorprone.annotations.CanIgnoreReturnValue
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtCompatible;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ParametricNullness;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import java.util.Map;
import java.util.Set;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtCompatible
public interface BiMap<K, V>
extends Map<K, V> {
    @Override
    @CheckForNull
    @CanIgnoreReturnValue
    public V put(@ParametricNullness K var1, @ParametricNullness V var2);

    @CheckForNull
    @CanIgnoreReturnValue
    public V forcePut(@ParametricNullness K var1, @ParametricNullness V var2);

    @Override
    public void putAll(Map<? extends K, ? extends V> var1);

    @Override
    public Set<V> values();

    public BiMap<V, K> inverse();
}

