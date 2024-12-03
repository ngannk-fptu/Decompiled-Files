/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.io.IOException;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageException;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.ImageSessionContext;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageLoader;
import org.apache.xmlgraphics.image.loader.spi.ImageLoader;

public class CompositeImageLoader
extends AbstractImageLoader {
    protected static final Log log = LogFactory.getLog(CompositeImageLoader.class);
    private ImageLoader[] loaders;

    public CompositeImageLoader(ImageLoader[] loaders) {
        if (loaders == null || loaders.length == 0) {
            throw new IllegalArgumentException("Must at least pass one ImageLoader as parameter");
        }
        int c = loaders.length;
        for (int i = 1; i < c; ++i) {
            if (loaders[0].getTargetFlavor().equals(loaders[i].getTargetFlavor())) continue;
            throw new IllegalArgumentException("All ImageLoaders must produce the same target flavor");
        }
        this.loaders = loaders;
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return this.loaders[0].getTargetFlavor();
    }

    @Override
    public int getUsagePenalty() {
        int maxPenalty = 0;
        int c = this.loaders.length;
        for (int i = 1; i < c; ++i) {
            maxPenalty = Math.max(maxPenalty, this.loaders[i].getUsagePenalty());
        }
        return maxPenalty;
    }

    @Override
    public Image loadImage(ImageInfo info, Map hints, ImageSessionContext session) throws ImageException, IOException {
        ImageException firstException = null;
        for (ImageLoader loader : this.loaders) {
            try {
                Image img = loader.loadImage(info, hints, session);
                if (img != null && firstException != null) {
                    log.debug((Object)("First ImageLoader failed (" + firstException.getMessage() + "). Fallback was successful."));
                }
                return img;
            }
            catch (ImageException ie) {
                if (firstException != null) continue;
                firstException = ie;
            }
        }
        throw firstException;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < this.loaders.length; ++i) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(this.loaders[i].toString());
        }
        sb.append("]");
        return sb.toString();
    }
}

