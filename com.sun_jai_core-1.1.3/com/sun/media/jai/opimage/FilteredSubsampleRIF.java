/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.FilteredSubsampleOpImage;
import com.sun.media.jai.opimage.JaiI18N;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.JAI;

public class FilteredSubsampleRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        boolean validInterp;
        RenderedImage source = paramBlock.getRenderedSource(0);
        BorderExtender extender = renderHints == null ? null : (BorderExtender)renderHints.get(JAI.KEY_BORDER_EXTENDER);
        ImageLayout layout = renderHints == null ? null : (ImageLayout)renderHints.get(JAI.KEY_IMAGE_LAYOUT);
        int scaleX = paramBlock.getIntParameter(0);
        int scaleY = paramBlock.getIntParameter(1);
        float[] qsFilter = (float[])paramBlock.getObjectParameter(2);
        Interpolation interp = (Interpolation)paramBlock.getObjectParameter(3);
        SampleModel sm = source.getSampleModel();
        int dataType = sm.getDataType();
        boolean bl = validInterp = interp instanceof InterpolationNearest || interp instanceof InterpolationBilinear || interp instanceof InterpolationBicubic || interp instanceof InterpolationBicubic2;
        if (!validInterp) {
            throw new IllegalArgumentException(JaiI18N.getString("FilteredSubsample3"));
        }
        return new FilteredSubsampleOpImage(source, extender, renderHints, layout, scaleX, scaleY, qsFilter, interp);
    }
}

