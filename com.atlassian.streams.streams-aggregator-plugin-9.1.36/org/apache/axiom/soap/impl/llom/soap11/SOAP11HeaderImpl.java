/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap11;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.traverse.OMChildrenWithSpecificAttributeIterator;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPHeaderImpl;

public class SOAP11HeaderImpl
extends SOAPHeaderImpl {
    public SOAP11HeaderImpl(SOAPFactory factory) throws SOAPProcessingException {
        super(factory.getNamespace(), factory);
    }

    public SOAP11HeaderImpl(SOAPEnvelope envelope, SOAPFactory factory) throws SOAPProcessingException {
        super(envelope, factory);
    }

    public SOAP11HeaderImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(envelope, builder, factory);
    }

    public Iterator extractHeaderBlocks(String role) {
        return new OMChildrenWithSpecificAttributeIterator(this.getFirstOMChild(), new QName("http://schemas.xmlsoap.org/soap/envelope/", "actor"), role, true);
    }
}

