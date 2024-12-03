/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.processor;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.StringTokenizer;
import java.util.Vector;
import javax.xml.transform.TransformerException;
import org.apache.xalan.processor.StylesheetHandler;
import org.apache.xalan.res.XSLMessages;
import org.apache.xalan.templates.AVT;
import org.apache.xalan.templates.ElemTemplateElement;
import org.apache.xml.utils.QName;
import org.apache.xml.utils.StringToIntTable;
import org.apache.xml.utils.StringVector;
import org.apache.xml.utils.XML11Char;
import org.apache.xpath.XPath;
import org.xml.sax.SAXException;

public class XSLTAttributeDef {
    static final int FATAL = 0;
    static final int ERROR = 1;
    static final int WARNING = 2;
    static final int T_CDATA = 1;
    static final int T_URL = 2;
    static final int T_AVT = 3;
    static final int T_PATTERN = 4;
    static final int T_EXPR = 5;
    static final int T_CHAR = 6;
    static final int T_NUMBER = 7;
    static final int T_YESNO = 8;
    static final int T_QNAME = 9;
    static final int T_QNAMES = 10;
    static final int T_ENUM = 11;
    static final int T_SIMPLEPATTERNLIST = 12;
    static final int T_NMTOKEN = 13;
    static final int T_STRINGLIST = 14;
    static final int T_PREFIX_URLLIST = 15;
    static final int T_ENUM_OR_PQNAME = 16;
    static final int T_NCNAME = 17;
    static final int T_AVT_QNAME = 18;
    static final int T_QNAMES_RESOLVE_NULL = 19;
    static final int T_PREFIXLIST = 20;
    static final XSLTAttributeDef m_foreignAttr = new XSLTAttributeDef("*", "*", 1, false, false, 2);
    static final String S_FOREIGNATTR_SETTER = "setForeignAttr";
    private String m_namespace;
    private String m_name;
    private int m_type;
    private StringToIntTable m_enums;
    private String m_default;
    private boolean m_required;
    private boolean m_supportsAVT;
    int m_errorType = 2;
    String m_setterString = null;

    XSLTAttributeDef(String namespace, String name, int type, boolean required, boolean supportsAVT, int errorType) {
        this.m_namespace = namespace;
        this.m_name = name;
        this.m_type = type;
        this.m_required = required;
        this.m_supportsAVT = supportsAVT;
        this.m_errorType = errorType;
    }

    XSLTAttributeDef(String namespace, String name, int type, boolean supportsAVT, int errorType, String defaultVal) {
        this.m_namespace = namespace;
        this.m_name = name;
        this.m_type = type;
        this.m_required = false;
        this.m_supportsAVT = supportsAVT;
        this.m_errorType = errorType;
        this.m_default = defaultVal;
    }

    XSLTAttributeDef(String namespace, String name, boolean required, boolean supportsAVT, boolean prefixedQNameValAllowed, int errorType, String k1, int v1, String k2, int v2) {
        this.m_namespace = namespace;
        this.m_name = name;
        this.m_type = prefixedQNameValAllowed ? 16 : 11;
        this.m_required = required;
        this.m_supportsAVT = supportsAVT;
        this.m_errorType = errorType;
        this.m_enums = new StringToIntTable(2);
        this.m_enums.put(k1, v1);
        this.m_enums.put(k2, v2);
    }

    XSLTAttributeDef(String namespace, String name, boolean required, boolean supportsAVT, boolean prefixedQNameValAllowed, int errorType, String k1, int v1, String k2, int v2, String k3, int v3) {
        this.m_namespace = namespace;
        this.m_name = name;
        this.m_type = prefixedQNameValAllowed ? 16 : 11;
        this.m_required = required;
        this.m_supportsAVT = supportsAVT;
        this.m_errorType = errorType;
        this.m_enums = new StringToIntTable(3);
        this.m_enums.put(k1, v1);
        this.m_enums.put(k2, v2);
        this.m_enums.put(k3, v3);
    }

    XSLTAttributeDef(String namespace, String name, boolean required, boolean supportsAVT, boolean prefixedQNameValAllowed, int errorType, String k1, int v1, String k2, int v2, String k3, int v3, String k4, int v4) {
        this.m_namespace = namespace;
        this.m_name = name;
        this.m_type = prefixedQNameValAllowed ? 16 : 11;
        this.m_required = required;
        this.m_supportsAVT = supportsAVT;
        this.m_errorType = errorType;
        this.m_enums = new StringToIntTable(4);
        this.m_enums.put(k1, v1);
        this.m_enums.put(k2, v2);
        this.m_enums.put(k3, v3);
        this.m_enums.put(k4, v4);
    }

