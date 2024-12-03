/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibGradientOpImage;
import com.sun.media.jai.mlib.MlibSobelOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;
import javax.media.jai.KernelJAI;

public class MlibGradientRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(args, layout) || !MediaLibAccessor.hasSameNumBands(args, layout)) {
            return null;
        }
        BorderExtender extender = RIFUtil.getBorderExtenderHint(hints);
        RenderedImage source = args.getRenderedSource(0);
        KernelJAI kern_h = (KernelJAI)args.getObjectParameter(0);
        KernelJAI kern_v = (KernelJAI)args.getObjectParameter(1);
        int kWidth = kern_h.getWidth();
        int kHeight = kern_v.getHeight();
        float[] khdata = kern_h.getKernelData();
        float[] kvdata = kern_v.getKernelData();
        if (khdata[0] == -1.0f && khdata[1] == -2.0f && khdata[2] == -1.0f && khdata[3] == 0.0f && khdata[4] == 0.0f && khdata[5] == 0.0f && khdata[6] == 1.0f && khdata[7] == 2.0f && khdata[8] == 1.0f && kvdata[0] == -1.0f && kvdata[1] == 0.0f && kvdata[2] == 1.0f && kvdata[3] == -2.0f && kvdata[4] == 0.0f && kvdata[5] == 2.0f && kvdata[6] == -1.0f && kvdata[7] == 0.0f && kvdata[8] == 1.0f && kWidth == 3 && kHeight == 3) {
            return new MlibSobelOpImage(source, extender, hints, layout, kern_h);
        }
        return new MlibGradientOpImage(source, extender, hints, layout, kern_h, kern_v);
    }
}

