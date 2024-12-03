/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream
 */
package org.apache.poi.openxml4j.opc.internal;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.commons.io.output.UnsynchronizedByteArrayOutputStream;
import org.apache.poi.openxml4j.opc.internal.MemoryPackagePart;

public final class MemoryPackagePartOutputStream
extends OutputStream {
    private final MemoryPackagePart _part;
    private final UnsynchronizedByteArrayOutputStream _buff;

    public MemoryPackagePartOutputStream(MemoryPackagePart part) {
        this._part = part;
        this._buff = new UnsynchronizedByteArrayOutputStream();
    }

    @Override
    public void write(int b) {
        this._buff.write(b);
    }

    @Override
    public void close() throws IOException {
        this.flush();
    }

    @Override
    public void flush() throws IOException {
        this._buff.flush();
        if (this._part.data != null) {
            byte[] newArray = new byte[this._part.data.length + this._buff.size()];
            System.arraycopy(this._part.data, 0, newArray, 0, this._part.data.length);
            byte[] buffArr = this._buff.toByteArray();
            System.arraycopy(buffArr, 0, newArray, this._part.data.length, buffArr.length);
            this._part.data = newArray;
        } else {
            this._part.data = this._buff.toByteArray();
        }
        this._buff.reset();
    }

    @Override
    public void write(byte[] b, int off, int len) {
        this._buff.write(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        this._buff.write(b);
    }
}

