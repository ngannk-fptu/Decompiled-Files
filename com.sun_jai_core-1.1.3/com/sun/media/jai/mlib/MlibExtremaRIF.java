/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibExtremaOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;

public class MlibExtremaRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        if (!MediaLibAccessor.isMediaLibCompatible(args)) {
            return null;
        }
        RenderedImage source = args.getRenderedSource(0);
        ROI roi = (ROI)args.getObjectParameter(0);
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        boolean saveLocations = (Boolean)args.getObjectParameter(3);
        int maxRuns = args.getIntParameter(4);
        int xStart = source.getMinX();
        int yStart = source.getMinY();
        int maxWidth = source.getWidth();
        int maxHeight = source.getHeight();
        if (roi != null && !roi.contains(xStart, yStart, maxWidth, maxHeight)) {
            return null;
        }
        return new MlibExtremaOpImage(source, roi, xStart, yStart, xPeriod, yPeriod, saveLocations, maxRuns);
    }
}

