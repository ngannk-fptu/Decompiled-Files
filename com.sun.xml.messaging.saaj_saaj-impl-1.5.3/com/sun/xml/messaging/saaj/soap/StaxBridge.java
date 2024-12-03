/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter$Breakpoint
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.messaging.saaj.util.stax.SaajStaxWriter;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;

public abstract class StaxBridge {
    protected SaajStaxWriter saajWriter;
    protected XMLStreamReaderToXMLStreamWriter readerToWriter = new XMLStreamReaderToXMLStreamWriter();
    protected XMLStreamReaderToXMLStreamWriter.Breakpoint breakpoint;

    public StaxBridge(SOAPPartImpl soapPart) throws SOAPException {
        this.saajWriter = new SaajStaxWriter(soapPart.message, soapPart.getSOAPNamespace());
    }

    public void bridgeEnvelopeAndHeaders() throws XMLStreamException {
        this.readerToWriter.bridge(this.breakpoint);
    }

    public void bridgePayload() throws XMLStreamException {
        this.readerToWriter.bridge(this.breakpoint);
    }

    public abstract XMLStreamReader getPayloadReader();

    public abstract QName getPayloadQName();

    public abstract String getPayloadAttributeValue(String var1);

    public abstract String getPayloadAttributeValue(QName var1);
}

