/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.encoding;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.pipe.Codec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;

public interface RootOnlyCodec
extends Codec {
    public void decode(@NotNull InputStream var1, @NotNull String var2, @NotNull Packet var3, @NotNull AttachmentSet var4) throws IOException;

    public void decode(@NotNull ReadableByteChannel var1, @NotNull String var2, @NotNull Packet var3, @NotNull AttachmentSet var4);
}

