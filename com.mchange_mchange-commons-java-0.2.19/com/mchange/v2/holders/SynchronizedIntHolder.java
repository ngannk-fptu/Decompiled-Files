/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.holders;

import com.mchange.v2.holders.ThreadSafeIntHolder;
import com.mchange.v2.ser.UnsupportedVersionException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class SynchronizedIntHolder
implements ThreadSafeIntHolder,
Serializable {
    transient int value;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    public SynchronizedIntHolder(int n) {
        this.value = n;
    }

    public SynchronizedIntHolder() {
        this(0);
    }

    @Override
    public synchronized int getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(int n) {
        this.value = n;
    }

    public synchronized void increment() {
        ++this.value;
    }

    public synchronized void decrement() {
        --this.value;
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeInt(this.value);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.value = objectInputStream.readInt();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }
}

