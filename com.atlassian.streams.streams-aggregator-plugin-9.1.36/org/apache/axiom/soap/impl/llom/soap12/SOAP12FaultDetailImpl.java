/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;

public class SOAP12FaultDetailImpl
extends SOAPFaultDetailImpl {
    public SOAP12FaultDetailImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12FaultDetailImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, true, factory);
    }

    public SOAP12FaultDetailImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP12FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP12FaultImpl as parent, got " + parent.getClass());
        }
    }
}

