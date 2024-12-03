/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.WarpBilinearOpImage;
import com.sun.media.jai.opimage.WarpGeneralOpImage;
import com.sun.media.jai.opimage.WarpNearestOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.Warp;

public class WarpRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        BorderExtender extender = RIFUtil.getBorderExtenderHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        Warp warp = (Warp)paramBlock.getObjectParameter(0);
        Interpolation interp = (Interpolation)paramBlock.getObjectParameter(1);
        double[] backgroundValues = (double[])paramBlock.getObjectParameter(2);
        if (interp instanceof InterpolationNearest) {
            return new WarpNearestOpImage(source, renderHints, layout, warp, interp, backgroundValues);
        }
        if (interp instanceof InterpolationBilinear) {
            return new WarpBilinearOpImage(source, extender, (Map)renderHints, layout, warp, interp, backgroundValues);
        }
        return new WarpGeneralOpImage(source, extender, (Map)renderHints, layout, warp, interp, backgroundValues);
    }
}

