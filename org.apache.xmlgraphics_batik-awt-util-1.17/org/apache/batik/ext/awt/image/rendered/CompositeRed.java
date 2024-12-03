/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

public class CompositeRed
extends AbstractRed {
    CompositeRule rule;
    CompositeContext[] contexts;

    public CompositeRed(List srcs, CompositeRule rule) {
        CachableRed src = (CachableRed)srcs.get(0);
        ColorModel cm = CompositeRed.fixColorModel(src);
        this.rule = rule;
        SVGComposite comp = new SVGComposite(rule);
        this.contexts = new CompositeContext[srcs.size()];
        int idx = 0;
        Iterator i = srcs.iterator();
        Rectangle myBounds = null;
        block4: while (i.hasNext()) {
            CachableRed cr = (CachableRed)i.next();
            this.contexts[idx++] = comp.createContext(cr.getColorModel(), cm, null);
            Rectangle newBound = cr.getBounds();
            if (myBounds == null) {
                myBounds = newBound;
                continue;
            }
            switch (rule.getRule()) {
                case 2: {
                    if (myBounds.intersects(newBound)) {
                        myBounds = myBounds.intersection(newBound);
                        continue block4;
                    }
                    myBounds.width = 0;
                    myBounds.height = 0;
                    continue block4;
                }
                case 3: {
                    myBounds = newBound;
                    continue block4;
                }
            }
            myBounds.add(newBound);
        }
        if (myBounds == null) {
            throw new IllegalArgumentException("Composite Operation Must have some source!");
        }
        if (rule.getRule() == 6) {
            ArrayList<CachableRed> vec = new ArrayList<CachableRed>(srcs.size());
            for (CachableRed cr : srcs) {
                Rectangle r = cr.getBounds();
                if (r.x != myBounds.x || r.y != myBounds.y || r.width != myBounds.width || r.height != myBounds.height) {
                    cr = new PadRed(cr, myBounds, PadMode.ZERO_PAD, null);
                }
                vec.add(cr);
            }
            srcs = vec;
        }
        SampleModel sm = CompositeRed.fixSampleModel(src, cm, myBounds);
        int defSz = AbstractTiledRed.getDefaultTileSize();
        int tgX = defSz * (int)Math.floor(myBounds.x / defSz);
        int tgY = defSz * (int)Math.floor(myBounds.y / defSz);
        this.init(srcs, myBounds, cm, sm, tgX, tgY, null);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        this.genRect(wr);
        return wr;
    }

    @Override
    public Raster getTile(int x, int y) {
        int tx = this.tileGridXOff + x * this.tileWidth;
        int ty = this.tileGridYOff + y * this.tileHeight;
        Point pt = new Point(tx, ty);
        WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        this.genRect(wr);
        return wr;
    }

    public void emptyRect(WritableRaster wr) {
        PadRed.ZeroRecter zr = PadRed.ZeroRecter.getZeroRecter(wr);
        zr.zeroRect(new Rectangle(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight()));
    }

    public void genRect(WritableRaster wr) {
        Rectangle r = wr.getBounds();
        int idx = 0;
        Iterator i = this.srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            Rectangle crR;
            CachableRed cr = (CachableRed)i.next();
            if (first) {
                crR = cr.getBounds();
                if (r.x < crR.x || r.y < crR.y || r.x + r.width > crR.x + crR.width || r.y + r.height > crR.y + crR.height) {
                    this.emptyRect(wr);
                }
                cr.copyData(wr);
                if (!cr.getColorModel().isAlphaPremultiplied()) {
                    GraphicsUtil.coerceData(wr, cr.getColorModel(), true);
                }
                first = false;
            } else {
                crR = cr.getBounds();
                if (crR.intersects(r)) {
                    Rectangle smR = crR.intersection(r);
                    Raster ras = cr.getData(smR);
                    WritableRaster smWR = wr.createWritableChild(smR.x, smR.y, smR.width, smR.height, smR.x, smR.y, null);
                    this.contexts[idx].compose(ras, smWR, smWR);
                }
            }
            ++idx;
        }
    }

    public void genRect_OVER(WritableRaster wr) {
        Rectangle r = wr.getBounds();
        ColorModel cm = this.getColorModel();
        BufferedImage bi = new BufferedImage(cm, wr.createWritableTranslatedChild(0, 0), cm.isAlphaPremultiplied(), null);
        Graphics2D g2d = GraphicsUtil.createGraphics(bi);
        g2d.translate(-r.x, -r.y);
        Iterator i = this.srcs.iterator();
        boolean first = true;
        while (i.hasNext()) {
            CachableRed cr = (CachableRed)i.next();
            if (first) {
                Rectangle crR = cr.getBounds();
                if (r.x < crR.x || r.y < crR.y || r.x + r.width > crR.x + crR.width || r.y + r.height > crR.y + crR.height) {
                    this.emptyRect(wr);
                }
                cr.copyData(wr);
                GraphicsUtil.coerceData(wr, cr.getColorModel(), cm.isAlphaPremultiplied());
                first = false;
                continue;
            }
            GraphicsUtil.drawImage(g2d, cr);
        }
    }

    protected static SampleModel fixSampleModel(CachableRed src, ColorModel cm, Rectangle bounds) {
        int h;
        int defSz = AbstractTiledRed.getDefaultTileSize();
        int tgX = defSz * (int)Math.floor(bounds.x / defSz);
        int tgY = defSz * (int)Math.floor(bounds.y / defSz);
        int tw = bounds.x + bounds.width - tgX;
        int th = bounds.y + bounds.height - tgY;
        SampleModel sm = src.getSampleModel();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > tw) {
            w = tw;
        }
        if ((h = sm.getHeight()) < defSz) {
            h = defSz;
        }
        if (h > th) {
            h = th;
        }
        if (w <= 0 || h <= 0) {
            w = 1;
            h = 1;
        }
        return cm.createCompatibleSampleModel(w, h);
    }

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm.hasAlpha()) {
            if (!cm.isAlphaPremultiplied()) {
                cm = GraphicsUtil.coerceColorModel(cm, true);
            }
            return cm;
        }
        int b = src.getSampleModel().getNumBands() + 1;
        if (b > 4) {
            throw new IllegalArgumentException("CompositeRed can only handle up to three band images");
        }
        int[] masks = new int[4];
        for (int i = 0; i < b - 1; ++i) {
            masks[i] = 0xFF0000 >> 8 * i;
        }
        masks[3] = 255 << 8 * (b - 1);
        ColorSpace cs = cm.getColorSpace();
        return new DirectColorModel(cs, 8 * b, masks[0], masks[1], masks[2], masks[3], true, 3);
    }
}

