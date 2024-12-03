/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.runtime.output;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import org.apache.xalan.xsltc.runtime.output.OutputBuffer;

class WriterOutputBuffer
implements OutputBuffer {
    private static final int KB = 1024;
    private static int BUFFER_SIZE = 4096;
    private Writer _writer;

    public WriterOutputBuffer(Writer writer) {
        this._writer = new BufferedWriter(writer, BUFFER_SIZE);
    }

    @Override
    public String close() {
        try {
            this._writer.flush();
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return "";
    }

    @Override
    public OutputBuffer append(String s) {
        try {
            this._writer.write(s);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }

    @Override
    public OutputBuffer append(char[] s, int from, int to) {
        try {
            this._writer.write(s, from, to);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }

    @Override
    public OutputBuffer append(char ch) {
        try {
            this._writer.write(ch);
        }
        catch (IOException e) {
            throw new RuntimeException(e.toString());
        }
        return this;
    }

    static {
        String osName = System.getProperty("os.name");
        if (osName.equalsIgnoreCase("solaris")) {
            BUFFER_SIZE = 32768;
        }
    }
}

