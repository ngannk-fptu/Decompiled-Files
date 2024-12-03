/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import java.util.Iterator;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.OMXMLStreamReaderConfiguration;
import org.apache.axiom.om.impl.MTOMXMLStreamWriter;
import org.apache.axiom.om.impl.jaxp.OMSource;
import org.apache.axiom.om.impl.llom.IContainer;
import org.apache.axiom.om.impl.llom.OMChildrenLocalNameIterator;
import org.apache.axiom.om.impl.llom.OMChildrenNamespaceIterator;
import org.apache.axiom.om.impl.llom.OMChildrenQNameIterator;
import org.apache.axiom.om.impl.llom.OMContainerHelper;
import org.apache.axiom.om.impl.llom.OMDescendantsIterator;
import org.apache.axiom.om.impl.llom.OMDocumentImplUtil;
import org.apache.axiom.om.impl.llom.OMNodeImpl;
import org.apache.axiom.om.impl.llom.OMSerializableImpl;
import org.apache.axiom.om.impl.traverse.OMChildrenIterator;

public class OMDocumentImpl
extends OMSerializableImpl
implements OMDocument,
IContainer {
    protected OMXMLParserWrapper builder;
    protected int state;
    protected OMNode firstChild;
    protected OMNode lastChild;
    protected String charSetEncoding = "UTF-8";
    protected String xmlVersion = "1.0";
    protected String xmlEncoding;
    protected String isStandalone;

    public OMDocumentImpl(OMFactory factory) {
        super(factory);
        this.state = 1;
    }

    public OMDocumentImpl(OMXMLParserWrapper parserWrapper, OMFactory factory) {
        super(factory);
        this.builder = parserWrapper;
    }

    public OMXMLParserWrapper getBuilder() {
        return this.builder;
    }

    public OMElement getOMDocumentElement() {
        for (OMNode child = this.getFirstOMChild(); child != null; child = child.getNextOMSibling()) {
            if (!(child instanceof OMElement)) continue;
            return (OMElement)child;
        }
        return null;
    }

    public void setOMDocumentElement(OMElement documentElement) {
        if (documentElement == null) {
            throw new IllegalArgumentException("documentElement must not be null");
        }
        OMElement existingDocumentElement = this.getOMDocumentElement();
        if (existingDocumentElement == null) {
            this.addChild(documentElement);
        } else {
            OMNode nextSibling = existingDocumentElement.getNextOMSibling();
            existingDocumentElement.detach();
            if (nextSibling == null) {
                this.addChild(documentElement);
            } else {
                nextSibling.insertSiblingBefore(documentElement);
            }
        }
    }

    public int getState() {
        return this.state;
    }

    public boolean isComplete() {
        return this.state == 1;
    }

    public void setComplete(boolean complete) {
        this.state = complete ? 1 : 0;
    }

    public void discarded() {
        this.state = 2;
    }

    public void addChild(OMNode child) {
        this.addChild(child, false);
    }

    public void addChild(OMNode omNode, boolean fromBuilder) {
        if (!fromBuilder && omNode instanceof OMElement && this.getOMDocumentElement() != null) {
            throw new OMException("Document element already exists");
        }
        OMContainerHelper.addChild(this, omNode, fromBuilder);
    }

    public Iterator getChildren() {
        return new OMChildrenIterator(this.getFirstOMChild());
    }

    public Iterator getDescendants(boolean includeSelf) {
        return new OMDescendantsIterator(this, includeSelf);
    }

    public Iterator getChildrenWithName(QName elementQName) {
        return new OMChildrenQNameIterator(this.getFirstOMChild(), elementQName);
    }

    public Iterator getChildrenWithLocalName(String localName) {
        return new OMChildrenLocalNameIterator(this.getFirstOMChild(), localName);
    }

    public Iterator getChildrenWithNamespaceURI(String uri) {
        return new OMChildrenNamespaceIterator(this.getFirstOMChild(), uri);
    }

    public OMNode getFirstOMChild() {
        return OMContainerHelper.getFirstOMChild(this);
    }

    public OMNode getFirstOMChildIfAvailable() {
        return this.firstChild;
    }

    public OMNode getLastKnownOMChild() {
        return this.lastChild;
    }

    public OMElement getFirstChildWithName(QName elementQName) throws OMException {
        OMChildrenQNameIterator omChildrenQNameIterator = new OMChildrenQNameIterator(this.getFirstOMChild(), elementQName);
        OMNode omNode = null;
        if (omChildrenQNameIterator.hasNext()) {
            omNode = (OMNode)omChildrenQNameIterator.next();
        }
        return omNode != null && 1 == omNode.getType() ? (OMElement)omNode : null;
    }

    public void setFirstChild(OMNode firstChild) {
        this.firstChild = firstChild;
    }

    public void setLastChild(OMNode omNode) {
        this.lastChild = omNode;
    }

    public String getCharsetEncoding() {
        return this.charSetEncoding;
    }

    public void setCharsetEncoding(String charEncoding) {
        this.charSetEncoding = charEncoding;
    }

    public String isStandalone() {
        return this.isStandalone;
    }

    public void setStandalone(String isStandalone) {
        this.isStandalone = isStandalone;
    }

    public String getXMLVersion() {
        return this.xmlVersion;
    }

    public void setXMLVersion(String xmlVersion) {
        this.xmlVersion = xmlVersion;
    }

    public String getXMLEncoding() {
        return this.xmlEncoding;
    }

    public void setXMLEncoding(String encoding) {
        this.xmlEncoding = encoding;
    }

    public void internalSerialize(XMLStreamWriter writer, boolean cache) throws XMLStreamException {
        this.internalSerialize(writer, cache, !((MTOMXMLStreamWriter)writer).isIgnoreXMLDeclaration());
    }

    protected void internalSerialize(XMLStreamWriter writer, boolean cache, boolean includeXMLDeclaration) throws XMLStreamException {
        OMDocumentImplUtil.internalSerialize(this, writer, cache, includeXMLDeclaration);
    }

    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        this.internalSerialize(writer, false);
    }

    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        this.internalSerialize(writer, true);
    }

    public XMLStreamReader getXMLStreamReader() {
        return this.getXMLStreamReader(true);
    }

    public XMLStreamReader getXMLStreamReaderWithoutCaching() {
        return this.getXMLStreamReader(false);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache) {
        return OMContainerHelper.getXMLStreamReader(this, cache);
    }

    public XMLStreamReader getXMLStreamReader(boolean cache, OMXMLStreamReaderConfiguration configuration) {
        return OMContainerHelper.getXMLStreamReader(this, cache, configuration);
    }

    void notifyChildComplete() {
        if (this.state == 0 && this.builder == null) {
            Iterator iterator = this.getChildren();
            while (iterator.hasNext()) {
                OMNode node = (OMNode)iterator.next();
                if (node.isComplete()) continue;
                return;
            }
            this.setComplete(true);
        }
    }

    public SAXSource getSAXSource(boolean cache) {
        return new OMSource(this);
    }

    public void build() {
        OMContainerHelper.build(this);
    }

    public void removeChildren() {
        OMContainerHelper.removeChildren(this);
    }

    public OMInformationItem clone(OMCloneOptions options) {
        OMDocument targetDocument = options.isPreserveModel() ? this.createClone(options) : this.getOMFactory().createOMDocument();
        targetDocument.setXMLVersion(this.xmlVersion);
        targetDocument.setXMLEncoding(this.xmlEncoding);
        targetDocument.setCharsetEncoding(this.charSetEncoding);
        targetDocument.setStandalone(this.isStandalone);
        Iterator it = this.getChildren();
        while (it.hasNext()) {
            ((OMNodeImpl)it.next()).clone(options, targetDocument);
        }
        return targetDocument;
    }

    protected OMDocument createClone(OMCloneOptions options) {
        return this.factory.createOMDocument();
    }
}

