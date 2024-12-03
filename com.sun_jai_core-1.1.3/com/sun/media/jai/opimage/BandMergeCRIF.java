/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.BandMergeOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Vector;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class BandMergeCRIF
extends CRIFImpl {
    public BandMergeCRIF() {
        super("bandmerge");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        Vector<Object> sources = paramBlock.getSources();
        return new BandMergeOpImage(sources, renderHints, layout);
    }
}

