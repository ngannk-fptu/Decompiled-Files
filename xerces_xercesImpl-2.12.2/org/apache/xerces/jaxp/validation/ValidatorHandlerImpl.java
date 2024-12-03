/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.jaxp.validation;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.HashMap;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXResult;
import javax.xml.transform.sax.SAXSource;
import javax.xml.validation.TypeInfoProvider;
import javax.xml.validation.ValidatorHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.validation.EntityState;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.xs.XMLSchemaValidator;
import org.apache.xerces.jaxp.validation.DraconianErrorHandler;
import org.apache.xerces.jaxp.validation.JAXPValidationMessageFormatter;
import org.apache.xerces.jaxp.validation.Util;
import org.apache.xerces.jaxp.validation.ValidatorHelper;
import org.apache.xerces.jaxp.validation.XMLSchemaValidatorComponentManager;
import org.apache.xerces.jaxp.validation.XSGrammarPoolContainer;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.SAXLocatorWrapper;
import org.apache.xerces.util.SAXMessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLSymbols;
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
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xs.AttributePSVI;
import org.apache.xerces.xs.ElementPSVI;
import org.apache.xerces.xs.ItemPSVI;
import org.apache.xerces.xs.PSVIProvider;
import org.apache.xerces.xs.XSTypeDefinition;
import org.w3c.dom.TypeInfo;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.ext.Attributes2;
import org.xml.sax.ext.EntityResolver2;
import org.xml.sax.ext.LexicalHandler;

