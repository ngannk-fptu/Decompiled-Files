/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.StringTokenizer;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMStringListImpl;
import org.apache.xerces.dom.ObjectFactory;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.impl.msg.XMLMessageFormatter;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.DOMErrorHandlerWrapper;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMConfigurationImpl
extends ParserConfigurationSettings
implements XMLParserConfiguration,
DOMConfiguration {
    protected static final String XML11_DATATYPE_VALIDATOR_FACTORY = "org.apache.xerces.impl.dv.dtd.XML11DTDDVFactoryImpl";
    protected static final String XERCES_VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String XERCES_NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String SCHEMA = "http://apache.org/xml/features/validation/schema";
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String SEND_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String XML_STRING = "http://xml.org/sax/properties/xml-string";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String DTD_VALIDATOR_PROPERTY = "http://apache.org/xml/properties/internal/validator/dtd";
    protected static final String DTD_VALIDATOR_FACTORY_PROPERTY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    XMLDocumentHandler fDocumentHandler;
    protected short features = 0;
    protected static final short NAMESPACES = 1;
    protected static final short DTNORMALIZATION = 2;
    protected static final short ENTITIES = 4;
    protected static final short CDATA = 8;
    protected static final short SPLITCDATA = 16;
    protected static final short COMMENTS = 32;
    protected static final short VALIDATE = 64;
    protected static final short PSVI = 128;
    protected static final short WELLFORMED = 256;
    protected static final short NSDECL = 512;
    protected static final short INFOSET_TRUE_PARAMS = 801;
    protected static final short INFOSET_FALSE_PARAMS = 14;
    protected static final short INFOSET_MASK = 815;
    protected SymbolTable fSymbolTable;
    protected ArrayList fComponents;
    protected ValidationManager fValidationManager;
    protected Locale fLocale;
    protected XMLErrorReporter fErrorReporter;
    protected final DOMErrorHandlerWrapper fErrorHandlerWrapper = new DOMErrorHandlerWrapper();
    protected DTDDVFactory fCurrentDVFactory;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected DTDDVFactory fXML11DatatypeFactory;
    private String fSchemaLocation = null;
    private DOMStringList fRecognizedParameters;

    protected DOMConfigurationImpl() {
        this(null, null);
    }

    protected DOMConfigurationImpl(SymbolTable symbolTable) {
        this(symbolTable, null);
    }

    protected DOMConfigurationImpl(SymbolTable symbolTable, XMLComponentManager xMLComponentManager) {
        super(xMLComponentManager);
        MessageFormatter messageFormatter;
        this.fRecognizedFeatures = new ArrayList();
        this.fRecognizedProperties = new ArrayList();
        this.fFeatures = new HashMap();
        this.fProperties = new HashMap();
        String[] stringArray = new String[]{XERCES_VALIDATION, XERCES_NAMESPACES, SCHEMA, SCHEMA_FULL_CHECKING, DYNAMIC_VALIDATION, NORMALIZE_DATA, SCHEMA_ELEMENT_DEFAULT, SEND_PSVI, GENERATE_SYNTHETIC_ANNOTATIONS, VALIDATE_ANNOTATIONS, HONOUR_ALL_SCHEMALOCATIONS, USE_GRAMMAR_POOL_ONLY, DISALLOW_DOCTYPE_DECL_FEATURE, BALANCE_SYNTAX_TREES, WARN_ON_DUPLICATE_ATTDEF, "http://apache.org/xml/features/internal/parser-settings", NAMESPACE_GROWTH, TOLERATE_DUPLICATES};
        this.addRecognizedFeatures(stringArray);
        this.setFeature(XERCES_VALIDATION, false);
        this.setFeature(SCHEMA, false);
        this.setFeature(SCHEMA_FULL_CHECKING, false);
        this.setFeature(DYNAMIC_VALIDATION, false);
        this.setFeature(NORMALIZE_DATA, false);
        this.setFeature(SCHEMA_ELEMENT_DEFAULT, false);
        this.setFeature(XERCES_NAMESPACES, true);
        this.setFeature(SEND_PSVI, true);
        this.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS, false);
        this.setFeature(VALIDATE_ANNOTATIONS, false);
        this.setFeature(HONOUR_ALL_SCHEMALOCATIONS, false);
        this.setFeature(USE_GRAMMAR_POOL_ONLY, false);
        this.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, false);
        this.setFeature(BALANCE_SYNTAX_TREES, false);
        this.setFeature(WARN_ON_DUPLICATE_ATTDEF, false);
        this.setFeature("http://apache.org/xml/features/internal/parser-settings", true);
        this.setFeature(NAMESPACE_GROWTH, false);
        this.setFeature(TOLERATE_DUPLICATES, false);
        String[] stringArray2 = new String[]{XML_STRING, SYMBOL_TABLE, ERROR_HANDLER, ENTITY_RESOLVER, ERROR_REPORTER, ENTITY_MANAGER, VALIDATION_MANAGER, GRAMMAR_POOL, SECURITY_MANAGER, JAXP_SCHEMA_SOURCE, JAXP_SCHEMA_LANGUAGE, SCHEMA_LOCATION, SCHEMA_NONS_LOCATION, DTD_VALIDATOR_PROPERTY, DTD_VALIDATOR_FACTORY_PROPERTY, SCHEMA_DV_FACTORY};
        this.addRecognizedProperties(stringArray2);
        this.features = (short)(this.features | 1);
        this.features = (short)(this.features | 4);
        this.features = (short)(this.features | 0x20);
        this.features = (short)(this.features | 8);
        this.features = (short)(this.features | 0x10);
        this.features = (short)(this.features | 0x100);
        this.features = (short)(this.features | 0x200);
        if (symbolTable == null) {
            symbolTable = new SymbolTable();
        }
        this.fSymbolTable = symbolTable;
        this.fComponents = new ArrayList();
        this.setProperty(SYMBOL_TABLE, this.fSymbolTable);
        this.fErrorReporter = new XMLErrorReporter();
        this.setProperty(ERROR_REPORTER, this.fErrorReporter);
        this.addComponent(this.fErrorReporter);
        this.fDatatypeValidatorFactory = DTDDVFactory.getInstance();
        this.fXML11DatatypeFactory = DTDDVFactory.getInstance(XML11_DATATYPE_VALIDATOR_FACTORY);
        this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
        this.setProperty(DTD_VALIDATOR_FACTORY_PROPERTY, this.fCurrentDVFactory);
        XMLEntityManager xMLEntityManager = new XMLEntityManager();
        this.setProperty(ENTITY_MANAGER, xMLEntityManager);
        this.addComponent(xMLEntityManager);
        this.fValidationManager = this.createValidationManager();
        this.setProperty(VALIDATION_MANAGER, this.fValidationManager);
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210") == null) {
            messageFormatter = new XMLMessageFormatter();
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210", messageFormatter);
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/1999/REC-xml-names-19990114", messageFormatter);
        }
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            messageFormatter = null;
            try {
                messageFormatter = (MessageFormatter)ObjectFactory.newInstance("org.apache.xerces.impl.xs.XSMessageFormatter", ObjectFactory.findClassLoader(), true);
            }
            catch (Exception exception) {
                // empty catch block
            }
            if (messageFormatter != null) {
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", messageFormatter);
            }
        }
        try {
            this.setLocale(Locale.getDefault());
        }
        catch (XNIException xNIException) {
            // empty catch block
        }
    }

    @Override
    public void parse(XMLInputSource xMLInputSource) throws XNIException, IOException {
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        this.fDocumentHandler = xMLDocumentHandler;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    @Override
    public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
    }

    @Override
    public XMLDTDHandler getDTDHandler() {
        return null;
    }

    @Override
    public void setDTDContentModelHandler(XMLDTDContentModelHandler xMLDTDContentModelHandler) {
    }

    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return null;
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
        if (xMLErrorHandler != null) {
            this.fProperties.put(ERROR_HANDLER, xMLErrorHandler);
        }
    }

    @Override
    public XMLErrorHandler getErrorHandler() {
        return (XMLErrorHandler)this.fProperties.get(ERROR_HANDLER);
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        if (string.equals("http://apache.org/xml/features/internal/parser-settings")) {
            return true;
        }
        return super.getFeature(string);
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        super.setFeature(string, bl);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        super.setProperty(string, object);
    }

    @Override
    public void setLocale(Locale locale) throws XNIException {
        this.fLocale = locale;
        this.fErrorReporter.setLocale(locale);
    }

    @Override
    public Locale getLocale() {
        return this.fLocale;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setParameter(String string, Object object) throws DOMException {
        boolean bl = true;
        if (object instanceof Boolean) {
            boolean bl2 = (Boolean)object;
            if (string.equalsIgnoreCase("comments")) {
                this.features = (short)(bl2 ? this.features | 0x20 : this.features & 0xFFFFFFDF);
            } else if (string.equalsIgnoreCase("datatype-normalization")) {
                this.setFeature(NORMALIZE_DATA, bl2);
                this.features = (short)(bl2 ? this.features | 2 : this.features & 0xFFFFFFFD);
                if (bl2) {
                    this.features = (short)(this.features | 0x40);
                }
            } else if (string.equalsIgnoreCase("namespaces")) {
                this.features = (short)(bl2 ? this.features | 1 : this.features & 0xFFFFFFFE);
            } else if (string.equalsIgnoreCase("cdata-sections")) {
                this.features = (short)(bl2 ? this.features | 8 : this.features & 0xFFFFFFF7);
            } else if (string.equalsIgnoreCase("entities")) {
                this.features = (short)(bl2 ? this.features | 4 : this.features & 0xFFFFFFFB);
            } else if (string.equalsIgnoreCase("split-cdata-sections")) {
                this.features = (short)(bl2 ? this.features | 0x10 : this.features & 0xFFFFFFEF);
            } else if (string.equalsIgnoreCase("validate")) {
                this.features = (short)(bl2 ? this.features | 0x40 : this.features & 0xFFFFFFBF);
            } else if (string.equalsIgnoreCase("well-formed")) {
                this.features = (short)(bl2 ? this.features | 0x100 : this.features & 0xFFFFFEFF);
            } else if (string.equalsIgnoreCase("namespace-declarations")) {
                this.features = (short)(bl2 ? this.features | 0x200 : this.features & 0xFFFFFDFF);
            } else if (string.equalsIgnoreCase("infoset")) {
                if (bl2) {
                    this.features = (short)(this.features | 0x321);
                    this.features = (short)(this.features & 0xFFFFFFF1);
                    this.setFeature(NORMALIZE_DATA, false);
                }
            } else if (string.equalsIgnoreCase("normalize-characters") || string.equalsIgnoreCase("canonical-form") || string.equalsIgnoreCase("validate-if-schema") || string.equalsIgnoreCase("check-character-normalization")) {
                if (bl2) {
                    throw DOMConfigurationImpl.newFeatureNotSupportedError(string);
                }
            } else if (string.equalsIgnoreCase("element-content-whitespace")) {
                if (!bl2) {
                    throw DOMConfigurationImpl.newFeatureNotSupportedError(string);
                }
            } else if (string.equalsIgnoreCase(SEND_PSVI)) {
                if (!bl2) {
                    throw DOMConfigurationImpl.newFeatureNotSupportedError(string);
                }
            } else if (string.equalsIgnoreCase("psvi")) {
                this.features = (short)(bl2 ? this.features | 0x80 : this.features & 0xFFFFFF7F);
            } else {
                bl = false;
            }
        }
        if (bl && object instanceof Boolean) return;
        bl = true;
        if (string.equalsIgnoreCase("error-handler")) {
            if (!(object instanceof DOMErrorHandler) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            this.fErrorHandlerWrapper.setErrorHandler((DOMErrorHandler)object);
            this.setErrorHandler(this.fErrorHandlerWrapper);
            return;
        }
        if (string.equalsIgnoreCase("resource-resolver")) {
            if (!(object instanceof LSResourceResolver) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            try {
                this.setEntityResolver(new DOMEntityResolverWrapper((LSResourceResolver)object));
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase("schema-location")) {
            if (!(object instanceof String) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            try {
                if (object == null) {
                    this.fSchemaLocation = null;
                    this.setProperty(JAXP_SCHEMA_SOURCE, null);
                    return;
                }
                this.fSchemaLocation = (String)object;
                StringTokenizer stringTokenizer = new StringTokenizer(this.fSchemaLocation, " \n\t\r");
                if (stringTokenizer.hasMoreTokens()) {
                    ArrayList<String> arrayList = new ArrayList<String>();
                    arrayList.add(stringTokenizer.nextToken());
                    while (stringTokenizer.hasMoreTokens()) {
                        arrayList.add(stringTokenizer.nextToken());
                    }
                    this.setProperty(JAXP_SCHEMA_SOURCE, arrayList.toArray(new String[arrayList.size()]));
                    return;
                }
                this.setProperty(JAXP_SCHEMA_SOURCE, new String[]{(String)object});
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase("schema-type")) {
            if (!(object instanceof String) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            try {
                if (object == null) {
                    this.setProperty(JAXP_SCHEMA_LANGUAGE, null);
                    return;
                }
                if (object.equals(Constants.NS_XMLSCHEMA)) {
                    this.setProperty(JAXP_SCHEMA_LANGUAGE, Constants.NS_XMLSCHEMA);
                    return;
                }
                if (!object.equals(Constants.NS_DTD)) return;
                this.setProperty(JAXP_SCHEMA_LANGUAGE, Constants.NS_DTD);
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase(ENTITY_RESOLVER)) {
            if (!(object instanceof XMLEntityResolver) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            try {
                this.setEntityResolver((XMLEntityResolver)object);
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        } else if (string.equalsIgnoreCase(SYMBOL_TABLE)) {
            if (!(object instanceof SymbolTable)) throw DOMConfigurationImpl.newTypeMismatchError(string);
            this.setProperty(SYMBOL_TABLE, object);
            return;
        } else if (string.equalsIgnoreCase(GRAMMAR_POOL)) {
            if (!(object instanceof XMLGrammarPool) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            this.setProperty(GRAMMAR_POOL, object);
            return;
        } else {
            if (!string.equalsIgnoreCase(SECURITY_MANAGER)) throw DOMConfigurationImpl.newFeatureNotFoundError(string);
            if (!(object instanceof SecurityManager) && object != null) throw DOMConfigurationImpl.newTypeMismatchError(string);
            this.setProperty(SECURITY_MANAGER, object);
        }
    }

    @Override
    public Object getParameter(String string) throws DOMException {
        if (string.equalsIgnoreCase("comments")) {
            return (this.features & 0x20) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("namespaces")) {
            return (this.features & 1) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("datatype-normalization")) {
            return (this.features & 2) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("cdata-sections")) {
            return (this.features & 8) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("entities")) {
            return (this.features & 4) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("split-cdata-sections")) {
            return (this.features & 0x10) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("validate")) {
            return (this.features & 0x40) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("well-formed")) {
            return (this.features & 0x100) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("namespace-declarations")) {
            return (this.features & 0x200) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("infoset")) {
            return (this.features & 0x32F) == 801 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("normalize-characters") || string.equalsIgnoreCase("canonical-form") || string.equalsIgnoreCase("validate-if-schema") || string.equalsIgnoreCase("check-character-normalization")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase(SEND_PSVI)) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("psvi")) {
            return (this.features & 0x80) != 0 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("element-content-whitespace")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("error-handler")) {
            return this.fErrorHandlerWrapper.getErrorHandler();
        }
        if (string.equalsIgnoreCase("resource-resolver")) {
            XMLEntityResolver xMLEntityResolver = this.getEntityResolver();
            if (xMLEntityResolver != null && xMLEntityResolver instanceof DOMEntityResolverWrapper) {
                return ((DOMEntityResolverWrapper)xMLEntityResolver).getEntityResolver();
            }
            return null;
        }
        if (string.equalsIgnoreCase("schema-type")) {
            return this.getProperty(JAXP_SCHEMA_LANGUAGE);
        }
        if (string.equalsIgnoreCase("schema-location")) {
            return this.fSchemaLocation;
        }
        if (string.equalsIgnoreCase(ENTITY_RESOLVER)) {
            return this.getEntityResolver();
        }
        if (string.equalsIgnoreCase(SYMBOL_TABLE)) {
            return this.getProperty(SYMBOL_TABLE);
        }
        if (string.equalsIgnoreCase(GRAMMAR_POOL)) {
            return this.getProperty(GRAMMAR_POOL);
        }
        if (string.equalsIgnoreCase(SECURITY_MANAGER)) {
            return this.getProperty(SECURITY_MANAGER);
        }
        throw DOMConfigurationImpl.newFeatureNotFoundError(string);
    }

    @Override
    public boolean canSetParameter(String string, Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Boolean) {
            if (string.equalsIgnoreCase("comments") || string.equalsIgnoreCase("datatype-normalization") || string.equalsIgnoreCase("cdata-sections") || string.equalsIgnoreCase("entities") || string.equalsIgnoreCase("split-cdata-sections") || string.equalsIgnoreCase("namespaces") || string.equalsIgnoreCase("validate") || string.equalsIgnoreCase("well-formed") || string.equalsIgnoreCase("infoset") || string.equalsIgnoreCase("namespace-declarations")) {
                return true;
            }
            if (string.equalsIgnoreCase("normalize-characters") || string.equalsIgnoreCase("canonical-form") || string.equalsIgnoreCase("validate-if-schema") || string.equalsIgnoreCase("check-character-normalization")) {
                return !object.equals(Boolean.TRUE);
            }
            if (string.equalsIgnoreCase("element-content-whitespace") || string.equalsIgnoreCase(SEND_PSVI)) {
                return object.equals(Boolean.TRUE);
            }
            return false;
        }
        if (string.equalsIgnoreCase("error-handler")) {
            return object instanceof DOMErrorHandler;
        }
        if (string.equalsIgnoreCase("resource-resolver")) {
            return object instanceof LSResourceResolver;
        }
        if (string.equalsIgnoreCase("schema-location")) {
            return object instanceof String;
        }
        if (string.equalsIgnoreCase("schema-type")) {
            return object instanceof String && (object.equals(Constants.NS_XMLSCHEMA) || object.equals(Constants.NS_DTD));
        }
        if (string.equalsIgnoreCase(ENTITY_RESOLVER)) {
            return object instanceof XMLEntityResolver;
        }
        if (string.equalsIgnoreCase(SYMBOL_TABLE)) {
            return object instanceof SymbolTable;
        }
        if (string.equalsIgnoreCase(GRAMMAR_POOL)) {
            return object instanceof XMLGrammarPool;
        }
        if (string.equalsIgnoreCase(SECURITY_MANAGER)) {
            return object instanceof SecurityManager;
        }
        return false;
    }

    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("comments");
            arrayList.add("datatype-normalization");
            arrayList.add("cdata-sections");
            arrayList.add("entities");
            arrayList.add("split-cdata-sections");
            arrayList.add("namespaces");
            arrayList.add("validate");
            arrayList.add("infoset");
            arrayList.add("normalize-characters");
            arrayList.add("canonical-form");
            arrayList.add("validate-if-schema");
            arrayList.add("check-character-normalization");
            arrayList.add("well-formed");
            arrayList.add("namespace-declarations");
            arrayList.add("element-content-whitespace");
            arrayList.add("error-handler");
            arrayList.add("schema-type");
            arrayList.add("schema-location");
            arrayList.add("resource-resolver");
            arrayList.add(ENTITY_RESOLVER);
            arrayList.add(GRAMMAR_POOL);
            arrayList.add(SECURITY_MANAGER);
            arrayList.add(SYMBOL_TABLE);
            arrayList.add(SEND_PSVI);
            this.fRecognizedParameters = new DOMStringListImpl(arrayList);
        }
        return this.fRecognizedParameters;
    }

    protected void reset() throws XNIException {
        if (this.fValidationManager != null) {
            this.fValidationManager.reset();
        }
        int n = this.fComponents.size();
        for (int i = 0; i < n; ++i) {
            XMLComponent xMLComponent = (XMLComponent)this.fComponents.get(i);
            xMLComponent.reset(this);
        }
    }

    @Override
    protected void checkProperty(String string) throws XMLConfigurationException {
        int n;
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
        String[] stringArray = xMLComponent.getRecognizedFeatures();
        this.addRecognizedFeatures(stringArray);
        String[] stringArray2 = xMLComponent.getRecognizedProperties();
        this.addRecognizedProperties(stringArray2);
    }

    protected ValidationManager createValidationManager() {
        return new ValidationManager();
    }

    protected final void setDTDValidatorFactory(String string) {
        if ("1.1".equals(string)) {
            if (this.fCurrentDVFactory != this.fXML11DatatypeFactory) {
                this.fCurrentDVFactory = this.fXML11DatatypeFactory;
                this.setProperty(DTD_VALIDATOR_FACTORY_PROPERTY, this.fCurrentDVFactory);
            }
        } else if (this.fCurrentDVFactory != this.fDatatypeValidatorFactory) {
            this.fCurrentDVFactory = this.fDatatypeValidatorFactory;
            this.setProperty(DTD_VALIDATOR_FACTORY_PROPERTY, this.fCurrentDVFactory);
        }
    }

    private static DOMException newFeatureNotSupportedError(String string) {
        String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{string});
        return new DOMException(9, string2);
    }

    private static DOMException newFeatureNotFoundError(String string) {
        String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_FOUND", new Object[]{string});
        return new DOMException(8, string2);
    }

    private static DOMException newTypeMismatchError(String string) {
        String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "TYPE_MISMATCH_ERR", new Object[]{string});
        return new DOMException(17, string2);
    }
}

