/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 *  javax.wsdl.BindingOperation
 *  javax.wsdl.Operation
 *  javax.wsdl.OperationType
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;
import javax.wsdl.Binding;
import javax.wsdl.BindingOperation;
import javax.wsdl.Operation;
import javax.wsdl.OperationType;
import javax.xml.rpc.holders.BooleanHolder;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameter;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaImplWriter
extends JavaClassWriter {
    protected Binding binding;
    protected SymbolTable symbolTable;
    protected BindingEntry bEntry;

    protected JavaImplWriter(Emitter emitter, BindingEntry bEntry, SymbolTable symbolTable) {
        super(emitter, emitter.getImplementationClassName() == null ? bEntry.getName() + "Impl" : emitter.getImplementationClassName(), "templateImpl");
        this.binding = bEntry.getBinding();
        this.symbolTable = symbolTable;
        this.bEntry = bEntry;
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        List operations = this.binding.getBindingOperations();
        for (int i = 0; i < operations.size(); ++i) {
            BindingOperation operation = (BindingOperation)operations.get(i);
            Operation ptOperation = operation.getOperation();
            OperationType type = ptOperation.getStyle();
            Parameters parameters = this.bEntry.getParameters(operation.getOperation());
            if (OperationType.NOTIFICATION.equals(type) || OperationType.SOLICIT_RESPONSE.equals(type)) {
                pw.println(parameters.signature);
                pw.println();
                continue;
            }
            this.writeOperation(pw, parameters);
        }
    }

    protected String getImplementsText() {
        String portTypeName = (String)this.bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME);
        String implementsText = "implements " + portTypeName;
        return implementsText;
    }

    protected void writeOperation(PrintWriter pw, Parameters parms) throws IOException {
        pw.println(parms.signature + " {");
        Iterator iparam = parms.list.iterator();
        while (iparam.hasNext()) {
            Parameter param = (Parameter)iparam.next();
            if (param.getMode() != 2) continue;
            BooleanHolder bThrow = new BooleanHolder(false);
            String constructorString = Utils.getConstructorForParam(param, this.symbolTable, bThrow);
            if (bThrow.value) {
                pw.println("        try {");
            }
            pw.println("        " + Utils.xmlNameToJava(param.getName()) + ".value = " + constructorString + ";");
            if (!bThrow.value) continue;
            pw.println("        } catch (Exception e) {");
            pw.println("        }");
        }
        Parameter returnParam = parms.returnParam;
        if (returnParam != null) {
            TypeEntry returnType = returnParam.getType();
            pw.print("        return ");
            if (!returnParam.isOmittable() && Utils.isPrimitiveType(returnType)) {
                String returnString = returnType.getName();
                if ("boolean".equals(returnString)) {
                    pw.println("false;");
                } else if ("byte".equals(returnString)) {
                    pw.println("(byte)-3;");
                } else if ("short".equals(returnString)) {
                    pw.println("(short)-3;");
                } else {
                    pw.println("-3;");
                }
            } else {
                pw.println("null;");
            }
        }
        pw.println("    }");
        pw.println();
    }
}

