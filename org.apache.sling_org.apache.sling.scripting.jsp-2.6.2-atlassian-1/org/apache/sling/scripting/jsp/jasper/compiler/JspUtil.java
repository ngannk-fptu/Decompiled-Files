/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.el.ExpressionFactory
 *  javax.el.FunctionMapper
 *  javax.servlet.jsp.el.ExpressionEvaluator
 *  org.apache.sling.commons.compiler.source.JavaEscapeHelper
 */
package org.apache.sling.scripting.jsp.jasper.compiler;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Vector;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import javax.el.ExpressionFactory;
import javax.el.FunctionMapper;
import javax.servlet.jsp.el.ExpressionEvaluator;
import org.apache.sling.commons.compiler.source.JavaEscapeHelper;
import org.apache.sling.scripting.jsp.jasper.Constants;
import org.apache.sling.scripting.jsp.jasper.JasperException;
import org.apache.sling.scripting.jsp.jasper.JspCompilationContext;
import org.apache.sling.scripting.jsp.jasper.compiler.ErrorDispatcher;
import org.apache.sling.scripting.jsp.jasper.compiler.Generator;
import org.apache.sling.scripting.jsp.jasper.compiler.Mark;
import org.apache.sling.scripting.jsp.jasper.compiler.Node;
import org.apache.sling.scripting.jsp.jasper.el.ExpressionEvaluatorImpl;
import org.xml.sax.Attributes;

public class JspUtil {
    private static final String WEB_INF_TAGS = "/WEB-INF/tags/";
    private static final String META_INF_TAGS = "/META-INF/tags/";
    private static final String OPEN_EXPR = "<%=";
    private static final String CLOSE_EXPR = "%>";
    private static final String OPEN_EXPR_XML = "%=";
    private static final String CLOSE_EXPR_XML = "%";
    private static int tempSequenceNumber = 0;
    private static final ExpressionEvaluator expressionEvaluator = new ExpressionEvaluatorImpl(ExpressionFactory.newInstance());
    private static final String[] javaKeywords = new String[]{"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throws", "transient", "try", "void", "volatile", "while"};
    public static final int CHUNKSIZE = 1024;

    public static char[] removeQuotes(char[] chars) {
        CharArrayWriter caw = new CharArrayWriter();
        for (int i = 0; i < chars.length; ++i) {
            if (chars[i] == '%' && chars[i + 1] == '\\' && chars[i + 2] == '>') {
                caw.write(37);
                caw.write(62);
                i += 2;
                continue;
            }
            caw.write(chars[i]);
        }
        return caw.toCharArray();
    }

    public static char[] escapeQuotes(char[] chars) {
        int n;
        String s = new String(chars);
        while ((n = s.indexOf("%\\>")) >= 0) {
            StringBuffer sb = new StringBuffer(s.substring(0, n));
            sb.append(CLOSE_EXPR);
            sb.append(s.substring(n + 3));
            s = sb.toString();
        }
        chars = s.toCharArray();
        return chars;
    }

    public static boolean isExpression(String token, boolean isXml) {
        String closeExpr;
        String openExpr;
        if (isXml) {
            openExpr = OPEN_EXPR_XML;
            closeExpr = CLOSE_EXPR_XML;
        } else {
            openExpr = OPEN_EXPR;
            closeExpr = CLOSE_EXPR;
        }
        return token.startsWith(openExpr) && token.endsWith(closeExpr);
    }

    public static String getExpr(String expression, boolean isXml) {
        String closeExpr;
        String openExpr;
        if (isXml) {
            openExpr = OPEN_EXPR_XML;
            closeExpr = CLOSE_EXPR_XML;
        } else {
            openExpr = OPEN_EXPR;
            closeExpr = CLOSE_EXPR;
        }
        int length = expression.length();
        String returnString = expression.startsWith(openExpr) && expression.endsWith(closeExpr) ? expression.substring(openExpr.length(), length - closeExpr.length()) : "";
        return returnString;
    }

    public static String getExprInXml(String expression) {
        int length = expression.length();
        String returnString = expression.startsWith(OPEN_EXPR) && expression.endsWith(CLOSE_EXPR) ? expression.substring(1, length - 1) : expression;
        return JspUtil.escapeXml(returnString.replace('\u001b', '$'));
    }

