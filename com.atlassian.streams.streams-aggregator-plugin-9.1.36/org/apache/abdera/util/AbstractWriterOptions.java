/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.util;

import org.apache.abdera.i18n.text.io.CompressionUtil;
import org.apache.abdera.writer.WriterOptions;

public abstract class AbstractWriterOptions
implements WriterOptions {
    protected String charset = "UTF-8";
    protected CompressionUtil.CompressionCodec[] codecs = null;
    protected boolean autoclose = false;

    public Object clone() throws CloneNotSupportedException {
        AbstractWriterOptions copy = (AbstractWriterOptions)super.clone();
        return copy;
    }

    public CompressionUtil.CompressionCodec[] getCompressionCodecs() {
        return this.codecs;
    }

    public WriterOptions setCompressionCodecs(CompressionUtil.CompressionCodec ... codecs) {
        this.codecs = codecs;
        return this;
    }

    public String getCharset() {
        return this.charset;
    }

    public WriterOptions setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public boolean getAutoClose() {
        return this.autoclose;
    }

    public WriterOptions setAutoClose(boolean autoclose) {
        this.autoclose = autoclose;
        return this;
    }
}

