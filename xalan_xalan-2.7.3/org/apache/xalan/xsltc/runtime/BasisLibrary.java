/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.xml.serializer.SerializationHandler
 */
package org.apache.xalan.xsltc.runtime;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.dom.DOMSource;
import org.apache.xalan.xsltc.DOM;
import org.apache.xalan.xsltc.Translet;
import org.apache.xalan.xsltc.dom.AbsoluteIterator;
import org.apache.xalan.xsltc.dom.ArrayNodeListIterator;
import org.apache.xalan.xsltc.dom.DOMAdapter;
import org.apache.xalan.xsltc.dom.MultiDOM;
import org.apache.xalan.xsltc.dom.SingletonIterator;
import org.apache.xalan.xsltc.dom.StepIterator;
import org.apache.xalan.xsltc.runtime.AbstractTranslet;
import org.apache.xalan.xsltc.runtime.InternalRuntimeError;
import org.apache.xalan.xsltc.runtime.Node;
import org.apache.xalan.xsltc.runtime.Operators;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.serializer.SerializationHandler;
import org.apache.xml.utils.XML11Char;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class BasisLibrary {
    private static final String EMPTYSTRING = "";
    private static final int DOUBLE_FRACTION_DIGITS = 340;
    private static final double lowerBounds = 0.001;
    private static final double upperBounds = 1.0E7;
    private static DecimalFormat defaultFormatter;
    private static String defaultPattern;
    private static FieldPosition _fieldPosition;
    private static char[] _characterArray;
    private static int prefixIndex;
    public static final String RUN_TIME_INTERNAL_ERR = "RUN_TIME_INTERNAL_ERR";
    public static final String RUN_TIME_COPY_ERR = "RUN_TIME_COPY_ERR";
    public static final String DATA_CONVERSION_ERR = "DATA_CONVERSION_ERR";
    public static final String EXTERNAL_FUNC_ERR = "EXTERNAL_FUNC_ERR";
    public static final String EQUALITY_EXPR_ERR = "EQUALITY_EXPR_ERR";
    public static final String INVALID_ARGUMENT_ERR = "INVALID_ARGUMENT_ERR";
    public static final String FORMAT_NUMBER_ERR = "FORMAT_NUMBER_ERR";
    public static final String ITERATOR_CLONE_ERR = "ITERATOR_CLONE_ERR";
    public static final String AXIS_SUPPORT_ERR = "AXIS_SUPPORT_ERR";
    public static final String TYPED_AXIS_SUPPORT_ERR = "TYPED_AXIS_SUPPORT_ERR";
    public static final String STRAY_ATTRIBUTE_ERR = "STRAY_ATTRIBUTE_ERR";
    public static final String STRAY_NAMESPACE_ERR = "STRAY_NAMESPACE_ERR";
    public static final String NAMESPACE_PREFIX_ERR = "NAMESPACE_PREFIX_ERR";
    public static final String DOM_ADAPTER_INIT_ERR = "DOM_ADAPTER_INIT_ERR";
    public static final String PARSER_DTD_SUPPORT_ERR = "PARSER_DTD_SUPPORT_ERR";
    public static final String NAMESPACES_SUPPORT_ERR = "NAMESPACES_SUPPORT_ERR";
    public static final String CANT_RESOLVE_RELATIVE_URI_ERR = "CANT_RESOLVE_RELATIVE_URI_ERR";
    public static final String UNSUPPORTED_XSL_ERR = "UNSUPPORTED_XSL_ERR";
    public static final String UNSUPPORTED_EXT_ERR = "UNSUPPORTED_EXT_ERR";
    public static final String UNKNOWN_TRANSLET_VERSION_ERR = "UNKNOWN_TRANSLET_VERSION_ERR";
    public static final String INVALID_QNAME_ERR = "INVALID_QNAME_ERR";
    public static final String INVALID_NCNAME_ERR = "INVALID_NCNAME_ERR";
    public static final String UNALLOWED_EXTENSION_FUNCTION_ERR = "UNALLOWED_EXTENSION_FUNCTION_ERR";
    public static final String UNALLOWED_EXTENSION_ELEMENT_ERR = "UNALLOWED_EXTENSION_ELEMENT_ERR";
    private static ResourceBundle m_bundle;
    public static final String ERROR_MESSAGES_KEY = "error-messages";

    public static int countF(DTMAxisIterator iterator) {
        return iterator.getLast();
    }

    public static int positionF(DTMAxisIterator iterator) {
        return iterator.isReverse() ? iterator.getLast() - iterator.getPosition() + 1 : iterator.getPosition();
    }

    public static double sumF(DTMAxisIterator iterator, DOM dom) {
        try {
            int node;
            double result = 0.0;
            while ((node = iterator.next()) != -1) {
                result += Double.parseDouble(dom.getStringValueX(node));
            }
            return result;
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public static String stringF(int node, DOM dom) {
        return dom.getStringValueX(node);
    }

    public static String stringF(Object obj, DOM dom) {
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        return obj.toString();
    }

    public static String stringF(Object obj, int node, DOM dom) {
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        if (obj instanceof Double) {
            int length;
            Double d = (Double)obj;
            String result = d.toString();
            if (result.charAt((length = result.length()) - 2) == '.' && result.charAt(length - 1) == '0') {
                return result.substring(0, length - 2);
            }
            return result;
        }
        if (obj != null) {
            return obj.toString();
        }
        return BasisLibrary.stringF(node, dom);
    }

    public static double numberF(int node, DOM dom) {
        return BasisLibrary.stringToReal(dom.getStringValueX(node));
    }

    public static double numberF(Object obj, DOM dom) {
        if (obj instanceof Double) {
            return (Double)obj;
        }
        if (obj instanceof Integer) {
            return ((Integer)obj).doubleValue();
        }
        if (obj instanceof Boolean) {
            return (Boolean)obj != false ? 1.0 : 0.0;
        }
        if (obj instanceof String) {
            return BasisLibrary.stringToReal((String)obj);
        }
        if (obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = (DTMAxisIterator)obj;
            return BasisLibrary.stringToReal(dom.getStringValueX(iter.reset().next()));
        }
        if (obj instanceof Node) {
            return BasisLibrary.stringToReal(dom.getStringValueX(((Node)obj).node));
        }
        if (obj instanceof DOM) {
            return BasisLibrary.stringToReal(((DOM)obj).getStringValue());
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(INVALID_ARGUMENT_ERR, className, "number()");
        return 0.0;
    }

    public static double roundF(double d) {
        return d < -0.5 || d > 0.0 ? Math.floor(d + 0.5) : (d == 0.0 ? d : (Double.isNaN(d) ? Double.NaN : -0.0));
    }

    public static boolean booleanF(Object obj) {
        if (obj instanceof Double) {
            double temp = (Double)obj;
            return temp != 0.0 && !Double.isNaN(temp);
        }
        if (obj instanceof Integer) {
            return ((Integer)obj).doubleValue() != 0.0;
        }
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        if (obj instanceof String) {
            return !((String)obj).equals(EMPTYSTRING);
        }
        if (obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = (DTMAxisIterator)obj;
            return iter.reset().next() != -1;
        }
        if (obj instanceof Node) {
            return true;
        }
        if (obj instanceof DOM) {
            String temp = ((DOM)obj).getStringValue();
            return !temp.equals(EMPTYSTRING);
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(INVALID_ARGUMENT_ERR, className, "boolean()");
        return false;
    }

    public static String substringF(String value, double start) {
        try {
            int strlen = value.length();
            int istart = (int)Math.round(start) - 1;
            if (Double.isNaN(start)) {
                return EMPTYSTRING;
            }
            if (istart > strlen) {
                return EMPTYSTRING;
            }
            if (istart < 1) {
                istart = 0;
            }
            return value.substring(istart);
        }
        catch (IndexOutOfBoundsException e) {
            BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "substring()");
            return null;
        }
    }

    public static String substringF(String value, double start, double length) {
        try {
            int strlen = value.length();
            int istart = (int)Math.round(start) - 1;
            int isum = istart + (int)Math.round(length);
            if (Double.isInfinite(length)) {
                isum = Integer.MAX_VALUE;
            }
            if (Double.isNaN(start) || Double.isNaN(length)) {
                return EMPTYSTRING;
            }
            if (Double.isInfinite(start)) {
                return EMPTYSTRING;
            }
            if (istart > strlen) {
                return EMPTYSTRING;
            }
            if (isum < 0) {
                return EMPTYSTRING;
            }
            if (istart < 0) {
                istart = 0;
            }
            if (isum > strlen) {
                return value.substring(istart);
            }
            return value.substring(istart, isum);
        }
        catch (IndexOutOfBoundsException e) {
            BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "substring()");
            return null;
        }
    }

    public static String substring_afterF(String value, String substring) {
        int index = value.indexOf(substring);
        if (index >= 0) {
            return value.substring(index + substring.length());
        }
        return EMPTYSTRING;
    }

    public static String substring_beforeF(String value, String substring) {
        int index = value.indexOf(substring);
        if (index >= 0) {
            return value.substring(0, index);
        }
        return EMPTYSTRING;
    }

    public static String translateF(String value, String from, String to) {
        int tol = to.length();
        int froml = from.length();
        int valuel = value.length();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < valuel; ++i) {
            int j;
            char ch = value.charAt(i);
            for (j = 0; j < froml; ++j) {
                if (ch != from.charAt(j)) continue;
                if (j >= tol) break;
                result.append(to.charAt(j));
                break;
            }
            if (j != froml) continue;
            result.append(ch);
        }
        return result.toString();
    }

    public static String normalize_spaceF(int node, DOM dom) {
        return BasisLibrary.normalize_spaceF(dom.getStringValueX(node));
    }

    public static String normalize_spaceF(String value) {
        int i;
        int n = value.length();
        StringBuffer result = new StringBuffer();
        for (i = 0; i < n && BasisLibrary.isWhiteSpace(value.charAt(i)); ++i) {
        }
        while (true) {
            if (i < n && !BasisLibrary.isWhiteSpace(value.charAt(i))) {
                result.append(value.charAt(i++));
                continue;
            }
            if (i == n) break;
            while (i < n && BasisLibrary.isWhiteSpace(value.charAt(i))) {
                ++i;
            }
            if (i >= n) continue;
            result.append(' ');
        }
        return result.toString();
    }

    public static String generate_idF(int node) {
        if (node > 0) {
            return "N" + node;
        }
        return EMPTYSTRING;
    }

    public static String getLocalName(String value) {
        int idx = value.lastIndexOf(58);
        if (idx >= 0) {
            value = value.substring(idx + 1);
        }
        if ((idx = value.lastIndexOf(64)) >= 0) {
            value = value.substring(idx + 1);
        }
        return value;
    }

    public static void unresolved_externalF(String name) {
        BasisLibrary.runTimeError(EXTERNAL_FUNC_ERR, name);
    }

    public static void unallowed_extension_functionF(String name) {
        BasisLibrary.runTimeError(UNALLOWED_EXTENSION_FUNCTION_ERR, name);
    }

    public static void unallowed_extension_elementF(String name) {
        BasisLibrary.runTimeError(UNALLOWED_EXTENSION_ELEMENT_ERR, name);
    }

    public static void unsupported_ElementF(String qname, boolean isExtension) {
        if (isExtension) {
            BasisLibrary.runTimeError(UNSUPPORTED_EXT_ERR, qname);
        } else {
            BasisLibrary.runTimeError(UNSUPPORTED_XSL_ERR, qname);
        }
    }

    public static String namespace_uriF(DTMAxisIterator iter, DOM dom) {
        return BasisLibrary.namespace_uriF(iter.next(), dom);
    }

    public static String system_propertyF(String name) {
        if (name.equals("xsl:version")) {
            return "1.0";
        }
        if (name.equals("xsl:vendor")) {
            return "Apache Software Foundation (Xalan XSLTC)";
        }
        if (name.equals("xsl:vendor-url")) {
            return "http://xml.apache.org/xalan-j";
        }
        BasisLibrary.runTimeError(INVALID_ARGUMENT_ERR, name, "system-property()");
        return EMPTYSTRING;
    }

    public static String namespace_uriF(int node, DOM dom) {
        String value = dom.getNodeName(node);
        int colon = value.lastIndexOf(58);
        if (colon >= 0) {
            return value.substring(0, colon);
        }
        return EMPTYSTRING;
    }

    public static String objectTypeF(Object obj) {
        if (obj instanceof String) {
            return "string";
        }
        if (obj instanceof Boolean) {
            return "boolean";
        }
        if (obj instanceof Number) {
            return "number";
        }
        if (obj instanceof DOM) {
            return "RTF";
        }
        if (obj instanceof DTMAxisIterator) {
            return "node-set";
        }
        return "unknown";
    }

    public static DTMAxisIterator nodesetF(Object obj) {
        if (obj instanceof DOM) {
            DOM dom = (DOM)obj;
            return new SingletonIterator(dom.getDocument(), true);
        }
        if (obj instanceof DTMAxisIterator) {
            return (DTMAxisIterator)obj;
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, "node-set", className);
        return null;
    }

    private static boolean isWhiteSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r';
    }

    private static boolean compareStrings(String lstring, String rstring, int op, DOM dom) {
        switch (op) {
            case 0: {
                return lstring.equals(rstring);
            }
            case 1: {
                return !lstring.equals(rstring);
            }
            case 2: {
                return BasisLibrary.numberF(lstring, dom) > BasisLibrary.numberF(rstring, dom);
            }
            case 3: {
                return BasisLibrary.numberF(lstring, dom) < BasisLibrary.numberF(rstring, dom);
            }
            case 4: {
                return BasisLibrary.numberF(lstring, dom) >= BasisLibrary.numberF(rstring, dom);
            }
            case 5: {
                return BasisLibrary.numberF(lstring, dom) <= BasisLibrary.numberF(rstring, dom);
            }
        }
        BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
        return false;
    }

    public static boolean compare(DTMAxisIterator left, DTMAxisIterator right, int op, DOM dom) {
        int lnode;
        left.reset();
        while ((lnode = left.next()) != -1) {
            int rnode;
            String lvalue = dom.getStringValueX(lnode);
            right.reset();
            while ((rnode = right.next()) != -1) {
                if (lnode == rnode) {
                    if (op == 0) {
                        return true;
                    }
                    if (op == 1) continue;
                }
                if (!BasisLibrary.compareStrings(lvalue, dom.getStringValueX(rnode), op, dom)) continue;
                return true;
            }
        }
        return false;
    }

    public static boolean compare(int node, DTMAxisIterator iterator, int op, DOM dom) {
        switch (op) {
            case 0: {
                int rnode = iterator.next();
                if (rnode == -1) break;
                String value = dom.getStringValueX(node);
                do {
                    if (node != rnode && !value.equals(dom.getStringValueX(rnode))) continue;
                    return true;
                } while ((rnode = iterator.next()) != -1);
                break;
            }
            case 1: {
                int rnode = iterator.next();
                if (rnode == -1) break;
                String value = dom.getStringValueX(node);
                do {
                    if (node == rnode || value.equals(dom.getStringValueX(rnode))) continue;
                    return true;
                } while ((rnode = iterator.next()) != -1);
                break;
            }
            case 3: {
                int rnode;
                while ((rnode = iterator.next()) != -1) {
                    if (rnode <= node) continue;
                    return true;
                }
                break;
            }
            case 2: {
                int rnode;
                while ((rnode = iterator.next()) != -1) {
                    if (rnode >= node) continue;
                    return true;
                }
                break;
            }
        }
        return false;
    }

    public static boolean compare(DTMAxisIterator left, double rnumber, int op, DOM dom) {
        switch (op) {
            case 0: {
                int node;
                while ((node = left.next()) != -1) {
                    if (BasisLibrary.numberF(dom.getStringValueX(node), dom) != rnumber) continue;
                    return true;
                }
                break;
            }
            case 1: {
                int node;
                while ((node = left.next()) != -1) {
                    if (BasisLibrary.numberF(dom.getStringValueX(node), dom) == rnumber) continue;
                    return true;
                }
                break;
            }
            case 2: {
                int node;
                while ((node = left.next()) != -1) {
                    if (!(BasisLibrary.numberF(dom.getStringValueX(node), dom) > rnumber)) continue;
                    return true;
                }
                break;
            }
            case 3: {
                int node;
                while ((node = left.next()) != -1) {
                    if (!(BasisLibrary.numberF(dom.getStringValueX(node), dom) < rnumber)) continue;
                    return true;
                }
                break;
            }
            case 4: {
                int node;
                while ((node = left.next()) != -1) {
                    if (!(BasisLibrary.numberF(dom.getStringValueX(node), dom) >= rnumber)) continue;
                    return true;
                }
                break;
            }
            case 5: {
                int node;
                while ((node = left.next()) != -1) {
                    if (!(BasisLibrary.numberF(dom.getStringValueX(node), dom) <= rnumber)) continue;
                    return true;
                }
                break;
            }
            default: {
                BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
            }
        }
        return false;
    }

    public static boolean compare(DTMAxisIterator left, String rstring, int op, DOM dom) {
        int node;
        while ((node = left.next()) != -1) {
            if (!BasisLibrary.compareStrings(dom.getStringValueX(node), rstring, op, dom)) continue;
            return true;
        }
        return false;
    }

    public static boolean compare(Object left, Object right, int op, DOM dom) {
        boolean hasSimpleArgs;
        boolean result = false;
        boolean bl = hasSimpleArgs = BasisLibrary.hasSimpleType(left) && BasisLibrary.hasSimpleType(right);
        if (op != 0 && op != 1) {
            if (left instanceof Node || right instanceof Node) {
                if (left instanceof Boolean) {
                    right = new Boolean(BasisLibrary.booleanF(right));
                    hasSimpleArgs = true;
                }
                if (right instanceof Boolean) {
                    left = new Boolean(BasisLibrary.booleanF(left));
                    hasSimpleArgs = true;
                }
            }
            if (hasSimpleArgs) {
                switch (op) {
                    case 2: {
                        return BasisLibrary.numberF(left, dom) > BasisLibrary.numberF(right, dom);
                    }
                    case 3: {
                        return BasisLibrary.numberF(left, dom) < BasisLibrary.numberF(right, dom);
                    }
                    case 4: {
                        return BasisLibrary.numberF(left, dom) >= BasisLibrary.numberF(right, dom);
                    }
                    case 5: {
                        return BasisLibrary.numberF(left, dom) <= BasisLibrary.numberF(right, dom);
                    }
                }
                BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "compare()");
            }
        }
        if (hasSimpleArgs) {
            result = left instanceof Boolean || right instanceof Boolean ? BasisLibrary.booleanF(left) == BasisLibrary.booleanF(right) : (left instanceof Double || right instanceof Double || left instanceof Integer || right instanceof Integer ? BasisLibrary.numberF(left, dom) == BasisLibrary.numberF(right, dom) : BasisLibrary.stringF(left, dom).equals(BasisLibrary.stringF(right, dom)));
            if (op == 1) {
                result = !result;
            }
        } else {
            if (left instanceof Node) {
                left = new SingletonIterator(((Node)left).node);
            }
            if (right instanceof Node) {
                right = new SingletonIterator(((Node)right).node);
            }
            if (BasisLibrary.hasSimpleType(left) || left instanceof DOM && right instanceof DTMAxisIterator) {
                Object temp = right;
                right = left;
                left = temp;
                op = Operators.swapOp(op);
            }
            if (left instanceof DOM) {
                if (right instanceof Boolean) {
                    result = (Boolean)right;
                    return result == (op == 0);
                }
                String sleft = ((DOM)left).getStringValue();
                if (right instanceof Number) {
                    result = ((Number)right).doubleValue() == BasisLibrary.stringToReal(sleft);
                } else if (right instanceof String) {
                    result = sleft.equals((String)right);
                } else if (right instanceof DOM) {
                    result = sleft.equals(((DOM)right).getStringValue());
                }
                if (op == 1) {
                    result = !result;
                }
                return result;
            }
            DTMAxisIterator iter = ((DTMAxisIterator)left).reset();
            if (right instanceof DTMAxisIterator) {
                result = BasisLibrary.compare(iter, (DTMAxisIterator)right, op, dom);
            } else if (right instanceof String) {
                result = BasisLibrary.compare(iter, (String)right, op, dom);
            } else if (right instanceof Number) {
                double temp = ((Number)right).doubleValue();
                result = BasisLibrary.compare(iter, temp, op, dom);
            } else if (right instanceof Boolean) {
                boolean temp = (Boolean)right;
                result = iter.reset().next() != -1 == temp;
            } else if (right instanceof DOM) {
                result = BasisLibrary.compare(iter, ((DOM)right).getStringValue(), op, dom);
            } else {
                if (right == null) {
                    return false;
                }
                String className = right.getClass().getName();
                BasisLibrary.runTimeError(INVALID_ARGUMENT_ERR, className, "compare()");
            }
        }
        return result;
    }

    public static boolean testLanguage(String testLang, DOM dom, int node) {
        String nodeLang = dom.getLanguage(node);
        if (nodeLang == null) {
            return false;
        }
        nodeLang = nodeLang.toLowerCase();
        if ((testLang = testLang.toLowerCase()).length() == 2) {
            return nodeLang.startsWith(testLang);
        }
        return nodeLang.equals(testLang);
    }

    private static boolean hasSimpleType(Object obj) {
        return obj instanceof Boolean || obj instanceof Double || obj instanceof Integer || obj instanceof String || obj instanceof Node || obj instanceof DOM;
    }

    public static double stringToReal(String s) {
        try {
            return Double.valueOf(s);
        }
        catch (NumberFormatException e) {
            return Double.NaN;
        }
    }

    public static int stringToInt(String s) {
        try {
            return Integer.parseInt(s);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String realToString(double d) {
        double m = Math.abs(d);
        if (m >= 0.001 && m < 1.0E7) {
            int length;
            String result = Double.toString(d);
            if (result.charAt((length = result.length()) - 2) == '.' && result.charAt(length - 1) == '0') {
                return result.substring(0, length - 2);
            }
            return result;
        }
        if (Double.isNaN(d) || Double.isInfinite(d)) {
            return Double.toString(d);
        }
        return BasisLibrary.formatNumber(d, defaultPattern, defaultFormatter);
    }

    public static int realToInt(double d) {
        return (int)d;
    }

    public static String formatNumber(double number, String pattern, DecimalFormat formatter) {
        if (formatter == null) {
            formatter = defaultFormatter;
        }
        try {
            StringBuffer result = new StringBuffer();
            if (pattern != defaultPattern) {
                formatter.applyLocalizedPattern(pattern);
            }
            formatter.format(number, result, _fieldPosition);
            return result.toString();
        }
        catch (IllegalArgumentException e) {
            BasisLibrary.runTimeError(FORMAT_NUMBER_ERR, Double.toString(number), pattern);
            return EMPTYSTRING;
        }
    }

    public static DTMAxisIterator referenceToNodeSet(Object obj) {
        if (obj instanceof Node) {
            return new SingletonIterator(((Node)obj).node);
        }
        if (obj instanceof DTMAxisIterator) {
            return ((DTMAxisIterator)obj).cloneIterator().reset();
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, "node-set");
        return null;
    }

    public static NodeList referenceToNodeList(Object obj, DOM dom) {
        if (obj instanceof Node || obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = BasisLibrary.referenceToNodeSet(obj);
            return dom.makeNodeList(iter);
        }
        if (obj instanceof DOM) {
            dom = (DOM)obj;
            return dom.makeNodeList(0);
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, "org.w3c.dom.NodeList");
        return null;
    }

    public static org.w3c.dom.Node referenceToNode(Object obj, DOM dom) {
        if (obj instanceof Node || obj instanceof DTMAxisIterator) {
            DTMAxisIterator iter = BasisLibrary.referenceToNodeSet(obj);
            return dom.makeNode(iter);
        }
        if (obj instanceof DOM) {
            dom = (DOM)obj;
            DTMAxisIterator iter = dom.getChildren(0);
            return dom.makeNode(iter);
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, "org.w3c.dom.Node");
        return null;
    }

    public static long referenceToLong(Object obj) {
        if (obj instanceof Number) {
            return ((Number)obj).longValue();
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, Long.TYPE);
        return 0L;
    }

    public static double referenceToDouble(Object obj) {
        if (obj instanceof Number) {
            return ((Number)obj).doubleValue();
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, Double.TYPE);
        return 0.0;
    }

    public static boolean referenceToBoolean(Object obj) {
        if (obj instanceof Boolean) {
            return (Boolean)obj;
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, Boolean.TYPE);
        return false;
    }

    public static String referenceToString(Object obj, DOM dom) {
        if (obj instanceof String) {
            return (String)obj;
        }
        if (obj instanceof DTMAxisIterator) {
            return dom.getStringValueX(((DTMAxisIterator)obj).reset().next());
        }
        if (obj instanceof Node) {
            return dom.getStringValueX(((Node)obj).node);
        }
        if (obj instanceof DOM) {
            return ((DOM)obj).getStringValue();
        }
        String className = obj.getClass().getName();
        BasisLibrary.runTimeError(DATA_CONVERSION_ERR, className, String.class);
        return null;
    }

    public static DTMAxisIterator node2Iterator(org.w3c.dom.Node node, Translet translet, DOM dom) {
        final org.w3c.dom.Node inNode = node;
        NodeList nodelist = new NodeList(){

            @Override
            public int getLength() {
                return 1;
            }

            @Override
            public org.w3c.dom.Node item(int index) {
                if (index == 0) {
                    return inNode;
                }
                return null;
            }
        };
        return BasisLibrary.nodeList2Iterator(nodelist, translet, dom);
    }

    private static DTMAxisIterator nodeList2IteratorUsingHandleFromNode(NodeList nodeList, Translet translet, DOM dom) {
        int n = nodeList.getLength();
        int[] dtmHandles = new int[n];
        DTMManager dtmManager = null;
        if (dom instanceof MultiDOM) {
            dtmManager = ((MultiDOM)dom).getDTMManager();
        }
        for (int i = 0; i < n; ++i) {
            int handle;
            org.w3c.dom.Node node = nodeList.item(i);
            if (dtmManager != null) {
                handle = dtmManager.getDTMHandleFromNode(node);
            } else if (node instanceof DTMNodeProxy && ((DTMNodeProxy)node).getDTM() == dom) {
                handle = ((DTMNodeProxy)node).getDTMNodeNumber();
            } else {
                BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "need MultiDOM");
                return null;
            }
            dtmHandles[i] = handle;
            System.out.println("Node " + i + " has handle 0x" + Integer.toString(handle, 16));
        }
        return new ArrayNodeListIterator(dtmHandles);
    }

    public static DTMAxisIterator nodeList2Iterator(NodeList nodeList, Translet translet, DOM dom) {
        int n = 0;
        Document doc = null;
        DTMManager dtmManager = null;
        int[] proxyNodes = new int[nodeList.getLength()];
        if (dom instanceof MultiDOM) {
            dtmManager = ((MultiDOM)dom).getDTMManager();
        }
        block12: for (int i = 0; i < nodeList.getLength(); ++i) {
            org.w3c.dom.Node node = nodeList.item(i);
            if (node instanceof DTMNodeProxy) {
                boolean isOurDOM;
                DTMNodeProxy proxy = (DTMNodeProxy)node;
                DTM nodeDTM = proxy.getDTM();
                int handle = proxy.getDTMNodeNumber();
                boolean bl = isOurDOM = nodeDTM == dom;
                if (!isOurDOM && dtmManager != null) {
                    try {
                        isOurDOM = nodeDTM == dtmManager.getDTM(handle);
                    }
                    catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
                        // empty catch block
                    }
                }
                if (isOurDOM) {
                    proxyNodes[i] = handle;
                    ++n;
                    continue;
                }
            }
            proxyNodes[i] = -1;
            short nodeType = node.getNodeType();
            if (doc == null) {
                if (!(dom instanceof MultiDOM)) {
                    BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "need MultiDOM");
                    return null;
                }
                try {
                    AbstractTranslet at = (AbstractTranslet)translet;
                    doc = at.newDocument(EMPTYSTRING, "__top__");
                }
                catch (ParserConfigurationException e) {
                    BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, e.getMessage());
                    return null;
                }
            }
            switch (nodeType) {
                case 1: 
                case 3: 
                case 4: 
                case 5: 
                case 7: 
                case 8: {
                    Element mid = doc.createElementNS(null, "__dummy__");
                    mid.appendChild(doc.importNode(node, true));
                    doc.getDocumentElement().appendChild(mid);
                    ++n;
                    continue block12;
                }
                case 2: {
                    Element mid = doc.createElementNS(null, "__dummy__");
                    mid.setAttributeNodeNS((Attr)doc.importNode(node, true));
                    doc.getDocumentElement().appendChild(mid);
                    ++n;
                    continue block12;
                }
                default: {
                    BasisLibrary.runTimeError(RUN_TIME_INTERNAL_ERR, "Don't know how to convert node type " + nodeType);
                }
            }
        }
        DTMAxisIterator iter = null;
        DTMAxisIterator childIter = null;
        DTMAxisIterator attrIter = null;
        if (doc != null) {
            MultiDOM multiDOM = (MultiDOM)dom;
            DOM idom = (DOM)((Object)dtmManager.getDTM(new DOMSource(doc), false, null, true, false));
            DOMAdapter domAdapter = new DOMAdapter(idom, translet.getNamesArray(), translet.getUrisArray(), translet.getTypesArray(), translet.getNamespaceArray());
            multiDOM.addDOMAdapter(domAdapter);
            DTMAxisIterator iter1 = idom.getAxisIterator(3);
            DTMAxisIterator iter2 = idom.getAxisIterator(3);
            iter = new AbsoluteIterator(new StepIterator(iter1, iter2));
            iter.setStartNode(0);
            childIter = idom.getAxisIterator(3);
            attrIter = idom.getAxisIterator(2);
        }
        int[] dtmHandles = new int[n];
        n = 0;
        for (int i = 0; i < nodeList.getLength(); ++i) {
            if (proxyNodes[i] != -1) {
                dtmHandles[n++] = proxyNodes[i];
                continue;
            }
            org.w3c.dom.Node node = nodeList.item(i);
            DTMAxisIterator iter3 = null;
            short nodeType = node.getNodeType();
            switch (nodeType) {
                case 1: 
                case 3: 
                case 4: 
                case 5: 
                case 7: 
                case 8: {
                    iter3 = childIter;
                    break;
                }
                case 2: {
                    iter3 = attrIter;
                    break;
                }
                default: {
                    throw new InternalRuntimeError("Mismatched cases");
                }
            }
            if (iter3 == null) continue;
            iter3.setStartNode(iter.next());
            dtmHandles[n] = iter3.next();
            if (dtmHandles[n] == -1) {
                throw new InternalRuntimeError("Expected element missing at " + i);
            }
            if (iter3.next() != -1) {
                throw new InternalRuntimeError("Too many elements at " + i);
            }
            ++n;
        }
        if (n != dtmHandles.length) {
            throw new InternalRuntimeError("Nodes lost in second pass");
        }
        return new ArrayNodeListIterator(dtmHandles);
    }

    public static DOM referenceToResultTree(Object obj) {
        try {
            return (DOM)obj;
        }
        catch (IllegalArgumentException e) {
            String className = obj.getClass().getName();
            BasisLibrary.runTimeError(DATA_CONVERSION_ERR, "reference", className);
            return null;
        }
    }

    public static DTMAxisIterator getSingleNode(DTMAxisIterator iterator) {
        int node = iterator.next();
        return new SingletonIterator(node);
    }

    public static void copy(Object obj, SerializationHandler handler, int node, DOM dom) {
        try {
            if (obj instanceof DTMAxisIterator) {
                DTMAxisIterator iter = (DTMAxisIterator)obj;
                dom.copy(iter.reset(), handler);
            } else if (obj instanceof Node) {
                dom.copy(((Node)obj).node, handler);
            } else if (obj instanceof DOM) {
                DOM newDom = (DOM)obj;
                newDom.copy(newDom.getDocument(), handler);
            } else {
                String string = obj.toString();
                int length = string.length();
                if (length > _characterArray.length) {
                    _characterArray = new char[length];
                }
                string.getChars(0, length, _characterArray, 0);
                handler.characters(_characterArray, 0, length);
            }
        }
        catch (SAXException e) {
            BasisLibrary.runTimeError(RUN_TIME_COPY_ERR);
        }
    }

    public static void checkAttribQName(String name) {
        int firstOccur = name.indexOf(58);
        int lastOccur = name.lastIndexOf(58);
        String localName = name.substring(lastOccur + 1);
        if (firstOccur > 0) {
            String oriPrefix;
            String newPrefix = name.substring(0, firstOccur);
            if (firstOccur != lastOccur && !XML11Char.isXML11ValidNCName(oriPrefix = name.substring(firstOccur + 1, lastOccur))) {
                BasisLibrary.runTimeError(INVALID_QNAME_ERR, oriPrefix + ":" + localName);
            }
            if (!XML11Char.isXML11ValidNCName(newPrefix)) {
                BasisLibrary.runTimeError(INVALID_QNAME_ERR, newPrefix + ":" + localName);
            }
        }
        if (!XML11Char.isXML11ValidNCName(localName) || localName.equals("xmlns")) {
            BasisLibrary.runTimeError(INVALID_QNAME_ERR, localName);
        }
    }

    public static void checkNCName(String name) {
        if (!XML11Char.isXML11ValidNCName(name)) {
            BasisLibrary.runTimeError(INVALID_NCNAME_ERR, name);
        }
    }

    public static void checkQName(String name) {
        if (!XML11Char.isXML11ValidQName(name)) {
            BasisLibrary.runTimeError(INVALID_QNAME_ERR, name);
        }
    }

    public static String startXslElement(String qname, String namespace, SerializationHandler handler, DOM dom, int node) {
        try {
            int index = qname.indexOf(58);
            if (index > 0) {
                String prefix = qname.substring(0, index);
                if (namespace == null || namespace.length() == 0) {
                    BasisLibrary.runTimeError(NAMESPACE_PREFIX_ERR, prefix);
                }
                handler.startElement(namespace, qname.substring(index + 1), qname);
                handler.namespaceAfterStartElement(prefix, namespace);
            } else if (namespace != null && namespace.length() > 0) {
                String prefix = BasisLibrary.generatePrefix();
                qname = prefix + ':' + qname;
                handler.startElement(namespace, qname, qname);
                handler.namespaceAfterStartElement(prefix, namespace);
            } else {
                handler.startElement(null, null, qname);
            }
        }
        catch (SAXException e) {
            throw new RuntimeException(e.getMessage());
        }
        return qname;
    }

    public static String lookupStylesheetQNameNamespace(String lexicalQName, int stylesheetNodeID, int[] ancestorNodeIDs, int[] prefixURIsIndex, String[] prefixURIPairs, boolean ignoreDefault) {
        String prefix = BasisLibrary.getPrefix(lexicalQName);
        String uri = EMPTYSTRING;
        if (prefix == null && !ignoreDefault) {
            prefix = EMPTYSTRING;
        }
        if (prefix != null) {
            int currentNodeID = stylesheetNodeID;
            block0: while (currentNodeID >= 0) {
                int prefixStartIdx = prefixURIsIndex[currentNodeID];
                int prefixLimitIdx = currentNodeID + 1 < prefixURIsIndex.length ? prefixURIsIndex[currentNodeID + 1] : prefixURIPairs.length;
                for (int prefixIdx = prefixStartIdx; prefixIdx < prefixLimitIdx; prefixIdx += 2) {
                    if (!prefix.equals(prefixURIPairs[prefixIdx])) continue;
                    uri = prefixURIPairs[prefixIdx + 1];
                    break block0;
                }
                currentNodeID = ancestorNodeIDs[currentNodeID];
            }
        }
        return uri;
    }

    public static String expandStylesheetQNameRef(String lexicalQName, int stylesheetNodeID, int[] ancestorNodeIDs, int[] prefixURIsIndex, String[] prefixURIPairs, boolean ignoreDefault) {
        String prefix = BasisLibrary.getPrefix(lexicalQName);
        String localName = prefix != null ? lexicalQName.substring(prefix.length() + 1) : lexicalQName;
        String uri = BasisLibrary.lookupStylesheetQNameNamespace(lexicalQName, stylesheetNodeID, ancestorNodeIDs, prefixURIsIndex, prefixURIPairs, ignoreDefault);
        if (prefix != null && prefix.length() != 0 && (uri == null || uri.length() == 0)) {
            BasisLibrary.runTimeError(NAMESPACE_PREFIX_ERR, prefix);
        }
        String expandedQName = uri.length() == 0 ? localName : uri + ':' + localName;
        return expandedQName;
    }

    public static String getPrefix(String qname) {
        int index = qname.indexOf(58);
        return index > 0 ? qname.substring(0, index) : null;
    }

    public static String generatePrefix() {
        return "ns" + prefixIndex++;
    }

    public static void runTimeError(String code) {
        throw new RuntimeException(m_bundle.getString(code));
    }

    public static void runTimeError(String code, Object[] args) {
        String message = MessageFormat.format(m_bundle.getString(code), args);
        throw new RuntimeException(message);
    }

    public static void runTimeError(String code, Object arg0) {
        BasisLibrary.runTimeError(code, new Object[]{arg0});
    }

    public static void runTimeError(String code, Object arg0, Object arg1) {
        BasisLibrary.runTimeError(code, new Object[]{arg0, arg1});
    }

    public static void consoleOutput(String msg) {
        System.out.println(msg);
    }

    public static String replace(String base, char ch, String str) {
        return base.indexOf(ch) < 0 ? base : BasisLibrary.replace(base, String.valueOf(ch), new String[]{str});
    }

    public static String replace(String base, String delim, String[] str) {
        int len = base.length();
        StringBuffer result = new StringBuffer();
        for (int i = 0; i < len; ++i) {
            char ch = base.charAt(i);
            int k = delim.indexOf(ch);
            if (k >= 0) {
                result.append(str[k]);
                continue;
            }
            result.append(ch);
        }
        return result.toString();
    }

    public static String mapQNameToJavaName(String base) {
        return BasisLibrary.replace(base, ".-:/{}?#%*", new String[]{"$dot$", "$dash$", "$colon$", "$slash$", EMPTYSTRING, "$colon$", "$ques$", "$hash$", "$per$", "$aster$"});
    }

    static {
        defaultPattern = EMPTYSTRING;
        NumberFormat f = NumberFormat.getInstance(Locale.getDefault());
        defaultFormatter = f instanceof DecimalFormat ? (DecimalFormat)f : new DecimalFormat();
        defaultFormatter.setMaximumFractionDigits(340);
        defaultFormatter.setMinimumFractionDigits(0);
        defaultFormatter.setMinimumIntegerDigits(1);
        defaultFormatter.setGroupingUsed(false);
        _fieldPosition = new FieldPosition(0);
        _characterArray = new char[32];
        prefixIndex = 0;
        String resource = "org.apache.xalan.xsltc.runtime.ErrorMessages";
        m_bundle = ResourceBundle.getBundle(resource);
    }
}

