/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPBodyImpl;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12FaultImpl;

public class SOAP12BodyImpl
extends SOAPBodyImpl {
    public SOAP12BodyImpl(SOAPFactory factory) {
        super("Body", factory.getNamespace(), factory);
    }

    public SOAP12BodyImpl(SOAPEnvelope envelope, SOAPFactory factory) throws SOAPProcessingException {
        super(envelope, factory);
    }

    public SOAP12BodyImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(envelope, builder, factory);
    }

    public SOAPFault addFault(Exception e) throws OMException {
        return new SOAP12FaultImpl((SOAPBody)this, e, (SOAPFactory)this.factory);
    }
}

