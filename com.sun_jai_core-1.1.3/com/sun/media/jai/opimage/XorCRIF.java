/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.XorOpImage;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Map;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class XorCRIF
extends CRIFImpl {
    public XorCRIF() {
        super("xor");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new XorOpImage(paramBlock.getRenderedSource(0), paramBlock.getRenderedSource(1), (Map)renderHints, layout);
    }
}

