/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.RectListManager
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.renderable.Filter
 *  org.apache.batik.ext.awt.image.rendered.CachableRed
 *  org.apache.batik.ext.awt.image.rendered.PadRed
 *  org.apache.batik.ext.awt.image.rendered.TileCacheRed
 *  org.apache.batik.ext.awt.image.rendered.TranslateRed
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.gvt.renderer;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.lang.ref.SoftReference;
import java.util.Collection;
import org.apache.batik.ext.awt.geom.RectListManager;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;
import org.apache.batik.gvt.GraphicsNode;
import org.apache.batik.gvt.renderer.ImageRenderer;
import org.apache.batik.util.HaltingThread;

public class StaticRenderer
implements ImageRenderer {
    protected GraphicsNode rootGN;
    protected Filter rootFilter;
    protected CachableRed rootCR;
    protected SoftReference lastCR;
    protected SoftReference lastCache;
    protected boolean isDoubleBuffered = false;
    protected WritableRaster currentBaseRaster;
    protected WritableRaster currentRaster;
    protected BufferedImage currentOffScreen;
    protected WritableRaster workingBaseRaster;
    protected WritableRaster workingRaster;
    protected BufferedImage workingOffScreen;
    protected int offScreenWidth;
    protected int offScreenHeight;
    protected RenderingHints renderingHints = new RenderingHints(null);
    protected AffineTransform usr2dev;
    protected static RenderingHints defaultRenderingHints = new RenderingHints(null);

    public StaticRenderer(RenderingHints rh, AffineTransform at) {
        this.renderingHints.add(rh);
        this.usr2dev = new AffineTransform(at);
    }

    public StaticRenderer() {
        this.renderingHints.add(defaultRenderingHints);
        this.usr2dev = new AffineTransform();
    }

    @Override
    public void dispose() {
        this.rootGN = null;
        this.rootFilter = null;
        this.rootCR = null;
        this.workingOffScreen = null;
        this.workingBaseRaster = null;
        this.workingRaster = null;
        this.currentOffScreen = null;
        this.currentBaseRaster = null;
        this.currentRaster = null;
        this.renderingHints = null;
        this.lastCache = null;
        this.lastCR = null;
    }

    @Override
    public void setTree(GraphicsNode rootGN) {
        this.rootGN = rootGN;
        this.rootFilter = null;
        this.rootCR = null;
        this.workingOffScreen = null;
        this.workingRaster = null;
        this.currentOffScreen = null;
        this.currentRaster = null;
    }

    @Override
    public GraphicsNode getTree() {
        return this.rootGN;
    }

    @Override
    public void setRenderingHints(RenderingHints rh) {
        this.renderingHints = new RenderingHints(null);
        this.renderingHints.add(rh);
        this.rootFilter = null;
        this.rootCR = null;
        this.workingOffScreen = null;
        this.workingRaster = null;
        this.currentOffScreen = null;
        this.currentRaster = null;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return this.renderingHints;
    }

    @Override
    public void setTransform(AffineTransform usr2dev) {
        if (this.usr2dev.equals(usr2dev)) {
            return;
        }
        this.usr2dev = usr2dev == null ? new AffineTransform() : new AffineTransform(usr2dev);
        this.rootCR = null;
    }

    @Override
    public AffineTransform getTransform() {
        return this.usr2dev;
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
            this.currentOffScreen = null;
            this.currentBaseRaster = null;
            this.currentRaster = null;
        } else {
            this.currentOffScreen = this.workingOffScreen;
            this.currentBaseRaster = this.workingBaseRaster;
            this.currentRaster = this.workingRaster;
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
        return this.currentOffScreen;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void clearOffScreen() {
        WritableRaster syncRaster;
        if (this.isDoubleBuffered) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.rootCR == null || this.workingBaseRaster == null) {
            return;
        }
        ColorModel cm = this.rootCR.getColorModel();
        WritableRaster writableRaster = syncRaster = this.workingBaseRaster;
        synchronized (writableRaster) {
            BufferedImage bi = new BufferedImage(cm, this.workingBaseRaster, cm.isAlphaPremultiplied(), null);
            Graphics2D g2d = bi.createGraphics();
            g2d.setComposite(AlphaComposite.Clear);
            g2d.fillRect(0, 0, bi.getWidth(), bi.getHeight());
            g2d.dispose();
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
    public void repaint(RectListManager areas) {
        if (areas == null) {
            return;
        }
        this.updateWorkingBuffers();
        if (this.rootCR == null || this.workingBaseRaster == null) {
            return;
        }
        CachableRed cr = this.rootCR;
        WritableRaster syncRaster = this.workingBaseRaster;
        WritableRaster copyRaster = this.workingRaster;
        Rectangle srcR = this.rootCR.getBounds();
        Rectangle dstR = this.workingRaster.getBounds();
        if (dstR.x < srcR.x || dstR.y < srcR.y || dstR.x + dstR.width > srcR.x + srcR.width || dstR.y + dstR.height > srcR.y + srcR.height) {
            cr = new PadRed(cr, dstR, PadMode.ZERO_PAD, null);
        }
        WritableRaster writableRaster = syncRaster;
        synchronized (writableRaster) {
            cr.copyData(copyRaster);
        }
        if (!HaltingThread.hasBeenHalted()) {
            BufferedImage tmpBI = this.workingOffScreen;
            this.workingBaseRaster = this.currentBaseRaster;
            this.workingRaster = this.currentRaster;
            this.workingOffScreen = this.currentOffScreen;
            this.currentRaster = copyRaster;
            this.currentBaseRaster = syncRaster;
            this.currentOffScreen = tmpBI;
        }
    }

    @Override
    public void flush() {
        if (this.lastCache == null) {
            return;
        }
        Object o = this.lastCache.get();
        if (o == null) {
            return;
        }
        TileCacheRed tcr = (TileCacheRed)o;
        tcr.flushCache(tcr.getBounds());
    }

    @Override
    public void flush(Collection areas) {
        AffineTransform at = this.getTransform();
        for (Object area : areas) {
            Shape s = (Shape)area;
            Rectangle r = at.createTransformedShape(s).getBounds();
            this.flush(r);
        }
    }

    @Override
    public void flush(Rectangle r) {
        if (this.lastCache == null) {
            return;
        }
        Object o = this.lastCache.get();
        if (o == null) {
            return;
        }
        TileCacheRed tcr = (TileCacheRed)o;
        r = (Rectangle)r.clone();
        r.x -= Math.round((float)this.usr2dev.getTranslateX());
        r.y -= Math.round((float)this.usr2dev.getTranslateY());
        tcr.flushCache(r);
    }

    protected CachableRed setupCache(CachableRed img) {
        if (this.lastCR == null || img != this.lastCR.get()) {
            this.lastCR = new SoftReference<CachableRed>(img);
            this.lastCache = null;
        }
        Object o = null;
        if (this.lastCache != null) {
            o = this.lastCache.get();
        }
        if (o != null) {
            return o;
        }
        img = new TileCacheRed(img);
        this.lastCache = new SoftReference<CachableRed>(img);
        return img;
    }

    protected CachableRed renderGNR() {
        AffineTransform at = this.usr2dev;
        AffineTransform rcAT = new AffineTransform(at.getScaleX(), at.getShearY(), at.getShearX(), at.getScaleY(), 0.0, 0.0);
        RenderContext rc = new RenderContext(rcAT, null, this.renderingHints);
        RenderedImage ri = this.rootFilter.createRendering(rc);
        if (ri == null) {
            return null;
        }
        CachableRed ret = GraphicsUtil.wrap((RenderedImage)ri);
        ret = this.setupCache(ret);
        int dx = Math.round((float)at.getTranslateX());
        int dy = Math.round((float)at.getTranslateY());
        ret = new TranslateRed(ret, ret.getMinX() + dx, ret.getMinY() + dy);
        ret = GraphicsUtil.convertTosRGB((CachableRed)ret);
        return ret;
    }

    protected void updateWorkingBuffers() {
        if (this.rootFilter == null) {
            this.rootFilter = this.rootGN.getGraphicsNodeRable(true);
            this.rootCR = null;
        }
        this.rootCR = this.renderGNR();
        if (this.rootCR == null) {
            this.workingRaster = null;
            this.workingOffScreen = null;
            this.workingBaseRaster = null;
            this.currentOffScreen = null;
            this.currentBaseRaster = null;
            this.currentRaster = null;
            return;
        }
        SampleModel sm = this.rootCR.getSampleModel();
        int w = this.offScreenWidth;
        int h = this.offScreenHeight;
        int tw = sm.getWidth();
        int th = sm.getHeight();
        w = ((w + tw - 1) / tw + 1) * tw;
        h = ((h + th - 1) / th + 1) * th;
        if (this.workingBaseRaster == null || this.workingBaseRaster.getWidth() < w || this.workingBaseRaster.getHeight() < h) {
            sm = sm.createCompatibleSampleModel(w, h);
            this.workingBaseRaster = Raster.createWritableRaster(sm, new Point(0, 0));
        }
        int tgx = -this.rootCR.getTileGridXOffset();
        int tgy = -this.rootCR.getTileGridYOffset();
        int xt = tgx >= 0 ? tgx / tw : (tgx - tw + 1) / tw;
        int yt = tgy >= 0 ? tgy / th : (tgy - th + 1) / th;
        int xloc = xt * tw - tgx;
        int yloc = yt * th - tgy;
        this.workingRaster = this.workingBaseRaster.createWritableChild(0, 0, w, h, xloc, yloc, null);
        this.workingOffScreen = new BufferedImage(this.rootCR.getColorModel(), this.workingRaster.createWritableChild(0, 0, this.offScreenWidth, this.offScreenHeight, 0, 0, null), this.rootCR.getColorModel().isAlphaPremultiplied(), null);
        if (!this.isDoubleBuffered) {
            this.currentOffScreen = this.workingOffScreen;
            this.currentBaseRaster = this.workingBaseRaster;
            this.currentRaster = this.workingRaster;
        }
    }

    static {
        defaultRenderingHints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        defaultRenderingHints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }
}

