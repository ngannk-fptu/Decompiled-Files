/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.JaiI18N;
import java.awt.Canvas;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.DataBufferInt;
import java.awt.image.PixelGrabber;
import java.awt.image.Raster;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.util.Map;
import javax.media.jai.ImageLayout;
import javax.media.jai.PlanarImage;
import javax.media.jai.RasterAccessor;
import javax.media.jai.RasterFactory;
import javax.media.jai.RasterFormatTag;
import javax.media.jai.SourcelessOpImage;

final class AWTImageOpImage
extends SourcelessOpImage {
    private int[] pixels;
    private RasterFormatTag rasterFormatTag;

    private static final ImageLayout layoutHelper(ImageLayout layout, Image image) {
        MediaTracker tracker = new MediaTracker(new Canvas());
        tracker.addImage(image, 0);
        try {
            tracker.waitForID(0);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(JaiI18N.getString("AWTImageOpImage0"));
        }
        if (tracker.isErrorID(0)) {
            throw new RuntimeException(JaiI18N.getString("AWTImageOpImage1"));
        }
        tracker.removeImage(image);
        if (layout == null) {
            layout = new ImageLayout();
        }
        layout.setMinX(0);
        layout.setMinY(0);
        layout.setWidth(image.getWidth(null));
        layout.setHeight(image.getHeight(null));
        if (!layout.isValid(64)) {
            layout.setTileWidth(layout.getWidth(null));
        }
        if (!layout.isValid(128)) {
            layout.setTileHeight(layout.getHeight(null));
        }
        if (layout.getTileWidth(null) == layout.getWidth(null) && layout.getTileHeight(null) == layout.getHeight(null)) {
            layout.setTileGridXOffset(layout.getMinX(null));
            layout.setTileGridYOffset(layout.getMinY(null));
            int[] bitMasks = new int[]{0xFF0000, 65280, 255};
            layout.setSampleModel(new SinglePixelPackedSampleModel(3, layout.getWidth(null), layout.getHeight(null), bitMasks));
        } else {
            layout.setSampleModel(RasterFactory.createPixelInterleavedSampleModel(0, layout.getTileWidth(null), layout.getTileHeight(null), 3));
        }
        layout.setColorModel(PlanarImage.createColorModel(layout.getSampleModel(null)));
        return layout;
    }

    public AWTImageOpImage(Map config, ImageLayout layout, Image image) {
        layout = AWTImageOpImage.layoutHelper(layout, image);
        super(layout, config, layout.getSampleModel(null), layout.getMinX(null), layout.getMinY(null), layout.getWidth(null), layout.getHeight(null));
        this.rasterFormatTag = null;
        if (this.getTileWidth() != this.getWidth() || this.getTileHeight() != this.getHeight()) {
            this.rasterFormatTag = new RasterFormatTag(this.getSampleModel(), 0);
        }
        this.pixels = new int[this.width * this.height];
        PixelGrabber grabber = new PixelGrabber(image, 0, 0, this.width, this.height, this.pixels, 0, this.width);
        try {
            if (!grabber.grabPixels()) {
                if ((grabber.getStatus() & 0x80) != 0) {
                    throw new RuntimeException(JaiI18N.getString("AWTImageOpImage2"));
                }
                throw new RuntimeException(grabber.getStatus() + JaiI18N.getString("AWTImageOpImage3"));
            }
        }
        catch (InterruptedException e) {
            e.printStackTrace();
            throw new RuntimeException(JaiI18N.getString("AWTImageOpImage4"));
        }
    }

    public Raster computeTile(int tileX, int tileY) {
        if (this.getTileWidth() == this.getWidth() && this.getTileHeight() == this.getHeight()) {
            DataBufferInt dataBuffer = new DataBufferInt(this.pixels, this.pixels.length);
            return Raster.createWritableRaster(this.getSampleModel(), dataBuffer, new Point(this.tileXToX(tileX), this.tileYToY(tileY)));
        }
        return super.computeTile(tileX, tileY);
    }

    protected void computeRect(PlanarImage[] sources, WritableRaster dest, Rectangle destRect) {
        RasterAccessor dst = new RasterAccessor(dest, destRect, this.rasterFormatTag, null);
        int dwidth = dst.getWidth();
        int dheight = dst.getHeight();
        int lineStride = dst.getScanlineStride();
        int pixelStride = dst.getPixelStride();
        int lineOffset0 = dst.getBandOffset(0);
        int lineOffset1 = dst.getBandOffset(1);
        int lineOffset2 = dst.getBandOffset(2);
        byte[] data = dst.getByteDataArray(0);
        int offset = (destRect.y - this.minY) * this.width + (destRect.x - this.minX);
        for (int h = 0; h < dheight; ++h) {
            int pixelOffset0 = lineOffset0;
            int pixelOffset1 = lineOffset1;
            int pixelOffset2 = lineOffset2;
            lineOffset0 += lineStride;
            lineOffset1 += lineStride;
            lineOffset2 += lineStride;
            int i = offset;
            offset += this.width;
            for (int w = 0; w < dwidth; ++w) {
                data[pixelOffset0] = (byte)(this.pixels[i] >> 16 & 0xFF);
                data[pixelOffset1] = (byte)(this.pixels[i] >> 8 & 0xFF);
                data[pixelOffset2] = (byte)(this.pixels[i] & 0xFF);
                pixelOffset0 += pixelStride;
                pixelOffset1 += pixelStride;
                pixelOffset2 += pixelStride;
                ++i;
            }
        }
    }
}

