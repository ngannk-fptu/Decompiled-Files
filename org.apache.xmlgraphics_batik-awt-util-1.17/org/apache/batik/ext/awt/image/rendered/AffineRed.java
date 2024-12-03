/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.AbstractTiledRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;

public class AffineRed
extends AbstractRed {
    RenderingHints hints;
    AffineTransform src2me;
    AffineTransform me2src;

    public AffineTransform getTransform() {
        return (AffineTransform)this.src2me.clone();
    }

    public CachableRed getSource() {
        return (CachableRed)this.getSources().get(0);
    }

    public AffineRed(CachableRed src, AffineTransform src2me, RenderingHints hints) {
        this.src2me = src2me;
        this.hints = hints;
        try {
            this.me2src = src2me.createInverse();
        }
        catch (NoninvertibleTransformException nite) {
            this.me2src = null;
        }
        Rectangle srcBounds = src.getBounds();
        Rectangle myBounds = src2me.createTransformedShape(srcBounds).getBounds();
        ColorModel cm = AffineRed.fixColorModel(src);
        SampleModel sm = this.fixSampleModel(src, cm, myBounds);
        Point2D pt = new Point2D.Float(src.getTileGridXOffset(), src.getTileGridYOffset());
        pt = src2me.transform(pt, null);
        this.init(src, myBounds, cm, sm, (int)pt.getX(), (int)pt.getY(), null);
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        PadRed.ZeroRecter zr = PadRed.ZeroRecter.getZeroRecter(wr);
        zr.zeroRect(new Rectangle(wr.getMinX(), wr.getMinY(), wr.getWidth(), wr.getHeight()));
        this.genRect(wr);
        return wr;
    }

    @Override
    public Raster getTile(int x, int y) {
        if (this.me2src == null) {
            return null;
        }
        int tx = this.tileGridXOff + x * this.tileWidth;
        int ty = this.tileGridYOff + y * this.tileHeight;
        Point pt = new Point(tx, ty);
        WritableRaster wr = Raster.createWritableRaster(this.sm, pt);
        this.genRect(wr);
        return wr;
    }

    public void genRect(WritableRaster wr) {
        if (this.me2src == null) {
            return;
        }
        Rectangle srcR = this.me2src.createTransformedShape(wr.getBounds()).getBounds();
        srcR.setBounds(srcR.x - 1, srcR.y - 1, srcR.width + 2, srcR.height + 2);
        CachableRed src = (CachableRed)this.getSources().get(0);
        if (!srcR.intersects(src.getBounds())) {
            return;
        }
        Raster srcRas = src.getData(srcR.intersection(src.getBounds()));
        if (srcRas == null) {
            return;
        }
        AffineTransform aff = (AffineTransform)this.src2me.clone();
        aff.concatenate(AffineTransform.getTranslateInstance(srcRas.getMinX(), srcRas.getMinY()));
        Point2D srcPt = new Point2D.Float(wr.getMinX(), wr.getMinY());
        srcPt = this.me2src.transform(srcPt, null);
        Point2D destPt = new Point2D.Double(srcPt.getX() - (double)srcRas.getMinX(), srcPt.getY() - (double)srcRas.getMinY());
        destPt = aff.transform(destPt, null);
        aff.preConcatenate(AffineTransform.getTranslateInstance(-destPt.getX(), -destPt.getY()));
        AffineTransformOp op = new AffineTransformOp(aff, this.hints);
        ColorModel srcCM = src.getColorModel();
        ColorModel myCM = this.getColorModel();
        WritableRaster srcWR = (WritableRaster)srcRas;
        srcCM = GraphicsUtil.coerceData(srcWR, srcCM, true);
        BufferedImage srcBI = new BufferedImage(srcCM, srcWR.createWritableTranslatedChild(0, 0), srcCM.isAlphaPremultiplied(), null);
        BufferedImage myBI = new BufferedImage(myCM, wr.createWritableTranslatedChild(0, 0), myCM.isAlphaPremultiplied(), null);
        op.filter(srcBI.getRaster(), myBI.getRaster());
    }

    protected static ColorModel fixColorModel(CachableRed src) {
        ColorModel cm = src.getColorModel();
        if (cm.hasAlpha()) {
            if (!cm.isAlphaPremultiplied()) {
                cm = GraphicsUtil.coerceColorModel(cm, true);
            }
            return cm;
        }
        ColorSpace cs = cm.getColorSpace();
        int b = src.getSampleModel().getNumBands() + 1;
        if (b == 4) {
            int[] masks = new int[4];
            for (int i = 0; i < b - 1; ++i) {
                masks[i] = 0xFF0000 >> 8 * i;
            }
            masks[3] = 255 << 8 * (b - 1);
            return new DirectColorModel(cs, 8 * b, masks[0], masks[1], masks[2], masks[3], true, 3);
        }
        int[] bits = new int[b];
        for (int i = 0; i < b; ++i) {
            bits[i] = 8;
        }
        return new ComponentColorModel(cs, bits, true, true, 3, 3);
    }

    protected SampleModel fixSampleModel(CachableRed src, ColorModel cm, Rectangle bounds) {
        int h;
        SampleModel sm = src.getSampleModel();
        int defSz = AbstractTiledRed.getDefaultTileSize();
        int w = sm.getWidth();
        if (w < defSz) {
            w = defSz;
        }
        if (w > bounds.width) {
            w = bounds.width;
        }
        if ((h = sm.getHeight()) < defSz) {
            h = defSz;
        }
        if (h > bounds.height) {
            h = bounds.height;
        }
        if (w <= 0 || h <= 0) {
            w = 1;
            h = 1;
        }
        return cm.createCompatibleSampleModel(w, h);
    }
}

