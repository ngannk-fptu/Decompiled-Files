/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.PiecewiseOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class PiecewiseCRIF
extends CRIFImpl {
    public PiecewiseCRIF() {
        super("piecewise");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new PiecewiseOpImage(args.getRenderedSource(0), renderHints, layout, (float[][][])args.getObjectParameter(0));
    }
}

