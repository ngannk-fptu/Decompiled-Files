/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.message.stream;

import com.sun.xml.ws.api.message.AttachmentSet;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.message.stream.StreamBasedMessage;
import javax.xml.stream.XMLStreamReader;

public class XMLStreamReaderMessage
extends StreamBasedMessage {
    public final XMLStreamReader msg;

    public XMLStreamReaderMessage(Packet properties, XMLStreamReader msg) {
        super(properties);
        this.msg = msg;
    }

    public XMLStreamReaderMessage(Packet properties, AttachmentSet attachments, XMLStreamReader msg) {
        super(properties, attachments);
        this.msg = msg;
    }
}

