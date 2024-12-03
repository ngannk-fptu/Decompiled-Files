/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.PatternOpImage;
import com.sun.media.jai.opimage.RIFUtil;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.ParameterBlock;
import java.awt.image.renderable.RenderedImageFactory;
import javax.media.jai.ImageLayout;

public class PatternRIF
implements RenderedImageFactory {
    public RenderedImage create(ParameterBlock paramBlock, RenderingHints renderHints) {
        ImageLayout layout = RIFUtil.getImageLayoutHint(renderHints);
        int minX = 0;
        int minY = 0;
        if (layout != null) {
            if (layout.isValid(1)) {
                minX = layout.getMinX(null);
            }
            if (layout.isValid(2)) {
                minY = layout.getMinY(null);
            }
        }
        RenderedImage source = (RenderedImage)paramBlock.getSource(0);
        Raster pattern = source.getData();
        ColorModel colorModel = source.getColorModel();
        int width = paramBlock.getIntParameter(0);
        int height = paramBlock.getIntParameter(1);
        return new PatternOpImage(pattern, colorModel, minX, minY, width, height);
    }
}

