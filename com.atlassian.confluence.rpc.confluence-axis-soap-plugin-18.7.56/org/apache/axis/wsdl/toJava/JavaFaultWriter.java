/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Message
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;
import javax.wsdl.Message;
import org.apache.axis.constants.Use;
import org.apache.axis.wsdl.symbolTable.FaultInfo;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaFaultWriter
extends JavaClassWriter {
    private Message faultMessage;
    private SymbolTable symbolTable;
    private boolean literal;
    private String faultName;

    protected JavaFaultWriter(Emitter emitter, SymbolTable symbolTable, FaultInfo faultInfo) {
        super(emitter, Utils.getFullExceptionName(faultInfo.getMessage(), symbolTable), "fault");
        this.literal = faultInfo.getUse().equals(Use.LITERAL);
        this.faultMessage = faultInfo.getMessage();
        this.symbolTable = symbolTable;
        this.faultName = faultInfo.getName();
    }

    protected String getExtendsText() {
        return "extends org.apache.axis.AxisFault ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        String variable;
        String variable2;
        String type;
        Parameter param;
        int i;
        Vector params = new Vector();
        this.symbolTable.getParametersFromParts(params, this.faultMessage.getOrderedParts(null), this.literal, this.faultName, null);
        for (i = 0; i < params.size(); ++i) {
            param = (Parameter)params.get(i);
            type = param.getType().getName();
            variable2 = Utils.xmlNameToJava(param.getName());
            pw.println("    public " + type + " " + variable2 + ";");
            pw.println("    public " + type + " get" + Utils.capitalizeFirstChar(variable2) + "() {");
            pw.println("        return this." + variable2 + ";");
            pw.println("    }");
        }
        pw.println();
        pw.println("    public " + this.className + "() {");
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "(java.lang.Exception target) {");
        pw.println("        super(target);");
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "(java.lang.String message, java.lang.Throwable t) {");
        pw.println("        super(message, t);");
        pw.println("    }");
        pw.println();
        if (params.size() > 0) {
            pw.print("      public " + this.className + "(");
            for (i = 0; i < params.size(); ++i) {
                if (i != 0) {
                    pw.print(", ");
                }
                param = (Parameter)params.get(i);
                type = param.getType().getName();
                variable2 = Utils.xmlNameToJava(param.getName());
                pw.print(type + " " + variable2);
            }
            pw.println(") {");
            for (i = 0; i < params.size(); ++i) {
                param = (Parameter)params.get(i);
                variable = Utils.xmlNameToJava(param.getName());
                pw.println("        this." + variable + " = " + variable + ";");
            }
            pw.println("    }");
        }
        pw.println();
        pw.println("    /**");
        pw.println("     * Writes the exception data to the faultDetails");
        pw.println("     */");
        pw.println("    public void writeDetails(javax.xml.namespace.QName qname, org.apache.axis.encoding.SerializationContext context) throws java.io.IOException {");
        for (i = 0; i < params.size(); ++i) {
            param = (Parameter)params.get(i);
            variable = Utils.xmlNameToJava(param.getName());
            pw.println("        context.serialize(qname, null, " + Utils.wrapPrimitiveType(param.getType(), variable) + ");");
        }
        pw.println("    }");
    }
}

