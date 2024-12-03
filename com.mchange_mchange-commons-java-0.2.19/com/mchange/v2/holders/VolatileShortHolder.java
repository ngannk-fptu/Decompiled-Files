/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.holders;

import com.mchange.v2.holders.ThreadSafeShortHolder;
import com.mchange.v2.ser.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class VolatileShortHolder
implements ThreadSafeShortHolder,
Serializable {
    volatile transient short value;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    @Override
    public short getValue() {
        return this.value;
    }

    @Override
    public void setValue(short s) {
        this.value = s;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeShort(this.value);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.value = objectInputStream.readShort();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }
}

