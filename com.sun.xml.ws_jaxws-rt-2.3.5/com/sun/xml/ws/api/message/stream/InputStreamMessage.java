/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.message.stream;

import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.stream.StreamBasedMessage;
import java.io.InputStream;

public class InputStreamMessage
extends StreamBasedMessage {
    public final String contentType;
    public final InputStream msg;

    public InputStreamMessage(Packet properties, String contentType, InputStream msg) {
        super(properties);
        this.contentType = contentType;
        this.msg = msg;
    }

    public InputStreamMessage(Packet properties, AttachmentSet attachments, String contentType, InputStream msg) {
        super(properties, attachments);
        this.contentType = contentType;
        this.msg = msg;
    }
}

