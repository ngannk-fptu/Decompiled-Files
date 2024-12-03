/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Locale;
import java.util.Stack;
import java.util.Vector;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import org.apache.xerces.impl.XMLEntityManager;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dv.SchemaDVFactory;
import org.apache.xerces.impl.dv.xs.XSSimpleTypeDecl;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XMLSchemaException;
import org.apache.xerces.impl.xs.XMLSchemaLoader;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSAttributeGroupDecl;
import org.apache.xerces.impl.xs.XSComplexTypeDecl;
import org.apache.xerces.impl.xs.XSDDescription;
import org.apache.xerces.impl.xs.XSDeclarationPool;
import org.apache.xerces.impl.xs.XSElementDecl;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.impl.xs.XSGroupDecl;
import org.apache.xerces.impl.xs.XSModelGroupImpl;
import org.apache.xerces.impl.xs.XSNotationDecl;
import org.apache.xerces.impl.xs.XSParticleDecl;
import org.apache.xerces.impl.xs.identity.IdentityConstraint;
import org.apache.xerces.impl.xs.opti.ElementImpl;
import org.apache.xerces.impl.xs.opti.SchemaDOM;
import org.apache.xerces.impl.xs.opti.SchemaDOMParser;
import org.apache.xerces.impl.xs.opti.SchemaParsingConfig;
import org.apache.xerces.impl.xs.traversers.SchemaContentHandler;
import org.apache.xerces.impl.xs.traversers.StAXSchemaParser;
import org.apache.xerces.impl.xs.traversers.XSAnnotationInfo;
import org.apache.xerces.impl.xs.traversers.XSAttributeChecker;
import org.apache.xerces.impl.xs.traversers.XSDAttributeGroupTraverser;
import org.apache.xerces.impl.xs.traversers.XSDAttributeTraverser;
import org.apache.xerces.impl.xs.traversers.XSDComplexTypeTraverser;
import org.apache.xerces.impl.xs.traversers.XSDElementTraverser;
import org.apache.xerces.impl.xs.traversers.XSDGroupTraverser;
import org.apache.xerces.impl.xs.traversers.XSDKeyrefTraverser;
import org.apache.xerces.impl.xs.traversers.XSDNotationTraverser;
import org.apache.xerces.impl.xs.traversers.XSDSimpleTypeTraverser;
import org.apache.xerces.impl.xs.traversers.XSDUniqueOrKeyTraverser;
import org.apache.xerces.impl.xs.traversers.XSDWildcardTraverser;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.SimpleLocator;
import org.apache.xerces.impl.xs.util.XSInputSource;
import org.apache.xerces.parsers.SAXParser;
import org.apache.xerces.parsers.XML11Configuration;
import org.apache.xerces.util.DOMInputSource;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.DefaultErrorHandler;
import org.apache.xerces.util.ErrorHandlerWrapper;
import org.apache.xerces.util.SAXInputSource;
import org.apache.xerces.util.StAXInputSource;
import org.apache.xerces.util.StAXLocationWrapper;
import org.apache.xerces.util.SymbolHash;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.URI;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarDescription;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.grammars.XMLSchemaDescription;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLEntityResolver;
import org.apache.xerces.xni.parser.XMLErrorHandler;
import org.apache.xerces.xni.parser.XMLInputSource;
import org.apache.xerces.xni.parser.XMLParseException;
import org.apache.xerces.xs.StringList;
import org.apache.xerces.xs.XSAttributeDeclaration;
import org.apache.xerces.xs.XSAttributeGroupDefinition;
import org.apache.xerces.xs.XSAttributeUse;
import org.apache.xerces.xs.XSElementDeclaration;
import org.apache.xerces.xs.XSModelGroup;
import org.apache.xerces.xs.XSModelGroupDefinition;
import org.apache.xerces.xs.XSNamedMap;
import org.apache.xerces.xs.XSObject;
import org.apache.xerces.xs.XSObjectList;
import org.apache.xerces.xs.XSParticle;
import org.apache.xerces.xs.XSSimpleTypeDefinition;
import org.apache.xerces.xs.XSTerm;
import org.apache.xerces.xs.XSTypeDefinition;
import org.apache.xerces.xs.datatypes.ObjectList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class XSDHandler {
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String XMLSCHEMA_VALIDATION = "http://apache.org/xml/features/validation/schema";
    protected static final String ALLOW_JAVA_ENCODINGS = "http://apache.org/xml/features/allow-java-encodings";
    protected static final String CONTINUE_AFTER_FATAL_ERROR = "http://apache.org/xml/features/continue-after-fatal-error";
    protected static final String STANDARD_URI_CONFORMANT_FEATURE = "http://apache.org/xml/features/standard-uri-conformant";
    protected static final String DISALLOW_DOCTYPE = "http://apache.org/xml/features/disallow-doctype-decl";
    protected static final String GENERATE_SYNTHETIC_ANNOTATIONS = "http://apache.org/xml/features/generate-synthetic-annotations";
    protected static final String VALIDATE_ANNOTATIONS = "http://apache.org/xml/features/validate-annotations";
    protected static final String HONOUR_ALL_SCHEMALOCATIONS = "http://apache.org/xml/features/honour-all-schemaLocations";
    protected static final String NAMESPACE_GROWTH = "http://apache.org/xml/features/namespace-growth";
    protected static final String TOLERATE_DUPLICATES = "http://apache.org/xml/features/internal/tolerate-duplicates";
    private static final String NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    protected static final String STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    protected static final String ERROR_HANDLER = "http://apache.org/xml/properties/internal/error-handler";
    protected static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
    public static final String ENTITY_RESOLVER = "http://apache.org/xml/properties/internal/entity-resolver";
    protected static final String ENTITY_MANAGER = "http://apache.org/xml/properties/internal/entity-manager";
    public static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    public static final String XMLGRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    public static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String SECURITY_MANAGER = "http://apache.org/xml/properties/security-manager";
    protected static final String LOCALE = "http://apache.org/xml/properties/locale";
    protected static final boolean DEBUG_NODE_POOL = false;
    static final int ATTRIBUTE_TYPE = 1;
    static final int ATTRIBUTEGROUP_TYPE = 2;
    static final int ELEMENT_TYPE = 3;
    static final int GROUP_TYPE = 4;
    static final int IDENTITYCONSTRAINT_TYPE = 5;
    static final int NOTATION_TYPE = 6;
    static final int TYPEDECL_TYPE = 7;
    public static final String REDEF_IDENTIFIER = "_fn3dktizrknc9pi";
    protected Hashtable fNotationRegistry = new Hashtable();
    protected XSDeclarationPool fDeclPool = null;
    private Hashtable fUnparsedAttributeRegistry = new Hashtable();
    private Hashtable fUnparsedAttributeGroupRegistry = new Hashtable();
    private Hashtable fUnparsedElementRegistry = new Hashtable();
    private Hashtable fUnparsedGroupRegistry = new Hashtable();
    private Hashtable fUnparsedIdentityConstraintRegistry = new Hashtable();
    private Hashtable fUnparsedNotationRegistry = new Hashtable();
    private Hashtable fUnparsedTypeRegistry = new Hashtable();
    private Hashtable fUnparsedAttributeRegistrySub = new Hashtable();
    private Hashtable fUnparsedAttributeGroupRegistrySub = new Hashtable();
    private Hashtable fUnparsedElementRegistrySub = new Hashtable();
    private Hashtable fUnparsedGroupRegistrySub = new Hashtable();
    private Hashtable fUnparsedIdentityConstraintRegistrySub = new Hashtable();
    private Hashtable fUnparsedNotationRegistrySub = new Hashtable();
    private Hashtable fUnparsedTypeRegistrySub = new Hashtable();
    private Hashtable[] fUnparsedRegistriesExt = new Hashtable[]{null, new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable(), new Hashtable()};
    private Hashtable fXSDocumentInfoRegistry = new Hashtable();
    private Hashtable fDependencyMap = new Hashtable();
    private Hashtable fImportMap = new Hashtable();
    private Vector fAllTNSs = new Vector();
    private Hashtable fLocationPairs = null;
    private static final Hashtable EMPTY_TABLE = new Hashtable();
    Hashtable fHiddenNodes = null;
    private Hashtable fTraversed = new Hashtable();
    private Hashtable fDoc2SystemId = new Hashtable();
    private XSDocumentInfo fRoot = null;
    private Hashtable fDoc2XSDocumentMap = new Hashtable();
    private Hashtable fRedefine2XSDMap = new Hashtable();
    private Hashtable fRedefine2NSSupport = new Hashtable();
    private Hashtable fRedefinedRestrictedAttributeGroupRegistry = new Hashtable();
    private Hashtable fRedefinedRestrictedGroupRegistry = new Hashtable();
    private boolean fLastSchemaWasDuplicate;
    private boolean fValidateAnnotations = false;
    private boolean fHonourAllSchemaLocations = false;
    boolean fNamespaceGrowth = false;
    boolean fTolerateDuplicates = false;
    private XMLErrorReporter fErrorReporter;
    private XMLEntityResolver fEntityResolver;
    private XSAttributeChecker fAttributeChecker;
    private SymbolTable fSymbolTable;
    private XSGrammarBucket fGrammarBucket;
    private XSDDescription fSchemaGrammarDescription;
    private XMLGrammarPool fGrammarPool;
    XSDAttributeGroupTraverser fAttributeGroupTraverser;
    XSDAttributeTraverser fAttributeTraverser;
    XSDComplexTypeTraverser fComplexTypeTraverser;
    XSDElementTraverser fElementTraverser;
    XSDGroupTraverser fGroupTraverser;
    XSDKeyrefTraverser fKeyrefTraverser;
    XSDNotationTraverser fNotationTraverser;
    XSDSimpleTypeTraverser fSimpleTypeTraverser;
    XSDUniqueOrKeyTraverser fUniqueOrKeyTraverser;
    XSDWildcardTraverser fWildCardTraverser;
    SchemaDVFactory fDVFactory;
    SchemaDOMParser fSchemaParser;
    SchemaContentHandler fXSContentHandler;
    StAXSchemaParser fStAXSchemaParser;
    XML11Configuration fAnnotationValidator;
    XSAnnotationGrammarPool fGrammarBucketAdapter;
    private static final int INIT_STACK_SIZE = 30;
    private static final int INC_STACK_SIZE = 10;
    private int fLocalElemStackPos = 0;
    private XSParticleDecl[] fParticle = new XSParticleDecl[30];
    private Element[] fLocalElementDecl = new Element[30];
    private XSDocumentInfo[] fLocalElementDecl_schema = new XSDocumentInfo[30];
    private int[] fAllContext = new int[30];
    private XSObject[] fParent = new XSObject[30];
    private String[][] fLocalElemNamespaceContext = new String[30][1];
    private static final int INIT_KEYREF_STACK = 2;
    private static final int INC_KEYREF_STACK_AMOUNT = 2;
    private int fKeyrefStackPos = 0;
    private Element[] fKeyrefs = new Element[2];
    private XSDocumentInfo[] fKeyrefsMapXSDocumentInfo = new XSDocumentInfo[2];
    private XSElementDecl[] fKeyrefElems = new XSElementDecl[2];
    private String[][] fKeyrefNamespaceContext = new String[2][1];
    SymbolHash fGlobalAttrDecls = new SymbolHash(12);
    SymbolHash fGlobalAttrGrpDecls = new SymbolHash(5);
    SymbolHash fGlobalElemDecls = new SymbolHash(25);
    SymbolHash fGlobalGroupDecls = new SymbolHash(5);
    SymbolHash fGlobalNotationDecls = new SymbolHash(1);
    SymbolHash fGlobalIDConstraintDecls = new SymbolHash(3);
    SymbolHash fGlobalTypeDecls = new SymbolHash(25);
    private static final String[][] NS_ERROR_CODES = new String[][]{{"src-include.2.1", "src-include.2.1"}, {"src-redefine.3.1", "src-redefine.3.1"}, {"src-import.3.1", "src-import.3.2"}, null, {"TargetNamespace.1", "TargetNamespace.2"}, {"TargetNamespace.1", "TargetNamespace.2"}, {"TargetNamespace.1", "TargetNamespace.2"}, {"TargetNamespace.1", "TargetNamespace.2"}};
    private static final String[] ELE_ERROR_CODES = new String[]{"src-include.1", "src-redefine.2", "src-import.2", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4", "schema_reference.4"};
    private Vector fReportedTNS = null;
    private static final String[] COMP_TYPE = new String[]{null, "attribute declaration", "attribute group", "element declaration", "group", "identity constraint", "notation", "type definition"};
    private static final String[] CIRCULAR_CODES = new String[]{"Internal-Error", "Internal-Error", "src-attribute_group.3", "e-props-correct.6", "mg-props-correct.2", "Internal-Error", "Internal-Error", "st-props-correct.2"};
    private SimpleLocator xl = new SimpleLocator();

    private String null2EmptyString(String string) {
        return string == null ? XMLSymbols.EMPTY_STRING : string;
    }

    private String emptyString2Null(String string) {
        return string == XMLSymbols.EMPTY_STRING ? null : string;
    }

    private String doc2SystemId(Element element) {
        String string = null;
        if (element.getOwnerDocument() instanceof SchemaDOM) {
            string = ((SchemaDOM)element.getOwnerDocument()).getDocumentURI();
        }
        return string != null ? string : (String)this.fDoc2SystemId.get(element);
    }

    public XSDHandler() {
        this.fHiddenNodes = new Hashtable();
        this.fSchemaParser = new SchemaDOMParser(new SchemaParsingConfig());
    }

    public XSDHandler(XSGrammarBucket xSGrammarBucket) {
        this();
        this.fGrammarBucket = xSGrammarBucket;
        this.fSchemaGrammarDescription = new XSDDescription();
    }

    public SchemaGrammar parseSchema(XMLInputSource xMLInputSource, XSDDescription xSDDescription, Hashtable hashtable) throws IOException {
        Object object;
        Object object2;
        this.fLocationPairs = hashtable;
        this.fSchemaParser.resetNodePool();
        SchemaGrammar schemaGrammar = null;
        String string = null;
        short s = xSDDescription.getContextType();
        if (s != 3) {
            schemaGrammar = this.fHonourAllSchemaLocations && s == 2 && this.isExistingGrammar(xSDDescription, this.fNamespaceGrowth) ? this.fGrammarBucket.getGrammar(xSDDescription.getTargetNamespace()) : this.findGrammar(xSDDescription, this.fNamespaceGrowth);
            if (schemaGrammar != null) {
                if (!this.fNamespaceGrowth) {
                    return schemaGrammar;
                }
                try {
                    if (schemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), false))) {
                        return schemaGrammar;
                    }
                }
                catch (URI.MalformedURIException malformedURIException) {
                    // empty catch block
                }
            }
            if ((string = xSDDescription.getTargetNamespace()) != null) {
                string = this.fSymbolTable.addSymbol(string);
            }
        }
        this.prepareForParse();
        Element element = null;
        element = xMLInputSource instanceof DOMInputSource ? this.getSchemaDocument(string, (DOMInputSource)xMLInputSource, s == 3, s, null) : (xMLInputSource instanceof SAXInputSource ? this.getSchemaDocument(string, (SAXInputSource)xMLInputSource, s == 3, s, null) : (xMLInputSource instanceof StAXInputSource ? this.getSchemaDocument(string, (StAXInputSource)xMLInputSource, s == 3, s, null) : (xMLInputSource instanceof XSInputSource ? this.getSchemaDocument((XSInputSource)xMLInputSource, xSDDescription) : this.getSchemaDocument(string, xMLInputSource, s == 3, s, null))));
        if (element == null) {
            if (xMLInputSource instanceof XSInputSource) {
                XSInputSource xSInputSource = (XSInputSource)xMLInputSource;
                SchemaGrammar[] schemaGrammarArray = xSInputSource.getGrammars();
                if (schemaGrammarArray != null && schemaGrammarArray.length > 0) {
                    schemaGrammar = this.fGrammarBucket.getGrammar(schemaGrammarArray[0].getTargetNamespace());
                } else {
                    XSObject[] xSObjectArray = xSInputSource.getComponents();
                    if (xSObjectArray != null && xSObjectArray.length > 0) {
                        schemaGrammar = this.fGrammarBucket.getGrammar(xSObjectArray[0].getNamespace());
                    }
                }
            }
            return schemaGrammar;
        }
        if (s == 3) {
            object2 = element;
            string = DOMUtil.getAttrValue((Element)object2, SchemaSymbols.ATT_TARGETNAMESPACE);
            if (string != null && string.length() > 0) {
                string = this.fSymbolTable.addSymbol(string);
                xSDDescription.setTargetNamespace(string);
            } else {
                string = null;
            }
            schemaGrammar = this.findGrammar(xSDDescription, this.fNamespaceGrowth);
            String string2 = XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), false);
            if (schemaGrammar != null && (!this.fNamespaceGrowth || string2 != null && schemaGrammar.getDocumentLocations().contains(string2))) {
                return schemaGrammar;
            }
            object = new XSDKey(string2, s, string);
            this.fTraversed.put(object, element);
            if (string2 != null) {
                this.fDoc2SystemId.put(element, string2);
            }
        }
        this.prepareForTraverse();
        this.fRoot = this.constructTrees(element, xMLInputSource.getSystemId(), xSDDescription, schemaGrammar != null);
        if (this.fRoot == null) {
            return null;
        }
        this.buildGlobalNameRegistries();
        object2 = this.fValidateAnnotations ? new ArrayList() : null;
        this.traverseSchemas((ArrayList)object2);
        this.traverseLocalElements();
        this.resolveKeyRefs();
        for (int i = this.fAllTNSs.size() - 1; i >= 0; --i) {
            object = (String)this.fAllTNSs.elementAt(i);
            Vector vector = (Vector)this.fImportMap.get(object);
            SchemaGrammar schemaGrammar2 = this.fGrammarBucket.getGrammar(this.emptyString2Null((String)object));
            if (schemaGrammar2 == null) continue;
            int n = 0;
            for (int j = 0; j < vector.size(); ++j) {
                SchemaGrammar schemaGrammar3 = this.fGrammarBucket.getGrammar((String)vector.elementAt(j));
                if (schemaGrammar3 == null) continue;
                vector.setElementAt(schemaGrammar3, n++);
            }
            vector.setSize(n);
            schemaGrammar2.setImportedGrammars(vector);
        }
        if (this.fValidateAnnotations && ((ArrayList)object2).size() > 0) {
            this.validateAnnotations((ArrayList)object2);
        }
        return this.fGrammarBucket.getGrammar(this.fRoot.fTargetNamespace);
    }

    private void validateAnnotations(ArrayList arrayList) {
        if (this.fAnnotationValidator == null) {
            this.createAnnotationValidator();
        }
        int n = arrayList.size();
        XMLInputSource xMLInputSource = new XMLInputSource(null, null, null);
        this.fGrammarBucketAdapter.refreshGrammars(this.fGrammarBucket);
        for (int i = 0; i < n; i += 2) {
            xMLInputSource.setSystemId((String)arrayList.get(i));
            XSAnnotationInfo xSAnnotationInfo = (XSAnnotationInfo)arrayList.get(i + 1);
            while (xSAnnotationInfo != null) {
                xMLInputSource.setCharacterStream(new StringReader(xSAnnotationInfo.fAnnotation));
                try {
                    this.fAnnotationValidator.parse(xMLInputSource);
                }
                catch (IOException iOException) {
                    // empty catch block
                }
                xSAnnotationInfo = xSAnnotationInfo.next;
            }
        }
    }

    private void createAnnotationValidator() {
        this.fAnnotationValidator = new XML11Configuration();
        this.fGrammarBucketAdapter = new XSAnnotationGrammarPool();
        this.fAnnotationValidator.setFeature(VALIDATION, true);
        this.fAnnotationValidator.setFeature(XMLSCHEMA_VALIDATION, true);
        this.fAnnotationValidator.setProperty(XMLGRAMMAR_POOL, this.fGrammarBucketAdapter);
        XMLErrorHandler xMLErrorHandler = this.fErrorReporter.getErrorHandler();
        this.fAnnotationValidator.setProperty(ERROR_HANDLER, xMLErrorHandler != null ? xMLErrorHandler : new DefaultErrorHandler());
        Locale locale = this.fErrorReporter.getLocale();
        this.fAnnotationValidator.setProperty(LOCALE, locale);
    }

    SchemaGrammar getGrammar(String string) {
        return this.fGrammarBucket.getGrammar(string);
    }

    protected SchemaGrammar findGrammar(XSDDescription xSDDescription, boolean bl) {
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(xSDDescription.getTargetNamespace());
        if (schemaGrammar == null && this.fGrammarPool != null && (schemaGrammar = (SchemaGrammar)this.fGrammarPool.retrieveGrammar(xSDDescription)) != null && !this.fGrammarBucket.putGrammar(schemaGrammar, true, bl)) {
            this.reportSchemaWarning("GrammarConflict", null, null);
            schemaGrammar = null;
        }
        return schemaGrammar;
    }

    protected XSDocumentInfo constructTrees(Element element, String string, XSDDescription xSDDescription, boolean bl) {
        Object object;
        if (element == null) {
            return null;
        }
        String string2 = xSDDescription.getTargetNamespace();
        short s = xSDDescription.getContextType();
        XSDocumentInfo xSDocumentInfo = null;
        try {
            xSDocumentInfo = new XSDocumentInfo(element, this.fAttributeChecker, this.fSymbolTable);
        }
        catch (XMLSchemaException xMLSchemaException) {
            this.reportSchemaError(ELE_ERROR_CODES[s], new Object[]{string}, element);
            return null;
        }
        if (xSDocumentInfo.fTargetNamespace != null && xSDocumentInfo.fTargetNamespace.length() == 0) {
            this.reportSchemaWarning("EmptyTargetNamespace", new Object[]{string}, element);
            xSDocumentInfo.fTargetNamespace = null;
        }
        if (string2 != null) {
            int n = 0;
            if (s == 0 || s == 1) {
                if (xSDocumentInfo.fTargetNamespace == null) {
                    xSDocumentInfo.fTargetNamespace = string2;
                    xSDocumentInfo.fIsChameleonSchema = true;
                } else if (string2 != xSDocumentInfo.fTargetNamespace) {
                    this.reportSchemaError(NS_ERROR_CODES[s][n], new Object[]{string2, xSDocumentInfo.fTargetNamespace}, element);
                    return null;
                }
            } else if (s != 3 && string2 != xSDocumentInfo.fTargetNamespace) {
                this.reportSchemaError(NS_ERROR_CODES[s][n], new Object[]{string2, xSDocumentInfo.fTargetNamespace}, element);
                return null;
            }
        } else if (xSDocumentInfo.fTargetNamespace != null) {
            if (s == 3) {
                xSDDescription.setTargetNamespace(xSDocumentInfo.fTargetNamespace);
                string2 = xSDocumentInfo.fTargetNamespace;
            } else {
                int n = 1;
                this.reportSchemaError(NS_ERROR_CODES[s][n], new Object[]{string2, xSDocumentInfo.fTargetNamespace}, element);
                return null;
            }
        }
        xSDocumentInfo.addAllowedNS(xSDocumentInfo.fTargetNamespace);
        SchemaGrammar schemaGrammar = null;
        if (bl) {
            object = this.fGrammarBucket.getGrammar(xSDocumentInfo.fTargetNamespace);
            if (((SchemaGrammar)object).isImmutable()) {
                schemaGrammar = new SchemaGrammar((SchemaGrammar)object);
                this.fGrammarBucket.putGrammar(schemaGrammar);
                this.updateImportListWith(schemaGrammar);
            } else {
                schemaGrammar = object;
            }
            this.updateImportListFor(schemaGrammar);
        } else if (s == 0 || s == 1) {
            schemaGrammar = this.fGrammarBucket.getGrammar(xSDocumentInfo.fTargetNamespace);
        } else if (this.fHonourAllSchemaLocations && s == 2) {
            schemaGrammar = this.findGrammar(xSDDescription, false);
            if (schemaGrammar == null) {
                schemaGrammar = new SchemaGrammar(xSDocumentInfo.fTargetNamespace, xSDDescription.makeClone(), this.fSymbolTable);
                this.fGrammarBucket.putGrammar(schemaGrammar);
            }
        } else {
            schemaGrammar = new SchemaGrammar(xSDocumentInfo.fTargetNamespace, xSDDescription.makeClone(), this.fSymbolTable);
            this.fGrammarBucket.putGrammar(schemaGrammar);
        }
        schemaGrammar.addDocument(null, (String)this.fDoc2SystemId.get(xSDocumentInfo.fSchemaElement));
        this.fDoc2XSDocumentMap.put(element, xSDocumentInfo);
        object = new Vector();
        Element element2 = element;
        Element element3 = null;
        Element element4 = DOMUtil.getFirstChildElement(element2);
        while (element4 != null) {
            block56: {
                Object object2;
                boolean bl2;
                String string3;
                String string4;
                block63: {
                    Object object3;
                    Object object4;
                    short s2;
                    String string5;
                    block66: {
                        Element element5;
                        block64: {
                            String string6;
                            block65: {
                                block57: {
                                    block61: {
                                        block62: {
                                            String string7;
                                            block60: {
                                                block59: {
                                                    block58: {
                                                        string5 = null;
                                                        string4 = null;
                                                        string3 = DOMUtil.getLocalName(element4);
                                                        s2 = -1;
                                                        bl2 = false;
                                                        if (string3.equals(SchemaSymbols.ELT_ANNOTATION)) break block56;
                                                        if (!string3.equals(SchemaSymbols.ELT_IMPORT)) break block57;
                                                        s2 = 2;
                                                        object2 = this.fAttributeChecker.checkAttributes(element4, true, xSDocumentInfo);
                                                        string4 = (String)object2[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
                                                        string5 = (String)object2[XSAttributeChecker.ATTIDX_NAMESPACE];
                                                        if (string5 != null) {
                                                            string5 = this.fSymbolTable.addSymbol(string5);
                                                        }
                                                        if ((element5 = DOMUtil.getFirstChildElement(element4)) != null) {
                                                            string7 = DOMUtil.getLocalName(element5);
                                                            if (string7.equals(SchemaSymbols.ELT_ANNOTATION)) {
                                                                schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(element5, (Object[])object2, true, xSDocumentInfo));
                                                            } else {
                                                                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{string3, "annotation?", string7}, element4);
                                                            }
                                                            if (DOMUtil.getNextSiblingElement(element5) != null) {
                                                                this.reportSchemaError("s4s-elt-must-match.1", new Object[]{string3, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(element5))}, element4);
                                                            }
                                                        } else {
                                                            string7 = DOMUtil.getSyntheticAnnotation(element4);
                                                            if (string7 != null) {
                                                                schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element4, string7, (Object[])object2, true, xSDocumentInfo));
                                                            }
                                                        }
                                                        this.fAttributeChecker.returnAttrArray((Object[])object2, xSDocumentInfo);
                                                        if (string5 != xSDocumentInfo.fTargetNamespace) break block58;
                                                        this.reportSchemaError(string5 != null ? "src-import.1.1" : "src-import.1.2", new Object[]{string5}, element4);
                                                        break block56;
                                                    }
                                                    if (!xSDocumentInfo.isAllowedNS(string5)) break block59;
                                                    if (this.fHonourAllSchemaLocations || this.fNamespaceGrowth) break block60;
                                                    break block56;
                                                }
                                                xSDocumentInfo.addAllowedNS(string5);
                                            }
                                            if ((object4 = (Vector)this.fImportMap.get(string7 = this.null2EmptyString(xSDocumentInfo.fTargetNamespace))) == null) {
                                                this.fAllTNSs.addElement(string7);
                                                object4 = new Vector<String>();
                                                this.fImportMap.put(string7, object4);
                                                ((Vector)object4).addElement(string5);
                                            } else if (!((Vector)object4).contains(string5)) {
                                                ((Vector)object4).addElement(string5);
                                            }
                                            this.fSchemaGrammarDescription.reset();
                                            this.fSchemaGrammarDescription.setContextType((short)2);
                                            this.fSchemaGrammarDescription.setBaseSystemId(this.doc2SystemId(element));
                                            this.fSchemaGrammarDescription.setLiteralSystemId(string4);
                                            this.fSchemaGrammarDescription.setLocationHints(new String[]{string4});
                                            this.fSchemaGrammarDescription.setTargetNamespace(string5);
                                            object3 = this.findGrammar(this.fSchemaGrammarDescription, this.fNamespaceGrowth);
                                            if (object3 == null) break block61;
                                            if (!this.fNamespaceGrowth) break block62;
                                            try {
                                                if (!((SchemaGrammar)object3).getDocumentLocations().contains(XMLEntityManager.expandSystemId(string4, this.fSchemaGrammarDescription.getBaseSystemId(), false))) {
                                                    bl2 = true;
                                                }
                                                break block56;
                                            }
                                            catch (URI.MalformedURIException malformedURIException) {}
                                            break block61;
                                        }
                                        if (!this.fHonourAllSchemaLocations || this.isExistingGrammar(this.fSchemaGrammarDescription, false)) break block56;
                                    }
                                    element3 = this.resolveSchema(this.fSchemaGrammarDescription, false, element4, object3 == null);
                                    break block63;
                                }
                                if (!string3.equals(SchemaSymbols.ELT_INCLUDE) && !string3.equals(SchemaSymbols.ELT_REDEFINE)) break;
                                object2 = this.fAttributeChecker.checkAttributes(element4, true, xSDocumentInfo);
                                string4 = (String)object2[XSAttributeChecker.ATTIDX_SCHEMALOCATION];
                                if (string3.equals(SchemaSymbols.ELT_REDEFINE)) {
                                    this.fRedefine2NSSupport.put(element4, new SchemaNamespaceSupport(xSDocumentInfo.fNamespaceSupport));
                                }
                                if (!string3.equals(SchemaSymbols.ELT_INCLUDE)) break block64;
                                element5 = DOMUtil.getFirstChildElement(element4);
                                if (element5 == null) break block65;
                                string6 = DOMUtil.getLocalName(element5);
                                if (string6.equals(SchemaSymbols.ELT_ANNOTATION)) {
                                    schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(element5, (Object[])object2, true, xSDocumentInfo));
                                } else {
                                    this.reportSchemaError("s4s-elt-must-match.1", new Object[]{string3, "annotation?", string6}, element4);
                                }
                                if (DOMUtil.getNextSiblingElement(element5) != null) {
                                    this.reportSchemaError("s4s-elt-must-match.1", new Object[]{string3, "annotation?", DOMUtil.getLocalName(DOMUtil.getNextSiblingElement(element5))}, element4);
                                }
                                break block66;
                            }
                            string6 = DOMUtil.getSyntheticAnnotation(element4);
                            if (string6 == null) break block66;
                            schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element4, string6, (Object[])object2, true, xSDocumentInfo));
                            break block66;
                        }
                        element5 = DOMUtil.getFirstChildElement(element4);
                        while (element5 != null) {
                            String string8 = DOMUtil.getLocalName(element5);
                            if (string8.equals(SchemaSymbols.ELT_ANNOTATION)) {
                                schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl(element5, (Object[])object2, true, xSDocumentInfo));
                                DOMUtil.setHidden(element5, this.fHiddenNodes);
                            } else {
                                object4 = DOMUtil.getSyntheticAnnotation(element4);
                                if (object4 != null) {
                                    schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element4, (String)object4, (Object[])object2, true, xSDocumentInfo));
                                }
                            }
                            element5 = DOMUtil.getNextSiblingElement(element5);
                        }
                    }
                    this.fAttributeChecker.returnAttrArray((Object[])object2, xSDocumentInfo);
                    if (string4 == null) {
                        this.reportSchemaError("s4s-att-must-appear", new Object[]{"<include> or <redefine>", "schemaLocation"}, element4);
                    }
                    boolean bl3 = false;
                    s2 = 0;
                    if (string3.equals(SchemaSymbols.ELT_REDEFINE)) {
                        bl3 = this.nonAnnotationContent(element4);
                        s2 = 1;
                    }
                    this.fSchemaGrammarDescription.reset();
                    this.fSchemaGrammarDescription.setContextType(s2);
                    this.fSchemaGrammarDescription.setBaseSystemId(this.doc2SystemId(element));
                    this.fSchemaGrammarDescription.setLocationHints(new String[]{string4});
                    this.fSchemaGrammarDescription.setTargetNamespace(string2);
                    boolean bl4 = false;
                    object4 = this.resolveSchemaSource(this.fSchemaGrammarDescription, bl3, element4, true);
                    if (this.fNamespaceGrowth && s2 == 0) {
                        try {
                            object3 = XMLEntityManager.expandSystemId(((XMLInputSource)object4).getSystemId(), ((XMLInputSource)object4).getBaseSystemId(), false);
                            bl4 = schemaGrammar.getDocumentLocations().contains((String)object3);
                        }
                        catch (URI.MalformedURIException malformedURIException) {
                            // empty catch block
                        }
                    }
                    if (!bl4) {
                        element3 = this.resolveSchema((XMLInputSource)object4, this.fSchemaGrammarDescription, bl3, element4);
                        string5 = xSDocumentInfo.fTargetNamespace;
                    } else {
                        this.fLastSchemaWasDuplicate = true;
                    }
                }
                object2 = null;
                object2 = this.fLastSchemaWasDuplicate ? (element3 == null ? null : (XSDocumentInfo)this.fDoc2XSDocumentMap.get(element3)) : this.constructTrees(element3, string4, this.fSchemaGrammarDescription, bl2);
                if (string3.equals(SchemaSymbols.ELT_REDEFINE) && object2 != null) {
                    this.fRedefine2XSDMap.put(element4, object2);
                }
                if (element3 != null) {
                    if (object2 != null) {
                        ((Vector)object).addElement(object2);
                    }
                    element3 = null;
                }
            }
            element4 = DOMUtil.getNextSiblingElement(element4);
        }
        this.fDependencyMap.put(xSDocumentInfo, object);
        return xSDocumentInfo;
    }

    private boolean isExistingGrammar(XSDDescription xSDDescription, boolean bl) {
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(xSDDescription.getTargetNamespace());
        if (schemaGrammar == null) {
            return this.findGrammar(xSDDescription, bl) != null;
        }
        if (schemaGrammar.isImmutable()) {
            return true;
        }
        try {
            return schemaGrammar.getDocumentLocations().contains(XMLEntityManager.expandSystemId(xSDDescription.getLiteralSystemId(), xSDDescription.getBaseSystemId(), false));
        }
        catch (URI.MalformedURIException malformedURIException) {
            return false;
        }
    }

    private void updateImportListFor(SchemaGrammar schemaGrammar) {
        Vector vector = schemaGrammar.getImportedGrammars();
        if (vector != null) {
            for (int i = 0; i < vector.size(); ++i) {
                SchemaGrammar schemaGrammar2 = (SchemaGrammar)vector.elementAt(i);
                SchemaGrammar schemaGrammar3 = this.fGrammarBucket.getGrammar(schemaGrammar2.getTargetNamespace());
                if (schemaGrammar3 == null || schemaGrammar2 == schemaGrammar3) continue;
                vector.set(i, schemaGrammar3);
            }
        }
    }

    private void updateImportListWith(SchemaGrammar schemaGrammar) {
        SchemaGrammar[] schemaGrammarArray = this.fGrammarBucket.getGrammars();
        block0: for (int i = 0; i < schemaGrammarArray.length; ++i) {
            Vector vector;
            SchemaGrammar schemaGrammar2 = schemaGrammarArray[i];
            if (schemaGrammar2 == schemaGrammar || (vector = schemaGrammar2.getImportedGrammars()) == null) continue;
            for (int j = 0; j < vector.size(); ++j) {
                SchemaGrammar schemaGrammar3 = (SchemaGrammar)vector.elementAt(j);
                if (!this.null2EmptyString(schemaGrammar3.getTargetNamespace()).equals(this.null2EmptyString(schemaGrammar.getTargetNamespace()))) continue;
                if (schemaGrammar3 == schemaGrammar) continue block0;
                vector.set(j, schemaGrammar);
                continue block0;
            }
        }
    }

    protected void buildGlobalNameRegistries() {
        Stack<XSDocumentInfo> stack = new Stack<XSDocumentInfo>();
        stack.push(this.fRoot);
        while (!stack.empty()) {
            XSDocumentInfo xSDocumentInfo = (XSDocumentInfo)stack.pop();
            Element element = xSDocumentInfo.fSchemaElement;
            if (DOMUtil.isHidden(element, this.fHiddenNodes)) continue;
            Element element2 = element;
            boolean bl = true;
            Object object = DOMUtil.getFirstChildElement(element2);
            while (object != null) {
                if (!DOMUtil.getLocalName((Node)object).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    String string;
                    String string2;
                    Object object2;
                    if (DOMUtil.getLocalName((Node)object).equals(SchemaSymbols.ELT_INCLUDE) || DOMUtil.getLocalName((Node)object).equals(SchemaSymbols.ELT_IMPORT)) {
                        if (!bl) {
                            this.reportSchemaError("s4s-elt-invalid-content.3", new Object[]{DOMUtil.getLocalName((Node)object)}, (Element)object);
                        }
                        DOMUtil.setHidden((Node)object, this.fHiddenNodes);
                    } else if (DOMUtil.getLocalName((Node)object).equals(SchemaSymbols.ELT_REDEFINE)) {
                        if (!bl) {
                            this.reportSchemaError("s4s-elt-invalid-content.3", new Object[]{DOMUtil.getLocalName((Node)object)}, (Element)object);
                        }
                        object2 = DOMUtil.getFirstChildElement((Node)object);
                        while (object2 != null) {
                            string2 = DOMUtil.getAttrValue((Element)object2, SchemaSymbols.ATT_NAME);
                            if (string2.length() != 0) {
                                String string3;
                                string = xSDocumentInfo.fTargetNamespace == null ? "," + string2 : xSDocumentInfo.fTargetNamespace + "," + string2;
                                string = XMLChar.trim(string);
                                String string4 = DOMUtil.getLocalName((Node)object2);
                                if (string4.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                                    this.checkForDuplicateNames(string, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, (Element)object2, xSDocumentInfo);
                                    string3 = DOMUtil.getAttrValue((Element)object2, SchemaSymbols.ATT_NAME) + REDEF_IDENTIFIER;
                                    this.renameRedefiningComponents(xSDocumentInfo, (Element)object2, SchemaSymbols.ELT_ATTRIBUTEGROUP, string2, string3);
                                } else if (string4.equals(SchemaSymbols.ELT_COMPLEXTYPE) || string4.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                                    this.checkForDuplicateNames(string, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, (Element)object2, xSDocumentInfo);
                                    string3 = DOMUtil.getAttrValue((Element)object2, SchemaSymbols.ATT_NAME) + REDEF_IDENTIFIER;
                                    if (string4.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                                        this.renameRedefiningComponents(xSDocumentInfo, (Element)object2, SchemaSymbols.ELT_COMPLEXTYPE, string2, string3);
                                    } else {
                                        this.renameRedefiningComponents(xSDocumentInfo, (Element)object2, SchemaSymbols.ELT_SIMPLETYPE, string2, string3);
                                    }
                                } else if (string4.equals(SchemaSymbols.ELT_GROUP)) {
                                    this.checkForDuplicateNames(string, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, (Element)object2, xSDocumentInfo);
                                    string3 = DOMUtil.getAttrValue((Element)object2, SchemaSymbols.ATT_NAME) + REDEF_IDENTIFIER;
                                    this.renameRedefiningComponents(xSDocumentInfo, (Element)object2, SchemaSymbols.ELT_GROUP, string2, string3);
                                }
                            }
                            object2 = DOMUtil.getNextSiblingElement((Node)object2);
                        }
                    } else {
                        bl = false;
                        object2 = DOMUtil.getAttrValue((Element)object, SchemaSymbols.ATT_NAME);
                        if (((String)object2).length() != 0) {
                            string2 = xSDocumentInfo.fTargetNamespace == null ? "," + (String)object2 : xSDocumentInfo.fTargetNamespace + "," + (String)object2;
                            string2 = XMLChar.trim(string2);
                            string = DOMUtil.getLocalName((Node)object);
                            if (string.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                                this.checkForDuplicateNames(string2, 1, this.fUnparsedAttributeRegistry, this.fUnparsedAttributeRegistrySub, (Element)object, xSDocumentInfo);
                            } else if (string.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                                this.checkForDuplicateNames(string2, 2, this.fUnparsedAttributeGroupRegistry, this.fUnparsedAttributeGroupRegistrySub, (Element)object, xSDocumentInfo);
                            } else if (string.equals(SchemaSymbols.ELT_COMPLEXTYPE) || string.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                                this.checkForDuplicateNames(string2, 7, this.fUnparsedTypeRegistry, this.fUnparsedTypeRegistrySub, (Element)object, xSDocumentInfo);
                            } else if (string.equals(SchemaSymbols.ELT_ELEMENT)) {
                                this.checkForDuplicateNames(string2, 3, this.fUnparsedElementRegistry, this.fUnparsedElementRegistrySub, (Element)object, xSDocumentInfo);
                            } else if (string.equals(SchemaSymbols.ELT_GROUP)) {
                                this.checkForDuplicateNames(string2, 4, this.fUnparsedGroupRegistry, this.fUnparsedGroupRegistrySub, (Element)object, xSDocumentInfo);
                            } else if (string.equals(SchemaSymbols.ELT_NOTATION)) {
                                this.checkForDuplicateNames(string2, 6, this.fUnparsedNotationRegistry, this.fUnparsedNotationRegistrySub, (Element)object, xSDocumentInfo);
                            }
                        }
                    }
                }
                object = DOMUtil.getNextSiblingElement((Node)object);
            }
            DOMUtil.setHidden(element, this.fHiddenNodes);
            object = (Vector)this.fDependencyMap.get(xSDocumentInfo);
            for (int i = 0; i < ((Vector)object).size(); ++i) {
                stack.push((XSDocumentInfo)((Vector)object).elementAt(i));
            }
        }
    }

    protected void traverseSchemas(ArrayList arrayList) {
        this.setSchemasVisible(this.fRoot);
        Stack<XSDocumentInfo> stack = new Stack<XSDocumentInfo>();
        stack.push(this.fRoot);
        while (!stack.empty()) {
            XSDocumentInfo xSDocumentInfo = (XSDocumentInfo)stack.pop();
            Element element = xSDocumentInfo.fSchemaElement;
            SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(xSDocumentInfo.fTargetNamespace);
            if (DOMUtil.isHidden(element, this.fHiddenNodes)) continue;
            Element element2 = element;
            boolean bl = false;
            Object object = DOMUtil.getFirstVisibleChildElement(element2, this.fHiddenNodes);
            while (object != null) {
                DOMUtil.setHidden((Node)object, this.fHiddenNodes);
                String string = DOMUtil.getLocalName((Node)object);
                if (DOMUtil.getLocalName((Node)object).equals(SchemaSymbols.ELT_REDEFINE)) {
                    xSDocumentInfo.backupNSSupport((SchemaNamespaceSupport)this.fRedefine2NSSupport.get(object));
                    Element element3 = DOMUtil.getFirstVisibleChildElement((Node)object, this.fHiddenNodes);
                    while (element3 != null) {
                        String string2 = DOMUtil.getLocalName(element3);
                        DOMUtil.setHidden(element3, this.fHiddenNodes);
                        if (string2.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                            this.fAttributeGroupTraverser.traverseGlobal(element3, xSDocumentInfo, schemaGrammar);
                        } else if (string2.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                            this.fComplexTypeTraverser.traverseGlobal(element3, xSDocumentInfo, schemaGrammar);
                        } else if (string2.equals(SchemaSymbols.ELT_GROUP)) {
                            this.fGroupTraverser.traverseGlobal(element3, xSDocumentInfo, schemaGrammar);
                        } else if (string2.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                            this.fSimpleTypeTraverser.traverseGlobal(element3, xSDocumentInfo, schemaGrammar);
                        } else {
                            this.reportSchemaError("s4s-elt-must-match.1", new Object[]{DOMUtil.getLocalName((Node)object), "(annotation | (simpleType | complexType | group | attributeGroup))*", string2}, element3);
                        }
                        element3 = DOMUtil.getNextVisibleSiblingElement(element3, this.fHiddenNodes);
                    }
                    xSDocumentInfo.restoreNSSupport();
                } else if (string.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                    this.fAttributeTraverser.traverseGlobal((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
                    this.fAttributeGroupTraverser.traverseGlobal((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                    this.fComplexTypeTraverser.traverseGlobal((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_ELEMENT)) {
                    this.fElementTraverser.traverseGlobal((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_GROUP)) {
                    this.fGroupTraverser.traverseGlobal((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_NOTATION)) {
                    this.fNotationTraverser.traverse((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
                    this.fSimpleTypeTraverser.traverseGlobal((Element)object, xSDocumentInfo, schemaGrammar);
                } else if (string.equals(SchemaSymbols.ELT_ANNOTATION)) {
                    schemaGrammar.addAnnotation(this.fElementTraverser.traverseAnnotationDecl((Element)object, xSDocumentInfo.getSchemaAttrs(), true, xSDocumentInfo));
                    bl = true;
                } else {
                    this.reportSchemaError("s4s-elt-invalid-content.1", new Object[]{SchemaSymbols.ELT_SCHEMA, DOMUtil.getLocalName((Node)object)}, (Element)object);
                }
                object = DOMUtil.getNextVisibleSiblingElement((Node)object, this.fHiddenNodes);
            }
            if (!bl && (object = DOMUtil.getSyntheticAnnotation(element2)) != null) {
                schemaGrammar.addAnnotation(this.fElementTraverser.traverseSyntheticAnnotation(element2, (String)object, xSDocumentInfo.getSchemaAttrs(), true, xSDocumentInfo));
            }
            if (arrayList != null && (object = xSDocumentInfo.getAnnotations()) != null) {
                arrayList.add(this.doc2SystemId(element));
                arrayList.add(object);
            }
            xSDocumentInfo.returnSchemaAttrs();
            DOMUtil.setHidden(element, this.fHiddenNodes);
            object = (Vector)this.fDependencyMap.get(xSDocumentInfo);
            for (int i = 0; i < ((Vector)object).size(); ++i) {
                stack.push((XSDocumentInfo)((Vector)object).elementAt(i));
            }
        }
    }

    private final boolean needReportTNSError(String string) {
        if (this.fReportedTNS == null) {
            this.fReportedTNS = new Vector();
        } else if (this.fReportedTNS.contains(string)) {
            return false;
        }
        this.fReportedTNS.addElement(string);
        return true;
    }

    void addGlobalAttributeDecl(XSAttributeDecl xSAttributeDecl) {
        String string;
        String string2 = xSAttributeDecl.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + xSAttributeDecl.getName() : string2 + "," + xSAttributeDecl.getName();
        if (this.fGlobalAttrDecls.get(string) == null) {
            this.fGlobalAttrDecls.put(string, xSAttributeDecl);
        }
    }

    void addGlobalAttributeGroupDecl(XSAttributeGroupDecl xSAttributeGroupDecl) {
        String string;
        String string2 = xSAttributeGroupDecl.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + xSAttributeGroupDecl.getName() : string2 + "," + xSAttributeGroupDecl.getName();
        if (this.fGlobalAttrGrpDecls.get(string) == null) {
            this.fGlobalAttrGrpDecls.put(string, xSAttributeGroupDecl);
        }
    }

    void addGlobalElementDecl(XSElementDecl xSElementDecl) {
        String string;
        String string2 = xSElementDecl.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + xSElementDecl.getName() : string2 + "," + xSElementDecl.getName();
        if (this.fGlobalElemDecls.get(string) == null) {
            this.fGlobalElemDecls.put(string, xSElementDecl);
        }
    }

    void addGlobalGroupDecl(XSGroupDecl xSGroupDecl) {
        String string;
        String string2 = xSGroupDecl.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + xSGroupDecl.getName() : string2 + "," + xSGroupDecl.getName();
        if (this.fGlobalGroupDecls.get(string) == null) {
            this.fGlobalGroupDecls.put(string, xSGroupDecl);
        }
    }

    void addGlobalNotationDecl(XSNotationDecl xSNotationDecl) {
        String string;
        String string2 = xSNotationDecl.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + xSNotationDecl.getName() : string2 + "," + xSNotationDecl.getName();
        if (this.fGlobalNotationDecls.get(string) == null) {
            this.fGlobalNotationDecls.put(string, xSNotationDecl);
        }
    }

    void addGlobalTypeDecl(XSTypeDefinition xSTypeDefinition) {
        String string;
        String string2 = xSTypeDefinition.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + xSTypeDefinition.getName() : string2 + "," + xSTypeDefinition.getName();
        if (this.fGlobalTypeDecls.get(string) == null) {
            this.fGlobalTypeDecls.put(string, xSTypeDefinition);
        }
    }

    void addIDConstraintDecl(IdentityConstraint identityConstraint) {
        String string;
        String string2 = identityConstraint.getNamespace();
        String string3 = string = string2 == null || string2.length() == 0 ? "," + identityConstraint.getIdentityConstraintName() : string2 + "," + identityConstraint.getIdentityConstraintName();
        if (this.fGlobalIDConstraintDecls.get(string) == null) {
            this.fGlobalIDConstraintDecls.put(string, identityConstraint);
        }
    }

    private XSAttributeDecl getGlobalAttributeDecl(String string) {
        return (XSAttributeDecl)this.fGlobalAttrDecls.get(string);
    }

    private XSAttributeGroupDecl getGlobalAttributeGroupDecl(String string) {
        return (XSAttributeGroupDecl)this.fGlobalAttrGrpDecls.get(string);
    }

    private XSElementDecl getGlobalElementDecl(String string) {
        return (XSElementDecl)this.fGlobalElemDecls.get(string);
    }

    private XSGroupDecl getGlobalGroupDecl(String string) {
        return (XSGroupDecl)this.fGlobalGroupDecls.get(string);
    }

    private XSNotationDecl getGlobalNotationDecl(String string) {
        return (XSNotationDecl)this.fGlobalNotationDecls.get(string);
    }

    private XSTypeDefinition getGlobalTypeDecl(String string) {
        return (XSTypeDefinition)this.fGlobalTypeDecls.get(string);
    }

    private IdentityConstraint getIDConstraintDecl(String string) {
        return (IdentityConstraint)this.fGlobalIDConstraintDecls.get(string);
    }

    protected Object getGlobalDecl(XSDocumentInfo xSDocumentInfo, int n, QName qName, Element element) {
        Object object;
        String string;
        Object object2;
        if (qName.uri != null && qName.uri == SchemaSymbols.URI_SCHEMAFORSCHEMA && n == 7 && (object2 = SchemaGrammar.SG_SchemaNS.getGlobalTypeDecl(qName.localpart)) != null) {
            return object2;
        }
        if (!xSDocumentInfo.isAllowedNS(qName.uri) && xSDocumentInfo.needReportTNSError(qName.uri)) {
            object2 = qName.uri == null ? "src-resolve.4.1" : "src-resolve.4.2";
            this.reportSchemaError((String)object2, new Object[]{this.fDoc2SystemId.get(xSDocumentInfo.fSchemaElement), qName.uri, qName.rawname}, element);
        }
        if ((object2 = this.fGrammarBucket.getGrammar(qName.uri)) == null) {
            if (this.needReportTNSError(qName.uri)) {
                this.reportSchemaError("src-resolve", new Object[]{qName.rawname, COMP_TYPE[n]}, element);
            }
            return null;
        }
        Object object3 = this.getGlobalDeclFromGrammar((SchemaGrammar)object2, n, qName.localpart);
        String string2 = string = qName.uri == null ? "," + qName.localpart : qName.uri + "," + qName.localpart;
        if (!this.fTolerateDuplicates) {
            if (object3 != null) {
                return object3;
            }
        } else {
            object = this.getGlobalDecl(string, n);
            if (object != null) {
                return object;
            }
        }
        object = null;
        Element element2 = null;
        XSDocumentInfo xSDocumentInfo2 = null;
        switch (n) {
            case 1: {
                element2 = (Element)this.fUnparsedAttributeRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedAttributeRegistrySub.get(string);
                break;
            }
            case 2: {
                element2 = (Element)this.fUnparsedAttributeGroupRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedAttributeGroupRegistrySub.get(string);
                break;
            }
            case 3: {
                element2 = (Element)this.fUnparsedElementRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedElementRegistrySub.get(string);
                break;
            }
            case 4: {
                element2 = (Element)this.fUnparsedGroupRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedGroupRegistrySub.get(string);
                break;
            }
            case 5: {
                element2 = (Element)this.fUnparsedIdentityConstraintRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedIdentityConstraintRegistrySub.get(string);
                break;
            }
            case 6: {
                element2 = (Element)this.fUnparsedNotationRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedNotationRegistrySub.get(string);
                break;
            }
            case 7: {
                element2 = (Element)this.fUnparsedTypeRegistry.get(string);
                xSDocumentInfo2 = (XSDocumentInfo)this.fUnparsedTypeRegistrySub.get(string);
                break;
            }
            default: {
                this.reportSchemaError("Internal-Error", new Object[]{"XSDHandler asked to locate component of type " + n + "; it does not recognize this type!"}, element);
            }
        }
        if (element2 == null) {
            if (object3 == null) {
                this.reportSchemaError("src-resolve", new Object[]{qName.rawname, COMP_TYPE[n]}, element);
            }
            return object3;
        }
        object = this.findXSDocumentForDecl(xSDocumentInfo, element2, xSDocumentInfo2);
        if (object == null) {
            if (object3 == null) {
                String string3 = qName.uri == null ? "src-resolve.4.1" : "src-resolve.4.2";
                this.reportSchemaError(string3, new Object[]{this.fDoc2SystemId.get(xSDocumentInfo.fSchemaElement), qName.uri, qName.rawname}, element);
            }
            return object3;
        }
        if (DOMUtil.isHidden(element2, this.fHiddenNodes)) {
            if (object3 == null) {
                String string4 = CIRCULAR_CODES[n];
                if (n == 7 && SchemaSymbols.ELT_COMPLEXTYPE.equals(DOMUtil.getLocalName(element2))) {
                    string4 = "ct-props-correct.3";
                }
                this.reportSchemaError(string4, new Object[]{qName.prefix + ":" + qName.localpart}, element);
            }
            return object3;
        }
        return this.traverseGlobalDecl(n, element2, (XSDocumentInfo)object, (SchemaGrammar)object2);
    }

    protected Object getGlobalDecl(String string, int n) {
        XSObject xSObject = null;
        switch (n) {
            case 1: {
                xSObject = this.getGlobalAttributeDecl(string);
                break;
            }
            case 2: {
                xSObject = this.getGlobalAttributeGroupDecl(string);
                break;
            }
            case 3: {
                xSObject = this.getGlobalElementDecl(string);
                break;
            }
            case 4: {
                xSObject = this.getGlobalGroupDecl(string);
                break;
            }
            case 5: {
                xSObject = this.getIDConstraintDecl(string);
                break;
            }
            case 6: {
                xSObject = this.getGlobalNotationDecl(string);
                break;
            }
            case 7: {
                xSObject = this.getGlobalTypeDecl(string);
            }
        }
        return xSObject;
    }

    protected Object getGlobalDeclFromGrammar(SchemaGrammar schemaGrammar, int n, String string) {
        XSObject xSObject = null;
        switch (n) {
            case 1: {
                xSObject = schemaGrammar.getGlobalAttributeDecl(string);
                break;
            }
            case 2: {
                xSObject = schemaGrammar.getGlobalAttributeGroupDecl(string);
                break;
            }
            case 3: {
                xSObject = schemaGrammar.getGlobalElementDecl(string);
                break;
            }
            case 4: {
                xSObject = schemaGrammar.getGlobalGroupDecl(string);
                break;
            }
            case 5: {
                xSObject = schemaGrammar.getIDConstraintDecl(string);
                break;
            }
            case 6: {
                xSObject = schemaGrammar.getGlobalNotationDecl(string);
                break;
            }
            case 7: {
                xSObject = schemaGrammar.getGlobalTypeDecl(string);
            }
        }
        return xSObject;
    }

    protected Object getGlobalDeclFromGrammar(SchemaGrammar schemaGrammar, int n, String string, String string2) {
        XSObject xSObject = null;
        switch (n) {
            case 1: {
                xSObject = schemaGrammar.getGlobalAttributeDecl(string, string2);
                break;
            }
            case 2: {
                xSObject = schemaGrammar.getGlobalAttributeGroupDecl(string, string2);
                break;
            }
            case 3: {
                xSObject = schemaGrammar.getGlobalElementDecl(string, string2);
                break;
            }
            case 4: {
                xSObject = schemaGrammar.getGlobalGroupDecl(string, string2);
                break;
            }
            case 5: {
                xSObject = schemaGrammar.getIDConstraintDecl(string, string2);
                break;
            }
            case 6: {
                xSObject = schemaGrammar.getGlobalNotationDecl(string, string2);
                break;
            }
            case 7: {
                xSObject = schemaGrammar.getGlobalTypeDecl(string, string2);
            }
        }
        return xSObject;
    }

    protected Object traverseGlobalDecl(int n, Element element, XSDocumentInfo xSDocumentInfo, SchemaGrammar schemaGrammar) {
        XSObject xSObject = null;
        DOMUtil.setHidden(element, this.fHiddenNodes);
        SchemaNamespaceSupport schemaNamespaceSupport = null;
        Element element2 = DOMUtil.getParent(element);
        if (DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_REDEFINE)) {
            schemaNamespaceSupport = (SchemaNamespaceSupport)this.fRedefine2NSSupport.get(element2);
        }
        xSDocumentInfo.backupNSSupport(schemaNamespaceSupport);
        switch (n) {
            case 7: {
                if (DOMUtil.getLocalName(element).equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
                    xSObject = this.fComplexTypeTraverser.traverseGlobal(element, xSDocumentInfo, schemaGrammar);
                    break;
                }
                xSObject = this.fSimpleTypeTraverser.traverseGlobal(element, xSDocumentInfo, schemaGrammar);
                break;
            }
            case 1: {
                xSObject = this.fAttributeTraverser.traverseGlobal(element, xSDocumentInfo, schemaGrammar);
                break;
            }
            case 3: {
                xSObject = this.fElementTraverser.traverseGlobal(element, xSDocumentInfo, schemaGrammar);
                break;
            }
            case 2: {
                xSObject = this.fAttributeGroupTraverser.traverseGlobal(element, xSDocumentInfo, schemaGrammar);
                break;
            }
            case 4: {
                xSObject = this.fGroupTraverser.traverseGlobal(element, xSDocumentInfo, schemaGrammar);
                break;
            }
            case 6: {
                xSObject = this.fNotationTraverser.traverse(element, xSDocumentInfo, schemaGrammar);
                break;
            }
        }
        xSDocumentInfo.restoreNSSupport();
        return xSObject;
    }

    public String schemaDocument2SystemId(XSDocumentInfo xSDocumentInfo) {
        return (String)this.fDoc2SystemId.get(xSDocumentInfo.fSchemaElement);
    }

    Object getGrpOrAttrGrpRedefinedByRestriction(int n, QName qName, XSDocumentInfo xSDocumentInfo, Element element) {
        String string = qName.uri != null ? qName.uri + "," + qName.localpart : "," + qName.localpart;
        String string2 = null;
        switch (n) {
            case 2: {
                string2 = (String)this.fRedefinedRestrictedAttributeGroupRegistry.get(string);
                break;
            }
            case 4: {
                string2 = (String)this.fRedefinedRestrictedGroupRegistry.get(string);
                break;
            }
            default: {
                return null;
            }
        }
        if (string2 == null) {
            return null;
        }
        int n2 = string2.indexOf(",");
        QName qName2 = new QName(XMLSymbols.EMPTY_STRING, string2.substring(n2 + 1), string2.substring(n2), n2 == 0 ? null : string2.substring(0, n2));
        Object object = this.getGlobalDecl(xSDocumentInfo, n, qName2, element);
        if (object == null) {
            switch (n) {
                case 2: {
                    this.reportSchemaError("src-redefine.7.2.1", new Object[]{qName.localpart}, element);
                    break;
                }
                case 4: {
                    this.reportSchemaError("src-redefine.6.2.1", new Object[]{qName.localpart}, element);
                }
            }
            return null;
        }
        return object;
    }

    protected void resolveKeyRefs() {
        for (int i = 0; i < this.fKeyrefStackPos; ++i) {
            XSDocumentInfo xSDocumentInfo = this.fKeyrefsMapXSDocumentInfo[i];
            xSDocumentInfo.fNamespaceSupport.makeGlobal();
            xSDocumentInfo.fNamespaceSupport.setEffectiveContext(this.fKeyrefNamespaceContext[i]);
            SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(xSDocumentInfo.fTargetNamespace);
            DOMUtil.setHidden(this.fKeyrefs[i], this.fHiddenNodes);
            this.fKeyrefTraverser.traverse(this.fKeyrefs[i], this.fKeyrefElems[i], xSDocumentInfo, schemaGrammar);
        }
    }

    protected Hashtable getIDRegistry() {
        return this.fUnparsedIdentityConstraintRegistry;
    }

    protected Hashtable getIDRegistry_sub() {
        return this.fUnparsedIdentityConstraintRegistrySub;
    }

    protected void storeKeyRef(Element element, XSDocumentInfo xSDocumentInfo, XSElementDecl xSElementDecl) {
        Object object;
        String string = DOMUtil.getAttrValue(element, SchemaSymbols.ATT_NAME);
        if (string.length() != 0) {
            object = xSDocumentInfo.fTargetNamespace == null ? "," + string : xSDocumentInfo.fTargetNamespace + "," + string;
            this.checkForDuplicateNames((String)object, 5, this.fUnparsedIdentityConstraintRegistry, this.fUnparsedIdentityConstraintRegistrySub, element, xSDocumentInfo);
        }
        if (this.fKeyrefStackPos == this.fKeyrefs.length) {
            object = new Element[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefs, 0, object, 0, this.fKeyrefStackPos);
            this.fKeyrefs = object;
            XSElementDecl[] xSElementDeclArray = new XSElementDecl[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefElems, 0, xSElementDeclArray, 0, this.fKeyrefStackPos);
            this.fKeyrefElems = xSElementDeclArray;
            String[][] stringArrayArray = new String[this.fKeyrefStackPos + 2][];
            System.arraycopy(this.fKeyrefNamespaceContext, 0, stringArrayArray, 0, this.fKeyrefStackPos);
            this.fKeyrefNamespaceContext = stringArrayArray;
            XSDocumentInfo[] xSDocumentInfoArray = new XSDocumentInfo[this.fKeyrefStackPos + 2];
            System.arraycopy(this.fKeyrefsMapXSDocumentInfo, 0, xSDocumentInfoArray, 0, this.fKeyrefStackPos);
            this.fKeyrefsMapXSDocumentInfo = xSDocumentInfoArray;
        }
        this.fKeyrefs[this.fKeyrefStackPos] = element;
        this.fKeyrefElems[this.fKeyrefStackPos] = xSElementDecl;
        this.fKeyrefNamespaceContext[this.fKeyrefStackPos] = xSDocumentInfo.fNamespaceSupport.getEffectiveLocalContext();
        this.fKeyrefsMapXSDocumentInfo[this.fKeyrefStackPos++] = xSDocumentInfo;
    }

    private Element resolveSchema(XSDDescription xSDDescription, boolean bl, Element element, boolean bl2) {
        XMLInputSource xMLInputSource = null;
        try {
            Hashtable hashtable = bl2 ? this.fLocationPairs : EMPTY_TABLE;
            xMLInputSource = XMLSchemaLoader.resolveDocument(xSDDescription, hashtable, this.fEntityResolver);
        }
        catch (IOException iOException) {
            if (bl) {
                this.reportSchemaError("schema_reference.4", new Object[]{xSDDescription.getLocationHints()[0]}, element);
            }
            this.reportSchemaWarning("schema_reference.4", new Object[]{xSDDescription.getLocationHints()[0]}, element);
        }
        if (xMLInputSource instanceof DOMInputSource) {
            return this.getSchemaDocument(xSDDescription.getTargetNamespace(), (DOMInputSource)xMLInputSource, bl, xSDDescription.getContextType(), element);
        }
        if (xMLInputSource instanceof SAXInputSource) {
            return this.getSchemaDocument(xSDDescription.getTargetNamespace(), (SAXInputSource)xMLInputSource, bl, xSDDescription.getContextType(), element);
        }
        if (xMLInputSource instanceof StAXInputSource) {
            return this.getSchemaDocument(xSDDescription.getTargetNamespace(), (StAXInputSource)xMLInputSource, bl, xSDDescription.getContextType(), element);
        }
        if (xMLInputSource instanceof XSInputSource) {
            return this.getSchemaDocument((XSInputSource)xMLInputSource, xSDDescription);
        }
        return this.getSchemaDocument(xSDDescription.getTargetNamespace(), xMLInputSource, bl, xSDDescription.getContextType(), element);
    }

    private Element resolveSchema(XMLInputSource xMLInputSource, XSDDescription xSDDescription, boolean bl, Element element) {
        if (xMLInputSource instanceof DOMInputSource) {
            return this.getSchemaDocument(xSDDescription.getTargetNamespace(), (DOMInputSource)xMLInputSource, bl, xSDDescription.getContextType(), element);
        }
        if (xMLInputSource instanceof SAXInputSource) {
            return this.getSchemaDocument(xSDDescription.getTargetNamespace(), (SAXInputSource)xMLInputSource, bl, xSDDescription.getContextType(), element);
        }
        if (xMLInputSource instanceof StAXInputSource) {
            return this.getSchemaDocument(xSDDescription.getTargetNamespace(), (StAXInputSource)xMLInputSource, bl, xSDDescription.getContextType(), element);
        }
        if (xMLInputSource instanceof XSInputSource) {
            return this.getSchemaDocument((XSInputSource)xMLInputSource, xSDDescription);
        }
        return this.getSchemaDocument(xSDDescription.getTargetNamespace(), xMLInputSource, bl, xSDDescription.getContextType(), element);
    }

    private XMLInputSource resolveSchemaSource(XSDDescription xSDDescription, boolean bl, Element element, boolean bl2) {
        XMLInputSource xMLInputSource = null;
        try {
            Hashtable hashtable = bl2 ? this.fLocationPairs : EMPTY_TABLE;
            xMLInputSource = XMLSchemaLoader.resolveDocument(xSDDescription, hashtable, this.fEntityResolver);
        }
        catch (IOException iOException) {
            if (bl) {
                this.reportSchemaError("schema_reference.4", new Object[]{xSDDescription.getLocationHints()[0]}, element);
            }
            this.reportSchemaWarning("schema_reference.4", new Object[]{xSDDescription.getLocationHints()[0]}, element);
        }
        return xMLInputSource;
    }

    private Element getSchemaDocument(String string, XMLInputSource xMLInputSource, boolean bl, short s, Element element) {
        boolean bl2 = true;
        IOException iOException = null;
        Element element2 = null;
        try {
            if (xMLInputSource != null && (xMLInputSource.getSystemId() != null || xMLInputSource.getByteStream() != null || xMLInputSource.getCharacterStream() != null)) {
                XSDKey xSDKey = null;
                String string2 = null;
                if (s != 3 && (element2 = (Element)this.fTraversed.get(xSDKey = new XSDKey(string2 = XMLEntityManager.expandSystemId(xMLInputSource.getSystemId(), xMLInputSource.getBaseSystemId(), false), s, string))) != null) {
                    this.fLastSchemaWasDuplicate = true;
                    return element2;
                }
                this.fSchemaParser.parse(xMLInputSource);
                Document document = this.fSchemaParser.getDocument();
                element2 = document != null ? DOMUtil.getRoot(document) : null;
                return this.getSchemaDocument0(xSDKey, string2, element2);
            }
            bl2 = false;
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        return this.getSchemaDocument1(bl, bl2, xMLInputSource, element, iOException);
    }

    private Element getSchemaDocument(String string, SAXInputSource sAXInputSource, boolean bl, short s, Element element) {
        XMLReader xMLReader = sAXInputSource.getXMLReader();
        InputSource inputSource = sAXInputSource.getInputSource();
        boolean bl2 = true;
        IOException iOException = null;
        Element element2 = null;
        try {
            if (inputSource != null && (inputSource.getSystemId() != null || inputSource.getByteStream() != null || inputSource.getCharacterStream() != null)) {
                XSDKey xSDKey = null;
                String string2 = null;
                if (s != 3 && (element2 = (Element)this.fTraversed.get(xSDKey = new XSDKey(string2 = XMLEntityManager.expandSystemId(inputSource.getSystemId(), sAXInputSource.getBaseSystemId(), false), s, string))) != null) {
                    this.fLastSchemaWasDuplicate = true;
                    return element2;
                }
                boolean bl3 = false;
                if (xMLReader != null) {
                    try {
                        bl3 = xMLReader.getFeature(NAMESPACE_PREFIXES);
                    }
                    catch (SAXException sAXException) {}
                } else {
                    try {
                        xMLReader = XMLReaderFactory.createXMLReader();
                    }
                    catch (SAXException sAXException) {
                        xMLReader = new SAXParser();
                    }
                    try {
                        Object object;
                        xMLReader.setFeature(NAMESPACE_PREFIXES, true);
                        bl3 = true;
                        if (xMLReader instanceof SAXParser && (object = this.fSchemaParser.getProperty(SECURITY_MANAGER)) != null) {
                            xMLReader.setProperty(SECURITY_MANAGER, object);
                        }
                    }
                    catch (SAXException sAXException) {
                        // empty catch block
                    }
                }
                boolean bl4 = false;
                try {
                    bl4 = xMLReader.getFeature(STRING_INTERNING);
                }
                catch (SAXException sAXException) {
                    // empty catch block
                }
                if (this.fXSContentHandler == null) {
                    this.fXSContentHandler = new SchemaContentHandler();
                }
                this.fXSContentHandler.reset(this.fSchemaParser, this.fSymbolTable, bl3, bl4);
                xMLReader.setContentHandler(this.fXSContentHandler);
                xMLReader.setErrorHandler(this.fErrorReporter.getSAXErrorHandler());
                xMLReader.parse(inputSource);
                try {
                    xMLReader.setContentHandler(null);
                    xMLReader.setErrorHandler(null);
                }
                catch (Exception exception) {
                    // empty catch block
                }
                Document document = this.fXSContentHandler.getDocument();
                element2 = document != null ? DOMUtil.getRoot(document) : null;
                return this.getSchemaDocument0(xSDKey, string2, element2);
            }
            bl2 = false;
        }
        catch (SAXParseException sAXParseException) {
            throw SAX2XNIUtil.createXMLParseException0(sAXParseException);
        }
        catch (SAXException sAXException) {
            throw SAX2XNIUtil.createXNIException0(sAXException);
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        return this.getSchemaDocument1(bl, bl2, sAXInputSource, element, iOException);
    }

    private Element getSchemaDocument(String string, DOMInputSource dOMInputSource, boolean bl, short s, Element element) {
        boolean bl2 = true;
        IOException iOException = null;
        Element element2 = null;
        Element element3 = null;
        Node node = dOMInputSource.getNode();
        int n = -1;
        if (node != null) {
            n = node.getNodeType();
            if (n == 9) {
                element3 = DOMUtil.getRoot((Document)node);
            } else if (n == 1) {
                element3 = (Element)node;
            }
        }
        try {
            if (element3 != null) {
                XSDKey xSDKey = null;
                String string2 = null;
                if (s != 3) {
                    Node node2;
                    boolean bl3;
                    string2 = XMLEntityManager.expandSystemId(dOMInputSource.getSystemId(), dOMInputSource.getBaseSystemId(), false);
                    boolean bl4 = bl3 = n == 9;
                    if (!bl3 && (node2 = element3.getParentNode()) != null) {
                        boolean bl5 = bl3 = node2.getNodeType() == 9;
                    }
                    if (bl3 && (element2 = (Element)this.fTraversed.get(xSDKey = new XSDKey(string2, s, string))) != null) {
                        this.fLastSchemaWasDuplicate = true;
                        return element2;
                    }
                }
                element2 = element3;
                return this.getSchemaDocument0(xSDKey, string2, element2);
            }
            bl2 = false;
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        return this.getSchemaDocument1(bl, bl2, dOMInputSource, element, iOException);
    }

    private Element getSchemaDocument(String string, StAXInputSource stAXInputSource, boolean bl, short s, Element element) {
        IOException iOException = null;
        Element element2 = null;
        try {
            Document document;
            boolean bl2 = stAXInputSource.shouldConsumeRemainingContent();
            XMLStreamReader xMLStreamReader = stAXInputSource.getXMLStreamReader();
            XMLEventReader xMLEventReader = stAXInputSource.getXMLEventReader();
            XSDKey xSDKey = null;
            String string2 = null;
            if (s != 3) {
                string2 = XMLEntityManager.expandSystemId(stAXInputSource.getSystemId(), stAXInputSource.getBaseSystemId(), false);
                boolean bl3 = bl2;
                if (!bl3) {
                    bl3 = xMLStreamReader != null ? xMLStreamReader.getEventType() == 7 : xMLEventReader.peek().isStartDocument();
                }
                if (bl3 && (element2 = (Element)this.fTraversed.get(xSDKey = new XSDKey(string2, s, string))) != null) {
                    this.fLastSchemaWasDuplicate = true;
                    return element2;
                }
            }
            if (this.fStAXSchemaParser == null) {
                this.fStAXSchemaParser = new StAXSchemaParser();
            }
            this.fStAXSchemaParser.reset(this.fSchemaParser, this.fSymbolTable);
            if (xMLStreamReader != null) {
                this.fStAXSchemaParser.parse(xMLStreamReader);
                if (bl2) {
                    while (xMLStreamReader.hasNext()) {
                        xMLStreamReader.next();
                    }
                }
            } else {
                this.fStAXSchemaParser.parse(xMLEventReader);
                if (bl2) {
                    while (xMLEventReader.hasNext()) {
                        xMLEventReader.nextEvent();
                    }
                }
            }
            element2 = (document = this.fStAXSchemaParser.getDocument()) != null ? DOMUtil.getRoot(document) : null;
            return this.getSchemaDocument0(xSDKey, string2, element2);
        }
        catch (XMLStreamException xMLStreamException) {
            Throwable throwable = xMLStreamException.getNestedException();
            if (throwable instanceof IOException) {
                iOException = (IOException)throwable;
            }
            StAXLocationWrapper stAXLocationWrapper = new StAXLocationWrapper();
            stAXLocationWrapper.setLocation(xMLStreamException.getLocation());
            throw new XMLParseException(stAXLocationWrapper, xMLStreamException.getMessage(), xMLStreamException);
        }
        catch (IOException iOException2) {
            iOException = iOException2;
        }
        return this.getSchemaDocument1(bl, true, stAXInputSource, element, iOException);
    }

    private Element getSchemaDocument0(XSDKey xSDKey, String string, Element element) {
        if (xSDKey != null) {
            this.fTraversed.put(xSDKey, element);
        }
        if (string != null) {
            this.fDoc2SystemId.put(element, string);
        }
        this.fLastSchemaWasDuplicate = false;
        return element;
    }

    private Element getSchemaDocument1(boolean bl, boolean bl2, XMLInputSource xMLInputSource, Element element, IOException iOException) {
        if (bl) {
            if (bl2) {
                this.reportSchemaError("schema_reference.4", new Object[]{xMLInputSource.getSystemId()}, element, iOException);
            } else {
                this.reportSchemaError("schema_reference.4", new Object[]{xMLInputSource == null ? "" : xMLInputSource.getSystemId()}, element, iOException);
            }
        } else if (bl2) {
            this.reportSchemaWarning("schema_reference.4", new Object[]{xMLInputSource.getSystemId()}, element, iOException);
        }
        this.fLastSchemaWasDuplicate = false;
        return null;
    }

    private Element getSchemaDocument(XSInputSource xSInputSource, XSDDescription xSDDescription) {
        SchemaGrammar[] schemaGrammarArray = xSInputSource.getGrammars();
        short s = xSDDescription.getContextType();
        if (schemaGrammarArray != null && schemaGrammarArray.length > 0) {
            Vector vector = this.expandGrammars(schemaGrammarArray);
            if (this.fNamespaceGrowth || !this.existingGrammars(vector)) {
                this.addGrammars(vector);
                if (s == 3) {
                    xSDDescription.setTargetNamespace(schemaGrammarArray[0].getTargetNamespace());
                }
            }
        } else {
            XSObject[] xSObjectArray = xSInputSource.getComponents();
            if (xSObjectArray != null && xSObjectArray.length > 0) {
                Hashtable hashtable = new Hashtable();
                Vector vector = this.expandComponents(xSObjectArray, hashtable);
                if (this.fNamespaceGrowth || this.canAddComponents(vector)) {
                    this.addGlobalComponents(vector, hashtable);
                    if (s == 3) {
                        xSDDescription.setTargetNamespace(xSObjectArray[0].getNamespace());
                    }
                }
            }
        }
        return null;
    }

    private Vector expandGrammars(SchemaGrammar[] schemaGrammarArray) {
        Vector<SchemaGrammar> vector = new Vector<SchemaGrammar>();
        for (int i = 0; i < schemaGrammarArray.length; ++i) {
            if (vector.contains(schemaGrammarArray[i])) continue;
            vector.add(schemaGrammarArray[i]);
        }
        for (int i = 0; i < vector.size(); ++i) {
            SchemaGrammar schemaGrammar = (SchemaGrammar)vector.elementAt(i);
            Vector vector2 = schemaGrammar.getImportedGrammars();
            if (vector2 == null) continue;
            for (int j = vector2.size() - 1; j >= 0; --j) {
                SchemaGrammar schemaGrammar2 = (SchemaGrammar)vector2.elementAt(j);
                if (vector.contains(schemaGrammar2)) continue;
                vector.addElement(schemaGrammar2);
            }
        }
        return vector;
    }

    private boolean existingGrammars(Vector vector) {
        int n = vector.size();
        XSDDescription xSDDescription = new XSDDescription();
        for (int i = 0; i < n; ++i) {
            SchemaGrammar schemaGrammar = (SchemaGrammar)vector.elementAt(i);
            xSDDescription.setNamespace(schemaGrammar.getTargetNamespace());
            SchemaGrammar schemaGrammar2 = this.findGrammar(xSDDescription, false);
            if (schemaGrammar2 == null) continue;
            return true;
        }
        return false;
    }

    private boolean canAddComponents(Vector vector) {
        int n = vector.size();
        XSDDescription xSDDescription = new XSDDescription();
        for (int i = 0; i < n; ++i) {
            XSObject xSObject = (XSObject)vector.elementAt(i);
            if (this.canAddComponent(xSObject, xSDDescription)) continue;
            return false;
        }
        return true;
    }

    private boolean canAddComponent(XSObject xSObject, XSDDescription xSDDescription) {
        xSDDescription.setNamespace(xSObject.getNamespace());
        SchemaGrammar schemaGrammar = this.findGrammar(xSDDescription, false);
        if (schemaGrammar == null) {
            return true;
        }
        if (schemaGrammar.isImmutable()) {
            return false;
        }
        short s = xSObject.getType();
        String string = xSObject.getName();
        switch (s) {
            case 3: {
                if (schemaGrammar.getGlobalTypeDecl(string) != xSObject) break;
                return true;
            }
            case 1: {
                if (schemaGrammar.getGlobalAttributeDecl(string) != xSObject) break;
                return true;
            }
            case 5: {
                if (schemaGrammar.getGlobalAttributeDecl(string) != xSObject) break;
                return true;
            }
            case 2: {
                if (schemaGrammar.getGlobalElementDecl(string) != xSObject) break;
                return true;
            }
            case 6: {
                if (schemaGrammar.getGlobalGroupDecl(string) != xSObject) break;
                return true;
            }
            case 11: {
                if (schemaGrammar.getGlobalNotationDecl(string) != xSObject) break;
                return true;
            }
            default: {
                return true;
            }
        }
        return false;
    }

    private void addGrammars(Vector vector) {
        int n = vector.size();
        XSDDescription xSDDescription = new XSDDescription();
        for (int i = 0; i < n; ++i) {
            SchemaGrammar schemaGrammar = (SchemaGrammar)vector.elementAt(i);
            xSDDescription.setNamespace(schemaGrammar.getTargetNamespace());
            SchemaGrammar schemaGrammar2 = this.findGrammar(xSDDescription, this.fNamespaceGrowth);
            if (schemaGrammar == schemaGrammar2) continue;
            this.addGrammarComponents(schemaGrammar, schemaGrammar2);
        }
    }

    private void addGrammarComponents(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        if (schemaGrammar2 == null) {
            this.createGrammarFrom(schemaGrammar);
            return;
        }
        SchemaGrammar schemaGrammar3 = schemaGrammar2;
        if (schemaGrammar3.isImmutable()) {
            schemaGrammar3 = this.createGrammarFrom(schemaGrammar2);
        }
        this.addNewGrammarLocations(schemaGrammar, schemaGrammar3);
        this.addNewImportedGrammars(schemaGrammar, schemaGrammar3);
        this.addNewGrammarComponents(schemaGrammar, schemaGrammar3);
    }

    private SchemaGrammar createGrammarFrom(SchemaGrammar schemaGrammar) {
        SchemaGrammar schemaGrammar2 = new SchemaGrammar(schemaGrammar);
        this.fGrammarBucket.putGrammar(schemaGrammar2);
        this.updateImportListWith(schemaGrammar2);
        this.updateImportListFor(schemaGrammar2);
        return schemaGrammar2;
    }

    private void addNewGrammarLocations(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        StringList stringList = schemaGrammar.getDocumentLocations();
        int n = stringList.size();
        StringList stringList2 = schemaGrammar2.getDocumentLocations();
        for (int i = 0; i < n; ++i) {
            String string = stringList.item(i);
            if (stringList2.contains(string)) continue;
            schemaGrammar2.addDocument(null, string);
        }
    }

    private void addNewImportedGrammars(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        Vector vector = schemaGrammar.getImportedGrammars();
        if (vector != null) {
            Vector<SchemaGrammar> vector2 = schemaGrammar2.getImportedGrammars();
            if (vector2 == null) {
                vector2 = new Vector<SchemaGrammar>();
                schemaGrammar2.setImportedGrammars(vector2);
            }
            int n = vector.size();
            for (int i = 0; i < n; ++i) {
                SchemaGrammar schemaGrammar3 = (SchemaGrammar)vector.elementAt(i);
                SchemaGrammar schemaGrammar4 = this.fGrammarBucket.getGrammar(schemaGrammar3.getTargetNamespace());
                if (schemaGrammar4 != null) {
                    schemaGrammar3 = schemaGrammar4;
                }
                if (this.containedImportedGrammar(vector2, schemaGrammar3)) continue;
                vector2.add(schemaGrammar3);
            }
        }
    }

    private void updateImportList(Vector vector, Vector vector2) {
        int n = vector.size();
        for (int i = 0; i < n; ++i) {
            SchemaGrammar schemaGrammar = (SchemaGrammar)vector.elementAt(i);
            if (this.containedImportedGrammar(vector2, schemaGrammar)) continue;
            vector2.add(schemaGrammar);
        }
    }

    private void addNewGrammarComponents(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        schemaGrammar2.resetComponents();
        this.addGlobalElementDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalAttributeDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalAttributeGroupDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalGroupDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalTypeDecls(schemaGrammar, schemaGrammar2);
        this.addGlobalNotationDecls(schemaGrammar, schemaGrammar2);
    }

    private void addGlobalElementDecls(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        XSElementDecl xSElementDecl;
        XSElementDecl xSElementDecl2;
        XSNamedMap xSNamedMap = schemaGrammar.getComponents((short)2);
        int n = xSNamedMap.getLength();
        for (int i = 0; i < n; ++i) {
            xSElementDecl2 = (XSElementDecl)xSNamedMap.item(i);
            xSElementDecl = schemaGrammar2.getGlobalElementDecl(xSElementDecl2.getName());
            if (xSElementDecl == null) {
                schemaGrammar2.addGlobalElementDecl(xSElementDecl2);
                continue;
            }
            if (xSElementDecl == xSElementDecl2) continue;
        }
        ObjectList objectList = schemaGrammar.getComponentsExt((short)2);
        n = objectList.getLength();
        for (int i = 0; i < n; i += 2) {
            String string = (String)objectList.item(i);
            int n2 = string.indexOf(44);
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, string.length());
            xSElementDecl2 = (XSElementDecl)objectList.item(i + 1);
            xSElementDecl = schemaGrammar2.getGlobalElementDecl(string3, string2);
            if (xSElementDecl == null) {
                schemaGrammar2.addGlobalElementDecl(xSElementDecl2, string2);
                continue;
            }
            if (xSElementDecl == xSElementDecl2) continue;
        }
    }

    private void addGlobalAttributeDecls(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        XSAttributeDecl xSAttributeDecl;
        XSAttributeDecl xSAttributeDecl2;
        XSNamedMap xSNamedMap = schemaGrammar.getComponents((short)1);
        int n = xSNamedMap.getLength();
        for (int i = 0; i < n; ++i) {
            xSAttributeDecl2 = (XSAttributeDecl)xSNamedMap.item(i);
            xSAttributeDecl = schemaGrammar2.getGlobalAttributeDecl(xSAttributeDecl2.getName());
            if (xSAttributeDecl == null) {
                schemaGrammar2.addGlobalAttributeDecl(xSAttributeDecl2);
                continue;
            }
            if (xSAttributeDecl == xSAttributeDecl2 || this.fTolerateDuplicates) continue;
            this.reportSharingError(xSAttributeDecl2.getNamespace(), xSAttributeDecl2.getName());
        }
        ObjectList objectList = schemaGrammar.getComponentsExt((short)1);
        n = objectList.getLength();
        for (int i = 0; i < n; i += 2) {
            String string = (String)objectList.item(i);
            int n2 = string.indexOf(44);
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, string.length());
            xSAttributeDecl2 = (XSAttributeDecl)objectList.item(i + 1);
            xSAttributeDecl = schemaGrammar2.getGlobalAttributeDecl(string3, string2);
            if (xSAttributeDecl == null) {
                schemaGrammar2.addGlobalAttributeDecl(xSAttributeDecl2, string2);
                continue;
            }
            if (xSAttributeDecl == xSAttributeDecl2) continue;
        }
    }

    private void addGlobalAttributeGroupDecls(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        XSAttributeGroupDecl xSAttributeGroupDecl;
        XSAttributeGroupDecl xSAttributeGroupDecl2;
        XSNamedMap xSNamedMap = schemaGrammar.getComponents((short)5);
        int n = xSNamedMap.getLength();
        for (int i = 0; i < n; ++i) {
            xSAttributeGroupDecl2 = (XSAttributeGroupDecl)xSNamedMap.item(i);
            xSAttributeGroupDecl = schemaGrammar2.getGlobalAttributeGroupDecl(xSAttributeGroupDecl2.getName());
            if (xSAttributeGroupDecl == null) {
                schemaGrammar2.addGlobalAttributeGroupDecl(xSAttributeGroupDecl2);
                continue;
            }
            if (xSAttributeGroupDecl == xSAttributeGroupDecl2 || this.fTolerateDuplicates) continue;
            this.reportSharingError(xSAttributeGroupDecl2.getNamespace(), xSAttributeGroupDecl2.getName());
        }
        ObjectList objectList = schemaGrammar.getComponentsExt((short)5);
        n = objectList.getLength();
        for (int i = 0; i < n; i += 2) {
            String string = (String)objectList.item(i);
            int n2 = string.indexOf(44);
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, string.length());
            xSAttributeGroupDecl2 = (XSAttributeGroupDecl)objectList.item(i + 1);
            xSAttributeGroupDecl = schemaGrammar2.getGlobalAttributeGroupDecl(string3, string2);
            if (xSAttributeGroupDecl == null) {
                schemaGrammar2.addGlobalAttributeGroupDecl(xSAttributeGroupDecl2, string2);
                continue;
            }
            if (xSAttributeGroupDecl == xSAttributeGroupDecl2) continue;
        }
    }

    private void addGlobalNotationDecls(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        XSNotationDecl xSNotationDecl;
        XSNotationDecl xSNotationDecl2;
        XSNamedMap xSNamedMap = schemaGrammar.getComponents((short)11);
        int n = xSNamedMap.getLength();
        for (int i = 0; i < n; ++i) {
            xSNotationDecl2 = (XSNotationDecl)xSNamedMap.item(i);
            xSNotationDecl = schemaGrammar2.getGlobalNotationDecl(xSNotationDecl2.getName());
            if (xSNotationDecl == null) {
                schemaGrammar2.addGlobalNotationDecl(xSNotationDecl2);
                continue;
            }
            if (xSNotationDecl == xSNotationDecl2 || this.fTolerateDuplicates) continue;
            this.reportSharingError(xSNotationDecl2.getNamespace(), xSNotationDecl2.getName());
        }
        ObjectList objectList = schemaGrammar.getComponentsExt((short)11);
        n = objectList.getLength();
        for (int i = 0; i < n; i += 2) {
            String string = (String)objectList.item(i);
            int n2 = string.indexOf(44);
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, string.length());
            xSNotationDecl2 = (XSNotationDecl)objectList.item(i + 1);
            xSNotationDecl = schemaGrammar2.getGlobalNotationDecl(string3, string2);
            if (xSNotationDecl == null) {
                schemaGrammar2.addGlobalNotationDecl(xSNotationDecl2, string2);
                continue;
            }
            if (xSNotationDecl == xSNotationDecl2) continue;
        }
    }

    private void addGlobalGroupDecls(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        XSGroupDecl xSGroupDecl;
        XSGroupDecl xSGroupDecl2;
        XSNamedMap xSNamedMap = schemaGrammar.getComponents((short)6);
        int n = xSNamedMap.getLength();
        for (int i = 0; i < n; ++i) {
            xSGroupDecl2 = (XSGroupDecl)xSNamedMap.item(i);
            xSGroupDecl = schemaGrammar2.getGlobalGroupDecl(xSGroupDecl2.getName());
            if (xSGroupDecl == null) {
                schemaGrammar2.addGlobalGroupDecl(xSGroupDecl2);
                continue;
            }
            if (xSGroupDecl2 == xSGroupDecl || this.fTolerateDuplicates) continue;
            this.reportSharingError(xSGroupDecl2.getNamespace(), xSGroupDecl2.getName());
        }
        ObjectList objectList = schemaGrammar.getComponentsExt((short)6);
        n = objectList.getLength();
        for (int i = 0; i < n; i += 2) {
            String string = (String)objectList.item(i);
            int n2 = string.indexOf(44);
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, string.length());
            xSGroupDecl2 = (XSGroupDecl)objectList.item(i + 1);
            xSGroupDecl = schemaGrammar2.getGlobalGroupDecl(string3, string2);
            if (xSGroupDecl == null) {
                schemaGrammar2.addGlobalGroupDecl(xSGroupDecl2, string2);
                continue;
            }
            if (xSGroupDecl == xSGroupDecl2) continue;
        }
    }

    private void addGlobalTypeDecls(SchemaGrammar schemaGrammar, SchemaGrammar schemaGrammar2) {
        XSTypeDefinition xSTypeDefinition;
        XSTypeDefinition xSTypeDefinition2;
        XSNamedMap xSNamedMap = schemaGrammar.getComponents((short)3);
        int n = xSNamedMap.getLength();
        for (int i = 0; i < n; ++i) {
            xSTypeDefinition2 = (XSTypeDefinition)xSNamedMap.item(i);
            xSTypeDefinition = schemaGrammar2.getGlobalTypeDecl(xSTypeDefinition2.getName());
            if (xSTypeDefinition == null) {
                schemaGrammar2.addGlobalTypeDecl(xSTypeDefinition2);
                continue;
            }
            if (xSTypeDefinition == xSTypeDefinition2 || this.fTolerateDuplicates) continue;
            this.reportSharingError(xSTypeDefinition2.getNamespace(), xSTypeDefinition2.getName());
        }
        ObjectList objectList = schemaGrammar.getComponentsExt((short)3);
        n = objectList.getLength();
        for (int i = 0; i < n; i += 2) {
            String string = (String)objectList.item(i);
            int n2 = string.indexOf(44);
            String string2 = string.substring(0, n2);
            String string3 = string.substring(n2 + 1, string.length());
            xSTypeDefinition2 = (XSTypeDefinition)objectList.item(i + 1);
            xSTypeDefinition = schemaGrammar2.getGlobalTypeDecl(string3, string2);
            if (xSTypeDefinition == null) {
                schemaGrammar2.addGlobalTypeDecl(xSTypeDefinition2, string2);
                continue;
            }
            if (xSTypeDefinition == xSTypeDefinition2) continue;
        }
    }

    private Vector expandComponents(XSObject[] xSObjectArray, Hashtable hashtable) {
        int n;
        Vector<XSObject> vector = new Vector<XSObject>();
        for (n = 0; n < xSObjectArray.length; ++n) {
            if (vector.contains(xSObjectArray[n])) continue;
            vector.add(xSObjectArray[n]);
        }
        for (n = 0; n < vector.size(); ++n) {
            XSObject xSObject = (XSObject)vector.elementAt(n);
            this.expandRelatedComponents(xSObject, vector, hashtable);
        }
        return vector;
    }

    private void expandRelatedComponents(XSObject xSObject, Vector vector, Hashtable hashtable) {
        short s = xSObject.getType();
        switch (s) {
            case 3: {
                this.expandRelatedTypeComponents((XSTypeDefinition)xSObject, vector, xSObject.getNamespace(), hashtable);
                break;
            }
            case 1: {
                this.expandRelatedAttributeComponents((XSAttributeDeclaration)xSObject, vector, xSObject.getNamespace(), hashtable);
                break;
            }
            case 5: {
                this.expandRelatedAttributeGroupComponents((XSAttributeGroupDefinition)xSObject, vector, xSObject.getNamespace(), hashtable);
            }
            case 2: {
                this.expandRelatedElementComponents((XSElementDeclaration)xSObject, vector, xSObject.getNamespace(), hashtable);
                break;
            }
            case 6: {
                this.expandRelatedModelGroupDefinitionComponents((XSModelGroupDefinition)xSObject, vector, xSObject.getNamespace(), hashtable);
            }
        }
    }

    private void expandRelatedAttributeComponents(XSAttributeDeclaration xSAttributeDeclaration, Vector vector, String string, Hashtable hashtable) {
        this.addRelatedType(xSAttributeDeclaration.getTypeDefinition(), vector, string, hashtable);
    }

    private void expandRelatedElementComponents(XSElementDeclaration xSElementDeclaration, Vector vector, String string, Hashtable hashtable) {
        this.addRelatedType(xSElementDeclaration.getTypeDefinition(), vector, string, hashtable);
        XSElementDeclaration xSElementDeclaration2 = xSElementDeclaration.getSubstitutionGroupAffiliation();
        if (xSElementDeclaration2 != null) {
            this.addRelatedElement(xSElementDeclaration2, vector, string, hashtable);
        }
    }

    private void expandRelatedTypeComponents(XSTypeDefinition xSTypeDefinition, Vector vector, String string, Hashtable hashtable) {
        if (xSTypeDefinition instanceof XSComplexTypeDecl) {
            this.expandRelatedComplexTypeComponents((XSComplexTypeDecl)xSTypeDefinition, vector, string, hashtable);
        } else if (xSTypeDefinition instanceof XSSimpleTypeDecl) {
            this.expandRelatedSimpleTypeComponents((XSSimpleTypeDefinition)xSTypeDefinition, vector, string, hashtable);
        }
    }

    private void expandRelatedModelGroupDefinitionComponents(XSModelGroupDefinition xSModelGroupDefinition, Vector vector, String string, Hashtable hashtable) {
        this.expandRelatedModelGroupComponents(xSModelGroupDefinition.getModelGroup(), vector, string, hashtable);
    }

    private void expandRelatedAttributeGroupComponents(XSAttributeGroupDefinition xSAttributeGroupDefinition, Vector vector, String string, Hashtable hashtable) {
        this.expandRelatedAttributeUsesComponents(xSAttributeGroupDefinition.getAttributeUses(), vector, string, hashtable);
    }

    private void expandRelatedComplexTypeComponents(XSComplexTypeDecl xSComplexTypeDecl, Vector vector, String string, Hashtable hashtable) {
        this.addRelatedType(xSComplexTypeDecl.getBaseType(), vector, string, hashtable);
        this.expandRelatedAttributeUsesComponents(xSComplexTypeDecl.getAttributeUses(), vector, string, hashtable);
        XSParticle xSParticle = xSComplexTypeDecl.getParticle();
        if (xSParticle != null) {
            this.expandRelatedParticleComponents(xSParticle, vector, string, hashtable);
        }
    }

    private void expandRelatedSimpleTypeComponents(XSSimpleTypeDefinition xSSimpleTypeDefinition, Vector vector, String string, Hashtable hashtable) {
        XSObjectList xSObjectList;
        XSSimpleTypeDefinition xSSimpleTypeDefinition2;
        XSSimpleTypeDefinition xSSimpleTypeDefinition3;
        XSTypeDefinition xSTypeDefinition = xSSimpleTypeDefinition.getBaseType();
        if (xSTypeDefinition != null) {
            this.addRelatedType(xSTypeDefinition, vector, string, hashtable);
        }
        if ((xSSimpleTypeDefinition3 = xSSimpleTypeDefinition.getItemType()) != null) {
            this.addRelatedType(xSSimpleTypeDefinition3, vector, string, hashtable);
        }
        if ((xSSimpleTypeDefinition2 = xSSimpleTypeDefinition.getPrimitiveType()) != null) {
            this.addRelatedType(xSSimpleTypeDefinition2, vector, string, hashtable);
        }
        if ((xSObjectList = xSSimpleTypeDefinition.getMemberTypes()).size() > 0) {
            for (int i = 0; i < xSObjectList.size(); ++i) {
                this.addRelatedType((XSTypeDefinition)xSObjectList.item(i), vector, string, hashtable);
            }
        }
    }

    private void expandRelatedAttributeUsesComponents(XSObjectList xSObjectList, Vector vector, String string, Hashtable hashtable) {
        int n = xSObjectList == null ? 0 : xSObjectList.size();
        for (int i = 0; i < n; ++i) {
            this.expandRelatedAttributeUseComponents((XSAttributeUse)xSObjectList.item(i), vector, string, hashtable);
        }
    }

    private void expandRelatedAttributeUseComponents(XSAttributeUse xSAttributeUse, Vector vector, String string, Hashtable hashtable) {
        this.addRelatedAttribute(xSAttributeUse.getAttrDeclaration(), vector, string, hashtable);
    }

    private void expandRelatedParticleComponents(XSParticle xSParticle, Vector vector, String string, Hashtable hashtable) {
        XSTerm xSTerm = xSParticle.getTerm();
        switch (xSTerm.getType()) {
            case 2: {
                this.addRelatedElement((XSElementDeclaration)xSTerm, vector, string, hashtable);
                break;
            }
            case 7: {
                this.expandRelatedModelGroupComponents((XSModelGroup)xSTerm, vector, string, hashtable);
                break;
            }
        }
    }

    private void expandRelatedModelGroupComponents(XSModelGroup xSModelGroup, Vector vector, String string, Hashtable hashtable) {
        XSObjectList xSObjectList = xSModelGroup.getParticles();
        int n = xSObjectList == null ? 0 : xSObjectList.getLength();
        for (int i = 0; i < n; ++i) {
            this.expandRelatedParticleComponents((XSParticle)xSObjectList.item(i), vector, string, hashtable);
        }
    }

    private void addRelatedType(XSTypeDefinition xSTypeDefinition, Vector vector, String string, Hashtable hashtable) {
        if (!xSTypeDefinition.getAnonymous()) {
            if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(xSTypeDefinition.getNamespace()) && !vector.contains(xSTypeDefinition)) {
                Vector vector2 = this.findDependentNamespaces(string, hashtable);
                this.addNamespaceDependency(string, xSTypeDefinition.getNamespace(), vector2);
                vector.add(xSTypeDefinition);
            }
        } else {
            this.expandRelatedTypeComponents(xSTypeDefinition, vector, string, hashtable);
        }
    }

    private void addRelatedElement(XSElementDeclaration xSElementDeclaration, Vector vector, String string, Hashtable hashtable) {
        if (xSElementDeclaration.getScope() == 1) {
            if (!vector.contains(xSElementDeclaration)) {
                Vector vector2 = this.findDependentNamespaces(string, hashtable);
                this.addNamespaceDependency(string, xSElementDeclaration.getNamespace(), vector2);
                vector.add(xSElementDeclaration);
            }
        } else {
            this.expandRelatedElementComponents(xSElementDeclaration, vector, string, hashtable);
        }
    }

    private void addRelatedAttribute(XSAttributeDeclaration xSAttributeDeclaration, Vector vector, String string, Hashtable hashtable) {
        if (xSAttributeDeclaration.getScope() == 1) {
            if (!vector.contains(xSAttributeDeclaration)) {
                Vector vector2 = this.findDependentNamespaces(string, hashtable);
                this.addNamespaceDependency(string, xSAttributeDeclaration.getNamespace(), vector2);
                vector.add(xSAttributeDeclaration);
            }
        } else {
            this.expandRelatedAttributeComponents(xSAttributeDeclaration, vector, string, hashtable);
        }
    }

    private void addGlobalComponents(Vector vector, Hashtable hashtable) {
        XSDDescription xSDDescription = new XSDDescription();
        int n = vector.size();
        for (int i = 0; i < n; ++i) {
            this.addGlobalComponent((XSObject)vector.elementAt(i), xSDDescription);
        }
        this.updateImportDependencies(hashtable);
    }

    private void addGlobalComponent(XSObject xSObject, XSDDescription xSDDescription) {
        String string = xSObject.getNamespace();
        xSDDescription.setNamespace(string);
        SchemaGrammar schemaGrammar = this.getSchemaGrammar(xSDDescription);
        short s = xSObject.getType();
        String string2 = xSObject.getName();
        switch (s) {
            case 3: {
                if (((XSTypeDefinition)xSObject).getAnonymous()) break;
                if (schemaGrammar.getGlobalTypeDecl(string2) == null) {
                    schemaGrammar.addGlobalTypeDecl((XSTypeDefinition)xSObject);
                }
                if (schemaGrammar.getGlobalTypeDecl(string2, "") != null) break;
                schemaGrammar.addGlobalTypeDecl((XSTypeDefinition)xSObject, "");
                break;
            }
            case 1: {
                if (((XSAttributeDecl)xSObject).getScope() != 1) break;
                if (schemaGrammar.getGlobalAttributeDecl(string2) == null) {
                    schemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)xSObject);
                }
                if (schemaGrammar.getGlobalAttributeDecl(string2, "") != null) break;
                schemaGrammar.addGlobalAttributeDecl((XSAttributeDecl)xSObject, "");
                break;
            }
            case 5: {
                if (schemaGrammar.getGlobalAttributeDecl(string2) == null) {
                    schemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)xSObject);
                }
                if (schemaGrammar.getGlobalAttributeDecl(string2, "") != null) break;
                schemaGrammar.addGlobalAttributeGroupDecl((XSAttributeGroupDecl)xSObject, "");
                break;
            }
            case 2: {
                if (((XSElementDecl)xSObject).getScope() != 1) break;
                schemaGrammar.addGlobalElementDeclAll((XSElementDecl)xSObject);
                if (schemaGrammar.getGlobalElementDecl(string2) == null) {
                    schemaGrammar.addGlobalElementDecl((XSElementDecl)xSObject);
                }
                if (schemaGrammar.getGlobalElementDecl(string2, "") != null) break;
                schemaGrammar.addGlobalElementDecl((XSElementDecl)xSObject, "");
                break;
            }
            case 6: {
                if (schemaGrammar.getGlobalGroupDecl(string2) == null) {
                    schemaGrammar.addGlobalGroupDecl((XSGroupDecl)xSObject);
                }
                if (schemaGrammar.getGlobalGroupDecl(string2, "") != null) break;
                schemaGrammar.addGlobalGroupDecl((XSGroupDecl)xSObject, "");
                break;
            }
            case 11: {
                if (schemaGrammar.getGlobalNotationDecl(string2) == null) {
                    schemaGrammar.addGlobalNotationDecl((XSNotationDecl)xSObject);
                }
                if (schemaGrammar.getGlobalNotationDecl(string2, "") != null) break;
                schemaGrammar.addGlobalNotationDecl((XSNotationDecl)xSObject, "");
                break;
            }
        }
    }

    private void updateImportDependencies(Hashtable hashtable) {
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
            String string = (String)enumeration.nextElement();
            Vector vector = (Vector)hashtable.get(this.null2EmptyString(string));
            if (vector.size() <= 0) continue;
            this.expandImportList(string, vector);
        }
    }

    private void expandImportList(String string, Vector vector) {
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(string);
        if (schemaGrammar != null) {
            Vector vector2 = schemaGrammar.getImportedGrammars();
            if (vector2 == null) {
                vector2 = new Vector();
                this.addImportList(schemaGrammar, vector2, vector);
                schemaGrammar.setImportedGrammars(vector2);
            } else {
                this.updateImportList(schemaGrammar, vector2, vector);
            }
        }
    }

    private void addImportList(SchemaGrammar schemaGrammar, Vector vector, Vector vector2) {
        int n = vector2.size();
        for (int i = 0; i < n; ++i) {
            SchemaGrammar schemaGrammar2 = this.fGrammarBucket.getGrammar((String)vector2.elementAt(i));
            if (schemaGrammar2 == null) continue;
            vector.add(schemaGrammar2);
        }
    }

    private void updateImportList(SchemaGrammar schemaGrammar, Vector vector, Vector vector2) {
        int n = vector2.size();
        for (int i = 0; i < n; ++i) {
            SchemaGrammar schemaGrammar2 = this.fGrammarBucket.getGrammar((String)vector2.elementAt(i));
            if (schemaGrammar2 == null || this.containedImportedGrammar(vector, schemaGrammar2)) continue;
            vector.add(schemaGrammar2);
        }
    }

    private boolean containedImportedGrammar(Vector vector, SchemaGrammar schemaGrammar) {
        int n = vector.size();
        for (int i = 0; i < n; ++i) {
            SchemaGrammar schemaGrammar2 = (SchemaGrammar)vector.elementAt(i);
            if (!this.null2EmptyString(schemaGrammar2.getTargetNamespace()).equals(this.null2EmptyString(schemaGrammar.getTargetNamespace()))) continue;
            return true;
        }
        return false;
    }

    private SchemaGrammar getSchemaGrammar(XSDDescription xSDDescription) {
        SchemaGrammar schemaGrammar = this.findGrammar(xSDDescription, this.fNamespaceGrowth);
        if (schemaGrammar == null) {
            schemaGrammar = new SchemaGrammar(xSDDescription.getNamespace(), xSDDescription.makeClone(), this.fSymbolTable);
            this.fGrammarBucket.putGrammar(schemaGrammar);
        } else if (schemaGrammar.isImmutable()) {
            schemaGrammar = this.createGrammarFrom(schemaGrammar);
        }
        return schemaGrammar;
    }

    private Vector findDependentNamespaces(String string, Hashtable hashtable) {
        String string2 = this.null2EmptyString(string);
        Vector vector = (Vector)hashtable.get(string2);
        if (vector == null) {
            vector = new Vector();
            hashtable.put(string2, vector);
        }
        return vector;
    }

    private void addNamespaceDependency(String string, String string2, Vector vector) {
        String string3;
        String string4 = this.null2EmptyString(string);
        if (!string4.equals(string3 = this.null2EmptyString(string2)) && !vector.contains(string3)) {
            vector.add(string3);
        }
    }

    private void reportSharingError(String string, String string2) {
        String string3 = string == null ? "," + string2 : string + "," + string2;
        this.reportSchemaError("sch-props-correct.2", new Object[]{string3}, null);
    }

    private void createTraversers() {
        this.fAttributeChecker = new XSAttributeChecker(this);
        this.fAttributeGroupTraverser = new XSDAttributeGroupTraverser(this, this.fAttributeChecker);
        this.fAttributeTraverser = new XSDAttributeTraverser(this, this.fAttributeChecker);
        this.fComplexTypeTraverser = new XSDComplexTypeTraverser(this, this.fAttributeChecker);
        this.fElementTraverser = new XSDElementTraverser(this, this.fAttributeChecker);
        this.fGroupTraverser = new XSDGroupTraverser(this, this.fAttributeChecker);
        this.fKeyrefTraverser = new XSDKeyrefTraverser(this, this.fAttributeChecker);
        this.fNotationTraverser = new XSDNotationTraverser(this, this.fAttributeChecker);
        this.fSimpleTypeTraverser = new XSDSimpleTypeTraverser(this, this.fAttributeChecker);
        this.fUniqueOrKeyTraverser = new XSDUniqueOrKeyTraverser(this, this.fAttributeChecker);
        this.fWildCardTraverser = new XSDWildcardTraverser(this, this.fAttributeChecker);
    }

    void prepareForParse() {
        this.fTraversed.clear();
        this.fDoc2SystemId.clear();
        this.fHiddenNodes.clear();
        this.fLastSchemaWasDuplicate = false;
    }

    void prepareForTraverse() {
        int n;
        this.fUnparsedAttributeRegistry.clear();
        this.fUnparsedAttributeGroupRegistry.clear();
        this.fUnparsedElementRegistry.clear();
        this.fUnparsedGroupRegistry.clear();
        this.fUnparsedIdentityConstraintRegistry.clear();
        this.fUnparsedNotationRegistry.clear();
        this.fUnparsedTypeRegistry.clear();
        this.fUnparsedAttributeRegistrySub.clear();
        this.fUnparsedAttributeGroupRegistrySub.clear();
        this.fUnparsedElementRegistrySub.clear();
        this.fUnparsedGroupRegistrySub.clear();
        this.fUnparsedIdentityConstraintRegistrySub.clear();
        this.fUnparsedNotationRegistrySub.clear();
        this.fUnparsedTypeRegistrySub.clear();
        for (n = 1; n <= 7; ++n) {
            this.fUnparsedRegistriesExt[n].clear();
        }
        this.fXSDocumentInfoRegistry.clear();
        this.fDependencyMap.clear();
        this.fDoc2XSDocumentMap.clear();
        this.fRedefine2XSDMap.clear();
        this.fRedefine2NSSupport.clear();
        this.fAllTNSs.removeAllElements();
        this.fImportMap.clear();
        this.fRoot = null;
        for (n = 0; n < this.fLocalElemStackPos; ++n) {
            this.fParticle[n] = null;
            this.fLocalElementDecl[n] = null;
            this.fLocalElementDecl_schema[n] = null;
            this.fLocalElemNamespaceContext[n] = null;
        }
        this.fLocalElemStackPos = 0;
        for (n = 0; n < this.fKeyrefStackPos; ++n) {
            this.fKeyrefs[n] = null;
            this.fKeyrefElems[n] = null;
            this.fKeyrefNamespaceContext[n] = null;
            this.fKeyrefsMapXSDocumentInfo[n] = null;
        }
        this.fKeyrefStackPos = 0;
        if (this.fAttributeChecker == null) {
            this.createTraversers();
        }
        Locale locale = this.fErrorReporter.getLocale();
        this.fAttributeChecker.reset(this.fSymbolTable);
        this.fAttributeGroupTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fAttributeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fComplexTypeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fElementTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fGroupTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fKeyrefTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fNotationTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fSimpleTypeTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fUniqueOrKeyTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fWildCardTraverser.reset(this.fSymbolTable, this.fValidateAnnotations, locale);
        this.fRedefinedRestrictedAttributeGroupRegistry.clear();
        this.fRedefinedRestrictedGroupRegistry.clear();
        this.fGlobalAttrDecls.clear();
        this.fGlobalAttrGrpDecls.clear();
        this.fGlobalElemDecls.clear();
        this.fGlobalGroupDecls.clear();
        this.fGlobalNotationDecls.clear();
        this.fGlobalIDConstraintDecls.clear();
        this.fGlobalTypeDecls.clear();
    }

    public void setDeclPool(XSDeclarationPool xSDeclarationPool) {
        this.fDeclPool = xSDeclarationPool;
    }

    public void setDVFactory(SchemaDVFactory schemaDVFactory) {
        this.fDVFactory = schemaDVFactory;
    }

    public void reset(XMLComponentManager xMLComponentManager) {
        Object object;
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        this.fEntityResolver = (XMLEntityResolver)xMLComponentManager.getProperty(ENTITY_MANAGER);
        XMLEntityResolver xMLEntityResolver = (XMLEntityResolver)xMLComponentManager.getProperty(ENTITY_RESOLVER);
        if (xMLEntityResolver != null) {
            this.fSchemaParser.setEntityResolver(xMLEntityResolver);
        }
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        try {
            Locale locale;
            object = this.fErrorReporter.getErrorHandler();
            if (object != this.fSchemaParser.getProperty(ERROR_HANDLER)) {
                this.fSchemaParser.setProperty(ERROR_HANDLER, object != null ? object : new DefaultErrorHandler());
                if (this.fAnnotationValidator != null) {
                    this.fAnnotationValidator.setProperty(ERROR_HANDLER, object != null ? object : new DefaultErrorHandler());
                }
            }
            if ((locale = this.fErrorReporter.getLocale()) != this.fSchemaParser.getProperty(LOCALE)) {
                this.fSchemaParser.setProperty(LOCALE, locale);
                if (this.fAnnotationValidator != null) {
                    this.fAnnotationValidator.setProperty(LOCALE, locale);
                }
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            this.fValidateAnnotations = xMLComponentManager.getFeature(VALIDATE_ANNOTATIONS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidateAnnotations = false;
        }
        try {
            this.fHonourAllSchemaLocations = xMLComponentManager.getFeature(HONOUR_ALL_SCHEMALOCATIONS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fHonourAllSchemaLocations = false;
        }
        try {
            this.fNamespaceGrowth = xMLComponentManager.getFeature(NAMESPACE_GROWTH);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fNamespaceGrowth = false;
        }
        try {
            this.fTolerateDuplicates = xMLComponentManager.getFeature(TOLERATE_DUPLICATES);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fTolerateDuplicates = false;
        }
        try {
            this.fSchemaParser.setFeature(CONTINUE_AFTER_FATAL_ERROR, this.fErrorReporter.getFeature(CONTINUE_AFTER_FATAL_ERROR));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            this.fSchemaParser.setFeature(ALLOW_JAVA_ENCODINGS, xMLComponentManager.getFeature(ALLOW_JAVA_ENCODINGS));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            this.fSchemaParser.setFeature(STANDARD_URI_CONFORMANT_FEATURE, xMLComponentManager.getFeature(STANDARD_URI_CONFORMANT_FEATURE));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            this.fGrammarPool = (XMLGrammarPool)xMLComponentManager.getProperty(XMLGRAMMAR_POOL);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fGrammarPool = null;
        }
        try {
            this.fSchemaParser.setFeature(DISALLOW_DOCTYPE, xMLComponentManager.getFeature(DISALLOW_DOCTYPE));
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
        try {
            object = xMLComponentManager.getProperty(SECURITY_MANAGER);
            if (object != null) {
                this.fSchemaParser.setProperty(SECURITY_MANAGER, object);
            }
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            // empty catch block
        }
    }

    void traverseLocalElements() {
        this.fElementTraverser.fDeferTraversingLocalElements = false;
        for (int i = 0; i < this.fLocalElemStackPos; ++i) {
            Element element = this.fLocalElementDecl[i];
            XSDocumentInfo xSDocumentInfo = this.fLocalElementDecl_schema[i];
            SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(xSDocumentInfo.fTargetNamespace);
            this.fElementTraverser.traverseLocal(this.fParticle[i], element, xSDocumentInfo, schemaGrammar, this.fAllContext[i], this.fParent[i], this.fLocalElemNamespaceContext[i]);
            if (this.fParticle[i].fType != 0) continue;
            XSModelGroupImpl xSModelGroupImpl = null;
            if (this.fParent[i] instanceof XSComplexTypeDecl) {
                XSParticle xSParticle = ((XSComplexTypeDecl)this.fParent[i]).getParticle();
                if (xSParticle != null) {
                    xSModelGroupImpl = (XSModelGroupImpl)xSParticle.getTerm();
                }
            } else {
                xSModelGroupImpl = ((XSGroupDecl)this.fParent[i]).fModelGroup;
            }
            if (xSModelGroupImpl == null) continue;
            this.removeParticle(xSModelGroupImpl, this.fParticle[i]);
        }
    }

    private boolean removeParticle(XSModelGroupImpl xSModelGroupImpl, XSParticleDecl xSParticleDecl) {
        for (int i = 0; i < xSModelGroupImpl.fParticleCount; ++i) {
            XSParticleDecl xSParticleDecl2 = xSModelGroupImpl.fParticles[i];
            if (xSParticleDecl2 == xSParticleDecl) {
                for (int j = i; j < xSModelGroupImpl.fParticleCount - 1; ++j) {
                    xSModelGroupImpl.fParticles[j] = xSModelGroupImpl.fParticles[j + 1];
                }
                --xSModelGroupImpl.fParticleCount;
                return true;
            }
            if (xSParticleDecl2.fType != 3 || !this.removeParticle((XSModelGroupImpl)xSParticleDecl2.fValue, xSParticleDecl)) continue;
            return true;
        }
        return false;
    }

    void fillInLocalElemInfo(Element element, XSDocumentInfo xSDocumentInfo, int n, XSObject xSObject, XSParticleDecl xSParticleDecl) {
        if (this.fParticle.length == this.fLocalElemStackPos) {
            XSParticleDecl[] xSParticleDeclArray = new XSParticleDecl[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fParticle, 0, xSParticleDeclArray, 0, this.fLocalElemStackPos);
            this.fParticle = xSParticleDeclArray;
            Element[] elementArray = new Element[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fLocalElementDecl, 0, elementArray, 0, this.fLocalElemStackPos);
            this.fLocalElementDecl = elementArray;
            XSDocumentInfo[] xSDocumentInfoArray = new XSDocumentInfo[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fLocalElementDecl_schema, 0, xSDocumentInfoArray, 0, this.fLocalElemStackPos);
            this.fLocalElementDecl_schema = xSDocumentInfoArray;
            int[] nArray = new int[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fAllContext, 0, nArray, 0, this.fLocalElemStackPos);
            this.fAllContext = nArray;
            XSObject[] xSObjectArray = new XSObject[this.fLocalElemStackPos + 10];
            System.arraycopy(this.fParent, 0, xSObjectArray, 0, this.fLocalElemStackPos);
            this.fParent = xSObjectArray;
            String[][] stringArrayArray = new String[this.fLocalElemStackPos + 10][];
            System.arraycopy(this.fLocalElemNamespaceContext, 0, stringArrayArray, 0, this.fLocalElemStackPos);
            this.fLocalElemNamespaceContext = stringArrayArray;
        }
        this.fParticle[this.fLocalElemStackPos] = xSParticleDecl;
        this.fLocalElementDecl[this.fLocalElemStackPos] = element;
        this.fLocalElementDecl_schema[this.fLocalElemStackPos] = xSDocumentInfo;
        this.fAllContext[this.fLocalElemStackPos] = n;
        this.fParent[this.fLocalElemStackPos] = xSObject;
        this.fLocalElemNamespaceContext[this.fLocalElemStackPos++] = xSDocumentInfo.fNamespaceSupport.getEffectiveLocalContext();
    }

    void checkForDuplicateNames(String string, int n, Hashtable hashtable, Hashtable hashtable2, Element element, XSDocumentInfo xSDocumentInfo) {
        Object var7_7 = null;
        Object v = hashtable.get(string);
        var7_7 = v;
        if (v == null) {
            if (this.fNamespaceGrowth && !this.fTolerateDuplicates) {
                this.checkForDuplicateNames(string, n, element);
            }
            hashtable.put(string, element);
            hashtable2.put(string, xSDocumentInfo);
        } else {
            Element element2 = var7_7;
            XSDocumentInfo xSDocumentInfo2 = (XSDocumentInfo)hashtable2.get(string);
            if (element2 == element) {
                return;
            }
            Element element3 = null;
            XSDocumentInfo xSDocumentInfo3 = null;
            boolean bl = true;
            element3 = DOMUtil.getParent(element2);
            if (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_REDEFINE)) {
                xSDocumentInfo3 = (XSDocumentInfo)this.fRedefine2XSDMap.get(element3);
            } else if (DOMUtil.getLocalName(DOMUtil.getParent(element)).equals(SchemaSymbols.ELT_REDEFINE)) {
                xSDocumentInfo3 = xSDocumentInfo2;
                bl = false;
            }
            if (xSDocumentInfo3 != null) {
                if (xSDocumentInfo2 == xSDocumentInfo) {
                    this.reportSchemaError("sch-props-correct.2", new Object[]{string}, element);
                    return;
                }
                String string2 = string.substring(string.lastIndexOf(44) + 1) + REDEF_IDENTIFIER;
                if (xSDocumentInfo3 == xSDocumentInfo) {
                    element.setAttribute(SchemaSymbols.ATT_NAME, string2);
                    if (xSDocumentInfo.fTargetNamespace == null) {
                        hashtable.put("," + string2, element);
                        hashtable2.put("," + string2, xSDocumentInfo);
                    } else {
                        hashtable.put(xSDocumentInfo.fTargetNamespace + "," + string2, element);
                        hashtable2.put(xSDocumentInfo.fTargetNamespace + "," + string2, xSDocumentInfo);
                    }
                    if (xSDocumentInfo.fTargetNamespace == null) {
                        this.checkForDuplicateNames("," + string2, n, hashtable, hashtable2, element, xSDocumentInfo);
                    } else {
                        this.checkForDuplicateNames(xSDocumentInfo.fTargetNamespace + "," + string2, n, hashtable, hashtable2, element, xSDocumentInfo);
                    }
                } else if (bl) {
                    if (xSDocumentInfo.fTargetNamespace == null) {
                        this.checkForDuplicateNames("," + string2, n, hashtable, hashtable2, element, xSDocumentInfo);
                    } else {
                        this.checkForDuplicateNames(xSDocumentInfo.fTargetNamespace + "," + string2, n, hashtable, hashtable2, element, xSDocumentInfo);
                    }
                } else {
                    this.reportSchemaError("sch-props-correct.2", new Object[]{string}, element);
                }
            } else if (!this.fTolerateDuplicates || this.fUnparsedRegistriesExt[n].get(string) == xSDocumentInfo) {
                this.reportSchemaError("sch-props-correct.2", new Object[]{string}, element);
            }
        }
        if (this.fTolerateDuplicates) {
            this.fUnparsedRegistriesExt[n].put(string, xSDocumentInfo);
        }
    }

    void checkForDuplicateNames(String string, int n, Element element) {
        Object object;
        int n2 = string.indexOf(44);
        String string2 = string.substring(0, n2);
        SchemaGrammar schemaGrammar = this.fGrammarBucket.getGrammar(this.emptyString2Null(string2));
        if (schemaGrammar != null && (object = this.getGlobalDeclFromGrammar(schemaGrammar, n, string.substring(n2 + 1))) != null) {
            this.reportSchemaError("sch-props-correct.2", new Object[]{string}, element);
        }
    }

    private void renameRedefiningComponents(XSDocumentInfo xSDocumentInfo, Element element, String string, String string2, String string3) {
        if (string.equals(SchemaSymbols.ELT_SIMPLETYPE)) {
            Element element2 = DOMUtil.getFirstChildElement(element);
            if (element2 == null) {
                this.reportSchemaError("src-redefine.5.a.a", null, element);
            } else {
                String string4 = DOMUtil.getLocalName(element2);
                if (string4.equals(SchemaSymbols.ELT_ANNOTATION)) {
                    element2 = DOMUtil.getNextSiblingElement(element2);
                }
                if (element2 == null) {
                    this.reportSchemaError("src-redefine.5.a.a", null, element);
                } else {
                    string4 = DOMUtil.getLocalName(element2);
                    if (!string4.equals(SchemaSymbols.ELT_RESTRICTION)) {
                        this.reportSchemaError("src-redefine.5.a.b", new Object[]{string4}, element);
                    } else {
                        Object[] objectArray = this.fAttributeChecker.checkAttributes(element2, false, xSDocumentInfo);
                        QName qName = (QName)objectArray[XSAttributeChecker.ATTIDX_BASE];
                        if (qName == null || qName.uri != xSDocumentInfo.fTargetNamespace || !qName.localpart.equals(string2)) {
                            this.reportSchemaError("src-redefine.5.a.c", new Object[]{string4, (xSDocumentInfo.fTargetNamespace == null ? "" : xSDocumentInfo.fTargetNamespace) + "," + string2}, element);
                        } else if (qName.prefix != null && qName.prefix.length() > 0) {
                            element2.setAttribute(SchemaSymbols.ATT_BASE, qName.prefix + ":" + string3);
                        } else {
                            element2.setAttribute(SchemaSymbols.ATT_BASE, string3);
                        }
                        this.fAttributeChecker.returnAttrArray(objectArray, xSDocumentInfo);
                    }
                }
            }
        } else if (string.equals(SchemaSymbols.ELT_COMPLEXTYPE)) {
            Element element3 = DOMUtil.getFirstChildElement(element);
            if (element3 == null) {
                this.reportSchemaError("src-redefine.5.b.a", null, element);
            } else {
                if (DOMUtil.getLocalName(element3).equals(SchemaSymbols.ELT_ANNOTATION)) {
                    element3 = DOMUtil.getNextSiblingElement(element3);
                }
                if (element3 == null) {
                    this.reportSchemaError("src-redefine.5.b.a", null, element);
                } else {
                    Element element4 = DOMUtil.getFirstChildElement(element3);
                    if (element4 == null) {
                        this.reportSchemaError("src-redefine.5.b.b", null, element3);
                    } else {
                        String string5 = DOMUtil.getLocalName(element4);
                        if (string5.equals(SchemaSymbols.ELT_ANNOTATION)) {
                            element4 = DOMUtil.getNextSiblingElement(element4);
                        }
                        if (element4 == null) {
                            this.reportSchemaError("src-redefine.5.b.b", null, element3);
                        } else {
                            string5 = DOMUtil.getLocalName(element4);
                            if (!string5.equals(SchemaSymbols.ELT_RESTRICTION) && !string5.equals(SchemaSymbols.ELT_EXTENSION)) {
                                this.reportSchemaError("src-redefine.5.b.c", new Object[]{string5}, element4);
                            } else {
                                Object[] objectArray = this.fAttributeChecker.checkAttributes(element4, false, xSDocumentInfo);
                                QName qName = (QName)objectArray[XSAttributeChecker.ATTIDX_BASE];
                                if (qName == null || qName.uri != xSDocumentInfo.fTargetNamespace || !qName.localpart.equals(string2)) {
                                    this.reportSchemaError("src-redefine.5.b.d", new Object[]{string5, (xSDocumentInfo.fTargetNamespace == null ? "" : xSDocumentInfo.fTargetNamespace) + "," + string2}, element4);
                                } else if (qName.prefix != null && qName.prefix.length() > 0) {
                                    element4.setAttribute(SchemaSymbols.ATT_BASE, qName.prefix + ":" + string3);
                                } else {
                                    element4.setAttribute(SchemaSymbols.ATT_BASE, string3);
                                }
                            }
                        }
                    }
                }
            }
        } else if (string.equals(SchemaSymbols.ELT_ATTRIBUTEGROUP)) {
            String string6 = xSDocumentInfo.fTargetNamespace == null ? "," + string2 : xSDocumentInfo.fTargetNamespace + "," + string2;
            int n = this.changeRedefineGroup(string6, string, string3, element, xSDocumentInfo);
            if (n > 1) {
                this.reportSchemaError("src-redefine.7.1", new Object[]{new Integer(n)}, element);
            } else if (n != 1) {
                if (xSDocumentInfo.fTargetNamespace == null) {
                    this.fRedefinedRestrictedAttributeGroupRegistry.put(string6, "," + string3);
                } else {
                    this.fRedefinedRestrictedAttributeGroupRegistry.put(string6, xSDocumentInfo.fTargetNamespace + "," + string3);
                }
            }
        } else if (string.equals(SchemaSymbols.ELT_GROUP)) {
            String string7 = xSDocumentInfo.fTargetNamespace == null ? "," + string2 : xSDocumentInfo.fTargetNamespace + "," + string2;
            int n = this.changeRedefineGroup(string7, string, string3, element, xSDocumentInfo);
            if (n > 1) {
                this.reportSchemaError("src-redefine.6.1.1", new Object[]{new Integer(n)}, element);
            } else if (n != 1) {
                if (xSDocumentInfo.fTargetNamespace == null) {
                    this.fRedefinedRestrictedGroupRegistry.put(string7, "," + string3);
                } else {
                    this.fRedefinedRestrictedGroupRegistry.put(string7, xSDocumentInfo.fTargetNamespace + "," + string3);
                }
            }
        } else {
            this.reportSchemaError("Internal-Error", new Object[]{"could not handle this particular <redefine>; please submit your schemas and instance document in a bug report!"}, element);
        }
    }

    private String findQName(String string, XSDocumentInfo xSDocumentInfo) {
        String string2;
        SchemaNamespaceSupport schemaNamespaceSupport = xSDocumentInfo.fNamespaceSupport;
        int n = string.indexOf(58);
        String string3 = XMLSymbols.EMPTY_STRING;
        if (n > 0) {
            string3 = string.substring(0, n);
        }
        String string4 = schemaNamespaceSupport.getURI(this.fSymbolTable.addSymbol(string3));
        String string5 = string2 = n == 0 ? string : string.substring(n + 1);
        if (string3 == XMLSymbols.EMPTY_STRING && string4 == null && xSDocumentInfo.fIsChameleonSchema) {
            string4 = xSDocumentInfo.fTargetNamespace;
        }
        if (string4 == null) {
            return "," + string2;
        }
        return string4 + "," + string2;
    }

    private int changeRedefineGroup(String string, String string2, String string3, Element element, XSDocumentInfo xSDocumentInfo) {
        int n = 0;
        Element element2 = DOMUtil.getFirstChildElement(element);
        while (element2 != null) {
            String string4 = DOMUtil.getLocalName(element2);
            if (!string4.equals(string2)) {
                n += this.changeRedefineGroup(string, string2, string3, element2, xSDocumentInfo);
            } else {
                String string5;
                String string6 = element2.getAttribute(SchemaSymbols.ATT_REF);
                if (string6.length() != 0 && string.equals(string5 = this.findQName(string6, xSDocumentInfo))) {
                    String string7 = XMLSymbols.EMPTY_STRING;
                    int n2 = string6.indexOf(":");
                    if (n2 > 0) {
                        string7 = string6.substring(0, n2);
                        element2.setAttribute(SchemaSymbols.ATT_REF, string7 + ":" + string3);
                    } else {
                        element2.setAttribute(SchemaSymbols.ATT_REF, string3);
                    }
                    ++n;
                    if (string2.equals(SchemaSymbols.ELT_GROUP)) {
                        String string8 = element2.getAttribute(SchemaSymbols.ATT_MINOCCURS);
                        String string9 = element2.getAttribute(SchemaSymbols.ATT_MAXOCCURS);
                        if (string9.length() != 0 && !string9.equals("1") || string8.length() != 0 && !string8.equals("1")) {
                            this.reportSchemaError("src-redefine.6.1.2", new Object[]{string6}, element2);
                        }
                    }
                }
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        return n;
    }

    private XSDocumentInfo findXSDocumentForDecl(XSDocumentInfo xSDocumentInfo, Element element, XSDocumentInfo xSDocumentInfo2) {
        XSDocumentInfo xSDocumentInfo3 = xSDocumentInfo2;
        if (xSDocumentInfo3 == null) {
            return null;
        }
        XSDocumentInfo xSDocumentInfo4 = xSDocumentInfo3;
        return xSDocumentInfo4;
    }

    private boolean nonAnnotationContent(Element element) {
        Element element2 = DOMUtil.getFirstChildElement(element);
        while (element2 != null) {
            if (!DOMUtil.getLocalName(element2).equals(SchemaSymbols.ELT_ANNOTATION)) {
                return true;
            }
            element2 = DOMUtil.getNextSiblingElement(element2);
        }
        return false;
    }

    private void setSchemasVisible(XSDocumentInfo xSDocumentInfo) {
        if (DOMUtil.isHidden(xSDocumentInfo.fSchemaElement, this.fHiddenNodes)) {
            DOMUtil.setVisible(xSDocumentInfo.fSchemaElement, this.fHiddenNodes);
            Vector vector = (Vector)this.fDependencyMap.get(xSDocumentInfo);
            for (int i = 0; i < vector.size(); ++i) {
                this.setSchemasVisible((XSDocumentInfo)vector.elementAt(i));
            }
        }
    }

    public SimpleLocator element2Locator(Element element) {
        if (!(element instanceof ElementImpl)) {
            return null;
        }
        SimpleLocator simpleLocator = new SimpleLocator();
        return this.element2Locator(element, simpleLocator) ? simpleLocator : null;
    }

    public boolean element2Locator(Element element, SimpleLocator simpleLocator) {
        if (simpleLocator == null) {
            return false;
        }
        if (element instanceof ElementImpl) {
            ElementImpl elementImpl = (ElementImpl)element;
            Document document = elementImpl.getOwnerDocument();
            String string = (String)this.fDoc2SystemId.get(DOMUtil.getRoot(document));
            int n = elementImpl.getLineNumber();
            int n2 = elementImpl.getColumnNumber();
            simpleLocator.setValues(string, string, n, n2, elementImpl.getCharacterOffset());
            return true;
        }
        return false;
    }

    void reportSchemaError(String string, Object[] objectArray, Element element) {
        this.reportSchemaError(string, objectArray, element, null);
    }

    void reportSchemaError(String string, Object[] objectArray, Element element, Exception exception) {
        if (this.element2Locator(element, this.xl)) {
            this.fErrorReporter.reportError(this.xl, "http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)1, exception);
        } else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)1, exception);
        }
    }

    void reportSchemaWarning(String string, Object[] objectArray, Element element) {
        this.reportSchemaWarning(string, objectArray, element, null);
    }

    void reportSchemaWarning(String string, Object[] objectArray, Element element, Exception exception) {
        if (this.element2Locator(element, this.xl)) {
            this.fErrorReporter.reportError(this.xl, "http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)0, exception);
        } else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/xml-schema-1", string, objectArray, (short)0, exception);
        }
    }

    public void setGenerateSyntheticAnnotations(boolean bl) {
        this.fSchemaParser.setFeature(GENERATE_SYNTHETIC_ANNOTATIONS, bl);
    }

    private static final class SAX2XNIUtil
    extends ErrorHandlerWrapper {
        private SAX2XNIUtil() {
        }

        public static XMLParseException createXMLParseException0(SAXParseException sAXParseException) {
            return SAX2XNIUtil.createXMLParseException(sAXParseException);
        }

        public static XNIException createXNIException0(SAXException sAXException) {
            return SAX2XNIUtil.createXNIException(sAXException);
        }
    }

    private static class XSDKey {
        String systemId;
        short referType;
        String referNS;

        XSDKey(String string, short s, String string2) {
            this.systemId = string;
            this.referType = s;
            this.referNS = string2;
        }

        public int hashCode() {
            return this.referNS == null ? 0 : this.referNS.hashCode();
        }

        public boolean equals(Object object) {
            if (!(object instanceof XSDKey)) {
                return false;
            }
            XSDKey xSDKey = (XSDKey)object;
            if (this.referNS != xSDKey.referNS) {
                return false;
            }
            return this.systemId != null && this.systemId.equals(xSDKey.systemId);
        }
    }

    private static class XSAnnotationGrammarPool
    implements XMLGrammarPool {
        private XSGrammarBucket fGrammarBucket;
        private Grammar[] fInitialGrammarSet;

        private XSAnnotationGrammarPool() {
        }

        @Override
        public Grammar[] retrieveInitialGrammarSet(String string) {
            if (string == "http://www.w3.org/2001/XMLSchema") {
                if (this.fInitialGrammarSet == null) {
                    if (this.fGrammarBucket == null) {
                        this.fInitialGrammarSet = new Grammar[]{SchemaGrammar.Schema4Annotations.INSTANCE};
                    } else {
                        SchemaGrammar[] schemaGrammarArray = this.fGrammarBucket.getGrammars();
                        for (int i = 0; i < schemaGrammarArray.length; ++i) {
                            if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(schemaGrammarArray[i].getTargetNamespace())) continue;
                            this.fInitialGrammarSet = schemaGrammarArray;
                            return this.fInitialGrammarSet;
                        }
                        Grammar[] grammarArray = new Grammar[schemaGrammarArray.length + 1];
                        System.arraycopy(schemaGrammarArray, 0, grammarArray, 0, schemaGrammarArray.length);
                        grammarArray[grammarArray.length - 1] = SchemaGrammar.Schema4Annotations.INSTANCE;
                        this.fInitialGrammarSet = grammarArray;
                    }
                }
                return this.fInitialGrammarSet;
            }
            return new Grammar[0];
        }

        @Override
        public void cacheGrammars(String string, Grammar[] grammarArray) {
        }

        @Override
        public Grammar retrieveGrammar(XMLGrammarDescription xMLGrammarDescription) {
            if (xMLGrammarDescription.getGrammarType() == "http://www.w3.org/2001/XMLSchema") {
                SchemaGrammar schemaGrammar;
                String string = ((XMLSchemaDescription)xMLGrammarDescription).getTargetNamespace();
                if (this.fGrammarBucket != null && (schemaGrammar = this.fGrammarBucket.getGrammar(string)) != null) {
                    return schemaGrammar;
                }
                if (SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(string)) {
                    return SchemaGrammar.Schema4Annotations.INSTANCE;
                }
            }
            return null;
        }

        public void refreshGrammars(XSGrammarBucket xSGrammarBucket) {
            this.fGrammarBucket = xSGrammarBucket;
            this.fInitialGrammarSet = null;
        }

        @Override
        public void lockPool() {
        }

        @Override
        public void unlockPool() {
        }

        @Override
        public void clear() {
        }
    }
}

