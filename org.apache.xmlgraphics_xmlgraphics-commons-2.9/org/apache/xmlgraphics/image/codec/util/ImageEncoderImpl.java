/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.util;

import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlgraphics.image.codec.util.ImageEncodeParam;
import org.apache.xmlgraphics.image.codec.util.ImageEncoder;
import org.apache.xmlgraphics.image.codec.util.SingleTileRenderedImage;

public abstract class ImageEncoderImpl
implements ImageEncoder {
    protected OutputStream output;
    protected ImageEncodeParam param;

    public ImageEncoderImpl(OutputStream output, ImageEncodeParam param) {
        this.output = output;
        this.param = param;
    }

    @Override
    public ImageEncodeParam getParam() {
        return this.param;
    }

    @Override
    public void setParam(ImageEncodeParam param) {
        this.param = param;
    }

    @Override
    public OutputStream getOutputStream() {
        return this.output;
    }

    @Override
    public void encode(Raster ras, ColorModel cm) throws IOException {
        SingleTileRenderedImage im = new SingleTileRenderedImage(ras, cm);
        this.encode(im);
    }

    @Override
    public abstract void encode(RenderedImage var1) throws IOException;
}

