/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.module.sitemesh;

import com.opensymphony.module.sitemesh.SitemeshBuffer;
import com.opensymphony.module.sitemesh.SitemeshBufferFragment;
import java.io.IOException;
import java.io.Writer;

public interface SitemeshWriter {
    public Writer getUnderlyingWriter();

    public void write(int var1) throws IOException;

    public void write(char[] var1, int var2, int var3) throws IOException;

    public void write(char[] var1) throws IOException;

    public void write(String var1, int var2, int var3) throws IOException;

    public void write(String var1) throws IOException;

    public void flush() throws IOException;

    public void close() throws IOException;

    public boolean writeSitemeshBufferFragment(SitemeshBufferFragment var1) throws IOException;

    public SitemeshBuffer getSitemeshBuffer();
}

