/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.g2d;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.NoninvertibleTransformException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.batik.ext.awt.g2d.TransformStackElement;

public class GraphicContext
implements Cloneable {
    protected AffineTransform defaultTransform = new AffineTransform();
    protected AffineTransform transform = new AffineTransform();
    protected List transformStack = new ArrayList();
    protected boolean transformStackValid = true;
    protected Paint paint = Color.black;
    protected Stroke stroke = new BasicStroke();
    protected Composite composite = AlphaComposite.SrcOver;
    protected Shape clip = null;
    protected RenderingHints hints = new RenderingHints(null);
    protected Font font = new Font("sanserif", 0, 12);
    protected Color background = new Color(0, 0, 0, 0);
    protected Color foreground = Color.black;

    public GraphicContext() {
        this.hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
    }

    public GraphicContext(AffineTransform defaultDeviceTransform) {
        this();
        this.defaultTransform = new AffineTransform(defaultDeviceTransform);
        this.transform = new AffineTransform(this.defaultTransform);
        if (!this.defaultTransform.isIdentity()) {
            this.transformStack.add(TransformStackElement.createGeneralTransformElement(this.defaultTransform));
        }
    }

    public Object clone() {
        GraphicContext copyGc = new GraphicContext(this.defaultTransform);
        copyGc.transform = new AffineTransform(this.transform);
        copyGc.transformStack = new ArrayList(this.transformStack.size());
        for (int i = 0; i < this.transformStack.size(); ++i) {
            TransformStackElement stackElement = (TransformStackElement)this.transformStack.get(i);
            copyGc.transformStack.add(stackElement.clone());
        }
        copyGc.transformStackValid = this.transformStackValid;
        copyGc.paint = this.paint;
        copyGc.stroke = this.stroke;
        copyGc.composite = this.composite;
        copyGc.clip = this.clip != null ? new GeneralPath(this.clip) : null;
        copyGc.hints = (RenderingHints)this.hints.clone();
        copyGc.font = this.font;
        copyGc.background = this.background;
        copyGc.foreground = this.foreground;
        return copyGc;
    }

    public Color getColor() {
        return this.foreground;
    }

    public void setColor(Color c) {
        if (c == null) {
            return;
        }
        if (this.paint != c) {
            this.setPaint(c);
        }
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        if (font != null) {
            this.font = font;
        }
    }

    public Rectangle getClipBounds() {
        Shape c = this.getClip();
        if (c == null) {
            return null;
        }
        return c.getBounds();
    }

    public void clipRect(int x, int y, int width, int height) {
        this.clip(new Rectangle(x, y, width, height));
    }

    public void setClip(int x, int y, int width, int height) {
        this.setClip(new Rectangle(x, y, width, height));
    }

    public Shape getClip() {
        try {
            return this.transform.createInverse().createTransformedShape(this.clip);
        }
        catch (NoninvertibleTransformException e) {
            return null;
        }
    }

    public void setClip(Shape clip) {
        this.clip = clip != null ? this.transform.createTransformedShape(clip) : null;
    }

    public void setComposite(Composite comp) {
        this.composite = comp;
    }

    public void setPaint(Paint paint) {
        if (paint == null) {
            return;
        }
        this.paint = paint;
        if (paint instanceof Color) {
            this.foreground = (Color)paint;
        }
    }

    public void setStroke(Stroke s) {
        this.stroke = s;
    }

    public void setRenderingHint(RenderingHints.Key hintKey, Object hintValue) {
        this.hints.put(hintKey, hintValue);
    }

    public Object getRenderingHint(RenderingHints.Key hintKey) {
        return this.hints.get(hintKey);
    }

    public void setRenderingHints(Map hints) {
        this.hints = new RenderingHints(hints);
    }

    public void addRenderingHints(Map hints) {
        this.hints.putAll((Map<?, ?>)hints);
    }

    public RenderingHints getRenderingHints() {
        return this.hints;
    }

    public void translate(int x, int y) {
        if (x != 0 || y != 0) {
            this.transform.translate(x, y);
            this.transformStack.add(TransformStackElement.createTranslateElement(x, y));
        }
    }

    public void translate(double tx, double ty) {
        this.transform.translate(tx, ty);
        this.transformStack.add(TransformStackElement.createTranslateElement(tx, ty));
    }

    public void rotate(double theta) {
        this.transform.rotate(theta);
        this.transformStack.add(TransformStackElement.createRotateElement(theta));
    }

    public void rotate(double theta, double x, double y) {
        this.transform.rotate(theta, x, y);
        this.transformStack.add(TransformStackElement.createTranslateElement(x, y));
        this.transformStack.add(TransformStackElement.createRotateElement(theta));
        this.transformStack.add(TransformStackElement.createTranslateElement(-x, -y));
    }

    public void scale(double sx, double sy) {
        this.transform.scale(sx, sy);
        this.transformStack.add(TransformStackElement.createScaleElement(sx, sy));
    }

    public void shear(double shx, double shy) {
        this.transform.shear(shx, shy);
        this.transformStack.add(TransformStackElement.createShearElement(shx, shy));
    }

    public void transform(AffineTransform Tx) {
        this.transform.concatenate(Tx);
        this.transformStack.add(TransformStackElement.createGeneralTransformElement(Tx));
    }

    public void setTransform(AffineTransform Tx) {
        this.transform = new AffineTransform(Tx);
        this.invalidateTransformStack();
        if (!Tx.isIdentity()) {
            this.transformStack.add(TransformStackElement.createGeneralTransformElement(Tx));
        }
    }

    public void validateTransformStack() {
        this.transformStackValid = true;
    }

    public boolean isTransformStackValid() {
        return this.transformStackValid;
    }

    public TransformStackElement[] getTransformStack() {
        TransformStackElement[] stack = new TransformStackElement[this.transformStack.size()];
        this.transformStack.toArray(stack);
        return stack;
    }

    protected void invalidateTransformStack() {
        this.transformStack.clear();
        this.transformStackValid = false;
    }

    public AffineTransform getTransform() {
        return new AffineTransform(this.transform);
    }

    public Paint getPaint() {
        return this.paint;
    }

    public Composite getComposite() {
        return this.composite;
    }

    public void setBackground(Color color) {
        if (color == null) {
            return;
        }
        this.background = color;
    }

    public Color getBackground() {
        return this.background;
    }

    public Stroke getStroke() {
        return this.stroke;
    }

    public void clip(Shape s) {
        if (s != null) {
            s = this.transform.createTransformedShape(s);
        }
        if (this.clip != null) {
            Area newClip = new Area(this.clip);
            newClip.intersect(new Area(s));
            this.clip = new GeneralPath(newClip);
        } else {
            this.clip = s;
        }
    }

    public FontRenderContext getFontRenderContext() {
        Object antialiasingHint = this.hints.get(RenderingHints.KEY_TEXT_ANTIALIASING);
        boolean isAntialiased = true;
        if (antialiasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_ON && antialiasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_DEFAULT) {
            if (antialiasingHint != RenderingHints.VALUE_TEXT_ANTIALIAS_OFF) {
                antialiasingHint = this.hints.get(RenderingHints.KEY_ANTIALIASING);
                if (antialiasingHint != RenderingHints.VALUE_ANTIALIAS_ON && antialiasingHint != RenderingHints.VALUE_ANTIALIAS_DEFAULT && antialiasingHint == RenderingHints.VALUE_ANTIALIAS_OFF) {
                    isAntialiased = false;
                }
            } else {
                isAntialiased = false;
            }
        }
        boolean useFractionalMetrics = true;
        if (this.hints.get(RenderingHints.KEY_FRACTIONALMETRICS) == RenderingHints.VALUE_FRACTIONALMETRICS_OFF) {
            useFractionalMetrics = false;
        }
        FontRenderContext frc = new FontRenderContext(this.defaultTransform, isAntialiased, useFractionalMetrics);
        return frc;
    }
}

