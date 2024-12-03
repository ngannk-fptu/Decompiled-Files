/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.rendered;

import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.DataBufferInt;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import org.apache.batik.ext.awt.image.ARGBChannel;
import org.apache.batik.ext.awt.image.GraphicsUtil;
import org.apache.batik.ext.awt.image.PadMode;
import org.apache.batik.ext.awt.image.rendered.AbstractRed;
import org.apache.batik.ext.awt.image.rendered.CachableRed;
import org.apache.batik.ext.awt.image.rendered.PadRed;
import org.apache.batik.ext.awt.image.rendered.TileCacheRed;

public class DisplacementMapRed
extends AbstractRed {
    private static final boolean TIME = false;
    private static final boolean USE_NN = false;
    private float scaleX;
    private float scaleY;
    private ARGBChannel xChannel;
    private ARGBChannel yChannel;
    CachableRed image;
    CachableRed offsets;
    int maxOffX;
    int maxOffY;
    RenderingHints hints;
    TileOffsets[] xOffsets;
    TileOffsets[] yOffsets;

    public DisplacementMapRed(CachableRed image, CachableRed offsets, ARGBChannel xChannel, ARGBChannel yChannel, float scaleX, float scaleY, RenderingHints rh) {
        if (xChannel == null) {
            throw new IllegalArgumentException("Must provide xChannel");
        }
        if (yChannel == null) {
            throw new IllegalArgumentException("Must provide yChannel");
        }
        this.offsets = offsets;
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        this.xChannel = xChannel;
        this.yChannel = yChannel;
        this.hints = rh;
        this.maxOffX = (int)Math.ceil(scaleX / 2.0f);
        this.maxOffY = (int)Math.ceil(scaleY / 2.0f);
        Rectangle rect = image.getBounds();
        Rectangle r = image.getBounds();
        r.x -= this.maxOffX;
        r.width += 2 * this.maxOffX;
        r.y -= this.maxOffY;
        r.height += 2 * this.maxOffY;
        image = new PadRed(image, r, PadMode.ZERO_PAD, null);
        this.image = image = new TileCacheRed(image);
        ColorModel cm = image.getColorModel();
        cm = GraphicsUtil.coerceColorModel(cm, true);
        this.init(image, rect, cm, image.getSampleModel(), rect.x, rect.y, null);
        this.xOffsets = new TileOffsets[this.getNumXTiles()];
        this.yOffsets = new TileOffsets[this.getNumYTiles()];
    }

    @Override
    public WritableRaster copyData(WritableRaster wr) {
        this.copyToRaster(wr);
        return wr;
    }

    @Override
    public Raster getTile(int tileX, int tileY) {
        WritableRaster dest = this.makeTile(tileX, tileY);
        Rectangle srcR = dest.getBounds();
        Raster mapRas = this.offsets.getData(srcR);
        ColorModel mapCM = this.offsets.getColorModel();
        GraphicsUtil.coerceData((WritableRaster)mapRas, mapCM, false);
        TileOffsets xinfo = this.getXOffsets(tileX);
        TileOffsets yinfo = this.getYOffsets(tileY);
        if (this.image.getColorModel().isAlphaPremultiplied()) {
            this.filterBL(mapRas, dest, xinfo.tile, xinfo.off, yinfo.tile, yinfo.off);
        } else {
            this.filterBLPre(mapRas, dest, xinfo.tile, xinfo.off, yinfo.tile, yinfo.off);
        }
        return dest;
    }

    public TileOffsets getXOffsets(int xTile) {
        TileOffsets ret = this.xOffsets[xTile - this.getMinTileX()];
        if (ret != null) {
            return ret;
        }
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)this.getSampleModel();
        int base = sppsm.getOffset(0, 0);
        int tw = sppsm.getWidth();
        int width = tw + 2 * this.maxOffX;
        int x0 = this.getTileGridXOffset() + xTile * tw - this.maxOffX - this.image.getTileGridXOffset();
        int x1 = x0 + width - 1;
        int tile = (int)Math.floor((double)x0 / (double)tw);
        int endTile = (int)Math.floor((double)x1 / (double)tw);
        int loc = x0 - tile * tw;
        int endLoc = tw;
        int slop = (endTile + 1) * tw - 1 - x1;
        this.xOffsets[xTile - this.getMinTileX()] = ret = new TileOffsets(width, base, 1, loc, endLoc, slop, tile, endTile);
        return ret;
    }

    public TileOffsets getYOffsets(int yTile) {
        TileOffsets ret = this.yOffsets[yTile - this.getMinTileY()];
        if (ret != null) {
            return ret;
        }
        SinglePixelPackedSampleModel sppsm = (SinglePixelPackedSampleModel)this.getSampleModel();
        int stride = sppsm.getScanlineStride();
        int th = sppsm.getHeight();
        int height = th + 2 * this.maxOffY;
        int y0 = this.getTileGridYOffset() + yTile * th - this.maxOffY - this.image.getTileGridYOffset();
        int y1 = y0 + height - 1;
        int tile = (int)Math.floor((double)y0 / (double)th);
        int endTile = (int)Math.floor((double)y1 / (double)th);
        int loc = y0 - tile * th;
        int endLoc = th;
        int slop = (endTile + 1) * th - 1 - y1;
        this.yOffsets[yTile - this.getMinTileY()] = ret = new TileOffsets(height, 0, stride, loc, endLoc, slop, tile, endTile);
        return ret;
    }

    public void filterBL(Raster off, WritableRaster dst, int[] xTile, int[] xOff, int[] yTile, int[] yOff) {
        int w = dst.getWidth();
        int h = dst.getHeight();
        int xStart = this.maxOffX;
        int yStart = this.maxOffY;
        int xEnd = xStart + w;
        int yEnd = yStart + h;
        DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        DataBufferInt offDB = (DataBufferInt)off.getDataBuffer();
        SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dst.getMinX() - dst.getSampleModelTranslateX(), dst.getMinY() - dst.getSampleModelTranslateY());
        SinglePixelPackedSampleModel offSPPSM = (SinglePixelPackedSampleModel)off.getSampleModel();
        int offOff = offDB.getOffset() + offSPPSM.getOffset(dst.getMinX() - off.getSampleModelTranslateX(), dst.getMinY() - off.getSampleModelTranslateY());
        int dstScanStride = dstSPPSM.getScanlineStride();
        int offScanStride = offSPPSM.getScanlineStride();
        int dstAdjust = dstScanStride - w;
        int offAdjust = offScanStride - w;
        int[] dstPixels = dstDB.getBankData()[0];
        int[] offPixels = offDB.getBankData()[0];
        int xShift = this.xChannel.toInt() * 8;
        int yShift = this.yChannel.toInt() * 8;
        int dp = dstOff;
        int ip = offOff;
        int fpScaleX = (int)((double)this.scaleX / 255.0 * 32768.0 + 0.5);
        int fpAdjX = (int)(-127.5 * (double)fpScaleX - 0.5);
        int fpScaleY = (int)((double)this.scaleY / 255.0 * 32768.0 + 0.5);
        int fpAdjY = (int)(-127.5 * (double)fpScaleY - 0.5);
        long start = System.currentTimeMillis();
        int xt = xTile[0] - 1;
        int yt = yTile[0] - 1;
        int[] imgPix = null;
        for (int y = yStart; y < yEnd; ++y) {
            int x = xStart;
            while (x < xEnd) {
                int pel11;
                int pel01;
                int pel10;
                int dPel = offPixels[ip];
                int xDisplace = fpScaleX * (dPel >> xShift & 0xFF) + fpAdjX;
                int yDisplace = fpScaleY * (dPel >> yShift & 0xFF) + fpAdjY;
                int x0 = x + (xDisplace >> 15);
                int y0 = y + (yDisplace >> 15);
                if (xt != xTile[x0] || yt != yTile[y0]) {
                    xt = xTile[x0];
                    yt = yTile[y0];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt).getDataBuffer()).getBankData()[0];
                }
                void pel00 = imgPix[xOff[x0] + yOff[y0]];
                int xt1 = xTile[x0 + 1];
                int yt1 = yTile[y0 + 1];
                if (yt == yt1) {
                    if (xt == xt1) {
                        pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                        pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                        pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                    } else {
                        pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                        imgPix = ((DataBufferInt)this.image.getTile(xt1, yt).getDataBuffer()).getBankData()[0];
                        pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                        pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                        xt = xt1;
                    }
                } else if (xt == xt1) {
                    pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt1).getDataBuffer()).getBankData()[0];
                    pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                    pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                    yt = yt1;
                } else {
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt1).getDataBuffer()).getBankData()[0];
                    pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt1, yt1).getDataBuffer()).getBankData()[0];
                    pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt1, yt).getDataBuffer()).getBankData()[0];
                    pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                    xt = xt1;
                }
                int xFrac = xDisplace & Short.MAX_VALUE;
                int yFrac = yDisplace & Short.MAX_VALUE;
                int sp0 = pel00 >>> 16 & 0xFF00;
                int sp1 = pel10 >>> 16 & 0xFF00;
                int pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = pel01 >>> 16 & 0xFF00;
                sp1 = pel11 >>> 16 & 0xFF00;
                int pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                int newPel = ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) << 1;
                sp0 = pel00 >> 8 & 0xFF00;
                sp1 = pel10 >> 8 & 0xFF00;
                pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = pel01 >> 8 & 0xFF00;
                sp1 = pel11 >> 8 & 0xFF00;
                pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                newPel |= ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) >>> 7;
                sp0 = pel00 & 0xFF00;
                sp1 = pel10 & 0xFF00;
                pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = pel01 & 0xFF00;
                sp1 = pel11 & 0xFF00;
                pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                newPel |= ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) >>> 15;
                sp0 = pel00 << 8 & 0xFF00;
                sp1 = pel10 << 8 & 0xFF00;
                pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = pel01 << 8 & 0xFF00;
                sp1 = pel11 << 8 & 0xFF00;
                pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                dstPixels[dp] = newPel |= ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) >>> 23;
                ++x;
                ++dp;
                ++ip;
            }
            dp += dstAdjust;
            ip += offAdjust;
        }
    }

    public void filterBLPre(Raster off, WritableRaster dst, int[] xTile, int[] xOff, int[] yTile, int[] yOff) {
        int w = dst.getWidth();
        int h = dst.getHeight();
        int xStart = this.maxOffX;
        int yStart = this.maxOffY;
        int xEnd = xStart + w;
        int yEnd = yStart + h;
        DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        DataBufferInt offDB = (DataBufferInt)off.getDataBuffer();
        SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dst.getMinX() - dst.getSampleModelTranslateX(), dst.getMinY() - dst.getSampleModelTranslateY());
        SinglePixelPackedSampleModel offSPPSM = (SinglePixelPackedSampleModel)off.getSampleModel();
        int offOff = offDB.getOffset() + offSPPSM.getOffset(dst.getMinX() - off.getSampleModelTranslateX(), dst.getMinY() - off.getSampleModelTranslateY());
        int dstScanStride = dstSPPSM.getScanlineStride();
        int offScanStride = offSPPSM.getScanlineStride();
        int dstAdjust = dstScanStride - w;
        int offAdjust = offScanStride - w;
        int[] dstPixels = dstDB.getBankData()[0];
        int[] offPixels = offDB.getBankData()[0];
        int xShift = this.xChannel.toInt() * 8;
        int yShift = this.yChannel.toInt() * 8;
        int dp = dstOff;
        int ip = offOff;
        int fpScaleX = (int)((double)this.scaleX / 255.0 * 32768.0 + 0.5);
        int fpAdjX = (int)(-127.5 * (double)fpScaleX - 0.5);
        int fpScaleY = (int)((double)this.scaleY / 255.0 * 32768.0 + 0.5);
        int fpAdjY = (int)(-127.5 * (double)fpScaleY - 0.5);
        long start = System.currentTimeMillis();
        int norm = 65793;
        int xt = xTile[0] - 1;
        int yt = yTile[0] - 1;
        int[] imgPix = null;
        for (int y = yStart; y < yEnd; ++y) {
            int x = xStart;
            while (x < xEnd) {
                int pel11;
                int pel01;
                int pel10;
                int dPel = offPixels[ip];
                int xDisplace = fpScaleX * (dPel >> xShift & 0xFF) + fpAdjX;
                int yDisplace = fpScaleY * (dPel >> yShift & 0xFF) + fpAdjY;
                int x0 = x + (xDisplace >> 15);
                int y0 = y + (yDisplace >> 15);
                if (xt != xTile[x0] || yt != yTile[y0]) {
                    xt = xTile[x0];
                    yt = yTile[y0];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt).getDataBuffer()).getBankData()[0];
                }
                void pel00 = imgPix[xOff[x0] + yOff[y0]];
                int xt1 = xTile[x0 + 1];
                int yt1 = yTile[y0 + 1];
                if (yt == yt1) {
                    if (xt == xt1) {
                        pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                        pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                        pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                    } else {
                        pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                        imgPix = ((DataBufferInt)this.image.getTile(xt1, yt).getDataBuffer()).getBankData()[0];
                        pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                        pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                        xt = xt1;
                    }
                } else if (xt == xt1) {
                    pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt1).getDataBuffer()).getBankData()[0];
                    pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                    pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                    yt = yt1;
                } else {
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt1).getDataBuffer()).getBankData()[0];
                    pel01 = imgPix[xOff[x0] + yOff[y0 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt1, yt1).getDataBuffer()).getBankData()[0];
                    pel11 = imgPix[xOff[x0 + 1] + yOff[y0 + 1]];
                    imgPix = ((DataBufferInt)this.image.getTile(xt1, yt).getDataBuffer()).getBankData()[0];
                    pel10 = imgPix[xOff[x0 + 1] + yOff[y0]];
                    xt = xt1;
                }
                int xFrac = xDisplace & Short.MAX_VALUE;
                int yFrac = yDisplace & Short.MAX_VALUE;
                int sp0 = pel00 >>> 16 & 0xFF00;
                int sp1 = pel10 >>> 16 & 0xFF00;
                int pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                int a00 = (sp0 >> 8) * 65793 + 128 >> 8;
                int a10 = (sp1 >> 8) * 65793 + 128 >> 8;
                sp0 = pel01 >>> 16 & 0xFF00;
                sp1 = pel11 >>> 16 & 0xFF00;
                int pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                int a01 = (sp0 >> 8) * 65793 + 128 >> 8;
                int a11 = (sp1 >> 8) * 65793 + 128 >> 8;
                int newPel = ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) << 1;
                sp0 = (pel00 >> 16 & 0xFF) * a00 + 128 >> 8;
                sp1 = (pel10 >> 16 & 0xFF) * a10 + 128 >> 8;
                pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = (pel01 >> 16 & 0xFF) * a01 + 128 >> 8;
                sp1 = (pel11 >> 16 & 0xFF) * a11 + 128 >> 8;
                pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                newPel |= ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) >>> 7;
                sp0 = (pel00 >> 8 & 0xFF) * a00 + 128 >> 8;
                sp1 = (pel10 >> 8 & 0xFF) * a10 + 128 >> 8;
                pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = (pel01 >> 8 & 0xFF) * a01 + 128 >> 8;
                sp1 = (pel11 >> 8 & 0xFF) * a11 + 128 >> 8;
                pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                newPel |= ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) >>> 15;
                sp0 = (pel00 & 0xFF) * a00 + 128 >> 8;
                sp1 = (pel10 & 0xFF) * a10 + 128 >> 8;
                pel0 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                sp0 = (pel01 & 0xFF) * a01 + 128 >> 8;
                sp1 = (pel11 & 0xFF) * a11 + 128 >> 8;
                pel1 = sp0 + ((sp1 - sp0) * xFrac + 16384 >> 15) & 0xFFFF;
                dstPixels[dp] = newPel |= ((pel0 << 15) + (pel1 - pel0) * yFrac + 0x400000 & 0x7F800000) >>> 23;
                ++x;
                ++dp;
                ++ip;
            }
            dp += dstAdjust;
            ip += offAdjust;
        }
    }

    public void filterNN(Raster off, WritableRaster dst, int[] xTile, int[] xOff, int[] yTile, int[] yOff) {
        int w = dst.getWidth();
        int h = dst.getHeight();
        int xStart = this.maxOffX;
        int yStart = this.maxOffY;
        int xEnd = xStart + w;
        int yEnd = yStart + h;
        DataBufferInt dstDB = (DataBufferInt)dst.getDataBuffer();
        DataBufferInt offDB = (DataBufferInt)off.getDataBuffer();
        SinglePixelPackedSampleModel dstSPPSM = (SinglePixelPackedSampleModel)dst.getSampleModel();
        int dstOff = dstDB.getOffset() + dstSPPSM.getOffset(dst.getMinX() - dst.getSampleModelTranslateX(), dst.getMinY() - dst.getSampleModelTranslateY());
        SinglePixelPackedSampleModel offSPPSM = (SinglePixelPackedSampleModel)off.getSampleModel();
        int offOff = offDB.getOffset() + offSPPSM.getOffset(off.getMinX() - off.getSampleModelTranslateX(), off.getMinY() - off.getSampleModelTranslateY());
        int dstScanStride = dstSPPSM.getScanlineStride();
        int offScanStride = offSPPSM.getScanlineStride();
        int dstAdjust = dstScanStride - w;
        int offAdjust = offScanStride - w;
        int[] dstPixels = dstDB.getBankData()[0];
        int[] offPixels = offDB.getBankData()[0];
        int xShift = this.xChannel.toInt() * 8;
        int yShift = this.yChannel.toInt() * 8;
        int fpScaleX = (int)((double)this.scaleX / 255.0 * 32768.0 + 0.5);
        int fpScaleY = (int)((double)this.scaleY / 255.0 * 32768.0 + 0.5);
        int fpAdjX = (int)(-127.5 * (double)fpScaleX - 0.5) + 16384;
        int fpAdjY = (int)(-127.5 * (double)fpScaleY - 0.5) + 16384;
        int dp = dstOff;
        int ip = offOff;
        long start = System.currentTimeMillis();
        int xt = xTile[0] - 1;
        int yt = yTile[0] - 1;
        int[] imgPix = null;
        for (int y = yStart; y < yEnd; ++y) {
            for (int x = xStart; x < xEnd; ++x) {
                int dPel = offPixels[ip];
                int xDisplace = fpScaleX * (dPel >> xShift & 0xFF) + fpAdjX;
                int yDisplace = fpScaleY * (dPel >> yShift & 0xFF) + fpAdjY;
                int x0 = x + (xDisplace >> 15);
                int y0 = y + (yDisplace >> 15);
                if (xt != xTile[x0] || yt != yTile[y0]) {
                    xt = xTile[x0];
                    yt = yTile[y0];
                    imgPix = ((DataBufferInt)this.image.getTile(xt, yt).getDataBuffer()).getBankData()[0];
                }
                dstPixels[dp] = imgPix[xOff[x0] + yOff[y0]];
                ++dp;
                ++ip;
            }
            dp += dstAdjust;
            ip += offAdjust;
        }
    }

    static class TileOffsets {
        int[] tile;
        int[] off;

        TileOffsets(int len, int base, int stride, int loc, int endLoc, int slop, int tile, int endTile) {
            this.tile = new int[len + 1];
            this.off = new int[len + 1];
            if (tile == endTile) {
                endLoc -= slop;
            }
            for (int i = 0; i < len; ++i) {
                this.tile[i] = tile++;
                this.off[i] = base + loc * stride;
                if (++loc != endLoc) continue;
                loc = 0;
                if (tile != endTile) continue;
                endLoc -= slop;
            }
            this.tile[len] = this.tile[len - 1];
            this.off[len] = this.off[len - 1];
        }
    }
}

