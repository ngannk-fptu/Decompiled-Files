/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultTextImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultReasonImpl;

public class SOAP12FaultTextImpl
extends SOAPFaultTextImpl {
    public SOAP12FaultTextImpl(SOAPFaultReason parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory);
    }

    public SOAP12FaultTextImpl(SOAPFactory factory) throws SOAPProcessingException {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultTextImpl(SOAPFaultReason parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultReasonImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultReasonImpl as parent, got " + parent.getClass());
        }
    }
}

