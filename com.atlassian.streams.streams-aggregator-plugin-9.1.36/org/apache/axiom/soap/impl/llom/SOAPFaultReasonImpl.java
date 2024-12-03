/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;
import org.apache.axiom.soap.impl.llom.SOAPFaultTextImpl;

public abstract class SOAPFaultReasonImpl
extends SOAPElement
implements SOAPFaultReason {
    protected SOAPFaultReasonImpl(OMNamespace ns, SOAPFactory factory) {
        super(factory.getSOAPVersion().getFaultReasonQName().getLocalPart(), ns, factory);
    }

    public SOAPFaultReasonImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, factory.getSOAPVersion().getFaultReasonQName().getLocalPart(), builder, factory);
    }

    public SOAPFaultReasonImpl(OMElement parent, boolean extractNamespaceFromParent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory.getSOAPVersion().getFaultReasonQName().getLocalPart(), extractNamespaceFromParent, factory);
    }

    public List getAllSoapTexts() {
        ArrayList<SOAPFaultTextImpl> faultTexts = new ArrayList<SOAPFaultTextImpl>(1);
        Iterator childrenIter = this.getChildren();
        while (childrenIter.hasNext()) {
            OMNode node = (OMNode)childrenIter.next();
            if (node.getType() != 1 || !(node instanceof SOAPFaultTextImpl)) continue;
            faultTexts.add((SOAPFaultTextImpl)node);
        }
        return faultTexts;
    }

    public SOAPFaultText getSOAPFaultText(String language) {
        Iterator childrenIter = this.getChildren();
        while (childrenIter.hasNext()) {
            OMNode node = (OMNode)childrenIter.next();
            if (node.getType() != 1 || !(node instanceof SOAPFaultTextImpl) || language != null && !language.equals(((SOAPFaultTextImpl)node).getLang())) continue;
            return (SOAPFaultText)node;
        }
        return null;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultReason((SOAPFault)targetParent);
    }
}

