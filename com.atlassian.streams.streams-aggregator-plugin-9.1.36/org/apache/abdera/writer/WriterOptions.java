/*
 * Decompiled with CFR 0.152.
 */
package org.apache.abdera.writer;

import org.apache.abdera.i18n.text.io.CompressionUtil;

public interface WriterOptions
extends Cloneable {
    public CompressionUtil.CompressionCodec[] getCompressionCodecs();

    public WriterOptions setCompressionCodecs(CompressionUtil.CompressionCodec ... var1);

    public Object clone() throws CloneNotSupportedException;

    public String getCharset();

    public WriterOptions setCharset(String var1);

    public boolean getAutoClose();

    public WriterOptions setAutoClose(boolean var1);
}

