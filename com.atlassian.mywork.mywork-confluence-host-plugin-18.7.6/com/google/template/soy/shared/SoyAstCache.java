/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package com.google.template.soy.shared;

import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.template.soy.base.internal.IdGenerator;
import com.google.template.soy.base.internal.IncrementingIdGenerator;
import com.google.template.soy.base.internal.SoyFileSupplier;
import com.google.template.soy.internal.base.Pair;
import com.google.template.soy.soytree.SoyFileNode;
import java.util.Map;

public class SoyAstCache {
    private final Map<String, Pair<SoyFileNode, SoyFileSupplier.Version>> cache = Maps.newHashMap();
    private final IdGenerator idGenerator = new IncrementingIdGenerator();

    @Inject
    public SoyAstCache() {
    }

    public synchronized void put(SoyFileSupplier supplier, SoyFileSupplier.Version version, SoyFileNode node) {
        this.cache.put(SoyAstCache.getCacheKey(supplier), Pair.of(node.clone(), version));
    }

    public synchronized Pair<SoyFileNode, SoyFileSupplier.Version> get(SoyFileSupplier supplier) {
        Pair<SoyFileNode, SoyFileSupplier.Version> entry = this.cache.get(SoyAstCache.getCacheKey(supplier));
        if (entry != null) {
            if (!supplier.hasChangedSince((SoyFileSupplier.Version)entry.second)) {
                return Pair.of(((SoyFileNode)entry.first).clone(), entry.second);
            }
            this.cache.remove(SoyAstCache.getCacheKey(supplier));
        }
        return null;
    }

    public IdGenerator getNodeIdGenerator() {
        return this.idGenerator;
    }

    private static String getCacheKey(SoyFileSupplier supplier) {
        return supplier.getFilePath();
    }
}

