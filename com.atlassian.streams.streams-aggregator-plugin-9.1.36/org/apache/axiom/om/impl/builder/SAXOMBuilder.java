/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.builder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.transform.sax.SAXSource;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.OMContainerEx;
import org.apache.axiom.om.impl.OMElementEx;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.BuilderUtil;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXOMBuilder
extends DefaultHandler
implements LexicalHandler,
DeclHandler,
OMXMLParserWrapper {
    private final SAXSource source;
    private final boolean expandEntityReferences;
    private OMDocument document;
    private String dtdName;
    private String dtdPublicId;
    private String dtdSystemId;
    private StringBuilder internalSubset;
    private Map entities;
    private boolean inExternalSubset;
    OMElement root = null;
    OMNode lastNode = null;
    OMElement nextElem = null;
    private final OMFactoryEx factory;
    List prefixMappings = new ArrayList();
    int textNodeType = 4;
    private boolean inEntityReference;
    private int entityReferenceDepth;

    public SAXOMBuilder(OMFactory factory, SAXSource source, boolean expandEntityReferences) {
        this.factory = (OMFactoryEx)factory;
        this.source = source;
        this.expandEntityReferences = expandEntityReferences;
    }

    public SAXOMBuilder(OMFactory factory) {
        this(factory, null, true);
    }

    public SAXOMBuilder() {
        this(OMAbstractFactory.getOMFactory());
    }

    private OMContainer getContainer() {
        if (this.lastNode != null) {
            return this.lastNode.isComplete() ? this.lastNode.getParent() : (OMContainer)((Object)this.lastNode);
        }
        if (this.document != null) {
            return this.document;
        }
        throw new OMException("Unexpected event. There is no container to add the node to.");
    }

    private void addNode(OMNode node) {
        if (this.root == null && node instanceof OMElement) {
            this.root = (OMElement)node;
        }
        this.lastNode = node;
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
        this.document = this.factory.createOMDocument(this);
    }

    public void endDocument() throws SAXException {
        ((OMContainerEx)((Object)this.document)).setComplete(true);
    }

    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.dtdName = name;
        this.dtdPublicId = publicId;
        this.dtdSystemId = systemId;
        this.internalSubset = new StringBuilder();
    }

    public void elementDecl(String name, String model) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ELEMENT ");
            this.internalSubset.append(name);
            this.internalSubset.append(' ');
            this.internalSubset.append(model);
            this.internalSubset.append(">\n");
        }
    }

    public void attributeDecl(String eName, String aName, String type, String mode, String value) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ATTLIST ");
            this.internalSubset.append(eName);
            this.internalSubset.append(' ');
            this.internalSubset.append(aName);
            this.internalSubset.append(' ');
            this.internalSubset.append(type);
            if (value != null) {
                this.internalSubset.append(' ');
                this.internalSubset.append(value);
            }
            this.internalSubset.append(">\n");
        }
    }

    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ENTITY ");
            this.internalSubset.append(name);
            if (publicId != null) {
                this.internalSubset.append(" PUBLIC \"");
                this.internalSubset.append(publicId);
            } else {
                this.internalSubset.append(" SYSTEM \"");
                this.internalSubset.append(systemId);
            }
            this.internalSubset.append("\">\n");
        }
    }

    public void internalEntityDecl(String name, String value) throws SAXException {
        if (this.entities == null) {
            this.entities = new HashMap();
        }
        this.entities.put(name, value);
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ENTITY ");
            this.internalSubset.append(name);
            this.internalSubset.append(" \"");
            this.internalSubset.append(value);
            this.internalSubset.append("\">\n");
        }
    }

    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!NOTATION ");
            this.internalSubset.append(name);
            if (publicId != null) {
                this.internalSubset.append(" PUBLIC \"");
                this.internalSubset.append(publicId);
            } else {
                this.internalSubset.append(" SYSTEM \"");
                this.internalSubset.append(systemId);
            }
            this.internalSubset.append("\">\n");
        }
    }

    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        if (!this.inExternalSubset) {
            this.internalSubset.append("<!ENTITY ");
            this.internalSubset.append(name);
            if (publicId != null) {
                this.internalSubset.append(" PUBLIC \"");
                this.internalSubset.append(publicId);
            } else {
                this.internalSubset.append(" SYSTEM \"");
                this.internalSubset.append(systemId);
            }
            this.internalSubset.append("\" NDATA ");
            this.internalSubset.append(notationName);
            this.internalSubset.append(">\n");
        }
    }

    public void endDTD() throws SAXException {
        this.addNode(this.factory.createOMDocType(this.getContainer(), this.dtdName, this.dtdPublicId, this.dtdSystemId, this.internalSubset.length() == 0 ? null : this.internalSubset.toString(), true));
        this.internalSubset = null;
    }

    protected OMElement createNextElement(String localName) throws OMException {
        OMElement element = this.factory.createOMElement(localName, this.getContainer(), this);
        this.addNode(element);
        return element;
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (!this.inEntityReference) {
            if (this.nextElem == null) {
                this.nextElem = this.createNextElement("DUMMY");
            }
            ((OMElementEx)this.nextElem).addNamespaceDeclaration(uri, prefix);
        }
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (this.inEntityReference) return;
        if (localName == null || localName.trim().equals("")) {
            localName = qName.substring(qName.indexOf(58) + 1);
        }
        if (this.nextElem == null) {
            this.nextElem = this.createNextElement(localName);
        } else {
            this.nextElem.setLocalName(localName);
        }
        int idx = qName.indexOf(58);
        String prefix = idx == -1 ? "" : qName.substring(0, idx);
        BuilderUtil.setNamespace(this.nextElem, namespaceURI, prefix, false);
        int j = atts.getLength();
        for (int i = 0; i < j; ++i) {
            OMNamespace ns;
            if (atts.getQName(i).startsWith("xmlns")) continue;
            String attrNamespaceURI = atts.getURI(i);
            if (attrNamespaceURI.length() > 0) {
                ns = this.nextElem.findNamespace(atts.getURI(i), null);
                if (ns == null) {
                    if (!attrNamespaceURI.equals("http://www.w3.org/XML/1998/namespace")) throw new SAXException("Unbound namespace " + attrNamespaceURI);
                    ns = this.factory.createOMNamespace("http://www.w3.org/XML/1998/namespace", "xml");
                }
            } else {
                ns = null;
            }
            OMAttribute attr = this.nextElem.addAttribute(atts.getLocalName(i), atts.getValue(i), ns);
            attr.setAttributeType(atts.getType(i));
        }
        this.lastNode = this.nextElem;
        this.nextElem = null;
    }

    public void endElement(String uri, String localName, String qName) throws SAXException {
        if (!this.inEntityReference) {
            if (this.lastNode.isComplete()) {
                OMContainer parent = this.lastNode.getParent();
                ((OMNodeEx)((Object)parent)).setComplete(true);
                this.lastNode = (OMNode)((Object)parent);
            } else {
                OMElement e = (OMElement)this.lastNode;
                ((OMNodeEx)((Object)e)).setComplete(true);
            }
        }
    }

    public void startCDATA() throws SAXException {
        if (!this.inEntityReference) {
            this.textNodeType = 12;
        }
    }

    public void endCDATA() throws SAXException {
        if (!this.inEntityReference) {
            this.textNodeType = 4;
        }
    }

    public void characterData(char[] ch, int start, int length, int nodeType) throws SAXException {
        if (!this.inEntityReference) {
            this.addNode(this.factory.createOMText(this.getContainer(), new String(ch, start, length), nodeType, true));
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (!this.inEntityReference) {
            this.characterData(ch, start, length, this.textNodeType);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (!this.inEntityReference) {
            this.characterData(ch, start, length, 6);
        }
    }

    public void processingInstruction(String target, String data) throws SAXException {
        if (!this.inEntityReference) {
            this.addNode(this.factory.createOMProcessingInstruction(this.getContainer(), target, data, true));
        }
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (!this.inEntityReference) {
            if (this.lastNode == null) {
                return;
            }
            this.addNode(this.factory.createOMComment(this.getContainer(), new String(ch, start, length), true));
        }
    }

    public void skippedEntity(String name) throws SAXException {
    }

    public void startEntity(String name) throws SAXException {
        if (this.inEntityReference) {
            ++this.entityReferenceDepth;
        } else if (name.equals("[dtd]")) {
            this.inExternalSubset = true;
        } else if (!this.expandEntityReferences) {
            this.addNode(this.factory.createOMEntityReference(this.getContainer(), name, this.entities == null ? null : (String)this.entities.get(name), true));
            this.inEntityReference = true;
            this.entityReferenceDepth = 1;
        }
    }

    public void endEntity(String name) throws SAXException {
        if (this.inEntityReference) {
            --this.entityReferenceDepth;
            if (this.entityReferenceDepth == 0) {
                this.inEntityReference = false;
            }
        } else if (name.equals("[dtd]")) {
            this.inExternalSubset = false;
        }
    }

    public OMDocument getDocument() {
        if (this.document == null && this.source != null) {
            XMLReader reader = this.source.getXMLReader();
            reader.setContentHandler(this);
            reader.setDTDHandler(this);
            try {
                reader.setProperty("http://xml.org/sax/properties/lexical-handler", this);
            }
            catch (SAXException ex) {
                // empty catch block
            }
            try {
                reader.setProperty("http://xml.org/sax/properties/declaration-handler", this);
            }
            catch (SAXException ex) {
                // empty catch block
            }
            try {
                reader.parse(this.source.getInputSource());
            }
            catch (IOException ex) {
                throw new OMException(ex);
            }
            catch (SAXException ex) {
                throw new OMException(ex);
            }
        }
        if (this.document != null && this.document.isComplete()) {
            return this.document;
        }
        throw new OMException("Tree not complete");
    }

    public OMElement getRootElement() {
        if (this.root != null && this.root.isComplete()) {
            return this.root;
        }
        throw new OMException("Tree not complete");
    }

    public int next() throws OMException {
        throw new UnsupportedOperationException();
    }

    public void discard(OMElement el) throws OMException {
        throw new UnsupportedOperationException();
    }

    public void setCache(boolean b) throws OMException {
        throw new UnsupportedOperationException();
    }

    public boolean isCache() {
        throw new UnsupportedOperationException();
    }

    public Object getParser() {
        throw new UnsupportedOperationException();
    }

    public boolean isCompleted() {
        return this.document != null && this.document.isComplete();
    }

    public OMElement getDocumentElement() {
        return this.getDocument().getOMDocumentElement();
    }

    public OMElement getDocumentElement(boolean discardDocument) {
        OMElement documentElement = this.getDocument().getOMDocumentElement();
        documentElement.detach();
        return documentElement;
    }

    public short getBuilderType() {
        throw new UnsupportedOperationException();
    }

    public void registerExternalContentHandler(Object obj) {
        throw new UnsupportedOperationException();
    }

    public Object getRegisteredContentHandler() {
        throw new UnsupportedOperationException();
    }

    public String getCharacterEncoding() {
        throw new UnsupportedOperationException();
    }

    public void close() {
    }
}

