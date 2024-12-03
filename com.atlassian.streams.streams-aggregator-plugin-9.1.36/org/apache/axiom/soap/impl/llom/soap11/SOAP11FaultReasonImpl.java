/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultReasonImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;

public class SOAP11FaultReasonImpl
extends SOAPFaultReasonImpl {
    public SOAP11FaultReasonImpl(SOAPFactory factory) {
        super(null, factory);
    }

    public SOAP11FaultReasonImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    public SOAP11FaultReasonImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super((OMElement)parent, false, factory);
    }

    public void addSOAPText(SOAPFaultText soapFaultText) throws SOAPProcessingException {
        throw new UnsupportedOperationException("addSOAPText() not allowed for SOAP 1.1!");
    }

    public SOAPFaultText getFirstSOAPText() {
        throw new UnsupportedOperationException("getFirstSOAPText() not allowed for SOAP 1.1!");
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultImpl, got " + parent.getClass());
        }
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        this.registerContentHandler(writer);
        OMSerializerUtil.serializeStartpart(this, "faultstring", writer);
        writer.writeCharacters(this.getText());
        writer.writeEndElement();
    }

    public String getLocalName() {
        return "faultstring";
    }
}

