/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibCompositeOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.operator.CompositeDescriptor;

public class MlibCompositeRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        RenderedImage alpha1 = (RenderedImage)args.getObjectParameter(0);
        Object alpha2 = args.getObjectParameter(1);
        boolean premultiplied = (Boolean)args.getObjectParameter(2);
        EnumeratedParameter destAlpha = (EnumeratedParameter)args.getObjectParameter(3);
        SampleModel sm = alpha1.getSampleModel();
        if (!(sm instanceof ComponentSampleModel) || sm.getNumBands() != 1 || !(alpha1.getColorModel() instanceof ComponentColorModel) || alpha2 != null || premultiplied || !destAlpha.equals(CompositeDescriptor.NO_DESTINATION_ALPHA)) {
            return null;
        }
        return new MlibCompositeOpImage(args.getRenderedSource(0), args.getRenderedSource(1), hints, layout, alpha1);
    }
}

