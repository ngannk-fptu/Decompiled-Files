/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBeanHelperWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.JavaWriter;
import org.apache.axis.wsdl.toJava.Utils;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;

public class JavaBeanWriter
extends JavaClassWriter {
    private TypeEntry type;
    private Vector elements;
    private Vector attributes;
    private TypeEntry extendType;
    protected JavaBeanHelperWriter helper;
    protected Vector names = new Vector();
    protected ArrayList simpleValueTypes = new ArrayList();
    protected Set enumerationTypes = new HashSet();
    protected PrintWriter pw;
    protected boolean enableDefaultConstructor = true;
    protected boolean enableFullConstructor = false;
    protected boolean enableSimpleConstructors = false;
    protected boolean enableToString = false;
    protected boolean enableSetters = true;
    protected boolean enableGetters = true;
    protected boolean enableEquals = true;
    protected boolean enableHashCode = true;
    protected boolean enableMemberFields = true;
    protected boolean isAny = false;
    protected boolean isMixed = false;

    protected JavaBeanWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry extendType, Vector attributes, JavaWriter helper) {
        super(emitter, type.getName(), "complexType");
        this.type = type;
        this.elements = elements;
        this.attributes = attributes;
        this.extendType = extendType;
        this.helper = (JavaBeanHelperWriter)helper;
        if (type.isSimpleType()) {
            this.enableSimpleConstructors = true;
            this.enableToString = true;
        } else if (null != extendType && null != SchemaUtils.getComplexElementRestrictionBase(type.getNode(), emitter.getSymbolTable())) {
            this.enableMemberFields = false;
            this.enableGetters = false;
            this.enableSetters = false;
            this.enableEquals = false;
            this.enableHashCode = false;
        }
    }

    protected void writeFileHeader(PrintWriter pw) throws IOException {
        this.writeHeaderComments(pw);
        this.writePackage(pw);
        try {
            String comments = SchemaUtils.getAnnotationDocumentation(this.type.getNode());
            comments = this.getJavadocDescriptionPart(comments, false);
            if (comments != null && comments.trim().length() > 0) {
                pw.println();
                pw.println("/**");
                pw.println(comments);
                pw.println(" */");
            }
        }
        catch (DOMException dOMException) {
            // empty catch block
        }
        pw.println(this.getClassModifiers() + this.getClassText() + this.getClassName() + ' ' + this.getExtendsText() + this.getImplementsText() + "{");
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        this.pw = pw;
        if (this.enableMemberFields) {
            this.writeMemberFields();
        }
        if (this.enableDefaultConstructor) {
            this.writeDefaultConstructor();
        }
        if (this.enableFullConstructor) {
            this.writeFullConstructor();
        }
        if (this.enableSimpleConstructors) {
            this.writeSimpleConstructors();
        }
        if (!this.enableFullConstructor && !this.enableSimpleConstructors && this.enableMemberFields) {
            this.writeMinimalConstructor();
        }
        if (this.enableToString) {
            this.writeToStringMethod();
        }
        this.writeAccessMethods();
        if (this.enableEquals) {
            this.writeEqualsMethod();
        }
        if (this.enableHashCode) {
            this.writeHashCodeMethod();
        }
        if (!this.emitter.isHelperWanted()) {
            this.helper.setPrintWriter(pw);
        }
        this.helper.generate();
    }

    protected void preprocess() {
        String variableName;
        String typeName;
        int i;
        if (this.elements != null) {
            for (i = 0; i < this.elements.size(); ++i) {
                ElementDecl elem = (ElementDecl)this.elements.get(i);
                typeName = elem.getType().getName();
                if (elem.getAnyElement()) {
                    typeName = "org.apache.axis.message.MessageElement []";
                    variableName = "_any";
                    this.isAny = true;
                } else {
                    variableName = elem.getName();
                    if (elem.getType().getUnderlTypeNillable() || elem.getNillable() && elem.getMaxOccursIsUnbounded()) {
                        typeName = Utils.getWrapperType(elem.getType());
                    } else if (elem.getMinOccursIs0() && elem.getMaxOccursIsExactlyOne() || elem.getNillable() || elem.getOptional()) {
                        typeName = Utils.getWrapperType(typeName);
                    }
                }
                variableName = JavaUtils.getUniqueValue(this.helper.reservedPropNames, variableName);
                this.names.add(typeName);
                this.names.add(variableName);
                if (this.type.isSimpleType() && (variableName.endsWith("Value") || variableName.equals("_value"))) {
                    this.simpleValueTypes.add(typeName);
                }
                if (null == Utils.getEnumerationBaseAndValues(elem.getType().getNode(), this.emitter.getSymbolTable())) continue;
                this.enumerationTypes.add(typeName);
            }
        }
        if (this.enableMemberFields && SchemaUtils.isMixed(this.type.getNode())) {
            this.isMixed = true;
            if (!this.isAny) {
                this.names.add("org.apache.axis.message.MessageElement []");
                this.names.add("_any");
            }
        }
        if (this.attributes != null) {
            for (i = 0; i < this.attributes.size(); ++i) {
                ContainedAttribute attr = (ContainedAttribute)this.attributes.get(i);
                typeName = attr.getType().getName();
                variableName = attr.getName();
                if (attr.getOptional()) {
                    typeName = Utils.getWrapperType(typeName);
                }
                variableName = JavaUtils.getUniqueValue(this.helper.reservedPropNames, variableName);
                this.names.add(typeName);
                this.names.add(variableName);
                if (this.type.isSimpleType() && (variableName.endsWith("Value") || variableName.equals("_value"))) {
                    this.simpleValueTypes.add(typeName);
                }
                if (null == Utils.getEnumerationBaseAndValues(attr.getType().getNode(), this.emitter.getSymbolTable())) continue;
                this.enumerationTypes.add(typeName);
            }
        }
        if (this.extendType != null && this.extendType.getDimensions().equals("[]")) {
            String typeName2 = this.extendType.getName();
            String elemName = this.extendType.getQName().getLocalPart();
            String variableName2 = Utils.xmlNameToJava(elemName);
            this.names.add(typeName2);
            this.names.add(variableName2);
        }
        block2: for (int i2 = 1; i2 < this.names.size(); i2 += 2) {
            int suffix = 2;
            String s = (String)this.names.elementAt(i2);
            if (i2 >= this.names.size() - 2) continue;
            int dup = this.names.indexOf(s, i2 + 1);
            while (dup > 0) {
                this.names.set(dup, this.names.get(dup) + Integer.toString(suffix));
                ++suffix;
                if (i2 >= this.names.size() - 2) continue block2;
                dup = this.names.indexOf(s, dup + 1);
            }
        }
    }

    protected String getBinaryTypeEncoderName(String elementName) {
        TypeEntry type = this.getElementDecl(elementName);
        if (type != null) {
            String typeName = type.getQName().getLocalPart();
            if (typeName.equals("base64Binary")) {
                return "org.apache.axis.encoding.Base64";
            }
            if (typeName.equals("hexBinary")) {
                return "org.apache.axis.types.HexBinary";
            }
            throw new RuntimeException("Unknown binary type " + typeName + " for element " + elementName);
        }
        throw new RuntimeException("Unknown element " + elementName);
    }

    protected TypeEntry getElementDecl(String elementName) {
        if (this.elements != null) {
            for (int i = 0; i < this.elements.size(); ++i) {
                ElementDecl elem = (ElementDecl)this.elements.get(i);
                String variableName = elem.getAnyElement() ? "_any" : elem.getName();
                if (!variableName.equals(elementName)) continue;
                return elem.getType();
            }
        }
        return null;
    }

    protected String getClassModifiers() {
        Node node = this.type.getNode();
        if (node != null && JavaUtils.isTrueExplicitly(Utils.getAttribute(node, "abstract"))) {
            return super.getClassModifiers() + "abstract ";
        }
        return super.getClassModifiers();
    }

    protected String getExtendsText() {
        String extendsText = "";
        if (this.extendType != null && !this.type.isSimpleType() && this.extendType.getDimensions().length() == 0) {
            extendsText = " extends " + this.extendType.getName() + " ";
        }
        return extendsText;
    }

    protected String getImplementsText() {
        String implementsText = " implements java.io.Serializable";
        if (this.type.isSimpleType()) {
            implementsText = implementsText + ", org.apache.axis.encoding.SimpleType";
        }
        this.preprocess();
        if (this.isAny) {
            implementsText = implementsText + ", org.apache.axis.encoding.AnyContentType";
        }
        if (this.isMixed) {
            implementsText = implementsText + ", org.apache.axis.encoding.MixedContentType";
        }
        implementsText = implementsText + " ";
        return implementsText;
    }

    protected void writeMemberFields() {
        if (this.isUnion()) {
            this.pw.println("    private java.lang.String _value;");
            return;
        }
        for (int i = 0; i < this.names.size(); i += 2) {
            String comments = "";
            if (this.elements != null && this.elements != null && i < this.elements.size() * 2) {
                ElementDecl elem = (ElementDecl)this.elements.get(i / 2);
                comments = elem.getDocumentation();
            }
            String typeName = (String)this.names.get(i);
            String variable = (String)this.names.get(i + 1);
            if (comments != null && comments.trim().length() > 0) {
                String flatComments = this.getJavadocDescriptionPart(comments, false).substring(3);
                this.pw.println("    /** " + flatComments.trim() + " */");
            }
            this.pw.print("    private " + typeName + " " + variable + ";");
            if (this.elements == null || i >= this.elements.size() * 2) {
                this.pw.println("  // attribute");
                continue;
            }
            this.pw.println();
        }
        this.pw.println();
    }

    protected void writeDefaultConstructor() {
        this.pw.println("    public " + this.className + "() {");
        this.pw.println("    }");
        this.pw.println();
    }

    protected void writeMinimalConstructor() {
        int i;
        if (this.isUnion() || this.names.size() == 0) {
            return;
        }
        this.pw.println("    public " + this.className + "(");
        for (i = 0; i < this.names.size(); i += 2) {
            String typeName = (String)this.names.get(i);
            String variable = (String)this.names.get(i + 1);
            this.pw.print("           " + typeName + " " + variable);
            if (i >= this.names.size() - 2) {
                this.pw.println(") {");
                continue;
            }
            this.pw.println(",");
        }
        for (i = 0; i < this.names.size(); i += 2) {
            String variable = (String)this.names.get(i + 1);
            this.pw.println("           this." + variable + " = " + variable + ";");
            if (i >= this.names.size() - 2) break;
        }
        this.pw.println("    }");
        this.pw.println();
    }

    protected void writeFullConstructor() {
        if (this.type.isSimpleType()) {
            return;
        }
        Vector<TypeEntry> extendList = new Vector<TypeEntry>();
        extendList.add(this.type);
        TypeEntry parent = this.extendType;
        while (parent != null) {
            extendList.add(parent);
            parent = SchemaUtils.getComplexElementExtensionBase(parent.getNode(), this.emitter.getSymbolTable());
        }
        Vector<String> paramTypes = new Vector<String>();
        Vector<String> paramNames = new Vector<String>();
        for (int i = extendList.size() - 1; i >= 0; --i) {
            Vector elements;
            Vector attributes;
            TypeEntry te = (TypeEntry)extendList.elementAt(i);
            String mangle = "";
            if (i > 0) {
                mangle = "_" + Utils.xmlNameToJava(te.getQName().getLocalPart()) + "_";
            }
            if ((attributes = te.getContainedAttributes()) != null) {
                for (int j = 0; j < attributes.size(); ++j) {
                    ContainedAttribute attr = (ContainedAttribute)attributes.get(j);
                    paramTypes.add(attr.getType().getName());
                    paramNames.add(JavaUtils.getUniqueValue(this.helper.reservedPropNames, attr.getName()));
                }
            }
            if ((elements = te.getContainedElements()) == null) continue;
            for (int j = 0; j < elements.size(); ++j) {
                ElementDecl elem = (ElementDecl)elements.get(j);
                paramTypes.add(elem.getType().getName());
                paramNames.add(JavaUtils.getUniqueValue(this.helper.reservedPropNames, elem.getName()));
            }
        }
        int localParams = paramTypes.size() - this.names.size() / 2;
        if (paramTypes.size() > 0) {
            int j;
            this.pw.println("    public " + this.className + "(");
            for (int i = 0; i < paramTypes.size(); ++i) {
                this.pw.print("           " + paramTypes.elementAt(i) + " " + paramNames.elementAt(i));
                if (i + 1 < paramTypes.size()) {
                    this.pw.println(",");
                    continue;
                }
                this.pw.println(") {");
            }
            if (this.extendType != null && localParams > 0) {
                this.pw.println("        super(");
                for (j = 0; j < localParams; ++j) {
                    this.pw.print("            " + paramNames.elementAt(j));
                    if (j + 1 < localParams) {
                        this.pw.println(",");
                        continue;
                    }
                    this.pw.println(");");
                }
            }
            for (j = localParams; j < paramNames.size(); ++j) {
                this.pw.println("        this." + paramNames.elementAt(j) + " = " + paramNames.elementAt(j) + ";");
            }
            this.pw.println("    }");
            this.pw.println();
        }
    }

    protected void writeSimpleConstructors() {
        if (this.simpleValueTypes.size() == 0) {
            return;
        }
        this.pw.println("    // " + Messages.getMessage("needStringCtor"));
        if (this.isUnion() || this.simpleValueTypes.get(0).equals("java.lang.String")) {
            this.pw.println("    public " + this.className + "(java.lang.String _value) {");
            this.pw.println("        this._value = _value;");
            this.pw.println("    }");
            int i = 0;
            Iterator iterator = this.simpleValueTypes.iterator();
            while (iterator.hasNext()) {
                String typeName = (String)iterator.next();
                if (typeName.equals("java.lang.String")) {
                    i += 2;
                    continue;
                }
                String capName = "_value";
                if (this.isUnion()) {
                    String name = (String)this.names.get(i + 1);
                    capName = Utils.capitalizeFirstChar(name);
                }
                this.pw.println("    public " + this.className + "(" + typeName + " _value) {");
                this.pw.println("        set" + capName + "(_value);");
                this.pw.println("    }");
                this.pw.println();
                i += 2;
            }
        } else if (this.simpleValueTypes.size() == 1) {
            this.pw.println("    public " + this.className + "(" + this.simpleValueTypes.get(0) + " _value) {");
            this.pw.println("        this._value = _value;");
            this.pw.println("    }");
            this.pw.println("    public " + this.className + "(java.lang.String _value) {");
            this.writeSimpleTypeGetter((String)this.simpleValueTypes.get(0), null, "this._value =");
            this.pw.println("    }");
            this.pw.println();
        }
    }

    protected void writeSimpleTypeGetter(String simpleValueType, String name, String returnString) {
        String wrapper = JavaUtils.getWrapper(simpleValueType);
        if (wrapper != null) {
            this.pw.println("        " + returnString + " new " + wrapper + "(_value)." + simpleValueType + "Value();");
        } else if (simpleValueType.equals("byte[]")) {
            String encoder = this.getBinaryTypeEncoderName("_value");
            this.pw.println("        " + returnString + " " + encoder + ".decode(_value);");
        } else if (simpleValueType.equals("org.apache.axis.types.URI")) {
            this.pw.println("        try {");
            this.pw.println("            " + returnString + " new org.apache.axis.types.URI(_value);");
            this.pw.println("        }");
            this.pw.println("        catch (org.apache.axis.types.URI.MalformedURIException mue) {");
            this.pw.println("            throw new java.lang.RuntimeException(mue.toString());");
            this.pw.println("       }");
        } else if (simpleValueType.equals("java.util.Date")) {
            this.pw.println("        try {");
            this.pw.println("            " + returnString + " (java.text.DateFormat.getDateTimeInstance()).parse(_value);");
            this.pw.println("        }");
            this.pw.println("        catch (java.text.ParseException e){");
            this.pw.println("            throw new java.lang.RuntimeException(e.toString());");
            this.pw.println("        }");
        } else if (simpleValueType.equals("java.util.Calendar")) {
            this.pw.println("        java.util.Calendar cal =");
            this.pw.println("            (java.util.Calendar) new org.apache.axis.encoding.ser.CalendarDeserializer(");
            this.pw.println("                java.lang.String.class, org.apache.axis.Constants.XSD_STRING).makeValue(_value);");
            this.pw.println("        " + returnString + " cal;");
        } else if (this.enumerationTypes.contains(simpleValueType)) {
            this.pw.println("        " + returnString + " " + simpleValueType + ".fromString(_value);");
        } else {
            this.pw.println("        " + returnString + " new " + simpleValueType + "(_value);");
        }
    }

    private boolean isUnion() {
        return this.simpleValueTypes.size() > 1;
    }

    protected void writeToStringMethod() {
        if (this.simpleValueTypes.size() == 0) {
            return;
        }
        this.pw.println("    // " + Messages.getMessage("needToString"));
        this.pw.println("    public java.lang.String toString() {");
        if (this.isUnion() || this.simpleValueTypes.get(0).equals("java.lang.String")) {
            this.pw.println("        return _value;");
        } else {
            String wrapper = JavaUtils.getWrapper((String)this.simpleValueTypes.get(0));
            if (wrapper != null) {
                this.pw.println("        return new " + wrapper + "(_value).toString();");
            } else {
                String simpleValueType0 = (String)this.simpleValueTypes.get(0);
                if (simpleValueType0.equals("byte[]")) {
                    String encoder = this.getBinaryTypeEncoderName("_value");
                    this.pw.println("        return _value == null ? null : " + encoder + ".encode(_value);");
                } else if (simpleValueType0.equals("java.util.Calendar")) {
                    this.pw.println("        return _value == null ? null : new org.apache.axis.encoding.ser.CalendarSerializer().getValueAsString(_value, null);");
                } else {
                    this.pw.println("        return _value == null ? null : _value.toString();");
                }
            }
        }
        this.pw.println("    }");
        this.pw.println();
    }

    protected void writeSimpleTypeSetter(String simpleValueType) {
        String wrapper = JavaUtils.getWrapper(simpleValueType);
        if (wrapper != null) {
            this.pw.println("        this._value = new " + wrapper + "(_value).toString();");
        } else if (simpleValueType.equals("byte[]")) {
            String encoder = this.getBinaryTypeEncoderName("_value");
            this.pw.println("        this._value = _value == null ? null : " + encoder + ".encode(_value);");
        } else if (simpleValueType.equals("java.util.Calendar")) {
            this.pw.println("        this._value = _value == null ? null : new org.apache.axis.encoding.ser.CalendarSerializer().getValueAsString(_value, null);");
        } else {
            this.pw.println("        this._value = _value == null ? null : _value.toString();");
        }
    }

    protected void writeAccessMethods() {
        int j = 0;
        int i = 0;
        while (i < this.names.size()) {
            ElementDecl elem;
            String comment;
            String typeName = (String)this.names.get(i);
            String name = (String)this.names.get(i + 1);
            String capName = Utils.capitalizeFirstChar(name);
            String documentation = "";
            if (this.elements != null && this.elements != null && i < this.elements.size() * 2) {
                ElementDecl elem2 = (ElementDecl)this.elements.get(i / 2);
                documentation = elem2.getDocumentation();
            }
            String get = "get";
            if (typeName.equals("boolean")) {
                get = "is";
            }
            if ((comment = this.getJavadocDescriptionPart(documentation, false)).length() > 3) {
                comment = comment.substring(2);
            }
            if (this.enableGetters) {
                try {
                    this.pw.println();
                    this.pw.println("    /**");
                    this.pw.println("     * Gets the " + name + " value for this " + this.getClassName() + ".");
                    this.pw.println("     * ");
                    this.pw.println("     * @return " + name + comment);
                    this.pw.println("     */");
                }
                catch (DOMException e) {
                    // empty catch block
                }
                this.pw.println("    public " + typeName + " " + get + capName + "() {");
                if (this.isUnion()) {
                    this.writeSimpleTypeGetter(typeName, name, "return");
                } else {
                    this.pw.println("        return " + name + ";");
                }
                this.pw.println("    }");
                this.pw.println();
            }
            if (this.enableSetters) {
                try {
                    String nm = this.isUnion() ? "_value" : name;
                    this.pw.println();
                    this.pw.println("    /**");
                    this.pw.println("     * Sets the " + nm + " value for this " + this.getClassName() + ".");
                    this.pw.println("     * ");
                    this.pw.println("     * @param " + nm + comment);
                    this.pw.println("     */");
                }
                catch (DOMException e) {
                    // empty catch block
                }
                if (this.isUnion()) {
                    this.pw.println("    public void set" + capName + "(" + typeName + " _value) {");
                    this.writeSimpleTypeSetter(typeName);
                } else {
                    this.pw.println("    public void set" + capName + "(" + typeName + " " + name + ") {");
                    this.pw.println("        this." + name + " = " + name + ";");
                }
                this.pw.println("    }");
                this.pw.println();
            }
            if (this.elements != null && j < this.elements.size() && (elem = (ElementDecl)this.elements.get(j)).getType().getQName().getLocalPart().indexOf("[") > 0) {
                String compName = typeName.substring(0, typeName.lastIndexOf("["));
                if (this.enableGetters) {
                    this.pw.println("    public " + compName + " " + get + capName + "(int i) {");
                    this.pw.println("        return this." + name + "[i];");
                    this.pw.println("    }");
                    this.pw.println();
                }
                if (this.enableSetters) {
                    this.pw.println("    public void set" + capName + "(int i, " + compName + " _value) {");
                    this.pw.println("        this." + name + "[i] = _value;");
                    this.pw.println("    }");
                    this.pw.println();
                }
            }
            i += 2;
            ++j;
        }
    }

    protected void writeEqualsMethod() {
        this.pw.println("    private java.lang.Object __equalsCalc = null;");
        this.pw.println("    public synchronized boolean equals(java.lang.Object obj) {");
        this.pw.println("        if (!(obj instanceof " + this.className + ")) return false;");
        this.pw.println("        " + this.className + " other = (" + this.className + ") obj;");
        this.pw.println("        if (obj == null) return false;");
        this.pw.println("        if (this == obj) return true;");
        this.pw.println("        if (__equalsCalc != null) {");
        this.pw.println("            return (__equalsCalc == obj);");
        this.pw.println("        }");
        this.pw.println("        __equalsCalc = obj;");
        String truth = "true";
        if (this.extendType != null && !this.type.isSimpleType()) {
            truth = "super.equals(obj)";
        }
        this.pw.println("        boolean _equals;");
        if (this.names.size() == 0) {
            this.pw.println("        _equals = " + truth + ";");
        } else if (this.isUnion()) {
            this.pw.println("        _equals = " + truth + " && " + " this.toString().equals(obj.toString());");
        } else {
            this.pw.println("        _equals = " + truth + " && ");
            for (int i = 0; i < this.names.size(); i += 2) {
                String variableType = (String)this.names.get(i);
                String variable = (String)this.names.get(i + 1);
                String get = "get";
                if (variableType.equals("boolean")) {
                    get = "is";
                }
                if (variableType.equals("int") || variableType.equals("long") || variableType.equals("short") || variableType.equals("float") || variableType.equals("double") || variableType.equals("boolean") || variableType.equals("byte")) {
                    this.pw.print("            this." + variable + " == other." + get + Utils.capitalizeFirstChar(variable) + "()");
                } else if (variableType.indexOf("[") >= 0) {
                    this.pw.println("            ((this." + variable + "==null && other." + get + Utils.capitalizeFirstChar(variable) + "()==null) || ");
                    this.pw.println("             (this." + variable + "!=null &&");
                    this.pw.print("              java.util.Arrays.equals(this." + variable + ", other." + get + Utils.capitalizeFirstChar(variable) + "())))");
                } else {
                    this.pw.println("            ((this." + variable + "==null && other." + get + Utils.capitalizeFirstChar(variable) + "()==null) || ");
                    this.pw.println("             (this." + variable + "!=null &&");
                    this.pw.print("              this." + variable + ".equals(other." + get + Utils.capitalizeFirstChar(variable) + "())))");
                }
                if (i == this.names.size() - 2) {
                    this.pw.println(";");
                    continue;
                }
                this.pw.println(" &&");
            }
        }
        this.pw.println("        __equalsCalc = null;");
        this.pw.println("        return _equals;");
        this.pw.println("    }");
        this.pw.println("");
    }

    protected void writeHashCodeMethod() {
        this.pw.println("    private boolean __hashCodeCalc = false;");
        this.pw.println("    public synchronized int hashCode() {");
        this.pw.println("        if (__hashCodeCalc) {");
        this.pw.println("            return 0;");
        this.pw.println("        }");
        this.pw.println("        __hashCodeCalc = true;");
        String start = "1";
        if (this.extendType != null && !this.type.isSimpleType()) {
            start = "super.hashCode()";
        }
        this.pw.println("        int _hashCode = " + start + ";");
        if (this.isUnion()) {
            this.pw.println("        if (this._value != null) {");
            this.pw.println("            _hashCode += this._value.hashCode();");
            this.pw.println("        }");
        }
        for (int i = 0; !this.isUnion() && i < this.names.size(); i += 2) {
            String variableType = (String)this.names.get(i);
            String variable = (String)this.names.get(i + 1);
            String get = "get";
            if (variableType.equals("boolean")) {
                get = "is";
            }
            if (variableType.equals("int") || variableType.equals("short") || variableType.equals("byte")) {
                this.pw.println("        _hashCode += " + get + Utils.capitalizeFirstChar(variable) + "();");
                continue;
            }
            if (variableType.equals("boolean")) {
                this.pw.println("        _hashCode += (" + get + Utils.capitalizeFirstChar(variable) + "() ? Boolean.TRUE : Boolean.FALSE).hashCode();");
                continue;
            }
            if (variableType.equals("long")) {
                this.pw.println("        _hashCode += new Long(" + get + Utils.capitalizeFirstChar(variable) + "()).hashCode();");
                continue;
            }
            if (variableType.equals("float")) {
                this.pw.println("        _hashCode += new Float(" + get + Utils.capitalizeFirstChar(variable) + "()).hashCode();");
                continue;
            }
            if (variableType.equals("double")) {
                this.pw.println("        _hashCode += new Double(" + get + Utils.capitalizeFirstChar(variable) + "()).hashCode();");
                continue;
            }
            if (variableType.indexOf("[") >= 0) {
                this.pw.println("        if (" + get + Utils.capitalizeFirstChar(variable) + "() != null) {");
                this.pw.println("            for (int i=0;");
                this.pw.println("                 i<java.lang.reflect.Array.getLength(" + get + Utils.capitalizeFirstChar(variable) + "());");
                this.pw.println("                 i++) {");
                this.pw.println("                java.lang.Object obj = java.lang.reflect.Array.get(" + get + Utils.capitalizeFirstChar(variable) + "(), i);");
                this.pw.println("                if (obj != null &&");
                this.pw.println("                    !obj.getClass().isArray()) {");
                this.pw.println("                    _hashCode += obj.hashCode();");
                this.pw.println("                }");
                this.pw.println("            }");
                this.pw.println("        }");
                continue;
            }
            this.pw.println("        if (" + get + Utils.capitalizeFirstChar(variable) + "() != null) {");
            this.pw.println("            _hashCode += " + get + Utils.capitalizeFirstChar(variable) + "().hashCode();");
            this.pw.println("        }");
        }
        this.pw.println("        __hashCodeCalc = false;");
        this.pw.println("        return _hashCode;");
        this.pw.println("    }");
        this.pw.println("");
    }

    public void generate() throws IOException {
        String fqcn = this.getPackage() + "." + this.getClassName();
        if (this.emitter.isDeploy() && this.emitter.doesExist(fqcn)) {
            if (this.emitter.isHelperWanted()) {
                this.helper.generate();
            }
        } else {
            super.generate();
        }
    }
}

