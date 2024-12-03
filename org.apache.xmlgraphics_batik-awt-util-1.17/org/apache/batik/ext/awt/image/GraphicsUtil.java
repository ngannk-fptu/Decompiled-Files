/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image;

import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.awt.image.renderable.RenderContext;
import java.awt.image.renderable.RenderableImage;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import org.apache.batik.ext.awt.RenderingHintsKeyExt;
import org.apache.batik.ext.awt.image.SVGComposite;
import org.apache.batik.ext.awt.image.renderable.PaintRable;
import org.apache.batik.ext.awt.image.rendered.AffineRed;
import org.apache.batik.ext.awt.image.rendered.Any2LsRGBRed;
import org.apache.batik.ext.awt.image.rendered.Any2sRGBRed;
import org.apache.batik.ext.awt.image.rendered.BufferedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.FormatRed;
import org.apache.batik.ext.awt.image.rendered.RenderedImageCachableRed;
import org.apache.batik.ext.awt.image.rendered.TranslateRed;

public class GraphicsUtil {
    public static AffineTransform IDENTITY = new AffineTransform();
    public static final boolean WARN_DESTINATION;
    public static final ColorModel Linear_sRGB;
    public static final ColorModel Linear_sRGB_Pre;
    public static final ColorModel Linear_sRGB_Unpre;
    public static final ColorModel sRGB;
    public static final ColorModel sRGB_Pre;
    public static final ColorModel sRGB_Unpre;

