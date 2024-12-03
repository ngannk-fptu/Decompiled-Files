/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import java.io.EOFException;
import java.io.IOException;
import java.io.StringReader;
import java.util.Locale;
import org.apache.xerces.impl.XMLDTDScannerImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.DTDGrammarBucket;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.dtd.XMLDTDProcessor;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;

public class XMLDTDLoader
extends XMLDTDProcessor
implements XMLGrammarLoader {
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
    private static final String[] LOADER_RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/validation/balance-syntax-trees"};
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    private static final String[] LOADER_RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd", "http://apache.org/xml/properties/locale"};
    private boolean fStrictURI = false;
    private boolean fBalanceSyntaxTrees = false;
    protected XMLEntityResolver fEntityResolver;
    protected XMLDTDScannerImpl fDTDScanner;
    protected XMLEntityManager fEntityManager;
    protected Locale fLocale;

    public XMLDTDLoader() {
        this(new SymbolTable());
    }

    public XMLDTDLoader(SymbolTable symbolTable) {
        this(symbolTable, null);
    }

    public XMLDTDLoader(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this(symbolTable, xMLGrammarPool, null, new XMLEntityManager());
    }

    XMLDTDLoader(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool, XMLErrorReporter xMLErrorReporter, XMLEntityResolver xMLEntityResolver) {
        this.fSymbolTable = symbolTable;
        this.fGrammarPool = xMLGrammarPool;
        if (xMLErrorReporter == null) {
            xMLErrorReporter = new XMLErrorReporter();
            xMLErrorReporter.setProperty(ERROR_HANDLER, new DefaultErrorHandler());
        }
        this.fErrorReporter = xMLErrorReporter;
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
        }
        this.fEntityResolver = xMLEntityResolver;
        this.fEntityManager = this.fEntityResolver instanceof XMLEntityManager ? (XMLEntityManager)this.fEntityResolver : new XMLEntityManager();
        this.fEntityManager.setProperty("http://apache.org/xml/properties/internal/error-reporter", xMLErrorReporter);
        this.fDTDScanner = this.createDTDScanner(this.fSymbolTable, this.fErrorReporter, this.fEntityManager);
        this.fDTDScanner.setDTDHandler(this);
        this.fDTDScanner.setDTDContentModelHandler(this);
        this.reset();
    }

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])LOADER_RECOGNIZED_FEATURES.clone();
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        if (string.equals("http://xml.org/sax/features/validation")) {
            this.fValidation = bl;
        } else if (string.equals("http://apache.org/xml/features/validation/warn-on-duplicate-attdef")) {
            this.fWarnDuplicateAttdef = bl;
        } else if (string.equals("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef")) {
            this.fWarnOnUndeclaredElemdef = bl;
        } else if (string.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
            this.fDTDScanner.setFeature(string, bl);
        } else if (string.equals(STANDARD_URI_CONFORMANT_FEATURE)) {
            this.fStrictURI = bl;
        } else if (string.equals(BALANCE_SYNTAX_TREES)) {
            this.fBalanceSyntaxTrees = bl;
        } else {
            throw new XMLConfigurationException(0, string);
        }
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])LOADER_RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public Object getProperty(String string) throws XMLConfigurationException {
        if (string.equals("http://apache.org/xml/properties/internal/symbol-table")) {
            return this.fSymbolTable;
        }
        if (string.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            return this.fErrorReporter;
        }
        if (string.equals(ERROR_HANDLER)) {
            return this.fErrorReporter.getErrorHandler();
        }
        if (string.equals(ENTITY_RESOLVER)) {
            return this.fEntityResolver;
        }
        if (string.equals(LOCALE)) {
            return this.getLocale();
        }
        if (string.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            return this.fGrammarPool;
        }
        if (string.equals("http://apache.org/xml/properties/internal/validator/dtd")) {
            return this.fValidator;
        }
        throw new XMLConfigurationException(0, string);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.equals("http://apache.org/xml/properties/internal/symbol-table")) {
            this.fSymbolTable = (SymbolTable)object;
            this.fDTDScanner.setProperty(string, object);
            this.fEntityManager.setProperty(string, object);
        } else if (string.equals("http://apache.org/xml/properties/internal/error-reporter")) {
            this.fErrorReporter = (XMLErrorReporter)object;
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
                XMLMessageFormatter xMLMessageFormatter = new XMLMessageFormatter();
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", xMLMessageFormatter);
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", xMLMessageFormatter);
            }
            this.fDTDScanner.setProperty(string, object);
            this.fEntityManager.setProperty(string, object);
        } else if (string.equals(ERROR_HANDLER)) {
            this.fErrorReporter.setProperty(string, object);
        } else if (string.equals(ENTITY_RESOLVER)) {
            this.fEntityResolver = (XMLEntityResolver)object;
            this.fEntityManager.setProperty(string, object);
        } else if (string.equals(LOCALE)) {
            this.setLocale((Locale)object);
        } else if (string.equals("http://apache.org/xml/properties/internal/grammar-pool")) {
            this.fGrammarPool = (XMLGrammarPool)object;
        } else {
            throw new XMLConfigurationException(0, string);
        }
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        if (string.equals("http://xml.org/sax/features/validation")) {
            return this.fValidation;
        }
        if (string.equals("http://apache.org/xml/features/validation/warn-on-duplicate-attdef")) {
            return this.fWarnDuplicateAttdef;
        }
        if (string.equals("http://apache.org/xml/features/validation/warn-on-undeclared-elemdef")) {
            return this.fWarnOnUndeclaredElemdef;
        }
        if (string.equals("http://apache.org/xml/features/scanner/notify-char-refs")) {
            return this.fDTDScanner.getFeature(string);
        }
        if (string.equals(STANDARD_URI_CONFORMANT_FEATURE)) {
            return this.fStrictURI;
        }
        if (string.equals(BALANCE_SYNTAX_TREES)) {
            return this.fBalanceSyntaxTrees;
        }
        throw new XMLConfigurationException(0, string);
    }

    @Override
    public void setLocale(Locale locale) {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }

    @Override
    public Locale getLocale() {
        return this.fLocale;
    }

    @Override
    public void setErrorHandler(XMLErrorHandler xMLErrorHandler) {
        this.fErrorReporter.setProperty(ERROR_HANDLER, xMLErrorHandler);
    }

    @Override
    public XMLErrorHandler getErrorHandler() {
        return this.fErrorReporter.getErrorHandler();
    }

    @Override
    public void setEntityResolver(XMLEntityResolver xMLEntityResolver) {
        this.fEntityResolver = xMLEntityResolver;
        this.fEntityManager.setProperty(ENTITY_RESOLVER, xMLEntityResolver);
    }

    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fEntityResolver;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Grammar loadGrammar(XMLInputSource xMLInputSource) throws IOException, XNIException {
        this.reset();
        String string = XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), this.fStrictURI);
        XMLDTDDescription xMLDTDDescription = new XMLDTDDescription(xMLInputSource.getPublicId(), xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), string, null);
        this.fDTDGrammar = !this.fBalanceSyntaxTrees ? new DTDGrammar(this.fSymbolTable, xMLDTDDescription) : new BalancedDTDGrammar(this.fSymbolTable, xMLDTDDescription);
        this.fGrammarBucket = new DTDGrammarBucket();
        this.fGrammarBucket.setStandalone(false);
        this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);
        try {
            this.fDTDScanner.setInputSource(xMLInputSource);
            this.fDTDScanner.scanDTDExternalSubset(true);
        }
        catch (EOFException eOFException) {
        }
        finally {
            this.fEntityManager.closeReaders();
        }
        if (this.fDTDGrammar != null && this.fGrammarPool != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[]{this.fDTDGrammar});
        }
        return this.fDTDGrammar;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void loadGrammarWithContext(XMLDTDValidator xMLDTDValidator, String string, String string2, String string3, String string4, String string5) throws IOException, XNIException {
        DTDGrammarBucket dTDGrammarBucket = xMLDTDValidator.getGrammarBucket();
        DTDGrammar dTDGrammar = dTDGrammarBucket.getActiveGrammar();
        if (dTDGrammar != null && !dTDGrammar.isImmutable()) {
            this.fGrammarBucket = dTDGrammarBucket;
            this.fEntityManager.setScannerVersion(this.getScannerVersion());
            this.reset();
            try {
                XMLInputSource xMLInputSource;
                Object object;
                if (string5 != null) {
                    object = new StringBuffer(string5.length() + 2);
                    ((StringBuffer)object).append(string5).append("]>");
                    xMLInputSource = new XMLInputSource(null, string4, null, new StringReader(((StringBuffer)object).toString()), null);
                    this.fEntityManager.startDocumentEntity(xMLInputSource);
                    this.fDTDScanner.scanDTDInternalSubset(true, false, string3 != null);
                }
                if (string3 != null) {
                    object = new XMLDTDDescription(string2, string3, string4, null, string);
                    xMLInputSource = this.fEntityManager.resolveEntity((XMLResourceIdentifier)object);
                    this.fDTDScanner.setInputSource(xMLInputSource);
                    this.fDTDScanner.scanDTDExternalSubset(true);
                }
            }
            catch (EOFException eOFException) {
            }
            finally {
                this.fEntityManager.closeReaders();
            }
        }
    }

    @Override
    protected void reset() {
        super.reset();
        this.fDTDScanner.reset();
        this.fEntityManager.reset();
        this.fErrorReporter.setDocumentLocator(this.fEntityManager.getEntityScanner());
    }

    protected XMLDTDScannerImpl createDTDScanner(SymbolTable symbolTable, XMLErrorReporter xMLErrorReporter, XMLEntityManager xMLEntityManager) {
        return new XMLDTDScannerImpl(symbolTable, xMLErrorReporter, xMLEntityManager);
    }

    protected short getScannerVersion() {
        return 1;
    }
}

