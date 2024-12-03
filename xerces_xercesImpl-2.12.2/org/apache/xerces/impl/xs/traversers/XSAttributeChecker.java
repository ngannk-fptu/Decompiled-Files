/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.xs.traversers;

import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Vector;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.XSSimpleType;
import org.apache.xerces.impl.xs.SchemaGrammar;
import org.apache.xerces.impl.xs.SchemaNamespaceSupport;
import org.apache.xerces.impl.xs.SchemaSymbols;
import org.apache.xerces.impl.xs.XSAttributeDecl;
import org.apache.xerces.impl.xs.XSGrammarBucket;
import org.apache.xerces.impl.xs.traversers.Container;
import org.apache.xerces.impl.xs.traversers.OneAttr;
import org.apache.xerces.impl.xs.traversers.XSDHandler;
import org.apache.xerces.impl.xs.traversers.XSDocumentInfo;
import org.apache.xerces.impl.xs.util.XInt;
import org.apache.xerces.impl.xs.util.XIntPool;
import org.apache.xerces.util.DOMUtil;
import org.apache.xerces.util.SymbolTable;
import org.apache.xerces.util.XMLChar;
import org.apache.xerces.util.XMLSymbols;
import org.apache.xerces.xni.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;

public class XSAttributeChecker {
    private static final String ELEMENT_N = "element_n";
    private static final String ELEMENT_R = "element_r";
    private static final String ATTRIBUTE_N = "attribute_n";
    private static final String ATTRIBUTE_R = "attribute_r";
    private static int ATTIDX_COUNT = 0;
    public static final int ATTIDX_ABSTRACT = ATTIDX_COUNT++;
    public static final int ATTIDX_AFORMDEFAULT = ATTIDX_COUNT++;
    public static final int ATTIDX_BASE = ATTIDX_COUNT++;
    public static final int ATTIDX_BLOCK = ATTIDX_COUNT++;
    public static final int ATTIDX_BLOCKDEFAULT = ATTIDX_COUNT++;
    public static final int ATTIDX_DEFAULT = ATTIDX_COUNT++;
    public static final int ATTIDX_EFORMDEFAULT = ATTIDX_COUNT++;
    public static final int ATTIDX_FINAL = ATTIDX_COUNT++;
    public static final int ATTIDX_FINALDEFAULT = ATTIDX_COUNT++;
    public static final int ATTIDX_FIXED = ATTIDX_COUNT++;
    public static final int ATTIDX_FORM = ATTIDX_COUNT++;
    public static final int ATTIDX_ID = ATTIDX_COUNT++;
    public static final int ATTIDX_ITEMTYPE = ATTIDX_COUNT++;
    public static final int ATTIDX_MAXOCCURS = ATTIDX_COUNT++;
    public static final int ATTIDX_MEMBERTYPES = ATTIDX_COUNT++;
    public static final int ATTIDX_MINOCCURS = ATTIDX_COUNT++;
    public static final int ATTIDX_MIXED = ATTIDX_COUNT++;
    public static final int ATTIDX_NAME = ATTIDX_COUNT++;
    public static final int ATTIDX_NAMESPACE = ATTIDX_COUNT++;
    public static final int ATTIDX_NAMESPACE_LIST = ATTIDX_COUNT++;
    public static final int ATTIDX_NILLABLE = ATTIDX_COUNT++;
    public static final int ATTIDX_NONSCHEMA = ATTIDX_COUNT++;
    public static final int ATTIDX_PROCESSCONTENTS = ATTIDX_COUNT++;
    public static final int ATTIDX_PUBLIC = ATTIDX_COUNT++;
    public static final int ATTIDX_REF = ATTIDX_COUNT++;
    public static final int ATTIDX_REFER = ATTIDX_COUNT++;
    public static final int ATTIDX_SCHEMALOCATION = ATTIDX_COUNT++;
    public static final int ATTIDX_SOURCE = ATTIDX_COUNT++;
    public static final int ATTIDX_SUBSGROUP = ATTIDX_COUNT++;
    public static final int ATTIDX_SYSTEM = ATTIDX_COUNT++;
    public static final int ATTIDX_TARGETNAMESPACE = ATTIDX_COUNT++;
    public static final int ATTIDX_TYPE = ATTIDX_COUNT++;
    public static final int ATTIDX_USE = ATTIDX_COUNT++;
    public static final int ATTIDX_VALUE = ATTIDX_COUNT++;
    public static final int ATTIDX_ENUMNSDECLS = ATTIDX_COUNT++;
    public static final int ATTIDX_VERSION = ATTIDX_COUNT++;
    public static final int ATTIDX_XML_LANG = ATTIDX_COUNT++;
    public static final int ATTIDX_XPATH = ATTIDX_COUNT++;
    public static final int ATTIDX_FROMDEFAULT = ATTIDX_COUNT++;
    public static final int ATTIDX_ISRETURNED = ATTIDX_COUNT++;
    private static final XIntPool fXIntPool = new XIntPool();
    private static final XInt INT_QUALIFIED = fXIntPool.getXInt(1);
    private static final XInt INT_UNQUALIFIED = fXIntPool.getXInt(0);
    private static final XInt INT_EMPTY_SET = fXIntPool.getXInt(0);
    private static final XInt INT_ANY_STRICT = fXIntPool.getXInt(1);
    private static final XInt INT_ANY_LAX = fXIntPool.getXInt(3);
    private static final XInt INT_ANY_SKIP = fXIntPool.getXInt(2);
    private static final XInt INT_ANY_ANY = fXIntPool.getXInt(1);
    private static final XInt INT_ANY_LIST = fXIntPool.getXInt(3);
    private static final XInt INT_ANY_NOT = fXIntPool.getXInt(2);
    private static final XInt INT_USE_OPTIONAL = fXIntPool.getXInt(0);
    private static final XInt INT_USE_REQUIRED = fXIntPool.getXInt(1);
    private static final XInt INT_USE_PROHIBITED = fXIntPool.getXInt(2);
    private static final XInt INT_WS_PRESERVE = fXIntPool.getXInt(0);
    private static final XInt INT_WS_REPLACE = fXIntPool.getXInt(1);
    private static final XInt INT_WS_COLLAPSE = fXIntPool.getXInt(2);
    private static final XInt INT_UNBOUNDED = fXIntPool.getXInt(-1);
    private static final Hashtable fEleAttrsMapG = new Hashtable(29);
    private static final Hashtable fEleAttrsMapL = new Hashtable(79);
    protected static final int DT_ANYURI = 0;
    protected static final int DT_ID = 1;
    protected static final int DT_QNAME = 2;
    protected static final int DT_STRING = 3;
    protected static final int DT_TOKEN = 4;
    protected static final int DT_NCNAME = 5;
    protected static final int DT_XPATH = 6;
    protected static final int DT_XPATH1 = 7;
    protected static final int DT_LANGUAGE = 8;
    protected static final int DT_COUNT = 9;
    private static final XSSimpleType[] fExtraDVs = new XSSimpleType[9];
    protected static final int DT_BLOCK = -1;
    protected static final int DT_BLOCK1 = -2;
    protected static final int DT_FINAL = -3;
    protected static final int DT_FINAL1 = -4;
    protected static final int DT_FINAL2 = -5;
    protected static final int DT_FORM = -6;
    protected static final int DT_MAXOCCURS = -7;
    protected static final int DT_MAXOCCURS1 = -8;
    protected static final int DT_MEMBERTYPES = -9;
    protected static final int DT_MINOCCURS1 = -10;
    protected static final int DT_NAMESPACE = -11;
    protected static final int DT_PROCESSCONTENTS = -12;
    protected static final int DT_USE = -13;
    protected static final int DT_WHITESPACE = -14;
    protected static final int DT_BOOLEAN = -15;
    protected static final int DT_NONNEGINT = -16;
    protected static final int DT_POSINT = -17;
    protected XSDHandler fSchemaHandler = null;
    protected SymbolTable fSymbolTable = null;
    protected Hashtable fNonSchemaAttrs = new Hashtable();
    protected Vector fNamespaceList = new Vector();
    protected boolean[] fSeen = new boolean[ATTIDX_COUNT];
    private static boolean[] fSeenTemp;
    static final int INIT_POOL_SIZE = 10;
    static final int INC_POOL_SIZE = 10;
    Object[][] fArrayPool = new Object[10][ATTIDX_COUNT];
    private static Object[] fTempArray;
    int fPoolPos = 0;

