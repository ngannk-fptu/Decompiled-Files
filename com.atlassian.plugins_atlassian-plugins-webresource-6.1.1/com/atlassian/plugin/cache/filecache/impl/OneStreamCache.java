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

public class OneStreamCache
extends StreamsCache {
    protected final File tmpFile;

    public OneStreamCache(File tmpFile) {
        this.tmpFile = tmpFile;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void stream(OutputStream out, Cache.StreamProvider provider) {
        block3: {
            boolean fromCache = this.doEnter(() -> {
                try {
                    this.streamToCache(provider);
                }
                catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }, this.tmpFile::isFile);
            try {
                if (fromCache) {
                    OneStreamCache.streamFromFile(this.tmpFile, out);
                    break block3;
                }
                provider.write(out);
            }
            catch (Throwable throwable) {
                this.doExit(this.tmpFile::delete);
                throw throwable;
            }
        }
        this.doExit(this.tmpFile::delete);
    }

    @Override
    public void streamTwo(OutputStream out1, OutputStream out2, Cache.TwoStreamProvider provider) {
        throw new RuntimeException("one stream cache doesn't support streamTwo method!");
    }

    @Override
    public void deleteWhenPossible() {
        this.deleteWhenPossible(this.tmpFile::delete);
    }

    protected void streamToCache(Cache.StreamProvider input) throws IOException {
        try (OutputStream cacheout = null;){
            cacheout = new BufferedOutputStream(this.createWriteStream(this.tmpFile));
            input.write(cacheout);
        }
    }
}

