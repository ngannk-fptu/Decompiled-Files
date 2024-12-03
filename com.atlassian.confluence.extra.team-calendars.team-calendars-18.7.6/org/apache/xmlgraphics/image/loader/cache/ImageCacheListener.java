/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.cache;

import java.util.EventListener;
import org.apache.xmlgraphics.image.loader.cache.ImageKey;

public interface ImageCacheListener
extends EventListener {
    public void invalidHit(String var1);

    public void cacheHitImageInfo(String var1);

    public void cacheMissImageInfo(String var1);

    public void cacheHitImage(ImageKey var1);

    public void cacheMissImage(ImageKey var1);
}

