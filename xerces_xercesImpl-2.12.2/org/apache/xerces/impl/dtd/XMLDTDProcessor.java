/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dtd;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.xerces.impl.XMLErrorReporter;
import org.apache.xerces.impl.dtd.DTDGrammar;
import org.apache.xerces.impl.dtd.DTDGrammarBucket;
import org.apache.xerces.impl.dtd.XMLContentSpec;
import org.apache.xerces.impl.dtd.XMLDTDValidator;
import org.apache.xerces.impl.dtd.XMLEntityDecl;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.Augmentations;
import org.apache.xerces.xni.XMLDTDContentModelHandler;
import org.apache.xerces.xni.XMLDTDHandler;
import org.apache.xerces.xni.XMLLocator;
import org.apache.xerces.xni.XMLResourceIdentifier;
import org.apache.xerces.xni.XMLString;
import org.apache.xerces.xni.XNIException;
import org.apache.xerces.xni.grammars.Grammar;
import org.apache.xerces.xni.grammars.XMLGrammarPool;
import org.apache.xerces.xni.parser.XMLComponent;
import org.apache.xerces.xni.parser.XMLComponentManager;
import org.apache.xerces.xni.parser.XMLConfigurationException;
import org.apache.xerces.xni.parser.XMLDTDContentModelFilter;
import org.apache.xerces.xni.parser.XMLDTDContentModelSource;
import org.apache.xerces.xni.parser.XMLDTDFilter;
import org.apache.xerces.xni.parser.XMLDTDSource;

