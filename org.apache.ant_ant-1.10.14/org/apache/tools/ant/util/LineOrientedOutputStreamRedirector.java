/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.io.IOException;
import java.io.OutputStream;
import org.apache.tools.ant.util.LineOrientedOutputStream;

public class LineOrientedOutputStreamRedirector
extends LineOrientedOutputStream {
    private OutputStream stream;

    public LineOrientedOutputStreamRedirector(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    protected void processLine(byte[] b) throws IOException {
        this.stream.write(b);
        this.stream.write(System.lineSeparator().getBytes());
    }

    @Override
    protected void processLine(String line) throws IOException {
        this.stream.write(String.format("%s%n", line).getBytes());
    }

    @Override
    public void close() throws IOException {
        super.close();
        this.stream.close();
    }

    @Override
    public void flush() throws IOException {
        super.flush();
        this.stream.flush();
    }
}

