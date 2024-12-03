/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.tidy;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import org.w3c.tidy.Configuration;
import org.w3c.tidy.Out;

public class OutJavaImpl
implements Out {
    private Writer writer;
    private char[] newline;

    protected OutJavaImpl(Configuration configuration, String encoding, OutputStream out) throws UnsupportedEncodingException {
        this.writer = new OutputStreamWriter(out, encoding);
        this.newline = configuration.newline;
    }

    protected OutJavaImpl(Configuration configuration, Writer out) {
        this.writer = out;
        this.newline = configuration.newline;
    }

    public void outc(int c) {
        try {
            this.writer.write(c);
        }
        catch (IOException e) {
            System.err.println("OutJavaImpl.outc: " + e.getMessage());
        }
    }

    public void outc(byte c) {
        try {
            this.writer.write(c);
        }
        catch (IOException e) {
            System.err.println("OutJavaImpl.outc: " + e.getMessage());
        }
    }

    public void newline() {
        try {
            this.writer.write(this.newline);
        }
        catch (IOException e) {
            System.err.println("OutJavaImpl.newline: " + e.getMessage());
        }
    }

    public void flush() {
        try {
            this.writer.flush();
        }
        catch (IOException e) {
            System.err.println("OutJavaImpl.flush: " + e.getMessage());
        }
    }
}

