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
import com.sun.media.jai.codecimpl.TIFFImageDecoder;
import com.sun.media.jai.codecimpl.TIFFImageEncoder;
import java.awt.image.RenderedImage;
import java.io.OutputStream;

public final class TIFFCodec
extends ImageCodec {
    static /* synthetic */ Class class$com$sun$media$jai$codec$TIFFEncodeParam;
    static /* synthetic */ Class class$com$sun$media$jai$codec$TIFFDecodeParam;

    public String getFormatName() {
        return "tiff";
    }

    public Class getEncodeParamClass() {
        return class$com$sun$media$jai$codec$TIFFEncodeParam == null ? (class$com$sun$media$jai$codec$TIFFEncodeParam = TIFFCodec.class$("com.sun.media.jai.codec.TIFFEncodeParam")) : class$com$sun$media$jai$codec$TIFFEncodeParam;
    }

    public Class getDecodeParamClass() {
        return class$com$sun$media$jai$codec$TIFFDecodeParam == null ? (class$com$sun$media$jai$codec$TIFFDecodeParam = TIFFCodec.class$("com.sun.media.jai.codec.TIFFDecodeParam")) : class$com$sun$media$jai$codec$TIFFDecodeParam;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        return true;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        return new TIFFImageEncoder(dst, param);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new TIFFImageDecoder(src, param);
    }

    public int getNumHeaderBytes() {
        return 4;
    }

    public boolean isFormatRecognized(byte[] header) {
        if (header[0] == 73 && header[1] == 73 && header[2] == 42 && header[3] == 0) {
            return true;
        }
        return header[0] == 77 && header[1] == 77 && header[2] == 0 && header[3] == 42;
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

