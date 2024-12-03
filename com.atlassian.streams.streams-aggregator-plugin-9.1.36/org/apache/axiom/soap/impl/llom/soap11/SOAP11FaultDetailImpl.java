/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPFaultDetailImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11FaultImpl;

public class SOAP11FaultDetailImpl
extends SOAPFaultDetailImpl {
    public SOAP11FaultDetailImpl(SOAPFactory factory) {
        super(null, factory);
    }

    public SOAP11FaultDetailImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, false, factory);
    }

    public SOAP11FaultDetailImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, builder, factory);
    }

    protected void checkParent(OMElement parent) throws SOAPProcessingException {
        if (!(parent instanceof SOAP11FaultImpl)) {
            throw new SOAPProcessingException("Expecting SOAP11FaultImpl, got " + parent.getClass());
        }
    }
}

