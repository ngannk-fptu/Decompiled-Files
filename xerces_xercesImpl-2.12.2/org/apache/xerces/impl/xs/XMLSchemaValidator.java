/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.DatatypeException;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidatedInfo;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.validation.ConfigurableValidationState;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.impl.xs.AttributePSVImpl;
import org.apache.xerces.impl.xs.ElementPSVImpl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.SubstitutionGroupHandler;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.XSAttributeUseImpl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSConstraints;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSElementDeclHelper;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.XSWildcardDecl;
import org.apache.xerces.impl.xs.identity.Field;
import org.apache.xerces.impl.xs.identity.FieldActivator;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.identity.KeyRef;
import org.apache.xerces.impl.xs.identity.Selector;
import org.apache.xerces.impl.xs.identity.UniqueOrKey;
import org.apache.xerces.impl.xs.identity.ValueStore;
import org.apache.xerces.impl.xs.identity.XPathMatcher;
import org.apache.xerces.impl.xs.models.CMBuilder;
import org.apache.xerces.impl.xs.models.CMNodeFactory;
import org.apache.xerces.impl.xs.models.XSCMValidator;
import org.apache.xerces.impl.xs.util.XS10TypeHelper;
import org.apache.xerces.util.AugmentationsImpl;
import org.apache.xerces.util.IntStack;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.NamespaceContext;
import org.apache.xerces.xni.XMLAttributes;
import org.apache.xerces.xni.XMLDocumentHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xs.ShortList;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSTypeDefinition;

