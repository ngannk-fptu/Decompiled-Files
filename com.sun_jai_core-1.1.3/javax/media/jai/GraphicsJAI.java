/*
 * Decompiled with CFR 0.152.
 */
package javax.media.jai;

import java.awt.Color;
import java.awt.Component;
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
import java.awt.image.ImageObserver;
import java.awt.image.RenderedImage;
import java.awt.image.renderable.RenderableImage;
import java.text.AttributedCharacterIterator;
import java.util.Map;

public class GraphicsJAI
extends Graphics2D {
    Graphics2D g;
    Component component;

    protected GraphicsJAI(Graphics2D g, Component component) {
        this.g = g;
        this.component = component;
    }

    public static GraphicsJAI createGraphicsJAI(Graphics2D g, Component component) {
        return new GraphicsJAI(g, component);
    }

    public Graphics create() {
        return new GraphicsJAI(this.g, this.component);
    }

    public Color getColor() {
        return this.g.getColor();
    }

    public void setColor(Color c) {
        this.g.setColor(c);
    }

    public void setPaintMode() {
        this.g.setPaintMode();
    }

    public void setXORMode(Color c1) {
        this.g.setXORMode(c1);
    }

    public Font getFont() {
        return this.g.getFont();
    }

    public void setFont(Font font) {
        this.g.setFont(font);
    }

    public FontMetrics getFontMetrics(Font f) {
        return this.g.getFontMetrics(f);
    }

    public Rectangle getClipBounds() {
        return this.g.getClipBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        this.g.clipRect(x, y, width, height);
    }

    public void setClip(int x, int y, int width, int height) {
        this.g.setClip(x, y, width, height);
    }

    public Shape getClip() {
        return this.g.getClip();
    }

    public void setClip(Shape clip) {
        this.g.setClip(clip);
    }

    public void copyArea(int x, int y, int width, int height, int dx, int dy) {
        this.g.copyArea(x, y, width, height, dx, dy);
    }

    public void drawLine(int x1, int y1, int x2, int y2) {
        this.g.drawLine(x1, y1, x2, y2);
    }

    public void fillRect(int x, int y, int width, int height) {
        this.g.fillRect(x, y, width, height);
    }

    public void clearRect(int x, int y, int width, int height) {
        this.g.clearRect(x, y, width, height);
    }

    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.g.drawRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        this.g.fillRoundRect(x, y, width, height, arcWidth, arcHeight);
    }

    public void drawOval(int x, int y, int width, int height) {
        this.g.drawOval(x, y, width, height);
    }

    public void fillOval(int x, int y, int width, int height) {
        this.g.fillOval(x, y, width, height);
    }

    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.g.drawArc(x, y, width, height, startAngle, arcAngle);
    }

    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        this.g.fillArc(x, y, width, height, startAngle, arcAngle);
    }

    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        this.g.drawPolyline(xPoints, yPoints, nPoints);
    }

    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        this.g.drawPolygon(xPoints, yPoints, nPoints);
    }

    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        this.g.fillPolygon(xPoints, yPoints, nPoints);
    }

    public boolean drawImage(Image img, int x, int y, ImageObserver observer) {
        return this.g.drawImage(img, x, y, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, ImageObserver observer) {
        return this.g.drawImage(img, x, y, width, height, observer);
    }

    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return this.g.drawImage(img, x, y, bgcolor, observer);
    }

    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        return this.g.drawImage(img, x, y, width, height, bgcolor, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        return this.g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        return this.g.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, bgcolor, observer);
    }

    public void dispose() {
        this.g.dispose();
    }

    public void draw(Shape s) {
        this.g.draw(s);
    }

    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        return this.g.drawImage(img, xform, obs);
    }

    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        this.g.drawImage(img, op, x, y);
    }

    public void drawRenderedImage(RenderedImage img, AffineTransform xform) {
        this.g.drawRenderedImage(img, xform);
    }

    public void drawRenderableImage(RenderableImage img, AffineTransform xform) {
        this.g.drawRenderableImage(img, xform);
    }

    public void drawString(String str, int x, int y) {
        this.g.drawString(str, x, y);
    }

    public void drawString(String s, float x, float y) {
        this.g.drawString(s, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.g.drawString(iterator, x, y);
    }

    public void drawString(AttributedCharacterIterator iterator, float x, float y) {
        this.g.drawString(iterator, x, y);
    }

    public void drawGlyphVector(GlyphVector g, float x, float y) {
        this.g.drawGlyphVector(g, x, y);
    }

    public void fill(Shape s) {
        this.g.fill(s);
    }

    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        return this.g.hit(rect, s, onStroke);
    }

    public GraphicsConfiguration getDeviceConfiguration() {
        return this.g.getDeviceConfiguration();
    }

    public void setComposite(Composite comp) {
        this.g.setComposite(comp);
    }

    public void setPaint(Paint paint) {
        this.g.setPaint(paint);
    }

    public void setStroke(Stroke s) {
        this.g.setStroke(s);
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.g.setRenderingHint(hintKey, hintValue);
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.g.getRenderingHint(hintKey);
    }

    public void setRenderingHints(Map hints) {
        this.g.setRenderingHints(hints);
    }

    public void addRenderingHints(Map hints) {
        this.g.addRenderingHints(hints);
    }

    public RenderingHints getRenderingHints() {
        return this.g.getRenderingHints();
    }

    public void translate(int x, int y) {
        this.g.translate(x, y);
    }

    public void translate(double tx, double ty) {
        this.g.translate(tx, ty);
    }

    public void rotate(double theta) {
        this.g.rotate(theta);
    }

    public void rotate(double theta, double x, double y) {
        this.g.rotate(theta, x, y);
    }

    public void scale(double sx, double sy) {
        this.g.scale(sx, sy);
    }

    public void shear(double shx, double shy) {
        this.g.shear(shx, shy);
    }

    public void transform(AffineTransform Tx) {
        this.g.transform(Tx);
    }

    public void setTransform(AffineTransform Tx) {
        this.g.setTransform(Tx);
    }

    public AffineTransform getTransform() {
        return this.g.getTransform();
    }

    public Paint getPaint() {
        return this.g.getPaint();
    }

    public Composite getComposite() {
        return this.g.getComposite();
    }

    public void setBackground(Color color) {
        this.g.setBackground(color);
    }

    public Color getBackground() {
        return this.g.getBackground();
    }

    public Stroke getStroke() {
        return this.g.getStroke();
    }

    public void clip(Shape s) {
        this.g.clip(s);
    }

    public FontRenderContext getFontRenderContext() {
        return this.g.getFontRenderContext();
    }
}

