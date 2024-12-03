/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import org.apache.xerces.impl.XML11DTDScannerImpl;
import org.apache.xerces.impl.XML11DocumentScannerImpl;
import org.apache.xerces.impl.XML11NSDocumentScannerImpl;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.impl.XMLEntityHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.XMLNSDocumentScannerImpl;
import org.apache.xerces.impl.XMLVersionDetector;
import org.apache.xerces.impl.dtd.XML11DTDProcessor;
import org.apache.xerces.impl.dtd.XML11DTDValidator;
import org.apache.xerces.impl.dtd.XML11NSDTDValidator;
import org.apache.xerces.impl.dtd.XMLDTDProcessor;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.impl.dtd.XMLNSDTDValidator;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.parsers.XML11Configurable;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;

public class XML11DTDConfiguration
extends ParserConfigurationSettings
implements XMLPullParserConfiguration,
XML11Configurable {
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl";
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String EXTERNAL_GENERAL_ENTITIES = "http://xml.org/sax/features/external-general-entities";
    protected static final String EXTERNAL_PARAMETER_ENTITIES = "http://xml.org/sax/features/external-parameter-entities";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
    protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DTD_PROCESSOR = "http://apache.org/xml/properties/internal/dtd-processor";
    protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
    protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
    protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
    protected SymbolTable fSymbolTable;
    protected XMLInputSource fInputSource;
    protected ValidationManager fValidationManager;
    protected XMLVersionDetector fVersionDetector;
    protected XMLLocator fLocator;
    protected Locale fLocale;
    protected ArrayList fComponents = new ArrayList();
    protected ArrayList fXML11Components = new ArrayList();
    protected ArrayList fCommonComponents = new ArrayList();
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLDocumentSource fLastComponent;
    protected boolean fParseInProgress = false;
    protected boolean fConfigUpdated = false;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLNSDocumentScannerImpl fNamespaceScanner;
    protected XMLDocumentScannerImpl fNonNSScanner;
    protected XMLDTDValidator fDTDValidator;
    protected XMLDTDValidator fNonNSDTDValidator;
    protected XMLDTDScanner fDTDScanner;
    protected XMLDTDProcessor fDTDProcessor;
    protected DTDDVFactory fXML11DatatypeFactory = null;
    protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
    protected XML11DocumentScannerImpl fXML11DocScanner = null;
    protected XML11NSDTDValidator fXML11NSDTDValidator = null;
    protected XML11DTDValidator fXML11DTDValidator = null;
    protected XML11DTDScannerImpl fXML11DTDScanner = null;
    protected XML11DTDProcessor fXML11DTDProcessor = null;
    protected XMLGrammarPool fGrammarPool;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLDocumentScanner fCurrentScanner;
    protected DTDDVFactory fCurrentDVFactory;
    protected XMLDTDScanner fCurrentDTDScanner;
    private boolean f11Initialized = false;

    public XML11DTDConfiguration() {
        this(null, null, null);
    }

    public XML11DTDConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public XML11DTDConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public XML11DTDConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(xMLComponentManager);
        this.fRecognizedFeatures = new ArrayList();
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        String[] stringArray = new String[]{CONTINUE_AFTER_FATAL_ERROR, LOAD_EXTERNAL_DTD, VALIDATION, NAMESPACES, EXTERNAL_GENERAL_ENTITIES, EXTERNAL_PARAMETER_ENTITIES, "http://apache.org/xml/features/internal/parser-settings"};
        this.addRecognizedFeatures(stringArray);
        this.fFeatures.put(VALIDATION, Boolean.FALSE);
        this.fFeatures.put(NAMESPACES, Boolean.TRUE);
        this.fFeatures.put(EXTERNAL_GENERAL_ENTITIES, Boolean.TRUE);
        this.fFeatures.put(EXTERNAL_PARAMETER_ENTITIES, Boolean.TRUE);
        this.fFeatures.put(CONTINUE_AFTER_FATAL_ERROR, Boolean.FALSE);
        this.fFeatures.put(LOAD_EXTERNAL_DTD, Boolean.TRUE);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        String[] stringArray2 = new String[]{SYMBOL_TABLE, ERROR_HANDLER, ENTITY_RESOLVER, ERROR_REPORTER, ENTITY_MANAGER, DOCUMENT_SCANNER, DTD_SCANNER, DTD_PROCESSOR, DTD_VALIDATOR, DATATYPE_VALIDATOR_FACTORY, VALIDATION_MANAGER, XML_STRING, XMLGRAMMAR_POOL, JAXP_SCHEMA_SOURCE, JAXP_SCHEMA_LANGUAGE};
        this.addRecognizedProperties(stringArray2);
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        this.fSymbolTable = symbolTable;
        this.fProperties.put(SYMBOL_TABLE, this.fSymbolTable);
        this.fGrammarPool = xMLGrammarPool;
        if (this.fGrammarPool != null) {
            this.fProperties.put(XMLGRAMMAR_POOL, this.fGrammarPool);
        }
        this.fEntityManager = new XMLEntityManager();
        this.fProperties.put(ENTITY_MANAGER, this.fEntityManager);
        this.addCommonComponent(this.fEntityManager);
        this.fErrorReporter = new XMLErrorReporter();
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fProperties.put(ERROR_REPORTER, this.fErrorReporter);
        this.addCommonComponent(this.fErrorReporter);
        this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
        this.fProperties.put(DOCUMENT_SCANNER, this.fNamespaceScanner);
        this.addComponent(this.fNamespaceScanner);
        this.fDTDScanner = new XMLDTDScannerImpl();
        this.fProperties.put(DTD_SCANNER, this.fDTDScanner);
        this.addComponent((XMLComponent)((Object)this.fDTDScanner));
        this.fDTDProcessor = new XMLDTDProcessor();
        this.fProperties.put(DTD_PROCESSOR, this.fDTDProcessor);
        this.addComponent(this.fDTDProcessor);
        this.fDTDValidator = new XMLNSDTDValidator();
        this.fProperties.put(DTD_VALIDATOR, this.fDTDValidator);
        this.addComponent(this.fDTDValidator);
        this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
        this.fProperties.put(DATATYPE_VALIDATOR_FACTORY, this.fDatatypeValidatorFactory);
        this.fValidationManager = new ValidationManager();
        this.fProperties.put(VALIDATION_MANAGER, this.fValidationManager);
        this.fVersionDetector = new XMLVersionDetector();
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (XNIException xNIException) {
            // empty catch block
        }
        this.fConfigUpdated = false;
    }

    @Override
    public void setInputSource(XMLInputSource xMLInputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = xMLInputSource;
    }

    @Override
    public void setLocale(Locale locale) throws XNIException {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        this.fDocumentHandler = xMLDocumentHandler;
        if (this.fLastComponent != null) {
            this.fLastComponent.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fLastComponent);
            }
        }
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    @Override
    public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
        this.fDTDHandler = xMLDTDHandler;
    }

    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    @Override
    public void setDTDContentModelHandler(XMLDTDContentModelHandler xMLDTDContentModelHandler) {
        this.fDTDContentModelHandler = xMLDTDContentModelHandler;
    }

    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }

    @Override
    public void setEntityResolver(XMLEntityResolver xMLEntityResolver) {
        this.fProperties.put(ENTITY_RESOLVER, xMLEntityResolver);
    }

    @Override
    public XMLEntityResolver getEntityResolver() {
        return (XMLEntityResolver)this.fProperties.get(ENTITY_RESOLVER);
    }

    @Override
    public void setErrorHandler(XMLErrorHandler xMLErrorHandler) {
        this.fProperties.put(ERROR_HANDLER, xMLErrorHandler);
    }

    @Override
    public XMLErrorHandler getErrorHandler() {
        return (XMLErrorHandler)this.fProperties.get(ERROR_HANDLER);
    }

    @Override
    public void cleanup() {
        this.fEntityManager.closeReaders();
    }

    @Override
    public void parse(XMLInputSource xMLInputSource) throws XNIException, IOException {
        if (this.fParseInProgress) {
            throw new XNIException("FWK005 parse may not be called while parsing.");
        }
        this.fParseInProgress = true;
        try {
            this.setInputSource(xMLInputSource);
            this.parse(true);
        }
        catch (XNIException xNIException) {
            throw xNIException;
        }
        catch (IOException iOException) {
            throw iOException;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (Exception exception) {
            throw new XNIException(exception);
        }
        finally {
            this.fParseInProgress = false;
            this.cleanup();
        }
    }

    @Override
    public boolean parse(boolean bl) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.fValidationManager.reset();
                this.fVersionDetector.reset(this);
                this.resetCommon();
                short s = this.fVersionDetector.determineDocVersion(this.fInputSource);
                if (s == 1) {
                    this.configurePipeline();
                    this.reset();
                } else if (s == 2) {
                    this.initXML11Components();
                    this.configureXML11Pipeline();
                    this.resetXML11();
                } else {
                    return false;
                }
                this.fConfigUpdated = false;
                this.fVersionDetector.startDocumentParsing((XMLEntityHandler)((Object)this.fCurrentScanner), s);
                this.fInputSource = null;
            }
            catch (XNIException xNIException) {
                throw xNIException;
            }
            catch (IOException iOException) {
                throw iOException;
            }
            catch (RuntimeException runtimeException) {
                throw runtimeException;
            }
            catch (Exception exception) {
                throw new XNIException(exception);
            }
        }
        try {
            return this.fCurrentScanner.scanDocument(bl);
        }
        catch (XNIException xNIException) {
            throw xNIException;
        }
        catch (IOException iOException) {
            throw iOException;
        }
        catch (RuntimeException runtimeException) {
            throw runtimeException;
        }
        catch (Exception exception) {
            throw new XNIException(exception);
        }
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        if (string.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return this.fConfigUpdated;
        }
        return super.getFeature(string);
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        XMLComponent xMLComponent;
        int n;
        this.fConfigUpdated = true;
        int n2 = this.fComponents.size();
        for (n = 0; n < n2; ++n) {
            xMLComponent = (XMLComponent)this.fComponents.get(n);
            xMLComponent.setFeature(string, bl);
        }
        n2 = this.fCommonComponents.size();
        for (n = 0; n < n2; ++n) {
            xMLComponent = (XMLComponent)this.fCommonComponents.get(n);
            xMLComponent.setFeature(string, bl);
        }
        n2 = this.fXML11Components.size();
        for (n = 0; n < n2; ++n) {
            xMLComponent = (XMLComponent)this.fXML11Components.get(n);
            try {
                xMLComponent.setFeature(string, bl);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        super.setFeature(string, bl);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        XMLComponent xMLComponent;
        int n;
        this.fConfigUpdated = true;
        int n2 = this.fComponents.size();
        for (n = 0; n < n2; ++n) {
            xMLComponent = (XMLComponent)this.fComponents.get(n);
            xMLComponent.setProperty(string, object);
        }
        n2 = this.fCommonComponents.size();
        for (n = 0; n < n2; ++n) {
            xMLComponent = (XMLComponent)this.fCommonComponents.get(n);
            xMLComponent.setProperty(string, object);
        }
        n2 = this.fXML11Components.size();
        for (n = 0; n < n2; ++n) {
            xMLComponent = (XMLComponent)this.fXML11Components.get(n);
            try {
                xMLComponent.setProperty(string, object);
                continue;
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        super.setProperty(string, object);
    }

    @Override
    public Locale getLocale() {
        return this.fLocale;
    }

    protected void reset() throws XNIException {
        int n = this.fComponents.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(i);
            xMLComponent.reset(this);
        }
    }

    protected void resetCommon() throws XNIException {
        int n = this.fCommonComponents.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fCommonComponents.get(i);
            xMLComponent.reset(this);
        }
    }

    protected void resetXML11() throws XNIException {
        int n = this.fXML11Components.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fXML11Components.get(i);
            xMLComponent.reset(this);
        }
    }

    protected void configureXML11Pipeline() {
        if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
            this.fCurrentDVFactory = this.fXML11DatatypeFactory;
            this.setProperty(DATATYPE_VALIDATOR_FACTORY, this.fCurrentDVFactory);
        }
        if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
            this.fCurrentDTDScanner = this.fXML11DTDScanner;
            this.setProperty(DTD_SCANNER, this.fCurrentDTDScanner);
            this.setProperty(DTD_PROCESSOR, this.fXML11DTDProcessor);
        }
        this.fXML11DTDScanner.setDTDHandler(this.fXML11DTDProcessor);
        this.fXML11DTDProcessor.setDTDSource(this.fXML11DTDScanner);
        this.fXML11DTDProcessor.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fXML11DTDProcessor);
        }
        this.fXML11DTDScanner.setDTDContentModelHandler(this.fXML11DTDProcessor);
        this.fXML11DTDProcessor.setDTDContentModelSource(this.fXML11DTDScanner);
        this.fXML11DTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDProcessor);
        }
        if (this.fFeatures.get(NAMESPACES) == Boolean.TRUE) {
            if (this.fCurrentScanner != this.fXML11NSDocScanner) {
                this.fCurrentScanner = this.fXML11NSDocScanner;
                this.setProperty(DOCUMENT_SCANNER, this.fXML11NSDocScanner);
                this.setProperty(DTD_VALIDATOR, this.fXML11NSDTDValidator);
            }
            this.fXML11NSDocScanner.setDTDValidator(this.fXML11NSDTDValidator);
            this.fXML11NSDocScanner.setDocumentHandler(this.fXML11NSDTDValidator);
            this.fXML11NSDTDValidator.setDocumentSource(this.fXML11NSDocScanner);
            this.fXML11NSDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fXML11NSDTDValidator);
            }
            this.fLastComponent = this.fXML11NSDTDValidator;
        } else {
            if (this.fXML11DocScanner == null) {
                this.fXML11DocScanner = new XML11DocumentScannerImpl();
                this.addXML11Component(this.fXML11DocScanner);
                this.fXML11DTDValidator = new XML11DTDValidator();
                this.addXML11Component(this.fXML11DTDValidator);
            }
            if (this.fCurrentScanner != this.fXML11DocScanner) {
                this.fCurrentScanner = this.fXML11DocScanner;
                this.setProperty(DOCUMENT_SCANNER, this.fXML11DocScanner);
                this.setProperty(DTD_VALIDATOR, this.fXML11DTDValidator);
            }
            this.fXML11DocScanner.setDocumentHandler(this.fXML11DTDValidator);
            this.fXML11DTDValidator.setDocumentSource(this.fXML11DocScanner);
            this.fXML11DTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fXML11DTDValidator);
            }
            this.fLastComponent = this.fXML11DTDValidator;
        }
    }

    protected void configurePipeline() {
        if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
            this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
            this.setProperty(DATATYPE_VALIDATOR_FACTORY, this.fCurrentDVFactory);
        }
        if (this.fCurrentDTDScanner != this.fDTDScanner) {
            this.fCurrentDTDScanner = this.fDTDScanner;
            this.setProperty(DTD_SCANNER, this.fCurrentDTDScanner);
            this.setProperty(DTD_PROCESSOR, this.fDTDProcessor);
        }
        this.fDTDScanner.setDTDHandler(this.fDTDProcessor);
        this.fDTDProcessor.setDTDSource(this.fDTDScanner);
        this.fDTDProcessor.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fDTDProcessor);
        }
        this.fDTDScanner.setDTDContentModelHandler(this.fDTDProcessor);
        this.fDTDProcessor.setDTDContentModelSource(this.fDTDScanner);
        this.fDTDProcessor.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDProcessor);
        }
        if (this.fFeatures.get(NAMESPACES) == Boolean.TRUE) {
            if (this.fCurrentScanner != this.fNamespaceScanner) {
                this.fCurrentScanner = this.fNamespaceScanner;
                this.setProperty(DOCUMENT_SCANNER, this.fNamespaceScanner);
                this.setProperty(DTD_VALIDATOR, this.fDTDValidator);
            }
            this.fNamespaceScanner.setDTDValidator(this.fDTDValidator);
            this.fNamespaceScanner.setDocumentHandler(this.fDTDValidator);
            this.fDTDValidator.setDocumentSource(this.fNamespaceScanner);
            this.fDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fDTDValidator);
            }
            this.fLastComponent = this.fDTDValidator;
        } else {
            if (this.fNonNSScanner == null) {
                this.fNonNSScanner = new XMLDocumentScannerImpl();
                this.fNonNSDTDValidator = new XMLDTDValidator();
                this.addComponent(this.fNonNSScanner);
                this.addComponent(this.fNonNSDTDValidator);
            }
            if (this.fCurrentScanner != this.fNonNSScanner) {
                this.fCurrentScanner = this.fNonNSScanner;
                this.setProperty(DOCUMENT_SCANNER, this.fNonNSScanner);
                this.setProperty(DTD_VALIDATOR, this.fNonNSDTDValidator);
            }
            this.fNonNSScanner.setDocumentHandler(this.fNonNSDTDValidator);
            this.fNonNSDTDValidator.setDocumentSource(this.fNonNSScanner);
            this.fNonNSDTDValidator.setDocumentHandler(this.fDocumentHandler);
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.setDocumentSource(this.fNonNSDTDValidator);
            }
            this.fLastComponent = this.fNonNSDTDValidator;
        }
    }

    @Override
    protected void checkFeature(String string) throws XMLConfigurationException {
        if (string.startsWith("http://apache.org/xml/features/")) {
            int n = string.length() - "http://apache.org/xml/features/".length();
            if (n == "validation/dynamic".length() && string.endsWith("validation/dynamic")) {
                return;
            }
            if (n == "validation/default-attribute-values".length() && string.endsWith("validation/default-attribute-values")) {
                short s = 1;
                throw new XMLConfigurationException(s, string);
            }
            if (n == "validation/validate-content-models".length() && string.endsWith("validation/validate-content-models")) {
                short s = 1;
                throw new XMLConfigurationException(s, string);
            }
            if (n == "nonvalidating/load-dtd-grammar".length() && string.endsWith("nonvalidating/load-dtd-grammar")) {
                return;
            }
            if (n == "nonvalidating/load-external-dtd".length() && string.endsWith("nonvalidating/load-external-dtd")) {
                return;
            }
            if (n == "validation/validate-datatypes".length() && string.endsWith("validation/validate-datatypes")) {
                short s = 1;
                throw new XMLConfigurationException(s, string);
            }
            if (n == "internal/parser-settings".length() && string.endsWith("internal/parser-settings")) {
                short s = 1;
                throw new XMLConfigurationException(s, string);
            }
        }
        super.checkFeature(string);
    }

    @Override
    protected void checkProperty(String string) throws XMLConfigurationException {
        int n;
        if (string.startsWith("http://apache.org/xml/properties/") && (n = string.length() - "http://apache.org/xml/properties/".length()) == "internal/dtd-scanner".length() && string.endsWith("internal/dtd-scanner")) {
            return;
        }
        if (string.startsWith("http://xml.org/sax/properties/") && (n = string.length() - "http://xml.org/sax/properties/".length()) == "xml-string".length() && string.endsWith("xml-string")) {
            short s = 1;
            throw new XMLConfigurationException(s, string);
        }
        super.checkProperty(string);
    }

    protected void addComponent(XMLComponent xMLComponent) {
        if (this.fComponents.contains(xMLComponent)) {
            return;
        }
        this.fComponents.add(xMLComponent);
        this.addRecognizedParamsAndSetDefaults(xMLComponent);
    }

    protected void addCommonComponent(XMLComponent xMLComponent) {
        if (this.fCommonComponents.contains(xMLComponent)) {
            return;
        }
        this.fCommonComponents.add(xMLComponent);
        this.addRecognizedParamsAndSetDefaults(xMLComponent);
    }

    protected void addXML11Component(XMLComponent xMLComponent) {
        if (this.fXML11Components.contains(xMLComponent)) {
            return;
        }
        this.fXML11Components.add(xMLComponent);
        this.addRecognizedParamsAndSetDefaults(xMLComponent);
    }

    protected void addRecognizedParamsAndSetDefaults(XMLComponent xMLComponent) {
        Object object;
        String string;
        int n;
        String[] stringArray = xMLComponent.getRecognizedFeatures();
        this.addRecognizedFeatures(stringArray);
        String[] stringArray2 = xMLComponent.getRecognizedProperties();
        this.addRecognizedProperties(stringArray2);
        if (stringArray != null) {
            for (n = 0; n < stringArray.length; ++n) {
                string = stringArray[n];
                object = xMLComponent.getFeatureDefault(string);
                if (object == null || this.fFeatures.containsKey(string)) continue;
                this.fFeatures.put(string, object);
                this.fConfigUpdated = true;
            }
        }
        if (stringArray2 != null) {
            for (n = 0; n < stringArray2.length; ++n) {
                string = stringArray2[n];
                object = xMLComponent.getPropertyDefault(string);
                if (object == null || this.fProperties.containsKey(string)) continue;
                this.fProperties.put(string, object);
                this.fConfigUpdated = true;
            }
        }
    }

    private void initXML11Components() {
        if (!this.f11Initialized) {
            this.fXML11DatatypeFactory = DTDDVFactory.getInstance(XML11_DATATYPE_VALIDATOR_FACTORY);
            this.fXML11DTDScanner = new XML11DTDScannerImpl();
            this.addXML11Component(this.fXML11DTDScanner);
            this.fXML11DTDProcessor = new XML11DTDProcessor();
            this.addXML11Component(this.fXML11DTDProcessor);
            this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
            this.addXML11Component(this.fXML11NSDocScanner);
            this.fXML11NSDTDValidator = new XML11NSDTDValidator();
            this.addXML11Component(this.fXML11NSDTDValidator);
            this.f11Initialized = true;
        }
    }
}

