/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.message.stream;

import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.message.AttachmentSetImpl;

abstract class StreamBasedMessage {
    public final Packet properties;
    public final AttachmentSet attachments;

    protected StreamBasedMessage(Packet properties) {
        this.properties = properties;
        this.attachments = new AttachmentSetImpl();
    }

    protected StreamBasedMessage(Packet properties, AttachmentSet attachments) {
        this.properties = properties;
        this.attachments = attachments;
    }
}