    public static void drawImage(Graphics2D g2d, RenderedImage ri) {
        GraphicsUtil.drawImage(g2d, GraphicsUtil.wrap(ri));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drawImage(Graphics2D g2d, CachableRed cr) {
        block41: {
            AffineTransform at = null;
            while (true) {
                if (cr instanceof AffineRed) {
                    AffineRed ar = (AffineRed)cr;
                    if (at == null) {
                        at = ar.getTransform();
                    } else {
                        at.concatenate(ar.getTransform());
                    }
                    cr = ar.getSource();
                    continue;
                }
                if (!(cr instanceof TranslateRed)) break;
                TranslateRed tr = (TranslateRed)cr;
                int dx = tr.getDeltaX();
                int dy = tr.getDeltaY();
                if (at == null) {
                    at = AffineTransform.getTranslateInstance(dx, dy);
                } else {
                    at.translate(dx, dy);
                }
                cr = tr.getSource();
            }
            AffineTransform g2dAt = g2d.getTransform();
            if (at == null || at.isIdentity()) {
                at = g2dAt;
            } else {
                at.preConcatenate(g2dAt);
            }
            ColorModel srcCM = cr.getColorModel();
            ColorModel g2dCM = GraphicsUtil.getDestinationColorModel(g2d);
            ColorSpace g2dCS = null;
            if (g2dCM != null) {
                g2dCS = g2dCM.getColorSpace();
            }
            if (g2dCS == null) {
                g2dCS = ColorSpace.getInstance(1000);
            }
            ColorModel drawCM = g2dCM;
            if (g2dCM == null || !g2dCM.hasAlpha()) {
                drawCM = sRGB_Unpre;
            }
            if (cr instanceof BufferedImageCachableRed && g2dCS.equals(srcCM.getColorSpace()) && drawCM.equals(srcCM)) {
                g2d.setTransform(at);
                BufferedImageCachableRed bicr = (BufferedImageCachableRed)cr;
                g2d.drawImage((Image)bicr.getBufferedImage(), bicr.getMinX(), bicr.getMinY(), null);
                g2d.setTransform(g2dAt);
                return;
            }
            double determinant = at.getDeterminant();
            if (!at.isIdentity() && determinant <= 1.0) {
                if (at.getType() != 1) {
                    cr = new AffineRed(cr, at, g2d.getRenderingHints());
                } else {
                    int xloc = cr.getMinX() + (int)at.getTranslateX();
                    int yloc = cr.getMinY() + (int)at.getTranslateY();
                    cr = new TranslateRed(cr, xloc, yloc);
                }
            }
            if (g2dCS != srcCM.getColorSpace()) {
                if (g2dCS == ColorSpace.getInstance(1000)) {
                    cr = GraphicsUtil.convertTosRGB(cr);
                } else if (g2dCS == ColorSpace.getInstance(1004)) {
                    cr = GraphicsUtil.convertToLsRGB(cr);
                }
            }
            if (!drawCM.equals(srcCM = cr.getColorModel())) {
                cr = FormatRed.construct(cr, drawCM);
            }
            if (!at.isIdentity() && determinant > 1.0) {
                cr = new AffineRed(cr, at, g2d.getRenderingHints());
            }
            g2d.setTransform(IDENTITY);
            Composite g2dComposite = g2d.getComposite();
            if (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) == "Printing" && SVGComposite.OVER.equals(g2dComposite)) {
                g2d.setComposite(SVGComposite.OVER);
            }
            Rectangle crR = cr.getBounds();
            Shape clip = g2d.getClip();
            try {
                Object atpHint;
                Rectangle clipR;
                if (clip == null) {
                    clip = crR;
                    clipR = crR;
                } else {
                    clipR = clip.getBounds();
                    if (!clipR.intersects(crR)) {
                        return;
                    }
                    clipR = clipR.intersection(crR);
                }
                Rectangle gcR = GraphicsUtil.getDestinationBounds(g2d);
                if (gcR != null) {
                    if (!clipR.intersects(gcR)) {
                        return;
                    }
                    clipR = clipR.intersection(gcR);
                }
                boolean useDrawRenderedImage = false;
                srcCM = cr.getColorModel();
                SampleModel srcSM = cr.getSampleModel();
                if (srcSM.getWidth() * srcSM.getHeight() >= clipR.width * clipR.height) {
                    useDrawRenderedImage = true;
                }
                if ((atpHint = g2d.getRenderingHint(RenderingHintsKeyExt.KEY_AVOID_TILE_PAINTING)) == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_ON) {
                    useDrawRenderedImage = true;
                }
                if (atpHint == RenderingHintsKeyExt.VALUE_AVOID_TILE_PAINTING_OFF) {
                    useDrawRenderedImage = false;
                }
                if (useDrawRenderedImage) {
                    Raster r = cr.getData(clipR);
                    WritableRaster wr = ((WritableRaster)r).createWritableChild(clipR.x, clipR.y, clipR.width, clipR.height, 0, 0, null);
                    BufferedImage bi = new BufferedImage(srcCM, wr, srcCM.isAlphaPremultiplied(), null);
                    g2d.drawImage((Image)bi, clipR.x, clipR.y, null);
                    break block41;
                }
                WritableRaster wr = Raster.createWritableRaster(srcSM, new Point(0, 0));
                BufferedImage bi = new BufferedImage(srcCM, wr, srcCM.isAlphaPremultiplied(), null);
                int xt0 = cr.getMinTileX();
                int xt1 = xt0 + cr.getNumXTiles();
                int yt0 = cr.getMinTileY();
                int yt1 = yt0 + cr.getNumYTiles();
                int tw = srcSM.getWidth();
                int th = srcSM.getHeight();
                Rectangle tR = new Rectangle(0, 0, tw, th);
                Rectangle iR = new Rectangle(0, 0, 0, 0);
                int yloc = yt0 * th + cr.getTileGridYOffset();
                int skip = (clipR.y - yloc) / th;
                if (skip < 0) {
                    skip = 0;
                }
                yt0 += skip;
                int xloc = xt0 * tw + cr.getTileGridXOffset();
                skip = (clipR.x - xloc) / tw;
                if (skip < 0) {
                    skip = 0;
                }
                int endX = clipR.x + clipR.width - 1;
                int endY = clipR.y + clipR.height - 1;
                yloc = yt0 * th + cr.getTileGridYOffset();
                int minX = (xt0 += skip) * tw + cr.getTileGridXOffset();
                int xStep = tw;
                xloc = minX;
                int y = yt0;
                while (y < yt1) {
                    if (yloc > endY) {
                        break;
                    }
                    for (int x = xt0; x < xt1 && xloc >= minX && xloc <= endX; ++x, xloc += xStep) {
                        tR.x = xloc;
                        tR.y = yloc;
                        Rectangle2D.intersect(crR, tR, iR);
                        WritableRaster twr = wr.createWritableChild(0, 0, iR.width, iR.height, iR.x, iR.y, null);
                        cr.copyData(twr);
                        BufferedImage subBI = bi.getSubimage(0, 0, iR.width, iR.height);
                        g2d.drawImage((Image)subBI, iR.x, iR.y, null);
                    }
                    xStep = -xStep;
                    xloc += xStep;
                    ++y;
                    yloc += th;
                }
            }
            finally {
                g2d.setTransform(g2dAt);
                g2d.setComposite(g2dComposite);
            }
        }
    }

    public static void drawImage(Graphics2D g2d, RenderableImage filter, RenderContext rc) {
        AffineTransform origDev = g2d.getTransform();
        Shape origClip = g2d.getClip();
        RenderingHints origRH = g2d.getRenderingHints();
        Shape clip = rc.getAreaOfInterest();
        if (clip != null) {
            g2d.clip(clip);
        }
        g2d.transform(rc.getTransform());
        g2d.setRenderingHints(rc.getRenderingHints());
        GraphicsUtil.drawImage(g2d, filter);
        g2d.setTransform(origDev);
        g2d.setClip(origClip);
        g2d.setRenderingHints(origRH);
    }

