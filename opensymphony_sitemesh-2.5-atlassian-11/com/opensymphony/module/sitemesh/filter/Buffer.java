/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.Page;
import com.opensymphony.module.sitemesh.PageParser;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferWriter;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.filter.SitemeshPrintWriter;
import com.opensymphony.module.sitemesh.filter.TextEncoder;
import com.opensymphony.module.sitemesh.scalability.ScalabilitySupport;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObservantSitemeshWriter;
import com.opensymphony.module.sitemesh.scalability.outputlength.OutputLengthObserver;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorage;
import com.opensymphony.module.sitemesh.scalability.secondarystorage.SecondaryStorageBufferWriter;
import com.opensymphony.module.sitemesh.util.FastByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import javax.servlet.ServletOutputStream;

public class Buffer {
    private final PageParser pageParser;
    private final String encoding;
    private final OutputLengthObserver outputLengthObserver;
    private final SecondaryStorage secondaryStorage;
    private final int initialBufferSize;
    private static final TextEncoder TEXT_ENCODER = new TextEncoder();
    private SitemeshWriter bufferedWriter;
    private FastByteArrayOutputStream bufferedStream;
    private PrintWriter exposedWriter;
    private ServletOutputStream exposedStream;

    public Buffer(PageParser pageParser, String encoding, ScalabilitySupport scalabilitySupport) {
        this.pageParser = pageParser;
        this.encoding = encoding;
        this.outputLengthObserver = scalabilitySupport.getOutputLengthObserver();
        this.secondaryStorage = scalabilitySupport.getSecondaryStorage();
        this.initialBufferSize = scalabilitySupport.getInitialBufferSize();
    }

    public SitemeshBuffer getContents() throws IOException {
        if (this.bufferedWriter != null) {
            return this.bufferedWriter.getSitemeshBuffer();
        }
        if (this.bufferedStream != null) {
            return new DefaultSitemeshBuffer(TEXT_ENCODER.encode(this.bufferedStream.toByteArray(), this.encoding));
        }
        return new DefaultSitemeshBuffer(new char[0]);
    }

    public boolean hasBeenOpened() {
        return this.exposedStream != null || this.exposedWriter != null;
    }

    public Page parse() throws IOException {
        return this.pageParser.parse(this.getContents());
    }

    public PrintWriter getWriter() {
        if (this.bufferedWriter == null) {
            if (this.bufferedStream != null) {
                throw new IllegalStateException("response.getWriter() called after response.getOutputStream()");
            }
            Writer bufferredWriterToUse = this.secondaryStorage != null && this.secondaryStorage.getMemoryLimitBeforeUse() > 0L ? new SecondaryStorageBufferWriter(this.initialBufferSize, this.secondaryStorage) : new SitemeshBufferWriter(this.initialBufferSize);
            bufferredWriterToUse = new OutputLengthObservantSitemeshWriter(this.outputLengthObserver, (SitemeshWriter)((Object)bufferredWriterToUse));
            this.bufferedWriter = bufferredWriterToUse;
            this.exposedWriter = new SitemeshPrintWriter((SitemeshWriter)((Object)bufferredWriterToUse));
        }
        return this.exposedWriter;
    }

    public ServletOutputStream getOutputStream() {
        if (this.bufferedStream == null) {
            if (this.bufferedWriter != null) {
                throw new IllegalStateException("response.getOutputStream() called after response.getWriter()");
            }
            this.bufferedStream = new FastByteArrayOutputStream();
            this.exposedStream = new ServletOutputStream(){

                public void write(int b) {
                    Buffer.this.outputLengthObserver.nBytes(1L);
                    Buffer.this.bufferedStream.write(b);
                }

                public void write(byte[] b) throws IOException {
                    Buffer.this.outputLengthObserver.nBytes(b.length);
                    Buffer.this.bufferedStream.write(b);
                }

                public void write(byte[] b, int off, int len) throws IOException {
                    Buffer.this.outputLengthObserver.nBytes(len);
                    Buffer.this.bufferedStream.write(b, off, len);
                }
            };
        }
        return this.exposedStream;
    }

    public boolean isUsingStream() {
        return this.bufferedStream != null;
    }
}

