/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools.cache;

import org.aspectj.weaver.tools.GeneratedClassHandler;
import org.aspectj.weaver.tools.cache.CachedClassReference;
import org.aspectj.weaver.tools.cache.WeavedClassCache;

public class GeneratedCachedClassHandler
implements GeneratedClassHandler {
    private final WeavedClassCache cache;
    private final GeneratedClassHandler nextGeneratedClassHandler;

    public GeneratedCachedClassHandler(WeavedClassCache cache, GeneratedClassHandler nextHandler) {
        this.cache = cache;
        this.nextGeneratedClassHandler = nextHandler;
    }

    @Override
    public void acceptClass(String name, byte[] originalBytes, byte[] wovenBytes) {
        CachedClassReference ref = this.cache.createGeneratedCacheKey(name.replace('/', '.'));
        this.cache.put(ref, originalBytes, wovenBytes);
        if (this.nextGeneratedClassHandler != null) {
            this.nextGeneratedClassHandler.acceptClass(name, originalBytes, wovenBytes);
        }
    }
}

