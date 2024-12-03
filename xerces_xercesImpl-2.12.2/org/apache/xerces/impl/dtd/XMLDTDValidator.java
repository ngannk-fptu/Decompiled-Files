/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import java.io.IOException;
import java.util.Iterator;
import org.apache.xerces.impl.Constants;
import org.apache.xerces.impl.RevalidationHandler;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dtd.BalancedDTDGrammar;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.DTDGrammarBucket;
import org.apache.xerces.impl.dtd.XMLAttributeDecl;
import org.apache.xerces.impl.dtd.XMLDTDDescription;
import org.apache.xerces.impl.dtd.XMLDTDLoader;
import org.apache.xerces.impl.dtd.XMLDTDValidatorFilter;
import org.apache.xerces.impl.dtd.XMLElementDecl;
import org.apache.xerces.impl.dtd.XMLEntityDecl;
import org.apache.xerces.impl.dtd.models.ContentModelValidator;
import org.apache.xerces.impl.dv.DTDDVFactory;
import org.apache.xerces.impl.dv.DatatypeValidator;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.validation.ValidationManager;
import org.apache.xerces.impl.validation.ValidationState;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
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
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDocumentFilter;
import org.apache.xerces.xni.parser.XMLDocumentSource;

public class XMLDTDValidator
implements XMLComponent,
XMLDocumentFilter,
XMLDTDValidatorFilter,
RevalidationHandler {
    private static final int TOP_LEVEL_SCOPE = -1;
    protected static final String NAMESPACES = "http://xml.org/sax/features/namespaces";
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String DYNAMIC_VALIDATION = "http://apache.org/xml/features/validation/dynamic";
    protected static final String BALANCE_SYNTAX_TREES = "http://apache.org/xml/features/validation/balance-syntax-trees";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DATATYPE_VALIDATOR_FACTORY = "http://apache.org/xml/properties/internal/datatype-validator-factory";
    protected static final String VALIDATION_MANAGER = "http://apache.org/xml/properties/internal/validation-manager";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/namespaces", "http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/dynamic", "http://apache.org/xml/features/validation/balance-syntax-trees"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, null, Boolean.FALSE, Boolean.FALSE};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/datatype-validator-factory", "http://apache.org/xml/properties/internal/validation-manager"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, null, null};
    private static final boolean DEBUG_ATTRIBUTES = false;
    private static final boolean DEBUG_ELEMENT_CHILDREN = false;
    protected ValidationManager fValidationManager = null;
    protected final ValidationState fValidationState = new ValidationState();
    protected boolean fNamespaces;
    protected boolean fValidation;
    protected boolean fDTDValidation;
    protected boolean fDynamicValidation;
    protected boolean fBalanceSyntaxTrees;
    protected boolean fWarnDuplicateAttdef;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected XMLGrammarPool fGrammarPool;
    protected DTDGrammarBucket fGrammarBucket;
    protected XMLLocator fDocLocation;
    protected NamespaceContext fNamespaceContext = null;
    protected DTDDVFactory fDatatypeValidatorFactory;
    protected XMLDocumentHandler fDocumentHandler;
    protected XMLDocumentSource fDocumentSource;
    protected DTDGrammar fDTDGrammar;
    protected boolean fSeenDoctypeDecl = false;
    private boolean fPerformValidation;
    private String fSchemaType;
    private final QName fCurrentElement = new QName();
    private int fCurrentElementIndex = -1;
    private int fCurrentContentSpecType = -1;
    private final QName fRootElement = new QName();
    private boolean fInCDATASection = false;
    private int[] fElementIndexStack = new int[8];
    private int[] fContentSpecTypeStack = new int[8];
    private QName[] fElementQNamePartsStack = new QName[8];
    private QName[] fElementChildren = new QName[32];
    private int fElementChildrenLength = 0;
    private int[] fElementChildrenOffsetStack = new int[32];
    private int fElementDepth = -1;
    private boolean fSeenRootElement = false;
    private boolean fInElementContent = false;
    private final XMLElementDecl fTempElementDecl = new XMLElementDecl();
    private final XMLAttributeDecl fTempAttDecl = new XMLAttributeDecl();
    private final XMLEntityDecl fEntityDecl = new XMLEntityDecl();
    private final QName fTempQName = new QName();
    private final StringBuffer fBuffer = new StringBuffer();
    protected DatatypeValidator fValID;
    protected DatatypeValidator fValIDRef;
    protected DatatypeValidator fValIDRefs;
    protected DatatypeValidator fValENTITY;
    protected DatatypeValidator fValENTITIES;
    protected DatatypeValidator fValNMTOKEN;
    protected DatatypeValidator fValNMTOKENS;
    protected DatatypeValidator fValNOTATION;

    public XMLDTDValidator() {
        for (int i = 0; i < this.fElementQNamePartsStack.length; ++i) {
            this.fElementQNamePartsStack[i] = new QName();
        }
        this.fGrammarBucket = new DTDGrammarBucket();
    }

    DTDGrammarBucket getGrammarBucket() {
        return this.fGrammarBucket;
    }

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        boolean bl;
        this.fDTDGrammar = null;
        this.fSeenDoctypeDecl = false;
        this.fInCDATASection = false;
        this.fSeenRootElement = false;
        this.fInElementContent = false;
        this.fCurrentElementIndex = -1;
        this.fCurrentContentSpecType = -1;
        this.fRootElement.clear();
        this.fValidationState.resetIDTables();
        this.fGrammarBucket.clear();
        this.fElementDepth = -1;
        this.fElementChildrenLength = 0;
        try {
            bl = xMLComponentManager.getFeature(PARSER_SETTINGS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            bl = true;
        }
        if (!bl) {
            this.fValidationManager.addValidationState(this.fValidationState);
            return;
        }
        try {
            this.fNamespaces = xMLComponentManager.getFeature(NAMESPACES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNamespaces = true;
        }
        try {
            this.fValidation = xMLComponentManager.getFeature(VALIDATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidation = false;
        }
        try {
            this.fDTDValidation = !xMLComponentManager.getFeature("http://apache.org/xml/features/validation/schema");
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fDTDValidation = true;
        }
        try {
            this.fDynamicValidation = xMLComponentManager.getFeature(DYNAMIC_VALIDATION);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fDynamicValidation = false;
        }
        try {
            this.fBalanceSyntaxTrees = xMLComponentManager.getFeature(BALANCE_SYNTAX_TREES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fBalanceSyntaxTrees = false;
        }
        try {
            this.fWarnDuplicateAttdef = xMLComponentManager.getFeature(WARN_ON_DUPLICATE_ATTDEF);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fWarnDuplicateAttdef = false;
        }
        try {
            this.fSchemaType = (String)xMLComponentManager.getProperty("http://java.sun.com/xml/jaxp/properties/schemaLanguage");
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fSchemaType = null;
        }
        this.fValidationManager = (ValidationManager)xMLComponentManager.getProperty(VALIDATION_MANAGER);
        this.fValidationManager.addValidationState(this.fValidationState);
        this.fValidationState.setUsingNamespaces(this.fNamespaces);
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        try {
            this.fGrammarPool = (XMLGrammarPool)xMLComponentManager.getProperty(GRAMMAR_POOL);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fGrammarPool = null;
        }
        this.fDatatypeValidatorFactory = (DTDDVFactory)xMLComponentManager.getProperty(DATATYPE_VALIDATOR_FACTORY);
        this.init();
    }

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
        if (this.fGrammarPool != null) {
            Grammar[] grammarArray = this.fGrammarPool.retrieveInitialGrammarSet("http://www.w3.org/TR/REC-xml");
            int n = grammarArray != null ? grammarArray.length : 0;
            for (int i = 0; i < n; ++i) {
                this.fGrammarBucket.putGrammar((DTDGrammar)grammarArray[i]);
            }
        }
        this.fDocLocation = xMLLocator;
        this.fNamespaceContext = namespaceContext;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startDocument(xMLLocator, string, namespaceContext, augmentations);
        }
    }

    @Override
    public void xmlDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.fGrammarBucket.setStandalone(string3 != null && string3.equals("yes"));
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.xmlDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void doctypeDecl(String string, String string2, String string3, Augmentations augmentations) throws XNIException {
        this.fSeenDoctypeDecl = true;
        this.fRootElement.setValues(null, string, string, null);
        String string4 = null;
        try {
            string4 = XMLEntityManager.expandSystemId(string3, this.fDocLocation.getExpandedSystemId(), false);
        }
        catch (IOException iOException) {
            // empty catch block
        }
        XMLDTDDescription xMLDTDDescription = new XMLDTDDescription(string2, string3, this.fDocLocation.getExpandedSystemId(), string4, string);
        this.fDTDGrammar = this.fGrammarBucket.getGrammar(xMLDTDDescription);
        if (this.fDTDGrammar == null && this.fGrammarPool != null && (string3 != null || string2 != null)) {
            this.fDTDGrammar = (DTDGrammar)this.fGrammarPool.retrieveGrammar(xMLDTDDescription);
        }
        if (this.fDTDGrammar == null) {
            this.fDTDGrammar = !this.fBalanceSyntaxTrees ? new DTDGrammar(this.fSymbolTable, xMLDTDDescription) : new BalancedDTDGrammar(this.fSymbolTable, xMLDTDDescription);
        } else {
            this.fValidationManager.setCachedDTD(true);
        }
        this.fGrammarBucket.setActiveGrammar(this.fDTDGrammar);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.doctypeDecl(string, string2, string3, augmentations);
        }
    }

    @Override
    public void startElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        this.handleStartElement(qName, xMLAttributes, augmentations);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startElement(qName, xMLAttributes, augmentations);
        }
    }

    @Override
    public void emptyElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        boolean bl = this.handleStartElement(qName, xMLAttributes, augmentations);
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.emptyElement(qName, xMLAttributes, augmentations);
        }
        if (!bl) {
            this.handleEndElement(qName, augmentations, true);
        }
    }

    @Override
    public void characters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        boolean bl = true;
        boolean bl2 = true;
        for (int i = xMLString.offset; i < xMLString.offset + xMLString.length; ++i) {
            if (this.isSpace(xMLString.ch[i])) continue;
            bl2 = false;
            break;
        }
        if (this.fInElementContent && bl2 && !this.fInCDATASection && this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(xMLString, augmentations);
            bl = false;
        }
        if (this.fPerformValidation) {
            if (this.fInElementContent) {
                if (this.fGrammarBucket.getStandalone() && this.fDTDGrammar.getElementDeclIsExternal(this.fCurrentElementIndex) && bl2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_WHITE_SPACE_IN_ELEMENT_CONTENT_WHEN_STANDALONE", null, (short)1);
                }
                if (!bl2) {
                    this.charDataInContent();
                }
                if (augmentations != null && augmentations.getItem("CHAR_REF_PROBABLE_WS") == Boolean.TRUE) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[]{this.fCurrentElement.rawname, this.fDTDGrammar.getContentSpecAsString(this.fElementDepth), "character reference"}, (short)1);
                }
            }
            if (this.fCurrentContentSpecType == 1) {
                this.charDataInContent();
            }
        }
        if (bl && this.fDocumentHandler != null) {
            this.fDocumentHandler.characters(xMLString, augmentations);
        }
    }

    @Override
    public void ignorableWhitespace(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.ignorableWhitespace(xMLString, augmentations);
        }
    }

    @Override
    public void endElement(QName qName, Augmentations augmentations) throws XNIException {
        this.handleEndElement(qName, augmentations, false);
    }

    @Override
    public void startCDATA(Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fInElementContent) {
            this.charDataInContent();
        }
        this.fInCDATASection = true;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startCDATA(augmentations);
        }
    }

    @Override
    public void endCDATA(Augmentations augmentations) throws XNIException {
        this.fInCDATASection = false;
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endCDATA(augmentations);
        }
    }

    @Override
    public void endDocument(Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endDocument(augmentations);
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
            this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
            if (this.fTempElementDecl.type == 1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[]{this.fCurrentElement.rawname, "EMPTY", "comment"}, (short)1);
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.comment(xMLString, augmentations);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
            this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
            if (this.fTempElementDecl.type == 1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[]{this.fCurrentElement.rawname, "EMPTY", "processing instruction"}, (short)1);
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.processingInstruction(string, xMLString, augmentations);
        }
    }

    @Override
    public void startGeneralEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fElementDepth >= 0 && this.fDTDGrammar != null) {
            this.fDTDGrammar.getElementDecl(this.fCurrentElementIndex, this.fTempElementDecl);
            if (this.fTempElementDecl.type == 1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID_SPECIFIED", new Object[]{this.fCurrentElement.rawname, "EMPTY", "ENTITY"}, (short)1);
            }
            if (this.fGrammarBucket.getStandalone()) {
                XMLDTDLoader.checkStandaloneEntityRef(string, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter);
            }
        }
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.startGeneralEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void endGeneralEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.endGeneralEntity(string, augmentations);
        }
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDocumentHandler != null) {
            this.fDocumentHandler.textDecl(string, string2, augmentations);
        }
    }

    @Override
    public final boolean hasGrammar() {
        return this.fDTDGrammar != null;
    }

    @Override
    public final boolean validate() {
        return this.fSchemaType != Constants.NS_XMLSCHEMA && (!this.fDynamicValidation && this.fValidation || this.fDynamicValidation && this.fSeenDoctypeDecl) && (this.fDTDValidation || this.fSeenDoctypeDecl);
    }

    protected void addDTDDefaultAttrsAndValidate(QName qName, int n, XMLAttributes xMLAttributes) throws XNIException {
        boolean bl;
        int n2;
        String string;
        if (n == -1 || this.fDTDGrammar == null) {
            return;
        }
        int n3 = this.fDTDGrammar.getFirstAttributeDeclIndex(n);
        while (n3 != -1) {
            int n4;
            boolean bl2;
            this.fDTDGrammar.getAttributeDecl(n3, this.fTempAttDecl);
            String string2 = this.fTempAttDecl.name.prefix;
            String string3 = this.fTempAttDecl.name.localpart;
            string = this.fTempAttDecl.name.rawname;
            String string4 = this.getAttributeTypeName(this.fTempAttDecl);
            n2 = this.fTempAttDecl.simpleType.defaultType;
            String string5 = null;
            if (this.fTempAttDecl.simpleType.defaultValue != null) {
                string5 = this.fTempAttDecl.simpleType.defaultValue;
            }
            boolean bl3 = false;
            bl = n2 == 2;
            boolean bl4 = bl2 = string4 == XMLSymbols.fCDATASymbol;
            if (!bl2 || bl || string5 != null) {
                n4 = xMLAttributes.getLength();
                for (int i = 0; i < n4; ++i) {
                    if (xMLAttributes.getQName(i) != string) continue;
                    bl3 = true;
                    break;
                }
            }
            if (!bl3) {
                if (bl) {
                    if (this.fPerformValidation) {
                        Object[] objectArray = new Object[]{qName.localpart, string};
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REQUIRED_ATTRIBUTE_NOT_SPECIFIED", objectArray, (short)1);
                    }
                } else if (string5 != null) {
                    if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && this.fDTDGrammar.getAttributeDeclIsExternal(n3)) {
                        Object[] objectArray = new Object[]{qName.localpart, string};
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DEFAULTED_ATTRIBUTE_NOT_SPECIFIED", objectArray, (short)1);
                    }
                    if (this.fNamespaces && (n4 = string.indexOf(58)) != -1) {
                        string2 = string.substring(0, n4);
                        string2 = this.fSymbolTable.addSymbol(string2);
                        string3 = string.substring(n4 + 1);
                        string3 = this.fSymbolTable.addSymbol(string3);
                    }
                    this.fTempQName.setValues(string2, string3, string, this.fTempAttDecl.name.uri);
                    n4 = xMLAttributes.addAttribute(this.fTempQName, string4, string5);
                }
            }
            n3 = this.fDTDGrammar.getNextAttributeDeclIndex(n3);
        }
        int n5 = xMLAttributes.getLength();
        for (int i = 0; i < n5; ++i) {
            String string6;
            String string7;
            Object[] objectArray;
            String string8;
            String string9;
            string = xMLAttributes.getQName(i);
            boolean bl5 = false;
            if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && (string9 = xMLAttributes.getNonNormalizedValue(i)) != null && (string8 = this.getExternalEntityRefInAttrValue(string9)) != null) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[]{string8}, (short)1);
            }
            n2 = -1;
            int n6 = this.fDTDGrammar.getFirstAttributeDeclIndex(n);
            while (n6 != -1) {
                this.fDTDGrammar.getAttributeDecl(n6, this.fTempAttDecl);
                if (this.fTempAttDecl.name.rawname == string) {
                    n2 = n6;
                    bl5 = true;
                    break;
                }
                n6 = this.fDTDGrammar.getNextAttributeDeclIndex(n6);
            }
            if (!bl5) {
                if (!this.fPerformValidation) continue;
                objectArray = new Object[]{qName.rawname, string};
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_NOT_DECLARED", objectArray, (short)1);
                continue;
            }
            objectArray = this.getAttributeTypeName(this.fTempAttDecl);
            xMLAttributes.setType(i, (String)objectArray);
            xMLAttributes.getAugmentations(i).putItem("ATTRIBUTE_DECLARED", Boolean.TRUE);
            bl = false;
            String string10 = string7 = xMLAttributes.getValue(i);
            if (xMLAttributes.isSpecified(i) && objectArray != XMLSymbols.fCDATASymbol) {
                bl = this.normalizeAttrValue(xMLAttributes, i);
                string10 = xMLAttributes.getValue(i);
                if (this.fPerformValidation && this.fGrammarBucket.getStandalone() && bl && this.fDTDGrammar.getAttributeDeclIsExternal(n6)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTVALUE_CHANGED_DURING_NORMALIZATION_WHEN_STANDALONE", new Object[]{string, string7, string10}, (short)1);
                }
            }
            if (!this.fPerformValidation) continue;
            if (this.fTempAttDecl.simpleType.defaultType == 1 && !string10.equals(string6 = this.fTempAttDecl.simpleType.defaultValue)) {
                Object[] objectArray2 = new Object[]{qName.localpart, string, string10, string6};
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_FIXED_ATTVALUE_INVALID", objectArray2, (short)1);
            }
            if (this.fTempAttDecl.simpleType.type != 1 && this.fTempAttDecl.simpleType.type != 2 && this.fTempAttDecl.simpleType.type != 3 && this.fTempAttDecl.simpleType.type != 4 && this.fTempAttDecl.simpleType.type != 5 && this.fTempAttDecl.simpleType.type != 6) continue;
            this.validateDTDattribute(qName, string10, this.fTempAttDecl);
        }
    }

    protected String getExternalEntityRefInAttrValue(String string) {
        int n = string.length();
        int n2 = string.indexOf(38);
        while (n2 != -1) {
            if (n2 + 1 < n && string.charAt(n2 + 1) != '#') {
                int n3 = string.indexOf(59, n2 + 1);
                String string2 = string.substring(n2 + 1, n3);
                int n4 = this.fDTDGrammar.getEntityDeclIndex(string2 = this.fSymbolTable.addSymbol(string2));
                if (n4 > -1) {
                    this.fDTDGrammar.getEntityDecl(n4, this.fEntityDecl);
                    if (this.fEntityDecl.inExternal || (string2 = this.getExternalEntityRefInAttrValue(this.fEntityDecl.value)) != null) {
                        return string2;
                    }
                }
            }
            n2 = string.indexOf(38, n2 + 1);
        }
        return null;
    }

    protected void validateDTDattribute(QName qName, String string, XMLAttributeDecl xMLAttributeDecl) throws XNIException {
        switch (xMLAttributeDecl.simpleType.type) {
            case 1: {
                boolean bl = xMLAttributeDecl.simpleType.list;
                try {
                    if (bl) {
                        this.fValENTITIES.validate(string, this.fValidationState);
                        break;
                    }
                    this.fValENTITY.validate(string, this.fValidationState);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)1);
                }
                break;
            }
            case 2: 
            case 6: {
                boolean bl = false;
                String[] stringArray = xMLAttributeDecl.simpleType.enumeration;
                if (stringArray == null) {
                    bl = false;
                } else {
                    for (int i = 0; i < stringArray.length; ++i) {
                        if (string != stringArray[i] && !string.equals(stringArray[i])) continue;
                        bl = true;
                        break;
                    }
                }
                if (bl) break;
                StringBuffer stringBuffer = new StringBuffer();
                if (stringArray != null) {
                    for (int i = 0; i < stringArray.length; ++i) {
                        stringBuffer.append(stringArray[i] + " ");
                    }
                }
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATTRIBUTE_VALUE_NOT_IN_LIST", new Object[]{xMLAttributeDecl.name.rawname, string, stringBuffer}, (short)1);
                break;
            }
            case 3: {
                try {
                    this.fValID.validate(string, this.fValidationState);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)1);
                }
                break;
            }
            case 4: {
                boolean bl = xMLAttributeDecl.simpleType.list;
                try {
                    if (bl) {
                        this.fValIDRefs.validate(string, this.fValidationState);
                        break;
                    }
                    this.fValIDRef.validate(string, this.fValidationState);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    if (bl) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDREFSInvalid", new Object[]{string}, (short)1);
                        break;
                    }
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", invalidDatatypeValueException.getKey(), invalidDatatypeValueException.getArgs(), (short)1);
                }
                break;
            }
            case 5: {
                boolean bl = xMLAttributeDecl.simpleType.list;
                try {
                    if (bl) {
                        this.fValNMTOKENS.validate(string, this.fValidationState);
                        break;
                    }
                    this.fValNMTOKEN.validate(string, this.fValidationState);
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    if (bl) {
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NMTOKENSInvalid", new Object[]{string}, (short)1);
                        break;
                    }
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NMTOKENInvalid", new Object[]{string}, (short)1);
                }
                break;
            }
        }
    }

    protected boolean invalidStandaloneAttDef(QName qName, QName qName2) {
        boolean bl = true;
        return bl;
    }

    private boolean normalizeAttrValue(XMLAttributes xMLAttributes, int n) {
        boolean bl = true;
        boolean bl2 = false;
        boolean bl3 = false;
        int n2 = 0;
        int n3 = 0;
        String string = xMLAttributes.getValue(n);
        char[] cArray = new char[string.length()];
        this.fBuffer.setLength(0);
        string.getChars(0, string.length(), cArray, 0);
        for (int i = 0; i < cArray.length; ++i) {
            if (cArray[i] == ' ') {
                if (bl3) {
                    bl2 = true;
                    bl3 = false;
                }
                if (bl2 && !bl) {
                    bl2 = false;
                    this.fBuffer.append(cArray[i]);
                    ++n2;
                    continue;
                }
                if (!bl && bl2) continue;
                ++n3;
                continue;
            }
            bl3 = true;
            bl2 = false;
            bl = false;
            this.fBuffer.append(cArray[i]);
            ++n2;
        }
        if (n2 > 0 && this.fBuffer.charAt(n2 - 1) == ' ') {
            this.fBuffer.setLength(n2 - 1);
        }
        String string2 = this.fBuffer.toString();
        xMLAttributes.setValue(n, string2);
        return !string.equals(string2);
    }

    private final void rootElementSpecified(QName qName) throws XNIException {
        if (this.fPerformValidation) {
            String string = this.fRootElement.rawname;
            String string2 = qName.rawname;
            if (string == null || !string.equals(string2)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "RootElementTypeMustMatchDoctypedecl", new Object[]{string, string2}, (short)1);
            }
        }
    }

    private int checkContent(int n, QName[] qNameArray, int n2, int n3) throws XNIException {
        this.fDTDGrammar.getElementDecl(n, this.fTempElementDecl);
        String string = this.fCurrentElement.rawname;
        int n4 = this.fCurrentContentSpecType;
        if (n4 == 1) {
            if (n3 != 0) {
                return 0;
            }
        } else if (n4 != 0) {
            if (n4 == 2 || n4 == 3) {
                ContentModelValidator contentModelValidator = null;
                contentModelValidator = this.fTempElementDecl.contentModelValidator;
                int n5 = contentModelValidator.validate(qNameArray, n2, n3);
                return n5;
            }
            if (n4 == -1 || n4 == 4) {
                // empty if block
            }
        }
        return -1;
    }

    private int getContentSpecType(int n) {
        int n2 = -1;
        if (n > -1 && this.fDTDGrammar.getElementDecl(n, this.fTempElementDecl)) {
            n2 = this.fTempElementDecl.type;
        }
        return n2;
    }

    private void charDataInContent() {
        Object object;
        if (this.fElementChildren.length <= this.fElementChildrenLength) {
            object = new QName[this.fElementChildren.length * 2];
            System.arraycopy(this.fElementChildren, 0, object, 0, this.fElementChildren.length);
            this.fElementChildren = object;
        }
        if ((object = this.fElementChildren[this.fElementChildrenLength]) == null) {
            for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; ++i) {
                this.fElementChildren[i] = new QName();
            }
            object = this.fElementChildren[this.fElementChildrenLength];
        }
        object.clear();
        ++this.fElementChildrenLength;
    }

    private String getAttributeTypeName(XMLAttributeDecl xMLAttributeDecl) {
        switch (xMLAttributeDecl.simpleType.type) {
            case 1: {
                return xMLAttributeDecl.simpleType.list ? XMLSymbols.fENTITIESSymbol : XMLSymbols.fENTITYSymbol;
            }
            case 2: {
                StringBuffer stringBuffer = new StringBuffer();
                stringBuffer.append('(');
                for (int i = 0; i < xMLAttributeDecl.simpleType.enumeration.length; ++i) {
                    if (i > 0) {
                        stringBuffer.append('|');
                    }
                    stringBuffer.append(xMLAttributeDecl.simpleType.enumeration[i]);
                }
                stringBuffer.append(')');
                return this.fSymbolTable.addSymbol(stringBuffer.toString());
            }
            case 3: {
                return XMLSymbols.fIDSymbol;
            }
            case 4: {
                return xMLAttributeDecl.simpleType.list ? XMLSymbols.fIDREFSSymbol : XMLSymbols.fIDREFSymbol;
            }
            case 5: {
                return xMLAttributeDecl.simpleType.list ? XMLSymbols.fNMTOKENSSymbol : XMLSymbols.fNMTOKENSymbol;
            }
            case 6: {
                return XMLSymbols.fNOTATIONSymbol;
            }
        }
        return XMLSymbols.fCDATASymbol;
    }

    protected void init() {
        if (this.fValidation || this.fDynamicValidation) {
            try {
                this.fValID = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDSymbol);
                this.fValIDRef = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSymbol);
                this.fValIDRefs = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fIDREFSSymbol);
                this.fValENTITY = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITYSymbol);
                this.fValENTITIES = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fENTITIESSymbol);
                this.fValNMTOKEN = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSymbol);
                this.fValNMTOKENS = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNMTOKENSSymbol);
                this.fValNOTATION = this.fDatatypeValidatorFactory.getBuiltInDV(XMLSymbols.fNOTATIONSymbol);
            }
            catch (Exception exception) {
                exception.printStackTrace(System.err);
            }
        }
    }

    private void ensureStackCapacity(int n) {
        if (n == this.fElementQNamePartsStack.length) {
            QName[] qNameArray = new QName[n * 2];
            System.arraycopy(this.fElementQNamePartsStack, 0, qNameArray, 0, n);
            this.fElementQNamePartsStack = qNameArray;
            QName qName = this.fElementQNamePartsStack[n];
            if (qName == null) {
                for (int i = n; i < this.fElementQNamePartsStack.length; ++i) {
                    this.fElementQNamePartsStack[i] = new QName();
                }
            }
            int[] nArray = new int[n * 2];
            System.arraycopy(this.fElementIndexStack, 0, nArray, 0, n);
            this.fElementIndexStack = nArray;
            nArray = new int[n * 2];
            System.arraycopy(this.fContentSpecTypeStack, 0, nArray, 0, n);
            this.fContentSpecTypeStack = nArray;
        }
    }

    protected boolean handleStartElement(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) throws XNIException {
        if (!this.fSeenRootElement) {
            this.fPerformValidation = this.validate();
            this.fSeenRootElement = true;
            this.fValidationManager.setEntityState(this.fDTDGrammar);
            this.fValidationManager.setGrammarFound(this.fSeenDoctypeDecl);
            this.rootElementSpecified(qName);
        }
        if (this.fDTDGrammar == null) {
            if (!this.fPerformValidation) {
                this.fCurrentElementIndex = -1;
                this.fCurrentContentSpecType = -1;
                this.fInElementContent = false;
            }
            if (this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_GRAMMAR_NOT_FOUND", new Object[]{qName.rawname}, (short)1);
            }
            if (this.fDocumentSource != null) {
                this.fDocumentSource.setDocumentHandler(this.fDocumentHandler);
                if (this.fDocumentHandler != null) {
                    this.fDocumentHandler.setDocumentSource(this.fDocumentSource);
                }
                return true;
            }
        } else {
            this.fCurrentElementIndex = this.fDTDGrammar.getElementDeclIndex(qName);
            this.fCurrentContentSpecType = this.fDTDGrammar.getContentSpecType(this.fCurrentElementIndex);
            if (this.fCurrentContentSpecType == -1 && this.fPerformValidation) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_NOT_DECLARED", new Object[]{qName.rawname}, (short)1);
            }
            this.addDTDDefaultAttrsAndValidate(qName, this.fCurrentElementIndex, xMLAttributes);
        }
        this.fInElementContent = this.fCurrentContentSpecType == 3;
        ++this.fElementDepth;
        if (this.fPerformValidation) {
            Object object;
            if (this.fElementChildrenOffsetStack.length <= this.fElementDepth) {
                object = new int[this.fElementChildrenOffsetStack.length * 2];
                System.arraycopy(this.fElementChildrenOffsetStack, 0, object, 0, this.fElementChildrenOffsetStack.length);
                this.fElementChildrenOffsetStack = object;
            }
            this.fElementChildrenOffsetStack[this.fElementDepth] = this.fElementChildrenLength;
            if (this.fElementChildren.length <= this.fElementChildrenLength) {
                object = new QName[this.fElementChildrenLength * 2];
                System.arraycopy(this.fElementChildren, 0, object, 0, this.fElementChildren.length);
                this.fElementChildren = object;
            }
            if ((object = (Object)this.fElementChildren[this.fElementChildrenLength]) == null) {
                for (int i = this.fElementChildrenLength; i < this.fElementChildren.length; ++i) {
                    this.fElementChildren[i] = new QName();
                }
                object = this.fElementChildren[this.fElementChildrenLength];
            }
            ((QName)object).setValues(qName);
            ++this.fElementChildrenLength;
        }
        this.fCurrentElement.setValues(qName);
        this.ensureStackCapacity(this.fElementDepth);
        this.fElementQNamePartsStack[this.fElementDepth].setValues(this.fCurrentElement);
        this.fElementIndexStack[this.fElementDepth] = this.fCurrentElementIndex;
        this.fContentSpecTypeStack[this.fElementDepth] = this.fCurrentContentSpecType;
        this.startNamespaceScope(qName, xMLAttributes, augmentations);
        return false;
    }

    protected void startNamespaceScope(QName qName, XMLAttributes xMLAttributes, Augmentations augmentations) {
    }

    protected void handleEndElement(QName qName, Augmentations augmentations, boolean bl) throws XNIException {
        --this.fElementDepth;
        if (this.fPerformValidation) {
            int n;
            int n2;
            QName[] qNameArray;
            int n3;
            int n4 = this.fCurrentElementIndex;
            if (n4 != -1 && this.fCurrentContentSpecType != -1 && (n3 = this.checkContent(n4, qNameArray = this.fElementChildren, n2 = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1, n = this.fElementChildrenLength - n2)) != -1) {
                this.fDTDGrammar.getElementDecl(n4, this.fTempElementDecl);
                if (this.fTempElementDecl.type == 1) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_CONTENT_INVALID", new Object[]{qName.rawname, "EMPTY"}, (short)1);
                } else {
                    String string = n3 != n ? "MSG_CONTENT_INVALID" : "MSG_CONTENT_INCOMPLETE";
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", string, new Object[]{qName.rawname, this.fDTDGrammar.getContentSpecAsString(n4)}, (short)1);
                }
            }
            this.fElementChildrenLength = this.fElementChildrenOffsetStack[this.fElementDepth + 1] + 1;
        }
        this.endNamespaceScope(this.fCurrentElement, augmentations, bl);
        if (this.fElementDepth < -1) {
            throw new RuntimeException("FWK008 Element stack underflow");
        }
        if (this.fElementDepth < 0) {
            Iterator iterator;
            this.fCurrentElement.clear();
            this.fCurrentElementIndex = -1;
            this.fCurrentContentSpecType = -1;
            this.fInElementContent = false;
            if (this.fPerformValidation && (iterator = this.fValidationState.checkIDRefID()) != null) {
                while (iterator.hasNext()) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_WITH_ID_REQUIRED", new Object[]{iterator.next()}, (short)1);
                }
            }
            return;
        }
        this.fCurrentElement.setValues(this.fElementQNamePartsStack[this.fElementDepth]);
        this.fCurrentElementIndex = this.fElementIndexStack[this.fElementDepth];
        this.fCurrentContentSpecType = this.fContentSpecTypeStack[this.fElementDepth];
        this.fInElementContent = this.fCurrentContentSpecType == 3;
    }

    protected void endNamespaceScope(QName qName, Augmentations augmentations, boolean bl) {
        if (this.fDocumentHandler != null && !bl) {
            this.fDocumentHandler.endElement(this.fCurrentElement, augmentations);
        }
    }

    protected boolean isSpace(int n) {
        return XMLChar.isSpace(n);
    }

    @Override
    public boolean characterData(String string, Augmentations augmentations) {
        this.characters(new XMLString(string.toCharArray(), 0, string.length()), augmentations);
        return true;
    }
}

