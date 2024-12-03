/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.messaging.saaj;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Source;

public interface LazyEnvelopeSource
extends Source {
    public QName getPayloadQName();

    public XMLStreamReader readToBodyStarTag() throws XMLStreamException;

    public XMLStreamReader readPayload();

    public void writePayloadTo(XMLStreamWriter var1) throws XMLStreamException;

    public boolean isPayloadStreamReader();
}

