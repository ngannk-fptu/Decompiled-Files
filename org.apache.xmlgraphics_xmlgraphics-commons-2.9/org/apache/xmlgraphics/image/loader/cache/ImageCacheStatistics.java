/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlgraphics.image.loader.cache.ImageCacheListener;
import org.apache.xmlgraphics.image.loader.cache.ImageKey;

public class ImageCacheStatistics
implements ImageCacheListener {
    private int invalidHits;
    private int imageInfoCacheHits;
    private int imageInfoCacheMisses;
    private int imageCacheHits;
    private int imageCacheMisses;
    private Map imageCacheHitMap;
    private Map imageCacheMissMap;

    public ImageCacheStatistics(boolean detailed) {
        if (detailed) {
            this.imageCacheHitMap = new HashMap();
            this.imageCacheMissMap = new HashMap();
        }
    }

    public void reset() {
        this.imageInfoCacheHits = 0;
        this.imageInfoCacheMisses = 0;
        this.invalidHits = 0;
    }

    @Override
    public void invalidHit(String uri) {
        ++this.invalidHits;
    }

    @Override
    public void cacheHitImageInfo(String uri) {
        ++this.imageInfoCacheHits;
    }

    @Override
    public void cacheMissImageInfo(String uri) {
        ++this.imageInfoCacheMisses;
    }

    private void increaseEntry(Map map, Object key) {
        Integer v = (Integer)map.get(key);
        v = v == null ? Integer.valueOf(1) : Integer.valueOf(v + 1);
        map.put(key, v);
    }

    @Override
    public void cacheHitImage(ImageKey key) {
        ++this.imageCacheHits;
        if (this.imageCacheHitMap != null) {
            this.increaseEntry(this.imageCacheHitMap, key);
        }
    }

    @Override
    public void cacheMissImage(ImageKey key) {
        ++this.imageCacheMisses;
        if (this.imageCacheMissMap != null) {
            this.increaseEntry(this.imageCacheMissMap, key);
        }
    }

    public int getInvalidHits() {
        return this.invalidHits;
    }

    public int getImageInfoCacheHits() {
        return this.imageInfoCacheHits;
    }

    public int getImageInfoCacheMisses() {
        return this.imageInfoCacheMisses;
    }

    public int getImageCacheHits() {
        return this.imageCacheHits;
    }

    public int getImageCacheMisses() {
        return this.imageCacheMisses;
    }

    public Map getImageCacheHitMap() {
        return Collections.unmodifiableMap(this.imageCacheHitMap);
    }

    public Map getImageCacheMissMap() {
        return Collections.unmodifiableMap(this.imageCacheMissMap);
    }
}

