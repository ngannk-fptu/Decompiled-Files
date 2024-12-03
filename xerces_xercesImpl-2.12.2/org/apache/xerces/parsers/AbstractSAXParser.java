/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.CharConversionException;
import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.parsers.AbstractXMLDocumentParser;
import org.apache.xerces.parsers.XML11Configurable;
import org.apache.xerces.util.EntityResolver2Wrapper;
import org.apache.xerces.util.EntityResolverWrapper;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.PSVIProvider;
import org.xml.sax.AttributeList;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.DocumentHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Parser;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ext.Locator2;
import org.xml.sax.ext.Locator2Impl;

public abstract class AbstractSAXParser
extends AbstractXMLDocumentParser
implements PSVIProvider,
Parser,
XMLReader {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/string-interning"};
    protected static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    protected static final String DECLARATION_HANDLER = "http://xml.org/sax/properties/declaration-handler";
    protected static final String DOM_NODE = "http://xml.org/sax/properties/dom-node";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/properties/declaration-handler", "http://xml.org/sax/properties/dom-node"};
    protected boolean fNamespaces;
    protected boolean fNamespacePrefixes = false;
    protected boolean fLexicalHandlerParameterEntities = true;
    protected boolean fStandalone;
    protected boolean fResolveDTDURIs = true;
    protected boolean fUseEntityResolver2 = true;
    protected boolean fXMLNSURIs = false;
    protected ContentHandler fContentHandler;
    protected DocumentHandler fDocumentHandler;
    protected NamespaceContext fNamespaceContext;
    protected DTDHandler fDTDHandler;
    protected DeclHandler fDeclHandler;
    protected LexicalHandler fLexicalHandler;
    protected final QName fQName = new QName();
    protected boolean fParseInProgress = false;
    protected String fVersion;
    private final AttributesProxy fAttributesProxy = new AttributesProxy();
    private Augmentations fAugmentations = null;
    protected SymbolHash fDeclaredAttrs = null;

    protected AbstractSAXParser(XMLParserConfiguration xMLParserConfiguration) {
        super(xMLParserConfiguration);
        xMLParserConfiguration.addRecognizedFeatures(RECOGNIZED_FEATURES);
        xMLParserConfiguration.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        try {
            xMLParserConfiguration.setFeature(ALLOW_UE_AND_NOTATION_EVENTS, false);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        this.fNamespaceContext = namespaceContext;
        try {
            if (this.fDocumentHandler != null) {
                if (xMLLocator != null) {
                    this.fDocumentHandler.setDocumentLocator(new LocatorProxy(xMLLocator));
                }
                if (this.fDocumentHandler != null) {
                    this.fDocumentHandler.startDocument();
                }
            }
            if (this.fContentHandler != null) {
                if (xMLLocator != null) {
                    this.fContentHandler.setDocumentLocator(new LocatorProxy(xMLLocator));
                }
                if (this.fContentHandler != null) {
                    this.fContentHandler.startDocument();
                }
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.fVersion = string;
        this.fStandalone = "yes".equals(string3);
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startDTD(string, string2, string3);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
        if (this.fDeclHandler != null) {
            this.fDeclaredAttrs = new SymbolHash(25);
        }
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        try {
            if (augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
                if (this.fContentHandler != null) {
                    this.fContentHandler.skippedEntity(string);
                }
            } else if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startEntity(string);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
        try {
            if (!(augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED")) || this.fLexicalHandler == null)) {
                this.fLexicalHandler.endEntity(string);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        block9: {
            try {
                if (this.fDocumentHandler != null) {
                    this.fAttributesProxy.setAttributes(xMLAttributes);
                    this.fDocumentHandler.startElement(qName.rawname, this.fAttributesProxy);
                }
                if (this.fContentHandler == null) break block9;
                if (this.fNamespaces) {
                    int n;
                    this.startNamespaceMapping();
                    int n2 = xMLAttributes.getLength();
                    if (!this.fNamespacePrefixes) {
                        for (n = n2 - 1; n >= 0; --n) {
                            xMLAttributes.getName(n, this.fQName);
                            if (this.fQName.prefix != XMLSymbols.PREFIX_XMLNS && this.fQName.rawname != XMLSymbols.PREFIX_XMLNS) continue;
                            xMLAttributes.removeAttributeAt(n);
                        }
                    } else if (!this.fXMLNSURIs) {
                        for (n = n2 - 1; n >= 0; --n) {
                            xMLAttributes.getName(n, this.fQName);
                            if (this.fQName.prefix != XMLSymbols.PREFIX_XMLNS && this.fQName.rawname != XMLSymbols.PREFIX_XMLNS) continue;
                            this.fQName.prefix = "";
                            this.fQName.uri = "";
                            this.fQName.localpart = "";
                            xMLAttributes.setName(n, this.fQName);
                        }
                    }
                }
                this.fAugmentations = augmentations;
                String string = qName.uri != null ? qName.uri : "";
                String string2 = this.fNamespaces ? qName.localpart : "";
                this.fAttributesProxy.setAttributes(xMLAttributes);
                this.fContentHandler.startElement(string, string2, qName.rawname, this.fAttributesProxy);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (xMLString.length == 0) {
            return;
        }
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.characters(xMLString.ch, xMLString.offset, xMLString.length);
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.characters(xMLString.ch, xMLString.offset, xMLString.length);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.ignorableWhitespace(xMLString.ch, xMLString.offset, xMLString.length);
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.ignorableWhitespace(xMLString.ch, xMLString.offset, xMLString.length);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endElement(qName.rawname);
            }
            if (this.fContentHandler != null) {
                this.fAugmentations = augmentations;
                String string = qName.uri != null ? qName.uri : "";
                String string2 = this.fNamespaces ? qName.localpart : "";
                this.fContentHandler.endElement(string, string2, qName.rawname);
                if (this.fNamespaces) {
                    this.endNamespaceMapping();
                }
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.startCDATA();
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endCDATA();
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.comment(xMLString.ch, 0, xMLString.length);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.processingInstruction(string, xMLString.toString());
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.processingInstruction(string, xMLString.toString());
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        try {
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endDocument();
            }
            if (this.fContentHandler != null) {
                this.fContentHandler.endDocument();
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        this.startParameterEntity("[dtd]", null, null, augmentations);
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
        this.endParameterEntity("[dtd]", augmentations);
    }

    @Override
    public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        try {
            if (augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
                if (this.fContentHandler != null) {
                    this.fContentHandler.skippedEntity(string);
                }
            } else if (this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
                this.fLexicalHandler.startEntity(string);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
        try {
            if ((augmentations == null || !Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) && this.fLexicalHandler != null && this.fLexicalHandlerParameterEntities) {
                this.fLexicalHandler.endEntity(string);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.elementDecl(string, string2);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                CharSequence charSequence;
                String string5 = new StringBuffer(string).append('<').append(string2).toString();
                if (this.fDeclaredAttrs.get(string5) != null) {
                    return;
                }
                this.fDeclaredAttrs.put(string5, Boolean.TRUE);
                if (string3.equals("NOTATION") || string3.equals("ENUMERATION")) {
                    charSequence = new StringBuffer();
                    if (string3.equals("NOTATION")) {
                        ((StringBuffer)charSequence).append(string3);
                        ((StringBuffer)charSequence).append(" (");
                    } else {
                        ((StringBuffer)charSequence).append('(');
                    }
                    for (int i = 0; i < stringArray.length; ++i) {
                        ((StringBuffer)charSequence).append(stringArray[i]);
                        if (i >= stringArray.length - 1) continue;
                        ((StringBuffer)charSequence).append('|');
                    }
                    ((StringBuffer)charSequence).append(')');
                    string3 = ((StringBuffer)charSequence).toString();
                }
                charSequence = xMLString == null ? null : xMLString.toString();
                this.fDeclHandler.attributeDecl(string, string2, string3, string4, (String)charSequence);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                this.fDeclHandler.internalEntityDecl(string, xMLString.toString());
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDeclHandler != null) {
                String string2 = xMLResourceIdentifier.getPublicId();
                String string3 = this.fResolveDTDURIs ? xMLResourceIdentifier.getExpandedSystemId() : xMLResourceIdentifier.getLiteralSystemId();
                this.fDeclHandler.externalEntityDecl(string, string2, string3);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDTDHandler != null) {
                String string3 = xMLResourceIdentifier.getPublicId();
                String string4 = this.fResolveDTDURIs ? xMLResourceIdentifier.getExpandedSystemId() : xMLResourceIdentifier.getLiteralSystemId();
                this.fDTDHandler.unparsedEntityDecl(string, string3, string4, string2);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        try {
            if (this.fDTDHandler != null) {
                String string2 = xMLResourceIdentifier.getPublicId();
                String string3 = this.fResolveDTDURIs ? xMLResourceIdentifier.getExpandedSystemId() : xMLResourceIdentifier.getLiteralSystemId();
                this.fDTDHandler.notationDecl(string, string2, string3);
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        this.fInDTD = false;
        try {
            if (this.fLexicalHandler != null) {
                this.fLexicalHandler.endDTD();
            }
        }
        catch (SAXException sAXException) {
            throw new XNIException(sAXException);
        }
        if (this.fDeclaredAttrs != null) {
            this.fDeclaredAttrs.clear();
        }
    }

    @Override
    public void parse(String string) throws SAXException, IOException {
        XMLInputSource xMLInputSource = new XMLInputSource(null, string, null);
        try {
            this.parse(xMLInputSource);
        }
        catch (XMLParseException xMLParseException) {
            Exception exception = xMLParseException.getException();
            if (exception == null || exception instanceof CharConversionException) {
                Locator2Impl locator2Impl = new Locator2Impl();
                locator2Impl.setXMLVersion(this.fVersion);
                locator2Impl.setPublicId(xMLParseException.getPublicId());
                locator2Impl.setSystemId(xMLParseException.getExpandedSystemId());
                locator2Impl.setLineNumber(xMLParseException.getLineNumber());
                locator2Impl.setColumnNumber(xMLParseException.getColumnNumber());
                throw exception == null ? new SAXParseException(xMLParseException.getMessage(), locator2Impl) : new SAXParseException(xMLParseException.getMessage(), locator2Impl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (XNIException xNIException) {
            Exception exception = xNIException.getException();
            if (exception == null) {
                throw new SAXException(xNIException.getMessage());
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
    }

    @Override
    public void parse(InputSource inputSource) throws SAXException, IOException {
        try {
            XMLInputSource xMLInputSource = new XMLInputSource(inputSource.getPublicId(), inputSource.getSystemId(), null);
            xMLInputSource.setByteStream(inputSource.getByteStream());
            xMLInputSource.setCharacterStream(inputSource.getCharacterStream());
            xMLInputSource.setEncoding(inputSource.getEncoding());
            this.parse(xMLInputSource);
        }
        catch (XMLParseException xMLParseException) {
            Exception exception = xMLParseException.getException();
            if (exception == null || exception instanceof CharConversionException) {
                Locator2Impl locator2Impl = new Locator2Impl();
                locator2Impl.setXMLVersion(this.fVersion);
                locator2Impl.setPublicId(xMLParseException.getPublicId());
                locator2Impl.setSystemId(xMLParseException.getExpandedSystemId());
                locator2Impl.setLineNumber(xMLParseException.getLineNumber());
                locator2Impl.setColumnNumber(xMLParseException.getColumnNumber());
                throw exception == null ? new SAXParseException(xMLParseException.getMessage(), locator2Impl) : new SAXParseException(xMLParseException.getMessage(), locator2Impl, exception);
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
        catch (XNIException xNIException) {
            Exception exception = xNIException.getException();
            if (exception == null) {
                throw new SAXException(xNIException.getMessage());
            }
            if (exception instanceof SAXException) {
                throw (SAXException)exception;
            }
            if (exception instanceof IOException) {
                throw (IOException)exception;
            }
            throw new SAXException(exception);
        }
    }

    @Override
    public void setEntityResolver(EntityResolver entityResolver) {
        try {
            XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (this.fUseEntityResolver2 && entityResolver instanceof EntityResolver2) {
                if (xMLEntityResolver instanceof EntityResolver2Wrapper) {
                    EntityResolver2Wrapper entityResolver2Wrapper = (EntityResolver2Wrapper)xMLEntityResolver;
                    entityResolver2Wrapper.setEntityResolver((EntityResolver2)entityResolver);
                } else {
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolver2Wrapper((EntityResolver2)entityResolver));
                }
            } else if (xMLEntityResolver instanceof EntityResolverWrapper) {
                EntityResolverWrapper entityResolverWrapper = (EntityResolverWrapper)xMLEntityResolver;
                entityResolverWrapper.setEntityResolver(entityResolver);
            } else {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new EntityResolverWrapper(entityResolver));
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    @Override
    public EntityResolver getEntityResolver() {
        EntityResolver entityResolver = null;
        try {
            XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
            if (xMLEntityResolver != null) {
                if (xMLEntityResolver instanceof EntityResolverWrapper) {
                    entityResolver = ((EntityResolverWrapper)xMLEntityResolver).getEntityResolver();
                } else if (xMLEntityResolver instanceof EntityResolver2Wrapper) {
                    entityResolver = ((EntityResolver2Wrapper)xMLEntityResolver).getEntityResolver();
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        return entityResolver;
    }

    @Override
    public void setErrorHandler(ErrorHandler errorHandler) {
        try {
            XMLErrorHandler xMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xMLErrorHandler instanceof ErrorHandlerWrapper) {
                ErrorHandlerWrapper errorHandlerWrapper = (ErrorHandlerWrapper)xMLErrorHandler;
                errorHandlerWrapper.setErrorHandler(errorHandler);
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
            XMLErrorHandler xMLErrorHandler = (XMLErrorHandler)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/error-handler");
            if (xMLErrorHandler != null && xMLErrorHandler instanceof ErrorHandlerWrapper) {
                errorHandler = ((ErrorHandlerWrapper)xMLErrorHandler).getErrorHandler();
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        return errorHandler;
    }

    @Override
    public void setLocale(Locale locale) throws SAXException {
        this.fConfiguration.setLocale(locale);
    }

    @Override
    public void setDTDHandler(DTDHandler dTDHandler) {
        this.fDTDHandler = dTDHandler;
    }

    @Override
    public void setDocumentHandler(DocumentHandler documentHandler) {
        this.fDocumentHandler = documentHandler;
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
    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (string.startsWith("http://xml.org/sax/features/")) {
                int n = string.length() - "http://xml.org/sax/features/".length();
                if (n == "namespaces".length() && string.endsWith("namespaces")) {
                    this.fConfiguration.setFeature(string, bl);
                    this.fNamespaces = bl;
                    return;
                }
                if (n == "namespace-prefixes".length() && string.endsWith("namespace-prefixes")) {
                    this.fNamespacePrefixes = bl;
                    return;
                }
                if (n == "string-interning".length() && string.endsWith("string-interning")) {
                    if (!bl) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "false-not-supported", new Object[]{string}));
                    }
                    return;
                }
                if (n == "lexical-handler/parameter-entities".length() && string.endsWith("lexical-handler/parameter-entities")) {
                    this.fLexicalHandlerParameterEntities = bl;
                    return;
                }
                if (n == "resolve-dtd-uris".length() && string.endsWith("resolve-dtd-uris")) {
                    this.fResolveDTDURIs = bl;
                    return;
                }
                if (n == "unicode-normalization-checking".length() && string.endsWith("unicode-normalization-checking")) {
                    if (bl) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "true-not-supported", new Object[]{string}));
                    }
                    return;
                }
                if (n == "xmlns-uris".length() && string.endsWith("xmlns-uris")) {
                    this.fXMLNSURIs = bl;
                    return;
                }
                if (n == "use-entity-resolver2".length() && string.endsWith("use-entity-resolver2")) {
                    if (bl != this.fUseEntityResolver2) {
                        this.fUseEntityResolver2 = bl;
                        this.setEntityResolver(this.getEntityResolver());
                    }
                    return;
                }
                if (n == "is-standalone".length() && string.endsWith("is-standalone") || n == "use-attributes2".length() && string.endsWith("use-attributes2") || n == "use-locator2".length() && string.endsWith("use-locator2") || n == "xml-1.1".length() && string.endsWith("xml-1.1")) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-read-only", new Object[]{string}));
                }
            }
            this.fConfiguration.setFeature(string, bl);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (string.startsWith("http://xml.org/sax/features/")) {
                int n = string.length() - "http://xml.org/sax/features/".length();
                if (n == "namespace-prefixes".length() && string.endsWith("namespace-prefixes")) {
                    return this.fNamespacePrefixes;
                }
                if (n == "string-interning".length() && string.endsWith("string-interning")) {
                    return true;
                }
                if (n == "is-standalone".length() && string.endsWith("is-standalone")) {
                    return this.fStandalone;
                }
                if (n == "xml-1.1".length() && string.endsWith("xml-1.1")) {
                    return this.fConfiguration instanceof XML11Configurable;
                }
                if (n == "lexical-handler/parameter-entities".length() && string.endsWith("lexical-handler/parameter-entities")) {
                    return this.fLexicalHandlerParameterEntities;
                }
                if (n == "resolve-dtd-uris".length() && string.endsWith("resolve-dtd-uris")) {
                    return this.fResolveDTDURIs;
                }
                if (n == "xmlns-uris".length() && string.endsWith("xmlns-uris")) {
                    return this.fXMLNSURIs;
                }
                if (n == "unicode-normalization-checking".length() && string.endsWith("unicode-normalization-checking")) {
                    return false;
                }
                if (n == "use-entity-resolver2".length() && string.endsWith("use-entity-resolver2")) {
                    return this.fUseEntityResolver2;
                }
                if (n == "use-attributes2".length() && string.endsWith("use-attributes2") || n == "use-locator2".length() && string.endsWith("use-locator2")) {
                    return true;
                }
            }
            return this.fConfiguration.getFeature(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (string.startsWith("http://xml.org/sax/properties/")) {
                int n = string.length() - "http://xml.org/sax/properties/".length();
                if (n == "lexical-handler".length() && string.endsWith("lexical-handler")) {
                    try {
                        this.setLexicalHandler((LexicalHandler)object);
                    }
                    catch (ClassCastException classCastException) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[]{string, "org.xml.sax.ext.LexicalHandler"}));
                    }
                    return;
                }
                if (n == "declaration-handler".length() && string.endsWith("declaration-handler")) {
                    try {
                        this.setDeclHandler((DeclHandler)object);
                    }
                    catch (ClassCastException classCastException) {
                        throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "incompatible-class", new Object[]{string, "org.xml.sax.ext.DeclHandler"}));
                    }
                    return;
                }
                if (n == "dom-node".length() && string.endsWith("dom-node") || n == "document-xml-version".length() && string.endsWith("document-xml-version")) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-read-only", new Object[]{string}));
                }
            }
            this.fConfiguration.setProperty(string, object);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        try {
            if (string.startsWith("http://xml.org/sax/properties/")) {
                int n = string.length() - "http://xml.org/sax/properties/".length();
                if (n == "document-xml-version".length() && string.endsWith("document-xml-version")) {
                    return this.fVersion;
                }
                if (n == "lexical-handler".length() && string.endsWith("lexical-handler")) {
                    return this.getLexicalHandler();
                }
                if (n == "declaration-handler".length() && string.endsWith("declaration-handler")) {
                    return this.getDeclHandler();
                }
                if (n == "dom-node".length() && string.endsWith("dom-node")) {
                    throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "dom-node-read-not-supported", null));
                }
            }
            return this.fConfiguration.getProperty(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    protected void setDeclHandler(DeclHandler declHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.fParseInProgress) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[]{DECLARATION_HANDLER}));
        }
        this.fDeclHandler = declHandler;
    }

    protected DeclHandler getDeclHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.fDeclHandler;
    }

    protected void setLexicalHandler(LexicalHandler lexicalHandler) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (this.fParseInProgress) {
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fConfiguration.getLocale(), "property-not-parsing-supported", new Object[]{LEXICAL_HANDLER}));
        }
        this.fLexicalHandler = lexicalHandler;
    }

    protected LexicalHandler getLexicalHandler() throws SAXNotRecognizedException, SAXNotSupportedException {
        return this.fLexicalHandler;
    }

    protected final void startNamespaceMapping() throws SAXException {
        int n = this.fNamespaceContext.getDeclaredPrefixCount();
        if (n > 0) {
            String string = null;
            String string2 = null;
            for (int i = 0; i < n; ++i) {
                string2 = this.fNamespaceContext.getURI(string = this.fNamespaceContext.getDeclaredPrefixAt(i));
                this.fContentHandler.startPrefixMapping(string, string2 == null ? "" : string2);
            }
        }
    }

    protected final void endNamespaceMapping() throws SAXException {
        int n = this.fNamespaceContext.getDeclaredPrefixCount();
        if (n > 0) {
            for (int i = 0; i < n; ++i) {
                this.fContentHandler.endPrefixMapping(this.fNamespaceContext.getDeclaredPrefixAt(i));
            }
        }
    }

    @Override
    public void reset() throws XNIException {
        super.reset();
        this.fInDTD = false;
        this.fVersion = "1.0";
        this.fStandalone = false;
        this.fNamespaces = this.fConfiguration.getFeature(NAMESPACES);
        this.fAugmentations = null;
        this.fDeclaredAttrs = null;
    }

    @Override
    public ElementPSVI getElementPSVI() {
        return this.fAugmentations != null ? (ElementPSVI)this.fAugmentations.getItem("ELEMENT_PSVI") : null;
    }

    @Override
    public AttributePSVI getAttributePSVI(int n) {
        return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_PSVI");
    }

    @Override
    public AttributePSVI getAttributePSVIByName(String string, String string2) {
        return (AttributePSVI)this.fAttributesProxy.fAttributes.getAugmentations(string, string2).getItem("ATTRIBUTE_PSVI");
    }

    protected static final class AttributesProxy
    implements AttributeList,
    Attributes2 {
        protected XMLAttributes fAttributes;

        protected AttributesProxy() {
        }

        public void setAttributes(XMLAttributes xMLAttributes) {
            this.fAttributes = xMLAttributes;
        }

        @Override
        public int getLength() {
            return this.fAttributes.getLength();
        }

        @Override
        public String getName(int n) {
            return this.fAttributes.getQName(n);
        }

        @Override
        public String getQName(int n) {
            return this.fAttributes.getQName(n);
        }

        @Override
        public String getURI(int n) {
            String string = this.fAttributes.getURI(n);
            return string != null ? string : "";
        }

        @Override
        public String getLocalName(int n) {
            return this.fAttributes.getLocalName(n);
        }

        @Override
        public String getType(int n) {
            return this.fAttributes.getType(n);
        }

        @Override
        public String getType(String string) {
            return this.fAttributes.getType(string);
        }

        @Override
        public String getType(String string, String string2) {
            return string.length() == 0 ? this.fAttributes.getType(null, string2) : this.fAttributes.getType(string, string2);
        }

        @Override
        public String getValue(int n) {
            return this.fAttributes.getValue(n);
        }

        @Override
        public String getValue(String string) {
            return this.fAttributes.getValue(string);
        }

        @Override
        public String getValue(String string, String string2) {
            return string.length() == 0 ? this.fAttributes.getValue(null, string2) : this.fAttributes.getValue(string, string2);
        }

        @Override
        public int getIndex(String string) {
            return this.fAttributes.getIndex(string);
        }

        @Override
        public int getIndex(String string, String string2) {
            return string.length() == 0 ? this.fAttributes.getIndex(null, string2) : this.fAttributes.getIndex(string, string2);
        }

        @Override
        public boolean isDeclared(int n) {
            if (n < 0 || n >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
        }

        @Override
        public boolean isDeclared(String string) {
            int n = this.getIndex(string);
            if (n == -1) {
                throw new IllegalArgumentException(string);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
        }

        @Override
        public boolean isDeclared(String string, String string2) {
            int n = this.getIndex(string, string2);
            if (n == -1) {
                throw new IllegalArgumentException(string2);
            }
            return Boolean.TRUE.equals(this.fAttributes.getAugmentations(n).getItem("ATTRIBUTE_DECLARED"));
        }

        @Override
        public boolean isSpecified(int n) {
            if (n < 0 || n >= this.fAttributes.getLength()) {
                throw new ArrayIndexOutOfBoundsException(n);
            }
            return this.fAttributes.isSpecified(n);
        }

        @Override
        public boolean isSpecified(String string) {
            int n = this.getIndex(string);
            if (n == -1) {
                throw new IllegalArgumentException(string);
            }
            return this.fAttributes.isSpecified(n);
        }

        @Override
        public boolean isSpecified(String string, String string2) {
            int n = this.getIndex(string, string2);
            if (n == -1) {
                throw new IllegalArgumentException(string2);
            }
            return this.fAttributes.isSpecified(n);
        }
    }

    protected static final class LocatorProxy
    implements Locator2 {
        protected XMLLocator fLocator;

        public LocatorProxy(XMLLocator xMLLocator) {
            this.fLocator = xMLLocator;
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

