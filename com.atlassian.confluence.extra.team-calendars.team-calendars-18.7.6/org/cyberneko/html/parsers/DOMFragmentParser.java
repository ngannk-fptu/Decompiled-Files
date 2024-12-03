/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xerces.util.ErrorHandlerWrapper
 *  org.apache.xerces.util.XMLChar
 *  org.apache.xerces.xni.Augmentations
 *  org.apache.xerces.xni.NamespaceContext
 *  org.apache.xerces.xni.QName
 *  org.apache.xerces.xni.XMLAttributes
 *  org.apache.xerces.xni.XMLDocumentHandler
 *  org.apache.xerces.xni.XMLLocator
 *  org.apache.xerces.xni.XMLResourceIdentifier
 *  org.apache.xerces.xni.XMLString
 *  org.apache.xerces.xni.XNIException
 *  org.apache.xerces.xni.parser.XMLConfigurationException
 *  org.apache.xerces.xni.parser.XMLDocumentSource
 *  org.apache.xerces.xni.parser.XMLErrorHandler
 *  org.apache.xerces.xni.parser.XMLInputSource
 *  org.apache.xerces.xni.parser.XMLParseException
 *  org.apache.xerces.xni.parser.XMLParserConfiguration
 */
package org.cyberneko.html.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.cyberneko.html.HTMLConfiguration;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.Text;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;

public class DOMFragmentParser
implements XMLDocumentHandler {
    protected static final String DOCUMENT_FRAGMENT = "http://cyberneko.org/html/features/document-fragment";
    protected static final String[] RECOGNIZED_FEATURES = new String[]{"http://cyberneko.org/html/features/document-fragment"};
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String CURRENT_ELEMENT_NODE = "http://apache.org/xml/properties/dom/current-element-node";
    protected static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/dom/current-element-node"};
    protected XMLParserConfiguration fParserConfiguration = new HTMLConfiguration();
    protected XMLDocumentSource fDocumentSource;
    protected DocumentFragment fDocumentFragment;
    protected Document fDocument;
    protected Node fCurrentNode;
    protected boolean fInCDATASection;

    public DOMFragmentParser() {
        this.fParserConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
        this.fParserConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        this.fParserConfiguration.setFeature(DOCUMENT_FRAGMENT, true);
        this.fParserConfiguration.setDocumentHandler((XMLDocumentHandler)this);
    }

    public void parse(String systemId, DocumentFragment fragment) throws SAXException, IOException {
        this.parse(new InputSource(systemId), fragment);
    }

    public void parse(InputSource source, DocumentFragment fragment) throws SAXException, IOException {
        this.fDocumentFragment = fragment;
        this.fCurrentNode = this.fDocumentFragment;
        this.fDocument = this.fDocumentFragment.getOwnerDocument();
        try {
            String pubid = source.getPublicId();
            String sysid = source.getSystemId();
            String encoding = source.getEncoding();
            InputStream stream = source.getByteStream();
            Reader reader = source.getCharacterStream();
            XMLInputSource inputSource = new XMLInputSource(pubid, sysid, sysid);
            inputSource.setEncoding(encoding);
            inputSource.setByteStream(stream);
            inputSource.setCharacterStream(reader);
            this.fParserConfiguration.parse(inputSource);
        }
        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex != null) {
                throw new SAXParseException(e.getMessage(), null, ex);
            }
            throw new SAXParseException(e.getMessage(), null);
        }
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.fParserConfiguration.setErrorHandler((XMLErrorHandler)new ErrorHandlerWrapper(errorHandler));
    }

    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.fParserConfiguration.getProperty(ERROR_HANDLER);
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
            this.fParserConfiguration.setFeature(featureId, state);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }

    public boolean getFeature(String featureId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            return this.fParserConfiguration.getFeature(featureId);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }

    public void setProperty(String propertyId, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            this.fParserConfiguration.setProperty(propertyId, value);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }

    public Object getProperty(String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (propertyId.equals(CURRENT_ELEMENT_NODE)) {
            return this.fCurrentNode != null && this.fCurrentNode.getNodeType() == 1 ? this.fCurrentNode : null;
        }
        try {
            return this.fParserConfiguration.getProperty(propertyId);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }

    public void setDocumentSource(XMLDocumentSource source) {
        this.fDocumentSource = source;
    }

    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    public void startDocument(XMLLocator locator, String encoding, Augmentations augs) throws XNIException {
        this.startDocument(locator, encoding, null, augs);
    }

    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
    }

    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
    }

    public void doctypeDecl(String root, String pubid, String sysid, Augmentations augs) throws XNIException {
    }

    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        String s = data.toString();
        if (XMLChar.isValidName((String)s)) {
            ProcessingInstruction pi = this.fDocument.createProcessingInstruction(target, s);
            this.fCurrentNode.appendChild(pi);
        }
    }

    public void comment(XMLString text, Augmentations augs) throws XNIException {
        Comment comment = this.fDocument.createComment(text.toString());
        this.fCurrentNode.appendChild(comment);
    }

    public void startPrefixMapping(String prefix, String uri, Augmentations augs) throws XNIException {
    }

    public void endPrefixMapping(String prefix, Augmentations augs) throws XNIException {
    }

    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        Element elementNode = this.fDocument.createElement(element.rawname);
        int count = attrs != null ? attrs.getLength() : 0;
        for (int i = 0; i < count; ++i) {
            String aname = attrs.getQName(i);
            String avalue = attrs.getValue(i);
            if (!XMLChar.isValidName((String)aname)) continue;
            elementNode.setAttribute(aname, avalue);
        }
        this.fCurrentNode.appendChild(elementNode);
        this.fCurrentNode = elementNode;
    }

    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.startElement(element, attrs, augs);
        this.endElement(element, augs);
    }

    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.fInCDATASection) {
            Node node = this.fCurrentNode.getLastChild();
            if (node != null && node.getNodeType() == 4) {
                CDATASection cdata = (CDATASection)node;
                cdata.appendData(text.toString());
            } else {
                CDATASection cdata = this.fDocument.createCDATASection(text.toString());
                this.fCurrentNode.appendChild(cdata);
            }
        } else {
            Node node = this.fCurrentNode.getLastChild();
            if (node != null && node.getNodeType() == 3) {
                Text textNode = (Text)node;
                textNode.appendData(text.toString());
            } else {
                Text textNode = this.fDocument.createTextNode(text.toString());
                this.fCurrentNode.appendChild(textNode);
            }
        }
    }

    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }

    public void startGeneralEntity(String name, XMLResourceIdentifier id, String encoding, Augmentations augs) throws XNIException {
        EntityReference entityRef = this.fDocument.createEntityReference(name);
        this.fCurrentNode.appendChild(entityRef);
        this.fCurrentNode = entityRef;
    }

    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
    }

    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }

    public void startCDATA(Augmentations augs) throws XNIException {
        this.fInCDATASection = true;
    }

    public void endCDATA(Augmentations augs) throws XNIException {
        this.fInCDATASection = false;
    }

    public void endElement(QName element, Augmentations augs) throws XNIException {
        this.fCurrentNode = this.fCurrentNode.getParentNode();
    }

    public void endDocument(Augmentations augs) throws XNIException {
    }
}

