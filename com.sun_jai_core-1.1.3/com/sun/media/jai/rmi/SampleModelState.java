/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.rmi.SerializableStateImpl;
import java.awt.RenderingHints;
import java.awt.image.BandedSampleModel;
import java.awt.image.ComponentSampleModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.media.jai.ComponentSampleModelJAI;
import javax.media.jai.RasterFactory;

public class SampleModelState
extends SerializableStateImpl {
    private static final int TYPE_BANDED = 1;
    private static final int TYPE_PIXEL_INTERLEAVED = 2;
    private static final int TYPE_SINGLE_PIXEL_PACKED = 3;
    private static final int TYPE_MULTI_PIXEL_PACKED = 4;
    private static final int TYPE_COMPONENT_JAI = 5;
    private static final int TYPE_COMPONENT = 6;
    static /* synthetic */ Class class$java$awt$image$BandedSampleModel;
    static /* synthetic */ Class class$java$awt$image$PixelInterleavedSampleModel;
    static /* synthetic */ Class class$java$awt$image$ComponentSampleModel;
    static /* synthetic */ Class class$java$awt$image$MultiPixelPackedSampleModel;
    static /* synthetic */ Class class$java$awt$image$SinglePixelPackedSampleModel;
    static /* synthetic */ Class class$javax$media$jai$ComponentSampleModelJAI;
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$util$ComponentSampleModelJAI;

    public static Class[] getSupportedClasses() {
        return new Class[]{class$java$awt$image$BandedSampleModel == null ? (class$java$awt$image$BandedSampleModel = SampleModelState.class$("java.awt.image.BandedSampleModel")) : class$java$awt$image$BandedSampleModel, class$java$awt$image$PixelInterleavedSampleModel == null ? (class$java$awt$image$PixelInterleavedSampleModel = SampleModelState.class$("java.awt.image.PixelInterleavedSampleModel")) : class$java$awt$image$PixelInterleavedSampleModel, class$java$awt$image$ComponentSampleModel == null ? (class$java$awt$image$ComponentSampleModel = SampleModelState.class$("java.awt.image.ComponentSampleModel")) : class$java$awt$image$ComponentSampleModel, class$java$awt$image$MultiPixelPackedSampleModel == null ? (class$java$awt$image$MultiPixelPackedSampleModel = SampleModelState.class$("java.awt.image.MultiPixelPackedSampleModel")) : class$java$awt$image$MultiPixelPackedSampleModel, class$java$awt$image$SinglePixelPackedSampleModel == null ? (class$java$awt$image$SinglePixelPackedSampleModel = SampleModelState.class$("java.awt.image.SinglePixelPackedSampleModel")) : class$java$awt$image$SinglePixelPackedSampleModel, class$javax$media$jai$ComponentSampleModelJAI == null ? (class$javax$media$jai$ComponentSampleModelJAI = SampleModelState.class$("javax.media.jai.ComponentSampleModelJAI")) : class$javax$media$jai$ComponentSampleModelJAI, class$com$sun$media$jai$codecimpl$util$ComponentSampleModelJAI == null ? (class$com$sun$media$jai$codecimpl$util$ComponentSampleModelJAI = SampleModelState.class$("com.sun.media.jai.codecimpl.util.ComponentSampleModelJAI")) : class$com$sun$media$jai$codecimpl$util$ComponentSampleModelJAI};
    }

    public SampleModelState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        SampleModel sampleModel = (SampleModel)this.theObject;
        if (sampleModel instanceof ComponentSampleModel) {
            ComponentSampleModel sm = (ComponentSampleModel)sampleModel;
            int sampleModelType = 6;
            int transferType = sm.getTransferType();
            if (sampleModel instanceof PixelInterleavedSampleModel) {
                sampleModelType = 2;
            } else if (sampleModel instanceof BandedSampleModel) {
                sampleModelType = 1;
            } else if (sampleModel instanceof ComponentSampleModelJAI || transferType == 4 || transferType == 5) {
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
        } else if (sampleModel instanceof SinglePixelPackedSampleModel) {
            SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel)sampleModel;
            out.writeInt(3);
            out.writeInt(sm.getTransferType());
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            out.writeInt(sm.getScanlineStride());
            out.writeObject(sm.getBitMasks());
        } else if (sampleModel instanceof MultiPixelPackedSampleModel) {
            MultiPixelPackedSampleModel sm = (MultiPixelPackedSampleModel)sampleModel;
            out.writeInt(4);
            out.writeInt(sm.getTransferType());
            out.writeInt(sm.getWidth());
            out.writeInt(sm.getHeight());
            out.writeInt(sm.getPixelBitStride());
            out.writeInt(sm.getScanlineStride());
            out.writeInt(sm.getDataBitOffset());
        } else {
            throw new RuntimeException(JaiI18N.getString("SampleModelState0"));
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        SampleModel sampleModel = null;
        int sampleModelType = in.readInt();
        switch (sampleModelType) {
            case 2: {
                sampleModel = RasterFactory.createPixelInterleavedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject());
                break;
            }
            case 1: {
                sampleModel = RasterFactory.createBandedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject(), (int[])in.readObject());
                break;
            }
            case 5: {
                sampleModel = new ComponentSampleModelJAI(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject(), (int[])in.readObject());
                break;
            }
            case 6: {
                sampleModel = new ComponentSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject(), (int[])in.readObject());
                break;
            }
            case 3: {
                sampleModel = new SinglePixelPackedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), (int[])in.readObject());
                break;
            }
            case 4: {
                sampleModel = new MultiPixelPackedSampleModel(in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt(), in.readInt());
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("SampleModelState0"));
            }
        }
        this.theObject = sampleModel;
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

