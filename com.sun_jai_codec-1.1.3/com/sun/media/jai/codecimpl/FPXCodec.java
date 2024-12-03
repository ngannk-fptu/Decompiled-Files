/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.FPXImageDecoder;
import com.sun.media.jai.codecimpl.JaiI18N;
import java.awt.image.RenderedImage;
import java.io.OutputStream;

public final class FPXCodec
extends ImageCodec {
    static /* synthetic */ Class class$com$sun$media$jai$codec$FPXDecodeParam;

    public String getFormatName() {
        return "fpx";
    }

    public Class getEncodeParamClass() {
        return null;
    }

    public Class getDecodeParamClass() {
        return class$com$sun$media$jai$codec$FPXDecodeParam == null ? (class$com$sun$media$jai$codec$FPXDecodeParam = FPXCodec.class$("com.sun.media.jai.codec.FPXDecodeParam")) : class$com$sun$media$jai$codec$FPXDecodeParam;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        return false;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        throw new RuntimeException(JaiI18N.getString("FPXCodec0"));
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new FPXImageDecoder(src, param);
    }

    public int getNumHeaderBytes() {
        return 8;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == -48 && header[1] == -49 && header[2] == 17 && header[3] == -32 && header[4] == -95 && header[5] == -79 && header[6] == 26 && header[7] == -31;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