    public static void drawImage(Graphics2D g2d, RenderableImage filter) {
        PaintRable pr;
        if (filter instanceof PaintRable && (pr = (PaintRable)((Object)filter)).paintRable(g2d)) {
            return;
        }
        AffineTransform at = g2d.getTransform();
        RenderedImage ri = filter.createRendering(new RenderContext(at, g2d.getClip(), g2d.getRenderingHints()));
        if (ri == null) {
            return;
        }
        g2d.setTransform(IDENTITY);
        GraphicsUtil.drawImage(g2d, GraphicsUtil.wrap(ri));
        g2d.setTransform(at);
    }

    public static Graphics2D createGraphics(BufferedImage bi, RenderingHints hints) {
        Graphics2D g2d = bi.createGraphics();
        if (hints != null) {
            g2d.addRenderingHints(hints);
        }
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference<BufferedImage>(bi));
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }

    public static Graphics2D createGraphics(BufferedImage bi) {
        Graphics2D g2d = bi.createGraphics();
        g2d.setRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE, new WeakReference<BufferedImage>(bi));
        g2d.clip(new Rectangle(0, 0, bi.getWidth(), bi.getHeight()));
        return g2d;
    }

    public static BufferedImage getDestination(Graphics2D g2d) {
        Object o = g2d.getRenderingHint(RenderingHintsKeyExt.KEY_BUFFERED_IMAGE);
        if (o != null) {
            return (BufferedImage)((Reference)o).get();
        }
        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null) {
            return null;
        }
        GraphicsDevice gd = gc.getDevice();
        if (WARN_DESTINATION && gd.getType() == 2 && g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) != "Printing") {
            System.err.println("Graphics2D from BufferedImage lacks BUFFERED_IMAGE hint");
        }
        return null;
    }

    public static ColorModel getDestinationColorModel(Graphics2D g2d) {
        BufferedImage bi = GraphicsUtil.getDestination(g2d);
        if (bi != null) {
            return bi.getColorModel();
        }
        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null) {
            return null;
        }
        if (gc.getDevice().getType() == 2) {
            if (g2d.getRenderingHint(RenderingHintsKeyExt.KEY_TRANSCODING) == "Printing") {
                return sRGB_Unpre;
            }
            return null;
        }
        return gc.getColorModel();
    }

    public static ColorSpace getDestinationColorSpace(Graphics2D g2d) {
        ColorModel cm = GraphicsUtil.getDestinationColorModel(g2d);
        if (cm != null) {
            return cm.getColorSpace();
        }
        return null;
    }

    public static Rectangle getDestinationBounds(Graphics2D g2d) {
        BufferedImage bi = GraphicsUtil.getDestination(g2d);
        if (bi != null) {
            return new Rectangle(0, 0, bi.getWidth(), bi.getHeight());
        }
        GraphicsConfiguration gc = g2d.getDeviceConfiguration();
        if (gc == null) {
            return null;
        }
        if (gc.getDevice().getType() == 2) {
            return null;
        }
        return null;
    }

    public static ColorModel makeLinear_sRGBCM(boolean premult) {
        return premult ? Linear_sRGB_Pre : Linear_sRGB_Unpre;
    }

    public static BufferedImage makeLinearBufferedImage(int width, int height, boolean premult) {
        ColorModel cm = GraphicsUtil.makeLinear_sRGBCM(premult);
        WritableRaster wr = cm.createCompatibleWritableRaster(width, height);
        return new BufferedImage(cm, wr, premult, null);
    }

    public static CachableRed convertToLsRGB(CachableRed src) {
        ColorModel cm = src.getColorModel();
        ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(1004)) {
            return src;
        }
        return new Any2LsRGBRed(src);
    }

    public static CachableRed convertTosRGB(CachableRed src) {
        ColorModel cm = src.getColorModel();
        ColorSpace cs = cm.getColorSpace();
        if (cs == ColorSpace.getInstance(1000)) {
            return src;
        }
        return new Any2sRGBRed(src);
    }

    public static CachableRed wrap(RenderedImage ri) {
        if (ri instanceof CachableRed) {
            return (CachableRed)ri;
        }
        if (ri instanceof BufferedImage) {
            return new BufferedImageCachableRed((BufferedImage)ri);
        }
        return new RenderedImageCachableRed(ri);
    }

    public static void copyData_INT_PACK(Raster src, WritableRaster dst) {
        int y1;
        int x1;
        int y0;
        int x0 = dst.getMinX();
        if (x0 < src.getMinX()) {
            x0 = src.getMinX();
        }
        if ((y0 = dst.getMinY()) < src.getMinY()) {
            y0 = src.getMinY();
        }
        if ((x1 = dst.getMinX() + dst.getWidth() - 1) > src.getMinX() + src.getWidth() - 1) {
            x1 = src.getMinX() + src.getWidth() - 1;
        }
        if ((y1 = dst.getMinY() + dst.getHeight() - 1) > src.getMinY() + src.getHeight() - 1) {
            y1 = src.getMinY() + src.getHeight() - 1;
        }
        int width = x1 - x0 + 1;
        int height = y1 - y0 + 1;
        SinglePixelPackedSampleModel srcSPPSM = (SinglePixelPackedSampleModel)src.getSampleModel();
        int srcScanStride = srcSPPSM.getScanlineStride();
        DataBufferInt srcDB = (DataBufferInt)src.getDataBuffer();
        int[] srcPixels = srcDB.getBankData()[0];
        int srcBase = srcDB.getOffset() + srcSPPSM.getOffset(x0 - src.getSampleModelTranslateX(), y0 - src.getSampleModelTranslateY());
        SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        int dstScanStride = dstSPPSM.getScanlineStride();
        DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        int[] dstPixels = dstDB.getBankData()[0];
        int dstBase = dstDB.getOffset() + dstSPPSM.getOffset(x0 - dst.getSampleModelTranslateX(), y0 - dst.getSampleModelTranslateY());
        if (srcScanStride == dstScanStride && srcScanStride == width) {
            System.arraycopy(srcPixels, srcBase, dstPixels, dstBase, width * height);
        } else if (width > 128) {
            int srcSP = srcBase;
            int dstSP = dstBase;
            for (int y = 0; y < height; ++y) {
                System.arraycopy(srcPixels, srcSP, dstPixels, dstSP, width);
                srcSP += srcScanStride;
                dstSP += dstScanStride;
            }
        } else {
            for (int y = 0; y < height; ++y) {
                int srcSP = srcBase + y * srcScanStride;
                int dstSP = dstBase + y * dstScanStride;
                for (int x = 0; x < width; ++x) {
                    dstPixels[dstSP++] = srcPixels[srcSP++];
                }
            }
        }
    }

    public static void copyData_FALLBACK(Raster src, WritableRaster dst) {
        int y1;
        int x1;
        int y0;
        int x0 = dst.getMinX();
        if (x0 < src.getMinX()) {
            x0 = src.getMinX();
        }
        if ((y0 = dst.getMinY()) < src.getMinY()) {
            y0 = src.getMinY();
        }
        if ((x1 = dst.getMinX() + dst.getWidth() - 1) > src.getMinX() + src.getWidth() - 1) {
            x1 = src.getMinX() + src.getWidth() - 1;
        }
        if ((y1 = dst.getMinY() + dst.getHeight() - 1) > src.getMinY() + src.getHeight() - 1) {
            y1 = src.getMinY() + src.getHeight() - 1;
        }
        int width = x1 - x0 + 1;
        int[] data = null;
        for (int y = y0; y <= y1; ++y) {
            data = src.getPixels(x0, y, width, 1, data);
            dst.setPixels(x0, y, width, 1, data);
        }
    }

    public static void copyData(Raster src, WritableRaster dst) {
        if (GraphicsUtil.is_INT_PACK_Data(src.getSampleModel(), false) && GraphicsUtil.is_INT_PACK_Data(dst.getSampleModel(), false)) {
            GraphicsUtil.copyData_INT_PACK(src, dst);
            return;
        }
        GraphicsUtil.copyData_FALLBACK(src, dst);
    }

    public static WritableRaster copyRaster(Raster ras) {
        return GraphicsUtil.copyRaster(ras, ras.getMinX(), ras.getMinY());
    }

    public static WritableRaster copyRaster(Raster ras, int minX, int minY) {
        WritableRaster ret = Raster.createWritableRaster(ras.getSampleModel(), new Point(0, 0));
        ret = ret.createWritableChild(ras.getMinX() - ras.getSampleModelTranslateX(), ras.getMinY() - ras.getSampleModelTranslateY(), ras.getWidth(), ras.getHeight(), minX, minY, null);
        DataBuffer srcDB = ras.getDataBuffer();
        DataBuffer retDB = ret.getDataBuffer();
        if (srcDB.getDataType() != retDB.getDataType()) {
            throw new IllegalArgumentException("New DataBuffer doesn't match original");
        }
        int len = srcDB.getSize();
        int banks = srcDB.getNumBanks();
        int[] offsets = srcDB.getOffsets();
        block6: for (int b = 0; b < banks; ++b) {
            switch (srcDB.getDataType()) {
                case 0: {
                    DataBuffer srcDBT = (DataBufferByte)srcDB;
                    DataBuffer retDBT = (DataBufferByte)retDB;
                    System.arraycopy(((DataBufferByte)srcDBT).getData(b), offsets[b], ((DataBufferByte)retDBT).getData(b), offsets[b], len);
                    continue block6;
                }
                case 3: {
                    DataBuffer srcDBT = (DataBufferInt)srcDB;
                    DataBuffer retDBT = (DataBufferInt)retDB;
                    System.arraycopy(((DataBufferInt)srcDBT).getData(b), offsets[b], ((DataBufferInt)retDBT).getData(b), offsets[b], len);
                    continue block6;
                }
                case 2: {
                    DataBuffer srcDBT = (DataBufferShort)srcDB;
                    DataBuffer retDBT = (DataBufferShort)retDB;
                    System.arraycopy(((DataBufferShort)srcDBT).getData(b), offsets[b], ((DataBufferShort)retDBT).getData(b), offsets[b], len);
                    continue block6;
                }
                case 1: {
                    DataBuffer srcDBT = (DataBufferUShort)srcDB;
                    DataBuffer retDBT = (DataBufferUShort)retDB;
                    System.arraycopy(((DataBufferUShort)srcDBT).getData(b), offsets[b], ((DataBufferUShort)retDBT).getData(b), offsets[b], len);
                    continue block6;
                }
            }
        }
        return ret;
    }

    public static WritableRaster makeRasterWritable(Raster ras) {
        return GraphicsUtil.makeRasterWritable(ras, ras.getMinX(), ras.getMinY());
    }

    public static WritableRaster makeRasterWritable(Raster ras, int minX, int minY) {
        WritableRaster ret = Raster.createWritableRaster(ras.getSampleModel(), ras.getDataBuffer(), new Point(0, 0));
        ret = ret.createWritableChild(ras.getMinX() - ras.getSampleModelTranslateX(), ras.getMinY() - ras.getSampleModelTranslateY(), ras.getWidth(), ras.getHeight(), minX, minY, null);
        return ret;
    }

    public static ColorModel coerceColorModel(ColorModel cm, boolean newAlphaPreMult) {
        if (cm.isAlphaPremultiplied() == newAlphaPreMult) {
            return cm;
        }
        WritableRaster wr = cm.createCompatibleWritableRaster(1, 1);
        return cm.coerceData(wr, newAlphaPreMult);
    }

    public static ColorModel coerceData(WritableRaster wr, ColorModel cm, boolean newAlphaPreMult) {
        if (!cm.hasAlpha()) {
            return cm;
        }
        if (cm.isAlphaPremultiplied() == newAlphaPreMult) {
            return cm;
        }
        if (newAlphaPreMult) {
            GraphicsUtil.multiplyAlpha(wr);
        } else {
            GraphicsUtil.divideAlpha(wr);
        }
        return GraphicsUtil.coerceColorModel(cm, newAlphaPreMult);
    }

    public static void multiplyAlpha(WritableRaster wr) {
        if (GraphicsUtil.is_BYTE_COMP_Data(wr.getSampleModel())) {
            GraphicsUtil.mult_BYTE_COMP_Data(wr);
        } else if (GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), true)) {
            GraphicsUtil.mult_INT_PACK_Data(wr);
        } else {
            int[] pixel = null;
            int bands = wr.getNumBands();
            float norm = 0.003921569f;
            int x0 = wr.getMinX();
            int x1 = x0 + wr.getWidth();
            int y0 = wr.getMinY();
            int y1 = y0 + wr.getHeight();
            for (int y = y0; y < y1; ++y) {
                for (int x = x0; x < x1; ++x) {
                    int a = (pixel = wr.getPixel(x, y, pixel))[bands - 1];
                    if (a < 0 || a >= 255) continue;
                    float alpha = (float)a * norm;
                    for (int b = 0; b < bands - 1; ++b) {
                        pixel[b] = (int)((float)pixel[b] * alpha + 0.5f);
                    }
                    wr.setPixel(x, y, pixel);
                }
            }
        }
    }

    public static void divideAlpha(WritableRaster wr) {
        if (GraphicsUtil.is_BYTE_COMP_Data(wr.getSampleModel())) {
            GraphicsUtil.divide_BYTE_COMP_Data(wr);
        } else if (GraphicsUtil.is_INT_PACK_Data(wr.getSampleModel(), true)) {
            GraphicsUtil.divide_INT_PACK_Data(wr);
        } else {
            int bands = wr.getNumBands();
            int[] pixel = null;
            int x0 = wr.getMinX();
            int x1 = x0 + wr.getWidth();
            int y0 = wr.getMinY();
            int y1 = y0 + wr.getHeight();
            for (int y = y0; y < y1; ++y) {
                for (int x = x0; x < x1; ++x) {
                    int a = (pixel = wr.getPixel(x, y, pixel))[bands - 1];
                    if (a <= 0 || a >= 255) continue;
                    float ialpha = 255.0f / (float)a;
                    for (int b = 0; b < bands - 1; ++b) {
                        pixel[b] = (int)((float)pixel[b] * ialpha + 0.5f);
                    }
                    wr.setPixel(x, y, pixel);
                }
            }
        }
    }

    public static void copyData(BufferedImage src, BufferedImage dst) {
        Rectangle srcRect = new Rectangle(0, 0, src.getWidth(), src.getHeight());
        GraphicsUtil.copyData(src, srcRect, dst, new Point(0, 0));
    }

    public static void copyData(BufferedImage src, Rectangle srcRect, BufferedImage dst, Point destP) {
        boolean dstAlpha;
        boolean srcAlpha = src.getColorModel().hasAlpha();
        if (!(srcAlpha != (dstAlpha = dst.getColorModel().hasAlpha()) || srcAlpha && src.isAlphaPremultiplied() != dst.isAlphaPremultiplied())) {
            GraphicsUtil.copyData(src.getRaster(), dst.getRaster());
            return;
        }
        int[] pixel = null;
        WritableRaster srcR = src.getRaster();
        WritableRaster dstR = dst.getRaster();
        int bands = dstR.getNumBands();
        int dx = destP.x - srcRect.x;
        int dy = destP.y - srcRect.y;
        int w = srcRect.width;
        int x0 = srcRect.x;
        int y0 = srcRect.y;
        int y1 = y0 + srcRect.height - 1;
        if (!srcAlpha) {
            int out;
            int[] oPix = new int[bands * w];
            for (out = w * bands - 1; out >= 0; out -= bands) {
                oPix[out] = 255;
            }
            for (int y = y0; y <= y1; ++y) {
                pixel = srcR.getPixels(x0, y, w, 1, pixel);
                int in = w * (bands - 1) - 1;
                out = w * bands - 2;
                switch (bands) {
                    case 4: {
                        while (in >= 0) {
                            oPix[out--] = pixel[in--];
                            oPix[out--] = pixel[in--];
                            oPix[out--] = pixel[in--];
                            --out;
                        }
                        break;
                    }
                    default: {
                        while (in >= 0) {
                            for (int b = 0; b < bands - 1; ++b) {
                                oPix[out--] = pixel[in--];
                            }
                            --out;
                        }
                        break block0;
                    }
                }
                dstR.setPixels(x0 + dx, y + dy, w, 1, oPix);
            }
        } else if (dstAlpha && dst.isAlphaPremultiplied()) {
            int fpNorm = 65793;
            int pt5 = 0x800000;
            for (int y = y0; y <= y1; ++y) {
                pixel = srcR.getPixels(x0, y, w, 1, pixel);
                int in = bands * w - 1;
                switch (bands) {
                    case 4: {
                        int alpha;
                        int a;
                        while (in >= 0) {
                            a = pixel[in];
                            if (a == 255) {
                                in -= 4;
                                continue;
                            }
                            alpha = fpNorm * a;
                            pixel[--in] = pixel[in] * alpha + pt5 >>> 24;
                            pixel[--in] = pixel[in] * alpha + pt5 >>> 24;
                            pixel[--in] = pixel[in] * alpha + pt5 >>> 24;
                            --in;
                        }
                        break;
                    }
                    default: {
                        int alpha;
                        int a;
                        while (in >= 0) {
                            a = pixel[in];
                            if (a == 255) {
                                in -= bands;
                                continue;
                            }
                            --in;
                            alpha = fpNorm * a;
                            for (int b = 0; b < bands - 1; ++b) {
                                pixel[in] = pixel[in] * alpha + pt5 >>> 24;
                                --in;
                            }
                        }
                        break block3;
                    }
                }
                dstR.setPixels(x0 + dx, y + dy, w, 1, pixel);
            }
        } else if (dstAlpha && !dst.isAlphaPremultiplied()) {
            int fpNorm = 0xFF0000;
            int pt5 = 32768;
            for (int y = y0; y <= y1; ++y) {
                pixel = srcR.getPixels(x0, y, w, 1, pixel);
                int in = bands * w - 1;
                switch (bands) {
                    case 4: {
                        int ialpha;
                        int a;
                        while (in >= 0) {
                            a = pixel[in];
                            if (a <= 0 || a >= 255) {
                                in -= 4;
                                continue;
                            }
                            ialpha = fpNorm / a;
                            pixel[--in] = pixel[in] * ialpha + pt5 >>> 16;
                            pixel[--in] = pixel[in] * ialpha + pt5 >>> 16;
                            pixel[--in] = pixel[in] * ialpha + pt5 >>> 16;
                            --in;
                        }
                        break;
                    }
                    default: {
                        int ialpha;
                        int a;
                        while (in >= 0) {
                            a = pixel[in];
                            if (a <= 0 || a >= 255) {
                                in -= bands;
                                continue;
                            }
                            --in;
                            ialpha = fpNorm / a;
                            for (int b = 0; b < bands - 1; ++b) {
                                pixel[in] = pixel[in] * ialpha + pt5 >>> 16;
                                --in;
                            }
                        }
                        break block6;
                    }
                }
                dstR.setPixels(x0 + dx, y + dy, w, 1, pixel);
            }
        } else if (src.isAlphaPremultiplied()) {
            int[] oPix = new int[bands * w];
            int fpNorm = 0xFF0000;
            int pt5 = 32768;
            for (int y = y0; y <= y1; ++y) {
                pixel = srcR.getPixels(x0, y, w, 1, pixel);
                int in = (bands + 1) * w - 1;
                int out = bands * w - 1;
                while (in >= 0) {
                    int b;
                    int a = pixel[in];
                    --in;
                    if (a > 0) {
                        if (a < 255) {
                            int ialpha = fpNorm / a;
                            for (b = 0; b < bands; ++b) {
                                oPix[out--] = pixel[in--] * ialpha + pt5 >>> 16;
                            }
                            continue;
                        }
                        for (b = 0; b < bands; ++b) {
                            oPix[out--] = pixel[in--];
                        }
                        continue;
                    }
                    in -= bands;
                    for (b = 0; b < bands; ++b) {
                        oPix[out--] = 255;
                    }
                }
                dstR.setPixels(x0 + dx, y + dy, w, 1, oPix);
            }
        } else {
            Rectangle dstRect = new Rectangle(destP.x, destP.y, srcRect.width, srcRect.height);
            for (int b = 0; b < bands; ++b) {
                GraphicsUtil.copyBand(srcR, srcRect, b, dstR, dstRect, b);
            }
        }
    }

    public static void copyBand(Raster src, int srcBand, WritableRaster dst, int dstBand) {
        Rectangle sR = src.getBounds();
        Rectangle dR = dst.getBounds();
        Rectangle cpR = sR.intersection(dR);
        GraphicsUtil.copyBand(src, cpR, srcBand, dst, cpR, dstBand);
    }

    public static void copyBand(Raster src, Rectangle sR, int sBand, WritableRaster dst, Rectangle dR, int dBand) {
        int dy = dR.y - sR.y;
        int dx = dR.x - sR.x;
        sR = sR.intersection(src.getBounds());
        dR = dR.intersection(dst.getBounds());
        int width = dR.width < sR.width ? dR.width : sR.width;
        int height = dR.height < sR.height ? dR.height : sR.height;
        int x = sR.x + dx;
        int[] samples = null;
        for (int y = sR.y; y < sR.y + height; ++y) {
            samples = src.getSamples(sR.x, y, width, 1, sBand, samples);
            dst.setSamples(x, y + dy, width, 1, dBand, samples);
        }
    }

    public static boolean is_INT_PACK_Data(SampleModel sm, boolean requireAlpha) {
        if (!(sm instanceof SinglePixelPackedSampleModel)) {
            return false;
        }
        if (sm.getDataType() != 3) {
            return false;
        }
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)sm;
        int[] masks = sppsm.getBitMasks();
        if (masks.length == 3 ? requireAlpha : masks.length != 4) {
            return false;
        }
        if (masks[0] != 0xFF0000) {
            return false;
        }
        if (masks[1] != 65280) {
            return false;
        }
        if (masks[2] != 255) {
            return false;
        }
        return masks.length != 4 || masks[3] == -16777216;
    }

    public static boolean is_BYTE_COMP_Data(SampleModel sm) {
        if (!(sm instanceof ComponentSampleModel)) {
            return false;
        }
        return sm.getDataType() == 0;
    }

    protected static void divide_INT_PACK_Data(WritableRaster wr) {
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        int width = wr.getWidth();
        int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        int base = db.getOffset() + sppsm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        int[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            int sp;
            int end = sp + width;
            for (sp = base + y * scanStride; sp < end; ++sp) {
                int pixel = pixels[sp];
                int a = pixel >>> 24;
                if (a <= 0) {
                    pixels[sp] = 0xFFFFFF;
                    continue;
                }
                if (a >= 255) continue;
                int aFP = 0xFF0000 / a;
                pixels[sp] = a << 24 | ((pixel & 0xFF0000) >> 16) * aFP & 0xFF0000 | (((pixel & 0xFF00) >> 8) * aFP & 0xFF0000) >> 8 | ((pixel & 0xFF) * aFP & 0xFF0000) >> 16;
            }
        }
    }

    protected static void mult_INT_PACK_Data(WritableRaster wr) {
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)wr.getSampleModel();
        int width = wr.getWidth();
        int scanStride = sppsm.getScanlineStride();
        DataBufferInt db = (DataBufferInt)wr.getDataBuffer();
        int base = db.getOffset() + sppsm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        int[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            int sp;
            int end = sp + width;
            for (sp = base + y * scanStride; sp < end; ++sp) {
                int pixel = pixels[sp];
                int a = pixel >>> 24;
                if (a < 0 || a >= 255) continue;
                pixels[sp] = a << 24 | (pixel & 0xFF0000) * a >> 8 & 0xFF0000 | (pixel & 0xFF00) * a >> 8 & 0xFF00 | (pixel & 0xFF) * a >> 8 & 0xFF;
            }
        }
    }

    protected static void divide_BYTE_COMP_Data(WritableRaster wr) {
        ComponentSampleModel csm = (ComponentSampleModel)wr.getSampleModel();
        int width = wr.getWidth();
        int scanStride = csm.getScanlineStride();
        int pixStride = csm.getPixelStride();
        int[] bandOff = csm.getBandOffsets();
        DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
        int base = db.getOffset() + csm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        int aOff = bandOff[bandOff.length - 1];
        int bands = bandOff.length - 1;
        byte[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            int sp;
            int end = sp + width * pixStride;
            for (sp = base + y * scanStride; sp < end; sp += pixStride) {
                int a = pixels[sp + aOff] & 0xFF;
                if (a == 0) {
                    for (int b = 0; b < bands; ++b) {
                        pixels[sp + bandOff[b]] = -1;
                    }
                    continue;
                }
                if (a >= 255) continue;
                int aFP = 0xFF0000 / a;
                for (int b = 0; b < bands; ++b) {
                    int i = sp + bandOff[b];
                    pixels[i] = (byte)((pixels[i] & 0xFF) * aFP >>> 16);
                }
            }
        }
    }

    protected static void mult_BYTE_COMP_Data(WritableRaster wr) {
        ComponentSampleModel csm = (ComponentSampleModel)wr.getSampleModel();
        int width = wr.getWidth();
        int scanStride = csm.getScanlineStride();
        int pixStride = csm.getPixelStride();
        int[] bandOff = csm.getBandOffsets();
        DataBufferByte db = (DataBufferByte)wr.getDataBuffer();
        int base = db.getOffset() + csm.getOffset(wr.getMinX() - wr.getSampleModelTranslateX(), wr.getMinY() - wr.getSampleModelTranslateY());
        int aOff = bandOff[bandOff.length - 1];
        int bands = bandOff.length - 1;
        byte[] pixels = db.getBankData()[0];
        for (int y = 0; y < wr.getHeight(); ++y) {
            int sp;
            int end = sp + width * pixStride;
            for (sp = base + y * scanStride; sp < end; sp += pixStride) {
                int a = pixels[sp + aOff] & 0xFF;
                if (a == 255) continue;
                for (int b = 0; b < bands; ++b) {
                    int i = sp + bandOff[b];
                    pixels[i] = (byte)((pixels[i] & 0xFF) * a >> 8);
                }
            }
        }
    }

    static {
        boolean warn = true;
        try {
            String s = System.getProperty("org.apache.batik.warn_destination", "true");
            warn = Boolean.valueOf(s);
        }
        catch (SecurityException securityException) {
        }
        catch (NumberFormatException numberFormatException) {
        }
        finally {
            WARN_DESTINATION = warn;
        }
        Linear_sRGB = new DirectColorModel(ColorSpace.getInstance(1004), 24, 0xFF0000, 65280, 255, 0, false, 3);
        Linear_sRGB_Pre = new DirectColorModel(ColorSpace.getInstance(1004), 32, 0xFF0000, 65280, 255, -16777216, true, 3);
        Linear_sRGB_Unpre = new DirectColorModel(ColorSpace.getInstance(1004), 32, 0xFF0000, 65280, 255, -16777216, false, 3);
        sRGB = new DirectColorModel(ColorSpace.getInstance(1000), 24, 0xFF0000, 65280, 255, 0, false, 3);
        sRGB_Pre = new DirectColorModel(ColorSpace.getInstance(1000), 32, 0xFF0000, 65280, 255, -16777216, true, 3);
        sRGB_Unpre = new DirectColorModel(ColorSpace.getInstance(1000), 32, 0xFF0000, 65280, 255, -16777216, false, 3);
    }
}

