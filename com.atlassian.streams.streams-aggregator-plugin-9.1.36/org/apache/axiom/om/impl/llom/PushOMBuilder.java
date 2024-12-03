/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 */
package org.apache.axiom.om.impl.llom;

import java.io.IOException;
import java.util.Iterator;
import javax.activation.DataHandler;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.ext.stax.datahandler.DataHandlerWriter;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.util.stax.AbstractXMLStreamWriter;

public class PushOMBuilder
extends AbstractXMLStreamWriter
implements DataHandlerWriter {
    private final OMSourcedElementImpl root;
    private final OMFactoryEx factory;
    private OMElement parent;

    public PushOMBuilder(OMSourcedElementImpl root) throws XMLStreamException {
        this.root = root;
        this.factory = (OMFactoryEx)root.getOMFactory();
        OMContainer parent = root.getParent();
        if (parent instanceof OMElement) {
            Iterator it = ((OMElement)parent).getNamespacesInScope();
            while (it.hasNext()) {
                OMNamespace ns = (OMNamespace)it.next();
                this.setPrefix(ns.getPrefix(), ns.getNamespaceURI());
            }
        }
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DataHandlerWriter.PROPERTY.equals(name)) {
            return this;
        }
        throw new IllegalArgumentException("Unsupported property " + name);
    }

    protected void doWriteStartDocument() {
    }

    protected void doWriteStartDocument(String encoding, String version) {
    }

    protected void doWriteStartDocument(String version) {
    }

    protected void doWriteEndDocument() {
    }

    protected void doWriteDTD(String dtd) throws XMLStreamException {
        throw new XMLStreamException("A DTD must not appear in element content");
    }

    private OMNamespace getOMNamespace(String prefix, String namespaceURI, boolean isDecl) {
        OMNamespace ns;
        if (prefix == null) {
            prefix = "";
        }
        if (namespaceURI == null) {
            namespaceURI = "";
        }
        if (!isDecl && namespaceURI.length() == 0) {
            return null;
        }
        if (this.parent != null && (ns = this.parent.findNamespaceURI(prefix)) != null && ns.getNamespaceURI().equals(namespaceURI)) {
            return ns;
        }
        return this.factory.createOMNamespace(namespaceURI, prefix);
    }

    protected void doWriteStartElement(String prefix, String localName, String namespaceURI) {
        OMNamespace ns = this.getOMNamespace(prefix, namespaceURI, false);
        if (this.parent == null) {
            this.root.validateName(prefix, localName, namespaceURI);
            this.parent = this.root;
        } else {
            this.parent = this.factory.createOMElement(localName, this.parent, null);
        }
        if (ns != null) {
            this.parent.setNamespaceWithNoFindInCurrentScope(ns);
        }
    }

    protected void doWriteStartElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeStartElement(String)");
    }

    protected void doWriteEndElement() {
        if (this.parent == this.root) {
            this.parent = null;
        } else {
            ((OMContainerEx)((Object)this.parent)).setComplete(true);
            this.parent = (OMElement)this.parent.getParent();
        }
    }

    protected void doWriteEmptyElement(String prefix, String localName, String namespaceURI) {
        this.doWriteStartElement(prefix, localName, namespaceURI);
        this.doWriteEndElement();
    }

    protected void doWriteEmptyElement(String localName) throws XMLStreamException {
        throw new UnsupportedOperationException("OMDataSource#serialize(XMLStreamWriter) MUST NOT use XMLStreamWriter#writeEmptyElement(String)");
    }

    protected void doWriteAttribute(String prefix, String namespaceURI, String localName, String value) {
        OMAttribute attr = this.factory.createOMAttribute(localName, this.getOMNamespace(prefix, namespaceURI, false), value);
        ((OMElementImpl)this.parent).appendAttribute(attr);
    }

    protected void doWriteAttribute(String localName, String value) throws XMLStreamException {
        this.doWriteAttribute(null, null, localName, value);
    }

    protected void doWriteNamespace(String prefix, String namespaceURI) {
        ((OMElementImpl)this.parent).addNamespaceDeclaration(this.getOMNamespace(prefix, namespaceURI, true));
    }

    protected void doWriteDefaultNamespace(String namespaceURI) {
        this.doWriteNamespace(null, namespaceURI);
    }

    protected void doWriteCharacters(char[] text, int start, int len) {
        this.doWriteCharacters(new String(text, start, len));
    }

    protected void doWriteCharacters(String text) {
        this.factory.createOMText((OMContainer)this.parent, text, 4, true);
    }

    protected void doWriteCData(String data) {
        this.factory.createOMText((OMContainer)this.parent, data, 12, true);
    }

    protected void doWriteComment(String data) {
        this.factory.createOMComment(this.parent, data, true);
    }

    protected void doWriteEntityRef(String name) throws XMLStreamException {
        this.factory.createOMEntityReference(this.parent, name, null, true);
    }

    protected void doWriteProcessingInstruction(String target, String data) {
        this.factory.createOMProcessingInstruction(this.parent, target, data, true);
    }

    protected void doWriteProcessingInstruction(String target) {
        this.doWriteProcessingInstruction(target, "");
    }

    public void flush() throws XMLStreamException {
    }

    public void close() throws XMLStreamException {
    }

    public void writeDataHandler(DataHandler dataHandler, String contentID, boolean optimize) throws IOException, XMLStreamException {
        OMText child = this.factory.createOMText(dataHandler, optimize);
        if (contentID != null) {
            child.setContentID(contentID);
        }
        this.parent.addChild(child);
    }

    public void writeDataHandler(DataHandlerProvider dataHandlerProvider, String contentID, boolean optimize) throws IOException, XMLStreamException {
        this.parent.addChild(this.factory.createOMText(contentID, dataHandlerProvider, optimize));
    }
}

