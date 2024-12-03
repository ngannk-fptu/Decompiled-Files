/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.parsers;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.xerces.dom.DOMErrorImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.DOMStringListImpl;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.parsers.AbstractDOMParser;
import org.apache.xerces.parsers.ObjectFactory;
import org.apache.xerces.util.DOMEntityResolverWrapper;
import org.apache.xerces.util.DOMErrorHandlerWrapper;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.DOMErrorHandler;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMStringList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.ls.LSException;
import org.w3c.dom.ls.LSInput;
import org.w3c.dom.ls.LSParser;
import org.w3c.dom.ls.LSParserFilter;
import org.w3c.dom.ls.LSResourceResolver;

public class DOMParserImpl
extends AbstractDOMParser
implements LSParser,
DOMConfiguration {
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String VALIDATION_FEATURE = "http://xml.org/sax/features/validation";
    protected static final String XMLSCHEMA = "http://apache.org/xml/features/validation/schema";
    protected static final String XMLSCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String DISALLOW_DOCTYPE_DECL_FEATURE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String PSVI_AUGMENT = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected boolean fNamespaceDeclarations = true;
    protected String fSchemaType = null;
    protected boolean fBusy = false;
    private boolean abortNow = false;
    private Thread currentThread;
    protected static final boolean DEBUG = false;
    private String fSchemaLocation = null;
    private DOMStringList fRecognizedParameters;
    private boolean fNullFilterInUse = false;
    private AbortHandler abortHandler = null;

    public DOMParserImpl(String string, String string2) {
        this((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", string));
        if (string2 != null) {
            if (string2.equals(Constants.NS_DTD)) {
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                this.fSchemaType = Constants.NS_DTD;
            } else if (string2.equals(Constants.NS_XMLSCHEMA)) {
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
            }
        }
    }

    public DOMParserImpl(XMLParserConfiguration xMLParserConfiguration) {
        super(xMLParserConfiguration);
        String[] stringArray = new String[]{"canonical-form", "cdata-sections", "charset-overrides-xml-encoding", "infoset", "namespace-declarations", "split-cdata-sections", "supported-media-types-only", "certified", "well-formed", "ignore-unknown-character-denormalizations"};
        this.fConfiguration.addRecognizedFeatures(stringArray);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/defer-node-expansion", false);
        this.fConfiguration.setFeature("namespace-declarations", true);
        this.fConfiguration.setFeature("well-formed", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
        this.fConfiguration.setFeature(NAMESPACES, true);
        this.fConfiguration.setFeature(DYNAMIC_VALIDATION, false);
        this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
        this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", false);
        this.fConfiguration.setFeature("canonical-form", false);
        this.fConfiguration.setFeature("charset-overrides-xml-encoding", true);
        this.fConfiguration.setFeature("split-cdata-sections", true);
        this.fConfiguration.setFeature("supported-media-types-only", false);
        this.fConfiguration.setFeature("ignore-unknown-character-denormalizations", true);
        this.fConfiguration.setFeature("certified", true);
        try {
            this.fConfiguration.setFeature(NORMALIZE_DATA, false);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    public DOMParserImpl(SymbolTable symbolTable) {
        this((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
    }

    public DOMParserImpl(SymbolTable symbolTable, XMLGrammarPool xMLGrammarPool) {
        this((XMLParserConfiguration)ObjectFactory.createObject("org.apache.xerces.xni.parser.XMLParserConfiguration", "org.apache.xerces.parsers.XIncludeAwareParserConfiguration"));
        this.fConfiguration.setProperty(SYMBOL_TABLE, symbolTable);
        this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/grammar-pool", xMLGrammarPool);
    }

    @Override
    public void reset() {
        super.reset();
        this.fNamespaceDeclarations = this.fConfiguration.getFeature("namespace-declarations");
        if (this.fNullFilterInUse) {
            this.fDOMFilter = null;
            this.fNullFilterInUse = false;
        }
        if (this.fSkippedElemStack != null) {
            this.fSkippedElemStack.removeAllElements();
        }
        this.fRejectedElementDepth = 0;
        this.fFilterReject = false;
        this.fSchemaType = null;
    }

    @Override
    public DOMConfiguration getDomConfig() {
        return this;
    }

    @Override
    public LSParserFilter getFilter() {
        return !this.fNullFilterInUse ? this.fDOMFilter : null;
    }

    @Override
    public void setFilter(LSParserFilter lSParserFilter) {
        if (this.fBusy && lSParserFilter == null && this.fDOMFilter != null) {
            this.fNullFilterInUse = true;
            this.fDOMFilter = NullLSParserFilter.INSTANCE;
        } else {
            this.fDOMFilter = lSParserFilter;
        }
        if (this.fSkippedElemStack == null) {
            this.fSkippedElemStack = new Stack();
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    public void setParameter(String string, Object object) throws DOMException {
        if (object instanceof Boolean) {
            boolean bl = (Boolean)object;
            try {
                if (string.equalsIgnoreCase("comments")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", bl);
                    return;
                }
                if (string.equalsIgnoreCase("datatype-normalization")) {
                    this.fConfiguration.setFeature(NORMALIZE_DATA, bl);
                    return;
                }
                if (string.equalsIgnoreCase("entities")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", bl);
                    return;
                }
                if (string.equalsIgnoreCase("disallow-doctype")) {
                    this.fConfiguration.setFeature(DISALLOW_DOCTYPE_DECL_FEATURE, bl);
                    return;
                }
                if (string.equalsIgnoreCase("supported-media-types-only") || string.equalsIgnoreCase("normalize-characters") || string.equalsIgnoreCase("check-character-normalization") || string.equalsIgnoreCase("canonical-form")) {
                    if (!bl) return;
                    throw DOMParserImpl.newFeatureNotSupportedError(string);
                }
                if (string.equalsIgnoreCase("namespaces")) {
                    this.fConfiguration.setFeature(NAMESPACES, bl);
                    return;
                }
                if (string.equalsIgnoreCase("infoset")) {
                    if (!bl) return;
                    this.fConfiguration.setFeature(NAMESPACES, true);
                    this.fConfiguration.setFeature("namespace-declarations", true);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/include-comments", true);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", true);
                    this.fConfiguration.setFeature(DYNAMIC_VALIDATION, false);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
                    this.fConfiguration.setFeature(NORMALIZE_DATA, false);
                    this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", false);
                    return;
                }
                if (string.equalsIgnoreCase("cdata-sections")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/create-cdata-nodes", bl);
                    return;
                }
                if (string.equalsIgnoreCase("namespace-declarations")) {
                    this.fConfiguration.setFeature("namespace-declarations", bl);
                    return;
                }
                if (string.equalsIgnoreCase("well-formed") || string.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                    if (bl) return;
                    throw DOMParserImpl.newFeatureNotSupportedError(string);
                }
                if (string.equalsIgnoreCase("validate")) {
                    this.fConfiguration.setFeature(VALIDATION_FEATURE, bl);
                    if (this.fSchemaType != Constants.NS_DTD) {
                        this.fConfiguration.setFeature(XMLSCHEMA, bl);
                        this.fConfiguration.setFeature(XMLSCHEMA_FULL_CHECKING, bl);
                    }
                    if (!bl) return;
                    this.fConfiguration.setFeature(DYNAMIC_VALIDATION, false);
                    return;
                }
                if (string.equalsIgnoreCase("validate-if-schema")) {
                    this.fConfiguration.setFeature(DYNAMIC_VALIDATION, bl);
                    if (!bl) return;
                    this.fConfiguration.setFeature(VALIDATION_FEATURE, false);
                    return;
                }
                if (string.equalsIgnoreCase("element-content-whitespace")) {
                    this.fConfiguration.setFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace", bl);
                    return;
                }
                if (string.equalsIgnoreCase("psvi")) {
                    this.fConfiguration.setFeature(PSVI_AUGMENT, true);
                    this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", "org.apache.xerces.dom.PSVIDocumentImpl");
                    return;
                }
                String string2 = string.equalsIgnoreCase(HONOUR_ALL_SCHEMALOCATIONS) ? HONOUR_ALL_SCHEMALOCATIONS : (string.equals(NAMESPACE_GROWTH) ? NAMESPACE_GROWTH : (string.equals(TOLERATE_DUPLICATES) ? TOLERATE_DUPLICATES : string.toLowerCase(Locale.ENGLISH)));
                this.fConfiguration.setFeature(string2, bl);
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                throw DOMParserImpl.newFeatureNotFoundError(string);
            }
        }
        if (string.equalsIgnoreCase("error-handler")) {
            if (!(object instanceof DOMErrorHandler) && object != null) throw DOMParserImpl.newTypeMismatchError(string);
            try {
                this.fErrorHandler = new DOMErrorHandlerWrapper((DOMErrorHandler)object);
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/error-handler", this.fErrorHandler);
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase("resource-resolver")) {
            if (!(object instanceof LSResourceResolver) && object != null) throw DOMParserImpl.newTypeMismatchError(string);
            try {
                this.fConfiguration.setProperty("http://apache.org/xml/properties/internal/entity-resolver", new DOMEntityResolverWrapper((LSResourceResolver)object));
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase("schema-location")) {
            if (!(object instanceof String) && object != null) throw DOMParserImpl.newTypeMismatchError(string);
            try {
                if (object == null) {
                    this.fSchemaLocation = null;
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", null);
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
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", arrayList.toArray());
                    return;
                }
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaSource", object);
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase("schema-type")) {
            if (!(object instanceof String) && object != null) throw DOMParserImpl.newTypeMismatchError(string);
            try {
                if (object == null) {
                    this.fConfiguration.setFeature(XMLSCHEMA, false);
                    this.fConfiguration.setFeature(XMLSCHEMA_FULL_CHECKING, false);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", null);
                    this.fSchemaType = null;
                    return;
                }
                if (object.equals(Constants.NS_XMLSCHEMA)) {
                    this.fConfiguration.setFeature(XMLSCHEMA, true);
                    this.fConfiguration.setFeature(XMLSCHEMA_FULL_CHECKING, true);
                    this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_XMLSCHEMA);
                    this.fSchemaType = Constants.NS_XMLSCHEMA;
                    return;
                }
                if (!object.equals(Constants.NS_DTD)) return;
                this.fConfiguration.setFeature(XMLSCHEMA, false);
                this.fConfiguration.setFeature(XMLSCHEMA_FULL_CHECKING, false);
                this.fConfiguration.setProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage", Constants.NS_DTD);
                this.fSchemaType = Constants.NS_DTD;
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
            return;
        }
        if (string.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
            this.fConfiguration.setProperty("http://apache.org/xml/properties/dom/document-class-name", object);
            return;
        } else {
            String string3 = string.toLowerCase(Locale.ENGLISH);
            try {
                this.fConfiguration.setProperty(string3, object);
                return;
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                try {
                    if (string.equalsIgnoreCase(HONOUR_ALL_SCHEMALOCATIONS)) {
                        string3 = HONOUR_ALL_SCHEMALOCATIONS;
                    } else if (string.equals(NAMESPACE_GROWTH)) {
                        string3 = NAMESPACE_GROWTH;
                    } else if (string.equals(TOLERATE_DUPLICATES)) {
                        string3 = TOLERATE_DUPLICATES;
                    }
                    this.fConfiguration.getFeature(string3);
                    throw DOMParserImpl.newTypeMismatchError(string);
                }
                catch (XMLConfigurationException xMLConfigurationException2) {
                    throw DOMParserImpl.newFeatureNotFoundError(string);
                }
            }
        }
    }

    @Override
    public Object getParameter(String string) throws DOMException {
        if (string.equalsIgnoreCase("comments")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("datatype-normalization")) {
            return this.fConfiguration.getFeature(NORMALIZE_DATA) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("entities")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("namespaces")) {
            return this.fConfiguration.getFeature(NAMESPACES) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("validate")) {
            return this.fConfiguration.getFeature(VALIDATION_FEATURE) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("validate-if-schema")) {
            return this.fConfiguration.getFeature(DYNAMIC_VALIDATION) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("element-content-whitespace")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("disallow-doctype")) {
            return this.fConfiguration.getFeature(DISALLOW_DOCTYPE_DECL_FEATURE) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("infoset")) {
            boolean bl = this.fConfiguration.getFeature(NAMESPACES) && this.fConfiguration.getFeature("namespace-declarations") && this.fConfiguration.getFeature("http://apache.org/xml/features/include-comments") && this.fConfiguration.getFeature("http://apache.org/xml/features/dom/include-ignorable-whitespace") && !this.fConfiguration.getFeature(DYNAMIC_VALIDATION) && !this.fConfiguration.getFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes") && !this.fConfiguration.getFeature(NORMALIZE_DATA) && !this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes");
            return bl ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("cdata-sections")) {
            return this.fConfiguration.getFeature("http://apache.org/xml/features/create-cdata-nodes") ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("check-character-normalization") || string.equalsIgnoreCase("normalize-characters")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("namespace-declarations") || string.equalsIgnoreCase("well-formed") || string.equalsIgnoreCase("ignore-unknown-character-denormalizations") || string.equalsIgnoreCase("canonical-form") || string.equalsIgnoreCase("supported-media-types-only") || string.equalsIgnoreCase("split-cdata-sections") || string.equalsIgnoreCase("charset-overrides-xml-encoding")) {
            return this.fConfiguration.getFeature(string.toLowerCase(Locale.ENGLISH)) ? Boolean.TRUE : Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("error-handler")) {
            if (this.fErrorHandler != null) {
                return this.fErrorHandler.getErrorHandler();
            }
            return null;
        }
        if (string.equalsIgnoreCase("resource-resolver")) {
            try {
                XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)this.fConfiguration.getProperty("http://apache.org/xml/properties/internal/entity-resolver");
                if (xMLEntityResolver != null && xMLEntityResolver instanceof DOMEntityResolverWrapper) {
                    return ((DOMEntityResolverWrapper)xMLEntityResolver).getEntityResolver();
                }
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                // empty catch block
            }
            return null;
        }
        if (string.equalsIgnoreCase("schema-type")) {
            return this.fConfiguration.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
        }
        if (string.equalsIgnoreCase("schema-location")) {
            return this.fSchemaLocation;
        }
        if (string.equalsIgnoreCase(SYMBOL_TABLE)) {
            return this.fConfiguration.getProperty(SYMBOL_TABLE);
        }
        if (string.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
            return this.fConfiguration.getProperty("http://apache.org/xml/properties/dom/document-class-name");
        }
        String string2 = string.equalsIgnoreCase(HONOUR_ALL_SCHEMALOCATIONS) ? HONOUR_ALL_SCHEMALOCATIONS : (string.equals(NAMESPACE_GROWTH) ? NAMESPACE_GROWTH : (string.equals(TOLERATE_DUPLICATES) ? TOLERATE_DUPLICATES : string.toLowerCase(Locale.ENGLISH)));
        try {
            return this.fConfiguration.getFeature(string2) ? Boolean.TRUE : Boolean.FALSE;
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            try {
                return this.fConfiguration.getProperty(string2);
            }
            catch (XMLConfigurationException xMLConfigurationException2) {
                throw DOMParserImpl.newFeatureNotFoundError(string);
            }
        }
    }

    @Override
    public boolean canSetParameter(String string, Object object) {
        if (object == null) {
            return true;
        }
        if (object instanceof Boolean) {
            boolean bl = (Boolean)object;
            if (string.equalsIgnoreCase("supported-media-types-only") || string.equalsIgnoreCase("normalize-characters") || string.equalsIgnoreCase("check-character-normalization") || string.equalsIgnoreCase("canonical-form")) {
                return !bl;
            }
            if (string.equalsIgnoreCase("well-formed") || string.equalsIgnoreCase("ignore-unknown-character-denormalizations")) {
                return bl;
            }
            if (string.equalsIgnoreCase("cdata-sections") || string.equalsIgnoreCase("charset-overrides-xml-encoding") || string.equalsIgnoreCase("comments") || string.equalsIgnoreCase("datatype-normalization") || string.equalsIgnoreCase("disallow-doctype") || string.equalsIgnoreCase("entities") || string.equalsIgnoreCase("infoset") || string.equalsIgnoreCase("namespaces") || string.equalsIgnoreCase("namespace-declarations") || string.equalsIgnoreCase("validate") || string.equalsIgnoreCase("validate-if-schema") || string.equalsIgnoreCase("element-content-whitespace") || string.equalsIgnoreCase("xml-declaration")) {
                return true;
            }
            try {
                String string2 = string.equalsIgnoreCase(HONOUR_ALL_SCHEMALOCATIONS) ? HONOUR_ALL_SCHEMALOCATIONS : (string.equalsIgnoreCase(NAMESPACE_GROWTH) ? NAMESPACE_GROWTH : (string.equalsIgnoreCase(TOLERATE_DUPLICATES) ? TOLERATE_DUPLICATES : string.toLowerCase(Locale.ENGLISH)));
                this.fConfiguration.getFeature(string2);
                return true;
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                return false;
            }
        }
        if (string.equalsIgnoreCase("error-handler")) {
            return object instanceof DOMErrorHandler || object == null;
        }
        if (string.equalsIgnoreCase("resource-resolver")) {
            return object instanceof LSResourceResolver || object == null;
        }
        if (string.equalsIgnoreCase("schema-type")) {
            return object instanceof String && (object.equals(Constants.NS_XMLSCHEMA) || object.equals(Constants.NS_DTD)) || object == null;
        }
        if (string.equalsIgnoreCase("schema-location")) {
            return object instanceof String || object == null;
        }
        if (string.equalsIgnoreCase("http://apache.org/xml/properties/dom/document-class-name")) {
            return true;
        }
        try {
            this.fConfiguration.getProperty(string.toLowerCase(Locale.ENGLISH));
            return true;
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            return false;
        }
    }

    @Override
    public DOMStringList getParameterNames() {
        if (this.fRecognizedParameters == null) {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add("namespaces");
            arrayList.add("cdata-sections");
            arrayList.add("canonical-form");
            arrayList.add("namespace-declarations");
            arrayList.add("split-cdata-sections");
            arrayList.add("entities");
            arrayList.add("validate-if-schema");
            arrayList.add("validate");
            arrayList.add("datatype-normalization");
            arrayList.add("charset-overrides-xml-encoding");
            arrayList.add("check-character-normalization");
            arrayList.add("supported-media-types-only");
            arrayList.add("ignore-unknown-character-denormalizations");
            arrayList.add("normalize-characters");
            arrayList.add("well-formed");
            arrayList.add("infoset");
            arrayList.add("disallow-doctype");
            arrayList.add("element-content-whitespace");
            arrayList.add("comments");
            arrayList.add("error-handler");
            arrayList.add("resource-resolver");
            arrayList.add("schema-location");
            arrayList.add("schema-type");
            this.fRecognizedParameters = new DOMStringListImpl(arrayList);
        }
        return this.fRecognizedParameters;
    }

    @Override
    public Document parseURI(String string) throws LSException {
        block7: {
            if (this.fBusy) {
                throw DOMParserImpl.newInvalidStateError();
            }
            XMLInputSource xMLInputSource = new XMLInputSource(null, string, null);
            try {
                this.currentThread = Thread.currentThread();
                this.fBusy = true;
                this.parse(xMLInputSource);
                this.fBusy = false;
                if (this.abortNow && this.currentThread.isInterrupted()) {
                    this.abortNow = false;
                    Thread.interrupted();
                }
            }
            catch (Exception exception) {
                this.fBusy = false;
                if (this.abortNow && this.currentThread.isInterrupted()) {
                    Thread.interrupted();
                }
                if (this.abortNow) {
                    this.abortNow = false;
                    this.restoreHandlers();
                    return null;
                }
                if (exception == AbstractDOMParser.Abort.INSTANCE) break block7;
                if (!(exception instanceof XMLParseException) && this.fErrorHandler != null) {
                    DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
                    dOMErrorImpl.fException = exception;
                    dOMErrorImpl.fMessage = exception.getMessage();
                    dOMErrorImpl.fSeverity = (short)3;
                    this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
                }
                throw (LSException)DOMUtil.createLSException((short)81, exception).fillInStackTrace();
            }
        }
        Document document = this.getDocument();
        this.dropDocumentReferences();
        return document;
    }

    @Override
    public Document parse(LSInput lSInput) throws LSException {
        block7: {
            XMLInputSource xMLInputSource = this.dom2xmlInputSource(lSInput);
            if (this.fBusy) {
                throw DOMParserImpl.newInvalidStateError();
            }
            try {
                this.currentThread = Thread.currentThread();
                this.fBusy = true;
                this.parse(xMLInputSource);
                this.fBusy = false;
                if (this.abortNow && this.currentThread.isInterrupted()) {
                    this.abortNow = false;
                    Thread.interrupted();
                }
            }
            catch (Exception exception) {
                this.fBusy = false;
                if (this.abortNow && this.currentThread.isInterrupted()) {
                    Thread.interrupted();
                }
                if (this.abortNow) {
                    this.abortNow = false;
                    this.restoreHandlers();
                    return null;
                }
                if (exception == AbstractDOMParser.Abort.INSTANCE) break block7;
                if (!(exception instanceof XMLParseException) && this.fErrorHandler != null) {
                    DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
                    dOMErrorImpl.fException = exception;
                    dOMErrorImpl.fMessage = exception.getMessage();
                    dOMErrorImpl.fSeverity = (short)3;
                    this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
                }
                throw (LSException)DOMUtil.createLSException((short)81, exception).fillInStackTrace();
            }
        }
        Document document = this.getDocument();
        this.dropDocumentReferences();
        return document;
    }

    private void restoreHandlers() {
        this.fConfiguration.setDocumentHandler(this);
        this.fConfiguration.setDTDHandler(this);
        this.fConfiguration.setDTDContentModelHandler(this);
    }

    @Override
    public Node parseWithContext(LSInput lSInput, Node node, short s) throws DOMException, LSException {
        throw new DOMException(9, "Not supported");
    }

    XMLInputSource dom2xmlInputSource(LSInput lSInput) {
        XMLInputSource xMLInputSource = null;
        if (lSInput.getCharacterStream() != null) {
            xMLInputSource = new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI(), lSInput.getCharacterStream(), "UTF-16");
        } else if (lSInput.getByteStream() != null) {
            xMLInputSource = new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI(), lSInput.getByteStream(), lSInput.getEncoding());
        } else if (lSInput.getStringData() != null && lSInput.getStringData().length() > 0) {
            xMLInputSource = new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI(), new StringReader(lSInput.getStringData()), "UTF-16");
        } else if (lSInput.getSystemId() != null && lSInput.getSystemId().length() > 0 || lSInput.getPublicId() != null && lSInput.getPublicId().length() > 0) {
            xMLInputSource = new XMLInputSource(lSInput.getPublicId(), lSInput.getSystemId(), lSInput.getBaseURI());
        } else {
            if (this.fErrorHandler != null) {
                DOMErrorImpl dOMErrorImpl = new DOMErrorImpl();
                dOMErrorImpl.fType = "no-input-specified";
                dOMErrorImpl.fMessage = "no-input-specified";
                dOMErrorImpl.fSeverity = (short)3;
                this.fErrorHandler.getErrorHandler().handleError(dOMErrorImpl);
            }
            throw new LSException(81, "no-input-specified");
        }
        return xMLInputSource;
    }

    @Override
    public boolean getAsync() {
        return false;
    }

    @Override
    public boolean getBusy() {
        return this.fBusy;
    }

    @Override
    public void abort() {
        if (this.fBusy) {
            this.fBusy = false;
            if (this.currentThread != null) {
                this.abortNow = true;
                if (this.abortHandler == null) {
                    this.abortHandler = new AbortHandler();
                }
                this.fConfiguration.setDocumentHandler(this.abortHandler);
                this.fConfiguration.setDTDHandler(this.abortHandler);
                this.fConfiguration.setDTDContentModelHandler(this.abortHandler);
                if (this.currentThread == Thread.currentThread()) {
                    throw AbstractDOMParser.Abort.INSTANCE;
                }
                this.currentThread.interrupt();
            }
        }
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) {
        if (!this.fNamespaceDeclarations && this.fNamespaceAware) {
            int n = xMLAttributes.getLength();
            for (int i = n - 1; i >= 0; --i) {
                if (XMLSymbols.PREFIX_XMLNS != xMLAttributes.getPrefix(i) && XMLSymbols.PREFIX_XMLNS != xMLAttributes.getQName(i)) continue;
                xMLAttributes.removeAttributeAt(i);
            }
        }
        super.startElement(qName, xMLAttributes, augmentations);
    }

    private static DOMException newInvalidStateError() {
        String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "INVALID_STATE_ERR", null);
        throw new DOMException(11, string);
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

    private static final class AbortHandler
    implements XMLDocumentHandler,
    XMLDTDHandler,
    XMLDTDContentModelHandler {
        private XMLDocumentSource documentSource;
        private XMLDTDContentModelSource dtdContentSource;
        private XMLDTDSource dtdSource;

        private AbortHandler() {
        }

        @Override
        public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endElement(QName qName, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startCDATA(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endCDATA(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endDocument(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
            this.documentSource = xMLDocumentSource;
        }

        @Override
        public XMLDocumentSource getDocumentSource() {
            return this.documentSource;
        }

        @Override
        public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endExternalSubset(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startAttlist(String string, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endAttlist(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startConditional(short s, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endConditional(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endDTD(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void setDTDSource(XMLDTDSource xMLDTDSource) {
            this.dtdSource = xMLDTDSource;
        }

        @Override
        public XMLDTDSource getDTDSource() {
            return this.dtdSource;
        }

        @Override
        public void startContentModel(String string, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void any(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void empty(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void startGroup(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void pcdata(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void element(String string, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void separator(short s, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void occurrence(short s, Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endGroup(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void endContentModel(Augmentations augmentations) throws XNIException {
            throw AbstractDOMParser.Abort.INSTANCE;
        }

        @Override
        public void setDTDContentModelSource(XMLDTDContentModelSource xMLDTDContentModelSource) {
            this.dtdContentSource = xMLDTDContentModelSource;
        }

        @Override
        public XMLDTDContentModelSource getDTDContentModelSource() {
            return this.dtdContentSource;
        }
    }

    static final class NullLSParserFilter
    implements LSParserFilter {
        static final NullLSParserFilter INSTANCE = new NullLSParserFilter();

        private NullLSParserFilter() {
        }

        @Override
        public short acceptNode(Node node) {
            return 1;
        }

        @Override
        public int getWhatToShow() {
            return -1;
        }

        @Override
        public short startElement(Element element) {
            return 1;
        }
    }
}

