/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.scalability.outputlength;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import java.io.IOException;
import java.io.Writer;

public class OutputLengthObservantSitemeshWriter
extends Writer
implements SitemeshWriter {
    private final OutputLengthObserver outputLengthObserver;
    private final SitemeshWriter delegate;

    public OutputLengthObservantSitemeshWriter(OutputLengthObserver outputLengthObserver, SitemeshWriter delegate) {
        this.outputLengthObserver = outputLengthObserver;
        this.delegate = delegate;
    }

    public Writer getUnderlyingWriter() {
        return this;
    }

    public void write(int c) throws IOException {
        this.outputLengthObserver.nChars(1L);
        this.delegate.write(c);
    }

    public void write(char[] chars, int off, int len) throws IOException {
        this.outputLengthObserver.nChars(len - off);
        this.delegate.write(chars, off, len);
    }

    public void write(char[] chars) throws IOException {
        this.outputLengthObserver.nChars(chars.length);
        this.delegate.write(chars);
    }

    public void write(String str, int off, int len) throws IOException {
        this.outputLengthObserver.nChars(len - off);
        this.delegate.write(str, off, len);
    }

    public void write(String str) throws IOException {
        this.outputLengthObserver.nChars(str.length());
        this.delegate.write(str);
    }

    public void flush() throws IOException {
        this.delegate.flush();
    }

    public void close() throws IOException {
        this.delegate.close();
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        return this.delegate.writeSitemeshBufferFragment(bufferFragment);
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return this.delegate.getSitemeshBuffer();
    }
}

