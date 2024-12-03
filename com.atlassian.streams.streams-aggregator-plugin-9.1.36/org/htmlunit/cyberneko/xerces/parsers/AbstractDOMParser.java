/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.parsers;

import java.io.CharConversionException;
import java.io.IOException;
import java.util.Stack;
import org.htmlunit.cyberneko.xerces.dom.AttrImpl;
import org.htmlunit.cyberneko.xerces.dom.CoreDocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.DOMMessageFormatter;
import org.htmlunit.cyberneko.xerces.dom.DocumentImpl;
import org.htmlunit.cyberneko.xerces.dom.EntityImpl;
import org.htmlunit.cyberneko.xerces.dom.EntityReferenceImpl;
import org.htmlunit.cyberneko.xerces.dom.TextImpl;
import org.htmlunit.cyberneko.xerces.parsers.AbstractXMLDocumentParser;
import org.htmlunit.cyberneko.xerces.util.ErrorHandlerWrapper;
import org.htmlunit.cyberneko.xerces.util.SAXMessageFormatter;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLErrorHandler;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.LocatorImpl;

public class AbstractDOMParser
extends AbstractXMLDocumentParser {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String CREATE_ENTITY_REF_NODES = "http://apache.org/xml/features/dom/create-entity-ref-nodes";
    protected static final String INCLUDE_COMMENTS_FEATURE = "http://apache.org/xml/features/include-comments";
    protected static final String CREATE_CDATA_NODES_FEATURE = "http://apache.org/xml/features/create-cdata-nodes";
    protected static final String INCLUDE_IGNORABLE_WHITESPACE = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://apache.org/xml/features/dom/create-entity-ref-nodes", "http://apache.org/xml/features/include-comments", "http://apache.org/xml/features/create-cdata-nodes", "http://apache.org/xml/features/dom/include-ignorable-whitespace"};
    private static final String[] RECOGNIZED_PROPERTIES = new String[0];
    private static final boolean DEBUG_EVENTS = false;
    protected boolean fCreateEntityRefNodes;
    protected boolean fIncludeIgnorableWhitespace;
    protected boolean fIncludeComments;
    protected boolean fCreateCDATANodes;
    protected Document fDocument;
    protected CoreDocumentImpl fDocumentImpl;
    protected Class<? extends DocumentImpl> fDocumentClass;
    protected DocumentType fDocumentType;
    protected Node fCurrentNode;
    protected CDATASection fCurrentCDATASection;
    protected EntityImpl fCurrentEntityDecl;
    protected final StringBuilder fStringBuffer = new StringBuilder();
    protected boolean fNamespaceAware;
    protected boolean fInCDATASection;
    protected boolean fFirstChunk = false;
    protected final Stack<String> fBaseURIStack = new Stack();
    private final QName fAttrQName = new QName();
    private XMLLocator fLocator;

    protected AbstractDOMParser(XMLParserConfiguration config, Class<? extends DocumentImpl> documentClass) {
        super(config);
        this.fConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
        this.fConfiguration.setFeature(CREATE_ENTITY_REF_NODES, true);
        this.fConfiguration.setFeature(INCLUDE_IGNORABLE_WHITESPACE, true);
        this.fConfiguration.setFeature(INCLUDE_COMMENTS_FEATURE, true);
        this.fConfiguration.setFeature(CREATE_CDATA_NODES_FEATURE, true);
        this.fConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        this.setDocumentClass(documentClass);
    }

    protected void setDocumentClass(Class<? extends DocumentImpl> documentClass) {
        this.fDocumentClass = documentClass;
    }

    public Document getDocument() {
        return this.fDocument;
    }

    @Override
    public void reset() throws XNIException {
        super.reset();
        this.fCreateEntityRefNodes = this.fConfiguration.getFeature(CREATE_ENTITY_REF_NODES);
        this.fIncludeIgnorableWhitespace = this.fConfiguration.getFeature(INCLUDE_IGNORABLE_WHITESPACE);
        this.fNamespaceAware = this.fConfiguration.getFeature(NAMESPACES);
        this.fIncludeComments = this.fConfiguration.getFeature(INCLUDE_COMMENTS_FEATURE);
        this.fCreateCDATANodes = this.fConfiguration.getFeature(CREATE_CDATA_NODES_FEATURE);
        this.fDocument = null;
        this.fDocumentImpl = null;
        this.fDocumentType = null;
        this.fCurrentNode = null;
        this.fStringBuffer.setLength(0);
        this.fInCDATASection = false;
        this.fFirstChunk = false;
        this.fCurrentCDATASection = null;
        this.fBaseURIStack.removeAllElements();
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
        this.setCharacterData(true);
        EntityReference er = this.fDocument.createEntityReference(name);
        if (this.fDocumentImpl != null) {
            EntityReferenceImpl erImpl = (EntityReferenceImpl)er;
            if (this.fDocumentType != null) {
                NamedNodeMap entities = this.fDocumentType.getEntities();
                this.fCurrentEntityDecl = (EntityImpl)entities.getNamedItem(name);
                if (this.fCurrentEntityDecl != null) {
                    this.fCurrentEntityDecl.setInputEncoding(encoding);
                }
            }
            erImpl.needsSyncChildren(false);
        }
        this.fCurrentNode.appendChild(er);
        this.fCurrentNode = er;
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
        if (this.fCurrentEntityDecl != null) {
            this.fCurrentEntityDecl.setXmlEncoding(encoding);
            if (version != null) {
                this.fCurrentEntityDecl.setXmlVersion(version);
            }
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        if (!this.fIncludeComments) {
            return;
        }
        Comment comment = this.fDocument.createComment(text.toString());
        this.setCharacterData(false);
        this.fCurrentNode.appendChild(comment);
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        ProcessingInstruction pi = this.fDocument.createProcessingInstruction(target, data.toString());
        this.setCharacterData(false);
        this.fCurrentNode.appendChild(pi);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
        this.fLocator = locator;
        if (this.fDocumentClass == null) {
            this.fDocument = new DocumentImpl();
            this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
            this.fDocumentImpl.setStrictErrorChecking(false);
            this.fDocumentImpl.setInputEncoding(encoding);
            this.fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
        } else {
            try {
                this.fDocument = this.fDocumentClass.newInstance();
                this.fDocumentImpl = (CoreDocumentImpl)this.fDocument;
                this.fDocumentImpl.setStrictErrorChecking(false);
                this.fDocumentImpl.setInputEncoding(encoding);
                if (locator != null) {
                    this.fDocumentImpl.setDocumentURI(locator.getExpandedSystemId());
                }
            }
            catch (Exception e) {
                throw new RuntimeException(DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "CannotCreateDocumentClass", new Object[]{this.fDocumentClass.getSimpleName()}));
            }
        }
        this.fCurrentNode = this.fDocument;
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        if (this.fDocumentImpl != null) {
            if (version != null) {
                this.fDocumentImpl.setXmlVersion(version);
            }
            this.fDocumentImpl.setXmlEncoding(encoding);
            this.fDocumentImpl.setXmlStandalone("yes".equals(standalone));
        }
    }

    @Override
    public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
        if (this.fDocumentImpl != null) {
            this.fDocumentType = this.fDocumentImpl.createDocumentType(rootElement, publicId, systemId);
            this.fCurrentNode.appendChild(this.fDocumentType);
        }
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        Element el = this.createElementNode(element);
        int attrCount = attributes.getLength();
        boolean seenSchemaDefault = false;
        for (int i = 0; i < attrCount; ++i) {
            attributes.getName(i, this.fAttrQName);
            Attr attr = this.createAttrNode(this.fAttrQName);
            String attrValue = attributes.getValue(i);
            attr.setValue(attrValue);
            boolean specified = attributes.isSpecified(i);
            if (!specified && (seenSchemaDefault || this.fAttrQName.uri != null && this.fAttrQName.uri != "http://www.w3.org/2000/xmlns/" && this.fAttrQName.prefix == null)) {
                el.setAttributeNodeNS(attr);
                seenSchemaDefault = true;
            } else {
                el.setAttributeNode(attr);
            }
            if (this.fDocumentImpl == null) continue;
            AttrImpl attrImpl = (AttrImpl)attr;
            attrImpl.setType(null);
            attrImpl.setSpecified(specified);
        }
        this.setCharacterData(false);
        this.fCurrentNode.appendChild(el);
        this.fCurrentNode = el;
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        this.startElement(element, attributes, augs);
        this.endElement(element, augs);
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fInCDATASection && this.fCreateCDATANodes) {
            if (this.fCurrentCDATASection == null) {
                this.fCurrentCDATASection = this.fDocument.createCDATASection(text.toString());
                this.fCurrentNode.appendChild(this.fCurrentCDATASection);
                this.fCurrentNode = this.fCurrentCDATASection;
            } else {
                this.fCurrentCDATASection.appendData(text.toString());
            }
        } else {
            if (text.length() == 0) {
                return;
            }
            Node child = this.fCurrentNode.getLastChild();
            if (child != null && child.getNodeType() == 3) {
                if (this.fFirstChunk) {
                    if (this.fDocumentImpl != null) {
                        this.fStringBuffer.append(((TextImpl)child).removeData());
                    } else {
                        this.fStringBuffer.append(((Text)child).getData());
                        child.setNodeValue(null);
                    }
                    this.fFirstChunk = false;
                }
                if (text.length() > 0) {
                    text.appendTo(this.fStringBuffer);
                }
            } else {
                this.fFirstChunk = true;
                Text textNode = this.fDocument.createTextNode(text.toString());
                this.fCurrentNode.appendChild(textNode);
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        if (!this.fIncludeIgnorableWhitespace) {
            return;
        }
        Node child = this.fCurrentNode.getLastChild();
        if (child != null && child.getNodeType() == 3) {
            Text textNode = (Text)child;
            textNode.appendData(text.toString());
        } else {
            Text textNode = this.fDocument.createTextNode(text.toString());
            if (this.fDocumentImpl != null) {
                TextImpl textNodeImpl = (TextImpl)textNode;
                textNodeImpl.setIgnorableWhitespace(true);
            }
            this.fCurrentNode.appendChild(textNode);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        this.setCharacterData(false);
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        this.fInCDATASection = true;
        if (this.fCreateCDATANodes) {
            this.setCharacterData(false);
        }
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
        if (this.fCurrentCDATASection != null) {
            this.fCurrentNode = this.fCurrentNode.getParentNode();
            this.fCurrentCDATASection = null;
        }
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
        if (this.fDocumentImpl != null) {
            if (this.fLocator != null) {
                this.fDocumentImpl.setInputEncoding(this.fLocator.getEncoding());
            }
            this.fDocumentImpl.setStrictErrorChecking(true);
        }
        this.fCurrentNode = null;
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        this.setCharacterData(true);
        if (this.fDocumentType != null) {
            NamedNodeMap entities = this.fDocumentType.getEntities();
            this.fCurrentEntityDecl = (EntityImpl)entities.getNamedItem(name);
            if (this.fCurrentEntityDecl != null) {
                if (this.fCurrentEntityDecl.getFirstChild() == null) {
                    for (Node child = this.fCurrentNode.getFirstChild(); child != null; child = child.getNextSibling()) {
                        Node copy = child.cloneNode(true);
                        this.fCurrentEntityDecl.appendChild(copy);
                    }
                }
                this.fCurrentEntityDecl = null;
            }
        }
        if (!this.fCreateEntityRefNodes) {
            NodeList children = this.fCurrentNode.getChildNodes();
            Node parent = this.fCurrentNode.getParentNode();
            int length = children.getLength();
            if (length > 0) {
                Node node = this.fCurrentNode.getPreviousSibling();
                Node child = children.item(0);
                if (node != null && node.getNodeType() == 3 && child.getNodeType() == 3) {
                    ((Text)node).appendData(child.getNodeValue());
                    this.fCurrentNode.removeChild(child);
                } else {
                    node = parent.insertBefore(child, this.fCurrentNode);
                    this.handleBaseURI(node);
                }
                for (int i = 1; i < length; ++i) {
                    node = parent.insertBefore(children.item(0), this.fCurrentNode);
                    this.handleBaseURI(node);
                }
            }
            parent.removeChild(this.fCurrentNode);
            this.fCurrentNode = parent;
        }
    }

    protected final void handleBaseURI(Node node) {
        if (this.fDocumentImpl != null) {
            short nodeType = node.getNodeType();
            if (nodeType == 1) {
                if (this.fNamespaceAware ? ((Element)node).getAttributeNodeNS("http://www.w3.org/XML/1998/namespace", "base") != null : ((Element)node).getAttributeNode("xml:base") != null) {
                    return;
                }
                String baseURI = this.fCurrentNode.getBaseURI();
                if (baseURI != null && !baseURI.equals(this.fDocumentImpl.getDocumentURI())) {
                    if (this.fNamespaceAware) {
                        ((Element)node).setAttributeNS("http://www.w3.org/XML/1998/namespace", "xml:base", baseURI);
                    } else {
                        ((Element)node).setAttribute("xml:base", baseURI);
                    }
                }
            } else if (nodeType == 7) {
                String string = this.fCurrentNode.getBaseURI();
            }
        }
    }

    protected Element createElementNode(QName element) {
        Element el = this.fNamespaceAware ? (this.fDocumentImpl != null ? this.fDocumentImpl.createElementNS(element.uri, element.rawname, element.localpart) : this.fDocument.createElementNS(element.uri, element.rawname)) : this.fDocument.createElement(element.rawname);
        return el;
    }

    protected Attr createAttrNode(QName attrQName) {
        Attr attr = this.fNamespaceAware ? (this.fDocumentImpl != null ? this.fDocumentImpl.createAttributeNS(attrQName.uri, attrQName.rawname, attrQName.localpart) : this.fDocument.createAttributeNS(attrQName.uri, attrQName.rawname)) : this.fDocument.createAttribute(attrQName.rawname);
        return attr;
    }

    protected void setCharacterData(boolean sawChars) {
        this.fFirstChunk = sawChars;
        Node child = this.fCurrentNode.getLastChild();
        if (child != null && this.fStringBuffer.length() > 0) {
            if (child.getNodeType() == 3) {
                if (this.fDocumentImpl != null) {
                    ((TextImpl)child).replaceData(this.fStringBuffer.toString());
                } else {
                    ((Text)child).setData(this.fStringBuffer.toString());
                }
            }
            this.fStringBuffer.setLength(0);
        }
    }

    public void parse(String systemId) throws SAXException, IOException {
        XMLInputSource source = new XMLInputSource(null, systemId, null);
        try {
            this.parse(source);
        }
        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null || ex instanceof CharConversionException) {
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw ex == null ? new SAXParseException(e.getMessage(), locatorImpl) : new SAXParseException(e.getMessage(), locatorImpl, ex);
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (XNIException e) {
            e.printStackTrace();
            Exception ex = e.getException();
            if (ex == null) {
                throw new SAXException(e.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
    }

    public void parse(InputSource inputSource) throws SAXException, IOException {
        try {
            XMLInputSource xmlInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
            xmlInputSource.setByteStream(inputSource.getByteStream());
            xmlInputSource.setCharacterStream(inputSource.getCharacterStream());
            xmlInputSource.setEncoding(inputSource.getEncoding());
            this.parse(xmlInputSource);
        }
        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null || ex instanceof CharConversionException) {
                LocatorImpl locatorImpl = new LocatorImpl();
                locatorImpl.setPublicId(e.getPublicId());
                locatorImpl.setSystemId(e.getExpandedSystemId());
                locatorImpl.setLineNumber(e.getLineNumber());
                locatorImpl.setColumnNumber(e.getColumnNumber());
                throw ex == null ? new SAXParseException(e.getMessage(), locatorImpl) : new SAXParseException(e.getMessage(), locatorImpl, ex);
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
        catch (XNIException e) {
            Exception ex = e.getException();
            if (ex == null) {
                throw new SAXException(e.getMessage());
            }
            if (ex instanceof SAXException) {
                throw (SAXException)ex;
            }
            if (ex instanceof IOException) {
                throw (IOException)ex;
            }
            throw new SAXException(ex);
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        try {
            XMLErrorHandler xeh = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xeh instanceof ErrorHandlerWrapper) {
                ErrorHandlerWrapper ehw = (ErrorHandlerWrapper)xeh;
                ehw.setErrorHandler(errorHandler);
            } else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", new ErrorHandlerWrapper(errorHandler));
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xmlErrorHandler != null && xmlErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xmlErrorHandler).getErrorHandler();
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        return errorHandler;
    }

    public void setFeature(String featureId, boolean state) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fConfiguration.setFeature(featureId, state);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage("feature-not-recognized", new Object[]{identifier}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage("feature-not-supported", new Object[]{identifier}));
        }
    }

    public boolean getFeature(String featureId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            return this.fConfiguration.getFeature(featureId);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage("feature-not-recognized", new Object[]{identifier}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage("feature-not-supported", new Object[]{identifier}));
        }
    }

    public void setProperty(String propertyId, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fConfiguration.setProperty(propertyId, value);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage("property-not-recognized", new Object[]{identifier}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage("property-not-supported", new Object[]{identifier}));
        }
    }

    public XMLParserConfiguration getXMLParserConfiguration() {
        return this.fConfiguration;
    }
}

