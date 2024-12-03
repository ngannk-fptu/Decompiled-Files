/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.codec.png;

import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;
import org.apache.batik.ext.awt.image.codec.png.PNGDecodeParam;
import org.apache.batik.ext.awt.image.codec.png.PNGImage;
import org.apache.batik.ext.awt.image.codec.util.ImageDecodeParam;
import org.apache.batik.ext.awt.image.codec.util.ImageDecoderImpl;
import org.apache.batik.ext.awt.image.codec.util.PropertyUtil;

public class PNGImageDecoder
extends ImageDecoderImpl {
    public PNGImageDecoder(InputStream input, PNGDecodeParam param) {
        super(input, (ImageDecodeParam)param);
    }

    @Override
    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(PropertyUtil.getString("PNGImageDecoder19"));
        }
        return new PNGImage(this.input, (PNGDecodeParam)this.param);
    }
}

