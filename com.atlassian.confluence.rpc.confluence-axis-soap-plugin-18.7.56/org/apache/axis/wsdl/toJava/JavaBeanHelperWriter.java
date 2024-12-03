/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.utils.JavaUtils;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.symbolTable.CollectionTE;
import org.apache.axis.wsdl.symbolTable.ContainedAttribute;
import org.apache.axis.wsdl.symbolTable.ElementDecl;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaBeanHelperWriter
extends JavaClassWriter {
    protected TypeEntry type;
    protected Vector elements;
    protected Vector attributes;
    protected TypeEntry extendType;
    protected PrintWriter wrapperPW = null;
    protected Vector elementMetaData = null;
    protected boolean canSearchParents;
    protected Set reservedPropNames;
    static /* synthetic */ Class class$org$apache$axis$wsdl$symbolTable$DefinedType;

    protected JavaBeanHelperWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry extendType, Vector attributes, Set reservedPropNames) {
        super(emitter, type.getName() + "_Helper", "helper");
        this.type = type;
        this.elements = elements;
        this.attributes = attributes;
        this.extendType = extendType;
        this.reservedPropNames = reservedPropNames;
        this.canSearchParents = null == extendType || null == SchemaUtils.getComplexElementRestrictionBase(type.getNode(), emitter.getSymbolTable());
    }

    protected void setPrintWriter(PrintWriter pw) {
        this.wrapperPW = pw;
    }

    protected PrintWriter getPrintWriter(String filename) throws IOException {
        return this.wrapperPW == null ? super.getPrintWriter(filename) : this.wrapperPW;
    }

    protected void registerFile(String file) {
        if (this.wrapperPW == null) {
            super.registerFile(file);
        }
    }

    protected String verboseMessage(String file) {
        if (this.wrapperPW == null) {
            return super.verboseMessage(file);
        }
        return null;
    }

    protected void writeFileHeader(PrintWriter pw) throws IOException {
        if (this.wrapperPW == null) {
            super.writeFileHeader(pw);
        }
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        this.writeMetaData(pw);
        this.writeSerializer(pw);
        this.writeDeserializer(pw);
    }

    protected void writeFileFooter(PrintWriter pw) throws IOException {
        if (this.wrapperPW == null) {
            super.writeFileFooter(pw);
        }
    }

    protected void closePrintWriter(PrintWriter pw) {
        if (this.wrapperPW == null) {
            pw.close();
        }
    }

    protected void writeMetaData(PrintWriter pw) throws IOException {
        if (this.elements != null) {
            for (int i = 0; i < this.elements.size(); ++i) {
                ElementDecl elem = (ElementDecl)this.elements.get(i);
                if (this.elementMetaData == null) {
                    this.elementMetaData = new Vector();
                }
                this.elementMetaData.add(elem);
            }
        }
        pw.println("    // " + Messages.getMessage("typeMeta"));
        pw.println("    private static org.apache.axis.description.TypeDesc typeDesc =");
        pw.println("        new org.apache.axis.description.TypeDesc(" + Utils.getJavaLocalName(this.type.getName()) + ".class, " + (this.canSearchParents ? "true" : "false") + ");");
        pw.println();
        pw.println("    static {");
        pw.println("        typeDesc.setXmlType(" + Utils.getNewQName(this.type.getQName()) + ");");
        if (this.attributes != null || this.elementMetaData != null) {
            if (this.attributes != null) {
                boolean wroteAttrDecl = false;
                for (int i = 0; i < this.attributes.size(); ++i) {
                    ContainedAttribute attr = (ContainedAttribute)this.attributes.get(i);
                    TypeEntry te = attr.getType();
                    QName attrName = attr.getQName();
                    String fieldName = this.getAsFieldName(attr.getName());
                    QName attrXmlType = te.getQName();
                    pw.print("        ");
                    if (!wroteAttrDecl) {
                        pw.print("org.apache.axis.description.AttributeDesc ");
                        wroteAttrDecl = true;
                    }
                    pw.println("attrField = new org.apache.axis.description.AttributeDesc();");
                    pw.println("        attrField.setFieldName(\"" + fieldName + "\");");
                    pw.println("        attrField.setXmlName(" + Utils.getNewQNameWithLastLocalPart(attrName) + ");");
                    if (attrXmlType != null) {
                        pw.println("        attrField.setXmlType(" + Utils.getNewQName(attrXmlType) + ");");
                    }
                    pw.println("        typeDesc.addFieldDesc(attrField);");
                }
            }
            if (this.elementMetaData != null) {
                boolean wroteElemDecl = false;
                for (int i = 0; i < this.elementMetaData.size(); ++i) {
                    QName itemQName;
                    ElementDecl elem = (ElementDecl)this.elementMetaData.elementAt(i);
                    if (elem.getAnyElement()) continue;
                    String fieldName = this.getAsFieldName(elem.getName());
                    QName xmlName = elem.getQName();
                    TypeEntry elemType = elem.getType();
                    QName xmlType = null;
                    if (elemType.getDimensions().length() > 1 && elemType.getClass() == (class$org$apache$axis$wsdl$symbolTable$DefinedType == null ? JavaBeanHelperWriter.class$("org.apache.axis.wsdl.symbolTable.DefinedType") : class$org$apache$axis$wsdl$symbolTable$DefinedType)) {
                        elemType = elemType.getRefType();
                    } else {
                        while (elemType instanceof CollectionTE) {
                            elemType = elemType.getRefType();
                        }
                    }
                    xmlType = elemType.getQName();
                    pw.print("        ");
                    if (!wroteElemDecl) {
                        pw.print("org.apache.axis.description.ElementDesc ");
                        wroteElemDecl = true;
                    }
                    pw.println("elemField = new org.apache.axis.description.ElementDesc();");
                    pw.println("        elemField.setFieldName(\"" + fieldName + "\");");
                    pw.println("        elemField.setXmlName(" + Utils.getNewQNameWithLastLocalPart(xmlName) + ");");
                    if (xmlType != null) {
                        pw.println("        elemField.setXmlType(" + Utils.getNewQName(xmlType) + ");");
                    }
                    if (elem.getMinOccursIs0()) {
                        pw.println("        elemField.setMinOccurs(0);");
                    }
                    if (elem.getNillable()) {
                        pw.println("        elemField.setNillable(true);");
                    } else {
                        pw.println("        elemField.setNillable(false);");
                    }
                    if (elem.getMaxOccursIsUnbounded()) {
                        pw.println("        elemField.setMaxOccursUnbounded(true);");
                    }
                    if ((itemQName = elem.getType().getItemQName()) != null) {
                        pw.println("        elemField.setItemQName(" + Utils.getNewQName(itemQName) + ");");
                    }
                    pw.println("        typeDesc.addFieldDesc(elemField);");
                }
            }
        }
        pw.println("    }");
        pw.println();
        pw.println("    /**");
        pw.println("     * " + Messages.getMessage("returnTypeMeta"));
        pw.println("     */");
        pw.println("    public static org.apache.axis.description.TypeDesc getTypeDesc() {");
        pw.println("        return typeDesc;");
        pw.println("    }");
        pw.println();
    }

    private String getAsFieldName(String fieldName) {
        if (fieldName.length() > 1 && Character.isUpperCase(fieldName.charAt(1))) {
            fieldName = Utils.capitalizeFirstChar(fieldName);
        }
        return JavaUtils.getUniqueValue(this.reservedPropNames, fieldName);
    }

    protected void writeSerializer(PrintWriter pw) throws IOException {
        String typeDesc = "typeDesc";
        String ser = " org.apache.axis.encoding.ser.BeanSerializer";
        if (this.type.isSimpleType()) {
            ser = " org.apache.axis.encoding.ser.SimpleSerializer";
        }
        pw.println("    /**");
        pw.println("     * Get Custom Serializer");
        pw.println("     */");
        pw.println("    public static org.apache.axis.encoding.Serializer getSerializer(");
        pw.println("           java.lang.String mechType, ");
        pw.println("           java.lang.Class _javaType,  ");
        pw.println("           javax.xml.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new " + ser + "(");
        pw.println("            _javaType, _xmlType, " + typeDesc + ");");
        pw.println("    }");
        pw.println();
    }

    protected void writeDeserializer(PrintWriter pw) throws IOException {
        String typeDesc = "typeDesc";
        String dser = " org.apache.axis.encoding.ser.BeanDeserializer";
        if (this.type.isSimpleType()) {
            dser = " org.apache.axis.encoding.ser.SimpleDeserializer";
        }
        pw.println("    /**");
        pw.println("     * Get Custom Deserializer");
        pw.println("     */");
        pw.println("    public static org.apache.axis.encoding.Deserializer getDeserializer(");
        pw.println("           java.lang.String mechType, ");
        pw.println("           java.lang.Class _javaType,  ");
        pw.println("           javax.xml.namespace.QName _xmlType) {");
        pw.println("        return ");
        pw.println("          new " + dser + "(");
        pw.println("            _javaType, _xmlType, " + typeDesc + ");");
        pw.println("    }");
        pw.println();
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}

