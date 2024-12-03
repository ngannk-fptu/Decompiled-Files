/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.holders;

import com.mchange.v2.holders.ThreadSafeDoubleHolder;
import com.mchange.v2.ser.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SynchronizedDoubleHolder
implements ThreadSafeDoubleHolder,
Serializable {
    transient double value;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    @Override
    public synchronized double getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(double d) {
        this.value = d;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeDouble(this.value);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.value = objectInputStream.readDouble();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }
}