    String getNamespace() {
        return this.m_namespace;
    }

    String getName() {
        return this.m_name;
    }

    int getType() {
        return this.m_type;
    }

    private int getEnum(String key) {
        return this.m_enums.get(key);
    }

    private String[] getEnumNames() {
        return this.m_enums.keys();
    }

    String getDefault() {
        return this.m_default;
    }

    void setDefault(String def) {
        this.m_default = def;
    }

    boolean getRequired() {
        return this.m_required;
    }

    boolean getSupportsAVT() {
        return this.m_supportsAVT;
    }

    int getErrorType() {
        return this.m_errorType;
    }

    public String getSetterMethodName() {
        if (null == this.m_setterString) {
            if (m_foreignAttr == this) {
                return S_FOREIGNATTR_SETTER;
            }
            if (this.m_name.equals("*")) {
                this.m_setterString = "addLiteralResultAttribute";
                return this.m_setterString;
            }
            StringBuffer outBuf = new StringBuffer();
            outBuf.append("set");
            if (this.m_namespace != null && this.m_namespace.equals("http://www.w3.org/XML/1998/namespace")) {
                outBuf.append("Xml");
            }
            int n = this.m_name.length();
            for (int i = 0; i < n; ++i) {
                char c = this.m_name.charAt(i);
                if ('-' == c) {
                    c = this.m_name.charAt(++i);
                    c = Character.toUpperCase(c);
                } else if (0 == i) {
                    c = Character.toUpperCase(c);
                }
                outBuf.append(c);
            }
            this.m_setterString = outBuf.toString();
        }
        return this.m_setterString;
    }

