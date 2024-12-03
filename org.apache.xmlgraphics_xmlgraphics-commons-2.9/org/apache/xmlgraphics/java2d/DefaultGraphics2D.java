/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.java2d;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import org.apache.xmlgraphics.java2d.AbstractGraphics2D;

public class DefaultGraphics2D
extends AbstractGraphics2D {
    private Graphics2D fmg;

    public DefaultGraphics2D(boolean textAsShapes) {
        super(textAsShapes);
        BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
    }

    public DefaultGraphics2D(DefaultGraphics2D g) {
        super(g);
        BufferedImage bi = new BufferedImage(1, 1, 2);
        this.fmg = bi.createGraphics();
    }

    @Override
    public Graphics create() {
        return new DefaultGraphics2D(this);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        System.err.println("drawImage");
        return true;
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        System.out.println("drawImage");
        return true;
    }

    @Override
    public void dispose() {
        System.out.println("dispose");
    }

    @Override
    public void draw(Shape s) {
        System.out.println("draw(Shape)");
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        System.out.println("drawRenderedImage");
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        System.out.println("drawRenderableImage");
    }

    @Override
    public void drawString(String s, float x, float y) {
        System.out.println("drawString(String)");
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        System.err.println("drawString(AttributedCharacterIterator)");
    }

    @Override
    public void fill(Shape s) {
        System.err.println("fill");
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        System.out.println("getDeviceConviguration");
        return null;
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.fmg.getFontMetrics(f);
    }

    @Override
    public void setXORMode(Color c1) {
        System.out.println("setXORMode");
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        System.out.println("copyArea");
    }
}

