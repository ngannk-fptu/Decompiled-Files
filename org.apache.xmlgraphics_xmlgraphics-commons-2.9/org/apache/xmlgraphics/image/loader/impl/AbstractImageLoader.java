/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.util.Map;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageProcessingHints;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public abstract class AbstractImageLoader
implements ImageLoader {
    @Override
    public Image loadImage(ImageInfo info, ImageSessionContext session) throws ImageException, IOException {
        return this.loadImage(info, null, session);
    }

    @Override
    public int getUsagePenalty() {
        return 0;
    }

    protected boolean ignoreColorProfile(Map hints) {
        if (hints == null) {
            return false;
        }
        Boolean b = (Boolean)hints.get(ImageProcessingHints.IGNORE_COLOR_PROFILE);
        return b != null && b != false;
    }
}

