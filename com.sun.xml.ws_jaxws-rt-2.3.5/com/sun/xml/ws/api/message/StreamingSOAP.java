/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.ws.api.message;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public interface StreamingSOAP {
    public XMLStreamReader readEnvelope();

    public QName getPayloadQName();

    public XMLStreamReader readToBodyStarTag() throws XMLStreamException;

    public XMLStreamReader readPayload() throws XMLStreamException;

    public void writeToBodyStart(XMLStreamWriter var1) throws XMLStreamException;

    public void writePayloadTo(XMLStreamWriter var1) throws XMLStreamException;

    public boolean isPayloadStreamReader();
}

