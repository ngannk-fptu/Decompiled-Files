/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.WriteListener
 */
package org.apache.catalina.ssi;

import java.io.ByteArrayOutputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;

public class ByteArrayServletOutputStream
extends ServletOutputStream {
    protected final ByteArrayOutputStream buf = new ByteArrayOutputStream();

    public byte[] toByteArray() {
        return this.buf.toByteArray();
    }

    public void write(int b) {
        this.buf.write(b);
    }

    public boolean isReady() {
        return false;
    }

    public void setWriteListener(WriteListener listener) {
    }
}

