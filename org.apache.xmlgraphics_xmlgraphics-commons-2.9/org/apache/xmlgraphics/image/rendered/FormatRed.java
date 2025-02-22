/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.rendered;

import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.xmlgraphics.image.GraphicsUtil;
import org.apache.xmlgraphics.image.rendered.AbstractRed;
import org.apache.xmlgraphics.image.rendered.CachableRed;

public class FormatRed
extends AbstractRed {
    public static CachableRed construct(CachableRed src, ColorModel cm) {
        ColorModel srcCM = src.getColorModel();
        if (cm.hasAlpha() != srcCM.hasAlpha() || cm.isAlphaPremultiplied() != srcCM.isAlphaPremultiplied()) {
            return new FormatRed(src, cm);
        }
        if (cm.getNumComponents() != srcCM.getNumComponents()) {
            throw new IllegalArgumentException("Incompatible ColorModel given");
        }
        if (srcCM instanceof ComponentColorModel && cm instanceof ComponentColorModel) {
            return src;
        }
        if (srcCM instanceof DirectColorModel && cm instanceof DirectColorModel) {
            return src;
        }
        return new FormatRed(src, cm);
    }

    public FormatRed(CachableRed cr, SampleModel sm) {
        super(cr, cr.getBounds(), FormatRed.makeColorModel(cr, sm), sm, cr.getTileGridXOffset(), cr.getTileGridYOffset(), null);
    }

    public FormatRed(CachableRed cr, ColorModel cm) {
        super(cr, cr.getBounds(), cm, FormatRed.makeSampleModel(cr, cm), cr.getTileGridXOffset(), cr.getTileGridYOffset(), null);
    }

    public CachableRed getSource() {
        return (CachableRed)this.getSources().get(0);
    }

    @Override
    public Object getProperty(String name) {
        return this.getSource().getProperty(name);
    }

    @Override
    public String[] getPropertyNames() {
        return this.getSource().getPropertyNames();
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        ColorModel cm = this.getColorModel();
        CachableRed cr = this.getSource();
        ColorModel srcCM = cr.getColorModel();
        SampleModel srcSM = cr.getSampleModel();
        srcSM = srcSM.createCompatibleSampleModel(wr.getWidth(), wr.getHeight());
        WritableRaster srcWR = Raster.createWritableRaster(srcSM, new Point(wr.getMinX(), wr.getMinY()));
        this.getSource().copyData(srcWR);
        BufferedImage srcBI = new BufferedImage(srcCM, srcWR.createWritableTranslatedChild(0, 0), srcCM.isAlphaPremultiplied(), null);
        BufferedImage dstBI = new BufferedImage(cm, wr.createWritableTranslatedChild(0, 0), cm.isAlphaPremultiplied(), null);
        GraphicsUtil.copyData(srcBI, dstBI);
        return wr;
    }

    public static SampleModel makeSampleModel(CachableRed cr, ColorModel cm) {
        SampleModel srcSM = cr.getSampleModel();
        return cm.createCompatibleSampleModel(srcSM.getWidth(), srcSM.getHeight());
    }

    public static ColorModel makeColorModel(CachableRed cr, SampleModel sm) {
        int bits;
        ColorModel srcCM = cr.getColorModel();
        ColorSpace cs = srcCM.getColorSpace();
        int bands = sm.getNumBands();
        int dt = sm.getDataType();
        switch (dt) {
            case 0: {
                bits = 8;
                break;
            }
            case 2: {
                bits = 16;
                break;
            }
            case 1: {
                bits = 16;
                break;
            }
            case 3: {
                bits = 32;
                break;
            }
            default: {
                throw new IllegalArgumentException("Unsupported DataBuffer type: " + dt);
            }
        }
        boolean hasAlpha = srcCM.hasAlpha();
        if (hasAlpha) {
            if (bands == srcCM.getNumComponents() - 1) {
                hasAlpha = false;
            } else if (bands != srcCM.getNumComponents()) {
                throw new IllegalArgumentException("Incompatible number of bands in and out");
            }
        } else if (bands == srcCM.getNumComponents() + 1) {
            hasAlpha = true;
        } else if (bands != srcCM.getNumComponents()) {
            throw new IllegalArgumentException("Incompatible number of bands in and out");
        }
        boolean preMult = srcCM.isAlphaPremultiplied();
        if (!hasAlpha) {
            preMult = false;
        }
        if (sm instanceof ComponentSampleModel) {
            int[] bitsPer = new int[bands];
            for (int i = 0; i < bands; ++i) {
                bitsPer[i] = bits;
            }
            return new ComponentColorModel(cs, bitsPer, hasAlpha, preMult, hasAlpha ? 3 : 1, dt);
        }
        if (sm instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
            int[] masks = sppsm.getBitMasks();
            if (bands == 4) {
                return new DirectColorModel(cs, bits, masks[0], masks[1], masks[2], masks[3], preMult, dt);
            }
            if (bands == 3) {
                return new DirectColorModel(cs, bits, masks[0], masks[1], masks[2], 0, preMult, dt);
            }
            throw new IllegalArgumentException("Incompatible number of bands out for ColorModel");
        }
        throw new IllegalArgumentException("Unsupported SampleModel Type");
    }
}

