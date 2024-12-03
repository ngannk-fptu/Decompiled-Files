/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter$Breakpoint
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.SOAPPartImpl;
import com.sun.xml.messaging.saaj.soap.StaxBridge;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.stream.XMLStreamReader;
import org.jvnet.staxex.util.XMLStreamReaderToXMLStreamWriter;

public class StaxReaderBridge
extends StaxBridge {
    private XMLStreamReader in;

    public StaxReaderBridge(XMLStreamReader reader, SOAPPartImpl soapPart) throws SOAPException {
        super(soapPart);
        this.in = reader;
        final String soapEnvNS = soapPart.getSOAPNamespace();
        this.breakpoint = new XMLStreamReaderToXMLStreamWriter.Breakpoint(reader, this.saajWriter){
            boolean seenBody;
            boolean stopedAtBody;
            {
                super(arg0, arg1);
                this.seenBody = false;
                this.stopedAtBody = false;
            }

            public boolean proceedBeforeStartElement() {
                if (this.stopedAtBody) {
                    return true;
                }
                if (this.seenBody) {
                    this.stopedAtBody = true;
                    return false;
                }
                if ("Body".equals(this.reader.getLocalName()) && soapEnvNS.equals(this.reader.getNamespaceURI())) {
                    this.seenBody = true;
                }
                return true;
            }
        };
    }

    @Override
    public XMLStreamReader getPayloadReader() {
        return this.in;
    }

    @Override
    public QName getPayloadQName() {
        return this.in.getEventType() == 1 ? this.in.getName() : null;
    }

    @Override
    public String getPayloadAttributeValue(String attName) {
        return this.in.getEventType() == 1 ? this.in.getAttributeValue(null, attName) : null;
    }

    @Override
    public String getPayloadAttributeValue(QName attName) {
        return this.in.getEventType() == 1 ? this.in.getAttributeValue(attName.getNamespaceURI(), attName.getLocalPart()) : null;
    }
}

