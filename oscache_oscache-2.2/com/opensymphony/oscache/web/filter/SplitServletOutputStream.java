/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 */
package com.opensymphony.oscache.web.filter;

import java.io.IOException;
import java.io.OutputStream;
import javax.servlet.ServletOutputStream;

public class SplitServletOutputStream
extends ServletOutputStream {
    OutputStream captureStream = null;
    OutputStream passThroughStream = null;

    public SplitServletOutputStream(OutputStream captureStream, OutputStream passThroughStream) {
        this.captureStream = captureStream;
        this.passThroughStream = passThroughStream;
    }

    public void write(int value) throws IOException {
        this.captureStream.write(value);
        this.passThroughStream.write(value);
    }

    public void write(byte[] value) throws IOException {
        this.captureStream.write(value);
        this.passThroughStream.write(value);
    }

    public void write(byte[] b, int off, int len) throws IOException {
        this.captureStream.write(b, off, len);
        this.passThroughStream.write(b, off, len);
    }
}

