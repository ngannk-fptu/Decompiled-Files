/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.OrConstOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class OrConstCRIF
extends CRIFImpl {
    public OrConstCRIF() {
        super("orconst");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new OrConstOpImage(args.getRenderedSource(0), renderHints, layout, (int[])args.getObjectParameter(0));
    }
}

