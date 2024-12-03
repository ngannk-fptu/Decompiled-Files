/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.util;

import java.awt.image.RenderedImage;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.NullOpImage;
import javax.media.jai.PlanarImage;
import javax.media.jai.RenderedImageAdapter;

public class DisposableNullOpImage
extends NullOpImage {
    public DisposableNullOpImage(RenderedImage source, ImageLayout layout, Map configuration, int computeType) {
        super(source, layout, configuration, computeType);
    }

    public synchronized void dispose() {
        PlanarImage src = this.getSource(0);
        if (src instanceof RenderedImageAdapter) {
            RenderedImage trueSrc = ((RenderedImageAdapter)src).getWrappedImage();
            Method disposeMethod = null;
            try {
                Class<?> cls = trueSrc.getClass();
                disposeMethod = cls.getMethod("dispose", null);
                if (!disposeMethod.isAccessible()) {
                    AccessibleObject.setAccessible(new AccessibleObject[]{disposeMethod}, true);
                }
                disposeMethod.invoke((Object)trueSrc, null);
            }
            catch (Exception e) {}
        } else {
            src.dispose();
        }
    }
}

