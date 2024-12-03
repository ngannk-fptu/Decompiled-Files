/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.opimage;

import com.sun.media.jai.opimage.FFT;
import javax.media.jai.operator.DFTDescriptor;

public class FCT {
    protected boolean isForwardTransform;
    private FFT fft = null;

    public FCT() {
    }

    public FCT(boolean isForwardTransform, int length) {
        this.isForwardTransform = isForwardTransform;
        this.fft = new FFT(isForwardTransform, new Integer(DFTDescriptor.SCALING_NONE.getValue()), length);
    }

    public void setLength(int length) {
        this.fft.setLength(length);
    }

    public void setData(int dataType, Object data, int offset, int stride, int count) {
        if (this.isForwardTransform) {
            this.fft.setFCTData(dataType, data, offset, stride, count);
        } else {
            this.fft.setIFCTData(dataType, data, offset, stride, count);
        }
    }

    public void getData(int dataType, Object data, int offset, int stride) {
        if (this.isForwardTransform) {
            this.fft.getFCTData(dataType, data, offset, stride);
        } else {
            this.fft.getIFCTData(dataType, data, offset, stride);
        }
    }

    public void transform() {
        this.fft.transform();
    }
}

