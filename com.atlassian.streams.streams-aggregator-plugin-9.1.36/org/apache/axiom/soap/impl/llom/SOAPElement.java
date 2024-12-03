/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.soap.impl.llom;

import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.serialize.StreamWriterToContentHandlerConverter;
import org.apache.axiom.soap.SOAPFactory;
import org.apache.axiom.soap.SOAPProcessingException;

public abstract class SOAPElement
extends OMElementImpl {
    protected SOAPElement(OMElement parent, String localName, boolean extractNamespaceFromParent, SOAPFactory factory) throws SOAPProcessingException {
        super(parent, localName, null, null, factory, true);
        if (parent == null) {
            throw new SOAPProcessingException(" Can not create " + localName + " element without a parent !!");
        }
        this.checkParent(parent);
        if (extractNamespaceFromParent) {
            this.ns = parent.getNamespace();
        }
    }

    protected SOAPElement(OMContainer parent, String localName, OMXMLParserWrapper builder, SOAPFactory factory) {
        super(parent, localName, null, builder, factory, false);
    }

    protected SOAPElement(String localName, OMNamespace ns, SOAPFactory factory) {
        super(null, localName, ns, null, factory, true);
    }

    protected abstract void checkParent(OMElement var1) throws SOAPProcessingException;

    public void setParent(OMContainer element) {
        super.setParent(element);
        if (element instanceof OMElement) {
            this.checkParent((OMElement)element);
        }
    }

    protected short registerContentHandler(XMLStreamWriter writer) {
        short builderType = 1;
        if (this.builder != null) {
            builderType = this.builder.getBuilderType();
        }
        if (builderType == 0 && this.builder.getRegisteredContentHandler() == null) {
            this.builder.registerExternalContentHandler(new StreamWriterToContentHandlerConverter(writer));
        }
        return builderType;
    }
}