public class XMLDTDProcessor
implements XMLComponent,
XMLDTDFilter,
XMLDTDContentModelFilter {
    private static final int TOP_LEVEL_SCOPE = -1;
    protected static final String VALIDATION = "http://xml.org/sax/features/validation";
    protected static final String NOTIFY_CHAR_REFS = "http://apache.org/xml/features/scanner/notify-char-refs";
    protected static final String WARN_ON_DUPLICATE_ATTDEF = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    protected static final String WARN_ON_UNDECLARED_ELEMDEF = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected static final String SYMBOL_TABLE = "http://apache.org/xml/properties/internal/symbol-table";
    protected static final String ERROR_REPORTER = "http://apache.org/xml/properties/internal/error-reporter";
    protected static final String GRAMMAR_POOL = "http://apache.org/xml/properties/internal/grammar-pool";
    protected static final String DTD_VALIDATOR = "http://apache.org/xml/properties/internal/validator/dtd";
    private static final String[] RECOGNIZED_FEATURES = new String[]{"http://xml.org/sax/features/validation", "http://apache.org/xml/features/validation/warn-on-duplicate-attdef", "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef", "http://apache.org/xml/features/scanner/notify-char-refs"};
    private static final Boolean[] FEATURE_DEFAULTS = new Boolean[]{null, Boolean.FALSE, Boolean.FALSE, null};
    private static final String[] RECOGNIZED_PROPERTIES = new String[]{"http://apache.org/xml/properties/internal/symbol-table", "http://apache.org/xml/properties/internal/error-reporter", "http://apache.org/xml/properties/internal/grammar-pool", "http://apache.org/xml/properties/internal/validator/dtd"};
    private static final Object[] PROPERTY_DEFAULTS = new Object[]{null, null, null, null};
    protected boolean fValidation;
    protected boolean fDTDValidation;
    protected boolean fWarnDuplicateAttdef;
    protected boolean fWarnOnUndeclaredElemdef;
    protected SymbolTable fSymbolTable;
    protected XMLErrorReporter fErrorReporter;
    protected DTDGrammarBucket fGrammarBucket;
    protected XMLDTDValidator fValidator;
    protected XMLGrammarPool fGrammarPool;
    protected Locale fLocale;
    protected XMLDTDHandler fDTDHandler;
    protected XMLDTDSource fDTDSource;
    protected XMLDTDContentModelHandler fDTDContentModelHandler;
    protected XMLDTDContentModelSource fDTDContentModelSource;
    protected DTDGrammar fDTDGrammar;
    private boolean fPerformValidation;
    protected boolean fInDTDIgnore;
    private boolean fMixed;
    private final XMLEntityDecl fEntityDecl = new XMLEntityDecl();
    private final HashMap fNDataDeclNotations = new HashMap();
    private String fDTDElementDeclName = null;
    private final ArrayList fMixedElementTypes = new ArrayList();
    private final ArrayList fDTDElementDecls = new ArrayList();
    private HashMap fTableOfIDAttributeNames;
    private HashMap fTableOfNOTATIONAttributeNames;
    private HashMap fNotationEnumVals;

    @Override
    public void reset(XMLComponentManager xMLComponentManager) throws XMLConfigurationException {
        boolean bl;
        try {
            bl = xMLComponentManager.getFeature(PARSER_SETTINGS);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            bl = true;
        }
        if (!bl) {
            this.reset();
            return;
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
            this.fWarnDuplicateAttdef = xMLComponentManager.getFeature(WARN_ON_DUPLICATE_ATTDEF);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fWarnDuplicateAttdef = false;
        }
        try {
            this.fWarnOnUndeclaredElemdef = xMLComponentManager.getFeature(WARN_ON_UNDECLARED_ELEMDEF);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fWarnOnUndeclaredElemdef = false;
        }
        this.fErrorReporter = (XMLErrorReporter)xMLComponentManager.getProperty(ERROR_REPORTER);
        this.fSymbolTable = (SymbolTable)xMLComponentManager.getProperty(SYMBOL_TABLE);
        try {
            this.fGrammarPool = (XMLGrammarPool)xMLComponentManager.getProperty(GRAMMAR_POOL);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fGrammarPool = null;
        }
        try {
            this.fValidator = (XMLDTDValidator)xMLComponentManager.getProperty(DTD_VALIDATOR);
        }
        catch (XMLConfigurationException xMLConfigurationException) {
            this.fValidator = null;
        }
        catch (ClassCastException classCastException) {
            this.fValidator = null;
        }
        this.fGrammarBucket = this.fValidator != null ? this.fValidator.getGrammarBucket() : null;
        this.reset();
    }

    protected void reset() {
        this.fDTDGrammar = null;
        this.fInDTDIgnore = false;
        this.fNDataDeclNotations.clear();
        if (this.fValidation) {
            if (this.fNotationEnumVals == null) {
                this.fNotationEnumVals = new HashMap();
            }
            this.fNotationEnumVals.clear();
            this.fTableOfIDAttributeNames = new HashMap();
            this.fTableOfNOTATIONAttributeNames = new HashMap();
        }
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
    public void setDTDHandler(XMLDTDHandler xMLDTDHandler) {
        this.fDTDHandler = xMLDTDHandler;
    }

    @Override
    public XMLDTDHandler getDTDHandler() {
        return this.fDTDHandler;
    }

    @Override
    public void setDTDContentModelHandler(XMLDTDContentModelHandler xMLDTDContentModelHandler) {
        this.fDTDContentModelHandler = xMLDTDContentModelHandler;
    }

    @Override
    public XMLDTDContentModelHandler getDTDContentModelHandler() {
        return this.fDTDContentModelHandler;
    }

    @Override
    public void startExternalSubset(XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startExternalSubset(xMLResourceIdentifier, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startExternalSubset(xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void endExternalSubset(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endExternalSubset(augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endExternalSubset(augmentations);
        }
    }

    protected static void checkStandaloneEntityRef(String string, DTDGrammar dTDGrammar, XMLEntityDecl xMLEntityDecl, XMLErrorReporter xMLErrorReporter) throws XNIException {
        int n = dTDGrammar.getEntityDeclIndex(string);
        if (n > -1) {
            dTDGrammar.getEntityDecl(n, xMLEntityDecl);
            if (xMLEntityDecl.inExternal) {
                xMLErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_REFERENCE_TO_EXTERNALLY_DECLARED_ENTITY_WHEN_STANDALONE", new Object[]{string}, (short)1);
            }
        }
    }

    @Override
    public void comment(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.comment(xMLString, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.comment(xMLString, augmentations);
        }
    }

    @Override
    public void processingInstruction(String string, XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.processingInstruction(string, xMLString, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.processingInstruction(string, xMLString, augmentations);
        }
    }

    @Override
    public void startDTD(XMLLocator xMLLocator, Augmentations augmentations) throws XNIException {
        this.fNDataDeclNotations.clear();
        this.fDTDElementDecls.clear();
        if (!this.fGrammarBucket.getActiveGrammar().isImmutable()) {
            this.fDTDGrammar = this.fGrammarBucket.getActiveGrammar();
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startDTD(xMLLocator, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startDTD(xMLLocator, augmentations);
        }
    }

    @Override
    public void ignoredCharacters(XMLString xMLString, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.ignoredCharacters(xMLString, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.ignoredCharacters(xMLString, augmentations);
        }
    }

    @Override
    public void textDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.textDecl(string, string2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.textDecl(string, string2, augmentations);
        }
    }

    @Override
    public void startParameterEntity(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fPerformValidation && this.fDTDGrammar != null && this.fGrammarBucket.getStandalone()) {
            XMLDTDProcessor.checkStandaloneEntityRef(string, this.fDTDGrammar, this.fEntityDecl, this.fErrorReporter);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startParameterEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startParameterEntity(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void endParameterEntity(String string, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endParameterEntity(string, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endParameterEntity(string, augmentations);
        }
    }

    @Override
    public void elementDecl(String string, String string2, Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            if (this.fDTDElementDecls.contains(string)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ELEMENT_ALREADY_DECLARED", new Object[]{string}, (short)1);
            } else {
                this.fDTDElementDecls.add(string);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.elementDecl(string, string2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.elementDecl(string, string2, augmentations);
        }
    }

    @Override
    public void startAttlist(String string, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startAttlist(string, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startAttlist(string, augmentations);
        }
    }

    @Override
    public void attributeDecl(String string, String string2, String string3, String[] stringArray, String string4, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        if (string3 != XMLSymbols.fCDATASymbol && xMLString != null) {
            this.normalizeDefaultAttrValue(xMLString);
        }
        if (this.fValidation) {
            int n;
            boolean bl = false;
            DTDGrammar dTDGrammar = this.fDTDGrammar != null ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            if (dTDGrammar.getAttributeDeclIndex(n = dTDGrammar.getElementDeclIndex(string), string2) != -1) {
                bl = true;
                if (this.fWarnDuplicateAttdef) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_DUPLICATE_ATTRIBUTE_DEFINITION", new Object[]{string, string2}, (short)0);
                }
            }
            if (string3 == XMLSymbols.fIDSymbol) {
                if (xMLString != null && xMLString.length != 0 && (string4 == null || string4 != XMLSymbols.fIMPLIEDSymbol && string4 != XMLSymbols.fREQUIREDSymbol)) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "IDDefaultTypeInvalid", new Object[]{string2}, (short)1);
                }
                if (!this.fTableOfIDAttributeNames.containsKey(string)) {
                    this.fTableOfIDAttributeNames.put(string, string2);
                } else if (!bl) {
                    String string5 = (String)this.fTableOfIDAttributeNames.get(string);
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_ID_ATTRIBUTE", new Object[]{string, string5, string2}, (short)1);
                }
            }
            if (string3 == XMLSymbols.fNOTATIONSymbol) {
                for (int i = 0; i < stringArray.length; ++i) {
                    this.fNotationEnumVals.put(stringArray[i], string2);
                }
                if (!this.fTableOfNOTATIONAttributeNames.containsKey(string)) {
                    this.fTableOfNOTATIONAttributeNames.put(string, string2);
                } else if (!bl) {
                    String string6 = (String)this.fTableOfNOTATIONAttributeNames.get(string);
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_MORE_THAN_ONE_NOTATION_ATTRIBUTE", new Object[]{string, string6, string2}, (short)1);
                }
            }
            if (string3 == XMLSymbols.fENUMERATIONSymbol || string3 == XMLSymbols.fNOTATIONSymbol) {
                block1: for (int i = 0; i < stringArray.length; ++i) {
                    for (int j = i + 1; j < stringArray.length; ++j) {
                        if (!stringArray[i].equals(stringArray[j])) continue;
                        this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", string3 == XMLSymbols.fENUMERATIONSymbol ? "MSG_DISTINCT_TOKENS_IN_ENUMERATION" : "MSG_DISTINCT_NOTATION_IN_ENUMERATION", new Object[]{string, stringArray[i], string2}, (short)1);
                        break block1;
                    }
                }
            }
            boolean bl2 = true;
            if (xMLString != null && (string4 == null || string4 != null && string4 == XMLSymbols.fFIXEDSymbol)) {
                String string7 = xMLString.toString();
                if (string3 == XMLSymbols.fNMTOKENSSymbol || string3 == XMLSymbols.fENTITIESSymbol || string3 == XMLSymbols.fIDREFSSymbol) {
                    StringTokenizer stringTokenizer = new StringTokenizer(string7, " ");
                    if (stringTokenizer.hasMoreTokens()) {
                        do {
                            String string8 = stringTokenizer.nextToken();
                            if (string3 == XMLSymbols.fNMTOKENSSymbol) {
                                if (this.isValidNmtoken(string8)) continue;
                                bl2 = false;
                            } else {
                                if (string3 != XMLSymbols.fENTITIESSymbol && string3 != XMLSymbols.fIDREFSSymbol || this.isValidName(string8)) continue;
                                bl2 = false;
                            }
                            break;
                        } while (stringTokenizer.hasMoreTokens());
                    }
                } else {
                    if (string3 == XMLSymbols.fENTITYSymbol || string3 == XMLSymbols.fIDSymbol || string3 == XMLSymbols.fIDREFSymbol || string3 == XMLSymbols.fNOTATIONSymbol) {
                        if (!this.isValidName(string7)) {
                            bl2 = false;
                        }
                    } else if (!(string3 != XMLSymbols.fNMTOKENSymbol && string3 != XMLSymbols.fENUMERATIONSymbol || this.isValidNmtoken(string7))) {
                        bl2 = false;
                    }
                    if (string3 == XMLSymbols.fNOTATIONSymbol || string3 == XMLSymbols.fENUMERATIONSymbol) {
                        bl2 = false;
                        for (int i = 0; i < stringArray.length; ++i) {
                            if (!xMLString.equals(stringArray[i])) continue;
                            bl2 = true;
                        }
                    }
                }
                if (!bl2) {
                    this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_ATT_DEFAULT_INVALID", new Object[]{string2, string7}, (short)1);
                }
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.attributeDecl(string, string2, string3, stringArray, string4, xMLString, xMLString2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.attributeDecl(string, string2, string3, stringArray, string4, xMLString, xMLString2, augmentations);
        }
    }

    @Override
    public void endAttlist(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endAttlist(augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endAttlist(augmentations);
        }
    }

    @Override
    public void internalEntityDecl(String string, XMLString xMLString, XMLString xMLString2, Augmentations augmentations) throws XNIException {
        DTDGrammar dTDGrammar = this.fDTDGrammar != null ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
        int n = dTDGrammar.getEntityDeclIndex(string);
        if (n == -1) {
            if (this.fDTDGrammar != null) {
                this.fDTDGrammar.internalEntityDecl(string, xMLString, xMLString2, augmentations);
            }
            if (this.fDTDHandler != null) {
                this.fDTDHandler.internalEntityDecl(string, xMLString, xMLString2, augmentations);
            }
        }
    }

    @Override
    public void externalEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        DTDGrammar dTDGrammar = this.fDTDGrammar != null ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
        int n = dTDGrammar.getEntityDeclIndex(string);
        if (n == -1) {
            if (this.fDTDGrammar != null) {
                this.fDTDGrammar.externalEntityDecl(string, xMLResourceIdentifier, augmentations);
            }
            if (this.fDTDHandler != null) {
                this.fDTDHandler.externalEntityDecl(string, xMLResourceIdentifier, augmentations);
            }
        }
    }

    @Override
    public void unparsedEntityDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, String string2, Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            this.fNDataDeclNotations.put(string, string2);
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.unparsedEntityDecl(string, xMLResourceIdentifier, string2, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.unparsedEntityDecl(string, xMLResourceIdentifier, string2, augmentations);
        }
    }

    @Override
    public void notationDecl(String string, XMLResourceIdentifier xMLResourceIdentifier, Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            DTDGrammar dTDGrammar;
            DTDGrammar dTDGrammar2 = dTDGrammar = this.fDTDGrammar != null ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            if (dTDGrammar.getNotationDeclIndex(string) != -1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UniqueNotationName", new Object[]{string}, (short)1);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.notationDecl(string, xMLResourceIdentifier, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.notationDecl(string, xMLResourceIdentifier, augmentations);
        }
    }

    @Override
    public void startConditional(short s, Augmentations augmentations) throws XNIException {
        boolean bl = this.fInDTDIgnore = s == 1;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startConditional(s, augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.startConditional(s, augmentations);
        }
    }

    @Override
    public void endConditional(Augmentations augmentations) throws XNIException {
        this.fInDTDIgnore = false;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endConditional(augmentations);
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endConditional(augmentations);
        }
    }

    @Override
    public void endDTD(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endDTD(augmentations);
            if (this.fGrammarPool != null) {
                this.fGrammarPool.cacheGrammars("http://www.w3.org/TR/REC-xml", new Grammar[]{this.fDTDGrammar});
            }
        }
        if (this.fValidation) {
            String string;
            Map.Entry entry3;
            DTDGrammar dTDGrammar = this.fDTDGrammar != null ? this.fDTDGrammar : this.fGrammarBucket.getActiveGrammar();
            for (Map.Entry entry2 : this.fNDataDeclNotations.entrySet()) {
                String object = (String)entry2.getValue();
                if (dTDGrammar.getNotationDeclIndex(object) != -1) continue;
                entry3 = (String)entry2.getKey();
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_UNPARSED_ENTITYDECL", new Object[]{entry3, object}, (short)1);
            }
            for (Map.Entry entry2 : this.fNotationEnumVals.entrySet()) {
                entry3 = (String)entry2.getKey();
                if (dTDGrammar.getNotationDeclIndex((String)((Object)entry3)) != -1) continue;
                string = (String)entry2.getValue();
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "MSG_NOTATION_NOT_DECLARED_FOR_NOTATIONTYPE_ATTRIBUTE", new Object[]{string, entry3}, (short)1);
            }
            for (Map.Entry entry3 : this.fTableOfNOTATIONAttributeNames.entrySet()) {
                string = (String)entry3.getKey();
                int n = dTDGrammar.getElementDeclIndex(string);
                if (dTDGrammar.getContentSpecType(n) != 1) continue;
                String string2 = (String)entry3.getValue();
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "NoNotationOnEmptyElement", new Object[]{string, string2}, (short)1);
            }
            this.fTableOfIDAttributeNames = null;
            this.fTableOfNOTATIONAttributeNames = null;
            if (this.fWarnOnUndeclaredElemdef) {
                this.checkDeclaredElements(dTDGrammar);
            }
        }
        if (this.fDTDHandler != null) {
            this.fDTDHandler.endDTD(augmentations);
        }
    }

    @Override
    public void setDTDSource(XMLDTDSource xMLDTDSource) {
        this.fDTDSource = xMLDTDSource;
    }

    @Override
    public XMLDTDSource getDTDSource() {
        return this.fDTDSource;
    }

    @Override
    public void setDTDContentModelSource(XMLDTDContentModelSource xMLDTDContentModelSource) {
        this.fDTDContentModelSource = xMLDTDContentModelSource;
    }

    @Override
    public XMLDTDContentModelSource getDTDContentModelSource() {
        return this.fDTDContentModelSource;
    }

    @Override
    public void startContentModel(String string, Augmentations augmentations) throws XNIException {
        if (this.fValidation) {
            this.fDTDElementDeclName = string;
            this.fMixedElementTypes.clear();
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startContentModel(string, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startContentModel(string, augmentations);
        }
    }

    @Override
    public void any(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.any(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.any(augmentations);
        }
    }

    @Override
    public void empty(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.empty(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.empty(augmentations);
        }
    }

    @Override
    public void startGroup(Augmentations augmentations) throws XNIException {
        this.fMixed = false;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.startGroup(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.startGroup(augmentations);
        }
    }

    @Override
    public void pcdata(Augmentations augmentations) {
        this.fMixed = true;
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.pcdata(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.pcdata(augmentations);
        }
    }

    @Override
    public void element(String string, Augmentations augmentations) throws XNIException {
        if (this.fMixed && this.fValidation) {
            if (this.fMixedElementTypes.contains(string)) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "DuplicateTypeInMixedContent", new Object[]{this.fDTDElementDeclName, string}, (short)1);
            } else {
                this.fMixedElementTypes.add(string);
            }
        }
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.element(string, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.element(string, augmentations);
        }
    }

    @Override
    public void separator(short s, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.separator(s, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.separator(s, augmentations);
        }
    }

    @Override
    public void occurrence(short s, Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.occurrence(s, augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.occurrence(s, augmentations);
        }
    }

    @Override
    public void endGroup(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endGroup(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endGroup(augmentations);
        }
    }

    @Override
    public void endContentModel(Augmentations augmentations) throws XNIException {
        if (this.fDTDGrammar != null) {
            this.fDTDGrammar.endContentModel(augmentations);
        }
        if (this.fDTDContentModelHandler != null) {
            this.fDTDContentModelHandler.endContentModel(augmentations);
        }
    }

    private boolean normalizeDefaultAttrValue(XMLString xMLString) {
        boolean bl = true;
        int n = xMLString.offset;
        int n2 = xMLString.offset + xMLString.length;
        for (int i = xMLString.offset; i < n2; ++i) {
            if (xMLString.ch[i] == ' ') {
                if (bl) continue;
                xMLString.ch[n++] = 32;
                bl = true;
                continue;
            }
            if (n != i) {
                xMLString.ch[n] = xMLString.ch[i];
            }
            ++n;
            bl = false;
        }
        if (n != n2) {
            if (bl) {
                --n;
            }
            xMLString.length = n - xMLString.offset;
            return true;
        }
        return false;
    }

    protected boolean isValidNmtoken(String string) {
        return XMLChar.isValidNmtoken(string);
    }

    protected boolean isValidName(String string) {
        return XMLChar.isValidName(string);
    }

    private void checkDeclaredElements(DTDGrammar dTDGrammar) {
        int n = dTDGrammar.getFirstElementDeclIndex();
        XMLContentSpec xMLContentSpec = new XMLContentSpec();
        while (n >= 0) {
            short s = dTDGrammar.getContentSpecType(n);
            if (s == 3 || s == 2) {
                this.checkDeclaredElements(dTDGrammar, n, dTDGrammar.getContentSpecIndex(n), xMLContentSpec);
            }
            n = dTDGrammar.getNextElementDeclIndex(n);
        }
    }

    private void checkDeclaredElements(DTDGrammar dTDGrammar, int n, int n2, XMLContentSpec xMLContentSpec) {
        dTDGrammar.getContentSpec(n2, xMLContentSpec);
        if (xMLContentSpec.type == 0) {
            String string = (String)xMLContentSpec.value;
            if (string != null && dTDGrammar.getElementDeclIndex(string) == -1) {
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "UndeclaredElementInContentSpec", new Object[]{dTDGrammar.getElementDeclName((int)n).rawname, string}, (short)0);
            }
        } else if (xMLContentSpec.type == 4 || xMLContentSpec.type == 5) {
            int n3 = ((int[])xMLContentSpec.value)[0];
            int n4 = ((int[])xMLContentSpec.otherValue)[0];
            this.checkDeclaredElements(dTDGrammar, n, n3, xMLContentSpec);
            this.checkDeclaredElements(dTDGrammar, n, n4, xMLContentSpec);
        } else if (xMLContentSpec.type == 2 || xMLContentSpec.type == 1 || xMLContentSpec.type == 3) {
            int n5 = ((int[])xMLContentSpec.value)[0];
            this.checkDeclaredElements(dTDGrammar, n, n5, xMLContentSpec);
        }
    }
}

