/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 */
package org.apache.xmlgraphics.java2d;

import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlgraphics.java2d.AbstractGraphicsConfiguration;
import org.apache.xmlgraphics.java2d.GenericGraphicsDevice;
import org.apache.xmlgraphics.java2d.GraphicsConfigurationWithTransparency;

public class GraphicsConfigurationWithoutTransparency
extends AbstractGraphicsConfiguration {
    private static final Log LOG = LogFactory.getLog(GraphicsConfigurationWithoutTransparency.class);
    private static final BufferedImage BI_WITHOUT_ALPHA = new BufferedImage(1, 1, 1);
    private final GraphicsConfigurationWithTransparency defaultDelegate = new GraphicsConfigurationWithTransparency();

    @Override
    public GraphicsDevice getDevice() {
        return new GenericGraphicsDevice(this);
    }

    @Override
    public BufferedImage createCompatibleImage(int width, int height) {
        return this.defaultDelegate.createCompatibleImage(width, height, 1);
    }

    @Override
    public BufferedImage createCompatibleImage(int width, int height, int transparency) {
        if (transparency != 1) {
            LOG.warn((Object)"Does not support transparencies (alpha channels) in images");
        }
        return this.defaultDelegate.createCompatibleImage(width, height, 1);
    }

    @Override
    public ColorModel getColorModel() {
        return BI_WITHOUT_ALPHA.getColorModel();
    }

    @Override
    public ColorModel getColorModel(int transparency) {
        if (transparency == 1) {
            LOG.warn((Object)"Does not support transparencies (alpha channels) in images");
        }
        return this.getColorModel();
    }

    @Override
    public AffineTransform getDefaultTransform() {
        return this.defaultDelegate.getDefaultTransform();
    }

    @Override
    public AffineTransform getNormalizingTransform() {
        return this.defaultDelegate.getNormalizingTransform();
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle();
    }
}

