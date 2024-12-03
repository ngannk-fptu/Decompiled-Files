/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class ByteMessage
implements Externalizable {
    private byte[] message;

    public ByteMessage() {
    }

    public ByteMessage(byte[] data) {
        this.message = data;
    }

    public byte[] getMessage() {
        return this.message;
    }

    public void setMessage(byte[] message) {
        this.message = message;
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException {
        int length = in.readInt();
        this.message = new byte[length];
        in.readFully(this.message);
    }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(this.message != null ? this.message.length : 0);
        if (this.message != null) {
            out.write(this.message, 0, this.message.length);
        }
    }
}

