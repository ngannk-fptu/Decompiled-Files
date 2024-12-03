/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.serializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;

interface WriterChain {
    public void write(int var1) throws IOException;

    public void write(char[] var1) throws IOException;

    public void write(char[] var1, int var2, int var3) throws IOException;

    public void write(String var1) throws IOException;

    public void write(String var1, int var2, int var3) throws IOException;

    public void flush() throws IOException;

    public void close() throws IOException;

    public Writer getWriter();

    public OutputStream getOutputStream();
}

