/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.xinclude;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Stack;
import java.util.StringTokenizer;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.HTTPInputSource;
import org.apache.xerces.util.IntStack;
import org.apache.xerces.util.ParserConfigurationSettings;
import org.apache.xerces.util.SecurityManager;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLLocatorWrapper;
import org.apache.xerces.util.XMLResourceIdentifierImpl;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xinclude.XIncludeMessageFormatter;
import org.apache.xerces.xinclude.XIncludeNamespaceSupport;
import org.apache.xerces.xinclude.XIncludeTextReader;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLDTDSource;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParserConfiguration;
import org.apache.xerces.xpointer.XPointerProcessor;

public class XIncludeHandler
implements XMLComponent,
XMLDocumentFilter,
XMLDTDFilter {
    public static final String XINCLUDE_DEFAULT_CONFIGURATION = "org.apache.xerces.parsers.XIncludeParserConfiguration";
    public static final String HTTP_ACCEPT = "Accept";
    public static final String HTTP_ACCEPT_LANGUAGE = "Accept-Language";
    public static final String XPOINTER = "xpointer";
    public static final String XINCLUDE_NS_URI = "http://www.w3.org/2001/XInclude".intern();
    public static final String XINCLUDE_INCLUDE = "include".intern();
    public static final String XINCLUDE_FALLBACK = "fallback".intern();
    public static final String XINCLUDE_PARSE_XML = "xml".intern();
    public static final String XINCLUDE_PARSE_TEXT = "text".intern();
    public static final String XINCLUDE_ATTR_HREF = "href".intern();
    public static final String XINCLUDE_ATTR_PARSE = "parse".intern();
    public static final String XINCLUDE_ATTR_ENCODING = "encoding".intern();
    public static final String XINCLUDE_ATTR_ACCEPT = "accept".intern();
    public static final String XINCLUDE_ATTR_ACCEPT_LANGUAGE = "accept-language".intern();
    public static final String XINCLUDE_INCLUDED = "[included]".intern();
    public static final String CURRENT_BASE_URI = "currentBaseURI";
    private static final String XINCLUDE_BASE = "base".intern();
    private static final QName XML_BASE_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_BASE, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_BASE).intern(), NamespaceContext.XML_URI);
    private static final String XINCLUDE_LANG = "lang".intern();
    private static final QName XML_LANG_QNAME = new QName(XMLSymbols.PREFIX_XML, XINCLUDE_LANG, (XMLSymbols.PREFIX_XML + ":" + XINCLUDE_LANG).intern(), NamespaceContext.XML_URI);
    private static final QName NEW_NS_ATTR_QNAME = new QName(XMLSymbols.PREFIX_XMLNS, "", XMLSymbols.PREFIX_XMLNS + ":", NamespaceContext.XMLNS_URI);
    private static final int STATE_NORMAL_PROCESSING = 1;
    private static final int STATE_IGNORE = 2;
    private static final int STATE_EXPECT_FALLBACK = 3;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String ALLOW_UE_AND_NOTATION_EVENTS = "http://xml.org/sax/features/allow-dtd-events-after-endDTD";
    protected static final String XINCLUDE_FIXUP_BASE_URIS = "http://apache.org/xml/features/xinclude/fixup-base-uris";
    protected static final String XINCLUDE_FIXUP_LANGUAGE = "http://apache.org/xml/features/xinclude/fixup-language";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String BUFFER_SIZE = "http://apache.org/xml/properties/input-buffer-size";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/allow-dtd-events-after-endDTD", "http://apache.org/xml/features/xinclude/fixup-base-uris", "http://apache.org/xml/features/xinclude/fixup-language"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{Boolean.TRUE, Boolean.TRUE, Boolean.TRUE};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/security-manager", "http://apache.org/xml/properties/input-buffer-size"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, new Integer(2048)};
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDSource fDTDSource;
    protected XIncludeHandler fParentXIncludeHandler;
    protected int fBufferSize = 2048;
    protected String fParentRelativeURI;
    protected XMLParserConfiguration fChildConfig;
    protected XMLParserConfiguration fXIncludeChildConfig;
    protected XMLParserConfiguration fXPointerChildConfig;
    protected XPointerProcessor fXPtrProcessor = null;
    protected XMLLocator fDocLocation;
    protected XMLLocatorWrapper fXIncludeLocator = new XMLLocatorWrapper();
    protected XIncludeMessageFormatter fXIncludeMessageFormatter = new XIncludeMessageFormatter();
    protected XIncludeNamespaceSupport fNamespaceContext;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLEntityResolver fEntityResolver;
    protected SecurityManager fSecurityManager;
    protected XIncludeTextReader fXInclude10TextReader;
    protected XIncludeTextReader fXInclude11TextReader;
    protected final XMLResourceIdentifier fCurrentBaseURI;
    protected final IntStack fBaseURIScope;
    protected final Stack fBaseURI;
    protected final Stack fLiteralSystemID;
    protected final Stack fExpandedSystemID;
    protected final IntStack fLanguageScope;
    protected final Stack fLanguageStack;
    protected String fCurrentLanguage;
    protected String fHrefFromParent;
    protected ParserConfigurationSettings fSettings;
    private int fDepth = 0;
    private int fResultDepth;
    private static final int INITIAL_SIZE = 8;
    private boolean[] fSawInclude = new boolean[8];
    private boolean[] fSawFallback = new boolean[8];
    private int[] fState = new int[8];
    private final ArrayList fNotations;
    private final ArrayList fUnparsedEntities;
    private boolean fFixupBaseURIs = true;
    private boolean fFixupLanguage = true;
    private boolean fSendUEAndNotationEvents;
    private boolean fIsXML11;
    private boolean fInDTD;
    boolean fHasIncludeReportedContent;
    private boolean fSeenRootElement;
    private boolean fNeedCopyFeatures = true;
    private static final boolean[] gNeedEscaping = new boolean[128];
    private static final char[] gAfterEscaping1 = new char[128];
    private static final char[] gAfterEscaping2 = new char[128];
    private static final char[] gHexChs = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public XIncludeHandler() {
        this.fSawFallback[this.fDepth] = false;
        this.fSawInclude[this.fDepth] = false;
        this.fState[this.fDepth] = 1;
        this.fNotations = new ArrayList();
        this.fUnparsedEntities = new ArrayList();
        this.fBaseURIScope = new IntStack();
        this.fBaseURI = new Stack();
        this.fLiteralSystemID = new Stack();
        this.fExpandedSystemID = new Stack();
        this.fCurrentBaseURI = new XMLResourceIdentifierImpl();
        this.fLanguageScope = new IntStack();
        this.fLanguageStack = new Stack();
        this.fCurrentLanguage = null;
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XNIException {
        Object object;
        int n;
        this.fNamespaceContext = null;
        this.fDepth = 0;
        this.fResultDepth = this.isRootDocument() ? 0 : this.fParentXIncludeHandler.getResultDepth();
        this.fNotations.clear();
        this.fUnparsedEntities.clear();
        this.fParentRelativeURI = null;
        this.fIsXML11 = false;
        this.fInDTD = false;
        this.fSeenRootElement = false;
        this.fBaseURIScope.clear();
        this.fBaseURI.clear();
        this.fLiteralSystemID.clear();
        this.fExpandedSystemID.clear();
        this.fLanguageScope.clear();
        this.fLanguageStack.clear();
        for (n = 0; n < this.fState.length; ++n) {
            this.fState[n] = 1;
        }
        for (n = 0; n < this.fSawFallback.length; ++n) {
            this.fSawFallback[n] = false;
        }
        for (n = 0; n < this.fSawInclude.length; ++n) {
            this.fSawInclude[n] = false;
        }
        try {
            if (!xMLComponentManager.getFeature(PARSER_SETTINGS)) {
                return;
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        this.fNeedCopyFeatures = true;
        try {
            this.fSendUEAndNotationEvents = xMLComponentManager.getFeature(ALLOW_UE_AND_NOTATION_EVENTS);
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature(ALLOW_UE_AND_NOTATION_EVENTS, this.fSendUEAndNotationEvents);
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            this.fFixupBaseURIs = xMLComponentManager.getFeature(XINCLUDE_FIXUP_BASE_URIS);
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature(XINCLUDE_FIXUP_BASE_URIS, this.fFixupBaseURIs);
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fFixupBaseURIs = true;
        }
        try {
            this.fFixupLanguage = xMLComponentManager.getFeature(XINCLUDE_FIXUP_LANGUAGE);
            if (this.fChildConfig != null) {
                this.fChildConfig.setFeature(XINCLUDE_FIXUP_LANGUAGE, this.fFixupLanguage);
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fFixupLanguage = true;
        }
        try {
            object = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
            if (object != null) {
                this.fSymbolTable = object;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty(SYMBOL_TABLE, object);
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSymbolTable = null;
        }
        try {
            object = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
            if (object != null) {
                this.setErrorReporter((XMLErrorReporter)object);
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty(ERROR_REPORTER, object);
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fErrorReporter = null;
        }
        try {
            object = (XMLEntityResolver)xMLComponentManager.getProperty(ENTITY_RESOLVER);
            if (object != null) {
                this.fEntityResolver = object;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty(ENTITY_RESOLVER, object);
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fEntityResolver = null;
        }
        try {
            object = (SecurityManager)xMLComponentManager.getProperty(SECURITY_MANAGER);
            if (object != null) {
                this.fSecurityManager = object;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty(SECURITY_MANAGER, object);
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSecurityManager = null;
        }
        try {
            object = (Integer)xMLComponentManager.getProperty(BUFFER_SIZE);
            if (object != null && (Integer)object > 0) {
                this.fBufferSize = (Integer)object;
                if (this.fChildConfig != null) {
                    this.fChildConfig.setProperty(BUFFER_SIZE, object);
                }
            } else {
                this.fBufferSize = (Integer)this.getPropertyDefault(BUFFER_SIZE);
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fBufferSize = (Integer)this.getPropertyDefault(BUFFER_SIZE);
        }
        if (this.fXInclude10TextReader != null) {
            this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
        }
        if (this.fXInclude11TextReader != null) {
            this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
        }
        this.fSettings = new ParserConfigurationSettings();
        this.copyFeatures(xMLComponentManager, this.fSettings);
        try {
            if (xMLComponentManager.getFeature(SCHEMA_VALIDATION)) {
                this.fSettings.setFeature(SCHEMA_VALIDATION, false);
                if (Constants.NS_XMLSCHEMA.equals(xMLComponentManager.getProperty(JAXP_SCHEMA_LANGUAGE))) {
                    this.fSettings.setFeature(VALIDATION, false);
                } else if (xMLComponentManager.getFeature(VALIDATION)) {
                    this.fSettings.setFeature(DYNAMIC_VALIDATION, true);
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])RECOGNIZED_FEATURES.clone();
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
        if (string.equals(ALLOW_UE_AND_NOTATION_EVENTS)) {
            this.fSendUEAndNotationEvents = bl;
        }
        if (this.fSettings != null) {
            this.fNeedCopyFeatures = true;
            this.fSettings.setFeature(string, bl);
        }
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.equals(SYMBOL_TABLE)) {
            this.fSymbolTable = (SymbolTable)object;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(string, object);
            }
            return;
        }
        if (string.equals(ERROR_REPORTER)) {
            this.setErrorReporter((XMLErrorReporter)object);
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(string, object);
            }
            return;
        }
        if (string.equals(ENTITY_RESOLVER)) {
            this.fEntityResolver = (XMLEntityResolver)object;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(string, object);
            }
            return;
        }
        if (string.equals(SECURITY_MANAGER)) {
            this.fSecurityManager = (SecurityManager)object;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(string, object);
            }
            return;
        }
        if (string.equals(BUFFER_SIZE)) {
            Integer n = (Integer)object;
            if (this.fChildConfig != null) {
                this.fChildConfig.setProperty(string, object);
            }
            if (n != null && n > 0) {
                this.fBufferSize = n;
                if (this.fXInclude10TextReader != null) {
                    this.fXInclude10TextReader.setBufferSize(this.fBufferSize);
                }
                if (this.fXInclude11TextReader != null) {
                    this.fXInclude11TextReader.setBufferSize(this.fBufferSize);
                }
            }
            return;
        }
    }

    @Override
    public Boolean getFeatureDefault(String string) {
        for (int i = 0; i < RECOGNIZED_FEATURES.length; ++i) {
            if (!RECOGNIZED_FEATURES[i].equals(string)) continue;
            return FEATURE_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public Object getPropertyDefault(String string) {
        for (int i = 0; i < RECOGNIZED_PROPERTIES.length; ++i) {
            if (!RECOGNIZED_PROPERTIES[i].equals(string)) continue;
            return PROPERTY_DEFAULTS[i];
        }
        return null;
    }

    @Override
    public void setDocumentHandler(XMLDocumentHandler xMLDocumentHandler) {
        if (this.fDocumentHandler != xMLDocumentHandler) {
            this.fDocumentHandler = xMLDocumentHandler;
            if (this.fXIncludeChildConfig != null) {
                this.fXIncludeChildConfig.setDocumentHandler(xMLDocumentHandler);
            }
            if (this.fXPointerChildConfig != null) {
                this.fXPointerChildConfig.setDocumentHandler(xMLDocumentHandler);
            }
        }
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
    }

    @Override
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        this.fErrorReporter.setDocumentLocator(xMLLocator);
        if (!(namespaceContext instanceof XIncludeNamespaceSupport)) {
            this.reportFatalError("IncompatibleNamespaceContext");
        }
        this.fNamespaceContext = (XIncludeNamespaceSupport)namespaceContext;
        this.fDocLocation = xMLLocator;
        this.fXIncludeLocator.setLocator(this.fDocLocation);
        this.setupCurrentBaseURI(xMLLocator);
        this.saveBaseURI();
        if (augmentations == null) {
            augmentations = new AugmentationsImpl();
        }
        augmentations.putItem(CURRENT_BASE_URI, this.fCurrentBaseURI);
        if (!this.isRootDocument()) {
            this.fParentXIncludeHandler.fHasIncludeReportedContent = true;
            if (this.fParentXIncludeHandler.searchForRecursiveIncludes(this.fCurrentBaseURI.getExpandedSystemId())) {
                this.reportFatalError("RecursiveInclude", new Object[]{this.fCurrentBaseURI.getExpandedSystemId()});
            }
        }
        this.fCurrentLanguage = XMLSymbols.EMPTY_STRING;
        this.saveLanguage(this.fCurrentLanguage);
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(this.fXIncludeLocator, string, namespaceContext, augmentations);
        }
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.fIsXML11 = "1.1".equals(string);
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        if (this.isRootDocument() && this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.fInDTD) {
            if (this.fDocumentHandler != null && this.getState() == 1) {
                ++this.fDepth;
                augmentations = this.modifyAugmentations(augmentations);
                this.fDocumentHandler.comment(xMLString, augmentations);
                --this.fDepth;
            }
        } else if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(xMLString, augmentations);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (!this.fInDTD) {
            if (this.fDocumentHandler != null && this.getState() == 1) {
                ++this.fDepth;
                augmentations = this.modifyAugmentations(augmentations);
                this.fDocumentHandler.processingInstruction(string, xMLString, augmentations);
                --this.fDepth;
            }
        } else if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(string, xMLString, augmentations);
        }
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        int n = this.getState(this.fDepth - 1);
        if (n == 3 && this.getState(this.fDepth - 2) == 3) {
            this.setState(2);
        } else {
            this.setState(n);
        }
        this.processXMLBaseAttributes(xMLAttributes);
        if (this.fFixupLanguage) {
            this.processXMLLangAttributes(xMLAttributes);
        }
        if (this.isIncludeElement(qName)) {
            boolean bl = this.handleIncludeElement(xMLAttributes);
            if (bl) {
                this.setState(2);
            } else {
                this.setState(3);
            }
        } else if (this.isFallbackElement(qName)) {
            this.handleFallbackElement();
        } else if (this.hasXIncludeNamespace(qName)) {
            if (this.getSawInclude(this.fDepth - 1)) {
                this.reportFatalError("IncludeChild", new Object[]{qName.rawname});
            }
            if (this.getSawFallback(this.fDepth - 1)) {
                this.reportFatalError("FallbackChild", new Object[]{qName.rawname});
            }
            if (this.getState() == 1) {
                if (this.fResultDepth++ == 0) {
                    this.checkMultipleRootElements();
                }
                if (this.fDocumentHandler != null) {
                    augmentations = this.modifyAugmentations(augmentations);
                    xMLAttributes = this.processAttributes(xMLAttributes);
                    this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations);
                }
            }
        } else if (this.getState() == 1) {
            if (this.fResultDepth++ == 0) {
                this.checkMultipleRootElements();
            }
            if (this.fDocumentHandler != null) {
                augmentations = this.modifyAugmentations(augmentations);
                xMLAttributes = this.processAttributes(xMLAttributes);
                this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations);
            }
        }
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        ++this.fDepth;
        int n = this.getState(this.fDepth - 1);
        if (n == 3 && this.getState(this.fDepth - 2) == 3) {
            this.setState(2);
        } else {
            this.setState(n);
        }
        this.processXMLBaseAttributes(xMLAttributes);
        if (this.fFixupLanguage) {
            this.processXMLLangAttributes(xMLAttributes);
        }
        if (this.isIncludeElement(qName)) {
            boolean bl = this.handleIncludeElement(xMLAttributes);
            if (bl) {
                this.setState(2);
            } else {
                this.reportFatalError("NoFallback");
            }
        } else if (this.isFallbackElement(qName)) {
            this.handleFallbackElement();
        } else if (this.hasXIncludeNamespace(qName)) {
            if (this.getSawInclude(this.fDepth - 1)) {
                this.reportFatalError("IncludeChild", new Object[]{qName.rawname});
            }
            if (this.getSawFallback(this.fDepth - 1)) {
                this.reportFatalError("FallbackChild", new Object[]{qName.rawname});
            }
            if (this.getState() == 1) {
                if (this.fResultDepth == 0) {
                    this.checkMultipleRootElements();
                }
                if (this.fDocumentHandler != null) {
                    augmentations = this.modifyAugmentations(augmentations);
                    xMLAttributes = this.processAttributes(xMLAttributes);
                    this.fDocumentHandler.emptyElement(qName, xMLAttributes, augmentations);
                }
            }
        } else if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                this.checkMultipleRootElements();
            }
            if (this.fDocumentHandler != null) {
                augmentations = this.modifyAugmentations(augmentations);
                xMLAttributes = this.processAttributes(xMLAttributes);
                this.fDocumentHandler.emptyElement(qName, xMLAttributes, augmentations);
            }
        }
        this.setSawFallback(this.fDepth + 1, false);
        this.setSawInclude(this.fDepth, false);
        if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
            this.restoreBaseURI();
        }
        --this.fDepth;
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        if (this.isIncludeElement(qName) && this.getState() == 3 && !this.getSawFallback(this.fDepth + 1)) {
            this.reportFatalError("NoFallback");
        }
        if (this.isFallbackElement(qName)) {
            if (this.getState() == 1) {
                this.setState(2);
            }
        } else if (this.getState() == 1) {
            --this.fResultDepth;
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endElement(qName, augmentations);
            }
        }
        this.setSawFallback(this.fDepth + 1, false);
        this.setSawInclude(this.fDepth, false);
        if (this.fBaseURIScope.size() > 0 && this.fDepth == this.fBaseURIScope.peek()) {
            this.restoreBaseURI();
        }
        if (this.fLanguageScope.size() > 0 && this.fDepth == this.fLanguageScope.peek()) {
            this.fCurrentLanguage = this.restoreLanguage();
        }
        --this.fDepth;
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                if (augmentations != null && Boolean.TRUE.equals(augmentations.getItem("ENTITY_SKIPPED"))) {
                    this.reportFatalError("UnexpandedEntityReferenceIllegal");
                }
            } else if (this.fDocumentHandler != null) {
                this.fDocumentHandler.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
            }
        }
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1) {
            this.fDocumentHandler.textDecl(string, string2, augmentations);
        }
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.endGeneralEntity(string, augmentations);
        }
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.getState() == 1) {
            if (this.fResultDepth == 0) {
                this.checkWhitespace(xMLString);
            } else if (this.fDocumentHandler != null) {
                ++this.fDepth;
                augmentations = this.modifyAugmentations(augmentations);
                this.fDocumentHandler.characters(xMLString, augmentations);
                --this.fDepth;
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.ignorableWhitespace(xMLString, augmentations);
        }
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null && this.getState() == 1 && this.fResultDepth != 0) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        if (this.isRootDocument()) {
            if (!this.fSeenRootElement) {
                this.reportFatalError("RootElementRequired");
            }
            if (this.fDocumentHandler != null) {
                this.fDocumentHandler.endDocument(augmentations);
            }
        }
    }

    @Override
    public void setDocumentSource(XMLDocumentSource xMLDocumentSource) {
        this.fDocumentSource = xMLDocumentSource;
    }

    @Override
    public XMLDocumentSource getDocumentSource() {
        return this.fDocumentSource;
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(string, string2, string3, stringArray, string4, xMLString, xMLString2, augmentations);
        }
    }

    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(string, string2, augmentations);
        }
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
        this.fInDTD = false;
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }

    @Override
    public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(string, augmentations);
        }
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.externalEntityDecl(string, xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }

    @Override
    public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(xMLString, augmentations);
        }
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.internalEntityDecl(string, xMLString, xMLString2, augmentations);
        }
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        this.addNotation(string, xMLResourceIdentifier, augmentations);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(string, xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void setDTDSource(XMLDTDSource xMLDTDSource) {
        this.fDTDSource = xMLDTDSource;
    }

    @Override
    public void startAttlist(String string, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(string, augmentations);
        }
    }

    @Override
    public void startConditional(short s, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(s, augmentations);
        }
    }

    @Override
    public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
        this.fInDTD = true;
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(xMLLocator, augmentations);
        }
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        this.addUnparsedEntity(string, xMLResourceIdentifier, string2, augmentations);
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    @Override
    public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
        this.fDTDHandler = xMLDTDHandler;
    }

    private void setErrorReporter(XMLErrorReporter xMLErrorReporter) {
        this.fErrorReporter = xMLErrorReporter;
        if (this.fErrorReporter != null) {
            this.fErrorReporter.putMessageFormatter("http://www.w3.org/TR/xinclude", this.fXIncludeMessageFormatter);
            if (this.fDocLocation != null) {
                this.fErrorReporter.setDocumentLocator(this.fDocLocation);
            }
        }
    }

    protected void handleFallbackElement() {
        if (!this.getSawInclude(this.fDepth - 1)) {
            if (this.getState() == 2) {
                return;
            }
            this.reportFatalError("FallbackParent");
        }
        this.setSawInclude(this.fDepth, false);
        this.fNamespaceContext.setContextInvalid();
        if (this.getSawFallback(this.fDepth)) {
            this.reportFatalError("MultipleFallbacks");
        } else {
            this.setSawFallback(this.fDepth, true);
        }
        if (this.getState() == 3) {
            this.setState(1);
        }
    }

    /*
     * Exception decompiling
     */
    protected boolean handleIncludeElement(XMLAttributes var1_1) throws XNIException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 2 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    protected boolean hasXIncludeNamespace(QName qName) {
        return qName.uri == XINCLUDE_NS_URI || this.fNamespaceContext.getURI(qName.prefix) == XINCLUDE_NS_URI;
    }

    protected boolean isIncludeElement(QName qName) {
        return qName.localpart.equals(XINCLUDE_INCLUDE) && this.hasXIncludeNamespace(qName);
    }

    protected boolean isFallbackElement(QName qName) {
        return qName.localpart.equals(XINCLUDE_FALLBACK) && this.hasXIncludeNamespace(qName);
    }

    protected boolean sameBaseURIAsIncludeParent() {
        String string = this.getIncludeParentBaseURI();
        String string2 = this.fCurrentBaseURI.getExpandedSystemId();
        return string != null && string.equals(string2);
    }

    protected boolean sameLanguageAsIncludeParent() {
        String string = this.getIncludeParentLanguage();
        return string != null && string.equalsIgnoreCase(this.fCurrentLanguage);
    }

    protected void setupCurrentBaseURI(XMLLocator xMLLocator) {
        this.fCurrentBaseURI.setBaseSystemId(xMLLocator.getBaseSystemId());
        if (xMLLocator.getLiteralSystemId() != null) {
            this.fCurrentBaseURI.setLiteralSystemId(xMLLocator.getLiteralSystemId());
        } else {
            this.fCurrentBaseURI.setLiteralSystemId(this.fHrefFromParent);
        }
        String string = xMLLocator.getExpandedSystemId();
        if (string == null) {
            try {
                string = XMLEntityManager.expandSystemId(this.fCurrentBaseURI.getLiteralSystemId(), this.fCurrentBaseURI.getBaseSystemId(), false);
                if (string == null) {
                    string = this.fCurrentBaseURI.getLiteralSystemId();
                }
            }
            catch (URI.MalformedURIException malformedURIException) {
                this.reportFatalError("ExpandedSystemId");
            }
        }
        this.fCurrentBaseURI.setExpandedSystemId(string);
    }

    protected boolean searchForRecursiveIncludes(String string) {
        if (string.equals(this.fCurrentBaseURI.getExpandedSystemId())) {
            return true;
        }
        if (this.fParentXIncludeHandler == null) {
            return false;
        }
        return this.fParentXIncludeHandler.searchForRecursiveIncludes(string);
    }

    protected boolean isTopLevelIncludedItem() {
        return this.isTopLevelIncludedItemViaInclude() || this.isTopLevelIncludedItemViaFallback();
    }

    protected boolean isTopLevelIncludedItemViaInclude() {
        return this.fDepth == 1 && !this.isRootDocument();
    }

    protected boolean isTopLevelIncludedItemViaFallback() {
        return this.getSawFallback(this.fDepth - 1);
    }

    protected XMLAttributes processAttributes(XMLAttributes xMLAttributes) {
        Object object;
        String string;
        String string2;
        if (this.isTopLevelIncludedItem()) {
            if (this.fFixupBaseURIs && !this.sameBaseURIAsIncludeParent()) {
                if (xMLAttributes == null) {
                    xMLAttributes = new XMLAttributesImpl();
                }
                String string3 = null;
                try {
                    string3 = this.getRelativeBaseURI();
                }
                catch (URI.MalformedURIException malformedURIException) {
                    string3 = this.fCurrentBaseURI.getExpandedSystemId();
                }
                int n = xMLAttributes.addAttribute(XML_BASE_QNAME, XMLSymbols.fCDATASymbol, string3);
                xMLAttributes.setSpecified(n, true);
            }
            if (this.fFixupLanguage && !this.sameLanguageAsIncludeParent()) {
                if (xMLAttributes == null) {
                    xMLAttributes = new XMLAttributesImpl();
                }
                int n = xMLAttributes.addAttribute(XML_LANG_QNAME, XMLSymbols.fCDATASymbol, this.fCurrentLanguage);
                xMLAttributes.setSpecified(n, true);
            }
            Enumeration enumeration = this.fNamespaceContext.getAllPrefixes();
            while (enumeration.hasMoreElements()) {
                int n;
                String string4 = (String)enumeration.nextElement();
                string2 = this.fNamespaceContext.getURIFromIncludeParent(string4);
                if (string2 == (string = this.fNamespaceContext.getURI(string4)) || xMLAttributes == null) continue;
                if (string4 == XMLSymbols.EMPTY_STRING) {
                    if (xMLAttributes.getValue(NamespaceContext.XMLNS_URI, XMLSymbols.PREFIX_XMLNS) != null) continue;
                    if (xMLAttributes == null) {
                        xMLAttributes = new XMLAttributesImpl();
                    }
                    object = (QName)NEW_NS_ATTR_QNAME.clone();
                    ((QName)object).prefix = null;
                    ((QName)object).localpart = XMLSymbols.PREFIX_XMLNS;
                    ((QName)object).rawname = XMLSymbols.PREFIX_XMLNS;
                    n = xMLAttributes.addAttribute((QName)object, XMLSymbols.fCDATASymbol, string != null ? string : XMLSymbols.EMPTY_STRING);
                    xMLAttributes.setSpecified(n, true);
                    this.fNamespaceContext.declarePrefix(string4, string);
                    continue;
                }
                if (xMLAttributes.getValue(NamespaceContext.XMLNS_URI, string4) != null) continue;
                if (xMLAttributes == null) {
                    xMLAttributes = new XMLAttributesImpl();
                }
                object = (QName)NEW_NS_ATTR_QNAME.clone();
                ((QName)object).localpart = string4;
                ((QName)object).rawname = ((QName)object).rawname + string4;
                ((QName)object).rawname = this.fSymbolTable != null ? this.fSymbolTable.addSymbol(((QName)object).rawname) : ((QName)object).rawname.intern();
                n = xMLAttributes.addAttribute((QName)object, XMLSymbols.fCDATASymbol, string != null ? string : XMLSymbols.EMPTY_STRING);
                xMLAttributes.setSpecified(n, true);
                this.fNamespaceContext.declarePrefix(string4, string);
            }
        }
        if (xMLAttributes != null) {
            int n = xMLAttributes.getLength();
            for (int i = 0; i < n; ++i) {
                string2 = xMLAttributes.getType(i);
                string = xMLAttributes.getValue(i);
                if (string2 == XMLSymbols.fENTITYSymbol) {
                    this.checkUnparsedEntity(string);
                }
                if (string2 == XMLSymbols.fENTITIESSymbol) {
                    object = new StringTokenizer(string);
                    while (((StringTokenizer)object).hasMoreTokens()) {
                        String string5 = ((StringTokenizer)object).nextToken();
                        this.checkUnparsedEntity(string5);
                    }
                    continue;
                }
                if (string2 != XMLSymbols.fNOTATIONSymbol) continue;
                this.checkNotation(string);
            }
        }
        return xMLAttributes;
    }

    protected String getRelativeBaseURI() throws URI.MalformedURIException {
        int n = this.getIncludeParentDepth();
        String string = this.getRelativeURI(n);
        if (this.isRootDocument()) {
            return string;
        }
        if (string.length() == 0) {
            string = this.fCurrentBaseURI.getLiteralSystemId();
        }
        if (n == 0) {
            String string2;
            String string3;
            if (this.fParentRelativeURI == null) {
                this.fParentRelativeURI = this.fParentXIncludeHandler.getRelativeBaseURI();
            }
            if (this.fParentRelativeURI.length() == 0) {
                return string;
            }
            URI uRI = new URI(this.fParentRelativeURI, true);
            URI uRI2 = new URI(uRI, string);
            String string4 = uRI.getScheme();
            if (!this.isEqual(string4, string3 = uRI2.getScheme())) {
                return string;
            }
            String string5 = uRI.getAuthority();
            if (!this.isEqual(string5, string2 = uRI2.getAuthority())) {
                return uRI2.getSchemeSpecificPart();
            }
            String string6 = uRI2.getPath();
            String string7 = uRI2.getQueryString();
            String string8 = uRI2.getFragment();
            if (string7 != null || string8 != null) {
                StringBuffer stringBuffer = new StringBuffer();
                if (string6 != null) {
                    stringBuffer.append(string6);
                }
                if (string7 != null) {
                    stringBuffer.append('?');
                    stringBuffer.append(string7);
                }
                if (string8 != null) {
                    stringBuffer.append('#');
                    stringBuffer.append(string8);
                }
                return stringBuffer.toString();
            }
            return string6;
        }
        return string;
    }

    private String getIncludeParentBaseURI() {
        int n = this.getIncludeParentDepth();
        if (!this.isRootDocument() && n == 0) {
            return this.fParentXIncludeHandler.getIncludeParentBaseURI();
        }
        return this.getBaseURI(n);
    }

    private String getIncludeParentLanguage() {
        int n = this.getIncludeParentDepth();
        if (!this.isRootDocument() && n == 0) {
            return this.fParentXIncludeHandler.getIncludeParentLanguage();
        }
        return this.getLanguage(n);
    }

    private int getIncludeParentDepth() {
        for (int i = this.fDepth - 1; i >= 0; --i) {
            if (this.getSawInclude(i) || this.getSawFallback(i)) continue;
            return i;
        }
        return 0;
    }

    private int getResultDepth() {
        return this.fResultDepth;
    }

    protected Augmentations modifyAugmentations(Augmentations augmentations) {
        return this.modifyAugmentations(augmentations, false);
    }

    protected Augmentations modifyAugmentations(Augmentations augmentations, boolean bl) {
        if (bl || this.isTopLevelIncludedItem()) {
            if (augmentations == null) {
                augmentations = new AugmentationsImpl();
            }
            augmentations.putItem(XINCLUDE_INCLUDED, Boolean.TRUE);
        }
        return augmentations;
    }

    protected int getState(int n) {
        return this.fState[n];
    }

    protected int getState() {
        return this.fState[this.fDepth];
    }

    protected void setState(int n) {
        if (this.fDepth >= this.fState.length) {
            int[] nArray = new int[this.fDepth * 2];
            System.arraycopy(this.fState, 0, nArray, 0, this.fState.length);
            this.fState = nArray;
        }
        this.fState[this.fDepth] = n;
    }

    protected void setSawFallback(int n, boolean bl) {
        if (n >= this.fSawFallback.length) {
            boolean[] blArray = new boolean[n * 2];
            System.arraycopy(this.fSawFallback, 0, blArray, 0, this.fSawFallback.length);
            this.fSawFallback = blArray;
        }
        this.fSawFallback[n] = bl;
    }

    protected boolean getSawFallback(int n) {
        if (n >= this.fSawFallback.length) {
            return false;
        }
        return this.fSawFallback[n];
    }

    protected void setSawInclude(int n, boolean bl) {
        if (n >= this.fSawInclude.length) {
            boolean[] blArray = new boolean[n * 2];
            System.arraycopy(this.fSawInclude, 0, blArray, 0, this.fSawInclude.length);
            this.fSawInclude = blArray;
        }
        this.fSawInclude[n] = bl;
    }

    protected boolean getSawInclude(int n) {
        if (n >= this.fSawInclude.length) {
            return false;
        }
        return this.fSawInclude[n];
    }

    protected void reportResourceError(String string) {
        this.reportResourceError(string, null);
    }

    protected void reportResourceError(String string, Object[] objectArray) {
        this.reportResourceError(string, objectArray, null);
    }

    protected void reportResourceError(String string, Object[] objectArray, Exception exception) {
        this.reportError(string, objectArray, (short)0, exception);
    }

    protected void reportFatalError(String string) {
        this.reportFatalError(string, null);
    }

    protected void reportFatalError(String string, Object[] objectArray) {
        this.reportFatalError(string, objectArray, null);
    }

    protected void reportFatalError(String string, Object[] objectArray, Exception exception) {
        this.reportError(string, objectArray, (short)2, exception);
    }

    private void reportError(String string, Object[] objectArray, short s, Exception exception) {
        if (this.fErrorReporter != null) {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xinclude", string, objectArray, s, exception);
        }
    }

    protected void setParent(XIncludeHandler xIncludeHandler) {
        this.fParentXIncludeHandler = xIncludeHandler;
    }

    protected void setHref(String string) {
        this.fHrefFromParent = string;
    }

    protected void setXIncludeLocator(XMLLocatorWrapper xMLLocatorWrapper) {
        this.fXIncludeLocator = xMLLocatorWrapper;
    }

    protected boolean isRootDocument() {
        return this.fParentXIncludeHandler == null;
    }

    protected void addUnparsedEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) {
        UnparsedEntity unparsedEntity = new UnparsedEntity();
        unparsedEntity.name = string;
        unparsedEntity.systemId = xMLResourceIdentifier.getLiteralSystemId();
        unparsedEntity.publicId = xMLResourceIdentifier.getPublicId();
        unparsedEntity.baseURI = xMLResourceIdentifier.getBaseSystemId();
        unparsedEntity.expandedSystemId = xMLResourceIdentifier.getExpandedSystemId();
        unparsedEntity.notation = string2;
        unparsedEntity.augmentations = augmentations;
        this.fUnparsedEntities.add(unparsedEntity);
    }

    protected void addNotation(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) {
        Notation notation = new Notation();
        notation.name = string;
        notation.systemId = xMLResourceIdentifier.getLiteralSystemId();
        notation.publicId = xMLResourceIdentifier.getPublicId();
        notation.baseURI = xMLResourceIdentifier.getBaseSystemId();
        notation.expandedSystemId = xMLResourceIdentifier.getExpandedSystemId();
        notation.augmentations = augmentations;
        this.fNotations.add(notation);
    }

    protected void checkUnparsedEntity(String string) {
        UnparsedEntity unparsedEntity = new UnparsedEntity();
        unparsedEntity.name = string;
        int n = this.fUnparsedEntities.indexOf(unparsedEntity);
        if (n != -1) {
            unparsedEntity = (UnparsedEntity)this.fUnparsedEntities.get(n);
            this.checkNotation(unparsedEntity.notation);
            this.checkAndSendUnparsedEntity(unparsedEntity);
        }
    }

    protected void checkNotation(String string) {
        Notation notation = new Notation();
        notation.name = string;
        int n = this.fNotations.indexOf(notation);
        if (n != -1) {
            notation = (Notation)this.fNotations.get(n);
            this.checkAndSendNotation(notation);
        }
    }

    protected void checkAndSendUnparsedEntity(UnparsedEntity unparsedEntity) {
        if (this.isRootDocument()) {
            int n = this.fUnparsedEntities.indexOf(unparsedEntity);
            if (n == -1) {
                XMLResourceIdentifierImpl xMLResourceIdentifierImpl = new XMLResourceIdentifierImpl(unparsedEntity.publicId, unparsedEntity.systemId, unparsedEntity.baseURI, unparsedEntity.expandedSystemId);
                this.addUnparsedEntity(unparsedEntity.name, xMLResourceIdentifierImpl, unparsedEntity.notation, unparsedEntity.augmentations);
                if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
                    this.fDTDHandler.unparsedEntityDecl(unparsedEntity.name, xMLResourceIdentifierImpl, unparsedEntity.notation, unparsedEntity.augmentations);
                }
            } else {
                UnparsedEntity unparsedEntity2 = (UnparsedEntity)this.fUnparsedEntities.get(n);
                if (!unparsedEntity.isDuplicate(unparsedEntity2)) {
                    this.reportFatalError("NonDuplicateUnparsedEntity", new Object[]{unparsedEntity.name});
                }
            }
        } else {
            this.fParentXIncludeHandler.checkAndSendUnparsedEntity(unparsedEntity);
        }
    }

    protected void checkAndSendNotation(Notation notation) {
        if (this.isRootDocument()) {
            int n = this.fNotations.indexOf(notation);
            if (n == -1) {
                XMLResourceIdentifierImpl xMLResourceIdentifierImpl = new XMLResourceIdentifierImpl(notation.publicId, notation.systemId, notation.baseURI, notation.expandedSystemId);
                this.addNotation(notation.name, xMLResourceIdentifierImpl, notation.augmentations);
                if (this.fSendUEAndNotationEvents && this.fDTDHandler != null) {
                    this.fDTDHandler.notationDecl(notation.name, xMLResourceIdentifierImpl, notation.augmentations);
                }
            } else {
                Notation notation2 = (Notation)this.fNotations.get(n);
                if (!notation.isDuplicate(notation2)) {
                    this.reportFatalError("NonDuplicateNotation", new Object[]{notation.name});
                }
            }
        } else {
            this.fParentXIncludeHandler.checkAndSendNotation(notation);
        }
    }

    private void checkWhitespace(XMLString xMLString) {
        int n = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset; i < n; ++i) {
            if (XMLChar.isSpace(xMLString.ch[i])) continue;
            this.reportFatalError("ContentIllegalAtTopLevel");
            return;
        }
    }

    private void checkMultipleRootElements() {
        if (this.getRootElementProcessed()) {
            this.reportFatalError("MultipleRootElements");
        }
        this.setRootElementProcessed(true);
    }

    private void setRootElementProcessed(boolean bl) {
        if (this.isRootDocument()) {
            this.fSeenRootElement = bl;
            return;
        }
        this.fParentXIncludeHandler.setRootElementProcessed(bl);
    }

    private boolean getRootElementProcessed() {
        return this.isRootDocument() ? this.fSeenRootElement : this.fParentXIncludeHandler.getRootElementProcessed();
    }

    protected void copyFeatures(XMLComponentManager xMLComponentManager, ParserConfigurationSettings parserConfigurationSettings) {
        Enumeration enumeration = Constants.getXercesFeatures();
        this.copyFeatures1(enumeration, "http://apache.org/xml/features/", xMLComponentManager, parserConfigurationSettings);
        enumeration = Constants.getSAXFeatures();
        this.copyFeatures1(enumeration, "http://xml.org/sax/features/", xMLComponentManager, parserConfigurationSettings);
    }

    protected void copyFeatures(XMLComponentManager xMLComponentManager, XMLParserConfiguration xMLParserConfiguration) {
        Enumeration enumeration = Constants.getXercesFeatures();
        this.copyFeatures1(enumeration, "http://apache.org/xml/features/", xMLComponentManager, xMLParserConfiguration);
        enumeration = Constants.getSAXFeatures();
        this.copyFeatures1(enumeration, "http://xml.org/sax/features/", xMLComponentManager, xMLParserConfiguration);
    }

    private void copyFeatures1(Enumeration enumeration, String string, XMLComponentManager xMLComponentManager, ParserConfigurationSettings parserConfigurationSettings) {
        while (enumeration.hasMoreElements()) {
            String string2 = string + (String)enumeration.nextElement();
            parserConfigurationSettings.addRecognizedFeatures(new String[]{string2});
            try {
                parserConfigurationSettings.setFeature(string2, xMLComponentManager.getFeature(string2));
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
        }
    }

    private void copyFeatures1(Enumeration enumeration, String string, XMLComponentManager xMLComponentManager, XMLParserConfiguration xMLParserConfiguration) {
        while (enumeration.hasMoreElements()) {
            String string2 = string + (String)enumeration.nextElement();
            boolean bl = xMLComponentManager.getFeature(string2);
            try {
                xMLParserConfiguration.setFeature(string2, bl);
            }
            catch (XMLConfigurationException xMLConfigurationException) {}
        }
    }

    protected void saveBaseURI() {
        this.fBaseURIScope.push(this.fDepth);
        this.fBaseURI.push(this.fCurrentBaseURI.getBaseSystemId());
        this.fLiteralSystemID.push(this.fCurrentBaseURI.getLiteralSystemId());
        this.fExpandedSystemID.push(this.fCurrentBaseURI.getExpandedSystemId());
    }

    protected void restoreBaseURI() {
        this.fBaseURI.pop();
        this.fLiteralSystemID.pop();
        this.fExpandedSystemID.pop();
        this.fBaseURIScope.pop();
        this.fCurrentBaseURI.setBaseSystemId((String)this.fBaseURI.peek());
        this.fCurrentBaseURI.setLiteralSystemId((String)this.fLiteralSystemID.peek());
        this.fCurrentBaseURI.setExpandedSystemId((String)this.fExpandedSystemID.peek());
    }

    protected void saveLanguage(String string) {
        this.fLanguageScope.push(this.fDepth);
        this.fLanguageStack.push(string);
    }

    public String restoreLanguage() {
        this.fLanguageStack.pop();
        this.fLanguageScope.pop();
        return (String)this.fLanguageStack.peek();
    }

    public String getBaseURI(int n) {
        int n2 = this.scopeOfBaseURI(n);
        return (String)this.fExpandedSystemID.elementAt(n2);
    }

    public String getLanguage(int n) {
        int n2 = this.scopeOfLanguage(n);
        return (String)this.fLanguageStack.elementAt(n2);
    }

    public String getRelativeURI(int n) throws URI.MalformedURIException {
        int n2 = this.scopeOfBaseURI(n) + 1;
        if (n2 == this.fBaseURIScope.size()) {
            return "";
        }
        URI uRI = new URI("file", (String)this.fLiteralSystemID.elementAt(n2));
        for (int i = n2 + 1; i < this.fBaseURIScope.size(); ++i) {
            uRI = new URI(uRI, (String)this.fLiteralSystemID.elementAt(i));
        }
        return uRI.getPath();
    }

    private int scopeOfBaseURI(int n) {
        for (int i = this.fBaseURIScope.size() - 1; i >= 0; --i) {
            if (this.fBaseURIScope.elementAt(i) > n) continue;
            return i;
        }
        return -1;
    }

    private int scopeOfLanguage(int n) {
        for (int i = this.fLanguageScope.size() - 1; i >= 0; --i) {
            if (this.fLanguageScope.elementAt(i) > n) continue;
            return i;
        }
        return -1;
    }

    protected void processXMLBaseAttributes(XMLAttributes xMLAttributes) {
        String string = xMLAttributes.getValue(NamespaceContext.XML_URI, "base");
        if (string != null) {
            try {
                String string2 = XMLEntityManager.expandSystemId(string, this.fCurrentBaseURI.getExpandedSystemId(), false);
                this.fCurrentBaseURI.setLiteralSystemId(string);
                this.fCurrentBaseURI.setBaseSystemId(this.fCurrentBaseURI.getExpandedSystemId());
                this.fCurrentBaseURI.setExpandedSystemId(string2);
                this.saveBaseURI();
            }
            catch (URI.MalformedURIException malformedURIException) {
                // empty catch block
            }
        }
    }

    protected void processXMLLangAttributes(XMLAttributes xMLAttributes) {
        String string = xMLAttributes.getValue(NamespaceContext.XML_URI, "lang");
        if (string != null) {
            this.fCurrentLanguage = string;
            this.saveLanguage(this.fCurrentLanguage);
        }
    }

    private boolean isValidInHTTPHeader(String string) {
        for (int i = string.length() - 1; i >= 0; --i) {
            char c = string.charAt(i);
            if (c >= ' ' && c <= '~') continue;
            return false;
        }
        return true;
    }

    private XMLInputSource createInputSource(String string, String string2, String string3, String string4, String string5) {
        HTTPInputSource hTTPInputSource = new HTTPInputSource(string, string2, string3);
        if (string4 != null && string4.length() > 0) {
            hTTPInputSource.setHTTPRequestProperty(HTTP_ACCEPT, string4);
        }
        if (string5 != null && string5.length() > 0) {
            hTTPInputSource.setHTTPRequestProperty(HTTP_ACCEPT_LANGUAGE, string5);
        }
        return hTTPInputSource;
    }

    private boolean isEqual(String string, String string2) {
        return string == string2 || string != null && string.equals(string2);
    }

    private String escapeHref(String string) {
        int n;
        int n2;
        int n3 = string.length();
        StringBuffer stringBuffer = new StringBuffer(n3 * 3);
        for (n2 = 0; n2 < n3 && (n = string.charAt(n2)) <= 126; ++n2) {
            if (n < 32) {
                return string;
            }
            if (gNeedEscaping[n]) {
                stringBuffer.append('%');
                stringBuffer.append(gAfterEscaping1[n]);
                stringBuffer.append(gAfterEscaping2[n]);
                continue;
            }
            stringBuffer.append((char)n);
        }
        if (n2 < n3) {
            int n4;
            for (int i = n2; i < n3; ++i) {
                n = string.charAt(i);
                if (n >= 32 && n <= 126 || n >= 160 && n <= 55295 || n >= 63744 && n <= 64975 || n >= 65008 && n <= 65519 || XMLChar.isHighSurrogate(n) && ++i < n3 && XMLChar.isLowSurrogate(n4 = string.charAt(i)) && (n4 = XMLChar.supplemental((char)n, (char)n4)) < 983040 && (n4 & 0xFFFF) <= 65533) continue;
                return string;
            }
            byte[] byArray = null;
            try {
                byArray = string.substring(n2).getBytes("UTF-8");
            }
            catch (UnsupportedEncodingException unsupportedEncodingException) {
                return string;
            }
            n3 = byArray.length;
            for (n2 = 0; n2 < n3; ++n2) {
                n4 = byArray[n2];
                if (n4 < 0) {
                    n = n4 + 256;
                    stringBuffer.append('%');
                    stringBuffer.append(gHexChs[n >> 4]);
                    stringBuffer.append(gHexChs[n & 0xF]);
                    continue;
                }
                if (gNeedEscaping[n4]) {
                    stringBuffer.append('%');
                    stringBuffer.append(gAfterEscaping1[n4]);
                    stringBuffer.append(gAfterEscaping2[n4]);
                    continue;
                }
                stringBuffer.append((char)n4);
            }
        }
        if (stringBuffer.length() != n3) {
            return stringBuffer.toString();
        }
        return string;
    }

    static {
        for (char c : new char[]{' ', '<', '>', '\"', '{', '}', '|', '\\', '^', '`'}) {
            XIncludeHandler.gNeedEscaping[c] = true;
            XIncludeHandler.gAfterEscaping1[c] = gHexChs[c >> 4];
            XIncludeHandler.gAfterEscaping2[c] = gHexChs[c & 0xF];
        }
    }

    protected static class UnparsedEntity {
        public String name;
        public String systemId;
        public String baseURI;
        public String publicId;
        public String expandedSystemId;
        public String notation;
        public Augmentations augmentations;

        protected UnparsedEntity() {
        }

        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object instanceof UnparsedEntity) {
                UnparsedEntity unparsedEntity = (UnparsedEntity)object;
                return this.name.equals(unparsedEntity.name);
            }
            return false;
        }

        public boolean isDuplicate(Object object) {
            if (object != null && object instanceof UnparsedEntity) {
                UnparsedEntity unparsedEntity = (UnparsedEntity)object;
                return this.name.equals(unparsedEntity.name) && this.isEqual(this.publicId, unparsedEntity.publicId) && this.isEqual(this.expandedSystemId, unparsedEntity.expandedSystemId) && this.isEqual(this.notation, unparsedEntity.notation);
            }
            return false;
        }

        private boolean isEqual(String string, String string2) {
            return string == string2 || string != null && string.equals(string2);
        }
    }

    protected static class Notation {
        public String name;
        public String systemId;
        public String baseURI;
        public String publicId;
        public String expandedSystemId;
        public Augmentations augmentations;

        protected Notation() {
        }

        public boolean equals(Object object) {
            if (object == null) {
                return false;
            }
            if (object instanceof Notation) {
                Notation notation = (Notation)object;
                return this.name.equals(notation.name);
            }
            return false;
        }

        public boolean isDuplicate(Object object) {
            if (object != null && object instanceof Notation) {
                Notation notation = (Notation)object;
                return this.name.equals(notation.name) && this.isEqual(this.publicId, notation.publicId) && this.isEqual(this.expandedSystemId, notation.expandedSystemId);
            }
            return false;
        }

        private boolean isEqual(String string, String string2) {
            return string == string2 || string != null && string.equals(string2);
        }
    }
}

