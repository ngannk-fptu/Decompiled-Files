/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.util.Map;
import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.impl.AbstractImageConverter;
import org.apache.xmlgraphics.image.loader.impl.ImageBuffered;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;

public class ImageConverterBuffered2Rendered
extends AbstractImageConverter {
    @Override
    public Image convert(Image src, Map hints) {
        this.checkSourceFlavor(src);
        assert (src instanceof ImageBuffered);
        ImageBuffered buffered = (ImageBuffered)src;
        return new ImageRendered(buffered.getInfo(), buffered.getRenderedImage(), buffered.getTransparentColor());
    }

    @Override
    public ImageFlavor getSourceFlavor() {
        return ImageFlavor.BUFFERED_IMAGE;
    }

    @Override
    public ImageFlavor getTargetFlavor() {
        return ImageFlavor.RENDERED_IMAGE;
    }

    @Override
    public int getConversionPenalty() {
        return 0;
    }
}

