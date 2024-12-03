/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecodeParam;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncodeParam;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNGDecodeParam;
import com.sun.media.jai.codec.PNGEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.PNGImageDecoder;
import com.sun.media.jai.codecimpl.PNGImageEncoder;
import java.awt.image.ColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public final class PNGCodec
extends ImageCodec {
    static /* synthetic */ Class class$com$sun$media$jai$codec$PNGEncodeParam;
    static /* synthetic */ Class class$com$sun$media$jai$codec$PNGDecodeParam;

    public String getFormatName() {
        return "png";
    }

    public Class getEncodeParamClass() {
        return class$com$sun$media$jai$codec$PNGEncodeParam == null ? (class$com$sun$media$jai$codec$PNGEncodeParam = PNGCodec.class$("com.sun.media.jai.codec.PNGEncodeParam")) : class$com$sun$media$jai$codec$PNGEncodeParam;
    }

    public Class getDecodeParamClass() {
        return class$com$sun$media$jai$codec$PNGDecodeParam == null ? (class$com$sun$media$jai$codec$PNGDecodeParam = PNGCodec.class$("com.sun.media.jai.codec.PNGDecodeParam")) : class$com$sun$media$jai$codec$PNGDecodeParam;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();
        int dataType = sampleModel.getTransferType();
        if (dataType == 4 || dataType == 5) {
            return false;
        }
        int[] sampleSize = sampleModel.getSampleSize();
        int bitDepth = sampleSize[0];
        for (int i = 1; i < sampleSize.length; ++i) {
            if (sampleSize[i] == bitDepth) continue;
            return false;
        }
        if (bitDepth < 1 || bitDepth > 16) {
            return false;
        }
        int numBands = sampleModel.getNumBands();
        if (numBands < 1 || numBands > 4) {
            return false;
        }
        ColorModel colorModel = im.getColorModel();
        if (colorModel instanceof IndexColorModel && (numBands != 1 || bitDepth > 8)) {
            return false;
        }
        if (param != null) {
            if (param instanceof PNGEncodeParam) {
                if (colorModel instanceof IndexColorModel ? !(param instanceof PNGEncodeParam.Palette) : (numBands < 3 ? !(param instanceof PNGEncodeParam.Gray) : !(param instanceof PNGEncodeParam.RGB))) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return true;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        PNGEncodeParam p = null;
        if (param != null) {
            p = (PNGEncodeParam)param;
        }
        return new PNGImageEncoder(dst, p);
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        PNGDecodeParam p = null;
        if (param != null) {
            p = (PNGDecodeParam)param;
        }
        return new PNGImageDecoder(src, p);
    }

    protected ImageDecoder createImageDecoder(File src, ImageDecodeParam param) throws IOException {
        PNGDecodeParam p = null;
        if (param != null) {
            p = (PNGDecodeParam)param;
        }
        return new PNGImageDecoder((InputStream)new FileInputStream(src), p);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        PNGDecodeParam p = null;
        if (param != null) {
            p = (PNGDecodeParam)param;
        }
        return new PNGImageDecoder((InputStream)src, p);
    }

    public int getNumHeaderBytes() {
        return 8;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == -119 && header[1] == 80 && header[2] == 78 && header[3] == 71 && header[4] == 13 && header[5] == 10 && header[6] == 26 && header[7] == 10;
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

