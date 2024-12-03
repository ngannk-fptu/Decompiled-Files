/*
 * Decompiled with CFR 0.152.
 */
package javax.servlet.jsp;

import java.io.IOException;
import java.io.Writer;

public abstract class JspWriter
extends Writer {
    public static final int NO_BUFFER = 0;
    public static final int DEFAULT_BUFFER = -1;
    public static final int UNBOUNDED_BUFFER = -2;
    protected int bufferSize;
    protected boolean autoFlush;

    protected JspWriter(int bufferSize, boolean autoFlush) {
        this.bufferSize = bufferSize;
        this.autoFlush = autoFlush;
    }

    public abstract void newLine() throws IOException;

    public abstract void print(boolean var1) throws IOException;

    public abstract void print(char var1) throws IOException;

    public abstract void print(int var1) throws IOException;

    public abstract void print(long var1) throws IOException;

    public abstract void print(float var1) throws IOException;

    public abstract void print(double var1) throws IOException;

    public abstract void print(char[] var1) throws IOException;

    public abstract void print(String var1) throws IOException;

    public abstract void print(Object var1) throws IOException;

    public abstract void println() throws IOException;

    public abstract void println(boolean var1) throws IOException;

    public abstract void println(char var1) throws IOException;

    public abstract void println(int var1) throws IOException;

    public abstract void println(long var1) throws IOException;

    public abstract void println(float var1) throws IOException;

    public abstract void println(double var1) throws IOException;

    public abstract void println(char[] var1) throws IOException;

    public abstract void println(String var1) throws IOException;

    public abstract void println(Object var1) throws IOException;

    public abstract void clear() throws IOException;

    public abstract void clearBuffer() throws IOException;

    @Override
    public abstract void flush() throws IOException;

    @Override
    public abstract void close() throws IOException;

    public int getBufferSize() {
        return this.bufferSize;
    }

    public abstract int getRemaining();

    public boolean isAutoFlush() {
        return this.autoFlush;
    }
}

