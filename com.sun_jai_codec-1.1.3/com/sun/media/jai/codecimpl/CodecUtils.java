/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.codecimpl;

import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.lang.reflect.Method;

class CodecUtils {
    static Method ioExceptionInitCause;
    static /* synthetic */ Class class$java$lang$Throwable;

    CodecUtils() {
    }

    static final boolean isPackedByteImage(RenderedImage im) {
        SampleModel imageSampleModel = im.getSampleModel();
        if (imageSampleModel instanceof SinglePixelPackedSampleModel) {
            for (int i = 0; i < imageSampleModel.getNumBands(); ++i) {
                if (imageSampleModel.getSampleSize(i) <= 8) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    static final IOException toIOException(Exception cause) {
        IOException ioe;
        if (cause != null) {
            if (cause instanceof IOException) {
                ioe = (IOException)cause;
            } else if (ioExceptionInitCause != null) {
                ioe = new IOException(cause.getMessage());
                try {
                    ioExceptionInitCause.invoke((Object)ioe, cause);
                }
                catch (Exception exception) {}
            } else {
                ioe = new IOException(cause.getClass().getName() + ": " + cause.getMessage());
            }
        } else {
            ioe = new IOException();
        }
        return ioe;
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }

    static {
        try {
            Class<?> c = Class.forName("java.io.IOException");
            ioExceptionInitCause = c.getMethod("initCause", class$java$lang$Throwable == null ? (class$java$lang$Throwable = CodecUtils.class$("java.lang.Throwable")) : class$java$lang$Throwable);
        }
        catch (Exception e) {
            ioExceptionInitCause = null;
        }
    }
}

