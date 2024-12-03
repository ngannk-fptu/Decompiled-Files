/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.CompositeNoDestAlphaOpImage;
import com.sun.media.jai.opimage.CompositeOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import javax.media.jai.CRIFImpl;
import javax.media.jai.EnumeratedParameter;
import javax.media.jai.ImageLayout;
import javax.media.jai.JAI;
import javax.media.jai.operator.CompositeDescriptor;

public class CompositeCRIF
extends CRIFImpl {
    public CompositeCRIF() {
        super("composite");
    }

    public RenderedImage create(ParameterBlock args, RenderingHints hints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(hints);
        RenderedImage source1 = args.getRenderedSource(0);
        RenderedImage source2 = args.getRenderedSource(1);
        RenderedImage alpha1 = (RenderedImage)args.getObjectParameter(0);
        RenderedImage alpha2 = null;
        if (args.getObjectParameter(1) != null) {
            alpha2 = (RenderedImage)args.getObjectParameter(1);
        }
        boolean premultiplied = (Boolean)args.getObjectParameter(2);
        EnumeratedParameter destAlpha = (EnumeratedParameter)args.getObjectParameter(3);
        if (destAlpha.equals(CompositeDescriptor.NO_DESTINATION_ALPHA)) {
            return new CompositeNoDestAlphaOpImage(source1, source2, hints, layout, alpha1, alpha2, premultiplied);
        }
        return new CompositeOpImage(source1, source2, hints, layout, alpha1, alpha2, premultiplied, destAlpha.equals(CompositeDescriptor.DESTINATION_ALPHA_FIRST));
    }

    public RenderedImage create(RenderContext renderContext, ParameterBlock paramBlock) {
        RenderableImage alphaImage1 = (RenderableImage)paramBlock.getObjectParameter(0);
        RenderableImage alphaImage2 = (RenderableImage)paramBlock.getObjectParameter(1);
        RenderedImage rAlphaImage1 = alphaImage1.createRendering(renderContext);
        RenderedImage rAlphaImage2 = alphaImage2.createRendering(renderContext);
        ParameterBlock newPB = (ParameterBlock)paramBlock.clone();
        newPB.set(rAlphaImage1, 0);
        newPB.set(rAlphaImage2, 1);
        return JAI.create("composite", newPB, renderContext.getRenderingHints());
    }
}

