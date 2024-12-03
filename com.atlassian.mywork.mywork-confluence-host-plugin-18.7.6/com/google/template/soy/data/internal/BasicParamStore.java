/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 */
package com.google.template.soy.data.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.ParamStore;
import java.util.Map;
import javax.annotation.Nonnull;

public class BasicParamStore
extends ParamStore {
    private final Map<String, SoyValueProvider> localStore = Maps.newHashMap();

    @Override
    public void setField(String name, @Nonnull SoyValueProvider valueProvider) {
        Preconditions.checkNotNull((Object)valueProvider);
        this.localStore.put(name, valueProvider);
    }

    @Override
    public boolean hasField(String name) {
        return this.localStore.containsKey(name);
    }

    @Override
    public SoyValueProvider getFieldProvider(String name) {
        return this.localStore.get(name);
    }
}