    public static void checkScope(String scope, Node n, ErrorDispatcher err) throws JasperException {
        if (!(scope == null || scope.equals("page") || scope.equals("request") || scope.equals("session") || scope.equals("application"))) {
            err.jspError(n, "jsp.error.invalid.scope", scope);
        }
    }

    public static void checkAttributes(String typeOfTag, Node n, ValidAttribute[] validAttributes, ErrorDispatcher err) throws JasperException {
        int attrLeftLength;
        int i;
        Attributes attrs = n.getAttributes();
        Mark start = n.getStart();
        boolean valid = true;
        int tempLength = attrs == null ? 0 : attrs.getLength();
        Vector<String> temp = new Vector<String>(tempLength, 1);
        for (int i2 = 0; i2 < tempLength; ++i2) {
            String qName = attrs.getQName(i2);
            if (qName.equals("xmlns") || qName.startsWith("xmlns:")) continue;
            temp.addElement(qName);
        }
        Node.Nodes tagBody = n.getBody();
        if (tagBody != null) {
            Node node;
            int numSubElements = tagBody.size();
            for (i = 0; i < numSubElements && (node = tagBody.getNode(i)) instanceof Node.NamedAttribute; ++i) {
                String attrName = node.getAttributeValue("name");
                temp.addElement(attrName);
                if (n.getAttributeValue(attrName) == null) continue;
                err.jspError(n, "jsp.error.duplicate.name.jspattribute", attrName);
            }
        }
        String missingAttribute = null;
        for (i = 0; i < validAttributes.length; ++i) {
            if (!validAttributes[i].mandatory) continue;
            int attrPos = temp.indexOf(validAttributes[i].name);
            if (attrPos != -1) {
                temp.remove(attrPos);
                valid = true;
                continue;
            }
            valid = false;
            missingAttribute = validAttributes[i].name;
            break;
        }
        if (!valid) {
            err.jspError(start, "jsp.error.mandatory.attribute", typeOfTag, missingAttribute);
        }
        if ((attrLeftLength = temp.size()) == 0) {
            return;
        }
        String attribute = null;
        for (int j = 0; j < attrLeftLength; ++j) {
            valid = false;
            attribute = (String)temp.elementAt(j);
            for (int i3 = 0; i3 < validAttributes.length; ++i3) {
                if (!attribute.equals(validAttributes[i3].name)) continue;
                valid = true;
                break;
            }
            if (valid) continue;
            err.jspError(start, "jsp.error.invalid.attribute", typeOfTag, attribute);
        }
    }

    public static String escapeQueryString(String unescString) {
        if (unescString == null) {
            return null;
        }
        String escString = "";
        String shellSpChars = "\\\"";
        for (int index = 0; index < unescString.length(); ++index) {
            char nextChar = unescString.charAt(index);
            if (shellSpChars.indexOf(nextChar) != -1) {
                escString = escString + "\\";
            }
            escString = escString + nextChar;
        }
        return escString;
    }

