/*
 * Decompiled with CFR 0.152.
 */
package com.sun.media.jai.rmi;

import com.sun.media.jai.rmi.JaiI18N;
import com.sun.media.jai.util.DataBufferUtils;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class DataBufferProxy
implements Serializable {
    private transient DataBuffer dataBuffer;

    public DataBufferProxy(DataBuffer source) {
        this.dataBuffer = source;
    }

    public DataBuffer getDataBuffer() {
        return this.dataBuffer;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        int dataType = this.dataBuffer.getDataType();
        out.writeInt(dataType);
        out.writeObject(this.dataBuffer.getOffsets());
        out.writeInt(this.dataBuffer.getSize());
        Object dataArray = null;
        switch (dataType) {
            case 0: {
                dataArray = ((DataBufferByte)this.dataBuffer).getBankData();
                break;
            }
            case 2: {
                dataArray = ((DataBufferShort)this.dataBuffer).getBankData();
                break;
            }
            case 1: {
                dataArray = ((DataBufferUShort)this.dataBuffer).getBankData();
                break;
            }
            case 3: {
                dataArray = ((DataBufferInt)this.dataBuffer).getBankData();
                break;
            }
            case 4: {
                dataArray = DataBufferUtils.getBankDataFloat(this.dataBuffer);
                break;
            }
            case 5: {
                dataArray = DataBufferUtils.getBankDataDouble(this.dataBuffer);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("DataBufferProxy0"));
            }
        }
        out.writeObject(dataArray);
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
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
                this.dataBuffer = new DataBufferByte((byte[][])dataArray, size, offsets);
                break;
            }
            case 2: {
                this.dataBuffer = new DataBufferShort((short[][])dataArray, size, offsets);
                break;
            }
            case 1: {
                this.dataBuffer = new DataBufferUShort((short[][])dataArray, size, offsets);
                break;
            }
            case 3: {
                this.dataBuffer = new DataBufferInt((int[][])dataArray, size, offsets);
                break;
            }
            case 4: {
                this.dataBuffer = DataBufferUtils.createDataBufferFloat((float[][])dataArray, size, offsets);
                break;
            }
            case 5: {
                this.dataBuffer = DataBufferUtils.createDataBufferDouble((double[][])dataArray, size, offsets);
                break;
            }
            default: {
                throw new RuntimeException(JaiI18N.getString("DataBufferProxy0"));
            }
        }
    }
}

