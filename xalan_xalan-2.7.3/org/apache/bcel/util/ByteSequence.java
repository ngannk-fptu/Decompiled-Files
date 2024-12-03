/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.util;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;

public final class ByteSequence
extends DataInputStream {
    private final ByteArrayStream byteStream;

    public ByteSequence(byte[] bytes) {
        super(new ByteArrayStream(bytes));
        this.byteStream = (ByteArrayStream)this.in;
    }

    public int getIndex() {
        return this.byteStream.getPosition();
    }

    void unreadByte() {
        this.byteStream.unreadByte();
    }

    private static final class ByteArrayStream
    extends ByteArrayInputStream {
        ByteArrayStream(byte[] bytes) {
            super(bytes);
        }

        int getPosition() {
            return this.pos;
        }

        void unreadByte() {
            if (this.pos > 0) {
                --this.pos;
            }
        }
    }
}

