/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.print.PrinterGraphics;
import java.io.InputStream;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.Layer;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.render.RenderingContext;
import org.xhtmlrenderer.simple.XHTMLPanel;
import org.xhtmlrenderer.swing.Java2DOutputDevice;
import org.xhtmlrenderer.swing.ScaleChangeEvent;
import org.xhtmlrenderer.swing.ScaleChangeListener;

public class ScalableXHTMLPanel
extends XHTMLPanel {
    public static final int SCALE_POLICY_NONE = 0;
    public static final int SCALE_POLICY_FIT_WIDTH = 1;
    public static final int SCALE_POLICY_FIT_HEIGHT = 2;
    public static final int SCALE_POLICY_FIT_WHOLE = 3;
    private static final long serialVersionUID = 1L;
    private int scalePolicy = 0;
    private double scale = -1.0;
    private ArrayList scListeners = new ArrayList();
    private Dimension lastLayoutSize = null;

    public ScalableXHTMLPanel() {
    }

    public ScalableXHTMLPanel(UserAgentCallback uac) {
        super(uac);
    }

    @Override
    public void setDocument(Document doc, String url) {
        this.resetScaleAccordingToPolicy();
        this.lastLayoutSize = null;
        super.setDocument(doc, url);
    }

    @Override
    public void setDocument(InputStream stream, String url) throws Exception {
        this.resetScaleAccordingToPolicy();
        this.lastLayoutSize = null;
        super.setDocument(stream, url);
    }

    private void resetScaleAccordingToPolicy() {
        if (this.getScalePolicy() != 0) {
            this.scale = -1.0;
        }
    }

    @Override
    public Box find(int x, int y) {
        Point p = this.convertFromScaled(x, y);
        Layer l = this.getRootLayer();
        if (l != null) {
            return l.find(this.getLayoutContext(), p.x, p.y, false);
        }
        return null;
    }

    public void setScale(double newScale) throws IllegalArgumentException {
        if (newScale <= 0.0) {
            throw new IllegalArgumentException("Only positive scales are allowed.");
        }
        this.scale = newScale;
        this.scalePolicy = 0;
        this.lastLayoutSize = null;
        this.repaint(this.getFixedRectangle());
        this.scaleChanged();
    }

    public double getScale() {
        return this.scale;
    }

    public void addScaleChangeListener(ScaleChangeListener scl) {
        this.scListeners.add(scl);
    }

    public void removeScaleChangeListener(ScaleChangeListener scl) {
        this.scListeners.remove(scl);
    }

    private void scaleChanged() {
        ScaleChangeEvent evt = new ScaleChangeEvent(this, this.scale);
        for (int i = 0; i < this.scListeners.size(); ++i) {
            ScaleChangeListener scl = (ScaleChangeListener)this.scListeners.get(i);
            scl.scaleChanged(evt);
        }
    }

    @Override
    protected void doRender(RenderingContext c, Layer root) {
        Graphics2D g = ((Java2DOutputDevice)c.getOutputDevice()).getGraphics();
        if (!(g instanceof PrinterGraphics) && this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(0, 0, this.getWidth(), this.getHeight());
        }
        AffineTransform current = g.getTransform();
        PaintingInfo pI = root.getMaster().getPaintingInfo();
        Dimension layoutSize = pI.getOuterMarginCorner();
        this.calculateScaleAccordingToPolicy(layoutSize);
        if (this.lastLayoutSize == null) {
            this.lastLayoutSize = layoutSize;
            this.setPreferredSize(new Dimension((int)((double)this.lastLayoutSize.width * this.scale), (int)((double)this.lastLayoutSize.height * this.scale)));
            this.revalidate();
        }
        g.transform(AffineTransform.getScaleInstance(this.scale, this.scale));
        super.doRender(c, root);
        g.setTransform(current);
    }

    protected void calculateScaleAccordingToPolicy(Dimension layoutSize) {
        Rectangle viewportBounds = this.getFixedRectangle();
        if (this.getScalePolicy() == 0) {
            if (this.scale == -1.0) {
                this.scale = 1.0;
            }
            return;
        }
        double xScale = viewportBounds.width < layoutSize.width ? (double)viewportBounds.width / (double)layoutSize.width : 1.0;
        double yScale = viewportBounds.height < layoutSize.height ? (double)viewportBounds.height / (double)layoutSize.height : 1.0;
        this.scale = this.getScalePolicy() == 1 ? xScale : (this.getScalePolicy() == 2 ? yScale : Math.min(xScale, yScale));
    }

    protected Point convertToScaled(Point origin) {
        if (this.scale <= 0.0) {
            return origin;
        }
        return new Point((int)((double)origin.x * this.scale), (int)((double)origin.y * this.scale));
    }

    protected Point convertFromScaled(Point origin) {
        if (this.scale <= 0.0) {
            return origin;
        }
        return new Point((int)((double)origin.x / this.scale), (int)((double)origin.y / this.scale));
    }

    protected Point convertToScaled(int x, int y) {
        if (this.scale <= 0.0) {
            return new Point(x, y);
        }
        return new Point((int)((double)x * this.scale), (int)((double)y * this.scale));
    }

    protected Point convertFromScaled(int x, int y) {
        if (this.scale <= 0.0) {
            return new Point(x, y);
        }
        return new Point((int)((double)x / this.scale), (int)((double)y / this.scale));
    }

    public int getScalePolicy() {
        return this.scalePolicy;
    }

    public void setScalePolicy(int scalePolicy) {
        this.scalePolicy = scalePolicy;
        this.lastLayoutSize = null;
        this.repaint(this.getFixedRectangle());
    }
}

