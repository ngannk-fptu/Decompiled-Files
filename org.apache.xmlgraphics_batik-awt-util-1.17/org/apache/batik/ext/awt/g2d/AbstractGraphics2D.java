/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.g2d;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.GraphicContext;

public abstract class AbstractGraphics2D
extends Graphics2D
implements Cloneable {
    protected GraphicContext gc;
    protected boolean textAsShapes = false;

    public AbstractGraphics2D(boolean textAsShapes) {
        this.textAsShapes = textAsShapes;
    }

    public AbstractGraphics2D(AbstractGraphics2D g) {
        this.gc = (GraphicContext)g.gc.clone();
        this.gc.validateTransformStack();
        this.textAsShapes = g.textAsShapes;
    }

    @Override
    public void translate(int x, int y) {
        this.gc.translate(x, y);
    }

    @Override
    public Color getColor() {
        return this.gc.getColor();
    }

    @Override
    public void setColor(Color c) {
        this.gc.setColor(c);
    }

    @Override
    public void setPaintMode() {
        this.gc.setComposite(AlphaComposite.SrcOver);
    }

    @Override
    public Font getFont() {
        return this.gc.getFont();
    }

    @Override
    public void setFont(Font font) {
        this.gc.setFont(font);
    }

    @Override
    public Rectangle getClipBounds() {
        return this.gc.getClipBounds();
    }

    @Override
    public void clipRect(int x, int y, int width, int height) {
        this.gc.clipRect(x, y, width, height);
    }

    @Override
    public void setClip(int x, int y, int width, int height) {
        this.gc.setClip(x, y, width, height);
    }

    @Override
    public Shape getClip() {
        return this.gc.getClip();
    }

    @Override
    public void setClip(Shape clip) {
        this.gc.setClip(clip);
    }

    @Override
    public void drawLine(int x1, int y1, int x2, int y2) {
        Line2D.Float line = new Line2D.Float(x1, y1, x2, y2);
        this.draw(line);
    }

    @Override
    public void fillRect(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        this.fill(rect);
    }

    @Override
    public void drawRect(int x, int y, int width, int height) {
        Rectangle rect = new Rectangle(x, y, width, height);
        this.draw(rect);
    }

    @Override
    public void clearRect(int x, int y, int width, int height) {
        Paint paint = this.gc.getPaint();
        this.gc.setColor(this.gc.getBackground());
        this.fillRect(x, y, width, height);
        this.gc.setPaint(paint);
    }

    @Override
    public void drawRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight);
        this.draw(rect);
    }

    @Override
    public void fillRoundRect(int x, int y, int width, int height, int arcWidth, int arcHeight) {
        RoundRectangle2D.Float rect = new RoundRectangle2D.Float(x, y, width, height, arcWidth, arcHeight);
        this.fill(rect);
    }

    @Override
    public void drawOval(int x, int y, int width, int height) {
        Ellipse2D.Float oval = new Ellipse2D.Float(x, y, width, height);
        this.draw(oval);
    }

    @Override
    public void fillOval(int x, int y, int width, int height) {
        Ellipse2D.Float oval = new Ellipse2D.Float(x, y, width, height);
        this.fill(oval);
    }

    @Override
    public void drawArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        Arc2D.Float arc = new Arc2D.Float(x, y, width, height, startAngle, arcAngle, 0);
        this.draw(arc);
    }

    @Override
    public void fillArc(int x, int y, int width, int height, int startAngle, int arcAngle) {
        Arc2D.Float arc = new Arc2D.Float(x, y, width, height, startAngle, arcAngle, 2);
        this.fill(arc);
    }

    @Override
    public void drawPolyline(int[] xPoints, int[] yPoints, int nPoints) {
        if (nPoints > 0) {
            GeneralPath path = new GeneralPath();
            path.moveTo(xPoints[0], yPoints[0]);
            for (int i = 1; i < nPoints; ++i) {
                path.lineTo(xPoints[i], yPoints[i]);
            }
            this.draw(path);
        }
    }

    @Override
    public void drawPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        this.draw(polygon);
    }

    @Override
    public void fillPolygon(int[] xPoints, int[] yPoints, int nPoints) {
        Polygon polygon = new Polygon(xPoints, yPoints, nPoints);
        this.fill(polygon);
    }

    @Override
    public void drawString(String str, int x, int y) {
        this.drawString(str, (float)x, (float)y);
    }

    @Override
    public void drawString(AttributedCharacterIterator iterator, int x, int y) {
        this.drawString(iterator, (float)x, (float)y);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, Color bgcolor, ImageObserver observer) {
        return this.drawImage(img, x, y, img.getWidth(null), img.getHeight(null), bgcolor, observer);
    }

    @Override
    public boolean drawImage(Image img, int x, int y, int width, int height, Color bgcolor, ImageObserver observer) {
        Paint paint = this.gc.getPaint();
        this.gc.setPaint(bgcolor);
        this.fillRect(x, y, width, height);
        this.gc.setPaint(paint);
        this.drawImage(img, x, y, width, height, observer);
        return true;
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, ImageObserver observer) {
        BufferedImage src = new BufferedImage(img.getWidth(null), img.getHeight(null), 2);
        Graphics2D g = src.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        src = src.getSubimage(sx1, sy1, sx2 - sx1, sy2 - sy1);
        return this.drawImage(src, dx1, dy1, dx2 - dx1, dy2 - dy1, observer);
    }

    @Override
    public boolean drawImage(Image img, int dx1, int dy1, int dx2, int dy2, int sx1, int sy1, int sx2, int sy2, Color bgcolor, ImageObserver observer) {
        Paint paint = this.gc.getPaint();
        this.gc.setPaint(bgcolor);
        this.fillRect(dx1, dy1, dx2 - dx1, dy2 - dy1);
        this.gc.setPaint(paint);
        return this.drawImage(img, dx1, dy1, dx2, dy2, sx1, sy1, sx2, sy2, observer);
    }

    @Override
    public boolean drawImage(Image img, AffineTransform xform, ImageObserver obs) {
        boolean retVal = true;
        if (xform.getDeterminant() != 0.0) {
            AffineTransform inverseTransform = null;
            try {
                inverseTransform = xform.createInverse();
            }
            catch (NoninvertibleTransformException e) {
                throw new RuntimeException(e.getMessage());
            }
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.transform(inverseTransform);
        } else {
            AffineTransform savTransform = new AffineTransform(this.gc.getTransform());
            this.gc.transform(xform);
            retVal = this.drawImage(img, 0, 0, null);
            this.gc.setTransform(savTransform);
        }
        return retVal;
    }

    @Override
    public void drawImage(BufferedImage img, BufferedImageOp op, int x, int y) {
        img = op.filter(img, null);
        this.drawImage((Image)img, x, y, null);
    }

    @Override
    public void drawGlyphVector(GlyphVector g, float x, float y) {
        Shape glyphOutline = g.getOutline(x, y);
        this.fill(glyphOutline);
    }

    @Override
    public boolean hit(Rectangle rect, Shape s, boolean onStroke) {
        if (onStroke) {
            s = this.gc.getStroke().createStrokedShape(s);
        }
        s = this.gc.getTransform().createTransformedShape(s);
        return s.intersects(rect);
    }

    @Override
    public void setComposite(Composite comp) {
        this.gc.setComposite(comp);
    }

    @Override
    public void setPaint(Paint paint) {
        this.gc.setPaint(paint);
    }

    @Override
    public void setStroke(Stroke s) {
        this.gc.setStroke(s);
    }

    @Override
    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.gc.setRenderingHint(hintKey, hintValue);
    }

    @Override
    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.gc.getRenderingHint(hintKey);
    }

    public void setRenderingHints(Map hints) {
        this.gc.setRenderingHints(hints);
    }

    public void addRenderingHints(Map hints) {
        this.gc.addRenderingHints(hints);
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.gc.getRenderingHints();
    }

    @Override
    public void translate(double tx, double ty) {
        this.gc.translate(tx, ty);
    }

    @Override
    public void rotate(double theta) {
        this.gc.rotate(theta);
    }

    @Override
    public void rotate(double theta, double x, double y) {
        this.gc.rotate(theta, x, y);
    }

    @Override
    public void scale(double sx, double sy) {
        this.gc.scale(sx, sy);
    }

    @Override
    public void shear(double shx, double shy) {
        this.gc.shear(shx, shy);
    }

    @Override
    public void transform(AffineTransform Tx) {
        this.gc.transform(Tx);
    }

    @Override
    public void setTransform(AffineTransform Tx) {
        this.gc.setTransform(Tx);
    }

    @Override
    public AffineTransform getTransform() {
        return this.gc.getTransform();
    }

    @Override
    public Paint getPaint() {
        return this.gc.getPaint();
    }

    @Override
    public Composite getComposite() {
        return this.gc.getComposite();
    }

    @Override
    public void setBackground(Color color) {
        this.gc.setBackground(color);
    }

    @Override
    public Color getBackground() {
        return this.gc.getBackground();
    }

    @Override
    public Stroke getStroke() {
        return this.gc.getStroke();
    }

    @Override
    public void clip(Shape s) {
        this.gc.clip(s);
    }

    @Override
    public FontRenderContext getFontRenderContext() {
        return this.gc.getFontRenderContext();
    }

    public GraphicContext getGraphicContext() {
        return this.gc;
    }
}

