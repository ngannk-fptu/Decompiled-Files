/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.util.Collections;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.symbolTable.SchemaUtils;
import org.apache.axis.wsdl.symbolTable.SymTabEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.Type;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBeanFaultWriter;
import org.apache.axis.wsdl.toJava.JavaBeanHelperWriter;
import org.apache.axis.wsdl.toJava.JavaBeanWriter;
import org.apache.axis.wsdl.toJava.JavaEnumTypeWriter;
import org.apache.axis.wsdl.toJava.JavaGeneratorFactory;
import org.apache.axis.wsdl.toJava.JavaHolderWriter;
import org.apache.axis.wsdl.toJava.JavaWriter;
import org.apache.axis.wsdl.toJava.Utils;
import org.w3c.dom.Node;

public class JavaTypeWriter
implements Generator {
    public static final String HOLDER_IS_NEEDED = "Holder is needed";
    private Generator typeWriter = null;
    private Generator holderWriter = null;

    public JavaTypeWriter(Emitter emitter, TypeEntry type, SymbolTable symbolTable) {
        if (type.isReferenced() && !type.isOnlyLiteralReferenced()) {
            Node node = type.getNode();
            boolean isSimpleList = SchemaUtils.isListWithItemType(node);
            if (!type.getName().endsWith("[]") && !isSimpleList) {
                Vector v = Utils.getEnumerationBaseAndValues(node, symbolTable);
                if (v != null) {
                    this.typeWriter = this.getEnumTypeWriter(emitter, type, v);
                } else {
                    QName baseQName;
                    TypeEntry base = SchemaUtils.getComplexElementExtensionBase(node, symbolTable);
                    if (base == null) {
                        base = SchemaUtils.getComplexElementRestrictionBase(node, symbolTable);
                    }
                    if (base == null && (baseQName = SchemaUtils.getSimpleTypeBase(node)) != null) {
                        base = symbolTable.getType(baseQName);
                    }
                    this.typeWriter = this.getBeanWriter(emitter, type, base);
                }
            }
            if (this.holderIsNeeded(type)) {
                this.holderWriter = this.getHolderWriter(emitter, type);
            }
            if (this.typeWriter != null && type instanceof Type) {
                ((Type)type).setGenerated(true);
            }
        }
    }

    public void generate() throws IOException {
        if (this.typeWriter != null) {
            this.typeWriter.generate();
        }
        if (this.holderWriter != null) {
            this.holderWriter.generate();
        }
    }

    private boolean holderIsNeeded(SymTabEntry entry) {
        Boolean holderIsNeeded = (Boolean)entry.getDynamicVar(HOLDER_IS_NEEDED);
        return holderIsNeeded != null && holderIsNeeded != false;
    }

    protected JavaWriter getEnumTypeWriter(Emitter emitter, TypeEntry type, Vector v) {
        return new JavaEnumTypeWriter(emitter, type, v);
    }

    protected JavaWriter getBeanWriter(Emitter emitter, TypeEntry type, TypeEntry base) {
        Vector elements = type.getContainedElements();
        Vector attributes = type.getContainedAttributes();
        Boolean isComplexFault = (Boolean)type.getDynamicVar(JavaGeneratorFactory.COMPLEX_TYPE_FAULT);
        if (isComplexFault != null && isComplexFault.booleanValue()) {
            return new JavaBeanFaultWriter(emitter, type, elements, base, attributes, this.getBeanHelperWriter(emitter, type, elements, base, attributes, true));
        }
        return new JavaBeanWriter(emitter, type, elements, base, attributes, this.getBeanHelperWriter(emitter, type, elements, base, attributes, false));
    }

    protected JavaWriter getBeanHelperWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry base, Vector attributes, boolean forException) {
        return new JavaBeanHelperWriter(emitter, type, elements, base, attributes, forException ? JavaBeanFaultWriter.RESERVED_PROPERTY_NAMES : Collections.EMPTY_SET);
    }

    protected Generator getHolderWriter(Emitter emitter, TypeEntry type) {
        return new JavaHolderWriter(emitter, type);
    }
}

