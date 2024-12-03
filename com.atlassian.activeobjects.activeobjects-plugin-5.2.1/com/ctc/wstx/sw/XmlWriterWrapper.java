/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.sw;

import com.ctc.wstx.sw.XmlWriter;
import java.io.IOException;
import java.io.Writer;

public abstract class XmlWriterWrapper
extends Writer {
    protected final XmlWriter mWriter;
    private char[] mBuffer = null;

    public static XmlWriterWrapper wrapWriteRaw(XmlWriter xw) {
        return new RawWrapper(xw);
    }

    public static XmlWriterWrapper wrapWriteCharacters(XmlWriter xw) {
        return new TextWrapper(xw);
    }

    protected XmlWriterWrapper(XmlWriter writer) {
        this.mWriter = writer;
    }

    public final void close() throws IOException {
        this.mWriter.close(false);
    }

    public final void flush() throws IOException {
        this.mWriter.flush();
    }

    public final void write(char[] cbuf) throws IOException {
        this.write(cbuf, 0, cbuf.length);
    }

    public abstract void write(char[] var1, int var2, int var3) throws IOException;

    public final void write(int c) throws IOException {
        if (this.mBuffer == null) {
            this.mBuffer = new char[1];
        }
        this.mBuffer[0] = (char)c;
        this.write(this.mBuffer, 0, 1);
    }

    public abstract void write(String var1) throws IOException;

    public abstract void write(String var1, int var2, int var3) throws IOException;

    private static class TextWrapper
    extends XmlWriterWrapper {
        protected TextWrapper(XmlWriter writer) {
            super(writer);
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            this.mWriter.writeCharacters(cbuf, off, len);
        }

        public void write(String str) throws IOException {
            this.mWriter.writeCharacters(str);
        }

        public void write(String str, int off, int len) throws IOException {
            this.mWriter.writeCharacters(str.substring(off, off + len));
        }
    }

    private static final class RawWrapper
    extends XmlWriterWrapper {
        protected RawWrapper(XmlWriter writer) {
            super(writer);
        }

        public void write(char[] cbuf, int off, int len) throws IOException {
            this.mWriter.writeRaw(cbuf, off, len);
        }

        public void write(String str, int off, int len) throws IOException {
            this.mWriter.writeRaw(str, off, len);
        }

        public final void write(String str) throws IOException {
            this.mWriter.writeRaw(str, 0, str.length());
        }
    }
}

