/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.loader.impl;

import org.apache.xmlgraphics.image.loader.Image;
import org.apache.xmlgraphics.image.loader.ImageFlavor;
import org.apache.xmlgraphics.image.loader.ImageInfo;
import org.apache.xmlgraphics.image.loader.impl.AbstractImage;
import org.apache.xmlgraphics.java2d.Graphics2DImagePainter;

public class ImageGraphics2D
extends AbstractImage {
    private Graphics2DImagePainter painter;

    public ImageGraphics2D(ImageInfo info, Graphics2DImagePainter painter) {
        super(info);
        this.setGraphics2DImagePainter(painter);
    }

    @Override
    public ImageFlavor getFlavor() {
        return ImageFlavor.GRAPHICS2D;
    }

    @Override
    public boolean isCacheable() {
        Image img = this.getInfo().getOriginalImage();
        if (img == null) {
            return true;
        }
        return img.isCacheable();
    }

    public Graphics2DImagePainter getGraphics2DImagePainter() {
        return this.painter;
    }

    public void setGraphics2DImagePainter(Graphics2DImagePainter painter) {
        this.painter = painter;
    }
}

