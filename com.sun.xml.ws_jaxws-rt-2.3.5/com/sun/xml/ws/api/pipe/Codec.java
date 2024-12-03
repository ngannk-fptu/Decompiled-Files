/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.pipe;

import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.ContentType;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

public interface Codec {
    public String getMimeType();

    public ContentType getStaticContentType(Packet var1);

    public ContentType encode(Packet var1, OutputStream var2) throws IOException;

    public ContentType encode(Packet var1, WritableByteChannel var2);

    public Codec copy();

    public void decode(InputStream var1, String var2, Packet var3) throws IOException;

    public void decode(ReadableByteChannel var1, String var2, Packet var3);
}

