/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.WeakHashMap;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMStringListImpl;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.xs.SchemaDVFactoryImpl;
import org.apache.xerces.impl.xs.FilePathToURI;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSDeclarationPool;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.impl.xs.XSMessageFormatter;
import org.apache.xerces.impl.xs.XSModelImpl;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.models.CMNodeFactory;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.DOMErrorHandlerWrapper;
import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.util.MessageFormatter;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarLoader;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.XSGrammar;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.LSInputList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSLoader;
import org.apache.xerces.xs.XSModel;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSResourceResolver;
import org.xml.sax.InputSource;

public class XMLSchemaLoader
implements XMLGrammarLoader,
XMLComponent,
XSElementDeclHelper,
XSLoader,
DOMConfiguration {
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/validation/schema/augment-psvi", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/disallow-doctype-decl", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates"};
    public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/entity-manager", "http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/error-handler", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/locale", "http://apache.org/xml/properties/internal/validation/schema/dv-factory"};
    private final ParserConfigurationSettings fLoaderConfig = new ParserConfigurationSettings();
    private XMLErrorReporter fErrorReporter = new XMLErrorReporter();
    private XMLEntityManager fEntityManager = null;
    private XMLEntityResolver fUserEntityResolver = null;
    private XMLGrammarPool fGrammarPool = null;
    private String fExternalSchemas = null;
    private String fExternalNoNSSchema = null;
    private Object fJAXPSource = null;
    private boolean fIsCheckedFully = false;
    private boolean fJAXPProcessed = false;
    private boolean fSettingsChanged = true;
    private XSDHandler fSchemaHandler;
    private XSGrammarBucket fGrammarBucket;
    private XSDeclarationPool fDeclPool = null;
    private SubstitutionGroupHandler fSubGroupHandler;
    private CMBuilder fCMBuilder;
    private XSDDescription fXSDDescription = new XSDDescription();
    private SchemaDVFactory fDefaultSchemaDVFactory;
    private WeakHashMap fJAXPCache;
    private Locale fLocale = Locale.getDefault();
    private DOMStringList fRecognizedParameters = null;
    private DOMErrorHandlerWrapper fErrorHandler = null;
    private DOMEntityResolverWrapper fResourceResolver = null;

    public XMLSchemaLoader() {
        this(new SymbolTable(), null, new XMLEntityManager(), null, null, null);
    }

    public XMLSchemaLoader(SymbolTable symbolTable) {
        this(symbolTable, null, new XMLEntityManager(), null, null, null);
    }

    XMLSchemaLoader(XMLErrorReporter xMLErrorReporter, XSGrammarBucket xSGrammarBucket, SubstitutionGroupHandler substitutionGroupHandler, CMBuilder cMBuilder) {
        this(null, xMLErrorReporter, null, xSGrammarBucket, substitutionGroupHandler, cMBuilder);
    }

    XMLSchemaLoader(SymbolTable symbolTable, XMLErrorReporter xMLErrorReporter, XMLEntityManager xMLEntityManager, XSGrammarBucket xSGrammarBucket, SubstitutionGroupHandler substitutionGroupHandler, CMBuilder cMBuilder) {
        this.fLoaderConfig.addRecognizedFeatures(RECOGNIZED_FEATURES);
        this.fLoaderConfig.addRecognizedProperties(RECOGNIZED_PROPERTIES);
        if (symbolTable != null) {
            this.fLoaderConfig.setProperty(SYMBOL_TABLE, symbolTable);
        }
        if (xMLErrorReporter == null) {
            xMLErrorReporter = new XMLErrorReporter();
            xMLErrorReporter.setLocale(this.fLocale);
            xMLErrorReporter.setProperty(ERROR_HANDLER, new DefaultErrorHandler());
        }
        this.fErrorReporter = xMLErrorReporter;
        if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
        }
        this.fLoaderConfig.setProperty(ERROR_REPORTER, this.fErrorReporter);
        this.fEntityManager = xMLEntityManager;
        if (this.fEntityManager != null) {
            this.fLoaderConfig.setProperty(ENTITY_MANAGER, this.fEntityManager);
        }
        this.fLoaderConfig.setFeature(AUGMENT_PSVI, true);
        if (xSGrammarBucket == null) {
            xSGrammarBucket = new XSGrammarBucket();
        }
        this.fGrammarBucket = xSGrammarBucket;
        if (substitutionGroupHandler == null) {
            substitutionGroupHandler = new SubstitutionGroupHandler(this);
        }
        this.fSubGroupHandler = substitutionGroupHandler;
        CMNodeFactory cMNodeFactory = new CMNodeFactory();
        if (cMBuilder == null) {
            cMBuilder = new CMBuilder(cMNodeFactory);
        }
        this.fCMBuilder = cMBuilder;
        this.fSchemaHandler = new XSDHandler(this.fGrammarBucket);
        this.fJAXPCache = new WeakHashMap();
        this.fSettingsChanged = true;
    }

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])RECOGNIZED_FEATURES.clone();
    }

    @Override
    public boolean getFeature(String string) throws XMLConfigurationException {
        return this.fLoaderConfig.getFeature(string);
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        this.fSettingsChanged = true;
        if (string.equals(CONTINUE_AFTER_FATAL_ERROR)) {
            this.fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, bl);
        } else if (string.equals(GENERATE_SYNTHETIC_ANNOTATIONS)) {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(bl);
        }
        this.fLoaderConfig.setFeature(string, bl);
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public Object getProperty(String string) throws XMLConfigurationException {
        return this.fLoaderConfig.getProperty(string);
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        this.fSettingsChanged = true;
        this.fLoaderConfig.setProperty(string, object);
        if (string.equals(JAXP_SCHEMA_SOURCE)) {
            this.fJAXPSource = object;
            this.fJAXPProcessed = false;
        } else if (string.equals(XMLGRAMMAR_POOL)) {
            this.fGrammarPool = (XMLGrammarPool)object;
        } else if (string.equals(SCHEMA_LOCATION)) {
            this.fExternalSchemas = (String)object;
        } else if (string.equals(SCHEMA_NONS_LOCATION)) {
            this.fExternalNoNSSchema = (String)object;
        } else if (string.equals(LOCALE)) {
            this.setLocale((Locale)object);
        } else if (string.equals(ENTITY_RESOLVER)) {
            this.fEntityManager.setProperty(ENTITY_RESOLVER, object);
        } else if (string.equals(ERROR_REPORTER)) {
            this.fErrorReporter = (XMLErrorReporter)object;
            if (this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1") == null) {
                this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xml-schema-1", new XSMessageFormatter());
            }
        }
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
        this.fUserEntityResolver = xMLEntityResolver;
        this.fLoaderConfig.setProperty(ENTITY_RESOLVER, xMLEntityResolver);
        this.fEntityManager.setProperty(ENTITY_RESOLVER, xMLEntityResolver);
    }

    @Override
    public XMLEntityResolver getEntityResolver() {
        return this.fUserEntityResolver;
    }

    public void loadGrammar(XMLInputSource[] xMLInputSourceArray) throws IOException, XNIException {
        int n = xMLInputSourceArray.length;
        for (int i = 0; i < n; ++i) {
            this.loadGrammar(xMLInputSourceArray[i]);
        }
    }

    @Override
    public Grammar loadGrammar(XMLInputSource xMLInputSource) throws IOException, XNIException {
        this.reset(this.fLoaderConfig);
        this.fSettingsChanged = false;
        XSDDescription xSDDescription = new XSDDescription();
        xSDDescription.fContextType = (short)3;
        xSDDescription.setBaseSystemId(xMLInputSource.getBaseSystemId());
        xSDDescription.setLiteralSystemId(xMLInputSource.getSystemId());
        Hashtable hashtable = new Hashtable();
        XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNSSchema, hashtable, this.fErrorReporter);
        SchemaGrammar schemaGrammar = this.loadSchema(xSDDescription, xMLInputSource, hashtable);
        if (schemaGrammar != null && this.fGrammarPool != null) {
            this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", this.fGrammarBucket.getGrammars());
            if (this.fIsCheckedFully && this.fJAXPCache.get(schemaGrammar) != schemaGrammar) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
            }
        }
        return schemaGrammar;
    }

    SchemaGrammar loadSchema(XSDDescription xSDDescription, XMLInputSource xMLInputSource, Hashtable hashtable) throws IOException, XNIException {
        if (!this.fJAXPProcessed) {
            this.processJAXPSchemaSource(hashtable);
        }
        SchemaGrammar schemaGrammar = this.fSchemaHandler.parseSchema(xMLInputSource, xSDDescription, hashtable);
        return schemaGrammar;
    }

    public static XMLInputSource resolveDocument(XSDDescription xSDDescription, Hashtable hashtable, XMLEntityResolver xMLEntityResolver) throws IOException {
        Object object;
        String string;
        LocationArray locationArray;
        String string2 = null;
        if ((xSDDescription.getContextType() == 2 || xSDDescription.fromInstance()) && (locationArray = (LocationArray)hashtable.get(string = (object = xSDDescription.getTargetNamespace()) == null ? XMLSymbols.EMPTY_STRING : object)) != null) {
            string2 = locationArray.getFirstLocation();
        }
        if (string2 == null && (object = xSDDescription.getLocationHints()) != null && ((String[])object).length > 0) {
            string2 = object[0];
        }
        object = XMLEntityManager.expandSystemId(string2, xSDDescription.getBaseSystemId(), false);
        xSDDescription.setLiteralSystemId(string2);
        xSDDescription.setExpandedSystemId((String)object);
        return xMLEntityResolver.resolveEntity(xSDDescription);
    }

    public static void processExternalHints(String string, String string2, Hashtable hashtable, XMLErrorReporter xMLErrorReporter) {
        XSAttributeDecl xSAttributeDecl;
        if (string != null) {
            try {
                xSAttributeDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
                xSAttributeDecl.fType.validate(string, null, null);
                if (!XMLSchemaLoader.tokenizeSchemaLocationStr(string, hashtable, null)) {
                    xMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[]{string}, (short)0);
                }
            }
            catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                xMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)0);
            }
        }
        if (string2 != null) {
            try {
                xSAttributeDecl = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
                xSAttributeDecl.fType.validate(string2, null, null);
                LocationArray locationArray = (LocationArray)hashtable.get(XMLSymbols.EMPTY_STRING);
                if (locationArray == null) {
                    locationArray = new LocationArray();
                    hashtable.put(XMLSymbols.EMPTY_STRING, locationArray);
                }
                locationArray.addLocation(string2);
            }
            catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                xMLErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)0);
            }
        }
    }

    public static boolean tokenizeSchemaLocationStr(String string, Hashtable hashtable, String string2) {
        if (string != null) {
            StringTokenizer stringTokenizer = new StringTokenizer(string, " \n\t\r");
            while (stringTokenizer.hasMoreTokens()) {
                String string3 = stringTokenizer.nextToken();
                if (!stringTokenizer.hasMoreTokens()) {
                    return false;
                }
                String string4 = stringTokenizer.nextToken();
                LocationArray locationArray = (LocationArray)hashtable.get(string3);
                if (locationArray == null) {
                    locationArray = new LocationArray();
                    hashtable.put(string3, locationArray);
                }
                if (string2 != null) {
                    try {
                        string4 = XMLEntityManager.expandSystemId(string4, string2, false);
                    }
                    catch (URI.MalformedURIException malformedURIException) {
                        // empty catch block
                    }
                }
                locationArray.addLocation(string4);
            }
        }
        return true;
    }

    private void processJAXPSchemaSource(Hashtable hashtable) throws IOException {
        this.fJAXPProcessed = true;
        if (this.fJAXPSource == null) {
            return;
        }
        Class<?> clazz = this.fJAXPSource.getClass().getComponentType();
        XMLInputSource xMLInputSource = null;
        String string = null;
        if (clazz == null) {
            SchemaGrammar schemaGrammar;
            if ((this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) && (schemaGrammar = (SchemaGrammar)this.fJAXPCache.get(this.fJAXPSource)) != null) {
                this.fGrammarBucket.putGrammar(schemaGrammar);
                return;
            }
            this.fXSDDescription.reset();
            xMLInputSource = this.xsdToXMLInputSource(this.fJAXPSource);
            string = xMLInputSource.getSystemId();
            this.fXSDDescription.fContextType = (short)3;
            if (string != null) {
                this.fXSDDescription.setBaseSystemId(xMLInputSource.getBaseSystemId());
                this.fXSDDescription.setLiteralSystemId(string);
                this.fXSDDescription.setExpandedSystemId(string);
                this.fXSDDescription.fLocationHints = new String[]{string};
            }
            if ((schemaGrammar = this.loadSchema(this.fXSDDescription, xMLInputSource, hashtable)) != null) {
                if (this.fJAXPSource instanceof InputStream || this.fJAXPSource instanceof InputSource) {
                    this.fJAXPCache.put(this.fJAXPSource, schemaGrammar);
                    if (this.fIsCheckedFully) {
                        XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
                    }
                }
                this.fGrammarBucket.putGrammar(schemaGrammar);
            }
            return;
        }
        if (!(clazz == Object.class || clazz == String.class || clazz == File.class || clazz == InputStream.class || clazz == InputSource.class || File.class.isAssignableFrom(clazz) || InputStream.class.isAssignableFrom(clazz) || InputSource.class.isAssignableFrom(clazz) || clazz.isInterface())) {
            MessageFormatter messageFormatter = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1");
            throw new XMLConfigurationException(1, messageFormatter.formatMessage(this.fErrorReporter.getLocale(), "jaxp12-schema-source-type.2", new Object[]{clazz.getName()}));
        }
        Object[] objectArray = (Object[])this.fJAXPSource;
        ArrayList<Object> arrayList = new ArrayList<Object>();
        for (int i = 0; i < objectArray.length; ++i) {
            Object object;
            if ((objectArray[i] instanceof InputStream || objectArray[i] instanceof InputSource) && (object = (SchemaGrammar)this.fJAXPCache.get(objectArray[i])) != null) {
                this.fGrammarBucket.putGrammar((SchemaGrammar)object);
                continue;
            }
            this.fXSDDescription.reset();
            xMLInputSource = this.xsdToXMLInputSource(objectArray[i]);
            string = xMLInputSource.getSystemId();
            this.fXSDDescription.fContextType = (short)3;
            if (string != null) {
                this.fXSDDescription.setBaseSystemId(xMLInputSource.getBaseSystemId());
                this.fXSDDescription.setLiteralSystemId(string);
                this.fXSDDescription.setExpandedSystemId(string);
                this.fXSDDescription.fLocationHints = new String[]{string};
            }
            object = null;
            SchemaGrammar schemaGrammar = this.fSchemaHandler.parseSchema(xMLInputSource, this.fXSDDescription, hashtable);
            if (this.fIsCheckedFully) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fErrorReporter);
            }
            if (schemaGrammar == null) continue;
            object = schemaGrammar.getTargetNamespace();
            if (arrayList.contains(object)) {
                MessageFormatter messageFormatter = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1");
                throw new IllegalArgumentException(messageFormatter.formatMessage(this.fErrorReporter.getLocale(), "jaxp12-schema-source-ns", null));
            }
            arrayList.add(object);
            if (objectArray[i] instanceof InputStream || objectArray[i] instanceof InputSource) {
                this.fJAXPCache.put(objectArray[i], schemaGrammar);
            }
            this.fGrammarBucket.putGrammar(schemaGrammar);
        }
    }

    private XMLInputSource xsdToXMLInputSource(Object object) {
        if (object instanceof String) {
            String string = (String)object;
            this.fXSDDescription.reset();
            this.fXSDDescription.setValues(null, string, null, null);
            XMLInputSource xMLInputSource = null;
            try {
                xMLInputSource = this.fEntityManager.resolveEntity(this.fXSDDescription);
            }
            catch (IOException iOException) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[]{string}, (short)1);
            }
            if (xMLInputSource == null) {
                return new XMLInputSource(null, string, null);
            }
            return xMLInputSource;
        }
        if (object instanceof InputSource) {
            return XMLSchemaLoader.saxToXMLInputSource((InputSource)object);
        }
        if (object instanceof InputStream) {
            return new XMLInputSource(null, null, null, (InputStream)object, null);
        }
        if (object instanceof File) {
            File file = (File)object;
            String string = FilePathToURI.filepath2URI(file.getAbsolutePath());
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
            }
            catch (FileNotFoundException fileNotFoundException) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[]{file.toString()}, (short)1);
            }
            return new XMLInputSource(null, string, null, bufferedInputStream, null);
        }
        MessageFormatter messageFormatter = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/xml-schema-1");
        throw new XMLConfigurationException(1, messageFormatter.formatMessage(this.fErrorReporter.getLocale(), "jaxp12-schema-source-type.1", new Object[]{object != null ? object.getClass().getName() : "null"}));
    }

    private static XMLInputSource saxToXMLInputSource(InputSource inputSource) {
        String string = inputSource.getPublicId();
        String string2 = inputSource.getSystemId();
        Reader reader = inputSource.getCharacterStream();
        if (reader != null) {
            return new XMLInputSource(string, string2, null, reader, null);
        }
        InputStream inputStream = inputSource.getByteStream();
        if (inputStream != null) {
            return new XMLInputSource(string, string2, null, inputStream, inputSource.getEncoding());
        }
        return new XMLInputSource(string, string2, null);
    }

    @Override
    public Boolean getFeatureDefault(String string) {
        if (string.equals(AUGMENT_PSVI)) {
            return Boolean.TRUE;
        }
        return null;
    }

    @Override
    public Object getPropertyDefault(String string) {
        return null;
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        this.fGrammarBucket.reset();
        this.fSubGroupHandler.reset();
        if (!this.fSettingsChanged || !this.parserSettingsUpdated(xMLComponentManager)) {
            this.fJAXPProcessed = false;
            this.initGrammarBucket();
            if (this.fDeclPool != null) {
                this.fDeclPool.reset();
            }
            return;
        }
        this.fEntityManager = (XMLEntityManager)xMLComponentManager.getProperty(ENTITY_MANAGER);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        SchemaDVFactory schemaDVFactory = null;
        try {
            schemaDVFactory = (SchemaDVFactory)xMLComponentManager.getProperty(SCHEMA_DV_FACTORY);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        if (schemaDVFactory == null) {
            if (this.fDefaultSchemaDVFactory == null) {
                this.fDefaultSchemaDVFactory = SchemaDVFactory.getInstance();
            }
            schemaDVFactory = this.fDefaultSchemaDVFactory;
        }
        this.fSchemaHandler.setDVFactory(schemaDVFactory);
        try {
            this.fExternalSchemas = (String)xMLComponentManager.getProperty(SCHEMA_LOCATION);
            this.fExternalNoNSSchema = (String)xMLComponentManager.getProperty(SCHEMA_NONS_LOCATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fExternalSchemas = null;
            this.fExternalNoNSSchema = null;
        }
        try {
            this.fJAXPSource = xMLComponentManager.getProperty(JAXP_SCHEMA_SOURCE);
            this.fJAXPProcessed = false;
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fJAXPSource = null;
            this.fJAXPProcessed = false;
        }
        try {
            this.fGrammarPool = (XMLGrammarPool)xMLComponentManager.getProperty(XMLGRAMMAR_POOL);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fGrammarPool = null;
        }
        this.initGrammarBucket();
        boolean bl = true;
        try {
            bl = xMLComponentManager.getFeature(AUGMENT_PSVI);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            bl = false;
        }
        if (bl || this.fGrammarPool == null) {
            // empty if block
        }
        this.fCMBuilder.setDeclPool(null);
        this.fSchemaHandler.setDeclPool(null);
        if (schemaDVFactory instanceof SchemaDVFactoryImpl) {
            ((SchemaDVFactoryImpl)schemaDVFactory).setDeclPool(null);
        }
        try {
            boolean bl2 = xMLComponentManager.getFeature(CONTINUE_AFTER_FATAL_ERROR);
            this.fErrorReporter.setFeature(CONTINUE_AFTER_FATAL_ERROR, bl2);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            this.fIsCheckedFully = xMLComponentManager.getFeature(SCHEMA_FULL_CHECKING);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fIsCheckedFully = false;
        }
        try {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(xMLComponentManager.getFeature(GENERATE_SYNTHETIC_ANNOTATIONS));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSchemaHandler.setGenerateSyntheticAnnotations(false);
        }
        this.fSchemaHandler.reset(xMLComponentManager);
    }

    private boolean parserSettingsUpdated(XMLComponentManager xMLComponentManager) {
        if (xMLComponentManager != this.fLoaderConfig) {
            try {
                return xMLComponentManager.getFeature(PARSER_SETTINGS);
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                // empty catch block
            }
        }
        return true;
    }

    private void initGrammarBucket() {
        if (this.fGrammarPool != null) {
            Grammar[] grammarArray = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/2001/XMLSchema");
            int n = grammarArray != null ? grammarArray.length : 0;
            for (int i = 0; i < n; ++i) {
                if (this.fGrammarBucket.putGrammar((SchemaGrammar)grammarArray[i], true)) continue;
                this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
            }
        }
    }

    @Override
    public DOMConfiguration getConfig() {
        return this;
    }

    @Override
    public XSModel load(LSInput lSInput) {
        try {
            Grammar grammar = this.loadGrammar(this.dom2xmlInputSource(lSInput));
            return ((XSGrammar)grammar).toXSModel();
        }
        catch (Exception exception) {
            this.reportDOMFatalError(exception);
            return null;
        }
    }

    @Override
    public XSModel loadInputList(LSInputList lSInputList) {
        int n = lSInputList.getLength();
        SchemaGrammar[] schemaGrammarArray = new SchemaGrammar[n];
        for (int i = 0; i < n; ++i) {
            try {
                schemaGrammarArray[i] = (SchemaGrammar)this.loadGrammar(this.dom2xmlInputSource(lSInputList.item(i)));
                continue;
            }
            catch (Exception exception) {
                this.reportDOMFatalError(exception);
                return null;
            }
        }
        return new XSModelImpl(schemaGrammarArray);
    }

    @Override
    public XSModel loadURI(String string) {
        try {
            Grammar grammar = this.loadGrammar(new XMLInputSource(null, string, null));
            return ((XSGrammar)grammar).toXSModel();
        }
        catch (Exception exception) {
            this.reportDOMFatalError(exception);
            return null;
        }
    }

    @Override
    public XSModel loadURIList(StringList stringList) {
        int n = stringList.getLength();
        SchemaGrammar[] schemaGrammarArray = new SchemaGrammar[n];
        for (int i = 0; i < n; ++i) {
            try {
                schemaGrammarArray[i] = (SchemaGrammar)this.loadGrammar(new XMLInputSource(null, stringList.item(i), null));
                continue;
            }
            catch (Exception exception) {
                this.reportDOMFatalError(exception);
                return null;
            }
        }
        return new XSModelImpl(schemaGrammarArray);
    }

    void reportDOMFatalError(Exception exception) {
        if (this.fErrorHandler != null) {
            DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
            dOMErrorImpl.fException = exception;
            dOMErrorImpl.fMessage = exception.getMessage();
            dOMErrorImpl.fSeverity = (short)3;
            this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
        }
    }

    @Override
    public boolean canSetParameter(String string, Object object) {
        if (object instanceof Boolean) {
            return string.equals("validate") || string.equals(SCHEMA_FULL_CHECKING) || string.equals(VALIDATE_ANNOTATIONS) || string.equals(CONTINUE_AFTER_FATAL_ERROR) || string.equals(ALLOW_JAVA_ENCODINGS) || string.equals(STANDARD_URI_CONFORMANT_FEATURE) || string.equals(GENERATE_SYNTHETIC_ANNOTATIONS) || string.equals(HONOUR_ALL_SCHEMALOCATIONS) || string.equals(NAMESPACE_GROWTH) || string.equals(TOLERATE_DUPLICATES);
        }
        return string.equals("error-handler") || string.equals("resource-resolver") || string.equals(SYMBOL_TABLE) || string.equals(ERROR_REPORTER) || string.equals(ERROR_HANDLER) || string.equals(ENTITY_RESOLVER) || string.equals(XMLGRAMMAR_POOL) || string.equals(SCHEMA_LOCATION) || string.equals(SCHEMA_NONS_LOCATION) || string.equals(JAXP_SCHEMA_SOURCE) || string.equals(SCHEMA_DV_FACTORY);
    }

    @Override
    public Object getParameter(String string) throws DOMException {
        if (string.equals("error-handler")) {
            return this.fErrorHandler != null ? this.fErrorHandler.getErrorHandler() : null;
        }
        if (string.equals("resource-resolver")) {
            return this.fResourceResolver != null ? this.fResourceResolver.getEntityResolver() : null;
        }
        try {
            boolean bl = this.getFeature(string);
            return bl ? Boolean.TRUE : Boolean.FALSE;
        }
        catch (Exception exception) {
            try {
                Object object = this.getProperty(string);
                return object;
            }
            catch (Exception exception2) {
                String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{string});
                throw new DOMException(9, string2);
            }
        }
    }

    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("validate");
            arrayList.add("error-handler");
            arrayList.add("resource-resolver");
            arrayList.add(SYMBOL_TABLE);
            arrayList.add(ERROR_REPORTER);
            arrayList.add(ERROR_HANDLER);
            arrayList.add(ENTITY_RESOLVER);
            arrayList.add(XMLGRAMMAR_POOL);
            arrayList.add(SCHEMA_LOCATION);
            arrayList.add(SCHEMA_NONS_LOCATION);
            arrayList.add(JAXP_SCHEMA_SOURCE);
            arrayList.add(SCHEMA_FULL_CHECKING);
            arrayList.add(CONTINUE_AFTER_FATAL_ERROR);
            arrayList.add(ALLOW_JAVA_ENCODINGS);
            arrayList.add(STANDARD_URI_CONFORMANT_FEATURE);
            arrayList.add(VALIDATE_ANNOTATIONS);
            arrayList.add(GENERATE_SYNTHETIC_ANNOTATIONS);
            arrayList.add(HONOUR_ALL_SCHEMALOCATIONS);
            arrayList.add(NAMESPACE_GROWTH);
            arrayList.add(TOLERATE_DUPLICATES);
            this.fRecognizedParameters = new DOMStringListImpl(arrayList);
        }
        return this.fRecognizedParameters;
    }

    @Override
    public void setParameter(String string, Object object) throws DOMException {
        if (object instanceof Boolean) {
            boolean bl = (Boolean)object;
            if (string.equals("validate") && bl) {
                return;
            }
            try {
                this.setFeature(string, bl);
            }
            catch (Exception exception) {
                String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{string});
                throw new DOMException(9, string2);
            }
            return;
        }
        if (string.equals("error-handler")) {
            if (object instanceof DOMErrorHandler) {
                try {
                    this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)object);
                    this.setErrorHandler(this.fErrorHandler);
                }
                catch (XMLConfigurationException xMLConfigurationException) {}
            } else {
                String string3 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{string});
                throw new DOMException(9, string3);
            }
            return;
        }
        if (string.equals("resource-resolver")) {
            if (object instanceof LSResourceResolver) {
                try {
                    this.fResourceResolver = new DOMEntityResolverWrapper((LSResourceResolver)object);
                    this.setEntityResolver(this.fResourceResolver);
                }
                catch (XMLConfigurationException xMLConfigurationException) {}
            } else {
                String string4 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{string});
                throw new DOMException(9, string4);
            }
            return;
        }
        try {
            this.setProperty(string, object);
        }
        catch (Exception exception) {
            String string5 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "FEATURE_NOT_SUPPORTED", new Object[]{string});
            throw new DOMException(9, string5);
        }
    }

    XMLInputSource dom2xmlInputSource(LSInput lSInput) {
        XMLInputSource xMLInputSource = null;
        xMLInputSource = lSInput.getCharacterStream() != null ? new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI(), lSInput.getCharacterStream(), "UTF-16") : (lSInput.getByteStream() != null ? new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI(), lSInput.getByteStream(), lSInput.getEncoding()) : (lSInput.getStringData() != null && lSInput.getStringData().length() != 0 ? new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI(), new StringReader(lSInput.getStringData()), "UTF-16") : new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI())));
        return xMLInputSource;
    }

    @Override
    public XSElementDecl getGlobalElementDecl(QName qName) {
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(qName.uri);
        if (schemaGrammar != null) {
            return schemaGrammar.getGlobalElementDecl(qName.localpart);
        }
        return null;
    }

    static class LocationArray {
        int length;
        String[] locations = new String[2];

        LocationArray() {
        }

        public void resize(int n, int n2) {
            String[] stringArray = new String[n2];
            System.arraycopy(this.locations, 0, stringArray, 0, Math.min(n, n2));
            this.locations = stringArray;
            this.length = Math.min(n, n2);
        }

        public void addLocation(String string) {
            if (this.length >= this.locations.length) {
                this.resize(this.length, Math.max(1, this.length * 2));
            }
            this.locations[this.length++] = string;
        }

        public String[] getLocationArray() {
            if (this.length < this.locations.length) {
                this.resize(this.locations.length, this.length);
            }
            return this.locations;
        }

        public String getFirstLocation() {
            return this.length > 0 ? this.locations[0] : null;
        }

        public int getLength() {
            return this.length;
        }
    }
}

