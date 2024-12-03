/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.schema;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.SchemaCodePrinter;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.common.NameUtil;
import org.apache.xmlbeans.impl.repackage.Repackager;
import org.apache.xmlbeans.impl.schema.AbstractDocumentFactory;
import org.apache.xmlbeans.impl.schema.DocumentFactory;
import org.apache.xmlbeans.impl.schema.ElementFactory;
import org.apache.xmlbeans.impl.schema.SchemaTypeImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.impl.schema.SimpleTypeFactory;

public final class SchemaTypeCodePrinter
implements SchemaCodePrinter {
    static final String INDEX_CLASSNAME = "TypeSystemHolder";
    private static final String MAX_SPACES = "                                        ";
    private static final int INDENT_INCREMENT = 4;
    private Writer _writer;
    private int _indent = 0;
    private XmlOptions opt;
    private static final int NOTHING = 1;
    private static final int ADD_NEW_VALUE = 3;
    private static final int THROW_EXCEPTION = 4;

    void indent() {
        this._indent += 4;
    }

    void outdent() {
        this._indent -= 4;
    }

    void emit(String s, XmlOptions.BeanMethod method) throws IOException {
        Set<XmlOptions.BeanMethod> partMet;
        Set<XmlOptions.BeanMethod> set = partMet = this.opt == null ? null : this.opt.getCompilePartialMethod();
        if (partMet == null || partMet.contains((Object)method)) {
            this.emit(s);
        }
    }

    void emit(String s) throws IOException {
        if (!s.trim().isEmpty()) {
            int indent = this._indent;
            if (indent > MAX_SPACES.length() / 2) {
                indent = MAX_SPACES.length() / 4 + indent / 2;
            }
            if (indent > MAX_SPACES.length()) {
                indent = MAX_SPACES.length();
            }
            this._writer.write(MAX_SPACES.substring(0, indent));
        }
        try {
            this._writer.write(s);
        }
        catch (CharacterCodingException cce) {
            this._writer.write(SchemaTypeCodePrinter.makeSafe(s));
        }
        this._writer.write(System.lineSeparator());
    }

    private static String makeSafe(String s) {
        char c;
        int i;
        Charset charset = Charset.forName(System.getProperty("file.encoding"));
        CharsetEncoder cEncoder = charset.newEncoder();
        StringBuilder result = new StringBuilder();
        for (i = 0; i < s.length() && cEncoder.canEncode(c = s.charAt(i)); ++i) {
        }
        while (i < s.length()) {
            c = s.charAt(i);
            if (cEncoder.canEncode(c)) {
                result.append(c);
            } else {
                String hexValue = Integer.toHexString(c);
                switch (hexValue.length()) {
                    case 1: {
                        result.append("\\u000").append(hexValue);
                        break;
                    }
                    case 2: {
                        result.append("\\u00").append(hexValue);
                        break;
                    }
                    case 3: {
                        result.append("\\u0").append(hexValue);
                        break;
                    }
                    case 4: {
                        result.append("\\u").append(hexValue);
                        break;
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
            }
            ++i;
        }
        return result.toString();
    }

    @Override
    public void printType(Writer writer, SchemaType sType, XmlOptions opt) throws IOException {
        this.opt = opt;
        this._writer = writer;
        this.printTopComment(sType);
        this.printPackage(sType, true);
        this.emit("");
        this.emit("import " + ElementFactory.class.getName() + ";");
        this.emit("import " + AbstractDocumentFactory.class.getName() + ";");
        this.emit("import " + DocumentFactory.class.getName() + ";");
        this.emit("import " + SimpleTypeFactory.class.getName() + ";");
        this.emit("");
        this.printInnerType(sType, sType.getTypeSystem());
        this._writer.flush();
    }

    @Override
    public void printTypeImpl(Writer writer, SchemaType sType, XmlOptions opt) throws IOException {
        this.opt = opt;
        this._writer = writer;
        this.printTopComment(sType);
        this.printPackage(sType, false);
        this.emit("");
        this.emit("import javax.xml.namespace.QName;");
        this.emit("import org.apache.xmlbeans.QNameSet;");
        this.emit("import org.apache.xmlbeans.XmlObject;");
        this.emit("");
        this.printInnerTypeImpl(sType, sType.getTypeSystem(), false);
    }

    String findJavaType(SchemaType sType) {
        while (sType.getFullJavaName() == null) {
            sType = sType.getBaseType();
        }
        return sType.getFullJavaName();
    }

    static String prettyQName(QName qname) {
        if (qname == null) {
            return "";
        }
        String result = qname.getLocalPart();
        if (qname.getNamespaceURI() != null) {
            result = result + "(@" + qname.getNamespaceURI() + ")";
        }
        return result;
    }

    void printInnerTypeJavaDoc(SchemaType sType) throws IOException {
        QName name = sType.getName();
        if (name == null) {
            if (sType.isDocumentType()) {
                name = sType.getDocumentElementName();
            } else if (sType.isAttributeType()) {
                name = sType.getAttributeTypeAttributeName();
            } else if (sType.getContainerField() != null) {
                name = sType.getContainerField().getName();
            }
        }
        this.emit("/**");
        if (this.opt.isCompileAnnotationAsJavadoc() && sType.getDocumentation() != null && sType.getDocumentation().length() > 0) {
            this.emit(" *");
            this.printJavaDocBody(sType.getDocumentation());
            this.emit(" *");
        }
        if (sType.isDocumentType()) {
            this.emit(" * A document containing one " + SchemaTypeCodePrinter.prettyQName(name) + " element.");
        } else if (sType.isAttributeType()) {
            this.emit(" * A document containing one " + SchemaTypeCodePrinter.prettyQName(name) + " attribute.");
        } else if (name != null) {
            this.emit(" * An XML " + SchemaTypeCodePrinter.prettyQName(name) + ".");
        } else {
            this.emit(" * An anonymous inner XML type.");
        }
        this.emit(" *");
        switch (sType.getSimpleVariety()) {
            case 0: {
                this.emit(" * This is a complex type.");
                break;
            }
            case 1: {
                this.emit(" * This is an atomic type that is a restriction of " + this.getFullJavaName(sType) + ".");
                break;
            }
            case 3: {
                this.emit(" * This is a list type whose items are " + sType.getListItemType().getFullJavaName() + ".");
                break;
            }
            case 2: {
                SchemaType[] members;
                this.emit(" * This is a union type. Instances are of one of the following types:");
                for (SchemaType member : members = sType.getUnionConstituentTypes()) {
                    this.emit(" *     " + member.getFullJavaName());
                }
                break;
            }
        }
        this.emit(" */");
    }

    private String getFullJavaName(SchemaType sType) {
        SchemaTypeImpl sTypeI;
        String ret = sTypeI.getFullJavaName();
        for (sTypeI = (SchemaTypeImpl)sType; sTypeI != null && sTypeI.isRedefinition(); sTypeI = (SchemaTypeImpl)sTypeI.getBaseType()) {
            ret = sTypeI.getFullJavaName();
        }
        return ret;
    }

    private String getUserTypeStaticHandlerMethod(boolean encode, SchemaTypeImpl stype) {
        String unqualifiedName = stype.getName().getLocalPart();
        unqualifiedName = unqualifiedName.length() < 2 ? unqualifiedName.toUpperCase(Locale.ROOT) : unqualifiedName.substring(0, 1).toUpperCase(Locale.ROOT) + unqualifiedName.substring(1);
        if (encode) {
            return stype.getUserTypeHandlerName() + ".encode" + unqualifiedName;
        }
        return stype.getUserTypeHandlerName() + ".decode" + unqualifiedName;
    }

    public static String indexClassForSystem(SchemaTypeSystem system) {
        String name = system.getName();
        return name + "." + INDEX_CLASSNAME;
    }

    void printStaticTypeDeclaration(SchemaType sType, SchemaTypeSystem system) throws IOException {
        Class factoryClass = sType.isAnonymousType() && !sType.isDocumentType() && !sType.isAttributeType() ? ElementFactory.class : (sType.isSimpleType() ? SimpleTypeFactory.class : (sType.isAbstract() ? AbstractDocumentFactory.class : DocumentFactory.class));
        String factoryName = factoryClass.getSimpleName();
        String fullName = sType.getFullJavaName().replace('$', '.');
        String sysName = sType.getTypeSystem().getName();
        this.emit(factoryName + "<" + fullName + "> Factory = new " + factoryName + "<>(" + sysName + ".TypeSystemHolder.typeSystem, \"" + ((SchemaTypeSystemImpl)system).handleForType(sType) + "\");");
        this.emit("org.apache.xmlbeans.SchemaType type = Factory.getType();");
        this.emit("");
    }

    void printInnerType(SchemaType sType, SchemaTypeSystem system) throws IOException {
        this.emit("");
        this.printInnerTypeJavaDoc(sType);
        this.startInterface(sType);
        this.printStaticTypeDeclaration(sType, system);
        if (sType.isSimpleType()) {
            if (sType.hasStringEnumValues()) {
                this.printStringEnumeration(sType);
            }
        } else {
            SchemaProperty[] props;
            if (sType.getContentType() == 2 && sType.hasStringEnumValues()) {
                this.printStringEnumeration(sType);
            }
            for (SchemaProperty prop : props = this.getDerivedProperties(sType)) {
                this.printPropertyGetters(prop);
                if (prop.isReadOnly()) continue;
                this.printPropertySetters(prop);
            }
        }
        this.printNestedInnerTypes(sType, system);
        this.endBlock();
    }

    void printNestedInnerTypes(SchemaType sType, SchemaTypeSystem system) throws IOException {
        boolean redefinition;
        boolean bl = redefinition = sType.getName() != null && sType.getName().equals(sType.getBaseType().getName());
        while (sType != null) {
            SchemaType[] anonTypes;
            for (SchemaType anonType : anonTypes = sType.getAnonymousTypes()) {
                if (anonType.isSkippedAnonymousType()) {
                    this.printNestedInnerTypes(anonType, system);
                    continue;
                }
                this.printInnerType(anonType, system);
            }
            if (!redefinition || sType.getDerivationType() != 2 && !sType.isSimpleType()) break;
            sType = sType.getBaseType();
        }
    }

    void printTopComment(SchemaType sType) throws IOException {
        this.emit("/*");
        if (sType.getName() != null) {
            this.emit(" * XML Type:  " + sType.getName().getLocalPart());
            this.emit(" * Namespace: " + sType.getName().getNamespaceURI());
        } else {
            QName thename = null;
            if (sType.isDocumentType()) {
                thename = sType.getDocumentElementName();
                this.emit(" * An XML document type.");
            } else if (sType.isAttributeType()) {
                thename = sType.getAttributeTypeAttributeName();
                this.emit(" * An XML attribute type.");
            } else assert (false);
            assert (thename != null);
            this.emit(" * Localname: " + thename.getLocalPart());
            this.emit(" * Namespace: " + thename.getNamespaceURI());
        }
        this.emit(" * Java type: " + sType.getFullJavaName());
        this.emit(" *");
        this.emit(" * Automatically generated - do not modify.");
        this.emit(" */");
    }

    void printPackage(SchemaType sType, boolean intf) throws IOException {
        String fqjn = intf ? sType.getFullJavaName() : sType.getFullJavaImplName();
        int lastdot = fqjn.lastIndexOf(46);
        if (lastdot < 0) {
            return;
        }
        String pkg = fqjn.substring(0, lastdot);
        this.emit("package " + pkg + ";");
    }

    void startInterface(SchemaType sType) throws IOException {
        String shortName = sType.getShortJavaName();
        String baseInterface = this.findJavaType(sType.getBaseType());
        this.emit("public interface " + shortName + " extends " + baseInterface + SchemaTypeCodePrinter.getExtensionInterfaces(sType) + " {");
        this.indent();
        this.emitSpecializedAccessors(sType);
    }

    private static String getExtensionInterfaces(SchemaType sType) {
        SchemaTypeImpl sImpl = SchemaTypeCodePrinter.getImpl(sType);
        if (sImpl == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        InterfaceExtension[] exts = sImpl.getInterfaceExtensions();
        if (exts != null) {
            for (InterfaceExtension ext : exts) {
                sb.append(", ").append(ext.getInterface());
            }
        }
        return sb.toString();
    }

    private static SchemaTypeImpl getImpl(SchemaType sType) {
        if (sType instanceof SchemaTypeImpl) {
            return (SchemaTypeImpl)sType;
        }
        return null;
    }

    private void emitSpecializedAccessors(SchemaType sType) throws IOException {
        int parentBits;
        int bits;
        if (sType.getSimpleVariety() == 1 && sType.getPrimitiveType().getBuiltinTypeCode() == 11 && ((bits = sType.getDecimalSize()) != (parentBits = sType.getBaseType().getDecimalSize()) || sType.getBaseType().getFullJavaName() == null)) {
            switch (bits) {
                case 1000000: {
                    this.emit("java.math.BigInteger getBigIntegerValue();", XmlOptions.BeanMethod.GET);
                    this.emit("void setBigIntegerValue(java.math.BigInteger bi);", XmlOptions.BeanMethod.SET);
                    break;
                }
                case 64: {
                    this.emit("long getLongValue();", XmlOptions.BeanMethod.GET);
                    this.emit("void setLongValue(long l);", XmlOptions.BeanMethod.SET);
                    break;
                }
                case 32: {
                    this.emit("int getIntValue();", XmlOptions.BeanMethod.GET);
                    this.emit("void setIntValue(int i);", XmlOptions.BeanMethod.SET);
                    break;
                }
                case 16: {
                    this.emit("short getShortValue();", XmlOptions.BeanMethod.GET);
                    this.emit("void setShortValue(short s);", XmlOptions.BeanMethod.SET);
                    break;
                }
                case 8: {
                    this.emit("byte getByteValue();", XmlOptions.BeanMethod.GET);
                    this.emit("void setByteValue(byte b);", XmlOptions.BeanMethod.SET);
                }
            }
        }
        if (sType.getSimpleVariety() == 2) {
            this.emit("java.lang.Object getObjectValue();", XmlOptions.BeanMethod.GET);
            this.emit("void setObjectValue(java.lang.Object val);", XmlOptions.BeanMethod.SET);
            this.emit("org.apache.xmlbeans.SchemaType instanceType();", XmlOptions.BeanMethod.INSTANCE_TYPE);
            SchemaType ctype = sType.getUnionCommonBaseType();
            if (ctype != null && ctype.getSimpleVariety() != 2) {
                this.emitSpecializedAccessors(ctype);
            }
        }
        if (sType.getSimpleVariety() == 3) {
            this.emit("java.util.List getListValue();", XmlOptions.BeanMethod.GET_LIST);
            this.emit("java.util.List xgetListValue();", XmlOptions.BeanMethod.XGET_LIST);
            this.emit("void setListValue(java.util.List<?> list);", XmlOptions.BeanMethod.SET_LIST);
        }
    }

    void startBlock() {
        this.indent();
    }

    void endBlock() throws IOException {
        this.outdent();
        this.emit("}");
    }

    void printJavaDoc(String sentence, XmlOptions.BeanMethod method) throws IOException {
        Set<XmlOptions.BeanMethod> partMet;
        Set<XmlOptions.BeanMethod> set = partMet = this.opt == null ? null : this.opt.getCompilePartialMethod();
        if (partMet == null || partMet.contains((Object)method)) {
            this.printJavaDoc(sentence);
        }
    }

    void printJavaDoc(String sentence) throws IOException {
        this.emit("");
        this.emit("/**");
        this.emit(" * " + sentence);
        this.emit(" */");
    }

    void printJavaDocParagraph(String s) throws IOException {
        this.emit("");
        this.emit("/**");
        this.printJavaDocBody(s);
        this.emit(" */");
    }

    void printJavaDocBody(String doc) throws IOException {
        String docClean = doc.trim().replace("\t", "").replace("*/", "* /");
        for (String s : docClean.split("[\\n\\r]+")) {
            this.emit(" * " + s);
        }
    }

    public static String javaStringEscape(String str) {
        block11: {
            int i = 0;
            while (i < str.length()) {
                switch (str.charAt(i)) {
                    case '\n': 
                    case '\r': 
                    case '\"': 
                    case '\\': {
                        break block11;
                    }
                    default: {
                        ++i;
                        break;
                    }
                }
            }
            return str;
        }
        StringBuilder sb = new StringBuilder();
        block10: for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            switch (ch) {
                default: {
                    sb.append(ch);
                    continue block10;
                }
                case '\n': {
                    sb.append("\\n");
                    continue block10;
                }
                case '\r': {
                    sb.append("\\r");
                    continue block10;
                }
                case '\"': {
                    sb.append("\\\"");
                    continue block10;
                }
                case '\\': {
                    sb.append("\\\\");
                }
            }
        }
        return sb.toString();
    }

    void printStringEnumeration(SchemaType sType) throws IOException {
        String constName;
        String enumValue;
        SchemaType baseEnumType = sType.getBaseEnumType();
        String baseEnumClass = baseEnumType.getFullJavaName();
        boolean hasBase = this.hasBase(sType);
        if (!hasBase) {
            this.emit("");
            this.emit("org.apache.xmlbeans.StringEnumAbstractBase getEnumValue();", XmlOptions.BeanMethod.GET);
            this.emit("void setEnumValue(org.apache.xmlbeans.StringEnumAbstractBase e);", XmlOptions.BeanMethod.SET);
        }
        this.emit("");
        SchemaStringEnumEntry[] entries = sType.getStringEnumEntries();
        HashSet<String> seenValues = new HashSet<String>();
        HashSet<String> repeatValues = new HashSet<String>();
        for (SchemaStringEnumEntry entry : entries) {
            enumValue = entry.getString();
            if (seenValues.contains(enumValue)) {
                repeatValues.add(enumValue);
                continue;
            }
            seenValues.add(enumValue);
            String constName2 = entry.getEnumName();
            if (hasBase) {
                this.emit(baseEnumClass + ".Enum " + constName2 + " = " + baseEnumClass + "." + constName2 + ";");
                continue;
            }
            this.emit("Enum " + constName2 + " = Enum.forString(\"" + SchemaTypeCodePrinter.javaStringEscape(enumValue) + "\");");
        }
        this.emit("");
        for (SchemaStringEnumEntry entry : entries) {
            if (repeatValues.contains(entry.getString())) continue;
            constName = "INT_" + entry.getEnumName();
            if (hasBase) {
                this.emit("int " + constName + " = " + baseEnumClass + "." + constName + ";");
                continue;
            }
            this.emit("int " + constName + " = Enum." + constName + ";");
        }
        if (!hasBase) {
            this.emit("");
            this.emit("/**");
            this.emit(" * Enumeration value class for " + baseEnumClass + ".");
            this.emit(" * These enum values can be used as follows:");
            this.emit(" * <pre>");
            this.emit(" * enum.toString(); // returns the string value of the enum");
            this.emit(" * enum.intValue(); // returns an int value, useful for switches");
            if (entries.length > 0) {
                this.emit(" * // e.g., case Enum.INT_" + entries[0].getEnumName());
            }
            this.emit(" * Enum.forString(s); // returns the enum value for a string");
            this.emit(" * Enum.forInt(i); // returns the enum value for an int");
            this.emit(" * </pre>");
            this.emit(" * Enumeration objects are immutable singleton objects that");
            this.emit(" * can be compared using == object equality. They have no");
            this.emit(" * public constructor. See the constants defined within this");
            this.emit(" * class for all the valid values.");
            this.emit(" */");
            this.emit("final class Enum extends org.apache.xmlbeans.StringEnumAbstractBase {");
            this.indent();
            this.emit("/**");
            this.emit(" * Returns the enum value for a string, or null if none.");
            this.emit(" */");
            this.emit("public static Enum forString(java.lang.String s) {");
            this.emit("    return (Enum)table.forString(s);");
            this.emit("}");
            this.emit("");
            this.emit("/**");
            this.emit(" * Returns the enum value corresponding to an int, or null if none.");
            this.emit(" */");
            this.emit("public static Enum forInt(int i) {");
            this.emit("    return (Enum)table.forInt(i);");
            this.emit("}");
            this.emit("");
            this.emit("private Enum(java.lang.String s, int i) {");
            this.emit("    super(s, i);");
            this.emit("}");
            this.emit("");
            for (SchemaStringEnumEntry entry : entries) {
                constName = "INT_" + entry.getEnumName();
                int intValue = entry.getIntValue();
                this.emit("static final int " + constName + " = " + intValue + ";");
            }
            this.emit("");
            this.emit("public static final org.apache.xmlbeans.StringEnumAbstractBase.Table table =");
            this.emit("    new org.apache.xmlbeans.StringEnumAbstractBase.Table(new Enum[] {");
            this.indent();
            for (SchemaStringEnumEntry entry : entries) {
                enumValue = entry.getString();
                String constName3 = "INT_" + entry.getEnumName();
                this.emit("new Enum(\"" + SchemaTypeCodePrinter.javaStringEscape(enumValue) + "\", " + constName3 + "),");
            }
            this.outdent();
            this.emit("});");
            this.emit("private static final long serialVersionUID = 1L;");
            this.emit("private java.lang.Object readResolve() {");
            this.emit("    return forInt(intValue());");
            this.emit("}");
            this.outdent();
            this.emit("}");
        }
    }

    private boolean hasBase(SchemaType sType) {
        SchemaType baseEnumType = sType.getBaseEnumType();
        boolean hasBase = baseEnumType.isAnonymousType() && baseEnumType.isSkippedAnonymousType() ? (sType.getContentBasedOnType() != null ? sType.getContentBasedOnType().getBaseType() != baseEnumType : sType.getBaseType() != baseEnumType) : baseEnumType != sType;
        return hasBase;
    }

    String xmlTypeForProperty(SchemaProperty sProp) {
        SchemaType sType = sProp.javaBasedOnType();
        return this.findJavaType(sType).replace('$', '.');
    }

    static boolean xmlTypeForPropertyIsUnion(SchemaProperty sProp) {
        SchemaType sType = sProp.javaBasedOnType();
        return sType.isSimpleType() && sType.getSimpleVariety() == 2;
    }

    static boolean isJavaPrimitive(int javaType) {
        return javaType >= 1 && javaType <= 7;
    }

    static String javaWrappedType(int javaType) {
        switch (javaType) {
            case 1: {
                return "java.lang.Boolean";
            }
            case 2: {
                return "java.lang.Float";
            }
            case 3: {
                return "java.lang.Double";
            }
            case 4: {
                return "java.lang.Byte";
            }
            case 5: {
                return "java.lang.Short";
            }
            case 6: {
                return "java.lang.Integer";
            }
            case 7: {
                return "java.lang.Long";
            }
        }
        assert (false);
        throw new IllegalStateException();
    }

    String javaTypeForProperty(SchemaProperty sProp) {
        if (sProp.getJavaTypeCode() == 0) {
            SchemaType sType = sProp.javaBasedOnType();
            return this.findJavaType(sType).replace('$', '.');
        }
        if (sProp.getJavaTypeCode() == 20) {
            return ((SchemaTypeImpl)sProp.getType()).getUserTypeName();
        }
        switch (sProp.getJavaTypeCode()) {
            case 1: {
                return "boolean";
            }
            case 2: {
                return "float";
            }
            case 3: {
                return "double";
            }
            case 4: {
                return "byte";
            }
            case 5: {
                return "short";
            }
            case 6: {
                return "int";
            }
            case 7: {
                return "long";
            }
            case 8: {
                return "java.math.BigDecimal";
            }
            case 9: {
                return "java.math.BigInteger";
            }
            case 10: {
                return "java.lang.String";
            }
            case 11: {
                return "byte[]";
            }
            case 12: {
                return "org.apache.xmlbeans.GDate";
            }
            case 13: {
                return "org.apache.xmlbeans.GDuration";
            }
            case 14: {
                return "java.util.Date";
            }
            case 15: {
                return "javax.xml.namespace.QName";
            }
            case 16: {
                return "java.util.List";
            }
            case 17: {
                return "java.util.Calendar";
            }
            case 18: {
                SchemaType sType = sProp.javaBasedOnType();
                if (sType.getSimpleVariety() == 2) {
                    sType = sType.getUnionCommonBaseType();
                }
                assert (sType.getBaseEnumType() != null);
                if (this.hasBase(sType)) {
                    return this.findJavaType(sType.getBaseEnumType()).replace('$', '.') + ".Enum";
                }
                return this.findJavaType(sType).replace('$', '.') + ".Enum";
            }
            case 19: {
                return "java.lang.Object";
            }
        }
        assert (false);
        throw new IllegalStateException();
    }

    void printPropertyGetters(SchemaProperty prop) throws IOException {
        boolean xmltype;
        String propertyName = prop.getJavaPropertyName();
        int javaType = prop.getJavaTypeCode();
        String type = this.javaTypeForProperty(prop);
        String xtype = this.xmlTypeForProperty(prop);
        boolean nillable = prop.hasNillable() != 0;
        boolean several = prop.extendsJavaArray();
        String propertyDocumentation = prop.getDocumentation();
        String propdesc = "\"" + prop.getName().getLocalPart() + "\"" + (prop.isAttribute() ? " attribute" : " element");
        boolean bl = xmltype = javaType == 0;
        if (prop.extendsJavaSingleton()) {
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc((several ? "Gets first " : "Gets the ") + propdesc, XmlOptions.BeanMethod.GET);
            }
            this.emit(type + " get" + propertyName + "();", XmlOptions.BeanMethod.GET);
            if (!xmltype) {
                this.printJavaDoc((several ? "Gets (as xml) first " : "Gets (as xml) the ") + propdesc, XmlOptions.BeanMethod.XGET);
                this.emit(xtype + " xget" + propertyName + "();", XmlOptions.BeanMethod.XGET);
            }
            if (nillable) {
                this.printJavaDoc((several ? "Tests for nil first " : "Tests for nil ") + propdesc, XmlOptions.BeanMethod.IS_NIL);
                this.emit("boolean isNil" + propertyName + "();", XmlOptions.BeanMethod.IS_NIL);
            }
        }
        if (prop.extendsJavaOption()) {
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc((several ? "True if has at least one " : "True if has ") + propdesc, XmlOptions.BeanMethod.IS_SET);
            }
            this.emit("boolean isSet" + propertyName + "();", XmlOptions.BeanMethod.IS_SET);
        }
        if (several) {
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            }
            String arrayName = propertyName + "Array";
            String wrappedType = type;
            if (SchemaTypeCodePrinter.isJavaPrimitive(javaType)) {
                wrappedType = SchemaTypeCodePrinter.javaWrappedType(javaType);
            }
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc("Gets a List of " + propdesc + "s", XmlOptions.BeanMethod.GET_LIST);
            }
            this.emit("java.util.List<" + wrappedType + "> get" + propertyName + "List();", XmlOptions.BeanMethod.GET_LIST);
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc("Gets array of all " + propdesc + "s", XmlOptions.BeanMethod.GET_ARRAY);
            }
            this.emit(type + "[] get" + arrayName + "();", XmlOptions.BeanMethod.GET_ARRAY);
            this.printJavaDoc("Gets ith " + propdesc, XmlOptions.BeanMethod.GET_IDX);
            this.emit(type + " get" + arrayName + "(int i);", XmlOptions.BeanMethod.GET_IDX);
            if (!xmltype) {
                this.printJavaDoc("Gets (as xml) a List of " + propdesc + "s", XmlOptions.BeanMethod.XGET_LIST);
                this.emit("java.util.List<" + xtype + "> xget" + propertyName + "List();", XmlOptions.BeanMethod.XGET_LIST);
                this.printJavaDoc("Gets (as xml) array of all " + propdesc + "s", XmlOptions.BeanMethod.XGET_ARRAY);
                this.emit(xtype + "[] xget" + arrayName + "();", XmlOptions.BeanMethod.XGET_ARRAY);
                this.printJavaDoc("Gets (as xml) ith " + propdesc, XmlOptions.BeanMethod.XGET_IDX);
                this.emit(xtype + " xget" + arrayName + "(int i);", XmlOptions.BeanMethod.XGET_IDX);
            }
            if (nillable) {
                this.printJavaDoc("Tests for nil ith " + propdesc, XmlOptions.BeanMethod.IS_NIL_IDX);
                this.emit("boolean isNil" + arrayName + "(int i);", XmlOptions.BeanMethod.IS_NIL_IDX);
            }
            this.printJavaDoc("Returns number of " + propdesc, XmlOptions.BeanMethod.SIZE_OF_ARRAY);
            this.emit("int sizeOf" + arrayName + "();", XmlOptions.BeanMethod.SIZE_OF_ARRAY);
        }
    }

    void printPropertySetters(SchemaProperty prop) throws IOException {
        QName qName = prop.getName();
        boolean isAttr = prop.isAttribute();
        String propertyName = prop.getJavaPropertyName();
        int javaType = prop.getJavaTypeCode();
        String type = this.javaTypeForProperty(prop);
        String xtype = this.xmlTypeForProperty(prop);
        boolean nillable = prop.hasNillable() != 0;
        boolean optional = prop.extendsJavaOption();
        boolean several = prop.extendsJavaArray();
        boolean singleton = prop.extendsJavaSingleton();
        String propertyDocumentation = prop.getDocumentation();
        String safeVarName = NameUtil.nonJavaKeyword(NameUtil.lowerCamelCase(propertyName));
        if (safeVarName.equals("i")) {
            safeVarName = "iValue";
        }
        boolean xmltype = javaType == 0;
        String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        if (singleton) {
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc((several ? "Sets first " : "Sets the ") + propdesc, XmlOptions.BeanMethod.SET);
            }
            this.emit("void set" + propertyName + "(" + type + " " + safeVarName + ");", XmlOptions.BeanMethod.SET);
            if (!xmltype) {
                this.printJavaDoc((several ? "Sets (as xml) first " : "Sets (as xml) the ") + propdesc, XmlOptions.BeanMethod.XSET);
                this.emit("void xset" + propertyName + "(" + xtype + " " + safeVarName + ");", XmlOptions.BeanMethod.XSET);
            }
            if (xmltype && !several) {
                this.printJavaDoc("Appends and returns a new empty " + propdesc, XmlOptions.BeanMethod.ADD_NEW);
                this.emit(xtype + " addNew" + propertyName + "();", XmlOptions.BeanMethod.ADD_NEW);
            }
            if (nillable) {
                this.printJavaDoc((several ? "Nils the first " : "Nils the ") + propdesc, XmlOptions.BeanMethod.SET_NIL);
                this.emit("void setNil" + propertyName + "();", XmlOptions.BeanMethod.SET_NIL);
            }
        }
        if (optional) {
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc((several ? "Removes first " : "Unsets the ") + propdesc, XmlOptions.BeanMethod.UNSET);
            }
            this.emit("void unset" + propertyName + "();", XmlOptions.BeanMethod.UNSET);
        }
        if (several) {
            String arrayName = propertyName + "Array";
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc("Sets array of all " + propdesc, XmlOptions.BeanMethod.SET_ARRAY);
            }
            this.emit("void set" + arrayName + "(" + type + "[] " + safeVarName + "Array);", XmlOptions.BeanMethod.SET_ARRAY);
            if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                this.printJavaDocParagraph(propertyDocumentation);
            } else {
                this.printJavaDoc("Sets ith " + propdesc, XmlOptions.BeanMethod.SET_IDX);
            }
            this.emit("void set" + arrayName + "(int i, " + type + " " + safeVarName + ");", XmlOptions.BeanMethod.SET_IDX);
            if (!xmltype) {
                this.printJavaDoc("Sets (as xml) array of all " + propdesc, XmlOptions.BeanMethod.XSET_ARRAY);
                this.emit("void xset" + arrayName + "(" + xtype + "[] " + safeVarName + "Array);", XmlOptions.BeanMethod.XSET_ARRAY);
                this.printJavaDoc("Sets (as xml) ith " + propdesc, XmlOptions.BeanMethod.XSET_IDX);
                this.emit("void xset" + arrayName + "(int i, " + xtype + " " + safeVarName + ");", XmlOptions.BeanMethod.XSET_IDX);
            }
            if (nillable) {
                this.printJavaDoc("Nils the ith " + propdesc, XmlOptions.BeanMethod.SET_NIL_IDX);
                this.emit("void setNil" + arrayName + "(int i);", XmlOptions.BeanMethod.SET_NIL_IDX);
            }
            if (!xmltype) {
                this.printJavaDoc("Inserts the value as the ith " + propdesc, XmlOptions.BeanMethod.INSERT_IDX);
                this.emit("void insert" + propertyName + "(int i, " + type + " " + safeVarName + ");", XmlOptions.BeanMethod.INSERT_IDX);
                this.printJavaDoc("Appends the value as the last " + propdesc, XmlOptions.BeanMethod.ADD);
                this.emit("void add" + propertyName + "(" + type + " " + safeVarName + ");", XmlOptions.BeanMethod.ADD);
            }
            this.printJavaDoc("Inserts and returns a new empty value (as xml) as the ith " + propdesc, XmlOptions.BeanMethod.INSERT_NEW_IDX);
            this.emit(xtype + " insertNew" + propertyName + "(int i);", XmlOptions.BeanMethod.INSERT_NEW_IDX);
            this.printJavaDoc("Appends and returns a new empty value (as xml) as the last " + propdesc, XmlOptions.BeanMethod.ADD_NEW);
            this.emit(xtype + " addNew" + propertyName + "();", XmlOptions.BeanMethod.ADD_NEW);
            this.printJavaDoc("Removes the ith " + propdesc, XmlOptions.BeanMethod.REMOVE_IDX);
            this.emit("void remove" + propertyName + "(int i);", XmlOptions.BeanMethod.REMOVE_IDX);
        }
    }

    String getAtomicRestrictionType(SchemaType sType) {
        SchemaType pType = sType.getPrimitiveType();
        switch (pType.getBuiltinTypeCode()) {
            case 2: {
                return "org.apache.xmlbeans.impl.values.XmlAnySimpleTypeImpl";
            }
            case 3: {
                return "org.apache.xmlbeans.impl.values.JavaBooleanHolderEx";
            }
            case 4: {
                return "org.apache.xmlbeans.impl.values.JavaBase64HolderEx";
            }
            case 5: {
                return "org.apache.xmlbeans.impl.values.JavaHexBinaryHolderEx";
            }
            case 6: {
                return "org.apache.xmlbeans.impl.values.JavaUriHolderEx";
            }
            case 7: {
                return "org.apache.xmlbeans.impl.values.JavaQNameHolderEx";
            }
            case 8: {
                return "org.apache.xmlbeans.impl.values.JavaNotationHolderEx";
            }
            case 9: {
                return "org.apache.xmlbeans.impl.values.JavaFloatHolderEx";
            }
            case 10: {
                return "org.apache.xmlbeans.impl.values.JavaDoubleHolderEx";
            }
            case 11: {
                switch (sType.getDecimalSize()) {
                    default: {
                        assert (false);
                    }
                    case 1000001: {
                        return "org.apache.xmlbeans.impl.values.JavaDecimalHolderEx";
                    }
                    case 1000000: {
                        return "org.apache.xmlbeans.impl.values.JavaIntegerHolderEx";
                    }
                    case 64: {
                        return "org.apache.xmlbeans.impl.values.JavaLongHolderEx";
                    }
                    case 8: 
                    case 16: 
                    case 32: 
                }
                return "org.apache.xmlbeans.impl.values.JavaIntHolderEx";
            }
            case 12: {
                if (sType.hasStringEnumValues()) {
                    return "org.apache.xmlbeans.impl.values.JavaStringEnumerationHolderEx";
                }
                return "org.apache.xmlbeans.impl.values.JavaStringHolderEx";
            }
            case 14: 
            case 15: 
            case 16: 
            case 17: 
            case 18: 
            case 19: 
            case 20: 
            case 21: {
                return "org.apache.xmlbeans.impl.values.JavaGDateHolderEx";
            }
            case 13: {
                return "org.apache.xmlbeans.impl.values.JavaGDurationHolderEx";
            }
        }
        assert (false) : "unrecognized primitive type";
        return null;
    }

    static SchemaType findBaseType(SchemaType sType) {
        while (sType.getFullJavaName() == null) {
            sType = sType.getBaseType();
        }
        return sType;
    }

    String getBaseClass(SchemaType sType) {
        SchemaType baseType = SchemaTypeCodePrinter.findBaseType(sType.getBaseType());
        switch (sType.getSimpleVariety()) {
            case 0: {
                if (!XmlObject.type.equals(baseType)) {
                    return baseType.getFullJavaImplName();
                }
                return "org.apache.xmlbeans.impl.values.XmlComplexContentImpl";
            }
            case 1: {
                assert (!sType.isBuiltinType());
                return this.getAtomicRestrictionType(sType);
            }
            case 3: {
                return "org.apache.xmlbeans.impl.values.XmlListImpl";
            }
            case 2: {
                return "org.apache.xmlbeans.impl.values.XmlUnionImpl";
            }
        }
        throw new IllegalStateException();
    }

    void printConstructor(SchemaType sType, String shortName) throws IOException {
        this.emit("");
        this.emit("public " + shortName + "(org.apache.xmlbeans.SchemaType sType) {");
        this.startBlock();
        this.emit("super(sType" + (sType.getSimpleVariety() == 0 ? "" : ", " + !sType.isSimpleType()) + ");");
        this.endBlock();
        if (sType.getSimpleVariety() != 0) {
            this.emit("");
            this.emit("protected " + shortName + "(org.apache.xmlbeans.SchemaType sType, boolean b) {");
            this.startBlock();
            this.emit("super(sType, b);");
            this.endBlock();
        }
    }

    void startClass(SchemaType sType, boolean isInner) throws IOException {
        String shortName = sType.getShortJavaImplName();
        String baseClass = this.getBaseClass(sType);
        StringBuilder interfaces = new StringBuilder();
        interfaces.append(sType.getFullJavaName().replace('$', '.'));
        if (sType.getSimpleVariety() == 2) {
            SchemaType[] memberTypes;
            for (SchemaType memberType : memberTypes = sType.getUnionMemberTypes()) {
                interfaces.append(", ").append(memberType.getFullJavaName().replace('$', '.'));
            }
        }
        this.emit("public " + (isInner ? "static " : "") + "class " + shortName + " extends " + baseClass + " implements " + interfaces + " {");
        this.startBlock();
        this.emit("private static final long serialVersionUID = 1L;");
    }

    void makeAttributeDefaultValue(String jtargetType, SchemaProperty prop, String identifier) throws IOException {
        String fullName = jtargetType;
        if (fullName == null) {
            fullName = prop.javaBasedOnType().getFullJavaName().replace('$', '.');
        }
        this.emit("target = (" + fullName + ")get_default_attribute_value(" + identifier + ");");
    }

    String makeMissingValue(int javaType) throws IOException {
        switch (javaType) {
            case 1: {
                return "false";
            }
            case 2: {
                return "0.0f";
            }
            case 3: {
                return "0.0";
            }
            case 4: 
            case 5: 
            case 6: {
                return "0";
            }
            case 7: {
                return "0L";
            }
        }
        return "null";
    }

    void printJGetArrayValue(int javaType, String type, SchemaTypeImpl stype, String setIdentifier) throws IOException {
        String em;
        switch (javaType) {
            case 0: {
                em = "XmlObjectArray(#ID#, new " + type + "[0]);";
                break;
            }
            case 18: {
                em = "EnumArray(#ID#, " + type + "[]::new);";
                break;
            }
            case 1: {
                em = "BooleanArray(#ID#);";
                break;
            }
            case 2: {
                em = "FloatArray(#ID#);";
                break;
            }
            case 3: {
                em = "DoubleArray(#ID#);";
                break;
            }
            case 4: {
                em = "ByteArray(#ID#);";
                break;
            }
            case 5: {
                em = "ShortArray(#ID#);";
                break;
            }
            case 6: {
                em = "IntArray(#ID#);";
                break;
            }
            case 7: {
                em = "LongArray(#ID#);";
                break;
            }
            case 8: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getBigDecimalValue, java.math.BigDecimal[]::new);";
                break;
            }
            case 9: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getBigIntegerValue, java.math.BigInteger[]::new);";
                break;
            }
            case 10: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getStringValue, String[]::new);";
                break;
            }
            case 11: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getByteArrayValue, byte[][]::new);";
                break;
            }
            case 17: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getCalendarValue, java.util.Calendar[]::new);";
                break;
            }
            case 14: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getDateValue, java.util.Date[]::new);";
                break;
            }
            case 12: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getGDateValue, org.apache.xmlbeans.GDate[]::new);";
                break;
            }
            case 13: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getGDurationValue, org.apache.xmlbeans.GDuration[]::new);";
                break;
            }
            case 15: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getQNameValue, javax.xml.namespace.QName[]::new);";
                break;
            }
            case 16: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getListValue, java.util.List[]::new);";
                break;
            }
            case 19: {
                em = "ObjectArray(#ID#, org.apache.xmlbeans.SimpleValue::getObjectValue, java.lang.Object[]::new);";
                break;
            }
            case 20: {
                em = "ObjectArray(#ID#, e -> " + this.getUserTypeStaticHandlerMethod(false, stype) + "(e), " + stype.getUserTypeName() + "[]::new);";
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        this.emit("return get" + em.replace("#ID#", setIdentifier), XmlOptions.BeanMethod.GET_ARRAY);
    }

    String printJGetValue(int javaType, String type, SchemaTypeImpl stype) throws IOException {
        switch (javaType) {
            case 0: {
                return "target";
            }
            case 1: {
                return "target.getBooleanValue()";
            }
            case 2: {
                return "target.getFloatValue()";
            }
            case 3: {
                return "target.getDoubleValue()";
            }
            case 4: {
                return "target.getByteValue()";
            }
            case 5: {
                return "target.getShortValue()";
            }
            case 6: {
                return "target.getIntValue()";
            }
            case 7: {
                return "target.getLongValue()";
            }
            case 8: {
                return "target.getBigDecimalValue()";
            }
            case 9: {
                return "target.getBigIntegerValue()";
            }
            case 10: {
                return "target.getStringValue()";
            }
            case 11: {
                return "target.getByteArrayValue()";
            }
            case 12: {
                return "target.getGDateValue()";
            }
            case 13: {
                return "target.getGDurationValue()";
            }
            case 17: {
                return "target.getCalendarValue()";
            }
            case 14: {
                return "target.getDateValue()";
            }
            case 15: {
                return "target.getQNameValue()";
            }
            case 16: {
                return "target.getListValue()";
            }
            case 18: {
                return "(" + type + ")target.getEnumValue()";
            }
            case 19: {
                return "target.getObjectValue()";
            }
            case 20: {
                return this.getUserTypeStaticHandlerMethod(false, stype) + "(target)";
            }
        }
        throw new IllegalStateException();
    }

    void printJSetValue(int javaType, String safeVarName, SchemaTypeImpl stype) throws IOException {
        String em;
        switch (javaType) {
            case 0: {
                em = "target.set(#VARNAME#)";
                break;
            }
            case 1: {
                em = "target.setBooleanValue(#VARNAME#)";
                break;
            }
            case 2: {
                em = "target.setFloatValue(#VARNAME#)";
                break;
            }
            case 3: {
                em = "target.setDoubleValue(#VARNAME#)";
                break;
            }
            case 4: {
                em = "target.setByteValue(#VARNAME#)";
                break;
            }
            case 5: {
                em = "target.setShortValue(#VARNAME#)";
                break;
            }
            case 6: {
                em = "target.setIntValue(#VARNAME#)";
                break;
            }
            case 7: {
                em = "target.setLongValue(#VARNAME#)";
                break;
            }
            case 8: {
                em = "target.setBigDecimalValue(#VARNAME#)";
                break;
            }
            case 9: {
                em = "target.setBigIntegerValue(#VARNAME#)";
                break;
            }
            case 10: {
                em = "target.setStringValue(#VARNAME#)";
                break;
            }
            case 11: {
                em = "target.setByteArrayValue(#VARNAME#)";
                break;
            }
            case 12: {
                em = "target.setGDateValue(#VARNAME#)";
                break;
            }
            case 13: {
                em = "target.setGDurationValue(#VARNAME#)";
                break;
            }
            case 17: {
                em = "target.setCalendarValue(#VARNAME#)";
                break;
            }
            case 14: {
                em = "target.setDateValue(#VARNAME#)";
                break;
            }
            case 15: {
                em = "target.setQNameValue(#VARNAME#)";
                break;
            }
            case 16: {
                em = "target.setListValue(#VARNAME#)";
                break;
            }
            case 18: {
                em = "target.setEnumValue(#VARNAME#)";
                break;
            }
            case 19: {
                em = "target.setObjectValue(#VARNAME#)";
                break;
            }
            case 20: {
                em = this.getUserTypeStaticHandlerMethod(true, stype) + "(#VARNAME#, target)";
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        this.emit(em.replace("#VARNAME#", safeVarName) + ";");
    }

    String getIdentifier(Map<QName, Integer> qnameMap, QName qName) {
        return "PROPERTY_QNAME[" + qnameMap.get(qName) + "]";
    }

    String getSetIdentifier(Map<QName, Integer> qnameMap, QName qName, Map<QName, Integer> qsetMap) {
        Integer ord = qsetMap.get(qName);
        return ord == null ? this.getIdentifier(qnameMap, qName) : "PROPERTY_QSET[" + ord + "]";
    }

    void printStaticFields(SchemaProperty[] properties, Map<QName, Integer> qnameMap, Map<QName, Integer> qsetMap) throws IOException {
        QName name;
        if (properties.length == 0) {
            return;
        }
        int countQSet = 0;
        this.emit("");
        this.emit("private static final QName[] PROPERTY_QNAME = {");
        this.indent();
        for (SchemaProperty prop : properties) {
            name = prop.getName();
            qnameMap.put(name, qnameMap.size());
            this.emit("new QName(\"" + name.getNamespaceURI() + "\", \"" + name.getLocalPart() + "\"),");
            countQSet = Math.max(countQSet, prop.acceptedNames() == null ? 0 : prop.acceptedNames().length);
        }
        this.outdent();
        this.emit("};");
        this.emit("");
        if (countQSet > 1) {
            this.emit("private static final QNameSet[] PROPERTY_QSET = {");
            for (SchemaProperty prop : properties) {
                name = prop.getName();
                QName[] qnames = prop.acceptedNames();
                if (qnames == null || qnames.length <= 1) continue;
                qsetMap.put(name, qsetMap.size());
                this.emit("QNameSet.forArray( new QName[] { ");
                this.indent();
                for (QName qname : qnames) {
                    this.emit("new QName(\"" + qname.getNamespaceURI() + "\", \"" + qname.getLocalPart() + "\"),");
                }
                this.outdent();
                this.emit("}),");
            }
            this.emit("};");
        }
    }

    void emitImplementationPreamble() throws IOException {
        this.emit("synchronized (monitor()) {");
        this.indent();
        this.emit("check_orphaned();");
    }

    void emitImplementationPostamble() throws IOException {
        this.outdent();
        this.emit("}");
    }

    void emitAddTarget(String identifier, boolean isAttr, String xtype) throws IOException {
        if (isAttr) {
            this.emit("target = (" + xtype + ")get_store().add_attribute_user(" + identifier + ");");
        } else {
            this.emit("target = (" + xtype + ")get_store().add_element_user(" + identifier + ");");
        }
    }

    void emitPre(SchemaType sType, int opType, String identifier, boolean isAttr) throws IOException {
        this.emitPre(sType, opType, identifier, isAttr, "-1");
    }

    void emitPre(SchemaType sType, int opType, String identifier, boolean isAttr, String index) throws IOException {
        SchemaTypeImpl sImpl = SchemaTypeCodePrinter.getImpl(sType);
        if (sImpl == null) {
            return;
        }
        PrePostExtension ext = sImpl.getPrePostExtension();
        if (ext != null && ext.hasPreCall()) {
            this.emit("if ( " + ext.getStaticHandler() + ".preSet(" + this.prePostOpString(opType) + ", this, " + identifier + ", " + isAttr + ", " + index + ")) {");
            this.startBlock();
        }
    }

    void emitPost(SchemaType sType, int opType, String identifier, boolean isAttr) throws IOException {
        this.emitPost(sType, opType, identifier, isAttr, "-1");
    }

    void emitPost(SchemaType sType, int opType, String identifier, boolean isAttr, String index) throws IOException {
        SchemaTypeImpl sImpl = SchemaTypeCodePrinter.getImpl(sType);
        if (sImpl == null) {
            return;
        }
        PrePostExtension ext = sImpl.getPrePostExtension();
        if (ext != null) {
            if (ext.hasPreCall()) {
                this.endBlock();
            }
            if (ext.hasPostCall()) {
                this.emit(ext.getStaticHandler() + ".postSet(" + this.prePostOpString(opType) + ", this, " + identifier + ", " + isAttr + ", " + index + ");");
            }
        }
    }

    String prePostOpString(int opType) {
        switch (opType) {
            default: {
                assert (false);
            }
            case 1: {
                return "org.apache.xmlbeans.PrePostExtension.OPERATION_SET";
            }
            case 2: {
                return "org.apache.xmlbeans.PrePostExtension.OPERATION_INSERT";
            }
            case 3: 
        }
        return "org.apache.xmlbeans.PrePostExtension.OPERATION_REMOVE";
    }

    void emitGetTarget(String setIdentifier, String identifier, boolean isAttr, String index, int nullBehaviour, String xtype) throws IOException {
        assert (setIdentifier != null && identifier != null);
        this.emit(xtype + " target = null;");
        if (isAttr) {
            this.emit("target = (" + xtype + ")get_store().find_attribute_user(" + identifier + ");");
        } else {
            this.emit("target = (" + xtype + ")get_store().find_element_user(" + setIdentifier + ", " + index + ");");
        }
        if (nullBehaviour == 1) {
            return;
        }
        this.emit("if (target == null) {");
        this.startBlock();
        switch (nullBehaviour) {
            case 3: {
                this.emitAddTarget(identifier, isAttr, xtype);
                break;
            }
            case 4: {
                this.emit("throw new IndexOutOfBoundsException();");
                break;
            }
            default: {
                assert (false) : "Bad behaviour type: " + nullBehaviour;
                break;
            }
        }
        this.endBlock();
    }

    void printListGetterImpl(String propdesc, String propertyName, String wrappedType, boolean xmltype, boolean xget) throws IOException {
        Set<XmlOptions.BeanMethod> bmList;
        Set<XmlOptions.BeanMethod> set = bmList = this.opt == null ? null : this.opt.getCompilePartialMethod();
        if (bmList != null && !bmList.contains((Object)(xget ? XmlOptions.BeanMethod.XGET_LIST : XmlOptions.BeanMethod.GET_LIST))) {
            return;
        }
        String arrayName = propertyName + "Array";
        this.printJavaDoc("Gets " + (xget ? "(as xml) " : "") + "a List of " + propdesc + "s");
        if (!this.opt.isCompileNoAnnotations()) {
            this.emit("@Override");
        }
        this.emit("public java.util.List<" + wrappedType + "> " + (xget ? "xget" : "get") + propertyName + "List() {");
        this.startBlock();
        this.emitImplementationPreamble();
        this.emit("return new org.apache.xmlbeans.impl.values.JavaList" + (xmltype || xget ? "Xml" : "") + "Object<>(");
        this.indent();
        if (bmList == null || bmList.contains((Object)(xget ? XmlOptions.BeanMethod.XGET_IDX : XmlOptions.BeanMethod.GET_IDX))) {
            this.emit("this::" + (xget ? "xget" : "get") + arrayName + ",");
        } else {
            this.emit("null,");
        }
        if (bmList == null || bmList.contains((Object)(xget ? XmlOptions.BeanMethod.XSET_IDX : XmlOptions.BeanMethod.SET_IDX))) {
            this.emit("this::" + (xget ? "xset" : "set") + arrayName + ",");
        } else {
            this.emit("null,");
        }
        if (bmList == null || bmList.contains((Object)(xmltype || xget ? XmlOptions.BeanMethod.INSERT_NEW_IDX : XmlOptions.BeanMethod.INSERT_IDX))) {
            this.emit("this::insert" + (xmltype || xget ? "New" : "") + propertyName + ",");
        } else {
            this.emit("null,");
        }
        if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.REMOVE_IDX)) {
            this.emit("this::remove" + propertyName + ",");
        } else {
            this.emit("null,");
        }
        if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SIZE_OF_ARRAY)) {
            this.emit("this::sizeOf" + arrayName);
        } else {
            this.emit("null");
        }
        this.outdent();
        this.emit(");");
        this.emitImplementationPostamble();
        this.endBlock();
    }

    void printGetterImpls(SchemaProperty prop, Map<QName, Integer> qnameMap, Map<QName, Integer> qsetMap) throws IOException {
        Set<XmlOptions.BeanMethod> bmList;
        QName qName = prop.getName();
        String identifier = this.getIdentifier(qnameMap, qName);
        String setIdentifier = this.getSetIdentifier(qnameMap, qName, qsetMap);
        boolean several = prop.extendsJavaArray();
        boolean nillable = prop.hasNillable() != 0;
        String type = this.javaTypeForProperty(prop);
        String xtype = this.xmlTypeForProperty(prop);
        int javaType = prop.getJavaTypeCode();
        boolean isAttr = prop.isAttribute();
        String propertyName = prop.getJavaPropertyName();
        String propertyDocumentation = prop.getDocumentation();
        String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        boolean xmltype = javaType == 0;
        String jtargetType = SchemaTypeCodePrinter.xmlTypeForPropertyIsUnion(prop) || !xmltype ? "org.apache.xmlbeans.SimpleValue" : xtype;
        Set<XmlOptions.BeanMethod> set = bmList = this.opt == null ? null : this.opt.getCompilePartialMethod();
        if (prop.extendsJavaSingleton()) {
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.GET)) {
                if (this.opt.isCompileAnnotationAsJavadoc() && propertyDocumentation != null && propertyDocumentation.length() > 0) {
                    this.printJavaDocParagraph(propertyDocumentation);
                } else {
                    this.printJavaDoc((several ? "Gets first " : "Gets the ") + propdesc);
                }
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + type + " get" + propertyName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 1, jtargetType);
                if (isAttr && (prop.hasDefault() == 2 || prop.hasFixed() == 2)) {
                    this.emit("if (target == null) {");
                    this.startBlock();
                    this.makeAttributeDefaultValue(jtargetType, prop, identifier);
                    this.endBlock();
                }
                this.emit("return (target == null) ? " + this.makeMissingValue(javaType) + " : " + this.printJGetValue(javaType, type, (SchemaTypeImpl)prop.getType()) + ";");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.XGET))) {
                this.printJavaDoc((several ? "Gets (as xml) first " : "Gets (as xml) the ") + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + xtype + " xget" + propertyName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 1, xtype);
                if (isAttr && (prop.hasDefault() == 2 || prop.hasFixed() == 2)) {
                    this.emit("if (target == null) {");
                    this.startBlock();
                    this.makeAttributeDefaultValue(xtype, prop, identifier);
                    this.endBlock();
                }
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.IS_NIL))) {
                this.printJavaDoc((several ? "Tests for nil first " : "Tests for nil ") + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public boolean isNil" + propertyName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 1, xtype);
                this.emit("return target != null && target.isNil();");
                this.emitImplementationPostamble();
                this.endBlock();
            }
        }
        if (prop.extendsJavaOption() && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.IS_SET))) {
            this.printJavaDoc((several ? "True if has at least one " : "True if has ") + propdesc);
            if (!this.opt.isCompileNoAnnotations()) {
                this.emit("@Override");
            }
            this.emit("public boolean isSet" + propertyName + "() {");
            this.startBlock();
            this.emitImplementationPreamble();
            if (isAttr) {
                this.emit("return get_store().find_attribute_user(" + identifier + ") != null;");
            } else {
                this.emit("return get_store().count_elements(" + setIdentifier + ") != 0;");
            }
            this.emitImplementationPostamble();
            this.endBlock();
        }
        if (several) {
            String arrayName = propertyName + "Array";
            String wrappedType = type;
            if (SchemaTypeCodePrinter.isJavaPrimitive(javaType)) {
                wrappedType = SchemaTypeCodePrinter.javaWrappedType(javaType);
            }
            this.printListGetterImpl(propdesc, propertyName, wrappedType, xmltype, false);
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.GET_ARRAY)) {
                this.printJavaDoc("Gets array of all " + propdesc + "s");
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + type + "[] get" + arrayName + "() {");
                this.startBlock();
                this.printJGetArrayValue(javaType, type, (SchemaTypeImpl)prop.getType(), setIdentifier);
                this.endBlock();
            }
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.GET_IDX)) {
                this.printJavaDoc("Gets ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + type + " get" + arrayName + "(int i) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, jtargetType);
                this.emit("return " + this.printJGetValue(javaType, type, (SchemaTypeImpl)prop.getType()) + ";");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (!xmltype) {
                this.printListGetterImpl(propdesc, propertyName, xtype, false, true);
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.XGET_ARRAY))) {
                this.printJavaDoc("Gets (as xml) array of all " + propdesc + "s");
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + xtype + "[] xget" + arrayName + "() {");
                this.startBlock();
                this.emit("return xgetArray(" + setIdentifier + ", " + xtype + "[]::new);");
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.XGET_IDX))) {
                this.printJavaDoc("Gets (as xml) ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + xtype + " xget" + arrayName + "(int i) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.IS_NIL_IDX))) {
                this.printJavaDoc("Tests for nil ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public boolean isNil" + arrayName + "(int i) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("return target.isNil();");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SIZE_OF_ARRAY)) {
                this.printJavaDoc("Returns number of " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public int sizeOf" + arrayName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emit("return get_store().count_elements(" + setIdentifier + ");");
                this.emitImplementationPostamble();
                this.endBlock();
            }
        }
    }

    void printSetterImpls(SchemaProperty prop, Map<QName, Integer> qnameMap, Map<QName, Integer> qsetMap, SchemaType sType) throws IOException {
        QName qName = prop.getName();
        String identifier = this.getIdentifier(qnameMap, qName);
        String setIdentifier = this.getSetIdentifier(qnameMap, qName, qsetMap);
        boolean several = prop.extendsJavaArray();
        boolean nillable = prop.hasNillable() != 0;
        String type = this.javaTypeForProperty(prop);
        String xtype = this.xmlTypeForProperty(prop);
        int javaType = prop.getJavaTypeCode();
        boolean isAttr = prop.isAttribute();
        String propertyName = prop.getJavaPropertyName();
        Set<XmlOptions.BeanMethod> bmList = this.opt == null ? null : this.opt.getCompilePartialMethod();
        String safeVarName = NameUtil.nonJavaKeyword(NameUtil.lowerCamelCase(propertyName));
        safeVarName = NameUtil.nonExtraKeyword(safeVarName);
        boolean xmltype = javaType == 0;
        boolean isobj = javaType == 19;
        boolean isSubstGroup = !Objects.equals(identifier, setIdentifier);
        String jtargetType = SchemaTypeCodePrinter.xmlTypeForPropertyIsUnion(prop) || !xmltype ? "org.apache.xmlbeans.SimpleValue" : xtype;
        String propdesc = "\"" + qName.getLocalPart() + "\"" + (isAttr ? " attribute" : " element");
        if (prop.extendsJavaSingleton()) {
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SET)) {
                this.printJavaDoc((several ? "Sets first " : "Sets the ") + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void set" + propertyName + "(" + type + " " + safeVarName + ") {");
                this.startBlock();
                if (xmltype && !isSubstGroup && !isAttr) {
                    this.emitPre(sType, 1, identifier, false, several ? "0" : "-1");
                    this.emit("generatedSetterHelperImpl(" + safeVarName + ", " + setIdentifier + ", 0, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_SINGLETON);");
                    this.emitPost(sType, 1, identifier, false, several ? "0" : "-1");
                } else {
                    this.emitImplementationPreamble();
                    this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                    this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 3, jtargetType);
                    this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                    this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
                    this.emitImplementationPostamble();
                }
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.XSET))) {
                this.printJavaDoc((several ? "Sets (as xml) first " : "Sets (as xml) the ") + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void xset" + propertyName + "(" + xtype + " " + safeVarName + ") {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 3, xtype);
                this.emit("target.set(" + safeVarName + ");");
                this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (xmltype && !several && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.ADD_NEW))) {
                this.printJavaDoc("Appends and returns a new empty " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + xtype + " addNew" + propertyName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emit(xtype + " target = null;");
                this.emitPre(sType, 2, identifier, isAttr);
                this.emitAddTarget(identifier, isAttr, xtype);
                this.emitPost(sType, 2, identifier, isAttr);
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SET_NIL))) {
                this.printJavaDoc((several ? "Nils the first " : "Nils the ") + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void setNil" + propertyName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "0", 3, xtype);
                this.emit("target.setNil();");
                this.emitPost(sType, 1, identifier, isAttr, several ? "0" : "-1");
                this.emitImplementationPostamble();
                this.endBlock();
            }
        }
        if (prop.extendsJavaOption() && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.UNSET))) {
            this.printJavaDoc((several ? "Removes first " : "Unsets the ") + propdesc);
            if (!this.opt.isCompileNoAnnotations()) {
                this.emit("@Override");
            }
            this.emit("public void unset" + propertyName + "() {");
            this.startBlock();
            this.emitImplementationPreamble();
            this.emitPre(sType, 3, identifier, isAttr, several ? "0" : "-1");
            if (isAttr) {
                this.emit("get_store().remove_attribute(" + identifier + ");");
            } else {
                this.emit("get_store().remove_element(" + setIdentifier + ", 0);");
            }
            this.emitPost(sType, 3, identifier, isAttr, several ? "0" : "-1");
            this.emitImplementationPostamble();
            this.endBlock();
        }
        if (several) {
            String arrayName = propertyName + "Array";
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SET_ARRAY)) {
                if (xmltype) {
                    this.printJavaDoc("Sets array of all " + propdesc + "  WARNING: This method is not atomicaly synchronized.");
                    if (!this.opt.isCompileNoAnnotations()) {
                        this.emit("@Override");
                    }
                    this.emit("public void set" + arrayName + "(" + type + "[] " + safeVarName + "Array) {");
                    this.startBlock();
                    this.emit("check_orphaned();");
                    this.emitPre(sType, 1, identifier, isAttr);
                    if (isobj) {
                        if (!isSubstGroup) {
                            this.emit("unionArraySetterHelper(" + safeVarName + "Array, " + identifier + ");");
                        } else {
                            this.emit("unionArraySetterHelper(" + safeVarName + "Array, " + identifier + ", " + setIdentifier + ");");
                        }
                    } else if (!isSubstGroup) {
                        this.emit("arraySetterHelper(" + safeVarName + "Array, " + identifier + ");");
                    } else {
                        this.emit("arraySetterHelper(" + safeVarName + "Array, " + identifier + ", " + setIdentifier + ");");
                    }
                    this.emitPost(sType, 1, identifier, isAttr);
                    this.endBlock();
                } else {
                    this.printJavaDoc("Sets array of all " + propdesc);
                    if (!this.opt.isCompileNoAnnotations()) {
                        this.emit("@Override");
                    }
                    this.emit("public void set" + arrayName + "(" + type + "[] " + safeVarName + "Array) {");
                    this.startBlock();
                    this.emitImplementationPreamble();
                    this.emitPre(sType, 1, identifier, isAttr);
                    if (isobj) {
                        if (!isSubstGroup) {
                            this.emit("unionArraySetterHelper(" + safeVarName + "Array, " + identifier + ");");
                        } else {
                            this.emit("unionArraySetterHelper(" + safeVarName + "Array, " + identifier + ", " + setIdentifier + ");");
                        }
                    } else if (prop.getJavaTypeCode() == 20) {
                        if (!isSubstGroup) {
                            this.emit("org.apache.xmlbeans.SimpleValue[] dests = arraySetterHelper(" + safeVarName + "Array.length, " + identifier + ");");
                            this.emit("for ( int i = 0 ; i < dests.length ; i++ ) {");
                            this.emit("    " + this.getUserTypeStaticHandlerMethod(true, (SchemaTypeImpl)prop.getType()) + "(" + safeVarName + "Array[i], dests[i]);");
                            this.emit("}");
                        } else {
                            this.emit("org.apache.xmlbeans.SimpleValue[] dests = arraySetterHelper(" + safeVarName + "Array.length, " + identifier + ", " + setIdentifier + ");");
                            this.emit("for ( int i = 0 ; i < dests.length ; i++ ) {");
                            this.emit("    " + this.getUserTypeStaticHandlerMethod(true, (SchemaTypeImpl)prop.getType()) + "(" + safeVarName + "Array[i], dests[i]);");
                            this.emit("}");
                        }
                    } else if (!isSubstGroup) {
                        this.emit("arraySetterHelper(" + safeVarName + "Array, " + identifier + ");");
                    } else {
                        this.emit("arraySetterHelper(" + safeVarName + "Array, " + identifier + ", " + setIdentifier + ");");
                    }
                    this.emitPost(sType, 1, identifier, isAttr);
                    this.emitImplementationPostamble();
                    this.endBlock();
                }
            }
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SET_IDX)) {
                this.printJavaDoc("Sets ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void set" + arrayName + "(int i, " + type + " " + safeVarName + ") {");
                this.startBlock();
                if (xmltype && !isSubstGroup) {
                    this.emitPre(sType, 1, identifier, isAttr, "i");
                    this.emit("generatedSetterHelperImpl(" + safeVarName + ", " + setIdentifier + ", i, org.apache.xmlbeans.impl.values.XmlObjectBase.KIND_SETTERHELPER_ARRAYITEM);");
                    this.emitPost(sType, 1, identifier, isAttr, "i");
                } else {
                    this.emitImplementationPreamble();
                    this.emitPre(sType, 1, identifier, isAttr, "i");
                    this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, jtargetType);
                    this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                    this.emitPost(sType, 1, identifier, isAttr, "i");
                    this.emitImplementationPostamble();
                }
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.XSET_ARRAY))) {
                this.printJavaDoc("Sets (as xml) array of all " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void xset" + arrayName + "(" + xtype + "[]" + safeVarName + "Array) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr);
                this.emit("arraySetterHelper(" + safeVarName + "Array, " + identifier + ");");
                this.emitPost(sType, 1, identifier, isAttr);
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.XSET_IDX))) {
                this.printJavaDoc("Sets (as xml) ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void xset" + arrayName + "(int i, " + xtype + " " + safeVarName + ") {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, "i");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("target.set(" + safeVarName + ");");
                this.emitPost(sType, 1, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (nillable && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.SET_NIL_IDX))) {
                this.printJavaDoc("Nils the ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void setNil" + arrayName + "(int i) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 1, identifier, isAttr, "i");
                this.emitGetTarget(setIdentifier, identifier, isAttr, "i", 4, xtype);
                this.emit("target.setNil();");
                this.emitPost(sType, 1, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.INSERT_IDX))) {
                this.printJavaDoc("Inserts the value as the ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void insert" + propertyName + "(int i, " + type + " " + safeVarName + ") {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 2, identifier, isAttr, "i");
                this.emit(jtargetType + " target =");
                this.indent();
                if (!isSubstGroup) {
                    this.emit("(" + jtargetType + ")get_store().insert_element_user(" + identifier + ", i);");
                } else {
                    this.emit("(" + jtargetType + ")get_store().insert_element_user(" + setIdentifier + ", " + identifier + ", i);");
                }
                this.outdent();
                this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                this.emitPost(sType, 2, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (!xmltype && (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.ADD))) {
                this.printJavaDoc("Appends the value as the last " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void add" + propertyName + "(" + type + " " + safeVarName + ") {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emit(jtargetType + " target = null;");
                this.emitPre(sType, 2, identifier, isAttr);
                this.emitAddTarget(identifier, isAttr, jtargetType);
                this.printJSetValue(javaType, safeVarName, (SchemaTypeImpl)prop.getType());
                this.emitPost(sType, 2, identifier, isAttr);
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.INSERT_NEW_IDX)) {
                this.printJavaDoc("Inserts and returns a new empty value (as xml) as the ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + xtype + " insertNew" + propertyName + "(int i) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emit(xtype + " target = null;");
                this.emitPre(sType, 2, identifier, isAttr, "i");
                if (!isSubstGroup) {
                    this.emit("target = (" + xtype + ")get_store().insert_element_user(" + identifier + ", i);");
                } else {
                    this.emit("target = (" + xtype + ")get_store().insert_element_user(" + setIdentifier + ", " + identifier + ", i);");
                }
                this.emitPost(sType, 2, identifier, isAttr, "i");
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.ADD_NEW)) {
                this.printJavaDoc("Appends and returns a new empty value (as xml) as the last " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public " + xtype + " addNew" + propertyName + "() {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emit(xtype + " target = null;");
                this.emitPre(sType, 2, identifier, isAttr);
                this.emitAddTarget(identifier, isAttr, xtype);
                this.emitPost(sType, 2, identifier, isAttr);
                this.emit("return target;");
                this.emitImplementationPostamble();
                this.endBlock();
            }
            if (bmList == null || bmList.contains((Object)XmlOptions.BeanMethod.REMOVE_IDX)) {
                this.printJavaDoc("Removes the ith " + propdesc);
                if (!this.opt.isCompileNoAnnotations()) {
                    this.emit("@Override");
                }
                this.emit("public void remove" + propertyName + "(int i) {");
                this.startBlock();
                this.emitImplementationPreamble();
                this.emitPre(sType, 3, identifier, isAttr, "i");
                this.emit("get_store().remove_element(" + setIdentifier + ", i);");
                this.emitPost(sType, 3, identifier, isAttr, "i");
                this.emitImplementationPostamble();
                this.endBlock();
            }
        }
    }

    SchemaProperty[] getSchemaProperties(SchemaType sType) {
        if (sType.getContentType() != 2) {
            return this.getDerivedProperties(sType);
        }
        SchemaType baseType = sType.getBaseType();
        ArrayList<SchemaProperty> extraProperties = null;
        while (!baseType.isSimpleType() && !baseType.isBuiltinType()) {
            for (SchemaProperty baseProperty : baseType.getDerivedProperties()) {
                if (baseProperty.isAttribute() && sType.getAttributeProperty(baseProperty.getName()) != null) continue;
                if (extraProperties == null) {
                    extraProperties = new ArrayList<SchemaProperty>();
                }
                extraProperties.add(baseProperty);
            }
            baseType = baseType.getBaseType();
        }
        SchemaProperty[] properties = sType.getProperties();
        if (extraProperties == null) {
            return properties;
        }
        Collections.addAll(extraProperties, properties);
        return extraProperties.toArray(new SchemaProperty[0]);
    }

    void printInnerTypeImpl(SchemaType sType, SchemaTypeSystem system, boolean isInner) throws IOException {
        String shortName = sType.getShortJavaImplName();
        this.printInnerTypeJavaDoc(sType);
        this.startClass(sType, isInner);
        this.printConstructor(sType, shortName);
        this.printExtensionImplMethods(sType);
        if (!sType.isSimpleType()) {
            SchemaProperty[] properties = this.getSchemaProperties(sType);
            HashMap<QName, Integer> qnameMap = new HashMap<QName, Integer>();
            HashMap<QName, Integer> qsetMap = new HashMap<QName, Integer>();
            this.printStaticFields(properties, qnameMap, qsetMap);
            for (SchemaProperty prop : properties) {
                this.printGetterImpls(prop, qnameMap, qsetMap);
                if (prop.isReadOnly()) continue;
                this.printSetterImpls(prop, qnameMap, qsetMap, sType);
            }
        }
        this.printNestedTypeImpls(sType, system);
        this.endBlock();
    }

    private SchemaProperty[] getDerivedProperties(SchemaType sType) {
        QName name = sType.getName();
        if (name != null && name.equals(sType.getBaseType().getName())) {
            SchemaType sType2 = sType.getBaseType();
            SchemaProperty[] props = sType.getDerivedProperties();
            LinkedHashMap<QName, SchemaProperty> propsByName = new LinkedHashMap<QName, SchemaProperty>();
            for (SchemaProperty prop : props) {
                propsByName.put(prop.getName(), prop);
            }
            while (sType2 != null && name.equals(sType2.getName())) {
                for (SchemaProperty prop : props = sType2.getDerivedProperties()) {
                    if (propsByName.containsKey(prop.getName())) continue;
                    propsByName.put(prop.getName(), prop);
                }
                sType2 = sType2.getBaseType();
            }
            return propsByName.values().toArray(new SchemaProperty[0]);
        }
        return sType.getDerivedProperties();
    }

    private void printExtensionImplMethods(SchemaType sType) throws IOException {
        SchemaTypeImpl sImpl = SchemaTypeCodePrinter.getImpl(sType);
        if (sImpl == null) {
            return;
        }
        InterfaceExtension[] exts = sImpl.getInterfaceExtensions();
        if (exts != null) {
            for (InterfaceExtension ext : exts) {
                InterfaceExtension.MethodSignature[] methods = ext.getMethods();
                if (methods == null) continue;
                for (InterfaceExtension.MethodSignature method : methods) {
                    this.printJavaDoc("Implementation method for interface " + ext.getStaticHandler());
                    this.printInterfaceMethodDecl(method);
                    this.startBlock();
                    this.printInterfaceMethodImpl(ext.getStaticHandler(), method);
                    this.endBlock();
                }
            }
        }
    }

    void printInterfaceMethodDecl(InterfaceExtension.MethodSignature method) throws IOException {
        StringBuilder decl = new StringBuilder(60);
        decl.append("public ").append(method.getReturnType());
        decl.append(" ").append(method.getName()).append("(");
        String[] paramTypes = method.getParameterTypes();
        String[] paramNames = method.getParameterNames();
        for (int i = 1; i < paramTypes.length; ++i) {
            if (i > 1) {
                decl.append(", ");
            }
            decl.append(paramTypes[i]).append(" ").append(paramNames[i]);
        }
        decl.append(")");
        String[] exceptions = method.getExceptionTypes();
        for (int i = 0; i < exceptions.length; ++i) {
            decl.append(i == 0 ? " throws " : ", ").append(exceptions[i]);
        }
        decl.append(" {");
        this.emit(decl.toString());
    }

    void printInterfaceMethodImpl(String handler, InterfaceExtension.MethodSignature method) throws IOException {
        StringBuilder impl = new StringBuilder(60);
        if (!method.getReturnType().equals("void")) {
            impl.append("return ");
        }
        impl.append(handler).append(".").append(method.getName()).append("(this");
        String[] params = method.getParameterTypes();
        String[] paramsNames = method.getParameterNames();
        for (int i = 1; i < params.length; ++i) {
            impl.append(", ").append(paramsNames[i]);
        }
        impl.append(");");
        this.emit(impl.toString());
    }

    void printNestedTypeImpls(SchemaType sType, SchemaTypeSystem system) throws IOException {
        boolean redefinition;
        boolean bl = redefinition = sType.getName() != null && sType.getName().equals(sType.getBaseType().getName());
        while (sType != null) {
            SchemaType[] anonTypes;
            for (SchemaType anonType : anonTypes = sType.getAnonymousTypes()) {
                if (anonType.isSkippedAnonymousType()) {
                    this.printNestedTypeImpls(anonType, system);
                    continue;
                }
                this.printInnerTypeImpl(anonType, system, true);
            }
            if (!redefinition || sType.getDerivationType() != 2 && !sType.isSimpleType()) break;
            sType = sType.getBaseType();
        }
    }

    @Override
    public void printHolder(Writer writer, SchemaTypeSystem system, XmlOptions opt, Repackager repackager) throws IOException {
        this._writer = writer;
        String sysPack = system.getName();
        if (repackager != null) {
            sysPack = repackager.repackage(new StringBuffer(sysPack)).toString();
        }
        this.emit("package " + sysPack + ";");
        this.emit("");
        this.emit("import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;");
        this.emit("");
        this.emit("public final class TypeSystemHolder extends SchemaTypeSystemImpl {");
        this.indent();
        this.emit("public static final TypeSystemHolder typeSystem = new TypeSystemHolder();");
        this.emit("");
        this.emit("private TypeSystemHolder() {");
        this.indent();
        this.emit("super(TypeSystemHolder.class);");
        this.outdent();
        this.emit("}");
        this.outdent();
        this.emit("}");
    }
}

