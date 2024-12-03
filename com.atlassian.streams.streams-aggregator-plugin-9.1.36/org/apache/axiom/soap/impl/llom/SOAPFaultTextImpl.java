/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFaultReason;
import org.apache.axiom.soap.SOAPFaultText;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultTextImpl
extends SOAPElement
implements SOAPFaultText {
    protected OMAttribute langAttr;
    protected OMNamespace langNamespace = null;

    protected SOAPFaultTextImpl(OMNamespace ns, SOAPFactory factory) {
        super("Text", ns, factory);
        this.langNamespace = factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");
    }

    protected SOAPFaultTextImpl(SOAPFaultReason parent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, "Text", true, factory);
        this.langNamespace = factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");
    }

    protected SOAPFaultTextImpl(SOAPFaultReason parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, "Text", builder, factory);
        this.langNamespace = factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");
    }

    public void setLang(String lang) {
        this.langAttr = new OMAttributeImpl("lang", this.langNamespace, lang, this.factory);
        this.addAttribute(this.langAttr);
    }

    public String getLang() {
        if (this.langAttr == null) {
            this.langAttr = this.getAttribute(new QName(this.langNamespace.getNamespaceURI(), "lang", "xml"));
        }
        return this.langAttr == null ? null : this.langAttr.getAttributeValue();
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultText((SOAPFaultReason)targetParent);
    }
}

