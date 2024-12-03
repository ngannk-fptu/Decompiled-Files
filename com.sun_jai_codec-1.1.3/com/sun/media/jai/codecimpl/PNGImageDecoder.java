/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoderImpl;
import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codecimpl.CodecUtils;
import com.sun.media.jai.codecimpl.JaiI18N;
import com.sun.media.jai.codecimpl.PNGImage;
import java.awt.image.RenderedImage;
import java.io.IOException;
import java.io.InputStream;

public class PNGImageDecoder
extends ImageDecoderImpl {
    public PNGImageDecoder(InputStream input, PNGDecodeParam param) {
        super(input, (ImageDecodeParam)param);
    }

    public RenderedImage decodeAsRenderedImage(int page) throws IOException {
        if (page != 0) {
            throw new IOException(JaiI18N.getString("PNGImageDecoder19"));
        }
        try {
            return new PNGImage(this.input, (PNGDecodeParam)this.param);
        }
        catch (Exception e) {
            throw CodecUtils.toIOException(e);
        }
    }
}

