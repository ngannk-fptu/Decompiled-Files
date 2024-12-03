/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.AddCollectionOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.util.Collection;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class AddCollectionCRIF
extends CRIFImpl {
    public AddCollectionCRIF() {
        super("addcollection");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        return new AddCollectionOpImage((Collection)args.getSource(0), renderHints, layout);
    }
}

