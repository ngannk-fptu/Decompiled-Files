/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import javax.xml.transform.dom.DOMResult;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.ElementNSImpl;
import org.apache.xerces.dom.PSVIAttrNSImpl;
import org.apache.xerces.dom.PSVIDocumentImpl;
import org.apache.xerces.dom.PSVIElementNSImpl;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.jaxp.validation.DOMDocumentHandler;
import org.apache.xerces.jaxp.validation.DOMValidatorHelper;
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
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;

final class DOMResultAugmentor
implements DOMDocumentHandler {
    private final DOMValidatorHelper fDOMValidatorHelper;
    private Document fDocument;
    private CoreDocumentImpl fDocumentImpl;
    private boolean fStorePSVI;
    private boolean fIgnoreChars;
    private final QName fAttributeQName = new QName();

    public DOMResultAugmentor(DOMValidatorHelper dOMValidatorHelper) {
        this.fDOMValidatorHelper = dOMValidatorHelper;
    }

    @Override
    public void setDOMResult(DOMResult dOMResult) {
        this.fIgnoreChars = false;
        if (dOMResult != null) {
            Node node = dOMResult.getNode();
            this.fDocument = node.getNodeType() == 9 ? (Document)node : node.getOwnerDocument();
            this.fDocumentImpl = this.fDocument instanceof CoreDocumentImpl ? (CoreDocumentImpl)this.fDocument : null;
            this.fStorePSVI = this.fDocument instanceof PSVIDocumentImpl;
            return;
        }
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fStorePSVI = false;
    }

    @Override
    public void doctypeDecl(DocumentType documentType) throws XNIException {
    }

    @Override
    public void characters(Text text) throws XNIException {
    }

    @Override
    public void cdata(CDATASection cDATASection) throws XNIException {
    }

    @Override
    public void comment(Comment comment) throws XNIException {
    }

    @Override
    public void processingInstruction(ProcessingInstruction processingInstruction) throws XNIException {
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
        block7: {
            int n;
            Object object;
            int n2;
            Element element = (Element)this.fDOMValidatorHelper.getCurrentElement();
            NamedNodeMap namedNodeMap = element.getAttributes();
            int n3 = namedNodeMap.getLength();
            if (this.fDocumentImpl != null) {
                for (n2 = 0; n2 < n3; ++n2) {
                    AttrImpl attrImpl = (AttrImpl)namedNodeMap.item(n2);
                    object = (AttributePSVI)xMLAttributes.getAugmentations(n2).getItem("ATTRIBUTE_PSVI");
                    if (object == null || !this.processAttributePSVI(attrImpl, (AttributePSVI)object)) continue;
                    ((ElementImpl)element).setIdAttributeNode(attrImpl, true);
                }
            }
            if ((n = xMLAttributes.getLength()) <= n3) break block7;
            if (this.fDocumentImpl == null) {
                for (n2 = n3; n2 < n; ++n2) {
                    xMLAttributes.getName(n2, this.fAttributeQName);
                    element.setAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, xMLAttributes.getValue(n2));
                }
            } else {
                for (n2 = n3; n2 < n; ++n2) {
                    xMLAttributes.getName(n2, this.fAttributeQName);
                    object = (AttrImpl)this.fDocumentImpl.createAttributeNS(this.fAttributeQName.uri, this.fAttributeQName.rawname, this.fAttributeQName.localpart);
                    ((AttrImpl)object).setValue(xMLAttributes.getValue(n2));
                    element.setAttributeNodeNS((Attr)object);
                    AttributePSVI attributePSVI = (AttributePSVI)xMLAttributes.getAugmentations(n2).getItem("ATTRIBUTE_PSVI");
                    if (attributePSVI != null && this.processAttributePSVI((AttrImpl)object, attributePSVI)) {
                        ((ElementImpl)element).setIdAttributeNode((Attr)object, true);
                    }
                    ((AttrImpl)object).setSpecified(false);
                }
            }
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
            Element element = (Element)this.fDOMValidatorHelper.getCurrentElement();
            element.appendChild(this.fDocument.createTextNode(xMLString.toString()));
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.characters(xMLString, augmentations);
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        ElementPSVI elementPSVI;
        Node node = this.fDOMValidatorHelper.getCurrentElement();
        if (augmentations != null && this.fDocumentImpl != null && (elementPSVI = (ElementPSVI)augmentations.getItem("ELEMENT_PSVI")) != null) {
            XSTypeDefinition xSTypeDefinition;
            if (this.fStorePSVI) {
                ((PSVIElementNSImpl)node).setPSVI(elementPSVI);
            }
            if ((xSTypeDefinition = elementPSVI.getMemberTypeDefinition()) == null) {
                xSTypeDefinition = elementPSVI.getTypeDefinition();
            }
            ((ElementNSImpl)node).setType(xSTypeDefinition);
        }
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return null;
    }

    private boolean processAttributePSVI(AttrImpl attrImpl, AttributePSVI attributePSVI) {
        XSTypeDefinition xSTypeDefinition;
        if (this.fStorePSVI) {
            ((PSVIAttrNSImpl)attrImpl).setPSVI(attributePSVI);
        }
        if ((xSTypeDefinition = attributePSVI.getMemberTypeDefinition()) == null) {
            xSTypeDefinition = attributePSVI.getTypeDefinition();
            if (xSTypeDefinition != null) {
                attrImpl.setType(xSTypeDefinition);
                return ((XSSimpleType)xSTypeDefinition).isIDType();
            }
        } else {
            attrImpl.setType(xSTypeDefinition);
            return ((XSSimpleType)xSTypeDefinition).isIDType();
        }
        return false;
    }
}

