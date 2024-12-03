/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter$Breakpoint
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.LazyEnvelopeSource;
import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.messaging.saaj.soap.StaxBridge;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;

public class StaxLazySourceBridge
extends StaxBridge {
    private LazyEnvelopeSource lazySource;

    public StaxLazySourceBridge(LazyEnvelopeSource src, SOAPPartImpl soapPart) throws SOAPException {
        super(soapPart);
        this.lazySource = src;
        final String soapEnvNS = soapPart.getSOAPNamespace();
        try {
            this.breakpoint = new XMLStreamReaderToXMLStreamWriter.Breakpoint(src.readToBodyStarTag(), this.saajWriter){

                public boolean proceedAfterStartElement() {
                    return !"Body".equals(this.reader.getLocalName()) || !soapEnvNS.equals(this.reader.getNamespaceURI());
                }
            };
        }
        catch (XMLStreamException e) {
            throw new SOAPException((Throwable)e);
        }
    }

    @Override
    public XMLStreamReader getPayloadReader() {
        return this.lazySource.readPayload();
    }

    @Override
    public QName getPayloadQName() {
        return this.lazySource.getPayloadQName();
    }

    @Override
    public String getPayloadAttributeValue(String attName) {
        XMLStreamReader reader;
        if (this.lazySource.isPayloadStreamReader() && (reader = this.lazySource.readPayload()).getEventType() == 1) {
            return reader.getAttributeValue(null, attName);
        }
        return null;
    }

    @Override
    public String getPayloadAttributeValue(QName attName) {
        XMLStreamReader reader;
        if (this.lazySource.isPayloadStreamReader() && (reader = this.lazySource.readPayload()).getEventType() == 1) {
            return reader.getAttributeValue(attName.getNamespaceURI(), attName.getLocalPart());
        }
        return null;
    }

    @Override
    public void bridgePayload() throws XMLStreamException {
        this.writePayloadTo(this.saajWriter);
    }

    public void writePayloadTo(XMLStreamWriter writer) throws XMLStreamException {
        this.lazySource.writePayloadTo(writer);
    }
}

