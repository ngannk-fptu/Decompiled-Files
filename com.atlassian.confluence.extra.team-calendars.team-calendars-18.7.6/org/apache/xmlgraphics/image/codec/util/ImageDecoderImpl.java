/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.image.codec.util;

import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.xmlgraphics.image.codec.util.ForwardSeekableStream;
import org.apache.xmlgraphics.image.codec.util.ImageDecodeParam;
import org.apache.xmlgraphics.image.codec.util.ImageDecoder;
import org.apache.xmlgraphics.image.codec.util.SeekableStream;

public abstract class ImageDecoderImpl
implements ImageDecoder {
    protected SeekableStream input;
    protected ImageDecodeParam param;

    public ImageDecoderImpl(SeekableStream input, ImageDecodeParam param) {
        this.input = input;
        this.param = param;
    }

    public ImageDecoderImpl(InputStream input, ImageDecodeParam param) {
        this.input = new ForwardSeekableStream(input);
        this.param = param;
    }

    @Override
    public ImageDecodeParam getParam() {
        return this.param;
    }

    @Override
    public void setParam(ImageDecodeParam param) {
        this.param = param;
    }

    @Override
    public SeekableStream getInputStream() {
        return this.input;
    }

    @Override
    public int getNumPages() throws IOException {
        return 1;
    }

    @Override
    public Raster decodeAsRaster() throws IOException {
        return this.decodeAsRaster(0);
    }

    @Override
    public Raster decodeAsRaster(int page) throws IOException {
        RenderedImage im = this.decodeAsRenderedImage(page);
        return im.getData();
    }

    @Override
    public RenderedImage decodeAsRenderedImage() throws IOException {
        return this.decodeAsRenderedImage(0);
    }

    @Override
    public abstract RenderedImage decodeAsRenderedImage(int var1) throws IOException;
}

