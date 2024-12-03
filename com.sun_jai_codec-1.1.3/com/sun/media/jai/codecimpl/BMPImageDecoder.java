/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codecimpl.BMPImage;
import com.sun.media.jai.codecimpl.CodecUtils;
import com.sun.media.jai.codecimpl.JaiI18N;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

public class BMPImageDecoder
extends ImageDecoderImpl {
    public BMPImageDecoder(InputStream input, ImageDecodeParam param) {
        super(input, param);
    }

    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(JaiI18N.getString("BMPImageDecoder8"));
        }
        try {
            return new BMPImage(this.input);
        }
        catch (Exception e) {
            throw CodecUtils.toIOException(e);
        }
    }
}

