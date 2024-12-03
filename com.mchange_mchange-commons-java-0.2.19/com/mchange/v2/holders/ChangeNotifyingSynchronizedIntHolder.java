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

public final class ChangeNotifyingSynchronizedIntHolder
implements ThreadSafeIntHolder,
Serializable {
    transient int value;
    transient boolean notify_all;
    static final long serialVersionUID = 1L;
    private static final short VERSION = 1;

    public ChangeNotifyingSynchronizedIntHolder(int n, boolean bl) {
        this.value = n;
        this.notify_all = bl;
    }

    public ChangeNotifyingSynchronizedIntHolder() {
        this(0, true);
    }

    @Override
    public synchronized int getValue() {
        return this.value;
    }

    @Override
    public synchronized void setValue(int n) {
        if (n != this.value) {
            this.value = n;
            this.doNotify();
        }
    }

    public synchronized void increment() {
        ++this.value;
        this.doNotify();
    }

    public synchronized void decrement() {
        --this.value;
        this.doNotify();
    }

    private void doNotify() {
        if (this.notify_all) {
            this.notifyAll();
        } else {
            this.notify();
        }
    }

    private void writeObject(ObjectOutputStream objectOutputStream) throws IOException {
        objectOutputStream.writeShort(1);
        objectOutputStream.writeInt(this.value);
        objectOutputStream.writeBoolean(this.notify_all);
    }

    private void readObject(ObjectInputStream objectInputStream) throws IOException {
        short s = objectInputStream.readShort();
        switch (s) {
            case 1: {
                this.value = objectInputStream.readInt();
                this.notify_all = objectInputStream.readBoolean();
                break;
            }
            default: {
                throw new UnsupportedVersionException(this, s);
            }
        }
    }
}

