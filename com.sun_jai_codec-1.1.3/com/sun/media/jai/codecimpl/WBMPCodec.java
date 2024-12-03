/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ForwardSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.WBMPImageDecoder;
import com.sun.media.jai.codecimpl.WBMPImageEncoder;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class WBMPCodec
extends ImageCodec {
    static /* synthetic */ Class class$java$lang$Object;

    public String getFormatName() {
        return "wbmp";
    }

    public Class getEncodeParamClass() {
        return class$java$lang$Object == null ? (class$java$lang$Object = WBMPCodec.class$("java.lang.Object")) : class$java$lang$Object;
    }

    public Class getDecodeParamClass() {
        return class$java$lang$Object == null ? (class$java$lang$Object = WBMPCodec.class$("java.lang.Object")) : class$java$lang$Object;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();
        int dataType = sampleModel.getTransferType();
        return dataType != 4 && dataType != 5 && sampleModel.getNumBands() == 1 && sampleModel.getSampleSize(0) == 1;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        return new WBMPImageEncoder(dst, null);
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        if (!(src instanceof BufferedInputStream)) {
            src = new BufferedInputStream(src);
        }
        return new WBMPImageDecoder(new ForwardSeekableStream(src), null);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new WBMPImageDecoder(src, null);
    }

    public int getNumHeaderBytes() {
        return 3;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == 0 && header[1] == 0 && ((header[2] & 0x8F) != 0 || (header[2] & 0x7F) != 0);
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

