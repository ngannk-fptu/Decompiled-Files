/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultValueImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultCodeImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultSubCodeImpl;

public class SOAP12FaultValueImpl
extends SOAPFaultValueImpl {
    public SOAP12FaultValueImpl(OMElement parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory);
    }

    public SOAP12FaultValueImpl(SOAPFactory factory) throws SOAPProcessingException {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultValueImpl(OMElement parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultSubCodeImpl) && !(parent instanceof SOAP12FaultCodeImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultSubCodeImpl or SOAP12FaultCodeImpl as parent, got " + parent.getClass());
        }
    }
}

