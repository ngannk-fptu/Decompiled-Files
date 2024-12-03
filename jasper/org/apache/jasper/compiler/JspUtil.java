/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.Jar
 *  org.apache.tomcat.util.security.Escape
 */
package org.apache.jasper.compiler;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import org.apache.jasper.Constants;
import org.apache.jasper.JasperException;
import org.apache.jasper.JspCompilationContext;
import org.apache.jasper.compiler.ErrorDispatcher;
import org.apache.jasper.compiler.Generator;
import org.apache.jasper.compiler.Localizer;
import org.apache.jasper.compiler.Mark;
import org.apache.jasper.compiler.Node;
import org.apache.tomcat.Jar;
import org.apache.tomcat.util.security.Escape;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;

public class JspUtil {
    private static final String WEB_INF_TAGS = "/WEB-INF/tags/";
    private static final String META_INF_TAGS = "/META-INF/tags/";
    private static final String OPEN_EXPR = "<%=";
    private static final String CLOSE_EXPR = "%>";
    private static final String[] javaKeywords = new String[]{"abstract", "assert", "boolean", "break", "byte", "case", "catch", "char", "class", "const", "continue", "default", "do", "double", "else", "enum", "extends", "final", "finally", "float", "for", "goto", "if", "implements", "import", "instanceof", "int", "interface", "long", "native", "new", "package", "private", "protected", "public", "return", "short", "static", "strictfp", "super", "switch", "synchronized", "this", "throw", "throws", "transient", "try", "void", "volatile", "while"};
    static final int JSP_INPUT_STREAM_BUFFER_SIZE = 1024;
    public static final int CHUNKSIZE = 1024;

    public static String getExprInXml(String expression) {
        int length = expression.length();
        String returnString = expression.startsWith(OPEN_EXPR) && expression.endsWith(CLOSE_EXPR) ? expression.substring(1, length - 1) : expression;
        return Escape.xml((String)returnString);
    }

    public static void checkScope(String scope, Node n, ErrorDispatcher err) throws JasperException {
        if (!(scope == null || scope.equals("page") || scope.equals("request") || scope.equals("session") || scope.equals("application"))) {
            err.jspError(n, "jsp.error.invalid.scope", scope);
        }
    }

    public static void checkAttributes(String typeOfTag, Node n, ValidAttribute[] validAttributes, ErrorDispatcher err) throws JasperException {
        int attrLeftLength;
        Attributes attrs = n.getAttributes();
        Mark start = n.getStart();
        boolean valid = true;
        int tempLength = attrs == null ? 0 : attrs.getLength();
        ArrayList<String> temp = new ArrayList<String>(tempLength);
        for (int i = 0; i < tempLength; ++i) {
            String qName = attrs.getQName(i);
            if (qName.equals("xmlns") || qName.startsWith("xmlns:")) continue;
            temp.add(qName);
        }
        Node.Nodes tagBody = n.getBody();
        if (tagBody != null) {
            Node node;
            int numSubElements = tagBody.size();
            for (int i = 0; i < numSubElements && (node = tagBody.getNode(i)) instanceof Node.NamedAttribute; ++i) {
                String attrName = node.getAttributeValue("name");
                temp.add(attrName);
                if (n.getAttributeValue(attrName) == null) continue;
                err.jspError(n, "jsp.error.duplicate.name.jspattribute", attrName);
            }
        }
        String missingAttribute = null;
        for (ValidAttribute validAttribute : validAttributes) {
            if (!validAttribute.mandatory) continue;
            int attrPos = temp.indexOf(validAttribute.name);
            if (attrPos != -1) {
                temp.remove(attrPos);
                valid = true;
                continue;
            }
            valid = false;
            missingAttribute = validAttribute.name;
            break;
        }
        if (!valid) {
            err.jspError(start, "jsp.error.mandatory.attribute", typeOfTag, missingAttribute);
        }
        if ((attrLeftLength = temp.size()) == 0) {
            return;
        }
        for (String attribute : temp) {
            valid = false;
            for (ValidAttribute validAttribute : validAttributes) {
                if (!attribute.equals(validAttribute.name)) continue;
                valid = true;
                break;
            }
            if (valid) continue;
            err.jspError(start, "jsp.error.invalid.attribute", typeOfTag, attribute);
        }
    }

