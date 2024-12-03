/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codec;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.SeekableStream;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.io.IOException;

public interface ImageDecoder {
    public ImageDecodeParam getParam();

    public void setParam(ImageDecodeParam var1);

    public SeekableStream getInputStream();

    public int getNumPages() throws IOException;

    public Raster decodeAsRaster() throws IOException;

    public Raster decodeAsRaster(int var1) throws IOException;

    public RenderedImage decodeAsRenderedImage() throws IOException;

    public RenderedImage decodeAsRenderedImage(int var1) throws IOException;
}

