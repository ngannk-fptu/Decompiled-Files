/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.BMPEncodeParam;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.BMPImageDecoder;
import com.sun.media.jai.codecimpl.BMPImageEncoder;
import com.sun.media.jai.codecimpl.CodecUtils;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class BMPCodec
extends ImageCodec {
    static /* synthetic */ Class class$com$sun$media$jai$codec$BMPEncodeParam;
    static /* synthetic */ Class class$java$lang$Object;

    public String getFormatName() {
        return "bmp";
    }

    public Class getEncodeParamClass() {
        return class$com$sun$media$jai$codec$BMPEncodeParam == null ? (class$com$sun$media$jai$codec$BMPEncodeParam = BMPCodec.class$("com.sun.media.jai.codec.BMPEncodeParam")) : class$com$sun$media$jai$codec$BMPEncodeParam;
    }

    public Class getDecodeParamClass() {
        return class$java$lang$Object == null ? (class$java$lang$Object = BMPCodec.class$("java.lang.Object")) : class$java$lang$Object;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();
        int dataType = sampleModel.getTransferType();
        if (dataType != 0 && !CodecUtils.isPackedByteImage(im)) {
            return false;
        }
        if (param != null) {
            if (!(param instanceof BMPEncodeParam)) {
                return false;
            }
            BMPEncodeParam BMPParam = (BMPEncodeParam)param;
            int version = BMPParam.getVersion();
            if (version == 0 || version == 2) {
                return false;
            }
        }
        return true;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        BMPEncodeParam p = null;
        if (param != null) {
            p = (BMPEncodeParam)param;
        }
        return new BMPImageEncoder(dst, p);
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        return new BMPImageDecoder(src, null);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        return new BMPImageDecoder(new FileInputStream(src), null);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new BMPImageDecoder((InputStream)src, null);
    }

    public int getNumHeaderBytes() {
        return 2;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == 66 && header[1] == 77;
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

