/*
 * Decompiled with CFR 0.152.
 */
package org.apache.pdfbox.rendering;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

class GroupGraphics
extends Graphics2D {
    private final BufferedImage groupImage;
    private final BufferedImage groupAlphaImage;
    private final Graphics2D groupG2D;
    private final Graphics2D alphaG2D;

    GroupGraphics(BufferedImage groupImage, Graphics2D groupGraphics) {
        this.groupImage = groupImage;
        this.groupG2D = groupGraphics;
        this.groupAlphaImage = new BufferedImage(groupImage.getWidth(), groupImage.getHeight(), 2);
        this.alphaG2D = this.groupAlphaImage.createGraphics();
    }

    private GroupGraphics(BufferedImage groupImage, Graphics2D groupGraphics, BufferedImage groupAlphaImage, Graphics2D alphaGraphics) {
        this.groupImage = groupImage;
        this.groupG2D = groupGraphics;
        this.groupAlphaImage = groupAlphaImage;
        this.alphaG2D = alphaGraphics;
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        this.groupG2D.clearRect(x, y, width, height);
        this.alphaG2D.clearRect(x, y, width, height);
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        this.groupG2D.clipRect(x, y, width, height);
        this.alphaG2D.clipRect(x, y, width, height);
    }

    @Override
    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        this.groupG2D.copyArea(x, y, width, height, dx, dy);
        this.alphaG2D.copyArea(x, y, width, height, dx, dy);
    }

    @Override
    public Graphics create() {
        Graphics g = this.groupG2D.create();
        Graphics a = this.alphaG2D.create();
        if (g instanceof Graphics2D && a instanceof Graphics2D) {
            return new GroupGraphics(this.groupImage, (Graphics2D)g, this.groupAlphaImage, (Graphics2D)a);
        }
        g.dispose();
        a.dispose();
        throw new UnsupportedOperationException("Only Graphics2D supported by this method");
    }

    @Override
    public void dispose() {
        this.groupG2D.dispose();
        this.alphaG2D.dispose();
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.groupG2D.drawArc(x, y, width, height, startAngle, arcAngle);
        this.alphaG2D.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, bgcolor, observer);
        return this.alphaG2D.drawImage(img, x, y, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, observer);
        return this.alphaG2D.drawImage(img, x, y, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, width, height, bgcolor, observer);
        return this.alphaG2D.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        this.groupG2D.drawImage(img, x, y, width, height, observer);
        return this.alphaG2D.drawImage(img, x, y, width, height, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        this.groupG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
        return this.alphaG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        this.groupG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
        return this.alphaG2D.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        this.groupG2D.drawLine(x1, y1, x2, y2);
        this.alphaG2D.drawLine(x1, y1, x2, y2);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        this.groupG2D.drawOval(x, y, width, height);
        this.alphaG2D.drawOval(x, y, width, height);
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        this.groupG2D.drawPolygon(xPoints, yPoints, nPoints);
        this.alphaG2D.drawPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        this.groupG2D.drawPolyline(xPoints, yPoints, nPoints);
        this.alphaG2D.drawPolyline(xPoints, yPoints, nPoints);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.groupG2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
        this.alphaG2D.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.groupG2D.drawString(iterator, x, y);
        this.alphaG2D.drawString(iterator, x, y);
    }

    @Override
    public void drawString(String str, int x, int y) {
        this.groupG2D.drawString(str, x, y);
        this.alphaG2D.drawString(str, x, y);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.groupG2D.fillArc(x, y, width, height, startAngle, arcAngle);
        this.alphaG2D.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        this.groupG2D.fillOval(x, y, width, height);
        this.alphaG2D.fillOval(x, y, width, height);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        this.groupG2D.fillPolygon(xPoints, yPoints, nPoints);
        this.alphaG2D.fillPolygon(xPoints, yPoints, nPoints);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        this.groupG2D.fillRect(x, y, width, height);
        this.alphaG2D.fillRect(x, y, width, height);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.groupG2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
        this.alphaG2D.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    @Override
    public Shape getClip() {
        return this.groupG2D.getClip();
    }

    @Override
    public Rectangle getClipBounds() {
        return this.groupG2D.getClipBounds();
    }

    @Override
    public Color getColor() {
        return this.groupG2D.getColor();
    }

    @Override
    public Font getFont() {
        return this.groupG2D.getFont();
    }

    @Override
    public FontMetrics getFontMetrics(Font f) {
        return this.groupG2D.getFontMetrics(f);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        this.groupG2D.setClip(x, y, width, height);
        this.alphaG2D.setClip(x, y, width, height);
    }

    @Override
    public void setClip(Shape clip) {
        this.groupG2D.setClip(clip);
        this.alphaG2D.setClip(clip);
    }

    @Override
    public void setColor(Color c) {
        this.groupG2D.setColor(c);
        this.alphaG2D.setColor(c);
    }

    @Override
    public void setFont(Font font) {
        this.groupG2D.setFont(font);
        this.alphaG2D.setFont(font);
    }

    @Override
    public void setPaintMode() {
        this.groupG2D.setPaintMode();
        this.alphaG2D.setPaintMode();
    }

    @Override
    public void setXORMode(Color c1) {
        this.groupG2D.setXORMode(c1);
        this.alphaG2D.setXORMode(c1);
    }

    @Override
    public void translate(int x, int y) {
        this.groupG2D.translate(x, y);
        this.alphaG2D.translate(x, y);
    }

    @Override
    public void addRenderingHints(Map<?, ?> hints) {
        this.groupG2D.addRenderingHints(hints);
        this.alphaG2D.addRenderingHints(hints);
    }

    @Override
    public void clip(Shape s) {
        this.groupG2D.clip(s);
        this.alphaG2D.clip(s);
    }

    @Override
    public void draw(Shape s) {
        this.groupG2D.draw(s);
        this.alphaG2D.draw(s);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        this.groupG2D.drawGlyphVector(g, x, y);
        this.alphaG2D.drawGlyphVector(g, x, y);
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        this.groupG2D.drawImage(img, op, x, y);
        this.alphaG2D.drawImage(img, op, x, y);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        this.groupG2D.drawImage(img, xform, obs);
        return this.alphaG2D.drawImage(img, xform, obs);
    }

    @Override
    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        this.groupG2D.drawRenderableImage(img, xform);
        this.alphaG2D.drawRenderableImage(img, xform);
    }

    @Override
    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        this.groupG2D.drawRenderedImage(img, xform);
        this.alphaG2D.drawRenderedImage(img, xform);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        this.groupG2D.drawString(iterator, x, y);
        this.alphaG2D.drawString(iterator, x, y);
    }

    @Override
    public void drawString(String str, float x, float y) {
        this.groupG2D.drawString(str, x, y);
        this.alphaG2D.drawString(str, x, y);
    }

    @Override
    public void fill(Shape s) {
        this.groupG2D.fill(s);
        this.alphaG2D.fill(s);
    }

    @Override
    public Color getBackground() {
        return this.groupG2D.getBackground();
    }

    @Override
    public Composite getComposite() {
        return this.groupG2D.getComposite();
    }

    @Override
    public GraphicsConfiguration getDeviceConfiguration() {
        return this.groupG2D.getDeviceConfiguration();
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return this.groupG2D.getFontRenderContext();
    }

    @Override
    public Paint getPaint() {
        return this.groupG2D.getPaint();
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.groupG2D.getRenderingHint(hintKey);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.groupG2D.getRenderingHints();
    }

    @Override
    public Stroke getStroke() {
        return this.groupG2D.getStroke();
    }

    @Override
    public AffineTransform getTransform() {
        return this.groupG2D.getTransform();
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return this.groupG2D.hit(rect, s, onStroke);
    }

    @Override
    public void rotate(double theta) {
        this.groupG2D.rotate(theta);
        this.alphaG2D.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        this.groupG2D.rotate(theta, x, y);
        this.alphaG2D.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        this.groupG2D.scale(sx, sy);
        this.alphaG2D.scale(sx, sy);
    }

    @Override
    public void setBackground(Color color) {
        this.groupG2D.setBackground(color);
        this.alphaG2D.setBackground(color);
    }

    @Override
    public void setComposite(Composite comp) {
        this.groupG2D.setComposite(comp);
        this.alphaG2D.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        this.groupG2D.setPaint(paint);
        this.alphaG2D.setPaint(paint);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.groupG2D.setRenderingHint(hintKey, hintValue);
        this.alphaG2D.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public void setRenderingHints(Map<?, ?> hints) {
        this.groupG2D.setRenderingHints(hints);
        this.alphaG2D.setRenderingHints(hints);
    }

    @Override
    public void setStroke(Stroke s) {
        this.groupG2D.setStroke(s);
        this.alphaG2D.setStroke(s);
    }

    @Override
    public void setTransform(AffineTransform tx) {
        this.groupG2D.setTransform(tx);
        this.alphaG2D.setTransform(tx);
    }

    @Override
    public void shear(double shx, double shy) {
        this.groupG2D.shear(shx, shy);
        this.alphaG2D.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform tx) {
        this.groupG2D.transform(tx);
        this.alphaG2D.transform(tx);
    }

    @Override
    public void translate(double tx, double ty) {
        this.groupG2D.translate(tx, ty);
        this.alphaG2D.translate(tx, ty);
    }

    void removeBackdrop(BufferedImage backdrop, int offsetX, int offsetY) {
        int groupWidth = this.groupImage.getWidth();
        int groupHeight = this.groupImage.getHeight();
        int backdropWidth = backdrop.getWidth();
        int backdropHeight = backdrop.getHeight();
        int groupType = this.groupImage.getType();
        int groupAlphaType = this.groupAlphaImage.getType();
        int backdropType = backdrop.getType();
        DataBuffer groupDataBuffer = this.groupImage.getRaster().getDataBuffer();
        DataBuffer groupAlphaDataBuffer = this.groupAlphaImage.getRaster().getDataBuffer();
        DataBuffer backdropDataBuffer = backdrop.getRaster().getDataBuffer();
        if (groupType == 2 && groupAlphaType == 2 && (backdropType == 2 || backdropType == 1) && groupDataBuffer instanceof DataBufferInt && groupAlphaDataBuffer instanceof DataBufferInt && backdropDataBuffer instanceof DataBufferInt) {
            int[] groupData = ((DataBufferInt)groupDataBuffer).getData();
            int[] groupAlphaData = ((DataBufferInt)groupAlphaDataBuffer).getData();
            int[] backdropData = ((DataBufferInt)backdropDataBuffer).getData();
            boolean backdropHasAlpha = backdropType == 2;
            for (int y = 0; y < groupHeight; ++y) {
                for (int x = 0; x < groupWidth; ++x) {
                    float alpha0;
                    int backdropRGB;
                    int index = x + y * groupWidth;
                    int alphagn = groupAlphaData[index] >> 24 & 0xFF;
                    if (alphagn == 0) {
                        groupData[index] = 0;
                        continue;
                    }
                    int backdropX = x + offsetX;
                    int backdropY = y + offsetY;
                    if (backdropX >= 0 && backdropX < backdropWidth && backdropY >= 0 && backdropY < backdropHeight) {
                        backdropRGB = backdropData[backdropX + backdropY * backdropWidth];
                        alpha0 = backdropHasAlpha ? (float)(backdropRGB >> 24 & 0xFF) : 255.0f;
                    } else {
                        backdropRGB = 0;
                        alpha0 = 0.0f;
                    }
                    float alphaFactor = alpha0 / (float)alphagn - alpha0 / 255.0f;
                    int groupRGB = groupData[index];
                    int r = this.backdropRemoval(groupRGB, backdropRGB, 16, alphaFactor);
                    int g = this.backdropRemoval(groupRGB, backdropRGB, 8, alphaFactor);
                    int b = this.backdropRemoval(groupRGB, backdropRGB, 0, alphaFactor);
                    groupData[index] = alphagn << 24 | r << 16 | g << 8 | b;
                }
            }
        } else {
            for (int y = 0; y < groupHeight; ++y) {
                for (int x = 0; x < groupWidth; ++x) {
                    float alpha0;
                    int backdropRGB;
                    int alphagn = this.groupAlphaImage.getRGB(x, y) >> 24 & 0xFF;
                    if (alphagn == 0) {
                        this.groupImage.setRGB(x, y, 0);
                        continue;
                    }
                    int backdropX = x + offsetX;
                    int backdropY = y + offsetY;
                    if (backdropX >= 0 && backdropX < backdropWidth && backdropY >= 0 && backdropY < backdropHeight) {
                        backdropRGB = backdrop.getRGB(backdropX, backdropY);
                        alpha0 = backdropRGB >> 24 & 0xFF;
                    } else {
                        backdropRGB = 0;
                        alpha0 = 0.0f;
                    }
                    int groupRGB = this.groupImage.getRGB(x, y);
                    float alphaFactor = alpha0 / (float)alphagn - alpha0 / 255.0f;
                    int r = this.backdropRemoval(groupRGB, backdropRGB, 16, alphaFactor);
                    int g = this.backdropRemoval(groupRGB, backdropRGB, 8, alphaFactor);
                    int b = this.backdropRemoval(groupRGB, backdropRGB, 0, alphaFactor);
                    this.groupImage.setRGB(x, y, alphagn << 24 | r << 16 | g << 8 | b);
                }
            }
        }
    }

    private int backdropRemoval(int groupRGB, int backdropRGB, int shift, float alphaFactor) {
        float cn = groupRGB >> shift & 0xFF;
        float c0 = backdropRGB >> shift & 0xFF;
        int c = Math.round(cn + (cn - c0) * alphaFactor);
        return c < 0 ? 0 : (c > 255 ? 255 : c);
    }
}

