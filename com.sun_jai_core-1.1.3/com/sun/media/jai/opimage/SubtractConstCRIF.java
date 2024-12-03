/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.AddConstOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.ImageLayout;

public class SubtractConstCRIF
extends CRIFImpl {
    public SubtractConstCRIF() {
        super("subtractconst");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        double[] constants = (double[])args.getObjectParameter(0);
        int length = constants.length;
        double[] negConstants = new double[length];
        for (int i = 0; i < length; ++i) {
            negConstants[i] = -constants[i];
        }
        return new AddConstOpImage(args.getRenderedSource(0), hints, layout, negConstants);
    }
}

