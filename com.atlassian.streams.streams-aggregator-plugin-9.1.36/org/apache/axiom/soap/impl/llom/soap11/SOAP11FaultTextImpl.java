/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultTextImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultReasonImpl;

public class SOAP11FaultTextImpl
extends SOAPFaultTextImpl {
    public SOAP11FaultTextImpl(SOAPFaultReason parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory);
    }

    public SOAP11FaultTextImpl(SOAPFactory factory) throws SOAPProcessingException {
        super((OMNamespace)null, factory);
    }

    public SOAP11FaultTextImpl(SOAPFaultReason parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultReasonImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultReasonImpl, got " + parent.getClass());
        }
    }
}

