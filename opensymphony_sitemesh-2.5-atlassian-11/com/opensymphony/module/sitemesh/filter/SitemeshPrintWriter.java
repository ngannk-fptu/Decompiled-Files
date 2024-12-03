/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh.filter;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

public class SitemeshPrintWriter
extends PrintWriter
implements SitemeshWriter {
    private final SitemeshWriter sitemeshWriter;

    public SitemeshPrintWriter(SitemeshWriter sitemeshWriter) {
        super(sitemeshWriter.getUnderlyingWriter());
        this.sitemeshWriter = sitemeshWriter;
    }

    public Writer getUnderlyingWriter() {
        return this;
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        return this.sitemeshWriter.writeSitemeshBufferFragment(bufferFragment);
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return this.sitemeshWriter.getSitemeshBuffer();
    }
}

