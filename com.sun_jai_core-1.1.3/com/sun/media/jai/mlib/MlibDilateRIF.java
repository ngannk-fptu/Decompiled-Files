/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibDilate3PlusOpImage;
import com.sun.media.jai.mlib.MlibDilate3SquareOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import java.util.Map;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class MlibDilateRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        boolean isBinary = false;
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            if (!MediaLibAccessor.isMediaLibBinaryCompatible(args, layout)) {
                return null;
            }
            isBinary = true;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        RenderedImage source = args.getRenderedSource(0);
        KernelJAI unRotatedKernel = (KernelJAI)args.getObjectParameter(0);
        KernelJAI kJAI = unRotatedKernel.getRotatedKernel();
        int kWidth = kJAI.getWidth();
        int kHeight = kJAI.getHeight();
        int xOri = kJAI.getXOrigin();
        int yOri = kJAI.getYOrigin();
        int numB = source.getSampleModel().getNumBands();
        if (xOri != 1 || yOri != 1 || kWidth != 3 || kHeight != 3 || numB != 1) {
            return null;
        }
        float[] kdata = kJAI.getKernelData();
        if (isBinary && this.isKernel3Square1(kdata) || !isBinary && this.isKernel3Square0(kdata)) {
            return new MlibDilate3SquareOpImage(source, extender, (Map)hints, layout);
        }
        if (isBinary && this.isKernel3Plus1(kdata)) {
            return new MlibDilate3PlusOpImage(source, extender, (Map)hints, layout);
        }
        return null;
    }

    private boolean isKernel3Plus1(float[] kdata) {
        return kdata[0] == 0.0f && kdata[1] == 1.0f && kdata[2] == 0.0f && kdata[3] == 1.0f && kdata[4] == 1.0f && kdata[5] == 1.0f && kdata[6] == 0.0f && kdata[7] == 1.0f && kdata[8] == 0.0f;
    }

    private boolean isKernel3Square0(float[] kdata) {
        return kdata[0] == 0.0f && kdata[1] == 0.0f && kdata[2] == 0.0f && kdata[3] == 0.0f && kdata[4] == 0.0f && kdata[5] == 0.0f && kdata[6] == 0.0f && kdata[7] == 0.0f && kdata[8] == 0.0f;
    }

    private boolean isKernel3Square1(float[] kdata) {
        return kdata[0] == 1.0f && kdata[1] == 1.0f && kdata[2] == 1.0f && kdata[3] == 1.0f && kdata[4] == 1.0f && kdata[5] == 1.0f && kdata[6] == 1.0f && kdata[7] == 1.0f && kdata[8] == 1.0f;
    }
}

