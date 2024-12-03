/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.RectListManager
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.gvt.renderer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Iterator;
import org.apache.batik.ext.awt.geom.RectListManager;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.util.HaltingThread;

public class MacRenderer
implements ImageRenderer {
    static final int COPY_OVERHEAD = 1000;
    static final int COPY_LINE_OVERHEAD = 10;
    static final AffineTransform IDENTITY = new AffineTransform();
    protected RenderingHints renderingHints = new RenderingHints(null);
    protected AffineTransform usr2dev;
    protected GraphicsNode rootGN;
    protected int offScreenWidth;
    protected int offScreenHeight;
    protected boolean isDoubleBuffered;
    protected BufferedImage currImg;
    protected BufferedImage workImg;
    protected RectListManager damagedAreas;
    public static int IMAGE_TYPE = 3;
    public static Color TRANSPARENT_WHITE = new Color(255, 255, 255, 0);
    protected static RenderingHints defaultRenderingHints = new RenderingHints(null);

    public MacRenderer() {
        this.renderingHints.add(defaultRenderingHints);
        this.usr2dev = new AffineTransform();
    }

    public MacRenderer(RenderingHints rh, AffineTransform at) {
        this.renderingHints.add(rh);
        this.usr2dev = at == null ? new AffineTransform() : new AffineTransform(at);
    }

    @Override
    public void dispose() {
        this.rootGN = null;
        this.currImg = null;
        this.workImg = null;
        this.renderingHints = null;
        this.usr2dev = null;
        if (this.damagedAreas != null) {
            this.damagedAreas.clear();
        }
        this.damagedAreas = null;
    }

    @Override
    public void setTree(GraphicsNode treeRoot) {
        this.rootGN = treeRoot;
    }

    @Override
    public GraphicsNode getTree() {
        return this.rootGN;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void setTransform(AffineTransform usr2dev) {
        this.usr2dev = usr2dev == null ? new AffineTransform() : new AffineTransform(usr2dev);
        if (this.workImg == null) {
            return;
        }
        BufferedImage bufferedImage = this.workImg;
        synchronized (bufferedImage) {
            Graphics2D g2d = this.workImg.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, this.workImg.getWidth(), this.workImg.getHeight());
            g2d.dispose();
        }
        this.damagedAreas = null;
    }

    @Override
    public AffineTransform getTransform() {
        return this.usr2dev;
    }

    @Override
    public void setRenderingHints(RenderingHints rh) {
        this.renderingHints = new RenderingHints(null);
        this.renderingHints.add(rh);
        this.damagedAreas = null;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    @Override
    public boolean isDoubleBuffered() {
        return this.isDoubleBuffered;
    }

    @Override
    public void setDoubleBuffered(boolean isDoubleBuffered) {
        if (this.isDoubleBuffered == isDoubleBuffered) {
            return;
        }
        this.isDoubleBuffered = isDoubleBuffered;
        if (isDoubleBuffered) {
            this.workImg = null;
        } else {
            this.workImg = this.currImg;
            this.damagedAreas = null;
        }
    }

    @Override
    public void updateOffScreen(int width, int height) {
        this.offScreenWidth = width;
        this.offScreenHeight = height;
    }

    @Override
    public BufferedImage getOffScreen() {
        if (this.rootGN == null) {
            return null;
        }
        return this.currImg;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearOffScreen() {
        if (this.isDoubleBuffered) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.workImg == null) {
            return;
        }
        BufferedImage bufferedImage = this.workImg;
        synchronized (bufferedImage) {
            Graphics2D g2d = this.workImg.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, this.workImg.getWidth(), this.workImg.getHeight());
            g2d.dispose();
        }
        this.damagedAreas = null;
    }

    @Override
    public void flush() {
    }

    @Override
    public void flush(Rectangle r) {
    }

    @Override
    public void flush(Collection areas) {
    }

    protected void updateWorkingBuffers() {
        if (this.rootGN == null) {
            this.currImg = null;
            this.workImg = null;
            return;
        }
        int w = this.offScreenWidth;
        int h = this.offScreenHeight;
        if (this.workImg == null || this.workImg.getWidth() < w || this.workImg.getHeight() < h) {
            this.workImg = new BufferedImage(w, h, IMAGE_TYPE);
        }
        if (!this.isDoubleBuffered) {
            this.currImg = this.workImg;
        }
    }

    @Override
    public void repaint(Shape area) {
        if (area == null) {
            return;
        }
        RectListManager rlm = new RectListManager();
        rlm.add(this.usr2dev.createTransformedShape(area).getBounds());
        this.repaint(rlm);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void repaint(RectListManager devRLM) {
        if (devRLM == null) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.rootGN == null || this.workImg == null) {
            return;
        }
        try {
            BufferedImage bufferedImage = this.workImg;
            synchronized (bufferedImage) {
                Graphics2D g2d = GraphicsUtil.createGraphics((BufferedImage)this.workImg, (RenderingHints)this.renderingHints);
                Rectangle dr = new Rectangle(0, 0, this.offScreenWidth, this.offScreenHeight);
                if (this.isDoubleBuffered && this.currImg != null && this.damagedAreas != null) {
                    this.damagedAreas.subtract(devRLM, 1000, 10);
                    this.damagedAreas.mergeRects(1000, 10);
                    Iterator iter = this.damagedAreas.iterator();
                    g2d.setComposite(AlphaComposite.Src);
                    while (iter.hasNext()) {
                        Rectangle r = (Rectangle)iter.next();
                        if (!dr.intersects(r)) continue;
                        r = dr.intersection(r);
                        g2d.setClip(r.x, r.y, r.width, r.height);
                        g2d.setComposite(AlphaComposite.Clear);
                        g2d.fillRect(r.x, r.y, r.width, r.height);
                        g2d.setComposite(AlphaComposite.SrcOver);
                        g2d.drawImage((Image)this.currImg, 0, 0, null);
                    }
                }
                for (Object aDevRLM : devRLM) {
                    Rectangle r = (Rectangle)aDevRLM;
                    if (!dr.intersects(r)) continue;
                    r = dr.intersection(r);
                    g2d.setTransform(IDENTITY);
                    g2d.setClip(r.x, r.y, r.width, r.height);
                    g2d.setComposite(AlphaComposite.Clear);
                    g2d.fillRect(r.x, r.y, r.width, r.height);
                    g2d.setComposite(AlphaComposite.SrcOver);
                    g2d.transform(this.usr2dev);
                    this.rootGN.paint(g2d);
                }
                g2d.dispose();
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
        if (HaltingThread.hasBeenHalted()) {
            return;
        }
        if (this.isDoubleBuffered) {
            BufferedImage tmpImg = this.workImg;
            this.workImg = this.currImg;
            this.currImg = tmpImg;
            this.damagedAreas = devRLM;
        }
    }

    static {
        defaultRenderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        defaultRenderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
}

