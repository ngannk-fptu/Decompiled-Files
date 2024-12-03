/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.mlib;

import com.sun.media.jai.mlib.FFTmediaLib;
import com.sun.media.jai.mlib.MediaLibAccessor;
import com.sun.media.jai.mlib.MlibDFTOpImage;
import com.sun.media.jai.opimage.DFTOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.util.MathJAI;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.operator.DFTDescriptor;

public class MlibIDFTRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        if (!MediaLibAccessor.isMediaLibCompatible(new ParameterBlock())) {
            return null;
        }
        RenderedImage source = args.getRenderedSource(0);
        EnumeratedParameter scalingType = (EnumeratedParameter)args.getObjectParameter(0);
        EnumeratedParameter dataNature = (EnumeratedParameter)args.getObjectParameter(1);
        boolean isComplexSource = !dataNature.equals(DFTDescriptor.REAL_TO_COMPLEX);
        int numSourceBands = source.getSampleModel().getNumBands();
        if ((isComplexSource && numSourceBands == 2 || !isComplexSource && numSourceBands == 1) && MlibDFTOpImage.isAcceptableSampleModel(source.getSampleModel())) {
            int sourceWidth = source.getWidth();
            int sourceHeight = source.getHeight();
            if (!MathJAI.isPositivePowerOf2(sourceWidth) || !MathJAI.isPositivePowerOf2(sourceHeight)) {
                ParameterBlock pb = new ParameterBlock();
                pb.addSource(source);
                pb.add(0);
                pb.add(MathJAI.nextPositivePowerOf2(sourceWidth) - sourceWidth);
                pb.add(0);
                pb.add(MathJAI.nextPositivePowerOf2(sourceHeight) - sourceHeight);
                pb.add(BorderExtender.createInstance(0));
                source = JAI.create("border", pb);
            }
            return new MlibDFTOpImage(source, hints, layout, dataNature, false, scalingType);
        }
        FFTmediaLib fft = new FFTmediaLib(false, new Integer(scalingType.getValue()), 2);
        return new DFTOpImage(source, hints, layout, dataNature, fft);
    }
}

