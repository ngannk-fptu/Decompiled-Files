/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.rmi.SerializableStateImpl;
import com.sun.media.jai.util.DataBufferUtils;
import java.awt.RenderingHints;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class DataBufferState
extends SerializableStateImpl {
    private static Class[] J2DDataBufferClasses = null;
    private transient DataBuffer dataBuffer;
    static /* synthetic */ Class class$java$awt$image$DataBufferByte;
    static /* synthetic */ Class class$java$awt$image$DataBufferShort;
    static /* synthetic */ Class class$java$awt$image$DataBufferUShort;
    static /* synthetic */ Class class$java$awt$image$DataBufferInt;
    static /* synthetic */ Class class$javax$media$jai$DataBufferFloat;
    static /* synthetic */ Class class$javax$media$jai$DataBufferDouble;
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$util$DataBufferFloat;
    static /* synthetic */ Class class$com$sun$media$jai$codecimpl$util$DataBufferDouble;

    public static Class[] getSupportedClasses() {
        Class[] supportedClasses = null;
        supportedClasses = J2DDataBufferClasses != null ? new Class[]{class$java$awt$image$DataBufferByte == null ? (class$java$awt$image$DataBufferByte = DataBufferState.class$("java.awt.image.DataBufferByte")) : class$java$awt$image$DataBufferByte, class$java$awt$image$DataBufferShort == null ? (class$java$awt$image$DataBufferShort = DataBufferState.class$("java.awt.image.DataBufferShort")) : class$java$awt$image$DataBufferShort, class$java$awt$image$DataBufferUShort == null ? (class$java$awt$image$DataBufferUShort = DataBufferState.class$("java.awt.image.DataBufferUShort")) : class$java$awt$image$DataBufferUShort, class$java$awt$image$DataBufferInt == null ? (class$java$awt$image$DataBufferInt = DataBufferState.class$("java.awt.image.DataBufferInt")) : class$java$awt$image$DataBufferInt, J2DDataBufferClasses[0], J2DDataBufferClasses[1], class$javax$media$jai$DataBufferFloat == null ? (class$javax$media$jai$DataBufferFloat = DataBufferState.class$("javax.media.jai.DataBufferFloat")) : class$javax$media$jai$DataBufferFloat, class$javax$media$jai$DataBufferDouble == null ? (class$javax$media$jai$DataBufferDouble = DataBufferState.class$("javax.media.jai.DataBufferDouble")) : class$javax$media$jai$DataBufferDouble, class$com$sun$media$jai$codecimpl$util$DataBufferFloat == null ? (class$com$sun$media$jai$codecimpl$util$DataBufferFloat = DataBufferState.class$("com.sun.media.jai.codecimpl.util.DataBufferFloat")) : class$com$sun$media$jai$codecimpl$util$DataBufferFloat, class$com$sun$media$jai$codecimpl$util$DataBufferDouble == null ? (class$com$sun$media$jai$codecimpl$util$DataBufferDouble = DataBufferState.class$("com.sun.media.jai.codecimpl.util.DataBufferDouble")) : class$com$sun$media$jai$codecimpl$util$DataBufferDouble} : new Class[]{class$java$awt$image$DataBufferByte == null ? (class$java$awt$image$DataBufferByte = DataBufferState.class$("java.awt.image.DataBufferByte")) : class$java$awt$image$DataBufferByte, class$java$awt$image$DataBufferShort == null ? (class$java$awt$image$DataBufferShort = DataBufferState.class$("java.awt.image.DataBufferShort")) : class$java$awt$image$DataBufferShort, class$java$awt$image$DataBufferUShort == null ? (class$java$awt$image$DataBufferUShort = DataBufferState.class$("java.awt.image.DataBufferUShort")) : class$java$awt$image$DataBufferUShort, class$java$awt$image$DataBufferInt == null ? (class$java$awt$image$DataBufferInt = DataBufferState.class$("java.awt.image.DataBufferInt")) : class$java$awt$image$DataBufferInt, class$javax$media$jai$DataBufferFloat == null ? (class$javax$media$jai$DataBufferFloat = DataBufferState.class$("javax.media.jai.DataBufferFloat")) : class$javax$media$jai$DataBufferFloat, class$javax$media$jai$DataBufferDouble == null ? (class$javax$media$jai$DataBufferDouble = DataBufferState.class$("javax.media.jai.DataBufferDouble")) : class$javax$media$jai$DataBufferDouble, class$com$sun$media$jai$codecimpl$util$DataBufferFloat == null ? (class$com$sun$media$jai$codecimpl$util$DataBufferFloat = DataBufferState.class$("com.sun.media.jai.codecimpl.util.DataBufferFloat")) : class$com$sun$media$jai$codecimpl$util$DataBufferFloat, class$com$sun$media$jai$codecimpl$util$DataBufferDouble == null ? (class$com$sun$media$jai$codecimpl$util$DataBufferDouble = DataBufferState.class$("com.sun.media.jai.codecimpl.util.DataBufferDouble")) : class$com$sun$media$jai$codecimpl$util$DataBufferDouble};
        return supportedClasses;
    }

    public DataBufferState(Class c, Object o, RenderingHints h) {
        super(c, o, h);
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        DataBuffer dataBuffer = (DataBuffer)this.theObject;
        int dataType = dataBuffer.getDataType();
        out.writeInt(dataType);
        out.writeObject(dataBuffer.getOffsets());
        out.writeInt(dataBuffer.getSize());
        Object dataArray = null;
        switch (dataType) {
            case 0: {
                dataArray = ((DataBufferByte)dataBuffer).getBankData();
                break;
            }
            case 2: {
                dataArray = ((DataBufferShort)dataBuffer).getBankData();
                break;
            }
            case 1: {
                dataArray = ((DataBufferUShort)dataBuffer).getBankData();
                break;
            }
            case 3: {
                dataArray = ((DataBufferInt)dataBuffer).getBankData();
                break;
            }
            case 4: {
                dataArray = DataBufferUtils.getBankDataFloat(dataBuffer);
                break;
            }
            case 5: {
                dataArray = DataBufferUtils.getBankDataDouble(dataBuffer);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("DataBufferState0"));
            }
        }
        out.writeObject(dataArray);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        DataBuffer dataBuffer = null;
        int dataType = -1;
        int[] offsets = null;
        int size = -1;
        Object dataArray = null;
        dataType = in.readInt();
        offsets = (int[])in.readObject();
        size = in.readInt();
        dataArray = in.readObject();
        switch (dataType) {
            case 0: {
                dataBuffer = new DataBufferByte((byte[][])dataArray, size, offsets);
                break;
            }
            case 2: {
                dataBuffer = new DataBufferShort((short[][])dataArray, size, offsets);
                break;
            }
            case 1: {
                dataBuffer = new DataBufferUShort((short[][])dataArray, size, offsets);
                break;
            }
            case 3: {
                dataBuffer = new DataBufferInt((int[][])dataArray, size, offsets);
                break;
            }
            case 4: {
                dataBuffer = DataBufferUtils.createDataBufferFloat((float[][])dataArray, size, offsets);
                break;
            }
            case 5: {
                dataBuffer = DataBufferUtils.createDataBufferDouble((double[][])dataArray, size, offsets);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("DataBufferState0"));
            }
        }
        this.theObject = dataBuffer;
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
            Class<?> dbfClass = Class.forName("java.awt.image.DataBufferFloat");
            Class<?> dbdClass = Class.forName("java.awt.image.DataBufferDouble");
            J2DDataBufferClasses = new Class[]{dbfClass, dbdClass};
        }
        catch (ClassNotFoundException classNotFoundException) {
            // empty catch block
        }
    }
}

