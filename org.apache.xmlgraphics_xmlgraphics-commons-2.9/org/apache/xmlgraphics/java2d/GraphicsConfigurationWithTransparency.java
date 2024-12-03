/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import org.apache.xmlgraphics.java2d.AbstractGraphicsConfiguration;
import org.apache.xmlgraphics.java2d.GenericGraphicsDevice;

public class GraphicsConfigurationWithTransparency
extends AbstractGraphicsConfiguration {
    private static final BufferedImage BI_WITH_ALPHA = new BufferedImage(1, 1, 2);
    private static final BufferedImage BI_WITHOUT_ALPHA = new BufferedImage(1, 1, 1);

    @Override
    public BufferedImage createCompatibleImage(int width, int height, int transparency) {
        if (transparency == 1) {
            return new BufferedImage(width, height, 1);
        }
        return new BufferedImage(width, height, 2);
    }

    @Override
    public BufferedImage createCompatibleImage(int width, int height) {
        return new BufferedImage(width, height, 2);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle();
    }

    @Override
    public ColorModel getColorModel() {
        return BI_WITH_ALPHA.getColorModel();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        if (transparency == 1) {
            return BI_WITHOUT_ALPHA.getColorModel();
        }
        return BI_WITH_ALPHA.getColorModel();
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return new AffineTransform();
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return new AffineTransform(2.0f, 0.0f, 0.0f, 2.0f, 0.0f, 0.0f);
    }

    @Override
    public GraphicsDevice getDevice() {
        return new GenericGraphicsDevice(this);
    }
}