    public XSAttributeChecker(XSDHandler xSDHandler) {
        this.fSchemaHandler = xSDHandler;
    }

    public void reset(SymbolTable symbolTable) {
        this.fSymbolTable = symbolTable;
        this.fNonSchemaAttrs.clear();
    }

    public Object[] checkAttributes(Element element, boolean bl, XSDocumentInfo xSDocumentInfo) {
        return this.checkAttributes(element, bl, xSDocumentInfo, false);
    }

    public Object[] checkAttributes(Element element, boolean bl, XSDocumentInfo xSDocumentInfo, boolean bl2) {
        int n;
        Object object;
        Container container;
        if (element == null) {
            return null;
        }
        Attr[] attrArray = DOMUtil.getAttrs(element);
        this.resolveNamespace(element, attrArray, xSDocumentInfo.fNamespaceSupport);
        String string = DOMUtil.getNamespaceURI(element);
        String string2 = DOMUtil.getLocalName(element);
        if (!SchemaSymbols.URI_SCHEMAFORSCHEMA.equals(string)) {
            this.reportSchemaError("s4s-elt-schema-ns", new Object[]{string2}, element);
        }
        Hashtable hashtable = fEleAttrsMapG;
        String string3 = string2;
        if (!bl) {
            hashtable = fEleAttrsMapL;
            if (string2.equals(SchemaSymbols.ELT_ELEMENT)) {
                string3 = DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null ? ELEMENT_R : ELEMENT_N;
            } else if (string2.equals(SchemaSymbols.ELT_ATTRIBUTE)) {
                string3 = DOMUtil.getAttr(element, SchemaSymbols.ATT_REF) != null ? ATTRIBUTE_R : ATTRIBUTE_N;
            }
        }
        if ((container = (Container)hashtable.get(string3)) == null) {
            this.reportSchemaError("s4s-elt-invalid", new Object[]{string2}, element);
            return null;
        }
        Object[] objectArray = this.getAvailableArray();
        long l = 0L;
        System.arraycopy(fSeenTemp, 0, this.fSeen, 0, ATTIDX_COUNT);
        int n2 = attrArray.length;
        Attr attr = null;
        for (int i = 0; i < n2; ++i) {
            block25: {
                Object object2;
                attr = attrArray[i];
                String string4 = attr.getName();
                object = DOMUtil.getNamespaceURI(attr);
                String string5 = DOMUtil.getValue(attr);
                if (string4.startsWith("xml")) {
                    object2 = DOMUtil.getPrefix(attr);
                    if ("xmlns".equals(object2) || "xmlns".equals(string4)) continue;
                    if (SchemaSymbols.ATT_XML_LANG.equals(string4) && (SchemaSymbols.ELT_SCHEMA.equals(string2) || SchemaSymbols.ELT_DOCUMENTATION.equals(string2))) {
                        object = null;
                    }
                }
                if (object != null && ((String)object).length() != 0) {
                    if (((String)object).equals(SchemaSymbols.URI_SCHEMAFORSCHEMA)) {
                        this.reportSchemaError("s4s-att-not-allowed", new Object[]{string2, string4}, element);
                        continue;
                    }
                    if (objectArray[ATTIDX_NONSCHEMA] == null) {
                        objectArray[XSAttributeChecker.ATTIDX_NONSCHEMA] = new Vector(4, 2);
                    }
                    ((Vector)objectArray[ATTIDX_NONSCHEMA]).addElement(string4);
                    ((Vector)objectArray[ATTIDX_NONSCHEMA]).addElement(string5);
                    continue;
                }
                object2 = container.get(string4);
                if (object2 == null) {
                    this.reportSchemaError("s4s-att-not-allowed", new Object[]{string2, string4}, element);
                    continue;
                }
                this.fSeen[((OneAttr)object2).valueIndex] = true;
                try {
                    if (((OneAttr)object2).dvIndex >= 0) {
                        if (((OneAttr)object2).dvIndex != 3 && ((OneAttr)object2).dvIndex != 6 && ((OneAttr)object2).dvIndex != 7) {
                            XSSimpleType xSSimpleType = fExtraDVs[((OneAttr)object2).dvIndex];
                            Object object3 = xSSimpleType.validate(string5, (ValidationContext)xSDocumentInfo.fValidationContext, null);
                            if (((OneAttr)object2).dvIndex == 2) {
                                QName qName = (QName)object3;
                                if (qName.prefix == XMLSymbols.EMPTY_STRING && qName.uri == null && xSDocumentInfo.fIsChameleonSchema) {
                                    qName.uri = xSDocumentInfo.fTargetNamespace;
                                }
                            }
                            objectArray[((OneAttr)object2).valueIndex] = object3;
                        } else {
                            objectArray[((OneAttr)object2).valueIndex] = string5;
                        }
                    } else {
                        objectArray[((OneAttr)object2).valueIndex] = this.validate(objectArray, string4, string5, ((OneAttr)object2).dvIndex, xSDocumentInfo);
                    }
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportSchemaError("s4s-att-invalid-value", new Object[]{string2, string4, invalidDatatypeValueException.getMessage()}, element);
                    if (((OneAttr)object2).dfltValue == null) break block25;
                    objectArray[((OneAttr)object2).valueIndex] = ((OneAttr)object2).dfltValue;
                }
            }
            if (!string2.equals(SchemaSymbols.ELT_ENUMERATION) || !bl2) continue;
            objectArray[XSAttributeChecker.ATTIDX_ENUMNSDECLS] = new SchemaNamespaceSupport(xSDocumentInfo.fNamespaceSupport);
        }
        OneAttr[] oneAttrArray = container.values;
        for (n = 0; n < oneAttrArray.length; ++n) {
            object = oneAttrArray[n];
            if (((OneAttr)object).dfltValue == null || this.fSeen[((OneAttr)object).valueIndex]) continue;
            objectArray[((OneAttr)object).valueIndex] = ((OneAttr)object).dfltValue;
            l |= (long)(1 << ((OneAttr)object).valueIndex);
        }
        objectArray[XSAttributeChecker.ATTIDX_FROMDEFAULT] = new Long(l);
        if (objectArray[ATTIDX_MAXOCCURS] != null) {
            n = ((XInt)objectArray[ATTIDX_MINOCCURS]).intValue();
            int n3 = ((XInt)objectArray[ATTIDX_MAXOCCURS]).intValue();
            if (n3 != -1 && n > n3) {
                this.reportSchemaError("p-props-correct.2.1", new Object[]{string2, objectArray[ATTIDX_MINOCCURS], objectArray[ATTIDX_MAXOCCURS]}, element);
                objectArray[XSAttributeChecker.ATTIDX_MINOCCURS] = objectArray[ATTIDX_MAXOCCURS];
            }
        }
        return objectArray;
    }

