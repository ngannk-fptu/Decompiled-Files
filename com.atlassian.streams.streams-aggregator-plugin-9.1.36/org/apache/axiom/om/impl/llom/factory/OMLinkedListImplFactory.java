/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom.factory;

import javax.xml.namespace.QName;
import org.apache.axiom.ext.stax.datahandler.DataHandlerProvider;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMComment;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMDataSource;
import org.apache.axiom.om.OMDocType;
import org.apache.axiom.om.OMDocument;
import org.apache.axiom.om.OMElement;
import org.apache.axiom.om.OMEntityReference;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMMetaFactory;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMProcessingInstruction;
import org.apache.axiom.om.OMSourcedElement;
import org.apache.axiom.om.OMText;
import org.apache.axiom.om.OMXMLParserWrapper;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.builder.StAXOMBuilder;
import org.apache.axiom.om.impl.llom.OMAttributeImpl;
import org.apache.axiom.om.impl.llom.OMCommentImpl;
import org.apache.axiom.om.impl.llom.OMDocTypeImpl;
import org.apache.axiom.om.impl.llom.OMDocumentImpl;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMEntityReferenceImpl;
import org.apache.axiom.om.impl.llom.OMNamespaceImpl;
import org.apache.axiom.om.impl.llom.OMProcessingInstructionImpl;
import org.apache.axiom.om.impl.llom.OMSourcedElementImpl;
import org.apache.axiom.om.impl.llom.OMTextImpl;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListMetaFactory;
import org.apache.axiom.om.impl.util.OMSerializerUtil;

