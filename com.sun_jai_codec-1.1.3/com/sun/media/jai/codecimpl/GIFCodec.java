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
import com.sun.media.jai.codecimpl.GIFImageDecoder;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class GIFCodec
extends ImageCodec {
    static /* synthetic */ Class class$java$lang$Object;

    public String getFormatName() {
        return "gif";
    }

    public Class getEncodeParamClass() {
        return class$java$lang$Object == null ? (class$java$lang$Object = GIFCodec.class$("java.lang.Object")) : class$java$lang$Object;
    }

    public Class getDecodeParamClass() {
        return class$java$lang$Object == null ? (class$java$lang$Object = GIFCodec.class$("java.lang.Object")) : class$java$lang$Object;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        return false;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        return null;
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        return new GIFImageDecoder(src, param);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        return new GIFImageDecoder(new FileInputStream(src), null);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new GIFImageDecoder(src, param);
    }

    public int getNumHeaderBytes() {
        return 4;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == 71 && header[1] == 73 && header[2] == 70 && header[3] == 56;
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