    public static boolean booleanValue(String s) {
        boolean b = false;
        if (s != null) {
            b = s.equalsIgnoreCase("yes") ? true : Boolean.parseBoolean(s);
        }
        return b;
    }

    public static Class<?> toClass(String type, ClassLoader loader) throws ClassNotFoundException {
        Class<Object> c = null;
        int i0 = type.indexOf(91);
        int dims = 0;
        if (i0 > 0) {
            for (int i = 0; i < type.length(); ++i) {
                if (type.charAt(i) != '[') continue;
                ++dims;
            }
            type = type.substring(0, i0);
        }
        c = "boolean".equals(type) ? Boolean.TYPE : ("char".equals(type) ? Character.TYPE : ("byte".equals(type) ? Byte.TYPE : ("short".equals(type) ? Short.TYPE : ("int".equals(type) ? Integer.TYPE : ("long".equals(type) ? Long.TYPE : ("float".equals(type) ? Float.TYPE : ("double".equals(type) ? Double.TYPE : ("void".equals(type) ? Void.TYPE : loader.loadClass(type)))))))));
        if (dims == 0) {
            return c;
        }
        if (dims == 1) {
            return Array.newInstance(c, 1).getClass();
        }
        return Array.newInstance(c, new int[dims]).getClass();
    }

    public static String interpreterCall(boolean isTagFile, String expression, Class<?> expectedType, String fnmapvar) {
        String returnType;
        String jspCtxt = null;
        jspCtxt = isTagFile ? "this.getJspContext()" : "_jspx_page_context";
        String targetType = returnType = expectedType.getCanonicalName();
        String primitiveConverterMethod = null;
        if (expectedType.isPrimitive()) {
            if (expectedType.equals(Boolean.TYPE)) {
                returnType = Boolean.class.getName();
                primitiveConverterMethod = "booleanValue";
            } else if (expectedType.equals(Byte.TYPE)) {
                returnType = Byte.class.getName();
                primitiveConverterMethod = "byteValue";
            } else if (expectedType.equals(Character.TYPE)) {
                returnType = Character.class.getName();
                primitiveConverterMethod = "charValue";
            } else if (expectedType.equals(Short.TYPE)) {
                returnType = Short.class.getName();
                primitiveConverterMethod = "shortValue";
            } else if (expectedType.equals(Integer.TYPE)) {
                returnType = Integer.class.getName();
                primitiveConverterMethod = "intValue";
            } else if (expectedType.equals(Long.TYPE)) {
                returnType = Long.class.getName();
                primitiveConverterMethod = "longValue";
            } else if (expectedType.equals(Float.TYPE)) {
                returnType = Float.class.getName();
                primitiveConverterMethod = "floatValue";
            } else if (expectedType.equals(Double.TYPE)) {
                returnType = Double.class.getName();
                primitiveConverterMethod = "doubleValue";
            }
        }
        targetType = JspUtil.toJavaSourceType(targetType);
        StringBuilder call = new StringBuilder("(" + returnType + ") org.apache.jasper.runtime.PageContextImpl.proprietaryEvaluate(" + Generator.quote(expression) + ", " + targetType + ".class, (javax.servlet.jsp.PageContext)" + jspCtxt + ", " + fnmapvar + ")");
        if (primitiveConverterMethod != null) {
            call.insert(0, "(");
            call.append(")." + primitiveConverterMethod + "()");
        }
        return call.toString();
    }

