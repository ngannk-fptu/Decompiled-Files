/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.DFTOpImage;
import com.sun.media.jai.opimage.FFT;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;

public class DFTCRIF
extends CRIFImpl {
    public DFTCRIF() {
        super("dft");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        EnumeratedParameter scalingType = (EnumeratedParameter)paramBlock.getObjectParameter(0);
        EnumeratedParameter dataNature = (EnumeratedParameter)paramBlock.getObjectParameter(1);
        FFT fft = new FFT(true, new Integer(scalingType.getValue()), 2);
        return new DFTOpImage(source, renderHints, layout, dataNature, fft);
    }
}

