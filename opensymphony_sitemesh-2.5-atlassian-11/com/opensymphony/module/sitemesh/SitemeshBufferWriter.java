/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.DefaultSitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import com.opensymphony.module.sitemesh.SitemeshWriter;
import com.opensymphony.module.sitemesh.util.CharArrayWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.TreeMap;

public class SitemeshBufferWriter
extends CharArrayWriter
implements SitemeshWriter {
    private final TreeMap<Integer, SitemeshBufferFragment> fragments = new TreeMap();

    public SitemeshBufferWriter() {
    }

    public SitemeshBufferWriter(int initialSize) {
        super(initialSize);
    }

    public Writer getUnderlyingWriter() {
        return this;
    }

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment bufferFragment) throws IOException {
        this.fragments.put(this.count, bufferFragment);
        return false;
    }

    public SitemeshBuffer getSitemeshBuffer() {
        return new DefaultSitemeshBuffer(this.buf, this.count, this.fragments);
    }
}

