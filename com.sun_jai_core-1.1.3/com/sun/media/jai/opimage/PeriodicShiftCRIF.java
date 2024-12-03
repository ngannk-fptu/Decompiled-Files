/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.PeriodicShiftOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class PeriodicShiftCRIF
extends CRIFImpl {
    public PeriodicShiftCRIF() {
        super("periodicshift");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        int shiftX = paramBlock.getIntParameter(0);
        int shiftY = paramBlock.getIntParameter(1);
        return new PeriodicShiftOpImage(source, renderHints, layout, shiftX, shiftY);
    }
}

