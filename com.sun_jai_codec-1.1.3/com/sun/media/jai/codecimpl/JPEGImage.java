/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.image.codec.jpeg.ImageFormatException
 *  com.sun.image.codec.jpeg.JPEGCodec
 *  com.sun.image.codec.jpeg.JPEGImageDecoder
 */
package com.sun.media.jai.codecimpl;

import com.sun.image.codec.jpeg.ImageFormatException;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageDecoder;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.JPEGDecodeParam;
import com.sun.media.jai.codecimpl.ImagingListenerProxy;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.NoMarkStream;
import com.sun.media.jai.codecimpl.SimpleRenderedImage;
import com.sun.media.jai.codecimpl.util.ImagingException;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentSampleModel;
import java.awt.image.Raster;
import java.io.IOException;
import java.io.InputStream;

class JPEGImage
extends SimpleRenderedImage {
    private static final Object LOCK = new Object();
    private Raster theTile = null;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public JPEGImage(InputStream stream, ImageDecodeParam param) {
        if (stream.markSupported()) {
            stream = new NoMarkStream(stream);
        }
        BufferedImage image = null;
        Object object = LOCK;
        synchronized (object) {
            JPEGImageDecoder decoder = JPEGCodec.createJPEGDecoder((InputStream)stream);
            try {
                image = decoder.decodeAsBufferedImage();
            }
            catch (ImageFormatException e) {
                String message = JaiI18N.getString("JPEGImageDecoder1");
                this.sendExceptionToListener(message, (Exception)((Object)e));
            }
            catch (IOException e) {
                String message = JaiI18N.getString("JPEGImageDecoder1");
                this.sendExceptionToListener(message, e);
            }
        }
        this.minX = 0;
        this.minY = 0;
        this.tileWidth = this.width = image.getWidth();
        this.tileHeight = this.height = image.getHeight();
        if ((param == null || param instanceof JPEGDecodeParam && ((JPEGDecodeParam)param).getDecodeToCSM()) && !(image.getSampleModel() instanceof ComponentSampleModel)) {
            int type = -1;
            int numBands = image.getSampleModel().getNumBands();
            if (numBands == 1) {
                type = 10;
            } else if (numBands == 3) {
                type = 5;
            } else if (numBands == 4) {
                type = 6;
            } else {
                throw new RuntimeException(JaiI18N.getString("JPEGImageDecoder3"));
            }
            BufferedImage bi = new BufferedImage(this.width, this.height, type);
            bi.getWritableTile(0, 0).setRect(image.getWritableTile(0, 0));
            bi.releaseWritableTile(0, 0);
            image = bi;
        }
        this.sampleModel = image.getSampleModel();
        this.colorModel = image.getColorModel();
        this.theTile = image.getWritableTile(0, 0);
    }

    public synchronized Raster getTile(int tileX, int tileY) {
        if (tileX != 0 || tileY != 0) {
            throw new IllegalArgumentException(JaiI18N.getString("JPEGImageDecoder4"));
        }
        return this.theTile;
    }

    public void dispose() {
        this.theTile = null;
    }

    private void sendExceptionToListener(String message, Exception e) {
        ImagingListenerProxy.errorOccurred(message, new ImagingException(message, e), this, false);
    }
}