    public static String escapeXml(String s) {
        if (s == null) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            char c = s.charAt(i);
            if (c == '<') {
                sb.append("&lt;");
                continue;
            }
            if (c == '>') {
                sb.append("&gt;");
                continue;
            }
            if (c == '\'') {
                sb.append("&apos;");
                continue;
            }
            if (c == '&') {
                sb.append("&amp;");
                continue;
            }
            if (c == '\"') {
                sb.append("&quot;");
                continue;
            }
            sb.append(c);
        }
        return sb.toString();
    }

    public static String replace(String name, char replace, String with) {
        StringBuffer buf = new StringBuffer();
        int begin = 0;
        int last = name.length();
        while (true) {
            int end;
            if ((end = name.indexOf(replace, begin)) < 0) {
                end = last;
            }
            buf.append(name.substring(begin, end));
            if (end == last) break;
            buf.append(with);
            begin = end + 1;
        }
        return buf.toString();
    }

    public static boolean booleanValue(String s) {
        boolean b = false;
        if (s != null) {
            b = s.equalsIgnoreCase("yes") ? true : Boolean.valueOf(s);
        }
        return b;
    }

    public static Class toClass(String type, ClassLoader loader) throws ClassNotFoundException {
        Class<Comparable<Boolean>> c = null;
        int i0 = type.indexOf(91);
        int dims = 0;
        if (i0 > 0) {
            for (int i = 0; i < type.length(); ++i) {
                if (type.charAt(i) != '[') continue;
                ++dims;
            }
            type = type.substring(0, i0);
        }
        if ("boolean".equals(type)) {
            c = Boolean.TYPE;
        } else if ("char".equals(type)) {
            c = Character.TYPE;
        } else if ("byte".equals(type)) {
            c = Byte.TYPE;
        } else if ("short".equals(type)) {
            c = Short.TYPE;
        } else if ("int".equals(type)) {
            c = Integer.TYPE;
        } else if ("long".equals(type)) {
            c = Long.TYPE;
        } else if ("float".equals(type)) {
            c = Float.TYPE;
        } else if ("double".equals(type)) {
            c = Double.TYPE;
        } else if (type.indexOf(91) < 0) {
            c = loader.loadClass(type);
        }
        if (dims == 0) {
            return c;
        }
        if (dims == 1) {
            return Array.newInstance(c, 1).getClass();
        }
        return Array.newInstance(c, new int[dims]).getClass();
    }

    public static String interpreterCall(boolean isTagFile, String expression, Class expectedType, String fnmapvar, boolean XmlEscape) {
        String jspCtxt = null;
        jspCtxt = isTagFile ? "this.getJspContext()" : "_jspx_page_context";
        String targetType = expectedType.getName();
        String primitiveConverterMethod = null;
        if (expectedType.isPrimitive()) {
            if (expectedType.equals(Boolean.TYPE)) {
                targetType = Boolean.class.getName();
                primitiveConverterMethod = "booleanValue";
            } else if (expectedType.equals(Byte.TYPE)) {
                targetType = Byte.class.getName();
                primitiveConverterMethod = "byteValue";
            } else if (expectedType.equals(Character.TYPE)) {
                targetType = Character.class.getName();
                primitiveConverterMethod = "charValue";
            } else if (expectedType.equals(Short.TYPE)) {
                targetType = Short.class.getName();
                primitiveConverterMethod = "shortValue";
            } else if (expectedType.equals(Integer.TYPE)) {
                targetType = Integer.class.getName();
                primitiveConverterMethod = "intValue";
            } else if (expectedType.equals(Long.TYPE)) {
                targetType = Long.class.getName();
                primitiveConverterMethod = "longValue";
            } else if (expectedType.equals(Float.TYPE)) {
                targetType = Float.class.getName();
                primitiveConverterMethod = "floatValue";
            } else if (expectedType.equals(Double.TYPE)) {
                targetType = Double.class.getName();
                primitiveConverterMethod = "doubleValue";
            }
        }
        if (primitiveConverterMethod != null) {
            XmlEscape = false;
        }
        targetType = JspUtil.toJavaSourceType(targetType);
        StringBuffer call = new StringBuffer("(" + targetType + ") org.apache.sling.scripting.jsp.jasper.runtime.PageContextImpl.proprietaryEvaluate(" + Generator.quote(expression) + ", " + targetType + ".class, (PageContext)" + jspCtxt + ", " + fnmapvar + ", " + XmlEscape + ")");
        if (primitiveConverterMethod != null) {
            call.insert(0, "(");
            call.append(")." + primitiveConverterMethod + "()");
        }
        return call.toString();
    }

    @Deprecated
    public static void validateExpressions(Mark where, String expressions, Class expectedType, FunctionMapper functionMapper, ErrorDispatcher err) throws JasperException {
    }

    @Deprecated
    public static void resetTemporaryVariableName() {
        tempSequenceNumber = 0;
    }

    @Deprecated
    public static String nextTemporaryVariableName() {
        return Constants.TEMP_VARIABLE_NAME_PREFIX + tempSequenceNumber++;
    }

    public static String coerceToPrimitiveBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToBoolean(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "false";
        }
        return Boolean.valueOf(s).toString();
    }

    public static String coerceToBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Boolean) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Boolean.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Boolean(false)";
        }
        return "new Boolean(" + Boolean.valueOf(s).toString() + ")";
    }

    public static String coerceToPrimitiveByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToByte(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(byte) 0";
        }
        return "((byte)" + Byte.valueOf(s).toString() + ")";
    }

    public static String coerceToByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Byte) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Byte.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Byte((byte) 0)";
        }
        return "new Byte((byte)" + Byte.valueOf(s).toString() + ")";
    }

    public static String coerceToChar(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToChar(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(char) 0";
        }
        char ch = s.charAt(0);
        return "((char) " + ch + ")";
    }

    public static String coerceToCharacter(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Character) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Character.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Character((char) 0)";
        }
        char ch = s.charAt(0);
        return "new Character((char) " + ch + ")";
    }

    public static String coerceToPrimitiveDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToDouble(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(double) 0";
        }
        return Double.valueOf(s).toString();
    }

    public static String coerceToDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Double) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Double.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Double(0)";
        }
        return "new Double(" + Double.valueOf(s).toString() + ")";
    }

    public static String coerceToPrimitiveFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToFloat(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(float) 0";
        }
        return Float.valueOf(s).toString() + "f";
    }

    public static String coerceToFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Float) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Float.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Float(0)";
        }
        return "new Float(" + Float.valueOf(s).toString() + "f)";
    }

    public static String coerceToInt(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToInt(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "0";
        }
        return Integer.valueOf(s).toString();
    }

    public static String coerceToInteger(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Integer) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Integer.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Integer(0)";
        }
        return "new Integer(" + Integer.valueOf(s).toString() + ")";
    }

    public static String coerceToPrimitiveShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToShort(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(short) 0";
        }
        return "((short) " + Short.valueOf(s).toString() + ")";
    }

    public static String coerceToShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Short) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Short.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Short((short) 0)";
        }
        return "new Short(\"" + Short.valueOf(s).toString() + "\")";
    }

    public static String coerceToPrimitiveLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerceToLong(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(long) 0";
        }
        return Long.valueOf(s).toString() + "l";
    }

    public static String coerceToLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(Long) org.apache.sling.scripting.jsp.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Long.class)";
        }
        if (s == null || s.length() == 0) {
            return "new Long(0)";
        }
        return "new Long(" + Long.valueOf(s).toString() + "l)";
    }

    public static InputStream getInputStream(String fname, JarFile jarFile, JspCompilationContext ctxt, ErrorDispatcher err) throws JasperException, IOException {
        InputStream in = null;
        if (jarFile != null) {
            String jarEntryName = fname.substring(1, fname.length());
            ZipEntry jarEntry = jarFile.getEntry(jarEntryName);
            if (jarEntry == null) {
                err.jspError("jsp.error.file.not.found", fname);
            }
            in = jarFile.getInputStream(jarEntry);
        } else {
            URL url;
            if (fname.startsWith(META_INF_TAGS) && (url = ctxt.getTagFileUrl(fname)) != null) {
                return url.openConnection().getInputStream();
            }
            in = ctxt.getResourceAsStream(fname);
            if (in == null) {
                in = ctxt.getInputStream(fname);
            }
        }
        if (in == null) {
            err.jspError("jsp.error.file.not.found", fname);
        }
        return in;
    }

    public static String getTagHandlerClassName(String path, ErrorDispatcher err) throws JasperException {
        String className = null;
        int begin = 0;
        int index = path.lastIndexOf(".tag");
        if (index == -1) {
            err.jspError("jsp.error.tagfile.badSuffix", path);
        }
        if ((index = path.indexOf(WEB_INF_TAGS)) != -1) {
            className = "org.apache.jsp.tag.web.";
            begin = index + WEB_INF_TAGS.length();
        } else {
            index = path.indexOf(META_INF_TAGS);
            if (index != -1) {
                className = "org.apache.jsp.tag.meta.";
                begin = index + META_INF_TAGS.length();
            } else {
                err.jspError("jsp.error.tagfile.illegalPath", path);
            }
        }
        className = className + JavaEscapeHelper.makeJavaPackage((String)path.substring(begin));
        return className;
    }

    private static final String[] split(String path, String pat) {
        Vector<String> comps = new Vector<String>();
        int pos = path.indexOf(pat);
        int start = 0;
        while (pos >= 0) {
            if (pos > start) {
                String comp = path.substring(start, pos);
                comps.add(comp);
            }
            start = pos + pat.length();
            pos = path.indexOf(pat, start);
        }
        if (start < path.length()) {
            comps.add(path.substring(start));
        }
        String[] result = new String[comps.size()];
        for (int i = 0; i < comps.size(); ++i) {
            result[i] = (String)comps.elementAt(i);
        }
        return result;
    }

    public static final String makeXmlJavaIdentifier(String name) {
        if (name.indexOf(45) >= 0) {
            name = JspUtil.replace(name, '-', "$1");
        }
        if (name.indexOf(46) >= 0) {
            name = JspUtil.replace(name, '.', "$2");
        }
        if (name.indexOf(58) >= 0) {
            name = JspUtil.replace(name, ':', "$3");
        }
        return name;
    }

    static InputStreamReader getReader(String fname, String encoding, JarFile jarFile, JspCompilationContext ctxt, ErrorDispatcher err) throws JasperException, IOException {
        return JspUtil.getReader(fname, encoding, jarFile, ctxt, err, 0);
    }

    static InputStreamReader getReader(String fname, String encoding, JarFile jarFile, JspCompilationContext ctxt, ErrorDispatcher err, int skip) throws JasperException, IOException {
        InputStreamReader reader = null;
        InputStream in = JspUtil.getInputStream(fname, jarFile, ctxt, err);
        for (int i = 0; i < skip; ++i) {
            in.read();
        }
        try {
            reader = new InputStreamReader(in, encoding);
        }
        catch (UnsupportedEncodingException ex) {
            err.jspError("jsp.error.unsupported.encoding", encoding);
        }
        return reader;
    }

    public static String toJavaSourceTypeFromTld(String type) {
        if (type == null || "void".equals(type)) {
            return "Void.TYPE";
        }
        return type + ".class";
    }

    public static String toJavaSourceType(String type) {
        if (type.charAt(0) != '[') {
            return type;
        }
        int dims = 1;
        String t = null;
        for (int i = 1; i < type.length(); ++i) {
            if (type.charAt(i) == '[') {
                ++dims;
                continue;
            }
            switch (type.charAt(i)) {
                case 'Z': {
                    t = "boolean";
                    break;
                }
                case 'B': {
                    t = "byte";
                    break;
                }
                case 'C': {
                    t = "char";
                    break;
                }
                case 'D': {
                    t = "double";
                    break;
                }
                case 'F': {
                    t = "float";
                    break;
                }
                case 'I': {
                    t = "int";
                    break;
                }
                case 'J': {
                    t = "long";
                    break;
                }
                case 'S': {
                    t = "short";
                    break;
                }
                case 'L': {
                    t = type.substring(i + 1, type.indexOf(59));
                }
            }
            break;
        }
        StringBuffer resultType = new StringBuffer(t);
        while (dims > 0) {
            resultType.append("[]");
            --dims;
        }
        return resultType.toString();
    }

    public static String getCanonicalName(Class c) {
        String binaryName = c.getName();
        if ((c = c.getDeclaringClass()) == null) {
            return binaryName;
        }
        StringBuffer buf = new StringBuffer(binaryName);
        do {
            buf.setCharAt(c.getName().length(), '.');
        } while ((c = c.getDeclaringClass()) != null);
        return buf.toString();
    }

    public static class ValidAttribute {
        String name;
        boolean mandatory;
        boolean rtexprvalue;

        public ValidAttribute(String name, boolean mandatory, boolean rtexprvalue) {
            this.name = name;
            this.mandatory = mandatory;
            this.rtexprvalue = rtexprvalue;
        }

        public ValidAttribute(String name, boolean mandatory) {
            this(name, mandatory, false);
        }

        public ValidAttribute(String name) {
            this(name, false);
        }
    }
}

