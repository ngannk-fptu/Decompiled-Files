/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.holders;

import com.mchange.v2.holders.ThreadSafeLongHolder;
import com.mchange.v2.ser.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SynchronizedLongHolder
implements ThreadSafeLongHolder,
Serializable {
    transient long value;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    @Override
    public synchronized long getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(long l) {
        this.value = l;
    }

    public SynchronizedLongHolder(long l) {
        this.value = l;
    }

    public SynchronizedLongHolder() {
        this(0L);
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeLong(this.value);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.value = objectInputStream.readLong();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }
}

