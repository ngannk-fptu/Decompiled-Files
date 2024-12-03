/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.Image;
import java.awt.image.RenderedImage;
import java.io.InputStream;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import javax.media.jai.OpImage;
import javax.media.jai.RenderedImageAdapter;
import javax.media.jai.TileCache;

class StreamImage
extends RenderedImageAdapter {
    private InputStream stream;

    public StreamImage(RenderedImage image, InputStream stream) {
        super(image);
        this.stream = stream;
        if (image instanceof OpImage) {
            this.setProperty("tile_cache_key", image);
            TileCache tileCache = ((OpImage)image).getTileCache();
            this.setProperty("tile_cache", tileCache == null ? Image.UndefinedProperty : tileCache);
        }
    }

    public void dispose() {
        RenderedImage trueSrc = this.getWrappedImage();
        Method disposeMethod = null;
        try {
            Class<?> cls = trueSrc.getClass();
            disposeMethod = cls.getMethod("dispose", null);
            if (!disposeMethod.isAccessible()) {
                AccessibleObject.setAccessible(new AccessibleObject[]{disposeMethod}, true);
            }
            disposeMethod.invoke((Object)trueSrc, null);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    protected void finalize() throws Throwable {
        this.stream.close();
        super.finalize();
    }
}

