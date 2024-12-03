/*
 * Decompiled with CFR 0.152.
 */
package org.jvnet.staxex;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

public interface StAxSOAPBody {
    public void setPayload(Payload var1) throws XMLStreamException;

    public Payload getPayload() throws XMLStreamException;

    public boolean hasStaxPayload();

    public static interface Payload {
        public QName getPayloadQName();

        public XMLStreamReader readPayload() throws XMLStreamException;

        public void writePayloadTo(XMLStreamWriter var1) throws XMLStreamException;

        public String getPayloadAttributeValue(String var1) throws XMLStreamException;

        public String getPayloadAttributeValue(QName var1) throws XMLStreamException;

        public void materialize() throws XMLStreamException;
    }
}

