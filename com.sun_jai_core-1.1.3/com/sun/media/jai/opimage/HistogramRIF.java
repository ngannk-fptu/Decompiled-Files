/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.HistogramOpImage;
import com.sun.media.jai.opimage.JaiI18N;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;
import javax.media.jai.util.ImagingListener;

public class HistogramRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        RenderedImage src = args.getRenderedSource(0);
        int xStart = src.getMinX();
        int yStart = src.getMinY();
        int maxWidth = src.getWidth();
        int maxHeight = src.getHeight();
        ROI roi = (ROI)args.getObjectParameter(0);
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        int[] numBins = (int[])args.getObjectParameter(3);
        double[] lowValue = (double[])args.getObjectParameter(4);
        double[] highValue = (double[])args.getObjectParameter(5);
        HistogramOpImage op = null;
        try {
            op = new HistogramOpImage(src, roi, xStart, yStart, xPeriod, yPeriod, numBins, lowValue, highValue);
        }
        catch (Exception e) {
            ImagingListener listener = ImageUtil.getImagingListener(hints);
            String message = JaiI18N.getString("HistogramRIF0");
            listener.errorOccurred(message, e, this, false);
        }
        return op;
    }
}

