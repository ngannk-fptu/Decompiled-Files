/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom.soap12;

import java.util.Iterator;
import javax.xml.namespace.QName;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.traverse.OMChildrenWithSpecificAttributeIterator;
import org.apache.axiom.soap.SOAPEnvelope;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPHeaderImpl;

public class SOAP12HeaderImpl
extends SOAPHeaderImpl {
    public SOAP12HeaderImpl(SOAPFactory factory) {
        super(factory.getNamespace(), factory);
    }

    public SOAP12HeaderImpl(SOAPEnvelope envelope, SOAPFactory factory) throws SOAPProcessingException {
        super(envelope, factory);
    }

    public SOAP12HeaderImpl(SOAPEnvelope envelope, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(envelope, builder, factory);
    }

    public Iterator extractHeaderBlocks(String role) {
        return new OMChildrenWithSpecificAttributeIterator(this.getFirstOMChild(), new QName("http://www.w3.org/2003/05/soap-envelope", "role"), role, true);
    }
}

