/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.SubtractFromConstOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class SubtractFromConstCRIF
extends CRIFImpl {
    public SubtractFromConstCRIF() {
        super("subtractfromconst");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new SubtractFromConstOpImage(args.getRenderedSource(0), renderHints, layout, (double[])args.getObjectParameter(0));
    }
}

