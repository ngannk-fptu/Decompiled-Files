/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 */
package com.google.template.soy.data.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyRecord;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.ParamStore;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AugmentedParamStore
extends ParamStore {
    private final SoyRecord backingStore;
    private final Map<String, SoyValueProvider> localStore;

    public AugmentedParamStore(@Nullable SoyRecord backingStore) {
        this.backingStore = backingStore;
        this.localStore = Maps.newHashMap();
    }

    @Override
    public void setField(String name, @Nonnull SoyValueProvider valueProvider) {
        Preconditions.checkNotNull((Object)valueProvider);
        this.localStore.put(name, valueProvider);
    }

    @Override
    public boolean hasField(String name) {
        return this.localStore.containsKey(name) || this.backingStore.hasField(name);
    }

    @Override
    public SoyValueProvider getFieldProvider(String name) {
        SoyValueProvider val = this.localStore.get(name);
        return val != null ? val : this.backingStore.getField(name);
    }
}

