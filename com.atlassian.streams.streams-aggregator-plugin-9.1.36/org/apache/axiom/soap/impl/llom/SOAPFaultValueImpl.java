/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultValueImpl
extends SOAPElement
implements SOAPFaultValue {
    protected SOAPFaultValueImpl(OMElement parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Value", true, factory);
    }

    protected SOAPFaultValueImpl(OMNamespace ns, SOAPFactory factory) {
        super("Value", ns, factory);
    }

    protected SOAPFaultValueImpl(OMElement parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, "Value", builder, factory);
    }

    protected SOAPFaultValueImpl(String localName, OMElement parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, localName, true, factory);
    }

    protected SOAPFaultValueImpl(OMNamespace ns, String localName, SOAPFactory factory) {
        super(localName, ns, factory);
    }

    protected SOAPFaultValueImpl(OMElement parent, String localName, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, localName, builder, factory);
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultValue((SOAPFaultCode)targetParent);
    }
}