public class OMLinkedListImplFactory
implements OMFactoryEx {
    private final OMLinkedListMetaFactory metaFactory;

    public OMLinkedListImplFactory(OMLinkedListMetaFactory metaFactory) {
        this.metaFactory = metaFactory;
    }

    public OMLinkedListImplFactory() {
        this(new OMLinkedListMetaFactory());
    }

    public OMMetaFactory getMetaFactory() {
        return this.metaFactory;
    }

    public OMElement createOMElement(String localName, OMNamespace ns) {
        return new OMElementImpl(null, localName, ns, null, this, true);
    }

    public OMElement createOMElement(String localName, OMNamespace ns, OMContainer parent) {
        return new OMElementImpl(parent, localName, ns, null, this, true);
    }

    public OMElement createOMElement(String localName, OMContainer parent, OMXMLParserWrapper builder) {
        return new OMElementImpl(parent, localName, null, builder, this, false);
    }

    public OMElement createOMElement(String localName, String namespaceURI, String prefix) {
        if (namespaceURI == null) {
            throw new IllegalArgumentException("namespaceURI must not be null");
        }
        if (namespaceURI.length() == 0) {
            if (prefix != null && prefix.length() > 0) {
                throw new IllegalArgumentException("Cannot create a prefixed element with an empty namespace name");
            }
            return this.createOMElement(localName, null);
        }
        return this.createOMElement(localName, this.createOMNamespace(namespaceURI, prefix));
    }

    public OMElement createOMElement(QName qname, OMContainer parent) throws OMException {
        return new OMElementImpl(qname, parent, this);
    }

    public OMElement createOMElement(QName qname) throws OMException {
        return new OMElementImpl(qname, null, this);
    }

    public OMSourcedElement createOMElement(OMDataSource source) {
        return new OMSourcedElementImpl(this, source);
    }

    public OMSourcedElement createOMElement(OMDataSource source, String localName, OMNamespace ns) {
        return new OMSourcedElementImpl(localName, ns, this, source);
    }

    public OMSourcedElement createOMElement(OMDataSource source, QName qname) {
        return new OMSourcedElementImpl(qname, this, source);
    }

    public OMNamespace createOMNamespace(String uri, String prefix) {
        return new OMNamespaceImpl(uri, prefix);
    }

    public OMText createOMText(OMContainer parent, String text) {
        return new OMTextImpl(parent, text, (OMFactory)this);
    }

    public OMText createOMText(OMContainer parent, QName text) {
        return new OMTextImpl(parent, text, (OMFactory)this);
    }

    public OMText createOMText(OMContainer parent, String text, int type) {
        return this.createOMText(parent, text, type, false);
    }

    public OMText createOMText(OMContainer parent, String text, int type, boolean fromBuilder) {
        return new OMTextImpl(parent, text, type, (OMFactory)this, fromBuilder);
    }

    public OMText createOMText(OMContainer parent, char[] charArary, int type) {
        return new OMTextImpl(parent, charArary, type, (OMFactory)this);
    }

    public OMText createOMText(OMContainer parent, QName text, int type) {
        return new OMTextImpl(parent, text, type, (OMFactory)this);
    }

    public OMText createOMText(String s) {
        return new OMTextImpl(s, (OMFactory)this);
    }

    public OMText createOMText(String s, int type) {
        return new OMTextImpl(s, type, (OMFactory)this);
    }

    public OMText createOMText(String s, String mimeType, boolean optimize) {
        return new OMTextImpl(s, mimeType, optimize, (OMFactory)this);
    }

    public OMText createOMText(Object dataHandler, boolean optimize) {
        return this.createOMText(null, dataHandler, optimize, false);
    }

    public OMText createOMText(OMContainer parent, Object dataHandler, boolean optimize, boolean fromBuilder) {
        return new OMTextImpl(parent, dataHandler, optimize, (OMFactory)this, fromBuilder);
    }

    public OMText createOMText(String contentID, DataHandlerProvider dataHandlerProvider, boolean optimize) {
        return new OMTextImpl(contentID, dataHandlerProvider, optimize, (OMFactory)this);
    }

    public OMText createOMText(OMContainer parent, OMText source) {
        return new OMTextImpl(parent, (OMTextImpl)source, (OMFactory)this);
    }

    public OMText createOMText(OMContainer parent, String s, String mimeType, boolean optimize) {
        return new OMTextImpl(parent, s, mimeType, optimize, this);
    }

    public OMAttribute createOMAttribute(String localName, OMNamespace ns, String value) {
        if (ns != null && ns.getPrefix() == null) {
            String namespaceURI = ns.getNamespaceURI();
            ns = namespaceURI.length() == 0 ? null : new OMNamespaceImpl(namespaceURI, OMSerializerUtil.getNextNSPrefix());
        }
        return new OMAttributeImpl(localName, ns, value, this);
    }

    public OMDocType createOMDocType(OMContainer parent, String rootName, String publicId, String systemId, String internalSubset) {
        return this.createOMDocType(parent, rootName, publicId, systemId, internalSubset, false);
    }

    public OMDocType createOMDocType(OMContainer parent, String rootName, String publicId, String systemId, String internalSubset, boolean fromBuilder) {
        return new OMDocTypeImpl(parent, rootName, publicId, systemId, internalSubset, this, fromBuilder);
    }

    public OMProcessingInstruction createOMProcessingInstruction(OMContainer parent, String piTarget, String piData) {
        return this.createOMProcessingInstruction(parent, piTarget, piData, false);
    }

    public OMProcessingInstruction createOMProcessingInstruction(OMContainer parent, String piTarget, String piData, boolean fromBuilder) {
        return new OMProcessingInstructionImpl(parent, piTarget, piData, this, fromBuilder);
    }

    public OMComment createOMComment(OMContainer parent, String content) {
        return this.createOMComment(parent, content, false);
    }

    public OMComment createOMComment(OMContainer parent, String content, boolean fromBuilder) {
        return new OMCommentImpl(parent, content, this, fromBuilder);
    }

    public OMDocument createOMDocument() {
        return new OMDocumentImpl(this);
    }

    public OMDocument createOMDocument(OMXMLParserWrapper builder) {
        return new OMDocumentImpl(builder, this);
    }

    public OMEntityReference createOMEntityReference(OMContainer parent, String name) {
        return this.createOMEntityReference(parent, name, null, false);
    }

    public OMEntityReference createOMEntityReference(OMContainer parent, String name, String replacementText, boolean fromBuilder) {
        return new OMEntityReferenceImpl(parent, name, replacementText, this, fromBuilder);
    }

    public OMNode importNode(OMNode child) {
        int type = child.getType();
        switch (type) {
            case 1: {
                OMElement childElement = (OMElement)child;
                OMElement newElement = new StAXOMBuilder(this, childElement.getXMLStreamReader()).getDocumentElement();
                newElement.buildWithAttachments();
                return newElement;
            }
            case 4: {
                OMText newText;
                OMText importedText = (OMText)child;
                if (importedText.isBinary()) {
                    boolean isOptimize = importedText.isOptimized();
                    newText = this.createOMText(importedText.getDataHandler(), isOptimize);
                } else {
                    newText = importedText.isCharacters() ? this.createOMText(null, importedText.getTextCharacters(), importedText.getType()) : this.createOMText(null, importedText.getText());
                }
                return newText;
            }
            case 3: {
                OMProcessingInstruction importedPI = (OMProcessingInstruction)child;
                return this.createOMProcessingInstruction(null, importedPI.getTarget(), importedPI.getValue());
            }
            case 5: {
                OMComment importedComment = (OMComment)child;
                return this.createOMComment(null, importedComment.getValue());
            }
            case 11: {
                OMDocType importedDocType = (OMDocType)child;
                return this.createOMDocType(null, importedDocType.getRootName(), importedDocType.getPublicId(), importedDocType.getSystemId(), importedDocType.getInternalSubset());
            }
        }
        throw new UnsupportedOperationException("Not Implemented Yet for the given node type");
    }
}