    AVT processAVT(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        try {
            AVT avt = new AVT(handler, uri, name, rawName, value, owner);
            return avt;
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    Object processCDATA(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        if (this.getSupportsAVT()) {
            try {
                AVT avt = new AVT(handler, uri, name, rawName, value, owner);
                return avt;
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        return value;
    }

    Object processCHAR(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        if (this.getSupportsAVT()) {
            try {
                AVT avt = new AVT(handler, uri, name, rawName, value, owner);
                if (avt.isSimple() && value.length() != 1) {
                    this.handleError(handler, "INVALID_TCHAR", new Object[]{name, value}, null);
                    return null;
                }
                return avt;
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        if (value.length() != 1) {
            this.handleError(handler, "INVALID_TCHAR", new Object[]{name, value}, null);
            return null;
        }
        return new Character(value.charAt(0));
    }

    Object processENUM(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        int retVal;
        AVT avt = null;
        if (this.getSupportsAVT()) {
            try {
                avt = new AVT(handler, uri, name, rawName, value, owner);
                if (!avt.isSimple()) {
                    return avt;
                }
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        if ((retVal = this.getEnum(value)) == -10000) {
            StringBuffer enumNamesList = this.getListOfEnums();
            this.handleError(handler, "INVALID_ENUM", new Object[]{name, value, enumNamesList.toString()}, null);
            return null;
        }
        if (this.getSupportsAVT()) {
            return avt;
        }
        return new Integer(retVal);
    }

    Object processENUM_OR_PQNAME(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        int key;
        Serializable objToReturn = null;
        if (this.getSupportsAVT()) {
            try {
                AVT avt = new AVT(handler, uri, name, rawName, value, owner);
                if (!avt.isSimple()) {
                    return avt;
                }
                objToReturn = avt;
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        if ((key = this.getEnum(value)) != -10000) {
            if (objToReturn == null) {
                objToReturn = new Integer(key);
            }
        } else {
            try {
                QName qname = new QName(value, handler, true);
                if (objToReturn == null) {
                    objToReturn = qname;
                }
                if (qname.getPrefix() == null) {
                    StringBuffer enumNamesList = this.getListOfEnums();
                    enumNamesList.append(" <qname-but-not-ncname>");
                    this.handleError(handler, "INVALID_ENUM", new Object[]{name, value, enumNamesList.toString()}, null);
                    return null;
                }
            }
            catch (IllegalArgumentException ie) {
                StringBuffer enumNamesList = this.getListOfEnums();
                enumNamesList.append(" <qname-but-not-ncname>");
                this.handleError(handler, "INVALID_ENUM", new Object[]{name, value, enumNamesList.toString()}, ie);
                return null;
            }
            catch (RuntimeException re) {
                StringBuffer enumNamesList = this.getListOfEnums();
                enumNamesList.append(" <qname-but-not-ncname>");
                this.handleError(handler, "INVALID_ENUM", new Object[]{name, value, enumNamesList.toString()}, re);
                return null;
            }
        }
        return objToReturn;
    }

    Object processEXPR(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        try {
            XPath expr = handler.createXPath(value, owner);
            return expr;
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    Object processNMTOKEN(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        if (this.getSupportsAVT()) {
            try {
                AVT avt = new AVT(handler, uri, name, rawName, value, owner);
                if (avt.isSimple() && !XML11Char.isXML11ValidNmtoken(value)) {
                    this.handleError(handler, "INVALID_NMTOKEN", new Object[]{name, value}, null);
                    return null;
                }
                return avt;
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        if (!XML11Char.isXML11ValidNmtoken(value)) {
            this.handleError(handler, "INVALID_NMTOKEN", new Object[]{name, value}, null);
            return null;
        }
        return value;
    }

    Object processPATTERN(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        try {
            XPath pattern = handler.createMatchPatternXPath(value, owner);
            return pattern;
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    Object processNUMBER(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        if (this.getSupportsAVT()) {
            AVT avt = null;
            try {
                avt = new AVT(handler, uri, name, rawName, value, owner);
                if (avt.isSimple()) {
                    Double d = Double.valueOf(value);
                }
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
            catch (NumberFormatException nfe) {
                this.handleError(handler, "INVALID_NUMBER", new Object[]{name, value}, nfe);
                return null;
            }
            return avt;
        }
        try {
            return Double.valueOf(value);
        }
        catch (NumberFormatException nfe) {
            this.handleError(handler, "INVALID_NUMBER", new Object[]{name, value}, nfe);
            return null;
        }
    }

    Object processQNAME(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        try {
            QName qname = new QName(value, handler, true);
            return qname;
        }
        catch (IllegalArgumentException ie) {
            this.handleError(handler, "INVALID_QNAME", new Object[]{name, value}, ie);
            return null;
        }
        catch (RuntimeException re) {
            this.handleError(handler, "INVALID_QNAME", new Object[]{name, value}, re);
            return null;
        }
    }

    Object processAVT_QNAME(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        AVT avt = null;
        try {
            avt = new AVT(handler, uri, name, rawName, value, owner);
            if (avt.isSimple()) {
                String localName;
                String prefix;
                int indexOfNSSep = value.indexOf(58);
                if (indexOfNSSep >= 0 && !XML11Char.isXML11ValidNCName(prefix = value.substring(0, indexOfNSSep))) {
                    this.handleError(handler, "INVALID_QNAME", new Object[]{name, value}, null);
                    return null;
                }
                String string = localName = indexOfNSSep < 0 ? value : value.substring(indexOfNSSep + 1);
                if (localName == null || localName.length() == 0 || !XML11Char.isXML11ValidNCName(localName)) {
                    this.handleError(handler, "INVALID_QNAME", new Object[]{name, value}, null);
                    return null;
                }
            }
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
        return avt;
    }

    Object processNCNAME(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        if (this.getSupportsAVT()) {
            AVT avt = null;
            try {
                avt = new AVT(handler, uri, name, rawName, value, owner);
                if (avt.isSimple() && !XML11Char.isXML11ValidNCName(value)) {
                    this.handleError(handler, "INVALID_NCNAME", new Object[]{name, value}, null);
                    return null;
                }
                return avt;
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        if (!XML11Char.isXML11ValidNCName(value)) {
            this.handleError(handler, "INVALID_NCNAME", new Object[]{name, value}, null);
            return null;
        }
        return value;
    }

    Vector processQNAMES(StylesheetHandler handler, String uri, String name, String rawName, String value) throws SAXException {
        StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
        int nQNames = tokenizer.countTokens();
        Vector<QName> qnames = new Vector<QName>(nQNames);
        for (int i = 0; i < nQNames; ++i) {
            qnames.addElement(new QName(tokenizer.nextToken(), handler));
        }
        return qnames;
    }

    final Vector processQNAMESRNU(StylesheetHandler handler, String uri, String name, String rawName, String value) throws SAXException {
        StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
        int nQNames = tokenizer.countTokens();
        Vector<QName> qnames = new Vector<QName>(nQNames);
        String defaultURI = handler.getNamespaceForPrefix("");
        for (int i = 0; i < nQNames; ++i) {
            String tok = tokenizer.nextToken();
            if (tok.indexOf(58) == -1) {
                qnames.addElement(new QName(defaultURI, tok));
                continue;
            }
            qnames.addElement(new QName(tok, handler));
        }
        return qnames;
    }

    Vector processSIMPLEPATTERNLIST(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        try {
            StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
            int nPatterns = tokenizer.countTokens();
            Vector<XPath> patterns = new Vector<XPath>(nPatterns);
            for (int i = 0; i < nPatterns; ++i) {
                XPath pattern = handler.createMatchPatternXPath(tokenizer.nextToken(), owner);
                patterns.addElement(pattern);
            }
            return patterns;
        }
        catch (TransformerException te) {
            throw new SAXException(te);
        }
    }

    StringVector processSTRINGLIST(StylesheetHandler handler, String uri, String name, String rawName, String value) {
        StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
        int nStrings = tokenizer.countTokens();
        StringVector strings = new StringVector(nStrings);
        for (int i = 0; i < nStrings; ++i) {
            strings.addElement(tokenizer.nextToken());
        }
        return strings;
    }

    StringVector processPREFIX_URLLIST(StylesheetHandler handler, String uri, String name, String rawName, String value) throws SAXException {
        StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
        int nStrings = tokenizer.countTokens();
        StringVector strings = new StringVector(nStrings);
        for (int i = 0; i < nStrings; ++i) {
            String prefix = tokenizer.nextToken();
            String url = handler.getNamespaceForPrefix(prefix);
            if (url == null) {
                throw new SAXException(XSLMessages.createMessage("ER_CANT_RESOLVE_NSPREFIX", new Object[]{prefix}));
            }
            strings.addElement(url);
        }
        return strings;
    }

    StringVector processPREFIX_LIST(StylesheetHandler handler, String uri, String name, String rawName, String value) throws SAXException {
        StringTokenizer tokenizer = new StringTokenizer(value, " \t\n\r\f");
        int nStrings = tokenizer.countTokens();
        StringVector strings = new StringVector(nStrings);
        for (int i = 0; i < nStrings; ++i) {
            String prefix = tokenizer.nextToken();
            String url = handler.getNamespaceForPrefix(prefix);
            if (!prefix.equals("#default") && url == null) {
                throw new SAXException(XSLMessages.createMessage("ER_CANT_RESOLVE_NSPREFIX", new Object[]{prefix}));
            }
            strings.addElement(prefix);
        }
        return strings;
    }

    Object processURL(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        if (this.getSupportsAVT()) {
            try {
                AVT avt = new AVT(handler, uri, name, rawName, value, owner);
                return avt;
            }
            catch (TransformerException te) {
                throw new SAXException(te);
            }
        }
        return value;
    }

    private Boolean processYESNO(StylesheetHandler handler, String uri, String name, String rawName, String value) throws SAXException {
        if (!value.equals("yes") && !value.equals("no")) {
            this.handleError(handler, "INVALID_BOOLEAN", new Object[]{name, value}, null);
            return null;
        }
        return new Boolean(value.equals("yes"));
    }

    Object processValue(StylesheetHandler handler, String uri, String name, String rawName, String value, ElemTemplateElement owner) throws SAXException {
        int type = this.getType();
        Object processedValue = null;
        switch (type) {
            case 3: {
                processedValue = this.processAVT(handler, uri, name, rawName, value, owner);
                break;
            }
            case 1: {
                processedValue = this.processCDATA(handler, uri, name, rawName, value, owner);
                break;
            }
            case 6: {
                processedValue = this.processCHAR(handler, uri, name, rawName, value, owner);
                break;
            }
            case 11: {
                processedValue = this.processENUM(handler, uri, name, rawName, value, owner);
                break;
            }
            case 5: {
                processedValue = this.processEXPR(handler, uri, name, rawName, value, owner);
                break;
            }
            case 13: {
                processedValue = this.processNMTOKEN(handler, uri, name, rawName, value, owner);
                break;
            }
            case 4: {
                processedValue = this.processPATTERN(handler, uri, name, rawName, value, owner);
                break;
            }
            case 7: {
                processedValue = this.processNUMBER(handler, uri, name, rawName, value, owner);
                break;
            }
            case 9: {
                processedValue = this.processQNAME(handler, uri, name, rawName, value, owner);
                break;
            }
            case 10: {
                processedValue = this.processQNAMES(handler, uri, name, rawName, value);
                break;
            }
            case 19: {
                processedValue = this.processQNAMESRNU(handler, uri, name, rawName, value);
                break;
            }
            case 12: {
                processedValue = this.processSIMPLEPATTERNLIST(handler, uri, name, rawName, value, owner);
                break;
            }
            case 2: {
                processedValue = this.processURL(handler, uri, name, rawName, value, owner);
                break;
            }
            case 8: {
                processedValue = this.processYESNO(handler, uri, name, rawName, value);
                break;
            }
            case 14: {
                processedValue = this.processSTRINGLIST(handler, uri, name, rawName, value);
                break;
            }
            case 15: {
                processedValue = this.processPREFIX_URLLIST(handler, uri, name, rawName, value);
                break;
            }
            case 16: {
                processedValue = this.processENUM_OR_PQNAME(handler, uri, name, rawName, value, owner);
                break;
            }
            case 17: {
                processedValue = this.processNCNAME(handler, uri, name, rawName, value, owner);
                break;
            }
            case 18: {
                processedValue = this.processAVT_QNAME(handler, uri, name, rawName, value, owner);
                break;
            }
            case 20: {
                processedValue = this.processPREFIX_LIST(handler, uri, name, rawName, value);
                break;
            }
        }
        return processedValue;
    }

    void setDefAttrValue(StylesheetHandler handler, ElemTemplateElement elem) throws SAXException {
        this.setAttrValue(handler, this.getNamespace(), this.getName(), this.getName(), this.getDefault(), elem);
    }

    private Class getPrimativeClass(Object obj) {
        if (obj instanceof XPath) {
            return XPath.class;
        }
        Class<Object> cl = obj.getClass();
        if (cl == Double.class) {
            cl = Double.TYPE;
        }
        if (cl == Float.class) {
            cl = Float.TYPE;
        } else if (cl == Boolean.class) {
            cl = Boolean.TYPE;
        } else if (cl == Byte.class) {
            cl = Byte.TYPE;
        } else if (cl == Character.class) {
            cl = Character.TYPE;
        } else if (cl == Short.class) {
            cl = Short.TYPE;
        } else if (cl == Integer.class) {
            cl = Integer.TYPE;
        } else if (cl == Long.class) {
            cl = Long.TYPE;
        }
        return cl;
    }

    private StringBuffer getListOfEnums() {
        StringBuffer enumNamesList = new StringBuffer();
        String[] enumValues = this.getEnumNames();
        for (int i = 0; i < enumValues.length; ++i) {
            if (i > 0) {
                enumNamesList.append(' ');
            }
            enumNamesList.append(enumValues[i]);
        }
        return enumNamesList;
    }

    boolean setAttrValue(StylesheetHandler handler, String attrUri, String attrLocalName, String attrRawName, String attrValue, ElemTemplateElement elem) throws SAXException {
        if (attrRawName.equals("xmlns") || attrRawName.startsWith("xmlns:")) {
            return true;
        }
        String setterString = this.getSetterMethodName();
        if (null != setterString) {
            try {
                Object[] args;
                Method meth;
                if (setterString.equals(S_FOREIGNATTR_SETTER)) {
                    if (attrUri == null) {
                        attrUri = "";
                    }
                    Class<?> sclass = attrUri.getClass();
                    Class[] argTypes = new Class[]{sclass, sclass, sclass, sclass};
                    meth = elem.getClass().getMethod(setterString, argTypes);
                    args = new Object[]{attrUri, attrLocalName, attrRawName, attrValue};
                } else {
                    Object value = this.processValue(handler, attrUri, attrLocalName, attrRawName, attrValue, elem);
                    if (null == value) {
                        return false;
                    }
                    Class[] argTypes = new Class[]{this.getPrimativeClass(value)};
                    try {
                        meth = elem.getClass().getMethod(setterString, argTypes);
                    }
                    catch (NoSuchMethodException nsme) {
                        Class<?> cl;
                        argTypes[0] = cl = value.getClass();
                        meth = elem.getClass().getMethod(setterString, argTypes);
                    }
                    args = new Object[]{value};
                }
                meth.invoke((Object)elem, args);
            }
            catch (NoSuchMethodException nsme) {
                if (!setterString.equals(S_FOREIGNATTR_SETTER)) {
                    handler.error("ER_FAILED_CALLING_METHOD", new Object[]{setterString}, nsme);
                    return false;
                }
            }
            catch (IllegalAccessException iae) {
                handler.error("ER_FAILED_CALLING_METHOD", new Object[]{setterString}, iae);
                return false;
            }
            catch (InvocationTargetException nsme) {
                this.handleError(handler, "WG_ILLEGAL_ATTRIBUTE_VALUE", new Object[]{"name", this.getName()}, nsme);
                return false;
            }
        }
        return true;
    }

    private void handleError(StylesheetHandler handler, String msg, Object[] args, Exception exc) throws SAXException {
        switch (this.getErrorType()) {
            case 0: 
            case 1: {
                handler.error(msg, args, exc);
                break;
            }
            case 2: {
                handler.warn(msg, args);
            }
        }
    }
}

