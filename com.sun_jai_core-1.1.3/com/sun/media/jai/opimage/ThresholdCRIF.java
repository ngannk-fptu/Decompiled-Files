/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.ThresholdOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class ThresholdCRIF
extends CRIFImpl {
    public ThresholdCRIF() {
        super("threshold");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new ThresholdOpImage(args.getRenderedSource(0), renderHints, layout, (double[])args.getObjectParameter(0), (double[])args.getObjectParameter(1), (double[])args.getObjectParameter(2));
    }
}

