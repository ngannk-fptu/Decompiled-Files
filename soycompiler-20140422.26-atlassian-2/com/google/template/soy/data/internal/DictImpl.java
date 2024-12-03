/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data.internal;

import com.google.common.collect.ImmutableMap;
import com.google.template.soy.data.SoyValueProvider;
import com.google.template.soy.data.internal.AbstractDict;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public final class DictImpl
extends AbstractDict {
    public static final DictImpl EMPTY = DictImpl.forProviderMap((Map<String, ? extends SoyValueProvider>)ImmutableMap.of());

    public static DictImpl forProviderMap(Map<String, ? extends SoyValueProvider> providerMap) {
        return new DictImpl(providerMap);
    }

    private DictImpl(Map<String, ? extends SoyValueProvider> providerMap) {
        super(providerMap);
    }
}

