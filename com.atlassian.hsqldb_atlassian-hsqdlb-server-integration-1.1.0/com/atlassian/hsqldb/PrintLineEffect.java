/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Effect
 *  javax.annotation.concurrent.NotThreadSafe
 *  org.apache.commons.io.output.NullWriter
 */
package com.atlassian.hsqldb;

import com.atlassian.fugue.Effect;
import java.io.PrintWriter;
import java.io.Writer;
import javax.annotation.concurrent.NotThreadSafe;
import org.apache.commons.io.output.NullWriter;

@NotThreadSafe
public class PrintLineEffect
extends PrintWriter {
    private final StringBuffer buffer = new StringBuffer(96);
    private final Effect<String> lineSink;

    public PrintLineEffect(Effect<String> lineSink) {
        super((Writer)new NullWriter());
        this.lineSink = lineSink;
    }

    @Override
    public void close() {
        super.flush();
        super.close();
    }

    @Override
    public void flush() {
        if (this.buffer.length() == 0) {
            return;
        }
        this.lineSink.apply((Object)this.buffer.toString());
        this.buffer.setLength(0);
        super.flush();
    }

    @Override
    public void write(int c) {
        this.buffer.append(c);
    }

    @Override
    public void write(char[] cbuf, int off, int len) {
        this.buffer.append(cbuf, off, len);
    }

    @Override
    public void write(String str, int off, int len) {
        this.buffer.append(str.substring(off, off + len));
    }

    @Override
    public void println() {
        super.flush();
    }
}

