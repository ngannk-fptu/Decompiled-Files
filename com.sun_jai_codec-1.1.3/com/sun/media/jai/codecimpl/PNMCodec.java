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
import com.sun.media.jai.codec.PNMEncodeParam;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codecimpl.PNMImageDecoder;
import com.sun.media.jai.codecimpl.PNMImageEncoder;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.BufferedInputStream;
import java.io.InputStream;
import java.io.OutputStream;

public final class PNMCodec
extends ImageCodec {
    static /* synthetic */ Class class$com$sun$media$jai$codec$PNMEncodeParam;
    static /* synthetic */ Class class$java$lang$Object;

    public String getFormatName() {
        return "pnm";
    }

    public Class getEncodeParamClass() {
        return class$com$sun$media$jai$codec$PNMEncodeParam == null ? (class$com$sun$media$jai$codec$PNMEncodeParam = PNMCodec.class$("com.sun.media.jai.codec.PNMEncodeParam")) : class$com$sun$media$jai$codec$PNMEncodeParam;
    }

    public Class getDecodeParamClass() {
        return class$java$lang$Object == null ? (class$java$lang$Object = PNMCodec.class$("java.lang.Object")) : class$java$lang$Object;
    }

    public boolean canEncodeImage(RenderedImage im, ImageEncodeParam param) {
        SampleModel sampleModel = im.getSampleModel();
        int dataType = sampleModel.getTransferType();
        if (dataType == 4 || dataType == 5) {
            return false;
        }
        int numBands = sampleModel.getNumBands();
        return numBands == 1 || numBands == 3;
    }

    protected ImageEncoder createImageEncoder(OutputStream dst, ImageEncodeParam param) {
        PNMEncodeParam p = null;
        if (param != null) {
            p = (PNMEncodeParam)param;
        }
        return new PNMImageEncoder(dst, p);
    }

    protected ImageDecoder createImageDecoder(InputStream src, ImageDecodeParam param) {
        if (!(src instanceof BufferedInputStream)) {
            src = new BufferedInputStream(src);
        }
        return new PNMImageDecoder(new ForwardSeekableStream(src), null);
    }

    protected ImageDecoder createImageDecoder(SeekableStream src, ImageDecodeParam param) {
        return new PNMImageDecoder(src, null);
    }

    public int getNumHeaderBytes() {
        return 2;
    }

    public boolean isFormatRecognized(byte[] header) {
        return header[0] == 80 && header[1] >= 49 && header[1] <= 54;
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

