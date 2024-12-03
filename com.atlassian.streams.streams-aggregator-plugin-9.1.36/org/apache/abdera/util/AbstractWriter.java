/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;
import java.util.zip.DeflaterOutputStream;
import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.model.Base;
import org.apache.abdera.model.Document;
import org.apache.abdera.model.Element;
import org.apache.abdera.writer.Writer;
import org.apache.abdera.writer.WriterOptions;

public abstract class AbstractWriter
implements Writer {
    protected WriterOptions options;

    public WriterOptions getDefaultWriterOptions() {
        if (this.options == null) {
            this.options = this.initDefaultWriterOptions();
        }
        try {
            return (WriterOptions)this.options.clone();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
    }

    protected abstract WriterOptions initDefaultWriterOptions();

    public synchronized Writer setDefaultWriterOptions(WriterOptions options) {
        try {
            this.options = options != null ? (WriterOptions)options.clone() : this.initDefaultWriterOptions();
        }
        catch (CloneNotSupportedException cnse) {
            throw new RuntimeException(cnse);
        }
        return this;
    }

    public Object write(Base base) throws IOException {
        return this.write(base, this.getDefaultWriterOptions());
    }

    public void writeTo(Base base, OutputStream out) throws IOException {
        this.writeTo(base, out, this.getDefaultWriterOptions());
    }

    public void writeTo(Base base, java.io.Writer out) throws IOException {
        this.writeTo(base, out, this.getDefaultWriterOptions());
    }

    protected OutputStream getCompressedOutputStream(OutputStream out, WriterOptions options) throws IOException {
        if (options.getCompressionCodecs() != null) {
            out = CompressionUtil.getEncodedOutputStream(out, options.getCompressionCodecs());
        }
        return out;
    }

    protected void finishCompressedOutputStream(OutputStream out, WriterOptions options) throws IOException {
        if (options.getCompressionCodecs() != null) {
            ((DeflaterOutputStream)out).finish();
        }
    }

    public void writeTo(Base base, WritableByteChannel out, WriterOptions options) throws IOException {
        String charset = options.getCharset();
        if (charset == null) {
            Document doc = null;
            if (base instanceof Document) {
                doc = (Document)base;
            } else if (base instanceof Element) {
                doc = ((Element)base).getDocument();
            }
            charset = doc != null ? doc.getCharset() : null;
        }
        this.writeTo(base, Channels.newWriter(out, charset != null ? charset : "utf-8"), options);
    }

    public void writeTo(Base base, WritableByteChannel out) throws IOException {
        this.writeTo(base, out, this.getDefaultWriterOptions());
    }
}

