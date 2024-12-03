/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.holders;

import com.mchange.v2.holders.ThreadSafeFloatHolder;
import com.mchange.v2.ser.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class VolatileFloatHolder
implements ThreadSafeFloatHolder,
Serializable {
    volatile transient float value;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    @Override
    public float getValue() {
        return this.value;
    }

    @Override
    public void setValue(float f) {
        this.value = f;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeFloat(this.value);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.value = objectInputStream.readFloat();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }
}

