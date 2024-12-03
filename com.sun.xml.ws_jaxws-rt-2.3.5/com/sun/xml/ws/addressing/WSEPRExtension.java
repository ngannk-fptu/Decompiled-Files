/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.stream.buffer.XMLStreamBuffer
 */
package com.sun.xml.ws.addressing;

import com.sun.xml.stream.buffer.XMLStreamBuffer;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class WSEPRExtension
extends WSEndpointReference.EPRExtension {
    XMLStreamBuffer xsb;
    final QName qname;

    public WSEPRExtension(XMLStreamBuffer xsb, QName qname) {
        this.xsb = xsb;
        this.qname = qname;
    }

    @Override
    public XMLStreamReader readAsXMLStreamReader() throws XMLStreamException {
        return this.xsb.readAsXMLStreamReader();
    }

    @Override
    public QName getQName() {
        return this.qname;
    }
}