    private Object validate(Object[] objectArray, String string, String string2, int n, XSDocumentInfo xSDocumentInfo) throws InvalidDatatypeValueException {
        if (string2 == null) {
            return null;
        }
        String string3 = XMLChar.trim(string2);
        Vector<QName> vector = null;
        switch (n) {
            case -15: {
                if (string3.equals("false") || string3.equals("0")) {
                    vector = Boolean.FALSE;
                    break;
                }
                if (string3.equals("true") || string3.equals("1")) {
                    vector = Boolean.TRUE;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string3, "boolean"});
            }
            case -16: {
                try {
                    if (string3.length() > 0 && string3.charAt(0) == '+') {
                        string3 = string3.substring(1);
                    }
                    vector = fXIntPool.getXInt(Integer.parseInt(string3));
                }
                catch (NumberFormatException numberFormatException) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string3, "nonNegativeInteger"});
                }
                if (((XInt)((Object)vector)).intValue() >= 0) break;
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string3, "nonNegativeInteger"});
            }
            case -17: {
                try {
                    if (string3.length() > 0 && string3.charAt(0) == '+') {
                        string3 = string3.substring(1);
                    }
                    vector = fXIntPool.getXInt(Integer.parseInt(string3));
                }
                catch (NumberFormatException numberFormatException) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string3, "positiveInteger"});
                }
                if (((XInt)((Object)vector)).intValue() > 0) break;
                throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string3, "positiveInteger"});
            }
            case -1: {
                int n2 = 0;
                if (string3.equals("#all")) {
                    n2 = 31;
                } else {
                    StringTokenizer stringTokenizer = new StringTokenizer(string3, " \n\t\r");
                    while (stringTokenizer.hasMoreTokens()) {
                        String string4 = stringTokenizer.nextToken();
                        if (string4.equals("extension")) {
                            n2 |= 1;
                            continue;
                        }
                        if (string4.equals("restriction")) {
                            n2 |= 2;
                            continue;
                        }
                        if (string4.equals("substitution")) {
                            n2 |= 4;
                            continue;
                        }
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{string3, "(#all | List of (extension | restriction | substitution))"});
                    }
                }
                vector = fXIntPool.getXInt(n2);
                break;
            }
            case -3: 
            case -2: {
                int n3 = 0;
                if (string3.equals("#all")) {
                    n3 = 31;
                } else {
                    StringTokenizer stringTokenizer = new StringTokenizer(string3, " \n\t\r");
                    while (stringTokenizer.hasMoreTokens()) {
                        String string5 = stringTokenizer.nextToken();
                        if (string5.equals("extension")) {
                            n3 |= 1;
                            continue;
                        }
                        if (string5.equals("restriction")) {
                            n3 |= 2;
                            continue;
                        }
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{string3, "(#all | List of (extension | restriction))"});
                    }
                }
                vector = fXIntPool.getXInt(n3);
                break;
            }
            case -4: {
                int n4 = 0;
                if (string3.equals("#all")) {
                    n4 = 31;
                } else {
                    StringTokenizer stringTokenizer = new StringTokenizer(string3, " \n\t\r");
                    while (stringTokenizer.hasMoreTokens()) {
                        String string6 = stringTokenizer.nextToken();
                        if (string6.equals("list")) {
                            n4 |= 0x10;
                            continue;
                        }
                        if (string6.equals("union")) {
                            n4 |= 8;
                            continue;
                        }
                        if (string6.equals("restriction")) {
                            n4 |= 2;
                            continue;
                        }
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{string3, "(#all | List of (list | union | restriction))"});
                    }
                }
                vector = fXIntPool.getXInt(n4);
                break;
            }
            case -5: {
                int n5 = 0;
                if (string3.equals("#all")) {
                    n5 = 31;
                } else {
                    StringTokenizer stringTokenizer = new StringTokenizer(string3, " \n\t\r");
                    while (stringTokenizer.hasMoreTokens()) {
                        String string7 = stringTokenizer.nextToken();
                        if (string7.equals("extension")) {
                            n5 |= 1;
                            continue;
                        }
                        if (string7.equals("restriction")) {
                            n5 |= 2;
                            continue;
                        }
                        if (string7.equals("list")) {
                            n5 |= 0x10;
                            continue;
                        }
                        if (string7.equals("union")) {
                            n5 |= 8;
                            continue;
                        }
                        throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{string3, "(#all | List of (extension | restriction | list | union))"});
                    }
                }
                vector = fXIntPool.getXInt(n5);
                break;
            }
            case -6: {
                if (string3.equals("qualified")) {
                    vector = INT_QUALIFIED;
                    break;
                }
                if (string3.equals("unqualified")) {
                    vector = INT_UNQUALIFIED;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string3, "(qualified | unqualified)"});
            }
            case -7: {
                if (string3.equals("unbounded")) {
                    vector = INT_UNBOUNDED;
                    break;
                }
                try {
                    vector = this.validate(objectArray, string, string3, -16, xSDocumentInfo);
                    break;
                }
                catch (NumberFormatException numberFormatException) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{string3, "(nonNegativeInteger | unbounded)"});
                }
            }
            case -8: {
                if (string3.equals("1")) {
                    vector = fXIntPool.getXInt(1);
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string3, "(1)"});
            }
            case -9: {
                Vector<QName> vector2 = new Vector<QName>();
                try {
                    StringTokenizer stringTokenizer = new StringTokenizer(string3, " \n\t\r");
                    while (stringTokenizer.hasMoreTokens()) {
                        String string8 = stringTokenizer.nextToken();
                        QName qName = (QName)fExtraDVs[2].validate(string8, (ValidationContext)xSDocumentInfo.fValidationContext, null);
                        if (qName.prefix == XMLSymbols.EMPTY_STRING && qName.uri == null && xSDocumentInfo.fIsChameleonSchema) {
                            qName.uri = xSDocumentInfo.fTargetNamespace;
                        }
                        vector2.addElement(qName);
                    }
                    vector = vector2;
                    break;
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.2", new Object[]{string3, "(List of QName)"});
                }
            }
            case -10: {
                if (string3.equals("0")) {
                    vector = fXIntPool.getXInt(0);
                    break;
                }
                if (string3.equals("1")) {
                    vector = fXIntPool.getXInt(1);
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string3, "(0 | 1)"});
            }
            case -11: {
                if (string3.equals("##any")) {
                    vector = INT_ANY_ANY;
                    break;
                }
                if (string3.equals("##other")) {
                    vector = INT_ANY_NOT;
                    String[] stringArray = new String[]{xSDocumentInfo.fTargetNamespace, null};
                    objectArray[XSAttributeChecker.ATTIDX_NAMESPACE_LIST] = stringArray;
                    break;
                }
                vector = INT_ANY_LIST;
                this.fNamespaceList.removeAllElements();
                StringTokenizer stringTokenizer = new StringTokenizer(string3, " \n\t\r");
                try {
                    while (stringTokenizer.hasMoreTokens()) {
                        String string9;
                        String string10 = stringTokenizer.nextToken();
                        if (string10.equals("##local")) {
                            string9 = null;
                        } else if (string10.equals("##targetNamespace")) {
                            string9 = xSDocumentInfo.fTargetNamespace;
                        } else {
                            fExtraDVs[0].validate(string10, (ValidationContext)xSDocumentInfo.fValidationContext, null);
                            string9 = this.fSymbolTable.addSymbol(string10);
                        }
                        if (this.fNamespaceList.contains(string9)) continue;
                        this.fNamespaceList.addElement(string9);
                    }
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.3", new Object[]{string3, "((##any | ##other) | List of (anyURI | (##targetNamespace | ##local)) )"});
                }
                int n6 = this.fNamespaceList.size();
                Object[] objectArray2 = new String[n6];
                this.fNamespaceList.copyInto(objectArray2);
                objectArray[XSAttributeChecker.ATTIDX_NAMESPACE_LIST] = objectArray2;
                break;
            }
            case -12: {
                if (string3.equals("strict")) {
                    vector = INT_ANY_STRICT;
                    break;
                }
                if (string3.equals("lax")) {
                    vector = INT_ANY_LAX;
                    break;
                }
                if (string3.equals("skip")) {
                    vector = INT_ANY_SKIP;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string3, "(lax | skip | strict)"});
            }
            case -13: {
                if (string3.equals("optional")) {
                    vector = INT_USE_OPTIONAL;
                    break;
                }
                if (string3.equals("required")) {
                    vector = INT_USE_REQUIRED;
                    break;
                }
                if (string3.equals("prohibited")) {
                    vector = INT_USE_PROHIBITED;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string3, "(optional | prohibited | required)"});
            }
            case -14: {
                if (string3.equals("preserve")) {
                    vector = INT_WS_PRESERVE;
                    break;
                }
                if (string3.equals("replace")) {
                    vector = INT_WS_REPLACE;
                    break;
                }
                if (string3.equals("collapse")) {
                    vector = INT_WS_COLLAPSE;
                    break;
                }
                throw new InvalidDatatypeValueException("cvc-enumeration-valid", new Object[]{string3, "(preserve | replace | collapse)"});
            }
        }
        return vector;
    }

    void reportSchemaError(String string, Object[] objectArray, Element element) {
        this.fSchemaHandler.reportSchemaError(string, objectArray, element);
    }

    public void checkNonSchemaAttributes(XSGrammarBucket xSGrammarBucket) {
        for (Map.Entry entry : this.fNonSchemaAttrs.entrySet()) {
            XSSimpleType xSSimpleType;
            XSAttributeDecl xSAttributeDecl;
            String string = (String)entry.getKey();
            String string2 = string.substring(0, string.indexOf(44));
            String string3 = string.substring(string.indexOf(44) + 1);
            SchemaGrammar schemaGrammar = xSGrammarBucket.getGrammar(string2);
            if (schemaGrammar == null || (xSAttributeDecl = schemaGrammar.getGlobalAttributeDecl(string3)) == null || (xSSimpleType = (XSSimpleType)xSAttributeDecl.getTypeDefinition()) == null) continue;
            Vector vector = (Vector)entry.getValue();
            String string4 = (String)vector.elementAt(0);
            int n = vector.size();
            for (int i = 1; i < n; i += 2) {
                String string5 = (String)vector.elementAt(i);
                try {
                    xSSimpleType.validate((String)vector.elementAt(i + 1), null, null);
                    continue;
                }
                catch (InvalidDatatypeValueException invalidDatatypeValueException) {
                    this.reportSchemaError("s4s-att-invalid-value", new Object[]{string5, string4, invalidDatatypeValueException.getMessage()}, null);
                }
            }
        }
    }

    public static String normalize(String string, short s) {
        int n;
        int n2 = n = string == null ? 0 : string.length();
        if (n == 0 || s == 0) {
            return string;
        }
        StringBuffer stringBuffer = new StringBuffer();
        if (s == 1) {
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (c != '\t' && c != '\n' && c != '\r') {
                    stringBuffer.append(c);
                    continue;
                }
                stringBuffer.append(' ');
            }
        } else {
            boolean bl = true;
            for (int i = 0; i < n; ++i) {
                char c = string.charAt(i);
                if (c != '\t' && c != '\n' && c != '\r' && c != ' ') {
                    stringBuffer.append(c);
                    bl = false;
                    continue;
                }
                while (i < n - 1 && ((c = string.charAt(i + 1)) == '\t' || c == '\n' || c == '\r' || c == ' ')) {
                    ++i;
                }
                if (i >= n - 1 || bl) continue;
                stringBuffer.append(' ');
            }
        }
        return stringBuffer.toString();
    }

    protected Object[] getAvailableArray() {
        if (this.fArrayPool.length == this.fPoolPos) {
            this.fArrayPool = new Object[this.fPoolPos + 10][];
            for (int i = this.fPoolPos; i < this.fArrayPool.length; ++i) {
                this.fArrayPool[i] = new Object[ATTIDX_COUNT];
            }
        }
        Object[] objectArray = this.fArrayPool[this.fPoolPos];
        this.fArrayPool[this.fPoolPos++] = null;
        System.arraycopy(fTempArray, 0, objectArray, 0, ATTIDX_COUNT - 1);
        objectArray[XSAttributeChecker.ATTIDX_ISRETURNED] = Boolean.FALSE;
        return objectArray;
    }

    public void returnAttrArray(Object[] objectArray, XSDocumentInfo xSDocumentInfo) {
        if (xSDocumentInfo != null) {
            xSDocumentInfo.fNamespaceSupport.popContext();
        }
        if (this.fPoolPos == 0 || objectArray == null || objectArray.length != ATTIDX_COUNT || ((Boolean)objectArray[ATTIDX_ISRETURNED]).booleanValue()) {
            return;
        }
        objectArray[XSAttributeChecker.ATTIDX_ISRETURNED] = Boolean.TRUE;
        if (objectArray[ATTIDX_NONSCHEMA] != null) {
            ((Vector)objectArray[ATTIDX_NONSCHEMA]).clear();
        }
        this.fArrayPool[--this.fPoolPos] = objectArray;
    }

    public void resolveNamespace(Element element, Attr[] attrArray, SchemaNamespaceSupport schemaNamespaceSupport) {
        schemaNamespaceSupport.pushContext();
        int n = attrArray.length;
        Attr attr = null;
        for (int i = 0; i < n; ++i) {
            attr = attrArray[i];
            String string = DOMUtil.getName(attr);
            String string2 = null;
            if (string.equals(XMLSymbols.PREFIX_XMLNS)) {
                string2 = XMLSymbols.EMPTY_STRING;
            } else if (string.startsWith("xmlns:")) {
                string2 = this.fSymbolTable.addSymbol(DOMUtil.getLocalName(attr));
            }
            if (string2 == null) continue;
            String string3 = this.fSymbolTable.addSymbol(DOMUtil.getValue(attr));
            schemaNamespaceSupport.declarePrefix(string2, string3.length() != 0 ? string3 : null);
        }
    }

    static {
        SchemaGrammar.BuiltinSchemaGrammar builtinSchemaGrammar = SchemaGrammar.SG_SchemaNS;
        XSAttributeChecker.fExtraDVs[0] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("anyURI");
        XSAttributeChecker.fExtraDVs[1] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("ID");
        XSAttributeChecker.fExtraDVs[2] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("QName");
        XSAttributeChecker.fExtraDVs[3] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("string");
        XSAttributeChecker.fExtraDVs[4] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("token");
        XSAttributeChecker.fExtraDVs[5] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("NCName");
        XSAttributeChecker.fExtraDVs[6] = fExtraDVs[3];
        XSAttributeChecker.fExtraDVs[6] = fExtraDVs[3];
        XSAttributeChecker.fExtraDVs[8] = (XSSimpleType)builtinSchemaGrammar.getGlobalTypeDecl("language");
        int n = 0;
        int n2 = n++;
        int n3 = n++;
        int n4 = n++;
        int n5 = n++;
        int n6 = n++;
        int n7 = n++;
        int n8 = n++;
        int n9 = n++;
        int n10 = n++;
        int n11 = n++;
        int n12 = n++;
        int n13 = n++;
        int n14 = n++;
        int n15 = n++;
        int n16 = n++;
        int n17 = n++;
        int n18 = n++;
        int n19 = n++;
        int n20 = n++;
        int n21 = n++;
        int n22 = n++;
        int n23 = n++;
        int n24 = n++;
        int n25 = n++;
        int n26 = n++;
        int n27 = n++;
        int n28 = n++;
        int n29 = n++;
        int n30 = n++;
        int n31 = n++;
        int n32 = n++;
        int n33 = n++;
        int n34 = n++;
        int n35 = n++;
        int n36 = n++;
        int n37 = n++;
        int n38 = n++;
        int n39 = n++;
        int n40 = n++;
        int n41 = n++;
        int n42 = n++;
        int n43 = n++;
        int n44 = n++;
        int n45 = n++;
        int n46 = n++;
        int n47 = n++;
        int n48 = n++;
        int n49 = n++;
        OneAttr[] oneAttrArray = new OneAttr[n];
        oneAttrArray[n2] = new OneAttr(SchemaSymbols.ATT_ABSTRACT, -15, ATTIDX_ABSTRACT, Boolean.FALSE);
        oneAttrArray[n3] = new OneAttr(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, -6, ATTIDX_AFORMDEFAULT, INT_UNQUALIFIED);
        oneAttrArray[n4] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, null);
        oneAttrArray[n5] = new OneAttr(SchemaSymbols.ATT_BASE, 2, ATTIDX_BASE, null);
        oneAttrArray[n6] = new OneAttr(SchemaSymbols.ATT_BLOCK, -1, ATTIDX_BLOCK, null);
        oneAttrArray[n7] = new OneAttr(SchemaSymbols.ATT_BLOCK, -2, ATTIDX_BLOCK, null);
        oneAttrArray[n8] = new OneAttr(SchemaSymbols.ATT_BLOCKDEFAULT, -1, ATTIDX_BLOCKDEFAULT, INT_EMPTY_SET);
        oneAttrArray[n9] = new OneAttr(SchemaSymbols.ATT_DEFAULT, 3, ATTIDX_DEFAULT, null);
        oneAttrArray[n10] = new OneAttr(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, -6, ATTIDX_EFORMDEFAULT, INT_UNQUALIFIED);
        oneAttrArray[n11] = new OneAttr(SchemaSymbols.ATT_FINAL, -3, ATTIDX_FINAL, null);
        oneAttrArray[n12] = new OneAttr(SchemaSymbols.ATT_FINAL, -4, ATTIDX_FINAL, null);
        oneAttrArray[n13] = new OneAttr(SchemaSymbols.ATT_FINALDEFAULT, -5, ATTIDX_FINALDEFAULT, INT_EMPTY_SET);
        oneAttrArray[n14] = new OneAttr(SchemaSymbols.ATT_FIXED, 3, ATTIDX_FIXED, null);
        oneAttrArray[n15] = new OneAttr(SchemaSymbols.ATT_FIXED, -15, ATTIDX_FIXED, Boolean.FALSE);
        oneAttrArray[n16] = new OneAttr(SchemaSymbols.ATT_FORM, -6, ATTIDX_FORM, null);
        oneAttrArray[n17] = new OneAttr(SchemaSymbols.ATT_ID, 1, ATTIDX_ID, null);
        oneAttrArray[n18] = new OneAttr(SchemaSymbols.ATT_ITEMTYPE, 2, ATTIDX_ITEMTYPE, null);
        oneAttrArray[n19] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -7, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
        oneAttrArray[n20] = new OneAttr(SchemaSymbols.ATT_MAXOCCURS, -8, ATTIDX_MAXOCCURS, fXIntPool.getXInt(1));
        oneAttrArray[n21] = new OneAttr(SchemaSymbols.ATT_MEMBERTYPES, -9, ATTIDX_MEMBERTYPES, null);
        oneAttrArray[n22] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -16, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
        oneAttrArray[n23] = new OneAttr(SchemaSymbols.ATT_MINOCCURS, -10, ATTIDX_MINOCCURS, fXIntPool.getXInt(1));
        oneAttrArray[n24] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, Boolean.FALSE);
        oneAttrArray[n25] = new OneAttr(SchemaSymbols.ATT_MIXED, -15, ATTIDX_MIXED, null);
        oneAttrArray[n26] = new OneAttr(SchemaSymbols.ATT_NAME, 5, ATTIDX_NAME, null);
        oneAttrArray[n27] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, -11, ATTIDX_NAMESPACE, INT_ANY_ANY);
        oneAttrArray[n28] = new OneAttr(SchemaSymbols.ATT_NAMESPACE, 0, ATTIDX_NAMESPACE, null);
        oneAttrArray[n29] = new OneAttr(SchemaSymbols.ATT_NILLABLE, -15, ATTIDX_NILLABLE, Boolean.FALSE);
        oneAttrArray[n30] = new OneAttr(SchemaSymbols.ATT_PROCESSCONTENTS, -12, ATTIDX_PROCESSCONTENTS, INT_ANY_STRICT);
        oneAttrArray[n31] = new OneAttr(SchemaSymbols.ATT_PUBLIC, 4, ATTIDX_PUBLIC, null);
        oneAttrArray[n32] = new OneAttr(SchemaSymbols.ATT_REF, 2, ATTIDX_REF, null);
        oneAttrArray[n33] = new OneAttr(SchemaSymbols.ATT_REFER, 2, ATTIDX_REFER, null);
        oneAttrArray[n34] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, null);
        oneAttrArray[n35] = new OneAttr(SchemaSymbols.ATT_SCHEMALOCATION, 0, ATTIDX_SCHEMALOCATION, null);
        oneAttrArray[n36] = new OneAttr(SchemaSymbols.ATT_SOURCE, 0, ATTIDX_SOURCE, null);
        oneAttrArray[n37] = new OneAttr(SchemaSymbols.ATT_SUBSTITUTIONGROUP, 2, ATTIDX_SUBSGROUP, null);
        oneAttrArray[n38] = new OneAttr(SchemaSymbols.ATT_SYSTEM, 0, ATTIDX_SYSTEM, null);
        oneAttrArray[n39] = new OneAttr(SchemaSymbols.ATT_TARGETNAMESPACE, 0, ATTIDX_TARGETNAMESPACE, null);
        oneAttrArray[n40] = new OneAttr(SchemaSymbols.ATT_TYPE, 2, ATTIDX_TYPE, null);
        oneAttrArray[n41] = new OneAttr(SchemaSymbols.ATT_USE, -13, ATTIDX_USE, INT_USE_OPTIONAL);
        oneAttrArray[n42] = new OneAttr(SchemaSymbols.ATT_VALUE, -16, ATTIDX_VALUE, null);
        oneAttrArray[n43] = new OneAttr(SchemaSymbols.ATT_VALUE, -17, ATTIDX_VALUE, null);
        oneAttrArray[n44] = new OneAttr(SchemaSymbols.ATT_VALUE, 3, ATTIDX_VALUE, null);
        oneAttrArray[n45] = new OneAttr(SchemaSymbols.ATT_VALUE, -14, ATTIDX_VALUE, null);
        oneAttrArray[n46] = new OneAttr(SchemaSymbols.ATT_VERSION, 4, ATTIDX_VERSION, null);
        oneAttrArray[n47] = new OneAttr(SchemaSymbols.ATT_XML_LANG, 8, ATTIDX_XML_LANG, null);
        oneAttrArray[n48] = new OneAttr(SchemaSymbols.ATT_XPATH, 6, ATTIDX_XPATH, null);
        oneAttrArray[n49] = new OneAttr(SchemaSymbols.ATT_XPATH, 7, ATTIDX_XPATH, null);
        Container container = Container.getContainer(5);
        container.put(SchemaSymbols.ATT_DEFAULT, oneAttrArray[n9]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n14]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        container.put(SchemaSymbols.ATT_TYPE, oneAttrArray[n40]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTE, container);
        container = Container.getContainer(7);
        container.put(SchemaSymbols.ATT_DEFAULT, oneAttrArray[n9]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n14]);
        container.put(SchemaSymbols.ATT_FORM, oneAttrArray[n16]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        container.put(SchemaSymbols.ATT_TYPE, oneAttrArray[n40]);
        container.put(SchemaSymbols.ATT_USE, oneAttrArray[n41]);
        fEleAttrsMapL.put(ATTRIBUTE_N, container);
        container = Container.getContainer(5);
        container.put(SchemaSymbols.ATT_DEFAULT, oneAttrArray[n9]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n14]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_REF, oneAttrArray[n32]);
        container.put(SchemaSymbols.ATT_USE, oneAttrArray[n41]);
        fEleAttrsMapL.put(ATTRIBUTE_R, container);
        container = Container.getContainer(10);
        container.put(SchemaSymbols.ATT_ABSTRACT, oneAttrArray[n2]);
        container.put(SchemaSymbols.ATT_BLOCK, oneAttrArray[n6]);
        container.put(SchemaSymbols.ATT_DEFAULT, oneAttrArray[n9]);
        container.put(SchemaSymbols.ATT_FINAL, oneAttrArray[n11]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n14]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        container.put(SchemaSymbols.ATT_NILLABLE, oneAttrArray[n29]);
        container.put(SchemaSymbols.ATT_SUBSTITUTIONGROUP, oneAttrArray[n37]);
        container.put(SchemaSymbols.ATT_TYPE, oneAttrArray[n40]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_ELEMENT, container);
        container = Container.getContainer(10);
        container.put(SchemaSymbols.ATT_BLOCK, oneAttrArray[n6]);
        container.put(SchemaSymbols.ATT_DEFAULT, oneAttrArray[n9]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n14]);
        container.put(SchemaSymbols.ATT_FORM, oneAttrArray[n16]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MAXOCCURS, oneAttrArray[n19]);
        container.put(SchemaSymbols.ATT_MINOCCURS, oneAttrArray[n22]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        container.put(SchemaSymbols.ATT_NILLABLE, oneAttrArray[n29]);
        container.put(SchemaSymbols.ATT_TYPE, oneAttrArray[n40]);
        fEleAttrsMapL.put(ELEMENT_N, container);
        container = Container.getContainer(4);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MAXOCCURS, oneAttrArray[n19]);
        container.put(SchemaSymbols.ATT_MINOCCURS, oneAttrArray[n22]);
        container.put(SchemaSymbols.ATT_REF, oneAttrArray[n32]);
        fEleAttrsMapL.put(ELEMENT_R, container);
        container = Container.getContainer(6);
        container.put(SchemaSymbols.ATT_ABSTRACT, oneAttrArray[n2]);
        container.put(SchemaSymbols.ATT_BLOCK, oneAttrArray[n7]);
        container.put(SchemaSymbols.ATT_FINAL, oneAttrArray[n11]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MIXED, oneAttrArray[n24]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_COMPLEXTYPE, container);
        container = Container.getContainer(4);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        container.put(SchemaSymbols.ATT_PUBLIC, oneAttrArray[n31]);
        container.put(SchemaSymbols.ATT_SYSTEM, oneAttrArray[n38]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_NOTATION, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MIXED, oneAttrArray[n24]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXTYPE, container);
        container = Container.getContainer(1);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLECONTENT, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_BASE, oneAttrArray[n5]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_RESTRICTION, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_BASE, oneAttrArray[n4]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_EXTENSION, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_REF, oneAttrArray[n32]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAMESPACE, oneAttrArray[n27]);
        container.put(SchemaSymbols.ATT_PROCESSCONTENTS, oneAttrArray[n30]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_ANYATTRIBUTE, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MIXED, oneAttrArray[n25]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_COMPLEXCONTENT, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_ATTRIBUTEGROUP, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_GROUP, container);
        container = Container.getContainer(4);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MAXOCCURS, oneAttrArray[n19]);
        container.put(SchemaSymbols.ATT_MINOCCURS, oneAttrArray[n22]);
        container.put(SchemaSymbols.ATT_REF, oneAttrArray[n32]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_GROUP, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MAXOCCURS, oneAttrArray[n20]);
        container.put(SchemaSymbols.ATT_MINOCCURS, oneAttrArray[n23]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_ALL, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MAXOCCURS, oneAttrArray[n19]);
        container.put(SchemaSymbols.ATT_MINOCCURS, oneAttrArray[n22]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_CHOICE, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_SEQUENCE, container);
        container = Container.getContainer(5);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MAXOCCURS, oneAttrArray[n19]);
        container.put(SchemaSymbols.ATT_MINOCCURS, oneAttrArray[n22]);
        container.put(SchemaSymbols.ATT_NAMESPACE, oneAttrArray[n27]);
        container.put(SchemaSymbols.ATT_PROCESSCONTENTS, oneAttrArray[n30]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_ANY, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_UNIQUE, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_KEY, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        container.put(SchemaSymbols.ATT_REFER, oneAttrArray[n33]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_KEYREF, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_XPATH, oneAttrArray[n48]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_SELECTOR, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_XPATH, oneAttrArray[n49]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_FIELD, container);
        container = Container.getContainer(1);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_ANNOTATION, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_ANNOTATION, container);
        container = Container.getContainer(1);
        container.put(SchemaSymbols.ATT_SOURCE, oneAttrArray[n36]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_APPINFO, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_APPINFO, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_SOURCE, oneAttrArray[n36]);
        container.put(SchemaSymbols.ATT_XML_LANG, oneAttrArray[n47]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_DOCUMENTATION, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_DOCUMENTATION, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_FINAL, oneAttrArray[n12]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAME, oneAttrArray[n26]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_SIMPLETYPE, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_FINAL, oneAttrArray[n12]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_SIMPLETYPE, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_ITEMTYPE, oneAttrArray[n18]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_LIST, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_MEMBERTYPES, oneAttrArray[n21]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_UNION, container);
        container = Container.getContainer(8);
        container.put(SchemaSymbols.ATT_ATTRIBUTEFORMDEFAULT, oneAttrArray[n3]);
        container.put(SchemaSymbols.ATT_BLOCKDEFAULT, oneAttrArray[n8]);
        container.put(SchemaSymbols.ATT_ELEMENTFORMDEFAULT, oneAttrArray[n10]);
        container.put(SchemaSymbols.ATT_FINALDEFAULT, oneAttrArray[n13]);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_TARGETNAMESPACE, oneAttrArray[n39]);
        container.put(SchemaSymbols.ATT_VERSION, oneAttrArray[n46]);
        container.put(SchemaSymbols.ATT_XML_LANG, oneAttrArray[n47]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_SCHEMA, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_SCHEMALOCATION, oneAttrArray[n34]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_INCLUDE, container);
        fEleAttrsMapG.put(SchemaSymbols.ELT_REDEFINE, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_NAMESPACE, oneAttrArray[n28]);
        container.put(SchemaSymbols.ATT_SCHEMALOCATION, oneAttrArray[n35]);
        fEleAttrsMapG.put(SchemaSymbols.ELT_IMPORT, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_VALUE, oneAttrArray[n42]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n15]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_LENGTH, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_MINLENGTH, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_MAXLENGTH, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_FRACTIONDIGITS, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_VALUE, oneAttrArray[n43]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n15]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_TOTALDIGITS, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_VALUE, oneAttrArray[n44]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_PATTERN, container);
        container = Container.getContainer(2);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_VALUE, oneAttrArray[n44]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_ENUMERATION, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_VALUE, oneAttrArray[n45]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n15]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_WHITESPACE, container);
        container = Container.getContainer(3);
        container.put(SchemaSymbols.ATT_ID, oneAttrArray[n17]);
        container.put(SchemaSymbols.ATT_VALUE, oneAttrArray[n44]);
        container.put(SchemaSymbols.ATT_FIXED, oneAttrArray[n15]);
        fEleAttrsMapL.put(SchemaSymbols.ELT_MAXINCLUSIVE, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_MAXEXCLUSIVE, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_MININCLUSIVE, container);
        fEleAttrsMapL.put(SchemaSymbols.ELT_MINEXCLUSIVE, container);
        fSeenTemp = new boolean[ATTIDX_COUNT];
        fTempArray = new Object[ATTIDX_COUNT];
    }
}

