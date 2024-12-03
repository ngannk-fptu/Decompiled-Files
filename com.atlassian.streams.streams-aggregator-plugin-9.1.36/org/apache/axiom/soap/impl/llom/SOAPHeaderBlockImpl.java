/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import javax.xml.namespace.QName;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDataSourceExt;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.soap.SOAPCloneOptions;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPHeader;
import org.apache.axiom.soap.SOAPHeaderBlock;
import org.apache.axiom.soap.SOAPProcessingException;

public abstract class SOAPHeaderBlockImpl
extends OMSourcedElementImpl
implements SOAPHeaderBlock {
    private boolean processed = false;

    public SOAPHeaderBlockImpl(OMContainer parent, String localName, OMNamespace ns, OMXMLParserWrapper builder, OMFactory factory, boolean generateNSDecl) {
        super(parent, localName, ns, builder, factory, generateNSDecl);
    }

    public SOAPHeaderBlockImpl(SOAPFactory factory, OMDataSource source) {
        super(factory, source);
    }

    public SOAPHeaderBlockImpl(String localName, OMNamespace ns, SOAPFactory factory, OMDataSource ds) {
        super(localName, ns, factory, ds);
    }

    protected abstract void checkParent(OMElement var1) throws SOAPProcessingException;

    public void setParent(OMContainer element) {
        super.setParent(element);
        if (element instanceof OMElement) {
            this.checkParent((OMElement)element);
        }
    }

    protected void setAttribute(String attributeName, String attrValue, String soapEnvelopeNamespaceURI) {
        OMAttribute omAttribute = this.getAttribute(new QName(soapEnvelopeNamespaceURI, attributeName));
        if (omAttribute != null) {
            omAttribute.setAttributeValue(attrValue);
        } else {
            OMAttributeImpl attribute = new OMAttributeImpl(attributeName, new OMNamespaceImpl(soapEnvelopeNamespaceURI, "soapenv"), attrValue, this.factory);
            this.addAttribute(attribute);
        }
    }

    protected String getAttribute(String attrName, String soapEnvelopeNamespaceURI) {
        OMAttribute omAttribute = this.getAttribute(new QName(soapEnvelopeNamespaceURI, attrName));
        return omAttribute != null ? omAttribute.getAttributeValue() : null;
    }

    public boolean isProcessed() {
        return this.processed;
    }

    public void setProcessed() {
        this.processed = true;
    }

    protected String getOMDataSourceProperty(String key) {
        if (this.hasOMDataSourceProperty(key)) {
            return (String)((OMDataSourceExt)this.getDataSource()).getProperty(key);
        }
        return null;
    }

    protected boolean hasOMDataSourceProperty(String key) {
        OMDataSource ds;
        if (!this.isExpanded() && (ds = this.getDataSource()) instanceof OMDataSourceExt) {
            return ((OMDataSourceExt)ds).hasProperty(key);
        }
        return false;
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        SOAPHeaderBlock clone = ((SOAPFactory)this.factory).createSOAPHeaderBlock(this.getLocalName(), this.getNamespace(), (SOAPHeader)targetParent);
        this.copyData(options, clone);
        return clone;
    }

    protected OMSourcedElement createClone(OMCloneOptions options, OMDataSource ds) {
        SOAPHeaderBlock clone = ((SOAPFactory)this.factory).createSOAPHeaderBlock(ds);
        this.copyData(options, clone);
        return clone;
    }

    private void copyData(OMCloneOptions options, SOAPHeaderBlock targetSHB) {
        Boolean processedFlag;
        Boolean bl = processedFlag = options instanceof SOAPCloneOptions ? ((SOAPCloneOptions)options).getProcessedFlag() : null;
        if (processedFlag == null && this.isProcessed() || processedFlag != null && processedFlag.booleanValue()) {
            targetSHB.setProcessed();
        }
    }
}

