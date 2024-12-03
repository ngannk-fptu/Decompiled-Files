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

public interface ImageEncoder {
    public ImageEncodeParam getParam();

    public void setParam(ImageEncodeParam var1);

    public OutputStream getOutputStream();

    public void encode(Raster var1, ColorModel var2) throws IOException;

    public void encode(RenderedImage var1) throws IOException;
}

