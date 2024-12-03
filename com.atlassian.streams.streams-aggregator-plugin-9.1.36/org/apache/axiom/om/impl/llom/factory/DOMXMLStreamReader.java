/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import java.util.NoSuchElementException;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamException;
import org.apache.axiom.ext.stax.DTDReader;
import org.apache.axiom.om.impl.llom.factory.DOMNamespaceContext;
import org.apache.axiom.om.impl.llom.factory.DOMUtils;
import org.apache.axiom.util.stax.AbstractXMLStreamReader;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

class DOMXMLStreamReader
extends AbstractXMLStreamReader
implements DTDReader {
    private final Node root;
    private final boolean expandEntityReferences;
    private Node node;
    private int event;
    private boolean attributesLoaded;
    private int attributeCount;
    private Attr[] attributes = new Attr[8];
    private int namespaceCount;
    private Attr[] namespaces = new Attr[8];
    private NamespaceContext nsContext;

    DOMXMLStreamReader(Node node, boolean expandEntityReferences) {
        this.root = node;
        this.node = node.getNodeType() == 9 ? node : null;
        this.expandEntityReferences = expandEntityReferences;
        this.event = 7;
    }

    Node currentNode() {
        return this.node;
    }

    public Object getProperty(String name) throws IllegalArgumentException {
        if (DTDReader.PROPERTY.equals(name)) {
            return this;
        }
        return null;
    }

    public boolean hasNext() throws XMLStreamException {
        return this.event != 8;
    }

    public int next() throws XMLStreamException {
        if (this.event == 8) {
            throw new NoSuchElementException("End of the document reached");
        }
        boolean forceTraverse = false;
        block10: while (true) {
            boolean visited;
            if (this.node == null) {
                this.node = this.root;
                visited = false;
            } else if (this.event == 7 || this.event == 1 || forceTraverse) {
                Node firstChild = this.node.getFirstChild();
                if (firstChild == null) {
                    visited = true;
                } else {
                    this.node = firstChild;
                    visited = false;
                }
                forceTraverse = false;
            } else if (this.node == this.root) {
                this.node = null;
                visited = true;
            } else {
                Node nextSibling = this.node.getNextSibling();
                if (nextSibling == null) {
                    this.node = this.node.getParentNode();
                    visited = true;
                } else {
                    this.node = nextSibling;
                    visited = false;
                }
            }
            switch (this.node == null ? 9 : (int)this.node.getNodeType()) {
                case 9: {
                    this.event = 8;
                    break block10;
                }
                case 10: {
                    this.event = 11;
                    break block10;
                }
                case 1: {
                    this.event = visited ? 2 : 1;
                    this.attributesLoaded = false;
                    break block10;
                }
                case 3: {
                    this.event = ((Text)this.node).isElementContentWhitespace() ? 6 : 4;
                    break block10;
                }
                case 4: {
                    this.event = 12;
                    break block10;
                }
                case 8: {
                    this.event = 5;
                    break block10;
                }
                case 7: {
                    this.event = 3;
                    break block10;
                }
                case 5: {
                    if (this.expandEntityReferences) {
                        if (visited) continue block10;
                        forceTraverse = true;
                        continue block10;
                    }
                    this.event = 9;
                    break block10;
                }
                default: {
                    throw new IllegalStateException("Unexpected node type " + this.node.getNodeType());
                }
            }
            break;
        }
        return this.event;
    }

    public int getEventType() {
        return this.event;
    }

    public String getEncoding() {
        if (this.event == 7) {
            return this.node != null ? ((Document)this.node).getInputEncoding() : null;
        }
        throw new IllegalStateException();
    }

    public String getVersion() {
        return this.node != null ? ((Document)this.node).getXmlVersion() : "1.0";
    }

    public String getCharacterEncodingScheme() {
        if (this.event == 7) {
            return this.node != null ? ((Document)this.node).getXmlEncoding() : null;
        }
        throw new IllegalStateException();
    }

    public boolean isStandalone() {
        return this.node != null ? ((Document)this.node).getXmlStandalone() : true;
    }

    public boolean standaloneSet() {
        return true;
    }

    public String getRootName() {
        return ((DocumentType)this.node).getName();
    }

    public String getPublicId() {
        return ((DocumentType)this.node).getPublicId();
    }

    public String getSystemId() {
        return ((DocumentType)this.node).getSystemId();
    }

    public String getLocalName() {
        switch (this.event) {
            case 1: 
            case 2: {
                return this.node.getLocalName();
            }
            case 9: {
                return this.node.getNodeName();
            }
        }
        throw new IllegalStateException();
    }

    public String getNamespaceURI() {
        switch (this.event) {
            case 1: 
            case 2: {
                return this.node.getNamespaceURI();
            }
        }
        throw new IllegalStateException();
    }

    public String getPrefix() {
        switch (this.event) {
            case 1: 
            case 2: {
                return this.node.getPrefix();
            }
        }
        throw new IllegalStateException();
    }

    public QName getName() {
        switch (this.event) {
            case 1: 
            case 2: {
                return DOMXMLStreamReader.getQName(this.node);
            }
        }
        throw new IllegalStateException();
    }

    private Attr[] grow(Attr[] array) {
        Attr[] newArray = new Attr[array.length * 2];
        System.arraycopy(array, 0, newArray, 0, array.length);
        return newArray;
    }

    private void loadAttributes() {
        if (!this.attributesLoaded) {
            this.attributeCount = 0;
            this.namespaceCount = 0;
            NamedNodeMap attrs = this.node.getAttributes();
            int l = attrs.getLength();
            for (int i = 0; i < l; ++i) {
                Attr attr = (Attr)attrs.item(i);
                if (DOMUtils.isNSDecl(attr)) {
                    if (this.namespaceCount == this.namespaces.length) {
                        this.namespaces = this.grow(this.namespaces);
                    }
                    this.namespaces[this.namespaceCount++] = attr;
                    continue;
                }
                if (this.attributeCount == this.attributes.length) {
                    this.attributes = this.grow(this.attributes);
                }
                this.attributes[this.attributeCount++] = attr;
            }
            this.attributesLoaded = true;
        }
    }

    public int getAttributeCount() {
        if (this.event == 1) {
            this.loadAttributes();
            return this.attributeCount;
        }
        throw new IllegalStateException();
    }

    private Attr getAttribute(int index) {
        if (this.event == 1) {
            this.loadAttributes();
            return this.attributes[index];
        }
        throw new IllegalStateException();
    }

    public String getAttributeLocalName(int index) {
        return this.getAttribute(index).getLocalName();
    }

    public String getAttributeNamespace(int index) {
        return this.getAttribute(index).getNamespaceURI();
    }

    public String getAttributePrefix(int index) {
        return this.getAttribute(index).getPrefix();
    }

    public QName getAttributeName(int index) {
        return DOMXMLStreamReader.getQName(this.getAttribute(index));
    }

    public String getAttributeValue(int index) {
        return this.getAttribute(index).getValue();
    }

    public String getAttributeType(int index) {
        if (this.event == 1) {
            return "CDATA";
        }
        throw new IllegalStateException();
    }

    public boolean isAttributeSpecified(int index) {
        return this.getAttribute(index).getSpecified();
    }

    public String getAttributeValue(String namespaceURI, String localName) {
        return ((Element)this.node).getAttributeNS(namespaceURI == null || namespaceURI.length() == 0 ? null : namespaceURI, localName);
    }

    public int getNamespaceCount() {
        switch (this.event) {
            case 1: 
            case 2: {
                this.loadAttributes();
                return this.namespaceCount;
            }
        }
        throw new IllegalStateException();
    }

    private Attr getNamespace(int index) {
        switch (this.event) {
            case 1: 
            case 2: {
                this.loadAttributes();
                return this.namespaces[index];
            }
        }
        throw new IllegalStateException();
    }

    public String getNamespacePrefix(int index) {
        return DOMUtils.getNSDeclPrefix(this.getNamespace(index));
    }

    public String getNamespaceURI(int index) {
        return this.getNamespace(index).getValue();
    }

    private String internalGetText() {
        switch (this.event) {
            case 4: 
            case 5: 
            case 6: 
            case 12: {
                return this.node.getNodeValue();
            }
        }
        throw new IllegalStateException();
    }

    public String getText() {
        switch (this.event) {
            case 11: {
                return ((DocumentType)this.node).getInternalSubset();
            }
            case 9: {
                return null;
            }
        }
        return this.internalGetText();
    }

    public int getTextStart() {
        this.internalGetText();
        return 0;
    }

    public int getTextLength() {
        return this.internalGetText().length();
    }

    public char[] getTextCharacters() {
        return this.internalGetText().toCharArray();
    }

    public int getTextCharacters(int sourceStart, char[] target, int targetStart, int length) throws XMLStreamException {
        String text = this.internalGetText();
        int copied = Math.min(length, text.length() - sourceStart);
        text.getChars(sourceStart, sourceStart + copied, target, targetStart);
        return copied;
    }

    public String getPITarget() {
        if (this.event == 3) {
            return ((ProcessingInstruction)this.node).getTarget();
        }
        throw new IllegalStateException();
    }

    public String getPIData() {
        if (this.event == 3) {
            return ((ProcessingInstruction)this.node).getData();
        }
        throw new IllegalStateException();
    }

    public NamespaceContext getNamespaceContext() {
        if (this.nsContext == null) {
            this.nsContext = new DOMNamespaceContext(this);
        }
        return this.nsContext;
    }

    public String getNamespaceURI(String prefix) {
        Node current = this.node;
        do {
            NamedNodeMap attributes;
            if ((attributes = current.getAttributes()) == null) continue;
            int l = attributes.getLength();
            for (int i = 0; i < l; ++i) {
                Attr attr = (Attr)attributes.item(i);
                if (!DOMUtils.isNSDecl(attr)) continue;
                String candidatePrefix = DOMUtils.getNSDeclPrefix(attr);
                if (candidatePrefix == null) {
                    candidatePrefix = "";
                }
                if (!candidatePrefix.equals(prefix)) continue;
                return attr.getValue();
            }
        } while ((current = current.getParentNode()) != null);
        return null;
    }

    public void close() throws XMLStreamException {
    }

    private static QName getQName(Node node) {
        String prefix = node.getPrefix();
        return new QName(node.getNamespaceURI(), node.getLocalName(), prefix == null ? "" : prefix);
    }
}

