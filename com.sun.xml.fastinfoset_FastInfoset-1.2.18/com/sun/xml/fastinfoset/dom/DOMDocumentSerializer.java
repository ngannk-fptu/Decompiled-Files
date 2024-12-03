/*
 * Decompiled with CFR 0.152.
 */
package com.sun.xml.fastinfoset.dom;

import com.sun.xml.fastinfoset.Encoder;
import com.sun.xml.fastinfoset.QualifiedName;
import com.sun.xml.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.fastinfoset.util.NamespaceContextImplementation;
import java.io.IOException;
import org.jvnet.fastinfoset.FastInfosetException;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMDocumentSerializer
extends Encoder {
    protected NamespaceContextImplementation _namespaceScopeContext = new NamespaceContextImplementation();
    protected Node[] _attributes = new Node[32];

    public final void serialize(Node n) throws IOException {
        switch (n.getNodeType()) {
            case 9: {
                this.serialize((Document)n);
                break;
            }
            case 1: {
                this.serializeElementAsDocument(n);
                break;
            }
            case 8: {
                this.serializeComment(n);
                break;
            }
            case 7: {
                this.serializeProcessingInstruction(n);
            }
        }
    }

    public final void serialize(Document d) throws IOException {
        this.reset();
        this.encodeHeader(false);
        this.encodeInitialVocabulary();
        NodeList nl = d.getChildNodes();
        block5: for (int i = 0; i < nl.getLength(); ++i) {
            Node n = nl.item(i);
            switch (n.getNodeType()) {
                case 1: {
                    this.serializeElement(n);
                    continue block5;
                }
                case 8: {
                    this.serializeComment(n);
                    continue block5;
                }
                case 7: {
                    this.serializeProcessingInstruction(n);
                }
            }
        }
        this.encodeDocumentTermination();
    }

    protected final void serializeElementAsDocument(Node e) throws IOException {
        this.reset();
        this.encodeHeader(false);
        this.encodeInitialVocabulary();
        this.serializeElement(e);
        this.encodeDocumentTermination();
    }

    protected final void serializeElement(Node e) throws IOException {
        this.encodeTermination();
        int attributesSize = 0;
        this._namespaceScopeContext.pushContext();
        if (e.hasAttributes()) {
            NamedNodeMap nnm = e.getAttributes();
            for (int i = 0; i < nnm.getLength(); ++i) {
                Node a = nnm.item(i);
                String namespaceURI = a.getNamespaceURI();
                if (namespaceURI != null && namespaceURI.equals("http://www.w3.org/2000/xmlns/")) {
                    String attrPrefix = a.getLocalName();
                    String attrNamespace = a.getNodeValue();
                    if (attrPrefix == "xmlns" || attrPrefix.equals("xmlns")) {
                        attrPrefix = "";
                    }
                    this._namespaceScopeContext.declarePrefix(attrPrefix, attrNamespace);
                    continue;
                }
                if (attributesSize == this._attributes.length) {
                    Node[] attributes = new Node[attributesSize * 3 / 2 + 1];
                    System.arraycopy(this._attributes, 0, attributes, 0, attributesSize);
                    this._attributes = attributes;
                }
                this._attributes[attributesSize++] = a;
                String attrNamespaceURI = a.getNamespaceURI();
                String attrPrefix = a.getPrefix();
                if (attrPrefix == null || this._namespaceScopeContext.getNamespaceURI(attrPrefix).equals(attrNamespaceURI)) continue;
                this._namespaceScopeContext.declarePrefix(attrPrefix, attrNamespaceURI);
            }
        }
        String elementNamespaceURI = e.getNamespaceURI();
        String elementPrefix = e.getPrefix();
        if (elementPrefix == null) {
            elementPrefix = "";
        }
        if (elementNamespaceURI != null && !this._namespaceScopeContext.getNamespaceURI(elementPrefix).equals(elementNamespaceURI)) {
            this._namespaceScopeContext.declarePrefix(elementPrefix, elementNamespaceURI);
        }
        if (!this._namespaceScopeContext.isCurrentContextEmpty()) {
            if (attributesSize > 0) {
                this.write(120);
            } else {
                this.write(56);
            }
            for (int i = this._namespaceScopeContext.getCurrentContextStartIndex(); i < this._namespaceScopeContext.getCurrentContextEndIndex(); ++i) {
                String prefix = this._namespaceScopeContext.getPrefix(i);
                String uri = this._namespaceScopeContext.getNamespaceURI(i);
                this.encodeNamespaceAttribute(prefix, uri);
            }
            this.write(240);
            this._b = 0;
        } else {
            this._b = attributesSize > 0 ? 64 : 0;
        }
        String namespaceURI = elementNamespaceURI;
        namespaceURI = namespaceURI == null ? "" : namespaceURI;
        this.encodeElement(namespaceURI, e.getNodeName(), e.getLocalName());
        if (attributesSize > 0) {
            for (int i = 0; i < attributesSize; ++i) {
                Node a = this._attributes[i];
                this._attributes[i] = null;
                namespaceURI = a.getNamespaceURI();
                namespaceURI = namespaceURI == null ? "" : namespaceURI;
                this.encodeAttribute(namespaceURI, a.getNodeName(), a.getLocalName());
                String value = a.getNodeValue();
                boolean addToTable = this.isAttributeValueLengthMatchesLimit(value.length());
                this.encodeNonIdentifyingStringOnFirstBit(value, this._v.attributeValue, addToTable, false);
            }
            this._b = 240;
            this._terminate = true;
        }
        if (e.hasChildNodes()) {
            NodeList nl = e.getChildNodes();
            block10: for (int i = 0; i < nl.getLength(); ++i) {
                Node n = nl.item(i);
                switch (n.getNodeType()) {
                    case 1: {
                        this.serializeElement(n);
                        continue block10;
                    }
                    case 3: {
                        this.serializeText(n);
                        continue block10;
                    }
                    case 4: {
                        this.serializeCDATA(n);
                        continue block10;
                    }
                    case 8: {
                        this.serializeComment(n);
                        continue block10;
                    }
                    case 7: {
                        this.serializeProcessingInstruction(n);
                    }
                }
            }
        }
        this.encodeElementTermination();
        this._namespaceScopeContext.popContext();
    }

    protected final void serializeText(Node t) throws IOException {
        int length;
        String text = t.getNodeValue();
        int n = length = text != null ? text.length() : 0;
        if (length == 0) {
            return;
        }
        if (length < this._charBuffer.length) {
            text.getChars(0, length, this._charBuffer, 0);
            if (this.getIgnoreWhiteSpaceTextContent() && DOMDocumentSerializer.isWhiteSpace(this._charBuffer, 0, length)) {
                return;
            }
            this.encodeTermination();
            this.encodeCharacters(this._charBuffer, 0, length);
        } else {
            char[] ch = text.toCharArray();
            if (this.getIgnoreWhiteSpaceTextContent() && DOMDocumentSerializer.isWhiteSpace(ch, 0, length)) {
                return;
            }
            this.encodeTermination();
            this.encodeCharactersNoClone(ch, 0, length);
        }
    }

    protected final void serializeCDATA(Node t) throws IOException {
        int length;
        String text = t.getNodeValue();
        int n = length = text != null ? text.length() : 0;
        if (length == 0) {
            return;
        }
        char[] ch = text.toCharArray();
        if (this.getIgnoreWhiteSpaceTextContent() && DOMDocumentSerializer.isWhiteSpace(ch, 0, length)) {
            return;
        }
        this.encodeTermination();
        try {
            this.encodeCIIBuiltInAlgorithmDataAsCDATA(ch, 0, length);
        }
        catch (FastInfosetException e) {
            throw new IOException("");
        }
    }

    protected final void serializeComment(Node c) throws IOException {
        int length;
        if (this.getIgnoreComments()) {
            return;
        }
        this.encodeTermination();
        String comment = c.getNodeValue();
        int n = length = comment != null ? comment.length() : 0;
        if (length == 0) {
            this.encodeComment(this._charBuffer, 0, 0);
        } else if (length < this._charBuffer.length) {
            comment.getChars(0, length, this._charBuffer, 0);
            this.encodeComment(this._charBuffer, 0, length);
        } else {
            char[] ch = comment.toCharArray();
            this.encodeCommentNoClone(ch, 0, length);
        }
    }

    protected final void serializeProcessingInstruction(Node pi) throws IOException {
        if (this.getIgnoreProcesingInstructions()) {
            return;
        }
        this.encodeTermination();
        String target = pi.getNodeName();
        String data = pi.getNodeValue();
        this.encodeProcessingInstruction(target, data);
    }

    protected final void encodeElement(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI != names[i].namespaceName && !namespaceURI.equals(names[i].namespaceName)) continue;
                this.encodeNonZeroIntegerOnThirdBit(names[i].index);
                return;
            }
        }
        if (localName != null) {
            this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, DOMDocumentSerializer.getPrefixFromQualifiedName(qName), localName, entry);
        } else {
            this.encodeLiteralElementQualifiedNameOnThirdBit(namespaceURI, "", qName, entry);
        }
    }

    protected final void encodeAttribute(String namespaceURI, String qName, String localName) throws IOException {
        LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(qName);
        if (entry._valueIndex > 0) {
            QualifiedName[] names = entry._value;
            for (int i = 0; i < entry._valueIndex; ++i) {
                if (namespaceURI != names[i].namespaceName && !namespaceURI.equals(names[i].namespaceName)) continue;
                this.encodeNonZeroIntegerOnSecondBitFirstBitZero(names[i].index);
                return;
            }
        }
        if (localName != null) {
            this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, DOMDocumentSerializer.getPrefixFromQualifiedName(qName), localName, entry);
        } else {
            this.encodeLiteralAttributeQualifiedNameOnSecondBit(namespaceURI, "", qName, entry);
        }
    }
}

