/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  javax.annotation.ParametersAreNonnullByDefault
 */
package com.google.template.soy.data;

import com.google.common.collect.Maps;
import com.google.template.soy.data.SoyAbstractRecord;
import com.google.template.soy.data.SoyValueProvider;
import java.util.Map;
import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public abstract class SoyAbstractCachingRecord
extends SoyAbstractRecord {
    private final Map<String, SoyValueProvider> cachedFieldProviders = Maps.newHashMap();

    @Override
    public final SoyValueProvider getFieldProvider(String name) {
        if (this.cachedFieldProviders.containsKey(name)) {
            return this.cachedFieldProviders.get(name);
        }
        SoyValueProvider result = this.getFieldProviderInternal(name);
        this.cachedFieldProviders.put(name, result);
        return result;
    }

    public abstract SoyValueProvider getFieldProviderInternal(String var1);
}

