/*
 * Decompiled with CFR 0.152.
 */
package groovy.io;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;

public class PlatformLineWriter
extends Writer {
    private BufferedWriter writer;

    public PlatformLineWriter(Writer out) {
        this.writer = new BufferedWriter(out);
    }

    public PlatformLineWriter(Writer out, int sz) {
        this.writer = new BufferedWriter(out, sz);
    }

    @Override
    public void write(char[] cbuf, int off, int len) throws IOException {
        while (len > 0) {
            char c;
            if ((c = cbuf[off++]) == '\n') {
                this.writer.newLine();
            } else if (c != '\r') {
                this.writer.write(c);
            }
            --len;
        }
    }

    @Override
    public void flush() throws IOException {
        this.writer.flush();
    }

    @Override
    public void close() throws IOException {
        this.writer.close();
    }
}

