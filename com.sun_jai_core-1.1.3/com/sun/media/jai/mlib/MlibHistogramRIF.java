/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.JaiI18N;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibHistogramOpImage;
import com.sun.media.jai.util.ImageUtil;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ROI;
import javax.media.jai.util.ImagingListener;

public class MlibHistogramRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        int i;
        int maxPixelValue;
        int minPixelValue;
        if (!MediaLibAccessor.isMediaLibCompatible(args)) {
            return null;
        }
        RenderedImage src = args.getRenderedSource(0);
        int dataType = src.getSampleModel().getDataType();
        if (dataType == 4 || dataType == 5) {
            return null;
        }
        ROI roi = (ROI)args.getObjectParameter(0);
        if (roi != null && !roi.equals(new Rectangle(src.getMinX(), src.getMinY(), src.getWidth(), src.getHeight()))) {
            return null;
        }
        int xPeriod = args.getIntParameter(1);
        int yPeriod = args.getIntParameter(2);
        int[] numBins = (int[])args.getObjectParameter(3);
        double[] lowValueFP = (double[])args.getObjectParameter(4);
        double[] highValueFP = (double[])args.getObjectParameter(5);
        switch (dataType) {
            case 2: {
                minPixelValue = Short.MIN_VALUE;
                maxPixelValue = Short.MAX_VALUE;
                break;
            }
            case 1: {
                minPixelValue = 0;
                maxPixelValue = 65535;
                break;
            }
            case 3: {
                minPixelValue = Integer.MIN_VALUE;
                maxPixelValue = Integer.MAX_VALUE;
                break;
            }
            default: {
                minPixelValue = 0;
                maxPixelValue = 255;
            }
        }
        for (i = 0; i < lowValueFP.length; ++i) {
            if (!(lowValueFP[i] < (double)minPixelValue) && !(lowValueFP[i] > (double)maxPixelValue)) continue;
            return null;
        }
        for (i = 0; i < highValueFP.length; ++i) {
            if (!(highValueFP[i] <= (double)minPixelValue) && !(highValueFP[i] > (double)(maxPixelValue + 1))) continue;
            return null;
        }
        MlibHistogramOpImage op = null;
        try {
            op = new MlibHistogramOpImage(src, xPeriod, yPeriod, numBins, lowValueFP, highValueFP);
        }
        catch (Exception e) {
            ImagingListener listener = ImageUtil.getImagingListener(hints);
            String message = JaiI18N.getString("MlibHistogramRIF0");
            listener.errorOccurred(message, e, this, false);
        }
        return op;
    }
}

