/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 */
package com.sun.xml.ws.api.pipe;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Message;
import com.sun.xml.ws.api.pipe.Codec;
import javax.xml.stream.XMLStreamReader;

public interface StreamSOAPCodec
extends Codec {
    @NotNull
    public Message decode(@NotNull XMLStreamReader var1);

    @NotNull
    public Message decode(@NotNull XMLStreamReader var1, @NotNull AttachmentSet var2);
}

