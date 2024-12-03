/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codecimpl.CodecUtils;
import com.sun.media.jai.codecimpl.JPEGImage;
import com.sun.media.jai.codecimpl.JaiI18N;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

public class JPEGImageDecoder
extends ImageDecoderImpl {
    public JPEGImageDecoder(InputStream input, ImageDecodeParam param) {
        super(input, param);
    }

    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(JaiI18N.getString("JPEGImageDecoder0"));
        }
        try {
            return new JPEGImage(this.input, this.param);
        }
        catch (Exception e) {
            throw CodecUtils.toIOException(e);
        }
    }
}

