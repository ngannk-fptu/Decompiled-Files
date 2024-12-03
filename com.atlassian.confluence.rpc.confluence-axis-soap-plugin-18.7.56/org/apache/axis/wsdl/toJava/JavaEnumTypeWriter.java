/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaEnumTypeWriter
extends JavaClassWriter {
    private Vector elements;
    private TypeEntry type;

    protected JavaEnumTypeWriter(Emitter emitter, TypeEntry type, Vector elements) {
        super(emitter, type.getName(), "enumType");
        this.elements = elements;
        this.type = type;
    }

    protected String getImplementsText() {
        return "implements java.io.Serializable ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        int i;
        String baseType;
        String javaName = this.getClassName();
        String baseClass = baseType = ((TypeEntry)this.elements.get(0)).getName();
        if (baseType.indexOf("int") == 0) {
            baseClass = "java.lang.Integer";
        } else if (baseType.indexOf("char") == 0) {
            baseClass = "java.lang.Character";
        } else if (baseType.indexOf("short") == 0) {
            baseClass = "java.lang.Short";
        } else if (baseType.indexOf("long") == 0) {
            baseClass = "java.lang.Long";
        } else if (baseType.indexOf("double") == 0) {
            baseClass = "java.lang.Double";
        } else if (baseType.indexOf("float") == 0) {
            baseClass = "java.lang.Float";
        } else if (baseType.indexOf("byte") == 0) {
            baseClass = "java.lang.Byte";
        }
        Vector<String> values = new Vector<String>();
        for (int i2 = 1; i2 < this.elements.size(); ++i2) {
            String value = (String)this.elements.get(i2);
            if (baseClass.equals("java.lang.String")) {
                value = "\"" + value + "\"";
            } else if (baseClass.equals("java.lang.Character")) {
                value = "'" + value + "'";
            } else if (baseClass.equals("java.lang.Float")) {
                if (!value.endsWith("F") && !value.endsWith("f")) {
                    value = value + "F";
                }
            } else if (baseClass.equals("java.lang.Long")) {
                if (!value.endsWith("L") && !value.endsWith("l")) {
                    value = value + "L";
                }
            } else if (baseClass.equals("javax.xml.namespace.QName")) {
                value = org.apache.axis.wsdl.symbolTable.Utils.getQNameFromPrefixedName(this.type.getNode(), value).toString();
                value = "javax.xml.namespace.QName.valueOf(\"" + value + "\")";
            } else if (baseClass.equals(baseType)) {
                value = "new " + baseClass + "(\"" + value + "\")";
            }
            values.add(value);
        }
        Vector ids = JavaEnumTypeWriter.getEnumValueIds(this.elements);
        pw.println("    private " + baseType + " _value_;");
        pw.println("    private static java.util.HashMap _table_ = new java.util.HashMap();");
        pw.println("");
        pw.println("    // " + Messages.getMessage("ctor00"));
        pw.println("    protected " + javaName + "(" + baseType + " value) {");
        pw.println("        _value_ = value;");
        if (baseClass.equals("java.lang.String") || baseClass.equals(baseType)) {
            pw.println("        _table_.put(_value_,this);");
        } else {
            pw.println("        _table_.put(new " + baseClass + "(_value_),this);");
        }
        pw.println("    }");
        pw.println("");
        for (i = 0; i < ids.size(); ++i) {
            if (baseType.equals("org.apache.axis.types.URI")) {
                pw.println("    public static final " + baseType + " _" + ids.get(i) + ";");
                pw.println("    static {");
                pw.println("    \ttry {");
                pw.println("            _" + ids.get(i) + " = " + values.get(i) + ";");
                pw.println("        }");
                pw.println("        catch (org.apache.axis.types.URI.MalformedURIException mue) {");
                pw.println("            throw new java.lang.RuntimeException(mue.toString());");
                pw.println("        }");
                pw.println("    }");
                pw.println("");
                continue;
            }
            pw.println("    public static final " + baseType + " _" + ids.get(i) + " = " + values.get(i) + ";");
        }
        for (i = 0; i < ids.size(); ++i) {
            pw.println("    public static final " + javaName + " " + ids.get(i) + " = new " + javaName + "(_" + ids.get(i) + ");");
        }
        pw.println("    public " + baseType + " getValue() { return _value_;}");
        pw.println("    public static " + javaName + " fromValue(" + baseType + " value)");
        pw.println("          throws java.lang.IllegalArgumentException {");
        pw.println("        " + javaName + " enumeration = (" + javaName + ")");
        if (baseClass.equals("java.lang.String") || baseClass.equals(baseType)) {
            pw.println("            _table_.get(value);");
        } else {
            pw.println("            _table_.get(new " + baseClass + "(value));");
        }
        pw.println("        if (enumeration==null) throw new java.lang.IllegalArgumentException();");
        pw.println("        return enumeration;");
        pw.println("    }");
        pw.println("    public static " + javaName + " fromString(java.lang.String value)");
        pw.println("          throws java.lang.IllegalArgumentException {");
        if (baseClass.equals("java.lang.String")) {
            pw.println("        return fromValue(value);");
        } else if (baseClass.equals("javax.xml.namespace.QName")) {
            pw.println("        try {");
            pw.println("            return fromValue(javax.xml.namespace.QName.valueOf(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalArgumentException();");
            pw.println("        }");
        } else if (baseClass.equals(baseType)) {
            pw.println("        try {");
            pw.println("            return fromValue(new " + baseClass + "(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalArgumentException();");
            pw.println("        }");
        } else if (baseClass.equals("java.lang.Character")) {
            pw.println("        if (value != null && value.length() == 1);");
            pw.println("            return fromValue(value.charAt(0));");
            pw.println("        throw new java.lang.IllegalArgumentException();");
        } else if (baseClass.equals("java.lang.Integer")) {
            pw.println("        try {");
            pw.println("            return fromValue(java.lang.Integer.parseInt(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalArgumentException();");
            pw.println("        }");
        } else {
            String parse = "parse" + baseClass.substring(baseClass.lastIndexOf(".") + 1);
            pw.println("        try {");
            pw.println("            return fromValue(" + baseClass + "." + parse + "(value));");
            pw.println("        } catch (Exception e) {");
            pw.println("            throw new java.lang.IllegalArgumentException();");
            pw.println("        }");
        }
        pw.println("    }");
        pw.println("    public boolean equals(java.lang.Object obj) {return (obj == this);}");
        pw.println("    public int hashCode() { return toString().hashCode();}");
        if (baseClass.equals("java.lang.String")) {
            pw.println("    public java.lang.String toString() { return _value_;}");
        } else if (baseClass.equals(baseType)) {
            pw.println("    public java.lang.String toString() { return _value_.toString();}");
        } else {
            pw.println("    public java.lang.String toString() { return java.lang.String.valueOf(_value_);}");
        }
        pw.println("    public java.lang.Object readResolve() throws java.io.ObjectStreamException { return fromValue(_value_);}");
        pw.println("    public static org.apache.axis.encoding.Serializer getSerializer(");
        pw.println("           java.lang.String mechType, ");
        pw.println("           java.lang.Class _javaType,  ");
        pw.println("           javax.xml.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new org.apache.axis.encoding.ser.EnumSerializer(");
        pw.println("            _javaType, _xmlType);");
        pw.println("    }");
        pw.println("    public static org.apache.axis.encoding.Deserializer getDeserializer(");
        pw.println("           java.lang.String mechType, ");
        pw.println("           java.lang.Class _javaType,  ");
        pw.println("           javax.xml.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new org.apache.axis.encoding.ser.EnumDeserializer(");
        pw.println("            _javaType, _xmlType);");
        pw.println("    }");
        pw.println("    // " + Messages.getMessage("typeMeta"));
        pw.println("    private static org.apache.axis.description.TypeDesc typeDesc =");
        pw.println("        new org.apache.axis.description.TypeDesc(" + Utils.getJavaLocalName(this.type.getName()) + ".class);");
        pw.println();
        pw.println("    static {");
        pw.println("        typeDesc.setXmlType(" + Utils.getNewQName(this.type.getQName()) + ");");
        pw.println("    }");
        pw.println("    /**");
        pw.println("     * " + Messages.getMessage("returnTypeMeta"));
        pw.println("     */");
        pw.println("    public static org.apache.axis.description.TypeDesc getTypeDesc() {");
        pw.println("        return typeDesc;");
        pw.println("    }");
        pw.println();
    }

    public static Vector getEnumValueIds(Vector bv) {
        boolean validJava = true;
        for (int i = 1; i < bv.size() && validJava; ++i) {
            String value = (String)bv.get(i);
            if (JavaUtils.isJavaId(value)) continue;
            validJava = false;
        }
        Vector<String> ids = new Vector<String>();
        for (int i = 1; i < bv.size(); ++i) {
            if (!validJava) {
                ids.add("value" + i);
                continue;
            }
            ids.add((String)bv.get(i));
        }
        return ids;
    }

    public void generate() throws IOException {
        String fqcn = this.getPackage() + "." + this.getClassName();
        if (this.emitter.isDeploy()) {
            if (!this.emitter.doesExist(fqcn)) {
                super.generate();
            }
        } else {
            super.generate();
        }
    }
}

