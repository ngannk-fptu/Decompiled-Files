/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.util.ElementHelper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultCode;
import org.apache.axiom.soap.SOAPFaultSubCode;
import org.apache.axiom.soap.SOAPFaultValue;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultCodeImpl
extends SOAPElement
implements SOAPFaultCode {
    protected SOAPFaultCodeImpl(String localName, OMNamespace ns, SOAPFactory factory) {
        super(localName, ns, factory);
    }

    protected SOAPFaultCodeImpl(OMNamespace ns, SOAPFactory factory) {
        this(factory.getSOAPVersion().getFaultCodeQName().getLocalPart(), ns, factory);
    }

    public SOAPFaultCodeImpl(SOAPFault parent, String localName, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, localName, builder, factory);
    }

    public SOAPFaultCodeImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        this(parent, factory.getSOAPVersion().getFaultCodeQName().getLocalPart(), builder, factory);
    }

    public SOAPFaultCodeImpl(SOAPFault parent, String localName, boolean extractNamespaceFromParent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, localName, extractNamespaceFromParent, factory);
    }

    public SOAPFaultCodeImpl(SOAPFault parent, boolean extractNamespaceFromParent, SOAPFactory factory) throws SOAPProcessingException {
        this(parent, factory.getSOAPVersion().getFaultCodeQName().getLocalPart(), extractNamespaceFromParent, factory);
    }

    public void setValue(SOAPFaultValue value) throws SOAPProcessingException {
        ElementHelper.setNewElement(this, value, value);
    }

    public void setSubCode(SOAPFaultSubCode value) throws SOAPProcessingException {
        ElementHelper.setNewElement(this, this.getSubCode(), value);
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultCode((SOAPFault)targetParent);
    }
}

