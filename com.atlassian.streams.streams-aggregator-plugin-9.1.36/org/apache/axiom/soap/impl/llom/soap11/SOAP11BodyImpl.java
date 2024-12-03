/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPBody;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPBodyImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;

public class SOAP11BodyImpl
extends SOAPBodyImpl {
    public SOAP11BodyImpl(SOAPEnvelope envelope, SOAPFactory factory) throws SOAPProcessingException {
        super(envelope, factory);
    }

    public SOAP11BodyImpl(SOAPFactory factory) throws SOAPProcessingException {
        super("Body", factory.getNamespace(), factory);
    }

    public SOAP11BodyImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(envelope, builder, factory);
    }

    public SOAPFault addFault(Exception e) throws OMException {
        return ((SOAP11Factory)this.factory).createSOAPFault((SOAPBody)this, e);
    }
}

