/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.SOAPProcessingException;

public class SOAPMessageImpl
extends OMDocumentImpl
implements SOAPMessage {
    public SOAPMessageImpl(SOAPFactory factory) {
        super(factory);
    }

    public SOAPMessageImpl(OMXMLParserWrapper parserWrapper, SOAPFactory factory) {
        super(parserWrapper, factory);
    }

    public SOAPEnvelope getSOAPEnvelope() throws SOAPProcessingException {
        return (SOAPEnvelope)this.getOMDocumentElement();
    }

    public void setSOAPEnvelope(SOAPEnvelope envelope) throws SOAPProcessingException {
        super.addChild(envelope, true);
    }

    public void setOMDocumentElement(OMElement rootElement) {
        throw new UnsupportedOperationException("This is not allowed. Use set SOAPEnvelope instead");
    }

    protected void internalSerialize(XMLStreamWriter writer, boolean cache, boolean includeXMLDeclaration) throws XMLStreamException {
        ((OMNodeEx)((Object)this.getOMDocumentElement())).internalSerialize(writer, cache);
    }

    protected OMDocument createClone(OMCloneOptions options) {
        return new SOAPMessageImpl((SOAPFactory)this.getOMFactory());
    }
}

