/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tika.fork;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.tika.fork.ForkProxy;

class InputStreamProxy
extends InputStream
implements ForkProxy {
    private static final long serialVersionUID = 4350939227765568438L;
    private final int resource;
    private transient DataInputStream input;
    private transient DataOutputStream output;

    public InputStreamProxy(int resource) {
        this.resource = resource;
    }

    @Override
    public void init(DataInputStream input, DataOutputStream output) {
        this.input = input;
        this.output = output;
    }

    @Override
    public int read() throws IOException {
        this.output.writeByte(3);
        this.output.writeByte(this.resource);
        this.output.writeInt(1);
        this.output.flush();
        int n = this.input.readInt();
        if (n == 1) {
            return this.input.readUnsignedByte();
        }
        return n;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        this.output.writeByte(3);
        this.output.writeByte(this.resource);
        this.output.writeInt(len);
        this.output.flush();
        int n = this.input.readInt();
        if (n > 0) {
            this.input.readFully(b, off, n);
        }
        return n;
    }
}

