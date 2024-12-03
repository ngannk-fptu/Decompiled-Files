/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.writer;

import org.apache.abdera.writer.StreamWriter;
import org.apache.abdera.writer.Writer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface WriterFactory {
    public <T extends Writer> T getWriter();

    public <T extends Writer> T getWriter(String var1);

    public <T extends Writer> T getWriterByMediaType(String var1);

    public <T extends StreamWriter> T newStreamWriter();

    public <T extends StreamWriter> T newStreamWriter(String var1);
}

