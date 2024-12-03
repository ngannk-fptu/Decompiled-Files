/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibMeanOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;

public class MlibMeanRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        if (!MediaLibAccessor.isMediaLibCompatible(args)) {
            return null;
        }
        RenderedImage source = args.getRenderedSource(0);
        ROI roi = (ROI)args.getObjectParameter(0);
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        int xStart = source.getMinX();
        int yStart = source.getMinY();
        int maxWidth = source.getWidth();
        int maxHeight = source.getHeight();
        if (roi != null && !roi.contains(xStart, yStart, maxWidth, maxHeight)) {
            return null;
        }
        if (xPeriod != 1 || yPeriod != 1) {
            return null;
        }
        return new MlibMeanOpImage(source, roi, xStart, yStart, xPeriod, yPeriod);
    }
}

