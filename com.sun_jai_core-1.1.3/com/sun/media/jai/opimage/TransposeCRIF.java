/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.RIFUtil;
import com.sun.media.jai.opimage.TransposeBinaryOpImage;
import com.sun.media.jai.opimage.TransposeOpImage;
import java.awt.RenderingHints;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.renderable.ParameterBlock;
import javax.media.jai.CRIFImpl;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;

public class TransposeCRIF
extends CRIFImpl {
    public TransposeCRIF() {
        super("transpose");
    }

    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = paramBlock.getRenderedSource(0);
        EnumeratedParameter type = (EnumeratedParameter)paramBlock.getObjectParameter(0);
        SampleModel sm = source.getSampleModel();
        if (sm instanceof MultiPixelPackedSampleModel && sm.getSampleSize(0) == 1 && (sm.getDataType() == 0 || sm.getDataType() == 1 || sm.getDataType() == 3)) {
            return new TransposeBinaryOpImage(source, renderHints, layout, type.getValue());
        }
        return new TransposeOpImage(source, renderHints, layout, type.getValue());
    }
}

