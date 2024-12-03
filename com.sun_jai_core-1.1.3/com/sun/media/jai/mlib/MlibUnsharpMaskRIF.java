/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibConvolveNxNOpImage;
import com.sun.media.jai.mlib.MlibConvolveOpImage;
import com.sun.media.jai.mlib.MlibSeparableConvolveOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.ImageUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class MlibUnsharpMaskRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        RenderedImage source = args.getRenderedSource(0);
        KernelJAI unRotatedKernel = ImageUtil.getUnsharpMaskEquivalentKernel((KernelJAI)args.getObjectParameter(0), args.getFloatParameter(1));
        KernelJAI kJAI = unRotatedKernel.getRotatedKernel();
        int kWidth = kJAI.getWidth();
        int kHeight = kJAI.getHeight();
        if (kWidth < 2 || kHeight < 2) {
            return null;
        }
        if (kJAI.isSeparable() && kWidth >= 3 && kWidth <= 7 && kWidth == kHeight) {
            return new MlibSeparableConvolveOpImage(source, extender, hints, layout, kJAI);
        }
        if (kWidth == 3 && kHeight == 3 || kWidth == 5 && kHeight == 5) {
            return new MlibConvolveNxNOpImage(source, extender, hints, layout, kJAI);
        }
        return new MlibConvolveOpImage(source, extender, hints, layout, kJAI);
    }
}

