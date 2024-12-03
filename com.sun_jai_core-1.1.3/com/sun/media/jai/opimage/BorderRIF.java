/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.BorderOpImage;
import com.sun.media.jai.opimage.PatternOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.BorderExtender;
import javax.media.jai.ImageLayout;

public class BorderRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock args, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        RenderedImage source = args.getRenderedSource(0);
        int leftPad = args.getIntParameter(0);
        int rightPad = args.getIntParameter(1);
        int topPad = args.getIntParameter(2);
        int bottomPad = args.getIntParameter(3);
        BorderExtender type = (BorderExtender)args.getObjectParameter(4);
        if (type == BorderExtender.createInstance(3)) {
            int minX = source.getMinX() - leftPad;
            int minY = source.getMinY() - topPad;
            int width = source.getWidth() + leftPad + rightPad;
            int height = source.getHeight() + topPad + bottomPad;
            return new PatternOpImage(source.getData(), source.getColorModel(), minX, minY, width, height);
        }
        return new BorderOpImage(source, renderHints, layout, leftPad, rightPad, topPad, bottomPad, type);
    }
}

