/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.parsers;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import org.htmlunit.cyberneko.HTMLConfiguration;
import org.htmlunit.cyberneko.xerces.util.ErrorHandlerWrapper;
import org.htmlunit.cyberneko.xerces.util.XMLChar;
import org.htmlunit.cyberneko.xerces.xni.Augmentations;
import org.htmlunit.cyberneko.xerces.xni.NamespaceContext;
import org.htmlunit.cyberneko.xerces.xni.QName;
import org.htmlunit.cyberneko.xerces.xni.XMLAttributes;
import org.htmlunit.cyberneko.xerces.xni.XMLDocumentHandler;
import org.htmlunit.cyberneko.xerces.xni.XMLLocator;
import org.htmlunit.cyberneko.xerces.xni.XMLString;
import org.htmlunit.cyberneko.xerces.xni.XNIException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLConfigurationException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLDocumentSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLErrorHandler;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLInputSource;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParseException;
import org.htmlunit.cyberneko.xerces.xni.parser.XMLParserConfiguration;
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
    protected static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/error-handler"};
    private final XMLParserConfiguration parserConfiguration_ = new HTMLConfiguration();
    private XMLDocumentSource documentSource_;
    private DocumentFragment documentFragment_;
    private Document document_;
    private Node currentNode_;
    private boolean inCDATASection_;

    public DOMFragmentParser() {
        this.parserConfiguration_.addRecognizedFeatures(RECOGNIZED_FEATURES);
        this.parserConfiguration_.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        this.parserConfiguration_.setFeature(DOCUMENT_FRAGMENT, true);
        this.parserConfiguration_.setDocumentHandler(this);
    }

    public void parse(String systemId, DocumentFragment fragment) throws SAXException, IOException {
        this.parse(new InputSource(systemId), fragment);
    }

    public void parse(InputSource source, DocumentFragment fragment) throws SAXException, IOException {
        this.currentNode_ = fragment;
        this.documentFragment_ = fragment;
        this.document_ = this.documentFragment_.getOwnerDocument();
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
            this.parserConfiguration_.parse(inputSource);
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
        this.parserConfiguration_.setErrorHandler(new ErrorHandlerWrapper(errorHandler));
    }

    public ErrorHandler getErrorHandler() {
        ErrorHandler errorHandler = null;
        try {
            XMLErrorHandler xmlErrorHandler = (XMLErrorHandler)this.parserConfiguration_.getProperty(ERROR_HANDLER);
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
            this.parserConfiguration_.setFeature(featureId, state);
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
            return this.parserConfiguration_.getFeature(featureId);
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
            this.parserConfiguration_.setProperty(propertyId, value);
        }
        catch (XMLConfigurationException e) {
            String message = e.getMessage();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(message);
            }
            throw new SAXNotSupportedException(message);
        }
    }

    @Override
    public void setDocumentSource(XMLDocumentSource source) {
        this.documentSource_ = source;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.documentSource_;
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext nscontext, Augmentations augs) throws XNIException {
        this.inCDATASection_ = false;
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
    }

    @Override
    public void doctypeDecl(String root, String pubid, String sysid, Augmentations augs) throws XNIException {
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        String s = data.toString();
        if (XMLChar.isValidName(s)) {
            ProcessingInstruction pi = this.document_.createProcessingInstruction(target, s);
            this.currentNode_.appendChild(pi);
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        Comment comment = this.document_.createComment(text.toString());
        this.currentNode_.appendChild(comment);
    }

    @Override
    public void startElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        Element elementNode = this.document_.createElement(element.rawname);
        if (attrs != null) {
            int count = attrs.getLength();
            for (int i = 0; i < count; ++i) {
                String aname = attrs.getQName(i);
                String avalue = attrs.getValue(i);
                if (!XMLChar.isValidName(aname)) continue;
                elementNode.setAttribute(aname, avalue);
            }
        }
        this.currentNode_.appendChild(elementNode);
        this.currentNode_ = elementNode;
    }

    @Override
    public void emptyElement(QName element, XMLAttributes attrs, Augmentations augs) throws XNIException {
        this.startElement(element, attrs, augs);
        this.endElement(element, augs);
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (this.inCDATASection_) {
            Node node = this.currentNode_.getLastChild();
            if (node != null && node.getNodeType() == 4) {
                CDATASection cdata = (CDATASection)node;
                cdata.appendData(text.toString());
            } else {
                CDATASection cdata = this.document_.createCDATASection(text.toString());
                this.currentNode_.appendChild(cdata);
            }
        } else {
            Node node = this.currentNode_.getLastChild();
            if (node != null && node.getNodeType() == 3) {
                Text textNode = (Text)node;
                textNode.appendData(text.toString());
            } else {
                Text textNode = this.document_.createTextNode(text.toString());
                this.currentNode_.appendChild(textNode);
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        this.characters(text, augs);
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
        EntityReference entityRef = this.document_.createEntityReference(name);
        this.currentNode_.appendChild(entityRef);
        this.currentNode_ = entityRef;
    }

    @Override
    public void textDecl(String version, String encoding, Augmentations augs) throws XNIException {
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        this.currentNode_ = this.currentNode_.getParentNode();
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        this.inCDATASection_ = true;
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        this.inCDATASection_ = false;
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        this.currentNode_ = this.currentNode_.getParentNode();
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
    }

    public XMLParserConfiguration getXMLParserConfiguration() {
        return this.parserConfiguration_;
    }
}

