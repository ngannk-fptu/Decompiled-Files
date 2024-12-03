/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultValueImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultSubCodeImpl;

public class SOAP11FaultValueImpl
extends SOAPFaultValueImpl {
    public SOAP11FaultValueImpl(OMElement parent, SOAPFactory factory) throws SOAPProcessingException {
        super("faultcode", parent, factory);
    }

    public SOAP11FaultValueImpl(SOAPFactory factory) throws SOAPProcessingException {
        super((OMNamespace)null, "faultcode", factory);
    }

    public SOAP11FaultValueImpl(OMElement parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, "faultcode", builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultSubCodeImpl) && !(parent instanceof SOAP11FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultSubCodeImpl or SOAP11FaultCodeImpl, got " + parent.getClass());
        }
    }
}

