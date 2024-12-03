/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.svggen;

import org.apache.batik.svggen.GenericImageHandler;
import org.apache.batik.svggen.ImageCacher;

public interface CachedImageHandler
extends GenericImageHandler {
    public ImageCacher getImageCacher();
}

