/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import java.awt.image.BandedSampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import javax.media.jai.ComponentSampleModelJAI;
import javax.media.jai.RasterFactory;

public class SampleModelProxy
implements Serializable {
    private static final int TYPE_BANDED = 1;
    private static final int TYPE_PIXEL_INTERLEAVED = 2;
    private static final int TYPE_SINGLE_PIXEL_PACKED = 3;
    private static final int TYPE_MULTI_PIXEL_PACKED = 4;
    private static final int TYPE_COMPONENT_JAI = 5;
    private static final int TYPE_COMPONENT = 6;
    private transient SampleModel sampleModel;

    public SampleModelProxy(SampleModel source) {
        this.sampleModel = source;
    }

    public SampleModel getSampleModel() {
        return this.sampleModel;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        if (this.sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel sm = (ComponentSampleModel)this.sampleModel;
            int sampleModelType = 6;
            int transferType = sm.getTransferType();
            if (this.sampleModel instanceof PixelInterleavedSampleModel) {
                sampleModelType = 2;
            } else if (this.sampleModel instanceof BandedSampleModel) {
                sampleModelType = 1;
            } else if (this.sampleModel instanceof ComponentSampleModelJAI || transferType == 4 || transferType == 5) {
                sampleModelType = 5;
            }
            out.writeInt(sampleModelType);
            out.writeInt(transferType);
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            if (sampleModelType != 1) {
                out.writeInt(sm.getPixelStride());
            }
            out.writeInt(sm.getScanlineStride());
            if (sampleModelType != 2) {
                out.writeObject(sm.getBankIndices());
            }
            out.writeObject(sm.getBandOffsets());
        } else if (this.sampleModel instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel)this.sampleModel;
            out.writeInt(3);
            out.writeInt(sm.getTransferType());
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            out.writeInt(sm.getScanlineStride());
            out.writeObject(sm.getBitMasks());
        } else if (this.sampleModel instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)this.sampleModel;
            out.writeInt(4);
            out.writeInt(sm.getTransferType());
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            out.writeInt(sm.getPixelBitStride());
            out.writeInt(sm.getScanlineStride());
            out.writeInt(sm.getDataBitOffset());
        } else {
            throw new RuntimeException(JaiI18N.getString("SampleModelProxy0"));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        int sampleModelType = in.readInt();
        switch (sampleModelType) {
            case 2: {
                this.sampleModel = RasterFactory.createPixelInterleavedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject());
                break;
            }
            case 1: {
                this.sampleModel = RasterFactory.createBandedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject(), (int[])in.readObject());
                break;
            }
            case 5: {
                this.sampleModel = new ComponentSampleModelJAI(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject(), (int[])in.readObject());
                break;
            }
            case 6: {
                this.sampleModel = new ComponentSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject(), (int[])in.readObject());
                break;
            }
            case 3: {
                this.sampleModel = new SinglePixelPackedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject());
                break;
            }
            case 4: {
                this.sampleModel = new MultiPixelPackedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("SampleModelProxy0"));
            }
        }
    }
}

