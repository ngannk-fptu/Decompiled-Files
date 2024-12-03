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
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultNode;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultNodeImpl
extends SOAPElement
implements SOAPFaultNode {
    protected SOAPFaultNodeImpl(OMNamespace ns, SOAPFactory factory) {
        super("Node", ns, factory);
    }

    public SOAPFaultNodeImpl(SOAPFault parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Node", true, factory);
    }

    public SOAPFaultNodeImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, "Node", builder, factory);
    }

    public void setFaultNodeValue(String uri) {
        this.setText(uri);
    }

    public String getFaultNodeValue() {
        return this.getText();
    }

    public void setNodeValue(String uri) {
        this.setFaultNodeValue(uri);
    }

    public String getNodeValue() {
        return this.getFaultNodeValue();
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultNode((SOAPFault)targetParent);
    }
}

