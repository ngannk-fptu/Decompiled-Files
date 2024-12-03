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
import org.apache.axiom.soap.SOAPFaultRole;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultRoleImpl
extends SOAPElement
implements SOAPFaultRole {
    protected SOAPFaultRoleImpl(OMNamespace ns, SOAPFactory factory) {
        super(factory.getSOAPVersion().getFaultRoleQName().getLocalPart(), ns, factory);
    }

    public SOAPFaultRoleImpl(SOAPFault parent, boolean extractNamespaceFromParent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory.getSOAPVersion().getFaultRoleQName().getLocalPart(), extractNamespaceFromParent, factory);
    }

    public SOAPFaultRoleImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, factory.getSOAPVersion().getFaultRoleQName().getLocalPart(), builder, factory);
    }

    public void setRoleValue(String uri) {
        this.setText(uri);
    }

    public String getRoleValue() {
        return this.getText();
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultRole((SOAPFault)targetParent);
    }
}

