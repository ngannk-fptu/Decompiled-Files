/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.ExpOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class ExpCRIF
extends CRIFImpl {
    public ExpCRIF() {
        super("exp");
    }

    public RenderedImage create(ParameterBlock pb, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new ExpOpImage(pb.getRenderedSource(0), renderHints, layout);
    }
}

