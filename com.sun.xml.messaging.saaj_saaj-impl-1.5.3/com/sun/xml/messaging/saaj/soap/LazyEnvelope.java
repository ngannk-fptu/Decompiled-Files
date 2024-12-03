/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.Envelope;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public interface LazyEnvelope
extends Envelope {
    public XMLStreamReader getPayloadReader() throws SOAPException;

    public boolean isLazy();

    public void writeTo(XMLStreamWriter var1) throws XMLStreamException, SOAPException;

    public QName getPayloadQName() throws SOAPException;

    public String getPayloadAttributeValue(String var1) throws SOAPException;

    public String getPayloadAttributeValue(QName var1) throws SOAPException;
}

