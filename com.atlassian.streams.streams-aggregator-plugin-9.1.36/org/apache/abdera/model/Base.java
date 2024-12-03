/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.model;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import org.apache.abdera.factory.Factory;
import org.apache.abdera.writer.WriterOptions;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Base
extends Cloneable {
    public WriterOptions getDefaultWriterOptions();

    public void writeTo(OutputStream var1, WriterOptions var2) throws IOException;

    public void writeTo(Writer var1, WriterOptions var2) throws IOException;

    public void writeTo(org.apache.abdera.writer.Writer var1, OutputStream var2) throws IOException;

    public void writeTo(org.apache.abdera.writer.Writer var1, Writer var2) throws IOException;

    public void writeTo(String var1, OutputStream var2) throws IOException;

    public void writeTo(String var1, Writer var2) throws IOException;

    public void writeTo(org.apache.abdera.writer.Writer var1, OutputStream var2, WriterOptions var3) throws IOException;

    public void writeTo(org.apache.abdera.writer.Writer var1, Writer var2, WriterOptions var3) throws IOException;

    public void writeTo(String var1, OutputStream var2, WriterOptions var3) throws IOException;

    public void writeTo(String var1, Writer var2, WriterOptions var3) throws IOException;

    public void writeTo(OutputStream var1) throws IOException;

    public void writeTo(Writer var1) throws IOException;

    public Object clone();

    public Factory getFactory();

    public <T extends Base> T addComment(String var1);

    public <T extends Base> T complete();
}