final class ValidatorHandlerImpl
extends ValidatorHandler
implements DTDHandler,
EntityState,
PSVIProvider,
ValidatorHelper,
XMLDocumentHandler {
    private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    private static final String STRINGS_INTERNED = "http://apache.org/xml/features/internal/strings-interned";
    private static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    private static final String LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private static final String NAMESPACE_CONTEXT = "http://apache.org/xml/properties/internal/namespace-context";
    private static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    private static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    private static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    private static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private final XMLErrorReporter fErrorReporter;
    private final NamespaceContext fNamespaceContext;
    private final XMLSchemaValidator fSchemaValidator;
    private final SymbolTable fSymbolTable;
    private final ValidationManager fValidationManager;
    private final XMLSchemaValidatorComponentManager fComponentManager;
    private final SAXLocatorWrapper fSAXLocatorWrapper = new SAXLocatorWrapper();
    private boolean fNeedPushNSContext = true;
    private HashMap fUnparsedEntities = null;
    private boolean fStringsInternalized = false;
    private final QName fElementQName = new QName();
    private final QName fAttributeQName = new QName();
    private final XMLAttributesImpl fAttributes = new XMLAttributesImpl();
    private final AttributesProxy fAttrAdapter = new AttributesProxy(this.fAttributes);
    private final XMLString fTempString = new XMLString();
    private ContentHandler fContentHandler = null;
    private final XMLSchemaTypeInfoProvider fTypeInfoProvider = new XMLSchemaTypeInfoProvider();
    private final ResolutionForwarder fResolutionForwarder = new ResolutionForwarder(null);

    public ValidatorHandlerImpl(XSGrammarPoolContainer xSGrammarPoolContainer) {
        this(new XMLSchemaValidatorComponentManager(xSGrammarPoolContainer));
        this.fComponentManager.addRecognizedFeatures(new String[]{NAMESPACE_PREFIXES});
        this.fComponentManager.setFeature(NAMESPACE_PREFIXES, false);
        this.setErrorHandler(null);
        this.setResourceResolver(null);
    }

    public ValidatorHandlerImpl(XMLSchemaValidatorComponentManager xMLSchemaValidatorComponentManager) {
        this.fComponentManager = xMLSchemaValidatorComponentManager;
        this.fErrorReporter = (XMLErrorReporter)this.fComponentManager.getProperty(ERROR_REPORTER);
        this.fNamespaceContext = (NamespaceContext)this.fComponentManager.getProperty(NAMESPACE_CONTEXT);
        this.fSchemaValidator = (XMLSchemaValidator)this.fComponentManager.getProperty(SCHEMA_VALIDATOR);
        this.fSymbolTable = (SymbolTable)this.fComponentManager.getProperty(SYMBOL_TABLE);
        this.fValidationManager = (ValidationManager)this.fComponentManager.getProperty(VALIDATION_MANAGER);
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
    public void setErrorHandler(ErrorHandler errorHandler) {
        this.fComponentManager.setErrorHandler(errorHandler);
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this.fComponentManager.getErrorHandler();
    }

    @Override
    public void setResourceResolver(LSResourceResolver lSResourceResolver) {
        this.fComponentManager.setResourceResolver(lSResourceResolver);
    }

    @Override
    public LSResourceResolver getResourceResolver() {
        return this.fComponentManager.getResourceResolver();
    }

    @Override
    public TypeInfoProvider getTypeInfoProvider() {
        return this.fTypeInfoProvider;
    }

    @Override
    public boolean getFeature(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if (STRINGS_INTERNED.equals(string)) {
            return this.fStringsInternalized;
        }
        try {
            return this.fComponentManager.getFeature(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setFeature(String string, boolean bl) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "FeatureNameNull", null));
        }
        if (STRINGS_INTERNED.equals(string)) {
            this.fStringsInternalized = bl;
            return;
        }
        try {
            this.fComponentManager.setFeature(string, bl);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "feature-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public Object getProperty(String string) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        try {
            return this.fComponentManager.getProperty(string);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public void setProperty(String string, Object object) throws SAXNotRecognizedException, SAXNotSupportedException {
        if (string == null) {
            throw new NullPointerException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "ProperyNameNull", null));
        }
        try {
            this.fComponentManager.setProperty(string, object);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            String string2 = xMLConfigurationException.getIdentifier();
            if (xMLConfigurationException.getType() == 0) {
                throw new SAXNotRecognizedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-recognized", new Object[]{string2}));
            }
            throw new SAXNotSupportedException(SAXMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "property-not-supported", new Object[]{string2}));
        }
    }

    @Override
    public boolean isEntityDeclared(String string) {
        return false;
    }

    @Override
    public boolean isEntityUnparsed(String string) {
        if (this.fUnparsedEntities != null) {
            return this.fUnparsedEntities.containsKey(string);
        }
        return false;
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.startDocument();
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
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
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.processingInstruction(string, xMLString.toString());
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fTypeInfoProvider.beginStartElement(augmentations, xMLAttributes);
                this.fContentHandler.startElement(qName.uri != null ? qName.uri : XMLSymbols.EMPTY_STRING, qName.localpart, qName.rawname, this.fAttrAdapter);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
            finally {
                this.fTypeInfoProvider.finishStartElement();
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
        if (this.fContentHandler != null) {
            if (xMLString.length == 0) {
                return;
            }
            try {
                this.fContentHandler.characters(xMLString.ch, xMLString.offset, xMLString.length);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.ignorableWhitespace(xMLString.ch, xMLString.offset, xMLString.length);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        if (this.fContentHandler != null) {
            try {
                this.fTypeInfoProvider.beginEndElement(augmentations);
                this.fContentHandler.endElement(qName.uri != null ? qName.uri : XMLSymbols.EMPTY_STRING, qName.localpart, qName.rawname);
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
            finally {
                this.fTypeInfoProvider.finishEndElement();
            }
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
        if (this.fContentHandler != null) {
            try {
                this.fContentHandler.endDocument();
            }
            catch (SAXException sAXException) {
                throw new XNIException(sAXException);
            }
        }
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fSchemaValidator;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.fSAXLocatorWrapper.setLocator(locator);
        if (this.fContentHandler != null) {
            this.fContentHandler.setDocumentLocator(locator);
        }
    }

    @Override
    public void startDocument() throws SAXException {
        this.fComponentManager.reset();
        this.fSchemaValidator.setDocumentHandler(this);
        this.fValidationManager.setEntityState(this);
        this.fTypeInfoProvider.finishStartElement();
        this.fNeedPushNSContext = true;
        if (this.fUnparsedEntities != null && !this.fUnparsedEntities.isEmpty()) {
            this.fUnparsedEntities.clear();
        }
        this.fErrorReporter.setDocumentLocator(this.fSAXLocatorWrapper);
        try {
            this.fSchemaValidator.startDocument(this.fSAXLocatorWrapper, this.fSAXLocatorWrapper.getEncoding(), this.fNamespaceContext, null);
        }
        catch (XMLParseException xMLParseException) {
            throw Util.toSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        this.fSAXLocatorWrapper.setLocator(null);
        try {
            this.fSchemaValidator.endDocument(null);
        }
        catch (XMLParseException xMLParseException) {
            throw Util.toSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
    }

    @Override
    public void startPrefixMapping(String string, String string2) throws SAXException {
        String string3;
        String string4;
        if (!this.fStringsInternalized) {
            string4 = string != null ? this.fSymbolTable.addSymbol(string) : XMLSymbols.EMPTY_STRING;
            string3 = string2 != null && string2.length() > 0 ? this.fSymbolTable.addSymbol(string2) : null;
        } else {
            string4 = string != null ? string : XMLSymbols.EMPTY_STRING;
            String string5 = string3 = string2 != null && string2.length() > 0 ? string2 : null;
        }
        if (this.fNeedPushNSContext) {
            this.fNeedPushNSContext = false;
            this.fNamespaceContext.pushContext();
        }
        this.fNamespaceContext.declarePrefix(string4, string3);
        if (this.fContentHandler != null) {
            this.fContentHandler.startPrefixMapping(string, string2);
        }
    }

    @Override
    public void endPrefixMapping(String string) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.endPrefixMapping(string);
        }
    }

    @Override
    public void startElement(String string, String string2, String string3, Attributes attributes) throws SAXException {
        if (this.fNeedPushNSContext) {
            this.fNamespaceContext.pushContext();
        }
        this.fNeedPushNSContext = true;
        this.fillQName(this.fElementQName, string, string2, string3);
        if (attributes instanceof Attributes2) {
            this.fillXMLAttributes2((Attributes2)attributes);
        } else {
            this.fillXMLAttributes(attributes);
        }
        try {
            this.fSchemaValidator.startElement(this.fElementQName, this.fAttributes, null);
        }
        catch (XMLParseException xMLParseException) {
            throw Util.toSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
    }

    @Override
    public void endElement(String string, String string2, String string3) throws SAXException {
        this.fillQName(this.fElementQName, string, string2, string3);
        try {
            this.fSchemaValidator.endElement(this.fElementQName, null);
        }
        catch (XMLParseException xMLParseException) {
            throw Util.toSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
        finally {
            this.fNamespaceContext.popContext();
        }
    }

    @Override
    public void characters(char[] cArray, int n, int n2) throws SAXException {
        try {
            this.fTempString.setValues(cArray, n, n2);
            this.fSchemaValidator.characters(this.fTempString, null);
        }
        catch (XMLParseException xMLParseException) {
            throw Util.toSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
    }

    @Override
    public void ignorableWhitespace(char[] cArray, int n, int n2) throws SAXException {
        try {
            this.fTempString.setValues(cArray, n, n2);
            this.fSchemaValidator.ignorableWhitespace(this.fTempString, null);
        }
        catch (XMLParseException xMLParseException) {
            throw Util.toSAXParseException(xMLParseException);
        }
        catch (XNIException xNIException) {
            throw Util.toSAXException(xNIException);
        }
    }

    @Override
    public void processingInstruction(String string, String string2) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.processingInstruction(string, string2);
        }
    }

    @Override
    public void skippedEntity(String string) throws SAXException {
        if (this.fContentHandler != null) {
            this.fContentHandler.skippedEntity(string);
        }
    }

    @Override
    public void notationDecl(String string, String string2, String string3) throws SAXException {
    }

    @Override
    public void unparsedEntityDecl(String string, String string2, String string3, String string4) throws SAXException {
        if (this.fUnparsedEntities == null) {
            this.fUnparsedEntities = new HashMap();
        }
        this.fUnparsedEntities.put(string, string);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void validate(Source source, Result result) throws SAXException, IOException {
        if (result instanceof SAXResult || result == null) {
            Object object;
            SAXSource sAXSource = (SAXSource)source;
            SAXResult sAXResult = (SAXResult)result;
            LexicalHandler lexicalHandler = null;
            if (result != null) {
                object = sAXResult.getHandler();
                lexicalHandler = sAXResult.getLexicalHandler();
                if (lexicalHandler == null && object instanceof LexicalHandler) {
                    lexicalHandler = (LexicalHandler)object;
                }
                this.setContentHandler((ContentHandler)object);
            }
            object = null;
            try {
                Object object2;
                Object object3;
                object = sAXSource.getXMLReader();
                if (object == null) {
                    object3 = SAXParserFactory.newInstance();
                    ((SAXParserFactory)object3).setNamespaceAware(true);
                    try {
                        object = ((SAXParserFactory)object3).newSAXParser().getXMLReader();
                        if (object instanceof SAXParser && (object2 = this.fComponentManager.getProperty(SECURITY_MANAGER)) != null) {
                            try {
                                object.setProperty(SECURITY_MANAGER, object2);
                            }
                            catch (SAXException sAXException) {}
                        }
                    }
                    catch (Exception exception) {
                        throw new FactoryConfigurationError(exception);
                    }
                }
                try {
                    this.fStringsInternalized = object.getFeature(STRING_INTERNING);
                }
                catch (SAXException sAXException) {
                    this.fStringsInternalized = false;
                }
                object3 = this.fComponentManager.getErrorHandler();
                object.setErrorHandler((ErrorHandler)(object3 != null ? object3 : DraconianErrorHandler.getInstance()));
                object.setEntityResolver(this.fResolutionForwarder);
                this.fResolutionForwarder.setEntityResolver(this.fComponentManager.getResourceResolver());
                object.setContentHandler(this);
                object.setDTDHandler(this);
                try {
                    object.setProperty(LEXICAL_HANDLER, lexicalHandler);
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
                object2 = sAXSource.getInputSource();
                object.parse((InputSource)object2);
            }
            finally {
                this.setContentHandler(null);
                if (object != null) {
                    try {
                        object.setContentHandler(null);
                        object.setDTDHandler(null);
                        object.setErrorHandler(null);
                        object.setEntityResolver(null);
                        this.fResolutionForwarder.setEntityResolver(null);
                        object.setProperty(LEXICAL_HANDLER, null);
                    }
                    catch (Exception exception) {}
                }
            }
            return;
        }
        throw new IllegalArgumentException(JAXPValidationMessageFormatter.formatMessage(this.fComponentManager.getLocale(), "SourceResultMismatch", new Object[]{source.getClass().getName(), result.getClass().getName()}));
    }

    @Override
    public ElementPSVI getElementPSVI() {
        return this.fTypeInfoProvider.getElementPSVI();
    }

    @Override
    public AttributePSVI getAttributePSVI(int n) {
        return this.fTypeInfoProvider.getAttributePSVI(n);
    }

    @Override
    public AttributePSVI getAttributePSVIByName(String string, String string2) {
        return this.fTypeInfoProvider.getAttributePSVIByName(string, string2);
    }

    private void fillQName(QName qName, String string, String string2, String string3) {
        if (!this.fStringsInternalized) {
            string = string != null && string.length() > 0 ? this.fSymbolTable.addSymbol(string) : null;
            string2 = string2 != null ? this.fSymbolTable.addSymbol(string2) : XMLSymbols.EMPTY_STRING;
            string3 = string3 != null ? this.fSymbolTable.addSymbol(string3) : XMLSymbols.EMPTY_STRING;
        } else {
            if (string != null && string.length() == 0) {
                string = null;
            }
            if (string2 == null) {
                string2 = XMLSymbols.EMPTY_STRING;
            }
            if (string3 == null) {
                string3 = XMLSymbols.EMPTY_STRING;
            }
        }
        String string4 = XMLSymbols.EMPTY_STRING;
        int n = string3.indexOf(58);
        if (n != -1) {
            string4 = this.fSymbolTable.addSymbol(string3.substring(0, n));
        }
        qName.setValues(string4, string2, string3, string);
    }

    private void fillXMLAttributes(Attributes attributes) {
        this.fAttributes.removeAllAttributes();
        int n = attributes.getLength();
        for (int i = 0; i < n; ++i) {
            this.fillXMLAttribute(attributes, i);
            this.fAttributes.setSpecified(i, true);
        }
    }

    private void fillXMLAttributes2(Attributes2 attributes2) {
        this.fAttributes.removeAllAttributes();
        int n = attributes2.getLength();
        for (int i = 0; i < n; ++i) {
            this.fillXMLAttribute(attributes2, i);
            this.fAttributes.setSpecified(i, attributes2.isSpecified(i));
            if (!attributes2.isDeclared(i)) continue;
            this.fAttributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
        }
    }

    private void fillXMLAttribute(Attributes attributes, int n) {
        this.fillQName(this.fAttributeQName, attributes.getURI(n), attributes.getLocalName(n), attributes.getQName(n));
        String string = attributes.getType(n);
        this.fAttributes.addAttributeNS(this.fAttributeQName, string != null ? string : XMLSymbols.fCDATASymbol, attributes.getValue(n));
    }

    static final class ResolutionForwarder
    implements EntityResolver2 {
        private static final String XML_TYPE = "http://www.w3.org/TR/REC-xml";
        protected LSResourceResolver fEntityResolver;

        public ResolutionForwarder() {
        }

        public ResolutionForwarder(LSResourceResolver lSResourceResolver) {
            this.setEntityResolver(lSResourceResolver);
        }

        public void setEntityResolver(LSResourceResolver lSResourceResolver) {
            this.fEntityResolver = lSResourceResolver;
        }

        public LSResourceResolver getEntityResolver() {
            return this.fEntityResolver;
        }

        @Override
        public InputSource getExternalSubset(String string, String string2) throws SAXException, IOException {
            return null;
        }

        @Override
        public InputSource resolveEntity(String string, String string2, String string3, String string4) throws SAXException, IOException {
            LSInput lSInput;
            if (this.fEntityResolver != null && (lSInput = this.fEntityResolver.resolveResource(XML_TYPE, null, string2, string4, string3)) != null) {
                String string5 = lSInput.getPublicId();
                String string6 = lSInput.getSystemId();
                String string7 = lSInput.getBaseURI();
                Reader reader = lSInput.getCharacterStream();
                InputStream inputStream = lSInput.getByteStream();
                String string8 = lSInput.getStringData();
                String string9 = lSInput.getEncoding();
                InputSource inputSource = new InputSource();
                inputSource.setPublicId(string5);
                inputSource.setSystemId(string7 != null ? this.resolveSystemId(string6, string7) : string6);
                if (reader != null) {
                    inputSource.setCharacterStream(reader);
                } else if (inputStream != null) {
                    inputSource.setByteStream(inputStream);
                } else if (string8 != null && string8.length() != 0) {
                    inputSource.setCharacterStream(new StringReader(string8));
                }
                inputSource.setEncoding(string9);
                return inputSource;
            }
            return null;
        }

        @Override
        public InputSource resolveEntity(String string, String string2) throws SAXException, IOException {
            return this.resolveEntity(null, string, null, string2);
        }

        private String resolveSystemId(String string, String string2) {
            try {
                return XMLEntityManager.expandSystemId(string, string2, false);
            }
            catch (URI.MalformedURIException malformedURIException) {
                return string;
            }
        }
    }

    private class XMLSchemaTypeInfoProvider
    extends TypeInfoProvider {
        private Augmentations fElementAugs;
        private XMLAttributes fAttributes;
        private boolean fInStartElement = false;
        private boolean fInEndElement = false;

        private XMLSchemaTypeInfoProvider() {
        }

        void beginStartElement(Augmentations augmentations, XMLAttributes xMLAttributes) {
            this.fInStartElement = true;
            this.fElementAugs = augmentations;
            this.fAttributes = xMLAttributes;
        }

        void finishStartElement() {
            this.fInStartElement = false;
            this.fElementAugs = null;
            this.fAttributes = null;
        }

        void beginEndElement(Augmentations augmentations) {
            this.fInEndElement = true;
            this.fElementAugs = augmentations;
        }

        void finishEndElement() {
            this.fInEndElement = false;
            this.fElementAugs = null;
        }

        private void checkStateAttribute() {
            if (!this.fInStartElement) {
                throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalStateAttribute", null));
            }
        }

        private void checkStateElement() {
            if (!this.fInStartElement && !this.fInEndElement) {
                throw new IllegalStateException(JAXPValidationMessageFormatter.formatMessage(ValidatorHandlerImpl.this.fComponentManager.getLocale(), "TypeInfoProviderIllegalStateElement", null));
            }
        }

        @Override
        public TypeInfo getAttributeTypeInfo(int n) {
            this.checkStateAttribute();
            return this.getAttributeType(n);
        }

        private TypeInfo getAttributeType(int n) {
            this.checkStateAttribute();
            if (n < 0 || this.fAttributes.getLength() <= n) {
                throw new IndexOutOfBoundsException(Integer.toString(n));
            }
            Augmentations augmentations = this.fAttributes.getAugmentations(n);
            if (augmentations == null) {
                return null;
            }
            AttributePSVI attributePSVI = (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
            return this.getTypeInfoFromPSVI(attributePSVI);
        }

        public TypeInfo getAttributeTypeInfo(String string, String string2) {
            this.checkStateAttribute();
            return this.getAttributeTypeInfo(this.fAttributes.getIndex(string, string2));
        }

        public TypeInfo getAttributeTypeInfo(String string) {
            this.checkStateAttribute();
            return this.getAttributeTypeInfo(this.fAttributes.getIndex(string));
        }

        @Override
        public TypeInfo getElementTypeInfo() {
            this.checkStateElement();
            if (this.fElementAugs == null) {
                return null;
            }
            ElementPSVI elementPSVI = (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI");
            return this.getTypeInfoFromPSVI(elementPSVI);
        }

        private TypeInfo getTypeInfoFromPSVI(ItemPSVI itemPSVI) {
            XSTypeDefinition xSTypeDefinition;
            if (itemPSVI == null) {
                return null;
            }
            if (itemPSVI.getValidity() == 2 && (xSTypeDefinition = itemPSVI.getMemberTypeDefinition()) != null) {
                return xSTypeDefinition instanceof TypeInfo ? (TypeInfo)((Object)xSTypeDefinition) : null;
            }
            xSTypeDefinition = itemPSVI.getTypeDefinition();
            if (xSTypeDefinition != null) {
                return xSTypeDefinition instanceof TypeInfo ? (TypeInfo)((Object)xSTypeDefinition) : null;
            }
            return null;
        }

        @Override
        public boolean isIdAttribute(int n) {
            this.checkStateAttribute();
            XSSimpleType xSSimpleType = (XSSimpleType)((Object)this.getAttributeType(n));
            if (xSSimpleType == null) {
                return false;
            }
            return xSSimpleType.isIDType();
        }

        @Override
        public boolean isSpecified(int n) {
            this.checkStateAttribute();
            return this.fAttributes.isSpecified(n);
        }

        ElementPSVI getElementPSVI() {
            return this.fElementAugs != null ? (ElementPSVI)this.fElementAugs.getItem("ELEMENT_PSVI") : null;
        }

        AttributePSVI getAttributePSVI(int n) {
            Augmentations augmentations;
            if (this.fAttributes != null && (augmentations = this.fAttributes.getAugmentations(n)) != null) {
                return (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
            }
            return null;
        }

        AttributePSVI getAttributePSVIByName(String string, String string2) {
            Augmentations augmentations;
            if (this.fAttributes != null && (augmentations = this.fAttributes.getAugmentations(string, string2)) != null) {
                return (AttributePSVI)augmentations.getItem("ATTRIBUTE_PSVI");
            }
            return null;
        }
    }
}

