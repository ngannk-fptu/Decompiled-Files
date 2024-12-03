/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.cache.filecache.impl;

import com.atlassian.plugin.cache.filecache.Cache;
import com.atlassian.plugin.cache.filecache.impl.StreamsCache;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class TwoStreamsCache
extends StreamsCache {
    protected final File tmpFile1;
    protected final File tmpFile2;

    public TwoStreamsCache(File tmpFile1, File tmpFile2) {
        this.tmpFile1 = tmpFile1;
        this.tmpFile2 = tmpFile2;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void streamTwo(OutputStream out1, OutputStream out2, Cache.TwoStreamProvider provider) {
        boolean fromCache = this.doEnter(() -> {
            try {
                this.streamToCache(provider);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, () -> this.tmpFile1.isFile() && this.tmpFile2.isFile());
        try {
            if (fromCache) {
                if (out1 != null) {
                    TwoStreamsCache.streamFromFile(this.tmpFile1, out1);
                }
                if (out2 != null) {
                    TwoStreamsCache.streamFromFile(this.tmpFile2, out2);
                }
            } else {
                provider.write(out1, out2);
            }
        }
        finally {
            this.doExit(() -> {
                this.tmpFile1.delete();
                this.tmpFile2.delete();
            });
        }
    }

    @Override
    public void stream(OutputStream out, Cache.StreamProvider provider) {
        throw new RuntimeException("two streams cache doesn't support stream method!");
    }

    @Override
    public void deleteWhenPossible() {
        this.deleteWhenPossible(() -> {
            this.tmpFile1.delete();
            this.tmpFile2.delete();
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void streamToCache(Cache.TwoStreamProvider provider) throws IOException {
        OutputStream cacheout1 = null;
        OutputStream cacheout2 = null;
        try {
            cacheout1 = new BufferedOutputStream(this.createWriteStream(this.tmpFile1));
            cacheout2 = new BufferedOutputStream(this.createWriteStream(this.tmpFile2));
            provider.write(cacheout1, cacheout2);
        }
        finally {
            if (cacheout1 != null) {
                cacheout1.close();
            }
            if (cacheout2 != null) {
                cacheout2.close();
            }
        }
    }
}

