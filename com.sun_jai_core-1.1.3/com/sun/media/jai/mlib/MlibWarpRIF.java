/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibWarpGridOpImage;
import com.sun.media.jai.mlib.MlibWarpGridTableOpImage;
import com.sun.media.jai.mlib.MlibWarpPolynomialOpImage;
import com.sun.media.jai.mlib.MlibWarpPolynomialTableOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.Interpolation;
import javax.media.jai.InterpolationBicubic;
import javax.media.jai.InterpolationBicubic2;
import javax.media.jai.InterpolationBilinear;
import javax.media.jai.InterpolationNearest;
import javax.media.jai.InterpolationTable;
import javax.media.jai.Warp;
import javax.media.jai.WarpGrid;
import javax.media.jai.WarpPolynomial;

public class MlibWarpRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        RenderedImage source = args.getRenderedSource(0);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout) || source.getTileWidth() >= 32768 || source.getTileHeight() >= 32768) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        Warp warp = (Warp)args.getObjectParameter(0);
        Interpolation interp = (Interpolation)args.getObjectParameter(1);
        double[] backgroundValues = (double[])args.getObjectParameter(2);
        int filter = -1;
        if (interp instanceof InterpolationNearest) {
            filter = 0;
        } else if (interp instanceof InterpolationBilinear) {
            filter = 1;
        } else if (interp instanceof InterpolationBicubic) {
            filter = 2;
        } else if (interp instanceof InterpolationBicubic2) {
            filter = 3;
        } else if (!(interp instanceof InterpolationTable)) {
            return null;
        }
        if (warp instanceof WarpGrid) {
            if (interp instanceof InterpolationTable) {
                return new MlibWarpGridTableOpImage(source, extender, (Map)hints, layout, (WarpGrid)warp, interp, backgroundValues);
            }
            return new MlibWarpGridOpImage(source, extender, (Map)hints, layout, (WarpGrid)warp, interp, filter, backgroundValues);
        }
        if (warp instanceof WarpPolynomial) {
            if (interp instanceof InterpolationTable) {
                return new MlibWarpPolynomialTableOpImage(source, extender, (Map)hints, layout, (WarpPolynomial)warp, interp, backgroundValues);
            }
            return new MlibWarpPolynomialOpImage(source, extender, (Map)hints, layout, (WarpPolynomial)warp, interp, filter, backgroundValues);
        }
        return null;
    }
}

