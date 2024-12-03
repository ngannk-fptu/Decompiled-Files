/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultSubCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultValueImpl;

public class SOAP11FaultSubCodeImpl
extends SOAPFaultSubCodeImpl {
    public SOAP11FaultSubCodeImpl(SOAPFactory factory) {
        super(null, factory);
    }

    public SOAP11FaultSubCodeImpl(SOAPFaultCode parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Subcode", factory);
    }

    public SOAP11FaultSubCodeImpl(SOAPFaultCode parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMElement)parent, "Subcode", builder, factory);
    }

    public SOAP11FaultSubCodeImpl(SOAPFaultSubCode parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Subcode", factory);
    }

    public SOAP11FaultSubCodeImpl(SOAPFaultSubCode parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMElement)parent, "Subcode", builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultSubCodeImpl) && !(parent instanceof SOAP11FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultSubCodeImpl or SOAP11FaultCodeImpl, got " + parent.getClass());
        }
    }

    public void setSubCode(SOAPFaultSubCode subCode) throws SOAPProcessingException {
        if (!(this.parent instanceof SOAP11FaultSubCodeImpl) && !(this.parent instanceof SOAP11FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultSubCodeImpl or SOAP11FaultCodeImpl, got " + subCode.getClass());
        }
        super.setSubCode(subCode);
    }

    public void setValue(SOAPFaultValue soapFaultSubCodeValue) throws SOAPProcessingException {
        if (!(soapFaultSubCodeValue instanceof SOAP11FaultValueImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultValueImpl, got " + soapFaultSubCodeValue.getClass());
        }
        super.setValue(soapFaultSubCodeValue);
    }

    public void setValue(QName value) {
        throw new UnsupportedOperationException();
    }

    public QName getValueAsQName() {
        throw new UnsupportedOperationException();
    }
}

