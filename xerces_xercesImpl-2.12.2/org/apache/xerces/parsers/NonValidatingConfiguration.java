/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.IOException;
import java.util.Locale;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLDocumentScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.XMLNSDocumentScannerImpl;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.parsers.BasicParserConfiguration;
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

public class NonValidatingConfiguration
extends BasicParserConfiguration
implements XMLPullParserConfiguration {
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_DUPLICATE_ENTITYDEF = "http://apache.org/xml/features/warn-on-duplicate-entitydef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String LOAD_EXTERNAL_DTD = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
    protected static final String NOTIFY_BUILTIN_REFS = "http://apache.org/xml/features/scanner/notify-builtin-refs";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
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
    protected XMLGrammarPool fGrammarPool;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityManager fEntityManager;
    protected XMLDocumentScanner fScanner;
    protected XMLInputSource fInputSource;
    protected XMLDTDScanner fDTDScanner;
    protected ValidationManager fValidationManager;
    private XMLNSDocumentScannerImpl fNamespaceScanner;
    private XMLDocumentScannerImpl fNonNSScanner;
    protected boolean fConfigUpdated = false;
    protected XMLLocator fLocator;
    protected boolean fParseInProgress = false;

    public NonValidatingConfiguration() {
        this(null, null, null);
    }

    public NonValidatingConfiguration(SymbolTable symbolTable) {
        this(symbolTable, null, null);
    }

    public NonValidatingConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null);
    }

    public NonValidatingConfiguration(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLComponentManager xMLComponentManager) {
        super(symbolTable, xMLComponentManager);
        String[] stringArray = new String[]{"http://apache.org/xml/features/internal/parser-settings", "http://xml.org/sax/features/namespaces", CONTINUE_AFTER_FATAL_ERROR};
        this.addRecognizedFeatures(stringArray);
        this.fFeatures.put(CONTINUE_AFTER_FATAL_ERROR, Boolean.FALSE);
        this.fFeatures.put("http://apache.org/xml/features/internal/parser-settings", Boolean.TRUE);
        this.fFeatures.put("http://xml.org/sax/features/namespaces", Boolean.TRUE);
        String[] stringArray2 = new String[]{ERROR_REPORTER, ENTITY_MANAGER, DOCUMENT_SCANNER, DTD_SCANNER, DTD_VALIDATOR, NAMESPACE_BINDER, XMLGRAMMAR_POOL, DATATYPE_VALIDATOR_FACTORY, VALIDATION_MANAGER, LOCALE};
        this.addRecognizedProperties(stringArray2);
        this.fGrammarPool = xMLGrammarPool;
        if (this.fGrammarPool != null) {
            this.fProperties.put(XMLGRAMMAR_POOL, this.fGrammarPool);
        }
        this.fEntityManager = this.createEntityManager();
        this.fProperties.put(ENTITY_MANAGER, this.fEntityManager);
        this.addComponent(this.fEntityManager);
        this.fErrorReporter = this.createErrorReporter();
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
        this.fProperties.put(ERROR_REPORTER, this.fErrorReporter);
        this.addComponent(this.fErrorReporter);
        this.fDTDScanner = this.createDTDScanner();
        if (this.fDTDScanner != null) {
            this.fProperties.put(DTD_SCANNER, this.fDTDScanner);
            if (this.fDTDScanner instanceof XMLComponent) {
                this.addComponent((XMLComponent)((Object)this.fDTDScanner));
            }
        }
        this.fDatatypeValidatorFactory = this.createDatatypeValidatorFactory();
        if (this.fDatatypeValidatorFactory != null) {
            this.fProperties.put(DATATYPE_VALIDATOR_FACTORY, this.fDatatypeValidatorFactory);
        }
        this.fValidationManager = this.createValidationManager();
        if (this.fValidationManager != null) {
            this.fProperties.put(VALIDATION_MANAGER, this.fValidationManager);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
        }
        this.fConfigUpdated = false;
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (XNIException xNIException) {
            // empty catch block
        }
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        this.fConfigUpdated = true;
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
        super.setProperty(string, object);
    }

    @Override
    public void setLocale(Locale locale) throws XNIException {
        super.setLocale(locale);
        this.fErrorReporter.setLocale(locale);
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        if (string.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return this.fConfigUpdated;
        }
        return super.getFeature(string);
    }

    @Override
    public void setInputSource(XMLInputSource xMLInputSource) throws XMLConfigurationException, IOException {
        this.fInputSource = xMLInputSource;
    }

    @Override
    public boolean parse(boolean bl) throws XNIException, IOException {
        if (this.fInputSource != null) {
            try {
                this.reset();
                this.fScanner.setInputSource(this.fInputSource);
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
            return this.fScanner.scanDocument(bl);
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
    protected void reset() throws XNIException {
        if (this.fValidationManager != null) {
            this.fValidationManager.reset();
        }
        this.configurePipeline();
        super.reset();
    }

    protected void configurePipeline() {
        if (this.fFeatures.get("http://xml.org/sax/features/namespaces") == Boolean.TRUE) {
            if (this.fNamespaceScanner == null) {
                this.fNamespaceScanner = new XMLNSDocumentScannerImpl();
                this.addComponent(this.fNamespaceScanner);
            }
            this.fProperties.put(DOCUMENT_SCANNER, this.fNamespaceScanner);
            this.fNamespaceScanner.setDTDValidator(null);
            this.fScanner = this.fNamespaceScanner;
        } else {
            if (this.fNonNSScanner == null) {
                this.fNonNSScanner = new XMLDocumentScannerImpl();
                this.addComponent(this.fNonNSScanner);
            }
            this.fProperties.put(DOCUMENT_SCANNER, this.fNonNSScanner);
            this.fScanner = this.fNonNSScanner;
        }
        this.fScanner.setDocumentHandler(this.fDocumentHandler);
        this.fLastComponent = this.fScanner;
        if (this.fDTDScanner != null) {
            this.fDTDScanner.setDTDHandler(this.fDTDHandler);
            this.fDTDScanner.setDTDContentModelHandler(this.fDTDContentModelHandler);
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

    protected XMLEntityManager createEntityManager() {
        return new XMLEntityManager();
    }

    protected XMLErrorReporter createErrorReporter() {
        return new XMLErrorReporter();
    }

    protected XMLDocumentScanner createDocumentScanner() {
        return null;
    }

    protected XMLDTDScanner createDTDScanner() {
        return new XMLDTDScannerImpl();
    }

    protected DTDDVFactory createDatatypeValidatorFactory() {
        return DTDDVFactory.getInstance();
    }

    protected ValidationManager createValidationManager() {
        return new ValidationManager();
    }
}

