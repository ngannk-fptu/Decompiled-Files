/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  javax.annotation.Nonnull
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data.internal;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyAbstractMap;
import com.google.template.soy.data.SoyDataException;
import com.google.template.soy.data.SoyDict;
import com.google.template.soy.data.SoyValue;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.Transforms;
import com.google.template.soy.data.restricted.StringData;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
abstract class AbstractDict
extends SoyAbstractMap
implements SoyDict {
    protected final Map<String, ? extends SoyValueProvider> providerMap;

    AbstractDict(Map<String, ? extends SoyValueProvider> providerMap) {
        this.providerMap = providerMap;
    }

    @Override
    public final boolean hasField(String name) {
        return this.providerMap.containsKey(name);
    }

    @Override
    public final SoyValue getField(String name) {
        SoyValueProvider provider = this.providerMap.get(name);
        return provider != null ? provider.resolve() : null;
    }

    @Override
    public final SoyValueProvider getFieldProvider(String name) {
        return this.providerMap.get(name);
    }

    @Override
    public final int getItemCnt() {
        return this.providerMap.keySet().size();
    }

    @Override
    @Nonnull
    public final Iterable<? extends SoyValue> getItemKeys() {
        return Iterables.transform(this.providerMap.keySet(), (Function)new Function<String, SoyValue>(){

            public SoyValue apply(String key) {
                return StringData.forValue(key);
            }
        });
    }

    @Override
    public final boolean hasItem(SoyValue key) {
        return this.providerMap.containsKey(this.getStringKey(key));
    }

    @Override
    public final SoyValue getItem(SoyValue key) {
        return this.getField(this.getStringKey(key));
    }

    @Override
    public final SoyValueProvider getItemProvider(SoyValue key) {
        return this.providerMap.get(this.getStringKey(key));
    }

    @Override
    @Nonnull
    public final Map<String, ? extends SoyValueProvider> asJavaStringMap() {
        return Collections.unmodifiableMap(this.providerMap);
    }

    @Override
    @Nonnull
    public final Map<String, ? extends SoyValue> asResolvedJavaStringMap() {
        return Maps.transformValues(this.asJavaStringMap(), Transforms.RESOLVE_FUNCTION);
    }

    protected final String getStringKey(SoyValue key) {
        try {
            return ((StringData)key).getValue();
        }
        catch (ClassCastException e) {
            throw new SoyDataException("SoyDict accessed with non-string key (got key type " + key.getClass().getName() + ").");
        }
    }
}

