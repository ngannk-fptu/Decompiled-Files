/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.opti;

import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.impl.XML11DTDScannerImpl;
import org.apache.xerces.impl.XML11NSDocumentScannerImpl;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.XMLNSDocumentScannerImpl;
import org.apache.xerces.impl.XMLVersionDetector;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.parsers.BasicParserConfiguration;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDScanner;
import org.apache.xerces.xni.parser.XMLDocumentScanner;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLPullParserConfiguration;

public class SchemaParsingConfig
extends BasicParserConfiguration
implements XMLPullParserConfiguration {
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String DOCUMENT_SCANNER = "http://apache.org/xml/properties/internal/document-scanner";
    protected static final String DTD_SCANNER = "http://apache.org/xml/properties/internal/dtd-scanner";
    protected static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
    protected static final String NAMESPACE_BINDER = "http://apache.org/xml/properties/internal/namespace-binder";
    protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String SCHEMA_VALIDATOR = "http://apache.org/xml/properties/internal/validator/schema";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    private static final boolean PRINT_EXCEPTION_STACK_TRACE = false;
    protected final DTDDVFactory fDatatypeValidatorFactory;
    protected final XMLNSDocumentScannerImpl fNamespaceScanner;
    protected final XMLDTDScannerImpl fDTDScanner;
    protected DTDDVFactory fXML11DatatypeFactory = null;
    protected XML11NSDocumentScannerImpl fXML11NSDocScanner = null;
    protected XML11DTDScannerImpl fXML11DTDScanner = null;
    protected DTDDVFactory fCurrentDVFactory;
    protected XMLDocumentScanner fCurrentScanner;
    protected XMLDTDScanner fCurrentDTDScanner;
    protected XMLGrammarPool fGrammarPool;
    protected final XMLVersionDetector fVersionDetector;
    protected final XMLErrorReporter fErrorReporter;
    protected final XMLEntityManager fEntityManager;
    protected XMLInputSource fInputSource;
    protected final ValidationManager fValidationManager;
    protected XMLLocator fLocator;
    protected boolean fParseInProgress = false;
    protected boolean fConfigUpdated = false;
    private boolean f11Initialized = false;

    public SchemaParsingConfig() {
        this(null, null, null);
    }

    public SchemaParsingConfig(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public SchemaParsingConfig(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public SchemaParsingConfig(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLComponentManager);
        MessageFormatter messageFormatter;
        String[] stringArray = new String[]{"http://apache.org/xml/features/internal/parser-settings", WARN_ON_DUPLICATE_ATTDEF, WARN_ON_UNDECLARED_ELEMDEF, ALLOW_JAVA_ENCODINGS, CONTINUE_AFTER_FATAL_ERROR, LOAD_EXTERNAL_DTD, NOTIFY_BUILTIN_REFS, NOTIFY_CHAR_REFS, GENERATE_SYNTHETIC_ANNOTATIONS};
        this.addRecognizedFeatures(stringArray);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put(WARN_ON_DUPLICATE_ATTDEF, Boolean.FALSE);
        this.fFeatures.put(WARN_ON_UNDECLARED_ELEMDEF, Boolean.FALSE);
        this.fFeatures.put(ALLOW_JAVA_ENCODINGS, Boolean.FALSE);
        this.fFeatures.put(CONTINUE_AFTER_FATAL_ERROR, Boolean.FALSE);
        this.fFeatures.put(LOAD_EXTERNAL_DTD, Boolean.TRUE);
        this.fFeatures.put(NOTIFY_BUILTIN_REFS, Boolean.FALSE);
        this.fFeatures.put(NOTIFY_CHAR_REFS, Boolean.FALSE);
        this.fFeatures.put(GENERATE_SYNTHETIC_ANNOTATIONS, Boolean.FALSE);
        String[] stringArray2 = new String[]{ERROR_REPORTER, ENTITY_MANAGER, DOCUMENT_SCANNER, DTD_SCANNER, DTD_VALIDATOR, NAMESPACE_BINDER, XMLGRAMMAR_POOL, DATATYPE_VALIDATOR_FACTORY, VALIDATION_MANAGER, GENERATE_SYNTHETIC_ANNOTATIONS, LOCALE};
        this.addRecognizedProperties(stringArray2);
        this.fGrammarPool = xMLGrammarPool;
        if (this.fGrammarPool != null) {
            this.setProperty(XMLGRAMMAR_POOL, this.fGrammarPool);
        }
        this.fEntityManager = new XMLEntityManager();
        this.fProperties.put(ENTITY_MANAGER, this.fEntityManager);
        this.addComponent(this.fEntityManager);
        this.fErrorReporter = new XMLErrorReporter();
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fProperties.put(ERROR_REPORTER, this.fErrorReporter);
        this.addComponent(this.fErrorReporter);
        this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
        this.fProperties.put(DOCUMENT_SCANNER, this.fNamespaceScanner);
        this.addRecognizedParamsAndSetDefaults(this.fNamespaceScanner);
        this.fDTDScanner = new XMLDTDScannerImpl();
        this.fProperties.put(DTD_SCANNER, this.fDTDScanner);
        this.addRecognizedParamsAndSetDefaults(this.fDTDScanner);
        this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
        this.fProperties.put(DATATYPE_VALIDATOR_FACTORY, this.fDatatypeValidatorFactory);
        this.fValidationManager = new ValidationManager();
        this.fProperties.put(VALIDATION_MANAGER, this.fValidationManager);
        this.fVersionDetector = new XMLVersionDetector();
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            messageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", messageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", messageFormatter);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            messageFormatter = new XSMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", messageFormatter);
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (XNIException xNIException) {
            // empty catch block
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
        this.fConfigUpdated = true;
        this.fNamespaceScanner.setFeature(string, bl);
        this.fDTDScanner.setFeature(string, bl);
        if (this.f11Initialized) {
            try {
                this.fXML11DTDScanner.setFeature(string, bl);
            }
            catch (Exception exception) {
                // empty catch block
            }
            try {
                this.fXML11NSDocScanner.setFeature(string, bl);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        super.setFeature(string, bl);
    }

    @Override
    public Object getProperty(String string) throws XMLConfigurationException {
        if (LOCALE.equals(string)) {
            return this.getLocale();
        }
        return super.getProperty(string);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        this.fConfigUpdated = true;
        if (LOCALE.equals(string)) {
            this.setLocale((Locale)object);
        }
        this.fNamespaceScanner.setProperty(string, object);
        this.fDTDScanner.setProperty(string, object);
        if (this.f11Initialized) {
            try {
                this.fXML11DTDScanner.setProperty(string, object);
            }
            catch (Exception exception) {
                // empty catch block
            }
            try {
                this.fXML11NSDocScanner.setProperty(string, object);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        super.setProperty(string, object);
    }

    @Override
    public void setLocale(Locale locale) throws XNIException {
        super.setLocale(locale);
        this.fErrorReporter.setLocale(locale);
    }

    @Override
    public void setInputSource(XMLInputSource xMLInputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = xMLInputSource;
    }

    @Override
    public boolean parse(boolean bl) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.fValidationManager.reset();
                this.fVersionDetector.reset(this);
                this.reset();
                short s = this.fVersionDetector.determineDocVersion(this.fInputSource);
                if (s == 1) {
                    this.configurePipeline();
                    this.resetXML10();
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
    public void reset() throws XNIException {
        super.reset();
    }

    protected void configurePipeline() {
        if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
            this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
            this.setProperty(DATATYPE_VALIDATOR_FACTORY, this.fCurrentDVFactory);
        }
        if (this.fCurrentScanner != this.fNamespaceScanner) {
            this.fCurrentScanner = this.fNamespaceScanner;
            this.setProperty(DOCUMENT_SCANNER, this.fCurrentScanner);
        }
        this.fNamespaceScanner.setDocumentHandler(this.fDocumentHandler);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fNamespaceScanner);
        }
        this.fLastComponent = this.fNamespaceScanner;
        if (this.fCurrentDTDScanner != this.fDTDScanner) {
            this.fCurrentDTDScanner = this.fDTDScanner;
            this.setProperty(DTD_SCANNER, this.fCurrentDTDScanner);
        }
        this.fDTDScanner.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fDTDScanner);
        }
        this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fDTDScanner);
        }
    }

    protected void configureXML11Pipeline() {
        if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
            this.fCurrentDVFactory = this.fXML11DatatypeFactory;
            this.setProperty(DATATYPE_VALIDATOR_FACTORY, this.fCurrentDVFactory);
        }
        if (this.fCurrentScanner != this.fXML11NSDocScanner) {
            this.fCurrentScanner = this.fXML11NSDocScanner;
            this.setProperty(DOCUMENT_SCANNER, this.fCurrentScanner);
        }
        this.fXML11NSDocScanner.setDocumentHandler(this.fDocumentHandler);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.setDocumentSource(this.fXML11NSDocScanner);
        }
        this.fLastComponent = this.fXML11NSDocScanner;
        if (this.fCurrentDTDScanner != this.fXML11DTDScanner) {
            this.fCurrentDTDScanner = this.fXML11DTDScanner;
            this.setProperty(DTD_SCANNER, this.fCurrentDTDScanner);
        }
        this.fXML11DTDScanner.setDTDHandler(this.fDTDHandler);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.setDTDSource(this.fXML11DTDScanner);
        }
        this.fXML11DTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.setDTDContentModelSource(this.fXML11DTDScanner);
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
        }
        super.checkFeature(string);
    }

    @Override
    protected void checkProperty(String string) throws XMLConfigurationException {
        int n;
        if (string.startsWith("http://apache.org/xml/properties/") && (n = string.length() - "http://apache.org/xml/properties/".length()) == "internal/dtd-scanner".length() && string.endsWith("internal/dtd-scanner")) {
            return;
        }
        if (string.startsWith("http://java.sun.com/xml/jaxp/properties/") && (n = string.length() - "http://java.sun.com/xml/jaxp/properties/".length()) == "schemaSource".length() && string.endsWith("schemaSource")) {
            return;
        }
        super.checkProperty(string);
    }

    private void addRecognizedParamsAndSetDefaults(XMLComponent xMLComponent) {
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

    protected final void resetXML10() throws XNIException {
        this.fNamespaceScanner.reset(this);
        this.fDTDScanner.reset(this);
    }

    protected final void resetXML11() throws XNIException {
        this.fXML11NSDocScanner.reset(this);
        this.fXML11DTDScanner.reset(this);
    }

    public void resetNodePool() {
    }

    private void initXML11Components() {
        if (!this.f11Initialized) {
            this.fXML11DatatypeFactory = DTDDVFactory.getInstance(XML11_DATATYPE_VALIDATOR_FACTORY);
            this.fXML11DTDScanner = new XML11DTDScannerImpl();
            this.addRecognizedParamsAndSetDefaults(this.fXML11DTDScanner);
            this.fXML11NSDocScanner = new XML11NSDocumentScannerImpl();
            this.addRecognizedParamsAndSetDefaults(this.fXML11NSDocScanner);
            this.f11Initialized = true;
        }
    }
}

