/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.DCTOpImage;
import com.sun.media.jai.opimage.FCT;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class DCTCRIF
extends CRIFImpl {
    public DCTCRIF() {
        super("dct");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        return new DCTOpImage(source, renderHints, layout, new FCT(true, 2));
    }
}

