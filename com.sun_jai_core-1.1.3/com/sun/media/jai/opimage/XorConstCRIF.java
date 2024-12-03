/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.XorConstOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class XorConstCRIF
extends CRIFImpl {
    public XorConstCRIF() {
        super("xorconst");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new XorConstOpImage(args.getRenderedSource(0), renderHints, layout, (int[])args.getObjectParameter(0));
    }
}

