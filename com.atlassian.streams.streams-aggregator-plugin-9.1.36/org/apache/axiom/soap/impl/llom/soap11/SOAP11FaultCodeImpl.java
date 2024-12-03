/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.util.OMSerializerUtil;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultValueImpl;

public class SOAP11FaultCodeImpl
extends SOAPFaultCodeImpl {
    public SOAP11FaultCodeImpl(SOAPFactory factory) {
        super("faultcode", null, factory);
    }

    public SOAP11FaultCodeImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, "faultcode", builder, factory);
    }

    public SOAP11FaultCodeImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "faultcode", false, factory);
    }

    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException {
        if (!(subCode instanceof SOAP11FaultSubCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultSubCodeImpl, got " + subCode.getClass());
        }
        super.setSubCode(subCode);
    }

    public void setValue(SOAPFaultValue value) throws SOAPProcessingException {
        if (!(value instanceof SOAP11FaultValueImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultValueImpl, got " + value.getClass());
        }
        super.setValue(value);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultImpl, got " + parent.getClass());
        }
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        this.registerContentHandler(writer);
        OMSerializerUtil.serializeStartpart(this, "faultcode", writer);
        writer.writeCharacters(this.getText());
        writer.writeEndElement();
    }

    public String getLocalName() {
        return "faultcode";
    }

    public SOAPFaultValue getValue() {
        return null;
    }

    public SOAPFaultSubCode getSubCode() {
        return null;
    }

    public void setValue(QName value) {
        this.setText(value);
    }

    public QName getValueAsQName() {
        return this.getTextAsQName();
    }
}

