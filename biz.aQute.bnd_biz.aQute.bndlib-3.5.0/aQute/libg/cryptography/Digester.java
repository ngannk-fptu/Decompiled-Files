/*
 * Decompiled with CFR 0.152.
 */
package aQute.libg.cryptography;

import aQute.lib.io.IO;
import aQute.libg.cryptography.Digest;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;

public abstract class Digester<T extends Digest>
extends OutputStream {
    protected MessageDigest md;
    OutputStream[] out;

    public Digester(MessageDigest instance, OutputStream ... out) {
        this.md = instance;
        this.out = out;
    }

    @Override
    public void write(byte[] buffer, int offset, int length) throws IOException {
        this.md.update(buffer, offset, length);
        for (OutputStream o : this.out) {
            o.write(buffer, offset, length);
        }
    }

    @Override
    public void write(int b) throws IOException {
        this.md.update((byte)b);
        for (OutputStream o : this.out) {
            o.write(b);
        }
    }

    public MessageDigest getMessageDigest() throws Exception {
        return this.md;
    }

    public T from(InputStream in) throws Exception {
        IO.copy(in, (OutputStream)this);
        return this.digest();
    }

    public void setOutputs(OutputStream ... out) {
        this.out = out;
    }

    public abstract T digest() throws Exception;

    public abstract T digest(byte[] var1) throws Exception;

    public abstract String getAlgorithm();

    public T from(File f) throws Exception {
        IO.copy(f, (OutputStream)this);
        return this.digest();
    }

    public T from(byte[] f) throws Exception {
        IO.copy(f, (OutputStream)this);
        return this.digest();
    }
}

