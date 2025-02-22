/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import java.awt.RenderingHints;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.TileCache;

public class RIFUtil {
    public static ImageLayout getImageLayoutHint(RenderingHints renderHints) {
        if (renderHints == null) {
            return null;
        }
        return (ImageLayout)renderHints.get(JAI.KEY_IMAGE_LAYOUT);
    }

    public static TileCache getTileCacheHint(RenderingHints renderHints) {
        if (renderHints == null) {
            return null;
        }
        return (TileCache)renderHints.get(JAI.KEY_TILE_CACHE);
    }

    public static BorderExtender getBorderExtenderHint(RenderingHints renderHints) {
        if (renderHints == null) {
            return null;
        }
        return (BorderExtender)renderHints.get(JAI.KEY_BORDER_EXTENDER);
    }
}