public class XMLSchemaValidator
implements XMLComponent,
XMLDocumentFilter,
FieldActivator,
RevalidationHandler,
XSElementDeclHelper {
    private static final boolean DEBUG = false;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String SCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String SCHEMA_FULL_CHECKING = "http://apache.org/xml/features/validation/schema-full-checking";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String NORMALIZE_DATA = "http://apache.org/xml/features/validation/schema/normalized-value";
    protected static final String SCHEMA_ELEMENT_DEFAULT = "http://apache.org/xml/features/validation/schema/element-default";
    protected static final String SCHEMA_AUGMENT_PSVI = "http://apache.org/xml/features/validation/schema/augment-psvi";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String USE_GRAMMAR_POOL_ONLY = "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    protected static final String IGNORE_XSI_TYPE = "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl";
    protected static final String ID_IDREF_CHECKING = "http://apache.org/xml/features/validation/id-idref-checking";
    protected static final String UNPARSED_ENTITY_CHECKING = "http://apache.org/xml/features/validation/unparsed-entity-checking";
    protected static final String IDENTITY_CONSTRAINT_CHECKING = "http://apache.org/xml/features/validation/identity-constraint-checking";
    public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    protected static final String SCHEMA_LOCATION = "http://apache.org/xml/properties/schema/external-schemaLocation";
    protected static final String SCHEMA_NONS_LOCATION = "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    protected static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    protected static final String ROOT_TYPE_DEF = "http://apache.org/xml/properties/validation/schema/root-type-definition";
    protected static final String ROOT_ELEMENT_DECL = "http://apache.org/xml/properties/validation/schema/root-element-declaration";
    protected static final String SCHEMA_DV_FACTORY = "http://apache.org/xml/properties/internal/validation/schema/dv-factory";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/schema", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/schema-full-checking", "http://apache.org/xml/features/allow-java-encodings", "http://apache.org/xml/features/continue-after-fatal-error", "http://apache.org/xml/features/standard-uri-conformant", "http://apache.org/xml/features/generate-synthetic-annotations", "http://apache.org/xml/features/validate-annotations", "http://apache.org/xml/features/honour-all-schemaLocations", "http://apache.org/xml/features/internal/validation/schema/use-grammar-pool-only", "http://apache.org/xml/features/validation/schema/ignore-xsi-type-until-elemdecl", "http://apache.org/xml/features/validation/id-idref-checking", "http://apache.org/xml/features/validation/identity-constraint-checking", "http://apache.org/xml/features/validation/unparsed-entity-checking", "http://apache.org/xml/features/namespace-growth", "http://apache.org/xml/features/internal/tolerate-duplicates"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/entity-resolver", "http://apache.org/xml/properties/internal/validation-manager", "http://apache.org/xml/properties/schema/external-schemaLocation", "http://apache.org/xml/properties/schema/external-noNamespaceSchemaLocation", "http://java.sun.com/xml/jaxp/properties/schemaSource", "http://java.sun.com/xml/jaxp/properties/schemaLanguage", "http://apache.org/xml/properties/validation/schema/root-type-definition", "http://apache.org/xml/properties/validation/schema/root-element-declaration", "http://apache.org/xml/properties/internal/validation/schema/dv-factory"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, null, null, null, null, null, null, null, null};
    protected static final int ID_CONSTRAINT_NUM = 1;
    static final XSAttributeDecl XSI_TYPE = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_TYPE);
    static final XSAttributeDecl XSI_NIL = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NIL);
    static final XSAttributeDecl XSI_SCHEMALOCATION = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_SCHEMALOCATION);
    static final XSAttributeDecl XSI_NONAMESPACESCHEMALOCATION = SchemaGrammar.SG_XSI.getGlobalAttributeDecl(SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
    private static final Hashtable EMPTY_TABLE = new Hashtable();
    protected ElementPSVImpl fCurrentPSVI = new ElementPSVImpl();
    protected final AugmentationsImpl fAugmentations = new AugmentationsImpl();
    protected XMLString fDefaultValue;
    protected boolean fDynamicValidation = false;
    protected boolean fSchemaDynamicValidation = false;
    protected boolean fDoValidation = false;
    protected boolean fFullChecking = false;
    protected boolean fNormalizeData = true;
    protected boolean fSchemaElementDefault = true;
    protected boolean fAugPSVI = true;
    protected boolean fIdConstraint = false;
    protected boolean fUseGrammarPoolOnly = false;
    protected boolean fNamespaceGrowth = false;
    private String fSchemaType = null;
    protected boolean fEntityRef = false;
    protected boolean fInCDATA = false;
    protected SymbolTable fSymbolTable;
    private XMLLocator fLocator;
    protected final XSIErrorReporter fXSIErrorReporter = new XSIErrorReporter();
    protected XMLEntityResolver fEntityResolver;
    protected ValidationManager fValidationManager = null;
    protected ConfigurableValidationState fValidationState = new ConfigurableValidationState();
    protected XMLGrammarPool fGrammarPool;
    protected String fExternalSchemas = null;
    protected String fExternalNoNamespaceSchema = null;
    protected Object fJaxpSchemaSource = null;
    protected final XSDDescription fXSDDescription = new XSDDescription();
    protected final Hashtable fLocationPairs = new Hashtable();
    protected final Hashtable fExpandedLocationPairs = new Hashtable();
    protected final ArrayList fUnparsedLocations = new ArrayList();
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    static final int INITIAL_STACK_SIZE = 8;
    static final int INC_STACK_SIZE = 8;
    private static final boolean DEBUG_NORMALIZATION = false;
    private final XMLString fEmptyXMLStr = new XMLString(null, 0, -1);
    private static final int BUFFER_SIZE = 20;
    private final XMLString fNormalizedStr = new XMLString();
    private boolean fFirstChunk = true;
    private boolean fTrailing = false;
    private short fWhiteSpace = (short)-1;
    private boolean fUnionType = false;
    private final XSGrammarBucket fGrammarBucket = new XSGrammarBucket();
    private final SubstitutionGroupHandler fSubGroupHandler = new SubstitutionGroupHandler(this);
    private final XSSimpleType fQNameDV = (XSSimpleType)SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl("QName");
    private final CMNodeFactory nodeFactory = new CMNodeFactory();
    private final CMBuilder fCMBuilder = new CMBuilder(this.nodeFactory);
    private final XMLSchemaLoader fSchemaLoader;
    private String fValidationRoot;
    private int fSkipValidationDepth;
    private int fNFullValidationDepth;
    private int fNNoneValidationDepth;
    private int fElementDepth;
    private boolean fSubElement;
    private boolean[] fSubElementStack;
    private XSElementDecl fCurrentElemDecl;
    private XSElementDecl[] fElemDeclStack;
    private boolean fNil;
    private boolean[] fNilStack;
    private XSNotationDecl fNotation;
    private XSNotationDecl[] fNotationStack;
    private XSTypeDefinition fCurrentType;
    private XSTypeDefinition[] fTypeStack;
    private XSCMValidator fCurrentCM;
    private XSCMValidator[] fCMStack;
    private int[] fCurrCMState;
    private int[][] fCMStateStack;
    private boolean fStrictAssess;
    private boolean[] fStrictAssessStack;
    private final StringBuffer fBuffer;
    private boolean fAppendBuffer;
    private boolean fSawText;
    private boolean[] fSawTextStack;
    private boolean fSawCharacters;
    private boolean[] fStringContent;
    private final org.apache.xerces.xni.QName fTempQName;
    private QName fRootTypeQName;
    private XSTypeDefinition fRootTypeDefinition;
    private QName fRootElementDeclQName;
    private XSElementDecl fRootElementDeclaration;
    private int fIgnoreXSITypeDepth;
    private boolean fIDCChecking;
    private ValidatedInfo fValidatedInfo;
    private ValidationState fState4XsiType;
    private ValidationState fState4ApplyDefault;
    protected XPathMatcherStack fMatcherStack;
    protected ValueStoreCache fValueStoreCache;

    @Override
    public String[] getRecognizedFeatures() {
        return (String[])RECOGNIZED_FEATURES.clone();
    }

    @Override
    public void setFeature(String string, boolean bl) throws XMLConfigurationException {
    }

    @Override
    public String[] getRecognizedProperties() {
        return (String[])RECOGNIZED_PROPERTIES.clone();
    }

    @Override
    public void setProperty(String string, Object object) throws XMLConfigurationException {
        if (string.equals(ROOT_TYPE_DEF)) {
            if (object == null) {
                this.fRootTypeQName = null;
                this.fRootTypeDefinition = null;
            } else if (object instanceof QName) {
                this.fRootTypeQName = (QName)object;
                this.fRootTypeDefinition = null;
            } else {
                this.fRootTypeDefinition = (XSTypeDefinition)object;
                this.fRootTypeQName = null;
            }
        } else if (string.equals(ROOT_ELEMENT_DECL)) {
            if (object == null) {
                this.fRootElementDeclQName = null;
                this.fRootElementDeclaration = null;
            } else if (object instanceof QName) {
                this.fRootElementDeclQName = (QName)object;
                this.fRootElementDeclaration = null;
            } else {
                this.fRootElementDeclaration = (XSElementDecl)object;
                this.fRootElementDeclQName = null;
            }
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
        this.fDocumentHandler = xMLDocumentHandler;
    }

    @Override
    public XMLDocumentHandler getDocumentHandler() {
        return this.fDocumentHandler;
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
    public void startDocument(XMLLocator xMLLocator, String string, NamespaceContext namespaceContext, Augmentations augmentations) throws XNIException {
        this.fValidationState.setNamespaceSupport(namespaceContext);
        this.fState4XsiType.setNamespaceSupport(namespaceContext);
        this.fState4ApplyDefault.setNamespaceSupport(namespaceContext);
        this.fLocator = xMLLocator;
        this.handleStartDocument(xMLLocator, string);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(xMLLocator, string, namespaceContext, augmentations);
        }
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void startElement(org.apache.xerces.xni.QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        Augmentations augmentations2 = this.handleStartElement(qName, xMLAttributes, augmentations);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations2);
        }
    }

    @Override
    public void emptyElement(org.apache.xerces.xni.QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        Augmentations augmentations2 = this.handleStartElement(qName, xMLAttributes, augmentations);
        this.fDefaultValue = null;
        if (this.fElementDepth != -2) {
            augmentations2 = this.handleEndElement(qName, augmentations2);
        }
        if (this.fDocumentHandler != null) {
            if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
                this.fDocumentHandler.emptyElement(qName, xMLAttributes, augmentations2);
            } else {
                this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations2);
                this.fDocumentHandler.characters(this.fDefaultValue, null);
                this.fDocumentHandler.endElement(qName, augmentations2);
            }
        }
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        xMLString = this.handleCharacters(xMLString);
        if (this.fDocumentHandler != null) {
            if (this.fNormalizeData && this.fUnionType) {
                if (augmentations != null) {
                    this.fDocumentHandler.characters(this.fEmptyXMLStr, augmentations);
                }
            } else {
                this.fDocumentHandler.characters(xMLString, augmentations);
            }
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        this.handleIgnorableWhitespace(xMLString);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(xMLString, augmentations);
        }
    }

    @Override
    public void endElement(org.apache.xerces.xni.QName qName, Augmentations augmentations) throws XNIException {
        this.fDefaultValue = null;
        Augmentations augmentations2 = this.handleEndElement(qName, augmentations);
        if (this.fDocumentHandler != null) {
            if (!this.fSchemaElementDefault || this.fDefaultValue == null) {
                this.fDocumentHandler.endElement(qName, augmentations2);
            } else {
                this.fDocumentHandler.characters(this.fDefaultValue, null);
                this.fDocumentHandler.endElement(qName, augmentations2);
            }
        }
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        this.fInCDATA = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        this.fInCDATA = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        this.handleEndDocument();
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augmentations);
        }
        this.fLocator = null;
    }

    @Override
    public boolean characterData(String string, Augmentations augmentations) {
        boolean bl = this.fSawText = this.fSawText || string.length() > 0;
        if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(string, this.fWhiteSpace == 2);
            this.fBuffer.append(this.fNormalizedStr.ch, this.fNormalizedStr.offset, this.fNormalizedStr.length);
        } else if (this.fAppendBuffer) {
            this.fBuffer.append(string);
        }
        boolean bl2 = true;
        if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
            XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
            if (xSComplexTypeDecl.fContentType == 2) {
                for (int i = 0; i < string.length(); ++i) {
                    if (XMLChar.isSpace(string.charAt(i))) continue;
                    bl2 = false;
                    this.fSawCharacters = true;
                    break;
                }
            }
        }
        return bl2;
    }

    public void elementDefault(String string) {
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        this.fEntityRef = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(string, string2, augmentations);
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(xMLString, augmentations);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(string, xMLString, augmentations);
        }
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
        this.fEntityRef = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(string, augmentations);
        }
    }

    public XMLSchemaValidator() {
        this.fSchemaLoader = new XMLSchemaLoader(this.fXSIErrorReporter.fErrorReporter, this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder);
        this.fSubElementStack = new boolean[8];
        this.fElemDeclStack = new XSElementDecl[8];
        this.fNilStack = new boolean[8];
        this.fNotationStack = new XSNotationDecl[8];
        this.fTypeStack = new XSTypeDefinition[8];
        this.fCMStack = new XSCMValidator[8];
        this.fCMStateStack = new int[8][];
        this.fStrictAssess = true;
        this.fStrictAssessStack = new boolean[8];
        this.fBuffer = new StringBuffer();
        this.fAppendBuffer = true;
        this.fSawText = false;
        this.fSawTextStack = new boolean[8];
        this.fSawCharacters = false;
        this.fStringContent = new boolean[8];
        this.fTempQName = new org.apache.xerces.xni.QName();
        this.fRootTypeQName = null;
        this.fRootTypeDefinition = null;
        this.fRootElementDeclQName = null;
        this.fRootElementDeclaration = null;
        this.fValidatedInfo = new ValidatedInfo();
        this.fState4XsiType = new ValidationState();
        this.fState4ApplyDefault = new ValidationState();
        this.fMatcherStack = new XPathMatcherStack();
        this.fValueStoreCache = new ValueStoreCache();
        this.fState4XsiType.setExtraChecking(false);
        this.fState4ApplyDefault.setFacetChecking(false);
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        boolean bl;
        Object object;
        boolean bl2;
        this.fIdConstraint = false;
        this.fLocationPairs.clear();
        this.fExpandedLocationPairs.clear();
        this.fValidationState.resetIDTables();
        this.fSchemaLoader.reset(xMLComponentManager);
        this.fCurrentElemDecl = null;
        this.fCurrentCM = null;
        this.fCurrCMState = null;
        this.fSkipValidationDepth = -1;
        this.fNFullValidationDepth = -1;
        this.fNNoneValidationDepth = -1;
        this.fElementDepth = -1;
        this.fSubElement = false;
        this.fSchemaDynamicValidation = false;
        this.fEntityRef = false;
        this.fInCDATA = false;
        this.fMatcherStack.clear();
        this.fXSIErrorReporter.reset((XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER));
        try {
            bl2 = xMLComponentManager.getFeature(PARSER_SETTINGS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            bl2 = true;
        }
        if (!bl2) {
            this.fValidationManager.addValidationState(this.fValidationState);
            this.nodeFactory.reset();
            XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
            return;
        }
        this.nodeFactory.reset(xMLComponentManager);
        SymbolTable symbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        if (symbolTable != this.fSymbolTable) {
            this.fSymbolTable = symbolTable;
        }
        try {
            this.fNamespaceGrowth = xMLComponentManager.getFeature(NAMESPACE_GROWTH);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNamespaceGrowth = false;
        }
        try {
            this.fDynamicValidation = xMLComponentManager.getFeature(DYNAMIC_VALIDATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fDynamicValidation = false;
        }
        if (this.fDynamicValidation) {
            this.fDoValidation = true;
        } else {
            try {
                this.fDoValidation = xMLComponentManager.getFeature(VALIDATION);
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                this.fDoValidation = false;
            }
        }
        if (this.fDoValidation) {
            try {
                this.fDoValidation = xMLComponentManager.getFeature(SCHEMA_VALIDATION);
            }
            catch (XMLConfigurationException xMLConfigurationException) {
                // empty catch block
            }
        }
        try {
            this.fFullChecking = xMLComponentManager.getFeature(SCHEMA_FULL_CHECKING);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fFullChecking = false;
        }
        try {
            this.fNormalizeData = xMLComponentManager.getFeature(NORMALIZE_DATA);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNormalizeData = false;
        }
        try {
            this.fSchemaElementDefault = xMLComponentManager.getFeature(SCHEMA_ELEMENT_DEFAULT);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSchemaElementDefault = false;
        }
        try {
            this.fAugPSVI = xMLComponentManager.getFeature(SCHEMA_AUGMENT_PSVI);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fAugPSVI = true;
        }
        try {
            this.fSchemaType = (String)xMLComponentManager.getProperty(JAXP_SCHEMA_LANGUAGE);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSchemaType = null;
        }
        try {
            this.fUseGrammarPoolOnly = xMLComponentManager.getFeature(USE_GRAMMAR_POOL_ONLY);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fUseGrammarPoolOnly = false;
        }
        this.fEntityResolver = (XMLEntityResolver)xMLComponentManager.getProperty(ENTITY_MANAGER);
        this.fValidationManager = (ValidationManager)xMLComponentManager.getProperty(VALIDATION_MANAGER);
        this.fValidationManager.addValidationState(this.fValidationState);
        this.fValidationState.setSymbolTable(this.fSymbolTable);
        try {
            object = xMLComponentManager.getProperty(ROOT_TYPE_DEF);
            if (object == null) {
                this.fRootTypeQName = null;
                this.fRootTypeDefinition = null;
            } else if (object instanceof QName) {
                this.fRootTypeQName = (QName)object;
                this.fRootTypeDefinition = null;
            } else {
                this.fRootTypeDefinition = (XSTypeDefinition)object;
                this.fRootTypeQName = null;
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fRootTypeQName = null;
            this.fRootTypeDefinition = null;
        }
        try {
            object = xMLComponentManager.getProperty(ROOT_ELEMENT_DECL);
            if (object == null) {
                this.fRootElementDeclQName = null;
                this.fRootElementDeclaration = null;
            } else if (object instanceof QName) {
                this.fRootElementDeclQName = (QName)object;
                this.fRootElementDeclaration = null;
            } else {
                this.fRootElementDeclaration = (XSElementDecl)object;
                this.fRootElementDeclQName = null;
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fRootElementDeclQName = null;
            this.fRootElementDeclaration = null;
        }
        try {
            bl = xMLComponentManager.getFeature(IGNORE_XSI_TYPE);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            bl = false;
        }
        this.fIgnoreXSITypeDepth = bl ? 0 : -1;
        try {
            this.fIDCChecking = xMLComponentManager.getFeature(IDENTITY_CONSTRAINT_CHECKING);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fIDCChecking = true;
        }
        try {
            this.fValidationState.setIdIdrefChecking(xMLComponentManager.getFeature(ID_IDREF_CHECKING));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidationState.setIdIdrefChecking(true);
        }
        try {
            this.fValidationState.setUnparsedEntityChecking(xMLComponentManager.getFeature(UNPARSED_ENTITY_CHECKING));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidationState.setUnparsedEntityChecking(true);
        }
        try {
            this.fExternalSchemas = (String)xMLComponentManager.getProperty(SCHEMA_LOCATION);
            this.fExternalNoNamespaceSchema = (String)xMLComponentManager.getProperty(SCHEMA_NONS_LOCATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fExternalSchemas = null;
            this.fExternalNoNamespaceSchema = null;
        }
        XMLSchemaLoader.processExternalHints(this.fExternalSchemas, this.fExternalNoNamespaceSchema, this.fLocationPairs, this.fXSIErrorReporter.fErrorReporter);
        try {
            this.fJaxpSchemaSource = xMLComponentManager.getProperty(JAXP_SCHEMA_SOURCE);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fJaxpSchemaSource = null;
        }
        try {
            this.fGrammarPool = (XMLGrammarPool)xMLComponentManager.getProperty(XMLGRAMMAR_POOL);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fGrammarPool = null;
        }
        this.fState4XsiType.setSymbolTable(symbolTable);
        this.fState4ApplyDefault.setSymbolTable(symbolTable);
    }

    @Override
    public void startValueScopeFor(IdentityConstraint identityConstraint, int n) {
        ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(identityConstraint, n);
        valueStoreBase.startValueScope();
    }

    @Override
    public XPathMatcher activateField(Field field, int n) {
        ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(field.getIdentityConstraint(), n);
        XPathMatcher xPathMatcher = field.createMatcher(valueStoreBase);
        this.fMatcherStack.addMatcher(xPathMatcher);
        xPathMatcher.startDocumentFragment();
        return xPathMatcher;
    }

    @Override
    public void endValueScopeFor(IdentityConstraint identityConstraint, int n) {
        ValueStoreBase valueStoreBase = this.fValueStoreCache.getValueStoreFor(identityConstraint, n);
        valueStoreBase.endValueScope();
    }

    private void activateSelectorFor(IdentityConstraint identityConstraint) {
        Selector selector = identityConstraint.getSelector();
        XMLSchemaValidator xMLSchemaValidator = this;
        if (selector == null) {
            return;
        }
        XPathMatcher xPathMatcher = selector.createMatcher(xMLSchemaValidator, this.fElementDepth);
        this.fMatcherStack.addMatcher(xPathMatcher);
        xPathMatcher.startDocumentFragment();
    }

    @Override
    public XSElementDecl getGlobalElementDecl(org.apache.xerces.xni.QName qName) {
        SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)5, qName.uri, null, qName, null);
        if (schemaGrammar != null) {
            return schemaGrammar.getGlobalElementDecl(qName.localpart);
        }
        return null;
    }

    void ensureStackCapacity() {
        if (this.fElementDepth == this.fElemDeclStack.length) {
            int n = this.fElementDepth + 8;
            boolean[] blArray = new boolean[n];
            System.arraycopy(this.fSubElementStack, 0, blArray, 0, this.fElementDepth);
            this.fSubElementStack = blArray;
            XSElementDecl[] xSElementDeclArray = new XSElementDecl[n];
            System.arraycopy(this.fElemDeclStack, 0, xSElementDeclArray, 0, this.fElementDepth);
            this.fElemDeclStack = xSElementDeclArray;
            blArray = new boolean[n];
            System.arraycopy(this.fNilStack, 0, blArray, 0, this.fElementDepth);
            this.fNilStack = blArray;
            XSNotationDecl[] xSNotationDeclArray = new XSNotationDecl[n];
            System.arraycopy(this.fNotationStack, 0, xSNotationDeclArray, 0, this.fElementDepth);
            this.fNotationStack = xSNotationDeclArray;
            XSTypeDefinition[] xSTypeDefinitionArray = new XSTypeDefinition[n];
            System.arraycopy(this.fTypeStack, 0, xSTypeDefinitionArray, 0, this.fElementDepth);
            this.fTypeStack = xSTypeDefinitionArray;
            XSCMValidator[] xSCMValidatorArray = new XSCMValidator[n];
            System.arraycopy(this.fCMStack, 0, xSCMValidatorArray, 0, this.fElementDepth);
            this.fCMStack = xSCMValidatorArray;
            blArray = new boolean[n];
            System.arraycopy(this.fSawTextStack, 0, blArray, 0, this.fElementDepth);
            this.fSawTextStack = blArray;
            blArray = new boolean[n];
            System.arraycopy(this.fStringContent, 0, blArray, 0, this.fElementDepth);
            this.fStringContent = blArray;
            blArray = new boolean[n];
            System.arraycopy(this.fStrictAssessStack, 0, blArray, 0, this.fElementDepth);
            this.fStrictAssessStack = blArray;
            int[][] nArrayArray = new int[n][];
            System.arraycopy(this.fCMStateStack, 0, nArrayArray, 0, this.fElementDepth);
            this.fCMStateStack = nArrayArray;
        }
    }

    void handleStartDocument(XMLLocator xMLLocator, String string) {
        if (this.fIDCChecking) {
            this.fValueStoreCache.startDocument();
        }
        if (this.fAugPSVI) {
            this.fCurrentPSVI.fGrammars = null;
            this.fCurrentPSVI.fSchemaInformation = null;
        }
    }

    void handleEndDocument() {
        if (this.fIDCChecking) {
            this.fValueStoreCache.endDocument();
        }
    }

    XMLString handleCharacters(XMLString xMLString) {
        if (this.fSkipValidationDepth >= 0) {
            return xMLString;
        }
        boolean bl = this.fSawText = this.fSawText || xMLString.length > 0;
        if (this.fNormalizeData && this.fWhiteSpace != -1 && this.fWhiteSpace != 0) {
            this.normalizeWhitespace(xMLString, this.fWhiteSpace == 2);
            xMLString = this.fNormalizedStr;
        }
        if (this.fAppendBuffer) {
            this.fBuffer.append(xMLString.ch, xMLString.offset, xMLString.length);
        }
        if (this.fCurrentType != null && this.fCurrentType.getTypeCategory() == 15) {
            XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
            if (xSComplexTypeDecl.fContentType == 2) {
                for (int i = xMLString.offset; i < xMLString.offset + xMLString.length; ++i) {
                    if (XMLChar.isSpace(xMLString.ch[i])) continue;
                    this.fSawCharacters = true;
                    break;
                }
            }
        }
        return xMLString;
    }

    private void normalizeWhitespace(XMLString xMLString, boolean bl) {
        boolean bl2 = bl;
        boolean bl3 = false;
        boolean bl4 = false;
        boolean bl5 = false;
        int n = xMLString.offset + xMLString.length;
        if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < xMLString.length + 1) {
            this.fNormalizedStr.ch = new char[xMLString.length + 1];
        }
        this.fNormalizedStr.offset = 1;
        this.fNormalizedStr.length = 1;
        for (int i = xMLString.offset; i < n; ++i) {
            char c = xMLString.ch[i];
            if (XMLChar.isSpace(c)) {
                if (!bl2) {
                    this.fNormalizedStr.ch[this.fNormalizedStr.length++] = 32;
                    bl2 = bl;
                }
                if (bl3) continue;
                bl4 = true;
                continue;
            }
            this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
            bl2 = false;
            bl3 = true;
        }
        if (bl2) {
            if (this.fNormalizedStr.length > 1) {
                --this.fNormalizedStr.length;
                bl5 = true;
            } else if (bl4 && !this.fFirstChunk) {
                bl5 = true;
            }
        }
        if (this.fNormalizedStr.length > 1 && !this.fFirstChunk && this.fWhiteSpace == 2) {
            if (this.fTrailing) {
                this.fNormalizedStr.offset = 0;
                this.fNormalizedStr.ch[0] = 32;
            } else if (bl4) {
                this.fNormalizedStr.offset = 0;
                this.fNormalizedStr.ch[0] = 32;
            }
        }
        this.fNormalizedStr.length -= this.fNormalizedStr.offset;
        this.fTrailing = bl5;
        if (bl5 || bl3) {
            this.fFirstChunk = false;
        }
    }

    private void normalizeWhitespace(String string, boolean bl) {
        boolean bl2 = bl;
        int n = string.length();
        if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < n) {
            this.fNormalizedStr.ch = new char[n];
        }
        this.fNormalizedStr.offset = 0;
        this.fNormalizedStr.length = 0;
        for (int i = 0; i < n; ++i) {
            char c = string.charAt(i);
            if (XMLChar.isSpace(c)) {
                if (bl2) continue;
                this.fNormalizedStr.ch[this.fNormalizedStr.length++] = 32;
                bl2 = bl;
                continue;
            }
            this.fNormalizedStr.ch[this.fNormalizedStr.length++] = c;
            bl2 = false;
        }
        if (bl2 && this.fNormalizedStr.length != 0) {
            --this.fNormalizedStr.length;
        }
    }

    void handleIgnorableWhitespace(XMLString xMLString) {
        if (this.fSkipValidationDepth >= 0) {
            return;
        }
    }

    Augmentations handleStartElement(org.apache.xerces.xni.QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) {
        int n;
        Object object;
        Object object2;
        Object object3;
        Object object4;
        Object object5;
        Object object6;
        if (this.fElementDepth == -1 && this.fValidationManager.isGrammarFound() && this.fSchemaType == null) {
            this.fSchemaDynamicValidation = true;
        }
        if (!this.fUseGrammarPoolOnly) {
            object6 = xMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_SCHEMALOCATION);
            object5 = xMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION);
            this.storeLocations((String)object6, (String)object5);
        }
        if (this.fSkipValidationDepth >= 0) {
            ++this.fElementDepth;
            if (this.fAugPSVI) {
                augmentations = this.getEmptyAugs(augmentations);
            }
            return augmentations;
        }
        object6 = null;
        if (this.fCurrentCM != null) {
            object6 = this.fCurrentCM.oneTransition(qName, this.fCurrCMState, this.fSubGroupHandler);
            if (this.fCurrCMState[0] == -1) {
                object5 = (XSComplexTypeDecl)this.fCurrentType;
                if (((XSComplexTypeDecl)object5).fParticle != null && ((Vector)(object4 = this.fCurrentCM.whatCanGoHere(this.fCurrCMState))).size() > 0) {
                    object3 = this.expectedStr((Vector)object4);
                    object2 = this.fCurrentCM.occurenceInfo(this.fCurrCMState);
                    Object object7 = object = qName.uri != null ? "{\"" + qName.uri + '\"' + ":" + qName.localpart + "}" : qName.localpart;
                    if (object2 != null) {
                        n = object2[0];
                        int n2 = object2[1];
                        int n3 = object2[2];
                        if (n3 < n) {
                            int n4 = n - n3;
                            if (n4 > 1) {
                                this.reportSchemaError("cvc-complex-type.2.4.h", new Object[]{qName.rawname, this.fCurrentCM.getTermName(object2[3]), Integer.toString(n), Integer.toString(n4)});
                            } else {
                                this.reportSchemaError("cvc-complex-type.2.4.g", new Object[]{qName.rawname, this.fCurrentCM.getTermName(object2[3]), Integer.toString(n)});
                            }
                        } else if (n3 >= n2 && n2 != -1) {
                            this.reportSchemaError("cvc-complex-type.2.4.e", new Object[]{qName.rawname, object3, Integer.toString(n2)});
                        } else {
                            this.reportSchemaError("cvc-complex-type.2.4.a", new Object[]{object, object3});
                        }
                    } else {
                        this.reportSchemaError("cvc-complex-type.2.4.a", new Object[]{object, object3});
                    }
                } else {
                    object3 = this.fCurrentCM.occurenceInfo(this.fCurrCMState);
                    if (object3 != null) {
                        int n5 = object3[2];
                        int n6 = object3[1];
                        if (n5 >= n6 && n6 != -1) {
                            this.reportSchemaError("cvc-complex-type.2.4.f", new Object[]{this.fCurrentCM.getTermName(object3[3]), Integer.toString(n6)});
                        } else {
                            this.reportSchemaError("cvc-complex-type.2.4.d", new Object[]{qName.rawname});
                        }
                    } else {
                        this.reportSchemaError("cvc-complex-type.2.4.d", new Object[]{qName.rawname});
                    }
                }
            }
        }
        if (this.fElementDepth != -1) {
            this.ensureStackCapacity();
            this.fSubElementStack[this.fElementDepth] = true;
            this.fSubElement = false;
            this.fElemDeclStack[this.fElementDepth] = this.fCurrentElemDecl;
            this.fNilStack[this.fElementDepth] = this.fNil;
            this.fNotationStack[this.fElementDepth] = this.fNotation;
            this.fTypeStack[this.fElementDepth] = this.fCurrentType;
            this.fStrictAssessStack[this.fElementDepth] = this.fStrictAssess;
            this.fCMStack[this.fElementDepth] = this.fCurrentCM;
            this.fCMStateStack[this.fElementDepth] = this.fCurrCMState;
            this.fSawTextStack[this.fElementDepth] = this.fSawText;
            this.fStringContent[this.fElementDepth] = this.fSawCharacters;
        }
        ++this.fElementDepth;
        this.fCurrentElemDecl = null;
        object5 = null;
        this.fCurrentType = null;
        this.fStrictAssess = true;
        this.fNil = false;
        this.fNotation = null;
        this.fBuffer.setLength(0);
        this.fSawText = false;
        this.fSawCharacters = false;
        if (object6 != null) {
            if (object6 instanceof XSElementDecl) {
                this.fCurrentElemDecl = (XSElementDecl)object6;
            } else {
                object5 = (XSWildcardDecl)object6;
            }
        }
        if (object5 != null && ((XSWildcardDecl)object5).fProcessContents == 2) {
            this.fSkipValidationDepth = this.fElementDepth;
            if (this.fAugPSVI) {
                augmentations = this.getEmptyAugs(augmentations);
            }
            return augmentations;
        }
        if (this.fElementDepth == 0) {
            if (this.fRootElementDeclaration != null) {
                this.fCurrentElemDecl = this.fRootElementDeclaration;
                this.checkElementMatchesRootElementDecl(this.fCurrentElemDecl, qName);
            } else if (this.fRootElementDeclQName != null) {
                this.processRootElementDeclQName(this.fRootElementDeclQName, qName);
            } else if (this.fRootTypeDefinition != null) {
                this.fCurrentType = this.fRootTypeDefinition;
            } else if (this.fRootTypeQName != null) {
                this.processRootTypeQName(this.fRootTypeQName);
            }
        }
        if (this.fCurrentType == null) {
            if (this.fCurrentElemDecl == null && (object4 = this.findSchemaGrammar((short)5, qName.uri, null, qName, xMLAttributes)) != null) {
                this.fCurrentElemDecl = ((SchemaGrammar)object4).getGlobalElementDecl(qName.localpart);
            }
            if (this.fCurrentElemDecl != null) {
                this.fCurrentType = this.fCurrentElemDecl.fType;
            }
        }
        if (this.fElementDepth == this.fIgnoreXSITypeDepth && this.fCurrentElemDecl == null) {
            ++this.fIgnoreXSITypeDepth;
        }
        object4 = null;
        if (this.fElementDepth >= this.fIgnoreXSITypeDepth) {
            object4 = xMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_TYPE);
        }
        if (this.fCurrentType == null && object4 == null) {
            if (this.fElementDepth == 0) {
                if (this.fDynamicValidation || this.fSchemaDynamicValidation) {
                    if (this.fDocumentSource != null) {
                        this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
                        if (this.fDocumentHandler != null) {
                            this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
                        }
                        this.fElementDepth = -2;
                        return augmentations;
                    }
                    this.fSkipValidationDepth = this.fElementDepth;
                    if (this.fAugPSVI) {
                        augmentations = this.getEmptyAugs(augmentations);
                    }
                    return augmentations;
                }
                this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "cvc-elt.1.a", new Object[]{qName.rawname}, (short)1);
            } else if (object5 != null && ((XSWildcardDecl)object5).fProcessContents == 1) {
                this.reportSchemaError("cvc-complex-type.2.4.c", new Object[]{qName.rawname});
            }
            this.fCurrentType = SchemaGrammar.fAnyType;
            this.fStrictAssess = false;
            this.fNFullValidationDepth = this.fElementDepth;
            this.fAppendBuffer = false;
            this.fXSIErrorReporter.pushContext();
        } else {
            this.fXSIErrorReporter.pushContext();
            if (object4 != null) {
                object3 = this.fCurrentType;
                this.fCurrentType = this.getAndCheckXsiType(qName, (String)object4, xMLAttributes);
                if (this.fCurrentType == null) {
                    this.fCurrentType = object3 == null ? SchemaGrammar.fAnyType : (Object)object3;
                }
            }
            this.fNNoneValidationDepth = this.fElementDepth;
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                this.fAppendBuffer = true;
            } else if (this.fCurrentType.getTypeCategory() == 16) {
                this.fAppendBuffer = true;
            } else {
                object3 = (XSComplexTypeDecl)this.fCurrentType;
                boolean bl = this.fAppendBuffer = object3.fContentType == 1;
            }
        }
        if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getAbstract()) {
            this.reportSchemaError("cvc-elt.2", new Object[]{qName.rawname});
        }
        if (this.fElementDepth == 0) {
            this.fValidationRoot = qName.rawname;
        }
        if (this.fNormalizeData) {
            this.fFirstChunk = true;
            this.fTrailing = false;
            this.fUnionType = false;
            this.fWhiteSpace = (short)-1;
        }
        if (this.fCurrentType.getTypeCategory() == 15) {
            object3 = (XSComplexTypeDecl)this.fCurrentType;
            if (object3.getAbstract()) {
                this.reportSchemaError("cvc-type.2", new Object[]{qName.rawname});
            }
            if (this.fNormalizeData && object3.fContentType == 1) {
                if (object3.fXSSimpleType.getVariety() == 3) {
                    this.fUnionType = true;
                } else {
                    try {
                        this.fWhiteSpace = object3.fXSSimpleType.getWhitespace();
                    }
                    catch (DatatypeException datatypeException) {}
                }
            }
        } else if (this.fNormalizeData) {
            object3 = (XSSimpleType)this.fCurrentType;
            if (object3.getVariety() == 3) {
                this.fUnionType = true;
            } else {
                try {
                    this.fWhiteSpace = object3.getWhitespace();
                }
                catch (DatatypeException datatypeException) {
                    // empty catch block
                }
            }
        }
        this.fCurrentCM = null;
        if (this.fCurrentType.getTypeCategory() == 15) {
            this.fCurrentCM = ((XSComplexTypeDecl)this.fCurrentType).getContentModel(this.fCMBuilder);
        }
        this.fCurrCMState = null;
        if (this.fCurrentCM != null) {
            this.fCurrCMState = this.fCurrentCM.startContentModel();
        }
        if ((object3 = xMLAttributes.getValue(SchemaSymbols.URI_XSI, SchemaSymbols.XSI_NIL)) != null && this.fCurrentElemDecl != null) {
            this.fNil = this.getXsiNil(qName, (String)object3);
        }
        object2 = null;
        if (this.fCurrentType.getTypeCategory() == 15) {
            object = (XSComplexTypeDecl)this.fCurrentType;
            object2 = ((XSComplexTypeDecl)object).getAttrGrp();
        }
        if (this.fIDCChecking) {
            this.fValueStoreCache.startElement();
            this.fMatcherStack.pushContext();
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.fIDCPos > 0) {
                this.fIdConstraint = true;
                this.fValueStoreCache.initValueStoresFor(this.fCurrentElemDecl, this);
            }
        }
        this.processAttributes(qName, xMLAttributes, (XSAttributeGroupDecl)object2);
        if (object2 != null) {
            this.addDefaultAttributes(qName, xMLAttributes, (XSAttributeGroupDecl)object2);
        }
        int n7 = this.fMatcherStack.getMatcherCount();
        for (n = 0; n < n7; ++n) {
            XPathMatcher xPathMatcher = this.fMatcherStack.getMatcherAt(n);
            xPathMatcher.startElement(qName, xMLAttributes);
        }
        if (this.fAugPSVI) {
            augmentations = this.getEmptyAugs(augmentations);
            this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
            this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
            this.fCurrentPSVI.fNotation = this.fNotation;
            this.fCurrentPSVI.fNil = this.fNil;
        }
        return augmentations;
    }

    Augmentations handleEndElement(org.apache.xerces.xni.QName qName, Augmentations augmentations) {
        int n;
        if (this.fSkipValidationDepth >= 0) {
            if (this.fSkipValidationDepth == this.fElementDepth && this.fSkipValidationDepth > 0) {
                this.fNFullValidationDepth = this.fSkipValidationDepth - 1;
                this.fSkipValidationDepth = -1;
                --this.fElementDepth;
                this.fSubElement = this.fSubElementStack[this.fElementDepth];
                this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
                this.fNil = this.fNilStack[this.fElementDepth];
                this.fNotation = this.fNotationStack[this.fElementDepth];
                this.fCurrentType = this.fTypeStack[this.fElementDepth];
                this.fCurrentCM = this.fCMStack[this.fElementDepth];
                this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
                this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
                this.fSawText = this.fSawTextStack[this.fElementDepth];
                this.fSawCharacters = this.fStringContent[this.fElementDepth];
            } else {
                --this.fElementDepth;
            }
            if (this.fElementDepth == -1 && this.fFullChecking && !this.fUseGrammarPoolOnly) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }
            if (this.fAugPSVI) {
                augmentations = this.getEmptyAugs(augmentations);
            }
            return augmentations;
        }
        this.processElementContent(qName);
        if (this.fIDCChecking) {
            Selector.Matcher matcher;
            IdentityConstraint identityConstraint;
            XPathMatcher xPathMatcher;
            int n2;
            int n3 = this.fMatcherStack.getMatcherCount();
            for (n2 = n3 - 1; n2 >= 0; --n2) {
                XPathMatcher xPathMatcher2 = this.fMatcherStack.getMatcherAt(n2);
                if (this.fCurrentElemDecl == null) {
                    xPathMatcher2.endElement(qName, this.fCurrentType, false, this.fValidatedInfo.actualValue, this.fValidatedInfo.actualValueType, this.fValidatedInfo.itemValueTypes);
                    continue;
                }
                xPathMatcher2.endElement(qName, this.fCurrentType, this.fCurrentElemDecl.getNillable(), this.fDefaultValue == null ? this.fValidatedInfo.actualValue : this.fCurrentElemDecl.fDefault.actualValue, this.fDefaultValue == null ? this.fValidatedInfo.actualValueType : this.fCurrentElemDecl.fDefault.actualValueType, this.fDefaultValue == null ? this.fValidatedInfo.itemValueTypes : this.fCurrentElemDecl.fDefault.itemValueTypes);
            }
            if (this.fMatcherStack.size() > 0) {
                this.fMatcherStack.popContext();
            }
            n2 = this.fMatcherStack.getMatcherCount();
            for (n = n3 - 1; n >= n2; --n) {
                xPathMatcher = this.fMatcherStack.getMatcherAt(n);
                if (!(xPathMatcher instanceof Selector.Matcher) || (identityConstraint = (matcher = (Selector.Matcher)xPathMatcher).getIdentityConstraint()) == null || identityConstraint.getCategory() == 2) continue;
                this.fValueStoreCache.transplant(identityConstraint, matcher.getInitialDepth());
            }
            for (n = n3 - 1; n >= n2; --n) {
                ValueStoreBase valueStoreBase;
                xPathMatcher = this.fMatcherStack.getMatcherAt(n);
                if (!(xPathMatcher instanceof Selector.Matcher) || (identityConstraint = (matcher = (Selector.Matcher)xPathMatcher).getIdentityConstraint()) == null || identityConstraint.getCategory() != 2 || (valueStoreBase = this.fValueStoreCache.getValueStoreFor(identityConstraint, matcher.getInitialDepth())) == null || !valueStoreBase.fHasValue) continue;
                valueStoreBase.endDocumentFragment();
            }
            this.fValueStoreCache.endElement();
        }
        if (this.fElementDepth < this.fIgnoreXSITypeDepth) {
            --this.fIgnoreXSITypeDepth;
        }
        Grammar[] grammarArray = null;
        if (this.fElementDepth == 0) {
            Iterator iterator = this.fValidationState.checkIDRefID();
            this.fValidationState.resetIDTables();
            if (iterator != null) {
                while (iterator.hasNext()) {
                    this.reportSchemaError("cvc-id.1", new Object[]{iterator.next()});
                }
            }
            if (this.fFullChecking && !this.fUseGrammarPoolOnly) {
                XSConstraints.fullSchemaChecking(this.fGrammarBucket, this.fSubGroupHandler, this.fCMBuilder, this.fXSIErrorReporter.fErrorReporter);
            }
            grammarArray = this.fGrammarBucket.getGrammars();
            if (this.fGrammarPool != null) {
                for (n = 0; n < grammarArray.length; ++n) {
                    grammarArray[n].setImmutable(true);
                }
                this.fGrammarPool.cacheGrammars("http://www.w3.org/2001/XMLSchema", grammarArray);
            }
            augmentations = this.endElementPSVI(true, (SchemaGrammar[])grammarArray, augmentations);
        } else {
            augmentations = this.endElementPSVI(false, (SchemaGrammar[])grammarArray, augmentations);
            --this.fElementDepth;
            this.fSubElement = this.fSubElementStack[this.fElementDepth];
            this.fCurrentElemDecl = this.fElemDeclStack[this.fElementDepth];
            this.fNil = this.fNilStack[this.fElementDepth];
            this.fNotation = this.fNotationStack[this.fElementDepth];
            this.fCurrentType = this.fTypeStack[this.fElementDepth];
            this.fCurrentCM = this.fCMStack[this.fElementDepth];
            this.fStrictAssess = this.fStrictAssessStack[this.fElementDepth];
            this.fCurrCMState = this.fCMStateStack[this.fElementDepth];
            this.fSawText = this.fSawTextStack[this.fElementDepth];
            this.fSawCharacters = this.fStringContent[this.fElementDepth];
            this.fWhiteSpace = (short)-1;
            this.fAppendBuffer = false;
            this.fUnionType = false;
        }
        return augmentations;
    }

    final Augmentations endElementPSVI(boolean bl, SchemaGrammar[] schemaGrammarArray, Augmentations augmentations) {
        if (this.fAugPSVI) {
            augmentations = this.getEmptyAugs(augmentations);
            this.fCurrentPSVI.fDeclaration = this.fCurrentElemDecl;
            this.fCurrentPSVI.fTypeDecl = this.fCurrentType;
            this.fCurrentPSVI.fNotation = this.fNotation;
            this.fCurrentPSVI.fValidationContext = this.fValidationRoot;
            this.fCurrentPSVI.fNil = this.fNil;
            this.fCurrentPSVI.fValidationAttempted = this.fElementDepth > this.fNFullValidationDepth ? (short)2 : (this.fElementDepth > this.fNNoneValidationDepth ? (short)0 : 1);
            if (this.fNFullValidationDepth == this.fElementDepth) {
                this.fNFullValidationDepth = this.fElementDepth - 1;
            }
            if (this.fNNoneValidationDepth == this.fElementDepth) {
                this.fNNoneValidationDepth = this.fElementDepth - 1;
            }
            if (this.fDefaultValue != null) {
                this.fCurrentPSVI.fSpecified = true;
            }
            this.fCurrentPSVI.fValue.copyFrom(this.fValidatedInfo);
            if (this.fStrictAssess) {
                String[] stringArray = this.fXSIErrorReporter.mergeContext();
                this.fCurrentPSVI.fErrors = stringArray;
                this.fCurrentPSVI.fValidity = (short)(stringArray == null ? 2 : 1);
            } else {
                this.fCurrentPSVI.fValidity = 0;
                this.fXSIErrorReporter.popContext();
            }
            if (bl) {
                this.fCurrentPSVI.fGrammars = schemaGrammarArray;
                this.fCurrentPSVI.fSchemaInformation = null;
            }
        }
        return augmentations;
    }

    Augmentations getEmptyAugs(Augmentations augmentations) {
        if (augmentations == null) {
            augmentations = this.fAugmentations;
            augmentations.removeAllItems();
        }
        augmentations.putItem("ELEMENT_PSVI", this.fCurrentPSVI);
        this.fCurrentPSVI.reset();
        return augmentations;
    }

    void storeLocations(String string, String string2) {
        if (string != null && !XMLSchemaLoader.tokenizeSchemaLocationStr(string, this.fLocationPairs, this.fLocator == null ? null : this.fLocator.getExpandedSystemId())) {
            this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "SchemaLocation", new Object[]{string}, (short)0);
        }
        if (string2 != null) {
            XMLSchemaLoader.LocationArray locationArray = (XMLSchemaLoader.LocationArray)this.fLocationPairs.get(XMLSymbols.EMPTY_STRING);
            if (locationArray == null) {
                locationArray = new XMLSchemaLoader.LocationArray();
                this.fLocationPairs.put(XMLSymbols.EMPTY_STRING, locationArray);
            }
            if (this.fLocator != null) {
                try {
                    string2 = XMLEntityManager.expandSystemId(string2, this.fLocator.getExpandedSystemId(), false);
                }
                catch (URI.MalformedURIException malformedURIException) {
                    // empty catch block
                }
            }
            locationArray.addLocation(string2);
        }
    }

    SchemaGrammar findSchemaGrammar(short s, String string, org.apache.xerces.xni.QName qName, org.apache.xerces.xni.QName qName2, XMLAttributes xMLAttributes) {
        SchemaGrammar schemaGrammar = null;
        schemaGrammar = this.fGrammarBucket.getGrammar(string);
        if (schemaGrammar == null) {
            this.fXSDDescription.setNamespace(string);
            if (this.fGrammarPool != null && (schemaGrammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(this.fXSDDescription)) != null && !this.fGrammarBucket.putGrammar(schemaGrammar, true, this.fNamespaceGrowth)) {
                this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "GrammarConflict", null, (short)0);
                schemaGrammar = null;
            }
        }
        if (!this.fUseGrammarPoolOnly && (schemaGrammar == null || this.fNamespaceGrowth && !this.hasSchemaComponent(schemaGrammar, s, qName2))) {
            String[] stringArray;
            Hashtable hashtable;
            Object v;
            this.fXSDDescription.reset();
            this.fXSDDescription.fContextType = s;
            this.fXSDDescription.setNamespace(string);
            this.fXSDDescription.fEnclosedElementName = qName;
            this.fXSDDescription.fTriggeringComponent = qName2;
            this.fXSDDescription.fAttributes = xMLAttributes;
            if (this.fLocator != null) {
                this.fXSDDescription.setBaseSystemId(this.fLocator.getExpandedSystemId());
            }
            if ((v = (hashtable = this.fLocationPairs).get(string == null ? XMLSymbols.EMPTY_STRING : string)) != null && (stringArray = ((XMLSchemaLoader.LocationArray)v).getLocationArray()).length != 0) {
                this.setLocationHints(this.fXSDDescription, stringArray, schemaGrammar);
            }
            if (schemaGrammar == null || this.fXSDDescription.fLocationHints != null) {
                boolean bl = true;
                if (schemaGrammar != null) {
                    hashtable = EMPTY_TABLE;
                }
                try {
                    XMLInputSource xMLInputSource = XMLSchemaLoader.resolveDocument(this.fXSDDescription, hashtable, this.fEntityResolver);
                    if (schemaGrammar != null && this.fNamespaceGrowth) {
                        try {
                            if (schemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), false))) {
                                bl = false;
                            }
                        }
                        catch (URI.MalformedURIException malformedURIException) {
                            // empty catch block
                        }
                    }
                    if (bl) {
                        schemaGrammar = this.fSchemaLoader.loadSchema(this.fXSDDescription, xMLInputSource, this.fLocationPairs);
                    }
                }
                catch (IOException iOException) {
                    String[] stringArray2 = this.fXSDDescription.getLocationHints();
                    this.fXSIErrorReporter.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", "schema_reference.4", new Object[]{stringArray2 != null ? stringArray2[0] : XMLSymbols.EMPTY_STRING}, (short)0, iOException);
                }
            }
        }
        return schemaGrammar;
    }

    private boolean hasSchemaComponent(SchemaGrammar schemaGrammar, short s, org.apache.xerces.xni.QName qName) {
        String string;
        if (schemaGrammar != null && qName != null && (string = qName.localpart) != null && string.length() > 0) {
            switch (s) {
                case 5: {
                    return schemaGrammar.getElementDeclaration(string) != null;
                }
                case 6: {
                    return schemaGrammar.getAttributeDeclaration(string) != null;
                }
                case 7: {
                    return schemaGrammar.getTypeDefinition(string) != null;
                }
            }
        }
        return false;
    }

    private void setLocationHints(XSDDescription xSDDescription, String[] stringArray, SchemaGrammar schemaGrammar) {
        int n = stringArray.length;
        if (schemaGrammar == null) {
            this.fXSDDescription.fLocationHints = new String[n];
            System.arraycopy(stringArray, 0, this.fXSDDescription.fLocationHints, 0, n);
        } else {
            this.setLocationHints(xSDDescription, stringArray, schemaGrammar.getDocumentLocations());
        }
    }

    private void setLocationHints(XSDDescription xSDDescription, String[] stringArray, StringList stringList) {
        int n = stringArray.length;
        String[] stringArray2 = new String[n];
        int n2 = 0;
        for (int i = 0; i < n; ++i) {
            if (stringList.contains(stringArray[i])) continue;
            stringArray2[n2++] = stringArray[i];
        }
        if (n2 > 0) {
            if (n2 == n) {
                this.fXSDDescription.fLocationHints = stringArray2;
            } else {
                this.fXSDDescription.fLocationHints = new String[n2];
                System.arraycopy(stringArray2, 0, this.fXSDDescription.fLocationHints, 0, n2);
            }
        }
    }

    XSTypeDefinition getAndCheckXsiType(org.apache.xerces.xni.QName qName, String string, XMLAttributes xMLAttributes) {
        SchemaGrammar schemaGrammar;
        org.apache.xerces.xni.QName qName2 = null;
        try {
            qName2 = (org.apache.xerces.xni.QName)this.fQNameDV.validate(string, (ValidationContext)this.fValidationState, null);
        }
        catch (InvalidDatatypeValueException invalidDatatypeValueException) {
            this.reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
            this.reportSchemaError("cvc-elt.4.1", new Object[]{qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_TYPE, string});
            return null;
        }
        XSTypeDefinition xSTypeDefinition = null;
        if (qName2.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA) {
            xSTypeDefinition = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(qName2.localpart);
        }
        if (xSTypeDefinition == null && (schemaGrammar = this.findSchemaGrammar((short)7, qName2.uri, qName, qName2, xMLAttributes)) != null) {
            xSTypeDefinition = schemaGrammar.getGlobalTypeDecl(qName2.localpart);
        }
        if (xSTypeDefinition == null) {
            this.reportSchemaError("cvc-elt.4.2", new Object[]{qName.rawname, string});
            return null;
        }
        if (this.fCurrentType != null) {
            short s = 0;
            if (this.fCurrentElemDecl != null) {
                s = this.fCurrentElemDecl.fBlock;
            }
            if (this.fCurrentType.getTypeCategory() == 15) {
                s = (short)(s | ((XSComplexTypeDecl)this.fCurrentType).fBlock);
            }
            if (!XSConstraints.checkTypeDerivationOk(xSTypeDefinition, this.fCurrentType, s)) {
                this.reportSchemaError("cvc-elt.4.3", new Object[]{qName.rawname, string, XS10TypeHelper.getSchemaTypeName(this.fCurrentType)});
            }
        }
        return xSTypeDefinition;
    }

    boolean getXsiNil(org.apache.xerces.xni.QName qName, String string) {
        if (this.fCurrentElemDecl != null && !this.fCurrentElemDecl.getNillable()) {
            this.reportSchemaError("cvc-elt.3.1", new Object[]{qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL});
        } else {
            String string2 = XMLChar.trim(string);
            if (string2.equals("true") || string2.equals("1")) {
                if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2) {
                    this.reportSchemaError("cvc-elt.3.2.2", new Object[]{qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL});
                }
                return true;
            }
        }
        return false;
    }

    void processAttributes(org.apache.xerces.xni.QName qName, XMLAttributes xMLAttributes, XSAttributeGroupDecl xSAttributeGroupDecl) {
        String string = null;
        int n = xMLAttributes.getLength();
        Augmentations augmentations = null;
        AttributePSVImpl attributePSVImpl = null;
        boolean bl = this.fCurrentType == null || this.fCurrentType.getTypeCategory() == 16;
        XSObjectList xSObjectList = null;
        int n2 = 0;
        XSWildcardDecl xSWildcardDecl = null;
        if (!bl) {
            xSObjectList = xSAttributeGroupDecl.getAttributeUses();
            n2 = xSObjectList.getLength();
            xSWildcardDecl = xSAttributeGroupDecl.fAttributeWC;
        }
        for (int i = 0; i < n; ++i) {
            XSObject xSObject;
            xMLAttributes.getName(i, this.fTempQName);
            if (this.fAugPSVI || this.fIdConstraint) {
                augmentations = xMLAttributes.getAugmentations(i);
                attributePSVImpl = (AttributePSVImpl)augmentations.getItem("ATTRIBUTE_PSVI");
                if (attributePSVImpl != null) {
                    attributePSVImpl.reset();
                } else {
                    attributePSVImpl = new AttributePSVImpl();
                    augmentations.putItem("ATTRIBUTE_PSVI", attributePSVImpl);
                }
                attributePSVImpl.fValidationContext = this.fValidationRoot;
            }
            if (this.fTempQName.uri == SchemaSymbols.URI_XSI) {
                xSObject = null;
                if (this.fTempQName.localpart == SchemaSymbols.XSI_TYPE) {
                    xSObject = XSI_TYPE;
                } else if (this.fTempQName.localpart == SchemaSymbols.XSI_NIL) {
                    xSObject = XSI_NIL;
                } else if (this.fTempQName.localpart == SchemaSymbols.XSI_SCHEMALOCATION) {
                    xSObject = XSI_SCHEMALOCATION;
                } else if (this.fTempQName.localpart == SchemaSymbols.XSI_NONAMESPACESCHEMALOCATION) {
                    xSObject = XSI_NONAMESPACESCHEMALOCATION;
                }
                if (xSObject != null) {
                    this.processOneAttribute(qName, xMLAttributes, i, (XSAttributeDecl)xSObject, null, attributePSVImpl);
                    continue;
                }
            }
            if (this.fTempQName.rawname == XMLSymbols.PREFIX_XMLNS || this.fTempQName.rawname.startsWith("xmlns:")) continue;
            if (bl) {
                this.reportSchemaError("cvc-type.3.1.1", new Object[]{qName.rawname, this.fTempQName.rawname});
                continue;
            }
            xSObject = null;
            for (int j = 0; j < n2; ++j) {
                XSAttributeUseImpl xSAttributeUseImpl = (XSAttributeUseImpl)xSObjectList.item(j);
                if (xSAttributeUseImpl.fAttrDecl.fName != this.fTempQName.localpart || xSAttributeUseImpl.fAttrDecl.fTargetNamespace != this.fTempQName.uri) continue;
                xSObject = xSAttributeUseImpl;
                break;
            }
            if (!(xSObject != null || xSWildcardDecl != null && xSWildcardDecl.allowNamespace(this.fTempQName.uri))) {
                this.reportSchemaError("cvc-complex-type.3.2.2", new Object[]{qName.rawname, this.fTempQName.rawname});
                this.fNFullValidationDepth = this.fElementDepth;
                continue;
            }
            XSAttributeDecl xSAttributeDecl = null;
            if (xSObject != null) {
                xSAttributeDecl = ((XSAttributeUseImpl)xSObject).fAttrDecl;
            } else {
                if (xSWildcardDecl.fProcessContents == 2) continue;
                SchemaGrammar schemaGrammar = this.findSchemaGrammar((short)6, this.fTempQName.uri, qName, this.fTempQName, xMLAttributes);
                if (schemaGrammar != null) {
                    xSAttributeDecl = schemaGrammar.getGlobalAttributeDecl(this.fTempQName.localpart);
                }
                if (xSAttributeDecl == null) {
                    if (xSWildcardDecl.fProcessContents != 1) continue;
                    this.reportSchemaError("cvc-complex-type.3.2.2", new Object[]{qName.rawname, this.fTempQName.rawname});
                    continue;
                }
                if (xSAttributeDecl.fType.getTypeCategory() == 16 && xSAttributeDecl.fType.isIDType()) {
                    if (string != null) {
                        this.reportSchemaError("cvc-complex-type.5.1", new Object[]{qName.rawname, xSAttributeDecl.fName, string});
                    } else {
                        string = xSAttributeDecl.fName;
                    }
                }
            }
            this.processOneAttribute(qName, xMLAttributes, i, xSAttributeDecl, (XSAttributeUseImpl)xSObject, attributePSVImpl);
        }
        if (!bl && xSAttributeGroupDecl.fIDAttrName != null && string != null) {
            this.reportSchemaError("cvc-complex-type.5.2", new Object[]{qName.rawname, string, xSAttributeGroupDecl.fIDAttrName});
        }
    }

    void processOneAttribute(org.apache.xerces.xni.QName qName, XMLAttributes xMLAttributes, int n, XSAttributeDecl xSAttributeDecl, XSAttributeUseImpl xSAttributeUseImpl, AttributePSVImpl attributePSVImpl) {
        String[] stringArray;
        String string = xMLAttributes.getValue(n);
        this.fXSIErrorReporter.pushContext();
        XSSimpleType xSSimpleType = xSAttributeDecl.fType;
        Object object = null;
        try {
            object = xSSimpleType.validate(string, (ValidationContext)this.fValidationState, this.fValidatedInfo);
            if (this.fNormalizeData) {
                xMLAttributes.setValue(n, this.fValidatedInfo.normalizedValue);
            }
            if (xSSimpleType.getVariety() == 1 && xSSimpleType.getPrimitiveKind() == 20) {
                stringArray = (String[])object;
                SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(stringArray.uri);
                if (schemaGrammar != null) {
                    this.fNotation = schemaGrammar.getGlobalNotationDecl(stringArray.localpart);
                }
            }
        }
        catch (InvalidDatatypeValueException invalidDatatypeValueException) {
            this.reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
            this.reportSchemaError("cvc-attribute.3", new Object[]{qName.rawname, this.fTempQName.rawname, string, xSSimpleType instanceof XSSimpleTypeDecl ? ((XSSimpleTypeDecl)xSSimpleType).getTypeName() : xSSimpleType.getName()});
        }
        if (!(object == null || xSAttributeDecl.getConstraintType() != 2 || ValidatedInfo.isComparable(this.fValidatedInfo, xSAttributeDecl.fDefault) && object.equals(xSAttributeDecl.fDefault.actualValue))) {
            this.reportSchemaError("cvc-attribute.4", new Object[]{qName.rawname, this.fTempQName.rawname, string, xSAttributeDecl.fDefault.stringValue()});
        }
        if (!(object == null || xSAttributeUseImpl == null || xSAttributeUseImpl.fConstraintType != 2 || ValidatedInfo.isComparable(this.fValidatedInfo, xSAttributeUseImpl.fDefault) && object.equals(xSAttributeUseImpl.fDefault.actualValue))) {
            this.reportSchemaError("cvc-complex-type.3.1", new Object[]{qName.rawname, this.fTempQName.rawname, string, xSAttributeUseImpl.fDefault.stringValue()});
        }
        if (this.fIdConstraint) {
            attributePSVImpl.fValue.copyFrom(this.fValidatedInfo);
        }
        if (this.fAugPSVI) {
            attributePSVImpl.fDeclaration = xSAttributeDecl;
            attributePSVImpl.fTypeDecl = xSSimpleType;
            attributePSVImpl.fValue.copyFrom(this.fValidatedInfo);
            attributePSVImpl.fValidationAttempted = (short)2;
            this.fNNoneValidationDepth = this.fElementDepth;
            stringArray = this.fXSIErrorReporter.mergeContext();
            attributePSVImpl.fErrors = stringArray;
            attributePSVImpl.fValidity = (short)(stringArray == null ? 2 : 1);
        }
    }

    void addDefaultAttributes(org.apache.xerces.xni.QName qName, XMLAttributes xMLAttributes, XSAttributeGroupDecl xSAttributeGroupDecl) {
        XSObjectList xSObjectList = xSAttributeGroupDecl.getAttributeUses();
        int n = xSObjectList.getLength();
        for (int i = 0; i < n; ++i) {
            int n2;
            Object object;
            String string;
            boolean bl;
            XSAttributeUseImpl xSAttributeUseImpl = (XSAttributeUseImpl)xSObjectList.item(i);
            XSAttributeDecl xSAttributeDecl = xSAttributeUseImpl.fAttrDecl;
            short s = xSAttributeUseImpl.fConstraintType;
            ValidatedInfo validatedInfo = xSAttributeUseImpl.fDefault;
            if (s == 0) {
                s = xSAttributeDecl.getConstraintType();
                validatedInfo = xSAttributeDecl.fDefault;
            }
            boolean bl2 = bl = xMLAttributes.getValue(xSAttributeDecl.fTargetNamespace, xSAttributeDecl.fName) != null;
            if (xSAttributeUseImpl.fUse == 1 && !bl) {
                if (xSAttributeDecl.fTargetNamespace != null) {
                    this.reportSchemaError("cvc-complex-type.4_ns", new Object[]{qName.rawname, xSAttributeDecl.fName, xSAttributeDecl.fTargetNamespace});
                } else {
                    this.reportSchemaError("cvc-complex-type.4", new Object[]{qName.rawname, xSAttributeDecl.fName});
                }
            }
            if (bl || s == 0) continue;
            org.apache.xerces.xni.QName qName2 = new org.apache.xerces.xni.QName(null, xSAttributeDecl.fName, xSAttributeDecl.fName, xSAttributeDecl.fTargetNamespace);
            String string2 = string = validatedInfo != null ? validatedInfo.stringValue() : "";
            if (xMLAttributes instanceof XMLAttributesImpl) {
                object = (XMLAttributesImpl)xMLAttributes;
                n2 = ((XMLAttributesImpl)object).getLength();
                ((XMLAttributesImpl)object).addAttributeNS(qName2, "CDATA", string);
            } else {
                n2 = xMLAttributes.addAttribute(qName2, "CDATA", string);
            }
            if (!this.fAugPSVI) continue;
            object = xMLAttributes.getAugmentations(n2);
            AttributePSVImpl attributePSVImpl = new AttributePSVImpl();
            object.putItem("ATTRIBUTE_PSVI", attributePSVImpl);
            attributePSVImpl.fDeclaration = xSAttributeDecl;
            attributePSVImpl.fTypeDecl = xSAttributeDecl.fType;
            attributePSVImpl.fValue.copyFrom(validatedInfo);
            attributePSVImpl.fValidationContext = this.fValidationRoot;
            attributePSVImpl.fValidity = (short)2;
            attributePSVImpl.fValidationAttempted = (short)2;
            attributePSVImpl.fSpecified = true;
        }
    }

    void processElementContent(org.apache.xerces.xni.QName qName) {
        int n;
        Object object;
        if (!(this.fCurrentElemDecl == null || this.fCurrentElemDecl.fDefault == null || this.fSawText || this.fSubElement || this.fNil)) {
            object = this.fCurrentElemDecl.fDefault.stringValue();
            n = ((String)object).length();
            if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < n) {
                this.fNormalizedStr.ch = new char[n];
            }
            ((String)object).getChars(0, n, this.fNormalizedStr.ch, 0);
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.length = n;
            this.fDefaultValue = this.fNormalizedStr;
        }
        this.fValidatedInfo.normalizedValue = null;
        if (this.fNil && (this.fSubElement || this.fSawText)) {
            this.reportSchemaError("cvc-elt.3.2.1", new Object[]{qName.rawname, SchemaSymbols.URI_XSI + "," + SchemaSymbols.XSI_NIL});
        }
        this.fValidatedInfo.reset();
        if (!(this.fCurrentElemDecl == null || this.fCurrentElemDecl.getConstraintType() == 0 || this.fSubElement || this.fSawText || this.fNil)) {
            if (this.fCurrentType != this.fCurrentElemDecl.fType && XSConstraints.ElementDefaultValidImmediate(this.fCurrentType, this.fCurrentElemDecl.fDefault.stringValue(), this.fState4XsiType, null) == null) {
                this.reportSchemaError("cvc-elt.5.1.1", new Object[]{qName.rawname, this.fCurrentType.getName(), this.fCurrentElemDecl.fDefault.stringValue()});
            }
            this.elementLocallyValidType(qName, this.fCurrentElemDecl.fDefault.stringValue());
        } else {
            object = this.elementLocallyValidType(qName, this.fBuffer);
            if (this.fCurrentElemDecl != null && this.fCurrentElemDecl.getConstraintType() == 2 && !this.fNil) {
                String string = this.fBuffer.toString();
                if (this.fSubElement) {
                    this.reportSchemaError("cvc-elt.5.2.2.1", new Object[]{qName.rawname});
                }
                if (this.fCurrentType.getTypeCategory() == 15) {
                    XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
                    if (xSComplexTypeDecl.fContentType == 3) {
                        if (!this.fCurrentElemDecl.fDefault.normalizedValue.equals(string)) {
                            this.reportSchemaError("cvc-elt.5.2.2.2.1", new Object[]{qName.rawname, string, this.fCurrentElemDecl.fDefault.normalizedValue});
                        }
                    } else if (!(xSComplexTypeDecl.fContentType != 1 || object == null || ValidatedInfo.isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) && object.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
                        this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[]{qName.rawname, string, this.fCurrentElemDecl.fDefault.stringValue()});
                    }
                } else if (!(this.fCurrentType.getTypeCategory() != 16 || object == null || ValidatedInfo.isComparable(this.fValidatedInfo, this.fCurrentElemDecl.fDefault) && object.equals(this.fCurrentElemDecl.fDefault.actualValue))) {
                    this.reportSchemaError("cvc-elt.5.2.2.2.2", new Object[]{qName.rawname, string, this.fCurrentElemDecl.fDefault.stringValue()});
                }
            }
        }
        if (this.fDefaultValue == null && this.fNormalizeData && this.fDocumentHandler != null && this.fUnionType) {
            object = this.fValidatedInfo.normalizedValue;
            if (object == null) {
                object = this.fBuffer.toString();
            }
            n = ((String)object).length();
            if (this.fNormalizedStr.ch == null || this.fNormalizedStr.ch.length < n) {
                this.fNormalizedStr.ch = new char[n];
            }
            ((String)object).getChars(0, n, this.fNormalizedStr.ch, 0);
            this.fNormalizedStr.offset = 0;
            this.fNormalizedStr.length = n;
            this.fDocumentHandler.characters(this.fNormalizedStr, null);
        }
    }

    Object elementLocallyValidType(org.apache.xerces.xni.QName qName, Object object) {
        if (this.fCurrentType == null) {
            return null;
        }
        Object object2 = null;
        if (this.fCurrentType.getTypeCategory() == 16) {
            if (this.fSubElement) {
                this.reportSchemaError("cvc-type.3.1.2", new Object[]{qName.rawname});
            }
            if (!this.fNil) {
                XSSimpleType xSSimpleType = (XSSimpleType)this.fCurrentType;
                try {
                    if (!this.fNormalizeData || this.fUnionType) {
                        this.fValidationState.setNormalizationRequired(true);
                    }
                    object2 = xSSimpleType.validate(object, (ValidationContext)this.fValidationState, this.fValidatedInfo);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportSchemaError("cvc-type.3.1.3", new Object[]{qName.rawname, object});
                }
            }
        } else {
            object2 = this.elementLocallyValidComplexType(qName, object);
        }
        return object2;
    }

    Object elementLocallyValidComplexType(org.apache.xerces.xni.QName qName, Object object) {
        Object object2 = null;
        XSComplexTypeDecl xSComplexTypeDecl = (XSComplexTypeDecl)this.fCurrentType;
        if (!this.fNil) {
            Object object3;
            if (xSComplexTypeDecl.fContentType == 0 && (this.fSubElement || this.fSawText)) {
                this.reportSchemaError("cvc-complex-type.2.1", new Object[]{qName.rawname});
            } else if (xSComplexTypeDecl.fContentType == 1) {
                if (this.fSubElement) {
                    this.reportSchemaError("cvc-complex-type.2.2", new Object[]{qName.rawname});
                }
                object3 = xSComplexTypeDecl.fXSSimpleType;
                try {
                    if (!this.fNormalizeData || this.fUnionType) {
                        this.fValidationState.setNormalizationRequired(true);
                    }
                    object2 = object3.validate(object, (ValidationContext)this.fValidationState, this.fValidatedInfo);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportSchemaError(invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs());
                    this.reportSchemaError("cvc-complex-type.2.2", new Object[]{qName.rawname});
                }
            } else if (xSComplexTypeDecl.fContentType == 2 && this.fSawCharacters) {
                this.reportSchemaError("cvc-complex-type.2.3", new Object[]{qName.rawname});
            }
            if (!(xSComplexTypeDecl.fContentType != 2 && xSComplexTypeDecl.fContentType != 3 || this.fCurrCMState[0] < 0 || this.fCurrentCM.endContentModel(this.fCurrCMState))) {
                object3 = this.expectedStr(this.fCurrentCM.whatCanGoHere(this.fCurrCMState));
                int[] nArray = this.fCurrentCM.occurenceInfo(this.fCurrCMState);
                if (nArray != null) {
                    int n = nArray[2];
                    int n2 = nArray[0];
                    if (n < n2) {
                        int n3 = n2 - n;
                        if (n3 > 1) {
                            this.reportSchemaError("cvc-complex-type.2.4.j", new Object[]{qName.rawname, this.fCurrentCM.getTermName(nArray[3]), Integer.toString(n2), Integer.toString(n3)});
                        } else {
                            this.reportSchemaError("cvc-complex-type.2.4.i", new Object[]{qName.rawname, this.fCurrentCM.getTermName(nArray[3]), Integer.toString(n2)});
                        }
                    } else {
                        this.reportSchemaError("cvc-complex-type.2.4.b", new Object[]{qName.rawname, object3});
                    }
                } else {
                    this.reportSchemaError("cvc-complex-type.2.4.b", new Object[]{qName.rawname, object3});
                }
            }
        }
        return object2;
    }

    void processRootTypeQName(QName qName) {
        Object object;
        String string = qName.getNamespaceURI();
        if ((string = this.fSymbolTable.addSymbol(string)) != null && string.equals("")) {
            string = null;
        }
        if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(string)) {
            this.fCurrentType = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(qName.getLocalPart());
        } else {
            object = this.findSchemaGrammar((short)5, string, null, null, null);
            if (object != null) {
                this.fCurrentType = ((SchemaGrammar)object).getGlobalTypeDecl(qName.getLocalPart());
            }
        }
        if (this.fCurrentType == null) {
            object = qName.getPrefix().equals("") ? qName.getLocalPart() : qName.getPrefix() + ":" + qName.getLocalPart();
            this.reportSchemaError("cvc-type.1", new Object[]{object});
        }
    }

    void processRootElementDeclQName(QName qName, org.apache.xerces.xni.QName qName2) {
        SchemaGrammar schemaGrammar;
        String string = qName.getNamespaceURI();
        if ((string = this.fSymbolTable.addSymbol(string)) != null && string.equals("")) {
            string = null;
        }
        if ((schemaGrammar = this.findSchemaGrammar((short)5, string, null, null, null)) != null) {
            this.fCurrentElemDecl = schemaGrammar.getGlobalElementDecl(qName.getLocalPart());
        }
        if (this.fCurrentElemDecl == null) {
            String string2 = qName.getPrefix().equals("") ? qName.getLocalPart() : qName.getPrefix() + ":" + qName.getLocalPart();
            this.reportSchemaError("cvc-elt.1.a", new Object[]{string2});
        } else {
            this.checkElementMatchesRootElementDecl(this.fCurrentElemDecl, qName2);
        }
    }

    void checkElementMatchesRootElementDecl(XSElementDecl xSElementDecl, org.apache.xerces.xni.QName qName) {
        if (qName.localpart != xSElementDecl.fName || qName.uri != xSElementDecl.fTargetNamespace) {
            this.reportSchemaError("cvc-elt.1.b", new Object[]{qName.rawname, xSElementDecl.fName});
        }
    }

    void reportSchemaError(String string, Object[] objectArray) {
        if (this.fDoValidation) {
            this.fXSIErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)1);
        }
    }

    private String expectedStr(Vector vector) {
        StringBuffer stringBuffer = new StringBuffer("{");
        int n = vector.size();
        for (int i = 0; i < n; ++i) {
            if (i > 0) {
                stringBuffer.append(", ");
            }
            stringBuffer.append(vector.elementAt(i).toString());
        }
        stringBuffer.append('}');
        return stringBuffer.toString();
    }

    protected static final class ShortVector {
        private int fLength;
        private short[] fData;

        public ShortVector() {
        }

        public ShortVector(int n) {
            this.fData = new short[n];
        }

        public int length() {
            return this.fLength;
        }

        public void add(short s) {
            this.ensureCapacity(this.fLength + 1);
            this.fData[this.fLength++] = s;
        }

        public short valueAt(int n) {
            return this.fData[n];
        }

        public void clear() {
            this.fLength = 0;
        }

        public boolean contains(short s) {
            for (int i = 0; i < this.fLength; ++i) {
                if (this.fData[i] != s) continue;
                return true;
            }
            return false;
        }

        private void ensureCapacity(int n) {
            if (this.fData == null) {
                this.fData = new short[8];
            } else if (this.fData.length <= n) {
                short[] sArray = new short[this.fData.length * 2];
                System.arraycopy(this.fData, 0, sArray, 0, this.fData.length);
                this.fData = sArray;
            }
        }
    }

    protected static final class LocalIDKey {
        public IdentityConstraint fId;
        public int fDepth;

        public LocalIDKey() {
        }

        public LocalIDKey(IdentityConstraint identityConstraint, int n) {
            this.fId = identityConstraint;
            this.fDepth = n;
        }

        public int hashCode() {
            return this.fId.hashCode() + this.fDepth;
        }

        public boolean equals(Object object) {
            if (object instanceof LocalIDKey) {
                LocalIDKey localIDKey = (LocalIDKey)object;
                return localIDKey.fId == this.fId && localIDKey.fDepth == this.fDepth;
            }
            return false;
        }
    }

    protected class ValueStoreCache {
        final LocalIDKey fLocalId = new LocalIDKey();
        protected final ArrayList fValueStores = new ArrayList();
        protected final HashMap fIdentityConstraint2ValueStoreMap = new HashMap();
        protected final Stack fGlobalMapStack = new Stack();
        protected final HashMap fGlobalIDConstraintMap = new HashMap();

        public void startDocument() {
            this.fValueStores.clear();
            this.fIdentityConstraint2ValueStoreMap.clear();
            this.fGlobalIDConstraintMap.clear();
            this.fGlobalMapStack.removeAllElements();
        }

        public void startElement() {
            if (this.fGlobalIDConstraintMap.size() > 0) {
                this.fGlobalMapStack.push(this.fGlobalIDConstraintMap.clone());
            } else {
                this.fGlobalMapStack.push(null);
            }
            this.fGlobalIDConstraintMap.clear();
        }

        public void endElement() {
            if (this.fGlobalMapStack.isEmpty()) {
                return;
            }
            HashMap hashMap = (HashMap)this.fGlobalMapStack.pop();
            if (hashMap == null) {
                return;
            }
            for (Map.Entry entry : hashMap.entrySet()) {
                IdentityConstraint identityConstraint = (IdentityConstraint)entry.getKey();
                ValueStoreBase valueStoreBase = (ValueStoreBase)entry.getValue();
                if (valueStoreBase == null) continue;
                ValueStoreBase valueStoreBase2 = (ValueStoreBase)this.fGlobalIDConstraintMap.get(identityConstraint);
                if (valueStoreBase2 == null) {
                    this.fGlobalIDConstraintMap.put(identityConstraint, valueStoreBase);
                    continue;
                }
                if (valueStoreBase2 == valueStoreBase) continue;
                valueStoreBase2.append(valueStoreBase);
            }
        }

        public void initValueStoresFor(XSElementDecl xSElementDecl, FieldActivator fieldActivator) {
            IdentityConstraint[] identityConstraintArray = xSElementDecl.fIDConstraints;
            int n = xSElementDecl.fIDCPos;
            block5: for (int i = 0; i < n; ++i) {
                switch (identityConstraintArray[i].getCategory()) {
                    case 3: {
                        UniqueOrKey uniqueOrKey = (UniqueOrKey)identityConstraintArray[i];
                        LocalIDKey localIDKey = new LocalIDKey(uniqueOrKey, XMLSchemaValidator.this.fElementDepth);
                        UniqueValueStore uniqueValueStore = (UniqueValueStore)this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
                        if (uniqueValueStore == null) {
                            uniqueValueStore = new UniqueValueStore(uniqueOrKey);
                            this.fIdentityConstraint2ValueStoreMap.put(localIDKey, uniqueValueStore);
                        } else {
                            uniqueValueStore.clear();
                        }
                        this.fValueStores.add(uniqueValueStore);
                        XMLSchemaValidator.this.activateSelectorFor(identityConstraintArray[i]);
                        continue block5;
                    }
                    case 1: {
                        UniqueOrKey uniqueOrKey = (UniqueOrKey)identityConstraintArray[i];
                        LocalIDKey localIDKey = new LocalIDKey(uniqueOrKey, XMLSchemaValidator.this.fElementDepth);
                        KeyValueStore keyValueStore = (KeyValueStore)this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
                        if (keyValueStore == null) {
                            keyValueStore = new KeyValueStore(uniqueOrKey);
                            this.fIdentityConstraint2ValueStoreMap.put(localIDKey, keyValueStore);
                        } else {
                            keyValueStore.clear();
                        }
                        this.fValueStores.add(keyValueStore);
                        XMLSchemaValidator.this.activateSelectorFor(identityConstraintArray[i]);
                        continue block5;
                    }
                    case 2: {
                        KeyRef keyRef = (KeyRef)identityConstraintArray[i];
                        LocalIDKey localIDKey = new LocalIDKey(keyRef, XMLSchemaValidator.this.fElementDepth);
                        KeyRefValueStore keyRefValueStore = (KeyRefValueStore)this.fIdentityConstraint2ValueStoreMap.get(localIDKey);
                        if (keyRefValueStore == null) {
                            keyRefValueStore = new KeyRefValueStore(keyRef, null);
                            this.fIdentityConstraint2ValueStoreMap.put(localIDKey, keyRefValueStore);
                        } else {
                            keyRefValueStore.clear();
                        }
                        this.fValueStores.add(keyRefValueStore);
                        XMLSchemaValidator.this.activateSelectorFor(identityConstraintArray[i]);
                    }
                }
            }
        }

        public ValueStoreBase getValueStoreFor(IdentityConstraint identityConstraint, int n) {
            this.fLocalId.fDepth = n;
            this.fLocalId.fId = identityConstraint;
            return (ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
        }

        public ValueStoreBase getGlobalValueStoreFor(IdentityConstraint identityConstraint) {
            return (ValueStoreBase)this.fGlobalIDConstraintMap.get(identityConstraint);
        }

        public void transplant(IdentityConstraint identityConstraint, int n) {
            this.fLocalId.fDepth = n;
            this.fLocalId.fId = identityConstraint;
            ValueStoreBase valueStoreBase = (ValueStoreBase)this.fIdentityConstraint2ValueStoreMap.get(this.fLocalId);
            if (identityConstraint.getCategory() == 2) {
                return;
            }
            ValueStoreBase valueStoreBase2 = (ValueStoreBase)this.fGlobalIDConstraintMap.get(identityConstraint);
            if (valueStoreBase2 != null) {
                valueStoreBase2.append(valueStoreBase);
                this.fGlobalIDConstraintMap.put(identityConstraint, valueStoreBase2);
            } else {
                this.fGlobalIDConstraintMap.put(identityConstraint, valueStoreBase);
            }
        }

        public void endDocument() {
            int n = this.fValueStores.size();
            for (int i = 0; i < n; ++i) {
                ValueStoreBase valueStoreBase = (ValueStoreBase)this.fValueStores.get(i);
                valueStoreBase.endDocument();
            }
        }

        public String toString() {
            String string = super.toString();
            int n = string.lastIndexOf(36);
            if (n != -1) {
                return string.substring(n + 1);
            }
            int n2 = string.lastIndexOf(46);
            if (n2 != -1) {
                return string.substring(n2 + 1);
            }
            return string;
        }
    }

    protected class KeyRefValueStore
    extends ValueStoreBase {
        protected ValueStoreBase fKeyValueStore;

        public KeyRefValueStore(KeyRef keyRef, KeyValueStore keyValueStore) {
            super(keyRef);
            this.fKeyValueStore = keyValueStore;
        }

        @Override
        public void endDocumentFragment() {
            super.endDocumentFragment();
            this.fKeyValueStore = (ValueStoreBase)XMLSchemaValidator.this.fValueStoreCache.fGlobalIDConstraintMap.get(((KeyRef)this.fIdentityConstraint).getKey());
            if (this.fKeyValueStore == null) {
                String string = "KeyRefOutOfScope";
                String string2 = this.fIdentityConstraint.getName();
                XMLSchemaValidator.this.reportSchemaError(string, new Object[]{string2});
                return;
            }
            int n = this.fKeyValueStore.contains(this);
            if (n != -1) {
                String string = "KeyNotFound";
                String string3 = this.toString(this.fValues, n, this.fFieldCount);
                String string4 = this.fIdentityConstraint.getElementName();
                String string5 = this.fIdentityConstraint.getName();
                XMLSchemaValidator.this.reportSchemaError(string, new Object[]{string5, string3, string4});
            }
        }

        @Override
        public void endDocument() {
            super.endDocument();
        }
    }

    protected class KeyValueStore
    extends ValueStoreBase {
        public KeyValueStore(UniqueOrKey uniqueOrKey) {
            super(uniqueOrKey);
        }

        @Override
        protected void checkDuplicateValues() {
            if (this.contains()) {
                String string = "DuplicateKey";
                String string2 = this.toString(this.fLocalValues);
                String string3 = this.fIdentityConstraint.getElementName();
                String string4 = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(string, new Object[]{string2, string3, string4});
            }
        }
    }

    protected class UniqueValueStore
    extends ValueStoreBase {
        public UniqueValueStore(UniqueOrKey uniqueOrKey) {
            super(uniqueOrKey);
        }

        @Override
        protected void checkDuplicateValues() {
            if (this.contains()) {
                String string = "DuplicateUnique";
                String string2 = this.toString(this.fLocalValues);
                String string3 = this.fIdentityConstraint.getElementName();
                String string4 = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(string, new Object[]{string2, string3, string4});
            }
        }
    }

    protected abstract class ValueStoreBase
    implements ValueStore {
        protected IdentityConstraint fIdentityConstraint;
        protected int fFieldCount = 0;
        protected Field[] fFields = null;
        protected Object[] fLocalValues = null;
        protected short[] fLocalValueTypes = null;
        protected ShortList[] fLocalItemValueTypes = null;
        protected int fValuesCount;
        protected boolean fHasValue = false;
        public final Vector fValues = new Vector();
        public ShortVector fValueTypes = null;
        public Vector fItemValueTypes = null;
        private boolean fUseValueTypeVector = false;
        private int fValueTypesLength = 0;
        private short fValueType = 0;
        private boolean fUseItemValueTypeVector = false;
        private int fItemValueTypesLength = 0;
        private ShortList fItemValueType = null;
        final StringBuffer fTempBuffer = new StringBuffer();

        protected ValueStoreBase(IdentityConstraint identityConstraint) {
            this.fIdentityConstraint = identityConstraint;
            this.fFieldCount = this.fIdentityConstraint.getFieldCount();
            this.fFields = new Field[this.fFieldCount];
            this.fLocalValues = new Object[this.fFieldCount];
            this.fLocalValueTypes = new short[this.fFieldCount];
            this.fLocalItemValueTypes = new ShortList[this.fFieldCount];
            for (int i = 0; i < this.fFieldCount; ++i) {
                this.fFields[i] = this.fIdentityConstraint.getFieldAt(i);
            }
        }

        public void clear() {
            this.fValuesCount = 0;
            this.fUseValueTypeVector = false;
            this.fValueTypesLength = 0;
            this.fValueType = 0;
            this.fUseItemValueTypeVector = false;
            this.fItemValueTypesLength = 0;
            this.fItemValueType = null;
            this.fValues.setSize(0);
            if (this.fValueTypes != null) {
                this.fValueTypes.clear();
            }
            if (this.fItemValueTypes != null) {
                this.fItemValueTypes.setSize(0);
            }
        }

        public void append(ValueStoreBase valueStoreBase) {
            for (int i = 0; i < valueStoreBase.fValues.size(); ++i) {
                this.fValues.addElement(valueStoreBase.fValues.elementAt(i));
            }
        }

        public void startValueScope() {
            this.fValuesCount = 0;
            for (int i = 0; i < this.fFieldCount; ++i) {
                this.fLocalValues[i] = null;
                this.fLocalValueTypes[i] = 0;
                this.fLocalItemValueTypes[i] = null;
            }
        }

        public void endValueScope() {
            if (this.fValuesCount == 0) {
                if (this.fIdentityConstraint.getCategory() == 1) {
                    String string = "AbsentKeyValue";
                    String string2 = this.fIdentityConstraint.getElementName();
                    String string3 = this.fIdentityConstraint.getIdentityConstraintName();
                    XMLSchemaValidator.this.reportSchemaError(string, new Object[]{string2, string3});
                }
                return;
            }
            if (this.fValuesCount != this.fFieldCount) {
                if (this.fIdentityConstraint.getCategory() == 1) {
                    String string = "KeyNotEnoughValues";
                    UniqueOrKey uniqueOrKey = (UniqueOrKey)this.fIdentityConstraint;
                    String string4 = this.fIdentityConstraint.getElementName();
                    String string5 = uniqueOrKey.getIdentityConstraintName();
                    XMLSchemaValidator.this.reportSchemaError(string, new Object[]{string4, string5});
                }
                return;
            }
        }

        public void endDocumentFragment() {
        }

        public void endDocument() {
        }

        @Override
        public void reportError(String string, Object[] objectArray) {
            XMLSchemaValidator.this.reportSchemaError(string, objectArray);
        }

        @Override
        public void addValue(Field field, boolean bl, Object object, short s, ShortList shortList) {
            int n;
            for (n = this.fFieldCount - 1; n > -1 && this.fFields[n] != field; --n) {
            }
            if (n == -1) {
                String string = "UnknownField";
                String string2 = this.fIdentityConstraint.getElementName();
                String string3 = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(string, new Object[]{field.toString(), string2, string3});
                return;
            }
            if (!bl) {
                String string = "FieldMultipleMatch";
                String string4 = this.fIdentityConstraint.getIdentityConstraintName();
                XMLSchemaValidator.this.reportSchemaError(string, new Object[]{field.toString(), string4});
            } else {
                ++this.fValuesCount;
                this.fHasValue = true;
            }
            this.fLocalValues[n] = object;
            this.fLocalValueTypes[n] = s;
            this.fLocalItemValueTypes[n] = shortList;
            if (this.fValuesCount == this.fFieldCount) {
                this.checkDuplicateValues();
                for (n = 0; n < this.fFieldCount; ++n) {
                    this.fValues.addElement(this.fLocalValues[n]);
                    this.addValueType(this.fLocalValueTypes[n]);
                    this.addItemValueType(this.fLocalItemValueTypes[n]);
                }
            }
        }

        public boolean contains() {
            int n = 0;
            int n2 = this.fValues.size();
            int n3 = 0;
            while (n3 < n2) {
                block4: {
                    n = n3 + this.fFieldCount;
                    for (int i = 0; i < this.fFieldCount; ++i) {
                        Object object = this.fLocalValues[i];
                        Object e = this.fValues.elementAt(n3);
                        short s = this.fLocalValueTypes[i];
                        short s2 = this.getValueTypeAt(n3);
                        if (object == null || e == null || s != s2 || !object.equals(e)) break block4;
                        if (s == 44 || s == 43) {
                            ShortList shortList = this.fLocalItemValueTypes[i];
                            ShortList shortList2 = this.getItemValueTypeAt(n3);
                            if (shortList == null || shortList2 == null || !shortList.equals(shortList2)) break block4;
                        }
                        ++n3;
                    }
                    return true;
                }
                n3 = n;
            }
            return false;
        }

        public int contains(ValueStoreBase valueStoreBase) {
            Vector vector = valueStoreBase.fValues;
            int n = vector.size();
            if (this.fFieldCount <= 1) {
                for (int i = 0; i < n; ++i) {
                    ShortList shortList;
                    short s = valueStoreBase.getValueTypeAt(i);
                    if (!this.valueTypeContains(s) || !this.fValues.contains(vector.elementAt(i))) {
                        return i;
                    }
                    if (s != 44 && s != 43 || this.itemValueTypeContains(shortList = valueStoreBase.getItemValueTypeAt(i))) continue;
                    return i;
                }
            } else {
                int n2 = this.fValues.size();
                block1: for (int i = 0; i < n; i += this.fFieldCount) {
                    block2: for (int j = 0; j < n2; j += this.fFieldCount) {
                        for (int k = 0; k < this.fFieldCount; ++k) {
                            Object e = vector.elementAt(i + k);
                            Object e2 = this.fValues.elementAt(j + k);
                            short s = valueStoreBase.getValueTypeAt(i + k);
                            short s2 = this.getValueTypeAt(j + k);
                            if (e != e2 && (s != s2 || e == null || !e.equals(e2))) continue block2;
                            if (s != 44 && s != 43) continue;
                            ShortList shortList = valueStoreBase.getItemValueTypeAt(i + k);
                            ShortList shortList2 = this.getItemValueTypeAt(j + k);
                            if (shortList == null || shortList2 == null || !shortList.equals(shortList2)) continue block2;
                        }
                        continue block1;
                    }
                    return i;
                }
            }
            return -1;
        }

        protected void checkDuplicateValues() {
        }

        protected String toString(Object[] objectArray) {
            int n = objectArray.length;
            if (n == 0) {
                return "";
            }
            this.fTempBuffer.setLength(0);
            for (int i = 0; i < n; ++i) {
                if (i > 0) {
                    this.fTempBuffer.append(',');
                }
                this.fTempBuffer.append(objectArray[i]);
            }
            return this.fTempBuffer.toString();
        }

        protected String toString(Vector vector, int n, int n2) {
            if (n2 == 0) {
                return "";
            }
            if (n2 == 1) {
                return String.valueOf(vector.elementAt(n));
            }
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < n2; ++i) {
                if (i > 0) {
                    stringBuffer.append(',');
                }
                stringBuffer.append(vector.elementAt(n + i));
            }
            return stringBuffer.toString();
        }

        public String toString() {
            int n;
            String string = super.toString();
            int n2 = string.lastIndexOf(36);
            if (n2 != -1) {
                string = string.substring(n2 + 1);
            }
            if ((n = string.lastIndexOf(46)) != -1) {
                string = string.substring(n + 1);
            }
            return string + '[' + this.fIdentityConstraint + ']';
        }

        private void addValueType(short s) {
            if (this.fUseValueTypeVector) {
                this.fValueTypes.add(s);
            } else if (this.fValueTypesLength++ == 0) {
                this.fValueType = s;
            } else if (this.fValueType != s) {
                this.fUseValueTypeVector = true;
                if (this.fValueTypes == null) {
                    this.fValueTypes = new ShortVector(this.fValueTypesLength * 2);
                }
                for (int i = 1; i < this.fValueTypesLength; ++i) {
                    this.fValueTypes.add(this.fValueType);
                }
                this.fValueTypes.add(s);
            }
        }

        private short getValueTypeAt(int n) {
            if (this.fUseValueTypeVector) {
                return this.fValueTypes.valueAt(n);
            }
            return this.fValueType;
        }

        private boolean valueTypeContains(short s) {
            if (this.fUseValueTypeVector) {
                return this.fValueTypes.contains(s);
            }
            return this.fValueType == s;
        }

        private void addItemValueType(ShortList shortList) {
            if (this.fUseItemValueTypeVector) {
                this.fItemValueTypes.add(shortList);
            } else if (this.fItemValueTypesLength++ == 0) {
                this.fItemValueType = shortList;
            } else if (!(this.fItemValueType == shortList || this.fItemValueType != null && this.fItemValueType.equals(shortList))) {
                this.fUseItemValueTypeVector = true;
                if (this.fItemValueTypes == null) {
                    this.fItemValueTypes = new Vector(this.fItemValueTypesLength * 2);
                }
                for (int i = 1; i < this.fItemValueTypesLength; ++i) {
                    this.fItemValueTypes.add(this.fItemValueType);
                }
                this.fItemValueTypes.add(shortList);
            }
        }

        private ShortList getItemValueTypeAt(int n) {
            if (this.fUseItemValueTypeVector) {
                return (ShortList)this.fItemValueTypes.elementAt(n);
            }
            return this.fItemValueType;
        }

        private boolean itemValueTypeContains(ShortList shortList) {
            if (this.fUseItemValueTypeVector) {
                return this.fItemValueTypes.contains(shortList);
            }
            return this.fItemValueType == shortList || this.fItemValueType != null && this.fItemValueType.equals(shortList);
        }
    }

    protected static class XPathMatcherStack {
        protected XPathMatcher[] fMatchers = new XPathMatcher[4];
        protected int fMatchersCount;
        protected IntStack fContextStack = new IntStack();

        public void clear() {
            for (int i = 0; i < this.fMatchersCount; ++i) {
                this.fMatchers[i] = null;
            }
            this.fMatchersCount = 0;
            this.fContextStack.clear();
        }

        public int size() {
            return this.fContextStack.size();
        }

        public int getMatcherCount() {
            return this.fMatchersCount;
        }

        public void addMatcher(XPathMatcher xPathMatcher) {
            this.ensureMatcherCapacity();
            this.fMatchers[this.fMatchersCount++] = xPathMatcher;
        }

        public XPathMatcher getMatcherAt(int n) {
            return this.fMatchers[n];
        }

        public void pushContext() {
            this.fContextStack.push(this.fMatchersCount);
        }

        public void popContext() {
            this.fMatchersCount = this.fContextStack.pop();
        }

        private void ensureMatcherCapacity() {
            if (this.fMatchersCount == this.fMatchers.length) {
                XPathMatcher[] xPathMatcherArray = new XPathMatcher[this.fMatchers.length * 2];
                System.arraycopy(this.fMatchers, 0, xPathMatcherArray, 0, this.fMatchers.length);
                this.fMatchers = xPathMatcherArray;
            }
        }
    }

    protected final class XSIErrorReporter {
        XMLErrorReporter fErrorReporter;
        Vector fErrors = new Vector();
        int[] fContext = new int[8];
        int fContextCount;

        protected XSIErrorReporter() {
        }

        public void reset(XMLErrorReporter xMLErrorReporter) {
            this.fErrorReporter = xMLErrorReporter;
            this.fErrors.removeAllElements();
            this.fContextCount = 0;
        }

        public void pushContext() {
            if (!XMLSchemaValidator.this.fAugPSVI) {
                return;
            }
            if (this.fContextCount == this.fContext.length) {
                int n = this.fContextCount + 8;
                int[] nArray = new int[n];
                System.arraycopy(this.fContext, 0, nArray, 0, this.fContextCount);
                this.fContext = nArray;
            }
            this.fContext[this.fContextCount++] = this.fErrors.size();
        }

        public String[] popContext() {
            if (!XMLSchemaValidator.this.fAugPSVI) {
                return null;
            }
            int n = this.fContext[--this.fContextCount];
            int n2 = this.fErrors.size() - n;
            if (n2 == 0) {
                return null;
            }
            String[] stringArray = new String[n2];
            for (int i = 0; i < n2; ++i) {
                stringArray[i] = (String)this.fErrors.elementAt(n + i);
            }
            this.fErrors.setSize(n);
            return stringArray;
        }

        public String[] mergeContext() {
            if (!XMLSchemaValidator.this.fAugPSVI) {
                return null;
            }
            int n = this.fContext[--this.fContextCount];
            int n2 = this.fErrors.size() - n;
            if (n2 == 0) {
                return null;
            }
            String[] stringArray = new String[n2];
            for (int i = 0; i < n2; ++i) {
                stringArray[i] = (String)this.fErrors.elementAt(n + i);
            }
            return stringArray;
        }

        public void reportError(String string, String string2, Object[] objectArray, short s) throws XNIException {
            String string3 = this.fErrorReporter.reportError(string, string2, objectArray, s);
            if (XMLSchemaValidator.this.fAugPSVI) {
                this.fErrors.addElement(string2);
                this.fErrors.addElement(string3);
            }
        }

        public void reportError(XMLLocator xMLLocator, String string, String string2, Object[] objectArray, short s) throws XNIException {
            String string3 = this.fErrorReporter.reportError(xMLLocator, string, string2, objectArray, s);
            if (XMLSchemaValidator.this.fAugPSVI) {
                this.fErrors.addElement(string2);
                this.fErrors.addElement(string3);
            }
        }
    }
}

