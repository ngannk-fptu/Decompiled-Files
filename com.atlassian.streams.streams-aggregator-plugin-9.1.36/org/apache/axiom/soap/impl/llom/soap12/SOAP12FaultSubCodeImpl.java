/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultValueImpl;

public class SOAP12FaultSubCodeImpl
extends SOAPFaultSubCodeImpl {
    public SOAP12FaultSubCodeImpl(SOAPFaultCode parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Subcode", factory);
    }

    public SOAP12FaultSubCodeImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultSubCodeImpl(SOAPFaultCode parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMElement)parent, "Subcode", builder, factory);
    }

    public SOAP12FaultSubCodeImpl(SOAPFaultSubCode parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Subcode", factory);
    }

    public SOAP12FaultSubCodeImpl(SOAPFaultSubCode parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMElement)parent, "Subcode", builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultSubCodeImpl) && !(parent instanceof SOAP12FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP 1.2 implementation of SOAP FaultSubCode or SOAP FaultCodeValue as the parent. But received some other implementation");
        }
    }

    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException {
        if (!(subCode instanceof SOAP12FaultSubCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultSubCodeImpl, got " + subCode.getClass());
        }
        super.setSubCode(subCode);
    }

    public void setValue(SOAPFaultValue soapFaultSubCodeValue) throws SOAPProcessingException {
        if (!(soapFaultSubCodeValue instanceof SOAP12FaultValueImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultValueImpl, got " + soapFaultSubCodeValue.getClass());
        }
        super.setValue(soapFaultSubCodeValue);
    }

    public void setValue(QName value) {
        SOAPFaultValue valueElement = this.getValue();
        if (valueElement == null) {
            valueElement = ((SOAPFactory)this.getOMFactory()).createSOAPFaultValue(this);
        }
        valueElement.setText(value);
    }

    public QName getValueAsQName() {
        SOAPFaultValue value = this.getValue();
        return value == null ? null : value.getTextAsQName();
    }
}

