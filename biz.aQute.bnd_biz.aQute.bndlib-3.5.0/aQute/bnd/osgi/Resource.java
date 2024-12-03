/*
 * Decompiled with CFR 0.152.
 */
package aQute.bnd.osgi;

import java.io.Closeable;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public interface Resource
extends Closeable {
    public InputStream openInputStream() throws Exception;

    public void write(OutputStream var1) throws Exception;

    public long lastModified();

    public void setExtra(String var1);

    public String getExtra();

    public long size() throws Exception;

    public ByteBuffer buffer() throws Exception;
}

