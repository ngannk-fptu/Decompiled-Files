/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.factory.AbstractOMMetaFactory;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPMessage;
import org.apache.axiom.soap.impl.llom.SOAPMessageImpl;
import org.apache.axiom.soap.impl.llom.soap11.SOAP11Factory;
import org.apache.axiom.soap.impl.llom.soap12.SOAP12Factory;

public class OMLinkedListMetaFactory
extends AbstractOMMetaFactory {
    private final OMFactory omFactory = new OMLinkedListImplFactory(this);
    private final SOAPFactory soap11Factory = new SOAP11Factory(this);
    private final SOAPFactory soap12Factory = new SOAP12Factory(this);

    public OMFactory getOMFactory() {
        return this.omFactory;
    }

    public SOAPFactory getSOAP11Factory() {
        return this.soap11Factory;
    }

    public SOAPFactory getSOAP12Factory() {
        return this.soap12Factory;
    }

    public SOAPMessage createSOAPMessage(OMXMLParserWrapper builder) {
        return new SOAPMessageImpl(builder, null);
    }
}

