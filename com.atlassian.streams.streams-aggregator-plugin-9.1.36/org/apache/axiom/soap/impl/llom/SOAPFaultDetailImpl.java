/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import java.util.Iterator;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPFault;
import org.apache.axiom.soap.SOAPFaultDetail;
import org.apache.axiom.soap.SOAPProcessingException;
import org.apache.axiom.soap.impl.llom.SOAPElement;

public abstract class SOAPFaultDetailImpl
extends SOAPElement
implements SOAPFaultDetail {
    protected SOAPFaultDetailImpl(OMNamespace ns, SOAPFactory factory) {
        super(factory.getSOAPVersion().getFaultDetailQName().getLocalPart(), ns, factory);
    }

    protected SOAPFaultDetailImpl(SOAPFault parent, boolean extractNamespaceFromParent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, factory.getSOAPVersion().getFaultDetailQName().getLocalPart(), extractNamespaceFromParent, factory);
    }

    protected SOAPFaultDetailImpl(SOAPFault parent, OMXMLParserWrapper builder, SOAPFactory factory) {
        super((OMContainer)parent, factory.getSOAPVersion().getFaultDetailQName().getLocalPart(), builder, factory);
    }

    public void addDetailEntry(OMElement detailElement) {
        this.addChild(detailElement);
    }

    public Iterator getAllDetailEntries() {
        return this.getChildren();
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        this.registerContentHandler(writer);
        super.internalSerialize(writer, cache);
    }

    protected OMElement createClone(OMCloneOptions options, OMContainer targetParent) {
        return ((SOAPFactory)this.factory).createSOAPFaultDetail((SOAPFault)targetParent);
    }
}

