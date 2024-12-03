/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.JPEGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.JPEGImageDecoder;
import com.sun.media.jai.codecimpl.JPEGImageEncoder;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class JPEGCodec
extends ImageCodec {
    static /* synthetic */ Class class$com$sun$media$jai$codec$JPEGEncodeParam;
    static /* synthetic */ Class class$com$sun$media$jai$codec$JPEGDecodeParam;

    public String getFormatName() {
        return "jpeg";
    }

    public Class getEncodeParamClass() {
        return class$com$sun$media$jai$codec$JPEGEncodeParam == null ? (class$com$sun$media$jai$codec$JPEGEncodeParam = JPEGCodec.class$("com.sun.media.jai.codec.JPEGEncodeParam")) : class$com$sun$media$jai$codec$JPEGEncodeParam;
    }

    public Class getDecodeParamClass() {
        return class$com$sun$media$jai$codec$JPEGDecodeParam == null ? (class$com$sun$media$jai$codec$JPEGDecodeParam = JPEGCodec.class$("com.sun.media.jai.codec.JPEGDecodeParam")) : class$com$sun$media$jai$codec$JPEGDecodeParam;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        return true;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        JPEGEncodeParam p = null;
        if (param != null) {
            p = (JPEGEncodeParam)param;
        }
        return new JPEGImageEncoder(dst, p);
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        return new JPEGImageDecoder(src, param);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        return new JPEGImageDecoder(new FileInputStream(src), param);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new JPEGImageDecoder((InputStream)src, param);
    }

    public int getNumHeaderBytes() {
        return 3;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == -1 && header[1] == -40 && header[2] == -1;
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

