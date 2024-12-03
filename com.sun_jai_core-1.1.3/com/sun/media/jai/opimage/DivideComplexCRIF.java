/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ComplexArithmeticOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class DivideComplexCRIF
extends CRIFImpl {
    public DivideComplexCRIF() {
        super("dividecomplex");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new ComplexArithmeticOpImage(paramBlock.getRenderedSource(0), paramBlock.getRenderedSource(1), renderHints, layout, true);
    }
}