    public static String coerceToPrimitiveBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToBoolean(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "false";
        }
        return Boolean.valueOf(s).toString();
    }

    public static String coerceToBoolean(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Boolean) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Boolean.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Boolean.FALSE";
        }
        return "java.lang.Boolean.valueOf(" + Generator.quote(s) + ")";
    }

    public static String coerceToPrimitiveByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToByte(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(byte) 0";
        }
        return "((byte)" + Byte.valueOf(s).toString() + ")";
    }

    public static String coerceToByte(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Byte) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Byte.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Byte.valueOf((byte) 0)";
        }
        return "java.lang.Byte.valueOf(" + Generator.quote(s) + ")";
    }

    public static String coerceToChar(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToChar(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(char) 0";
        }
        char ch = s.charAt(0);
        return "((char) " + ch + ")";
    }

    public static String coerceToCharacter(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Character) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Character.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Character.valueOf((char) 0)";
        }
        char ch = s.charAt(0);
        return "java.lang.Character.valueOf((char) " + ch + ")";
    }

    public static String coerceToPrimitiveDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToDouble(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(double) 0";
        }
        return Double.valueOf(s).toString();
    }

    public static String coerceToDouble(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Double) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", Double.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Double.valueOf(0)";
        }
        return "java.lang.Double.valueOf(" + Generator.quote(s) + ")";
    }

    public static String coerceToPrimitiveFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToFloat(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(float) 0";
        }
        return Float.valueOf(s).toString() + "f";
    }

    public static String coerceToFloat(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Float) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Float.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Float.valueOf(0)";
        }
        return "java.lang.Float.valueOf(" + Generator.quote(s) + ")";
    }

    public static String coerceToInt(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToInt(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "0";
        }
        return Integer.valueOf(s).toString();
    }

    public static String coerceToInteger(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Integer) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Integer.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Integer.valueOf(0)";
        }
        return "java.lang.Integer.valueOf(" + Generator.quote(s) + ")";
    }

    public static String coerceToPrimitiveShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToShort(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(short) 0";
        }
        return "((short) " + Short.valueOf(s).toString() + ")";
    }

    public static String coerceToShort(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Short) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Short.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Short.valueOf((short) 0)";
        }
        return "java.lang.Short.valueOf(" + Generator.quote(s) + ")";
    }

    public static String coerceToPrimitiveLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "org.apache.jasper.runtime.JspRuntimeLibrary.coerceToLong(" + s + ")";
        }
        if (s == null || s.length() == 0) {
            return "(long) 0";
        }
        return Long.valueOf(s).toString() + "l";
    }

    public static String coerceToLong(String s, boolean isNamedAttribute) {
        if (isNamedAttribute) {
            return "(java.lang.Long) org.apache.jasper.runtime.JspRuntimeLibrary.coerce(" + s + ", java.lang.Long.class)";
        }
        if (s == null || s.length() == 0) {
            return "java.lang.Long.valueOf(0)";
        }
        return "java.lang.Long.valueOf(" + Generator.quote(s) + ")";
    }

    public static BufferedInputStream getInputStream(String fname, Jar jar, JspCompilationContext ctxt) throws IOException {
        InputStream in = null;
        if (jar != null) {
            String jarEntryName = fname.substring(1);
            in = jar.getInputStream(jarEntryName);
        } else {
            in = ctxt.getResourceAsStream(fname);
        }
        if (in == null) {
            throw new FileNotFoundException(Localizer.getMessage("jsp.error.file.not.found", fname));
        }
        return new BufferedInputStream(in, 1024);
    }

    public static InputSource getInputSource(String fname, Jar jar, JspCompilationContext ctxt) throws IOException {
        InputSource source;
        if (jar != null) {
            String jarEntryName = fname.substring(1);
            source = new InputSource(jar.getInputStream(jarEntryName));
            source.setSystemId(jar.getURL(jarEntryName));
        } else {
            source = new InputSource(ctxt.getResourceAsStream(fname));
            source.setSystemId(ctxt.getResource(fname).toExternalForm());
        }
        return source;
    }

    public static String getTagHandlerClassName(String path, String urn, ErrorDispatcher err) throws JasperException {
        String className = null;
        int begin = 0;
        int index = path.lastIndexOf(".tag");
        if (index == -1) {
            err.jspError("jsp.error.tagfile.badSuffix", path);
        }
        if ((index = path.indexOf(WEB_INF_TAGS)) != -1) {
            className = Constants.TAG_FILE_PACKAGE_NAME + ".web.";
            begin = index + WEB_INF_TAGS.length();
        } else {
            index = path.indexOf(META_INF_TAGS);
            if (index != -1) {
                className = JspUtil.getClassNameBase(urn);
                begin = index + META_INF_TAGS.length();
            } else {
                err.jspError("jsp.error.tagfile.illegalPath", path);
            }
        }
        className = className + JspUtil.makeJavaPackage(path.substring(begin));
        return className;
    }

    private static String getClassNameBase(String urn) {
        StringBuilder base = new StringBuilder(Constants.TAG_FILE_PACKAGE_NAME + ".meta.");
        if (urn != null) {
            base.append(JspUtil.makeJavaPackage(urn));
            base.append('.');
        }
        return base.toString();
    }

    public static final String makeJavaPackage(String path) {
        String[] classNameComponents = path.split("/");
        StringBuilder legalClassNames = new StringBuilder();
        for (String classNameComponent : classNameComponents) {
            if (classNameComponent.length() <= 0) continue;
            if (legalClassNames.length() > 0) {
                legalClassNames.append('.');
            }
            legalClassNames.append(JspUtil.makeJavaIdentifier(classNameComponent));
        }
        return legalClassNames.toString();
    }

    public static final String makeJavaIdentifier(String identifier) {
        return JspUtil.makeJavaIdentifier(identifier, true);
    }

    public static final String makeJavaIdentifierForAttribute(String identifier) {
        return JspUtil.makeJavaIdentifier(identifier, false);
    }

    private static String makeJavaIdentifier(String identifier, boolean periodToUnderscore) {
        StringBuilder modifiedIdentifier = new StringBuilder(identifier.length());
        if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
            modifiedIdentifier.append('_');
        }
        for (int i = 0; i < identifier.length(); ++i) {
            char ch = identifier.charAt(i);
            if (Character.isJavaIdentifierPart(ch) && (ch != '_' || !periodToUnderscore)) {
                modifiedIdentifier.append(ch);
                continue;
            }
            if (ch == '.' && periodToUnderscore) {
                modifiedIdentifier.append('_');
                continue;
            }
            modifiedIdentifier.append(JspUtil.mangleChar(ch));
        }
        if (JspUtil.isJavaKeyword(modifiedIdentifier.toString())) {
            modifiedIdentifier.append('_');
        }
        return modifiedIdentifier.toString();
    }

    public static final String mangleChar(char ch) {
        char[] result = new char[]{'_', Character.forDigit(ch >> 12 & 0xF, 16), Character.forDigit(ch >> 8 & 0xF, 16), Character.forDigit(ch >> 4 & 0xF, 16), Character.forDigit(ch & 0xF, 16)};
        return new String(result);
    }

    public static boolean isJavaKeyword(String key) {
        int i = 0;
        int j = javaKeywords.length;
        while (i < j) {
            int k = i + j >>> 1;
            int result = javaKeywords[k].compareTo(key);
            if (result == 0) {
                return true;
            }
            if (result < 0) {
                i = k + 1;
                continue;
            }
            j = k;
        }
        return false;
    }

    static InputStreamReader getReader(String fname, String encoding, Jar jar, JspCompilationContext ctxt, ErrorDispatcher err) throws JasperException, IOException {
        return JspUtil.getReader(fname, encoding, jar, ctxt, err, 0);
    }

    static InputStreamReader getReader(String fname, String encoding, Jar jar, JspCompilationContext ctxt, ErrorDispatcher err, int skip) throws JasperException, IOException {
        InputStreamReader reader = null;
        BufferedInputStream in = JspUtil.getInputStream(fname, jar, ctxt);
        try {
            for (int i = 0; i < skip; ++i) {
                ((InputStream)in).read();
            }
        }
        catch (IOException ioe) {
            try {
                ((InputStream)in).close();
            }
            catch (IOException iOException) {
                // empty catch block
            }
            throw ioe;
        }
        try {
            reader = new InputStreamReader((InputStream)in, encoding);
        }
        catch (UnsupportedEncodingException ex) {
            err.jspError("jsp.error.unsupported.encoding", encoding);
        }
        return reader;
    }

    public static String toJavaSourceTypeFromTld(String type) {
        if (type == null || "void".equals(type)) {
            return "java.lang.Void.TYPE";
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
        if (t == null) {
            throw new IllegalArgumentException(Localizer.getMessage("jsp.error.unable.getType", type));
        }
        StringBuilder resultType = new StringBuilder(t);
        while (dims > 0) {
            resultType.append("[]");
            --dims;
        }
        return resultType.toString();
    }

    public static class ValidAttribute {
        private final String name;
        private final boolean mandatory;

        public ValidAttribute(String name, boolean mandatory) {
            this.name = name;
            this.mandatory = mandatory;
        }

        public ValidAttribute(String name) {
            this(name, false);
        }
    }
}

