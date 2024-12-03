/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBeanWriter;
import org.apache.axis.wsdl.toJava.JavaWriter;

public class JavaBeanFaultWriter
extends JavaBeanWriter {
    public static final Set RESERVED_PROPERTY_NAMES;

    protected JavaBeanFaultWriter(Emitter emitter, TypeEntry type, Vector elements, TypeEntry extendType, Vector attributes, JavaWriter helper) {
        super(emitter, type, elements, extendType, attributes, helper);
        this.enableDefaultConstructor = true;
        this.enableFullConstructor = true;
        this.enableSetters = true;
    }

    protected String getExtendsText() {
        String extendsText = super.getExtendsText();
        if (extendsText.equals("")) {
            extendsText = " extends org.apache.axis.AxisFault ";
        }
        return extendsText;
    }

    protected void writeFileFooter(PrintWriter pw) throws IOException {
        pw.println();
        pw.println("    /**");
        pw.println("     * Writes the exception data to the faultDetails");
        pw.println("     */");
        pw.println("    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {");
        pw.println("        context.serialize(qname, null, this);");
        pw.println("    }");
        super.writeFileFooter(pw);
    }

    static {
        HashSet<String> temp = new HashSet<String>();
        temp.add("cause");
        temp.add("message");
        temp.add("localizedMessage");
        temp.add("stackTrace");
        temp.add("faultActor");
        temp.add("faultCode");
        temp.add("faultDetails");
        temp.add("faultNode");
        temp.add("faultReason");
        temp.add("faultRole");
        temp.add("faultString");
        temp.add("faultSubCodes");
        temp.add("headers");
        RESERVED_PROPERTY_NAMES = Collections.unmodifiableSet(temp);
    }
}

