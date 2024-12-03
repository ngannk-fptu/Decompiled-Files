/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.ext.awt.geom.RectListManager
 *  org.apache.batik.ext.awt.image.GraphicsUtil
 *  org.apache.batik.ext.awt.image.PadMode
 *  org.apache.batik.ext.awt.image.rendered.CachableRed
 *  org.apache.batik.ext.awt.image.rendered.PadRed
 *  org.apache.batik.util.HaltingThread
 */
package org.apache.batik.gvt.renderer;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Collection;
import org.apache.batik.ext.awt.geom.RectListManager;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.gvt.renderer.StaticRenderer;
import org.apache.batik.util.HaltingThread;

public class DynamicRenderer
extends StaticRenderer {
    static final int COPY_OVERHEAD = 1000;
    static final int COPY_LINE_OVERHEAD = 10;
    RectListManager damagedAreas;

    public DynamicRenderer() {
    }

    public DynamicRenderer(RenderingHints rh, AffineTransform at) {
        super(rh, at);
    }

    @Override
    protected CachableRed setupCache(CachableRed img) {
        return img;
    }

    @Override
    public void flush(Rectangle r) {
    }

    @Override
    public void flush(Collection areas) {
    }

    @Override
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
        if (this.workingBaseRaster == null || this.workingBaseRaster.getWidth() < w || this.workingBaseRaster.getHeight() < h) {
            sm = sm.createCompatibleSampleModel(w, h);
            this.workingBaseRaster = Raster.createWritableRaster(sm, new Point(0, 0));
            this.workingRaster = this.workingBaseRaster.createWritableChild(0, 0, w, h, 0, 0, null);
            this.workingOffScreen = new BufferedImage(this.rootCR.getColorModel(), this.workingRaster, this.rootCR.getColorModel().isAlphaPremultiplied(), null);
        }
        if (!this.isDoubleBuffered) {
            this.currentOffScreen = this.workingOffScreen;
            this.currentBaseRaster = this.workingBaseRaster;
            this.currentRaster = this.workingRaster;
        }
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
        boolean repaintAll = false;
        Rectangle dr = copyRaster.getBounds();
        Rectangle sr = null;
        if (this.currentRaster != null) {
            sr = this.currentRaster.getBounds();
        }
        WritableRaster writableRaster = syncRaster;
        synchronized (writableRaster) {
            if (repaintAll) {
                cr.copyData(copyRaster);
            } else {
                Rectangle r;
                Color borderColor;
                Color fillColor;
                Graphics2D g2d = null;
                if (this.isDoubleBuffered && this.currentRaster != null && this.damagedAreas != null) {
                    this.damagedAreas.subtract(devRLM, 1000, 10);
                    this.damagedAreas.mergeRects(1000, 10);
                    fillColor = new Color(0, 0, 255, 50);
                    borderColor = new Color(0, 0, 0, 50);
                    for (Object damagedArea : this.damagedAreas) {
                        r = (Rectangle)damagedArea;
                        if (!dr.intersects(r)) continue;
                        r = dr.intersection(r);
                        if (sr != null && !sr.intersects(r)) continue;
                        r = sr.intersection(r);
                        WritableRaster src = this.currentRaster.createWritableChild(r.x, r.y, r.width, r.height, r.x, r.y, null);
                        GraphicsUtil.copyData((Raster)src, (WritableRaster)copyRaster);
                        if (g2d == null) continue;
                        g2d.setPaint(fillColor);
                        g2d.fill(r);
                        g2d.setPaint(borderColor);
                        g2d.draw(r);
                    }
                }
                fillColor = new Color(255, 0, 0, 50);
                borderColor = new Color(0, 0, 0, 50);
                for (Object aDevRLM : devRLM) {
                    r = (Rectangle)aDevRLM;
                    if (!dr.intersects(r)) continue;
                    r = dr.intersection(r);
                    WritableRaster dst = copyRaster.createWritableChild(r.x, r.y, r.width, r.height, r.x, r.y, null);
                    cr.copyData(dst);
                    if (g2d == null) continue;
                    g2d.setPaint(fillColor);
                    g2d.fill(r);
                    g2d.setPaint(borderColor);
                    g2d.draw(r);
                }
            }
        }
        if (HaltingThread.hasBeenHalted()) {
            return;
        }
        BufferedImage tmpBI = this.workingOffScreen;
        this.workingBaseRaster = this.currentBaseRaster;
        this.workingRaster = this.currentRaster;
        this.workingOffScreen = this.currentOffScreen;
        this.currentRaster = copyRaster;
        this.currentBaseRaster = syncRaster;
        this.currentOffScreen = tmpBI;
        this.damagedAreas = devRLM;
    }
}

