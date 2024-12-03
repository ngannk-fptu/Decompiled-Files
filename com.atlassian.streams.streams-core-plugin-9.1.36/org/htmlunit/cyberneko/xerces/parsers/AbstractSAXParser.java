/*
 * Decompiled with CFR 0.152.
 */
package org.htmlunit.cyberneko.xerces.parsers;

import java.io.CharConversionException;
import java.io.IOException;
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
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.ext.Locator2Impl;

public abstract class AbstractSAXParser
extends AbstractXMLDocumentParser
implements XMLReader {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces"};
    protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://xml.org/sax/properties/lexical-handler"};
    protected boolean fNamespaces;
    protected boolean fNamespacePrefixes = false;
    protected boolean fLexicalHandlerParameterEntities = true;
    protected boolean fStandalone;
    protected boolean fUseEntityResolver2 = true;
    protected ContentHandler fContentHandler;
    protected NamespaceContext fNamespaceContext;
    protected DTDHandler fDTDHandler;
    protected LexicalHandler fLexicalHandler;
    protected final boolean fParseInProgress = false;
    protected String fVersion;
    private final AttributesProxy fAttributesProxy = new AttributesProxy();

    protected AbstractSAXParser(XMLParserConfiguration config) {
        super(config);
        config.addRecognizedFeatures(RECOGNIZED_FEATURES);
        config.addRecognizedProperties(RECOGNIZED_PROPERTIES);
    }

    @Override
    public void startDocument(XMLLocator locator, String encoding, NamespaceContext namespaceContext, Augmentations augs) throws XNIException {
        this.fNamespaceContext = namespaceContext;
        try {
            if (this.fContentHandler != null) {
                if (locator != null) {
                    this.fContentHandler.setDocumentLocator(new LocatorProxy(locator));
                }
                if (this.fContentHandler != null) {
                    this.fContentHandler.startDocument();
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void xmlDecl(String version, String encoding, String standalone, Augmentations augs) throws XNIException {
        this.fVersion = version;
        this.fStandalone = "yes".equals(standalone);
    }

    @Override
    public void doctypeDecl(String rootElement, String publicId, String systemId, Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startDTD(rootElement, publicId, systemId);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void startGeneralEntity(String name, String encoding, Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startEntity(name);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void endGeneralEntity(String name, Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endEntity(name);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void startElement(QName element, XMLAttributes attributes, Augmentations augs) throws XNIException {
        try {
            if (this.fContentHandler != null) {
                if (this.fNamespaces) {
                    this.startNamespaceMapping();
                }
                String uri = element.uri != null ? element.uri : "";
                String localpart = this.fNamespaces ? element.localpart : "";
                this.fAttributesProxy.setAttributes(attributes);
                this.fContentHandler.startElement(uri, localpart, element.rawname, this.fAttributesProxy);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void characters(XMLString text, Augmentations augs) throws XNIException {
        if (text.length() == 0) {
            return;
        }
        try {
            if (this.fContentHandler != null) {
                text.characters(this.fContentHandler);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString text, Augmentations augs) throws XNIException {
        try {
            if (this.fContentHandler != null) {
                text.ignorableWhitespace(this.fContentHandler);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void endElement(QName element, Augmentations augs) throws XNIException {
        try {
            if (this.fContentHandler != null) {
                String uri = element.uri != null ? element.uri : "";
                String localpart = this.fNamespaces ? element.localpart : "";
                this.fContentHandler.endElement(uri, localpart, element.rawname);
                if (this.fNamespaces) {
                    this.endNamespaceMapping();
                }
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void startCDATA(Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startCDATA();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void endCDATA(Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endCDATA();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void comment(XMLString text, Augmentations augs) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                text.comment(this.fLexicalHandler);
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void processingInstruction(String target, XMLString data, Augmentations augs) throws XNIException {
        try {
            if (this.fContentHandler != null) {
                this.fContentHandler.processingInstruction(target, data.toString());
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void endDocument(Augmentations augs) throws XNIException {
        try {
            if (this.fContentHandler != null) {
                this.fContentHandler.endDocument();
            }
        }
        catch (SAXException e) {
            throw new XNIException(e);
        }
    }

    @Override
    public void parse(String systemId) throws SAXException, IOException {
        XMLInputSource source = new XMLInputSource(null, systemId, null);
        try {
            this.parse(source);
        }
        catch (XMLParseException e) {
            Exception ex = e.getException();
            if (ex == null || ex instanceof CharConversionException) {
                Locator2Impl locatorImpl = new Locator2Impl();
                locatorImpl.setXMLVersion(this.fVersion);
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

    @Override
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
                Locator2Impl locatorImpl = new Locator2Impl();
                locatorImpl.setXMLVersion(this.fVersion);
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

    @Override
    public void setEntityResolver(EntityResolver resolver) {
    }

    @Override
    public EntityResolver getEntityResolver() {
        return null;
    }

    @Override
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

    @Override
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

    @Override
    public void setDTDHandler(DTDHandler dtdHandler) {
        this.fDTDHandler = dtdHandler;
    }

    @Override
    public void setContentHandler(ContentHandler contentHandler) {
        this.fContentHandler = contentHandler;
    }

    @Override
    public ContentHandler getContentHandler() {
        return this.fContentHandler;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    @Override
    public void setFeature(String featureId, boolean state) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (featureId.startsWith("http://xml.org/sax/features/")) {
                int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
                if (suffixLength == "namespaces".length() && featureId.endsWith("namespaces")) {
                    this.fConfiguration.setFeature(featureId, state);
                    this.fNamespaces = state;
                    return;
                }
                if (suffixLength == "namespace-prefixes".length() && featureId.endsWith("namespace-prefixes")) {
                    this.fNamespacePrefixes = state;
                    return;
                }
                if (suffixLength == "lexical-handler/parameter-entities".length() && featureId.endsWith("lexical-handler/parameter-entities")) {
                    this.fLexicalHandlerParameterEntities = state;
                    return;
                }
                if (suffixLength == "unicode-normalization-checking".length() && featureId.endsWith("unicode-normalization-checking")) {
                    if (state) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage("true-not-supported", new Object[]{featureId}));
                    }
                    return;
                }
            }
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

    @Override
    public boolean getFeature(String featureId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (featureId.startsWith("http://xml.org/sax/features/")) {
                int suffixLength = featureId.length() - "http://xml.org/sax/features/".length();
                if (suffixLength == "namespace-prefixes".length() && featureId.endsWith("namespace-prefixes")) {
                    return this.fNamespacePrefixes;
                }
                if (suffixLength == "lexical-handler/parameter-entities".length() && featureId.endsWith("lexical-handler/parameter-entities")) {
                    return this.fLexicalHandlerParameterEntities;
                }
                if (suffixLength == "unicode-normalization-checking".length() && featureId.endsWith("unicode-normalization-checking")) {
                    return false;
                }
            }
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

    @Override
    public void setProperty(String propertyId, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            int suffixLength;
            if (propertyId.startsWith("http://xml.org/sax/properties/") && (suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length()) == "lexical-handler".length() && propertyId.endsWith("lexical-handler")) {
                try {
                    this.setLexicalHandler((LexicalHandler)value);
                }
                catch (ClassCastException e) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage("incompatible-class", new Object[]{propertyId, "org.xml.sax.ext.LexicalHandler"}));
                }
                return;
            }
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

    @Override
    public Object getProperty(String propertyId) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (propertyId.startsWith("http://xml.org/sax/properties/")) {
                int suffixLength = propertyId.length() - "http://xml.org/sax/properties/".length();
                if (suffixLength == "document-xml-version".length() && propertyId.endsWith("document-xml-version")) {
                    return this.fVersion;
                }
                if (suffixLength == "lexical-handler".length() && propertyId.endsWith("lexical-handler")) {
                    return this.getLexicalHandler();
                }
            }
            return this.fConfiguration.getProperty(propertyId);
        }
        catch (XMLConfigurationException e) {
            String identifier = e.getIdentifier();
            if (e.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage("property-not-recognized", new Object[]{identifier}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage("property-not-supported", new Object[]{identifier}));
        }
    }

    protected void setLexicalHandler(LexicalHandler handler) throws SAXNotSupportedException {
        this.fLexicalHandler = handler;
    }

    protected LexicalHandler getLexicalHandler() {
        return this.fLexicalHandler;
    }

    protected final void startNamespaceMapping() throws SAXException {
        int count = this.fNamespaceContext.getDeclaredPrefixCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                String prefix;
                String uri = this.fNamespaceContext.getURI(prefix = this.fNamespaceContext.getDeclaredPrefixAt(i));
                this.fContentHandler.startPrefixMapping(prefix, uri == null ? "" : uri);
            }
        }
    }

    protected final void endNamespaceMapping() throws SAXException {
        int count = this.fNamespaceContext.getDeclaredPrefixCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
            }
        }
    }

    @Override
    public void reset() throws XNIException {
        super.reset();
        this.fVersion = "1.0";
        this.fStandalone = false;
        this.fNamespaces = this.fConfiguration.getFeature(NAMESPACES);
    }

    protected static final class AttributesProxy
    implements Attributes2 {
        private XMLAttributes fAttributes;

        protected AttributesProxy() {
        }

        public void setAttributes(XMLAttributes attributes) {
            this.fAttributes = attributes;
        }

        @Override
        public int getLength() {
            return this.fAttributes.getLength();
        }

        @Override
        public String getQName(int index) {
            return this.fAttributes.getQName(index);
        }

        @Override
        public String getURI(int index) {
            String uri = this.fAttributes.getURI(index);
            return uri != null ? uri : "";
        }

        @Override
        public String getLocalName(int index) {
            return this.fAttributes.getLocalName(index);
        }

        @Override
        public String getType(int i) {
            return this.fAttributes.getType(i);
        }

        @Override
        public String getType(String name) {
            return this.fAttributes.getType(name);
        }

        @Override
        public String getType(String uri, String localName) {
            return uri.length() == 0 ? this.fAttributes.getType(null, localName) : this.fAttributes.getType(uri, localName);
        }

        @Override
        public String getValue(int i) {
            return this.fAttributes.getValue(i);
        }

        @Override
        public String getValue(String name) {
            return this.fAttributes.getValue(name);
        }

        @Override
        public String getValue(String uri, String localName) {
            return uri.length() == 0 ? this.fAttributes.getValue(null, localName) : this.fAttributes.getValue(uri, localName);
        }

        @Override
        public int getIndex(String qName) {
            return this.fAttributes.getIndex(qName);
        }

        @Override
        public int getIndex(String uri, String localPart) {
            return uri.length() == 0 ? this.fAttributes.getIndex(null, localPart) : this.fAttributes.getIndex(uri, localPart);
        }

        @Override
        public boolean isDeclared(int index) {
            return false;
        }

        @Override
        public boolean isDeclared(String qName) {
            return false;
        }

        @Override
        public boolean isDeclared(String uri, String localName) {
            return false;
        }

        @Override
        public boolean isSpecified(int index) {
            if (index < 0 || index >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(index);
            }
            return this.fAttributes.isSpecified(index);
        }

        @Override
        public boolean isSpecified(String qName) {
            int index = this.getIndex(qName);
            if (index == -1) {
                throw new IllegalArgumentException(qName);
            }
            return this.fAttributes.isSpecified(index);
        }

        @Override
        public boolean isSpecified(String uri, String localName) {
            int index = this.getIndex(uri, localName);
            if (index == -1) {
                throw new IllegalArgumentException(localName);
            }
            return this.fAttributes.isSpecified(index);
        }
    }

    protected static final class LocatorProxy
    implements Locator2 {
        private final XMLLocator fLocator;

        public LocatorProxy(XMLLocator locator) {
            this.fLocator = locator;
        }

        @Override
        public String getPublicId() {
            return this.fLocator.getPublicId();
        }

        @Override
        public String getSystemId() {
            return this.fLocator.getExpandedSystemId();
        }

        @Override
        public int getLineNumber() {
            return this.fLocator.getLineNumber();
        }

        @Override
        public int getColumnNumber() {
            return this.fLocator.getColumnNumber();
        }

        @Override
        public String getXMLVersion() {
            return this.fLocator.getXMLVersion();
        }

        @Override
        public String getEncoding() {
            return this.fLocator.getEncoding();
        }
    }
}

