/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.util;

import com.amazonaws.ResponseMetadata;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.util.MetadataCache;
import java.util.LinkedHashMap;
import java.util.Map;

@SdkInternalApi
public class ResponseMetadataCache
implements MetadataCache {
    private final InternalCache internalCache;

    public ResponseMetadataCache(int maxEntries) {
        this.internalCache = new InternalCache(maxEntries);
    }

    @Override
    public synchronized void add(Object obj, ResponseMetadata metadata) {
        if (obj == null) {
            return;
        }
        this.internalCache.put(System.identityHashCode(obj), metadata);
    }

    @Override
    public synchronized ResponseMetadata get(Object obj) {
        return (ResponseMetadata)this.internalCache.get(System.identityHashCode(obj));
    }

    private static final class InternalCache
    extends LinkedHashMap<Integer, ResponseMetadata> {
        private static final long serialVersionUID = 1L;
        private int maxSize;

        InternalCache(int maxSize) {
            super(maxSize);
            this.maxSize = maxSize;
        }

        @Override
        protected boolean removeEldestEntry(Map.Entry<Integer, ResponseMetadata> eldest) {
            return this.size() > this.maxSize;
        }
    }
}

