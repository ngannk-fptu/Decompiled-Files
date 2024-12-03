/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.util.ArrayList;
import javax.xml.transform.dom.DOMResult;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.EntityImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.NotationImpl;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.jaxp.validation.DOMDocumentHandler;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.Entity;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.Notation;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultBuilder
implements DOMDocumentHandler {
    private static final int[] kidOK = new int[13];
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private Node fTarget;
    private Node fNextSibling;
    private Node fCurrentNode;
    private Node fFragmentRoot;
    private final ArrayList fTargetChildren = new ArrayList();
    private boolean fIgnoreChars;
    private final QName fAttributeQName = new QName();

    @Override
    public void setDOMResult(DOMResult dOMResult) {
        this.fCurrentNode = null;
        this.fFragmentRoot = null;
        this.fIgnoreChars = false;
        this.fTargetChildren.clear();
        if (dOMResult != null) {
            this.fTarget = dOMResult.getNode();
            this.fNextSibling = dOMResult.getNextSibling();
            this.fDocument = this.fTarget.getNodeType() == 9 ? (Document)this.fTarget : this.fTarget.getOwnerDocument();
            this.fDocumentImpl = this.fDocument instanceof CoreDocumentImpl ? (CoreDocumentImpl)this.fDocument : null;
            this.fStorePSVI = this.fDocument instanceof PSVIDocumentImpl;
            return;
        }
        this.fTarget = null;
        this.fNextSibling = null;
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fStorePSVI = false;
    }

    @Override
    public void doctypeDecl(DocumentType documentType) throws XNIException {
        if (this.fDocumentImpl != null) {
            NodeImpl nodeImpl;
            Node node;
            int n;
            DocumentType documentType2 = this.fDocumentImpl.createDocumentType(documentType.getName(), documentType.getPublicId(), documentType.getSystemId());
            String string = documentType.getInternalSubset();
            if (string != null) {
                ((DocumentTypeImpl)documentType2).setInternalSubset(string);
            }
            NamedNodeMap namedNodeMap = documentType.getEntities();
            NamedNodeMap namedNodeMap2 = documentType2.getEntities();
            int n2 = namedNodeMap.getLength();
            for (n = 0; n < n2; ++n) {
                node = (Entity)namedNodeMap.item(n);
                nodeImpl = (EntityImpl)this.fDocumentImpl.createEntity(node.getNodeName());
                ((EntityImpl)nodeImpl).setPublicId(node.getPublicId());
                ((EntityImpl)nodeImpl).setSystemId(node.getSystemId());
                ((EntityImpl)nodeImpl).setNotationName(node.getNotationName());
                namedNodeMap2.setNamedItem(nodeImpl);
            }
            namedNodeMap = documentType.getNotations();
            namedNodeMap2 = documentType2.getNotations();
            n2 = namedNodeMap.getLength();
            for (n = 0; n < n2; ++n) {
                node = (Notation)namedNodeMap.item(n);
                nodeImpl = (NotationImpl)this.fDocumentImpl.createNotation(node.getNodeName());
                ((NotationImpl)nodeImpl).setPublicId(node.getPublicId());
                ((NotationImpl)nodeImpl).setSystemId(node.getSystemId());
                namedNodeMap2.setNamedItem(nodeImpl);
            }
            this.append(documentType2);
        }
    }

    @Override
    public void characters(Text text) throws XNIException {
        this.append(this.fDocument.createTextNode(text.getNodeValue()));
    }

    @Override
    public void cdata(CDATASection cDATASection) throws XNIException {
        this.append(this.fDocument.createCDATASection(cDATASection.getNodeValue()));
    }

    @Override
    public void comment(Comment comment) throws XNIException {
        this.append(this.fDocument.createComment(comment.getNodeValue()));
    }

    @Override
    public void processingInstruction(ProcessingInstruction processingInstruction) throws XNIException {
        this.append(this.fDocument.createProcessingInstruction(processingInstruction.getTarget(), processingInstruction.getData()));
    }

    @Override
    public void setIgnoringCharacters(boolean bl) {
        this.fIgnoreChars = bl;
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        Element element;
        int n = xMLAttributes.getLength();
        if (this.fDocumentImpl == null) {
            element = this.fDocument.createElementNS(qName.uri, qName.rawname);
            for (int i = 0; i < n; ++i) {
                xMLAttributes.getName(i, this.fAttributeQName);
                element.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, xMLAttributes.getValue(i));
            }
        } else {
            element = this.fDocumentImpl.createElementNS(qName.uri, qName.rawname, qName.localpart);
            for (int i = 0; i < n; ++i) {
                xMLAttributes.getName(i, this.fAttributeQName);
                AttrImpl attrImpl = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
                attrImpl.setValue(xMLAttributes.getValue(i));
                element.setAttributeNodeNS(attrImpl);
                AttributePSVI attributePSVI = (AttributePSVI)xMLAttributes.getAugmentations(i).getItem("ATTRIBUTE_PSVI");
                if (attributePSVI != null) {
                    XSTypeDefinition xSTypeDefinition;
                    if (this.fStorePSVI) {
                        ((PSVIAttrNSImpl)attrImpl).setPSVI(attributePSVI);
                    }
                    if ((xSTypeDefinition = attributePSVI.getMemberTypeDefinition()) == null) {
                        xSTypeDefinition = attributePSVI.getTypeDefinition();
                        if (xSTypeDefinition != null) {
                            attrImpl.setType(xSTypeDefinition);
                            if (((XSSimpleType)xSTypeDefinition).isIDType()) {
                                ((ElementImpl)element).setIdAttributeNode(attrImpl, true);
                            }
                        }
                    } else {
                        attrImpl.setType(xSTypeDefinition);
                        if (((XSSimpleType)xSTypeDefinition).isIDType()) {
                            ((ElementImpl)element).setIdAttributeNode(attrImpl, true);
                        }
                    }
                }
                attrImpl.setSpecified(xMLAttributes.isSpecified(i));
            }
        }
        this.append(element);
        this.fCurrentNode = element;
        if (this.fFragmentRoot == null) {
            this.fFragmentRoot = element;
        }
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.startElement(qName, xMLAttributes, augmentations);
        this.endElement(qName, augmentations);
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.fIgnoreChars) {
            this.append(this.fDocument.createTextNode(xMLString.toString()));
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.characters(xMLString, augmentations);
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        ElementPSVI elementPSVI;
        if (augmentations != null && this.fDocumentImpl != null && (elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI")) != null) {
            XSTypeDefinition xSTypeDefinition;
            if (this.fStorePSVI) {
                ((PSVIElementNSImpl)this.fCurrentNode).setPSVI(elementPSVI);
            }
            if ((xSTypeDefinition = elementPSVI.getMemberTypeDefinition()) == null) {
                xSTypeDefinition = elementPSVI.getTypeDefinition();
            }
            ((ElementNSImpl)this.fCurrentNode).setType(xSTypeDefinition);
        }
        if (this.fCurrentNode == this.fFragmentRoot) {
            this.fCurrentNode = null;
            this.fFragmentRoot = null;
            return;
        }
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        int n = this.fTargetChildren.size();
        if (this.fNextSibling == null) {
            for (int i = 0; i < n; ++i) {
                this.fTarget.appendChild((Node)this.fTargetChildren.get(i));
            }
        } else {
            for (int i = 0; i < n; ++i) {
                this.fTarget.insertBefore((Node)this.fTargetChildren.get(i), this.fNextSibling);
            }
        }
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }

    private void append(Node node) throws XNIException {
        if (this.fCurrentNode != null) {
            this.fCurrentNode.appendChild(node);
        } else {
            if ((kidOK[this.fTarget.getNodeType()] & 1 << node.getNodeType()) == 0) {
                String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "HIERARCHY_REQUEST_ERR", null);
                throw new XNIException(string);
            }
            this.fTargetChildren.add(node);
        }
    }

    static {
        DOMResultBuilder.kidOK[9] = 1410;
        DOMResultBuilder.kidOK[1] = 442;
        DOMResultBuilder.kidOK[5] = 442;
        DOMResultBuilder.kidOK[6] = 442;
        DOMResultBuilder.kidOK[11] = 442;
        DOMResultBuilder.kidOK[2] = 40;
        DOMResultBuilder.kidOK[10] = 0;
        DOMResultBuilder.kidOK[7] = 0;
        DOMResultBuilder.kidOK[8] = 0;
        DOMResultBuilder.kidOK[3] = 0;
        DOMResultBuilder.kidOK[4] = 0;
        DOMResultBuilder.kidOK[12] = 0;
    }
}

