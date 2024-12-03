/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.writer;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.WritableByteChannel;
import org.apache.abdera.model.Base;
import org.apache.abdera.writer.WriterOptions;

public interface Writer {
    public void writeTo(Base var1, OutputStream var2) throws IOException;

    public void writeTo(Base var1, java.io.Writer var2) throws IOException;

    public Object write(Base var1) throws IOException;

    public void writeTo(Base var1, OutputStream var2, WriterOptions var3) throws IOException;

    public void writeTo(Base var1, java.io.Writer var2, WriterOptions var3) throws IOException;

    public Object write(Base var1, WriterOptions var2) throws IOException;

    public void writeTo(Base var1, WritableByteChannel var2) throws IOException;

    public void writeTo(Base var1, WritableByteChannel var2, WriterOptions var3) throws IOException;

    public WriterOptions getDefaultWriterOptions();

    public Writer setDefaultWriterOptions(WriterOptions var1);
}

