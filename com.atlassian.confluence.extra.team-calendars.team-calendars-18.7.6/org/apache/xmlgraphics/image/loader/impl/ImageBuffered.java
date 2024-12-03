/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.ImageRendered;

public class ImageBuffered
extends ImageRendered {
    public ImageBuffered(ImageInfo info, BufferedImage buffered, Color transparentColor) {
        super(info, buffered, transparentColor);
    }

    @Override
    public ImageFlavor getFlavor() {
        return ImageFlavor.BUFFERED_IMAGE;
    }

    public BufferedImage getBufferedImage() {
        return (BufferedImage)this.getRenderedImage();
    }
}

