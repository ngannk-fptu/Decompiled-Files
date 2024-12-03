/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Operation
 *  javax.wsdl.PortType
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import javax.wsdl.Operation;
import javax.wsdl.PortType;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.Parameters;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaBindingWriter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;

public class JavaInterfaceWriter
extends JavaClassWriter {
    protected PortType portType;
    protected BindingEntry bEntry;

    protected JavaInterfaceWriter(Emitter emitter, PortTypeEntry ptEntry, BindingEntry bEntry, SymbolTable symbolTable) {
        super(emitter, (String)bEntry.getDynamicVar(JavaBindingWriter.INTERFACE_NAME), "interface");
        this.portType = ptEntry.getPortType();
        this.bEntry = bEntry;
    }

    public void generate() throws IOException {
        String fqClass = this.getPackage() + "." + this.getClassName();
        if (!this.emitter.getGeneratedFileInfo().getClassNames().contains(fqClass)) {
            super.generate();
        }
    }

    protected String getClassText() {
        return "interface ";
    }

    protected String getExtendsText() {
        return "extends java.rmi.Remote ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        Iterator operations = this.portType.getOperations().iterator();
        while (operations.hasNext()) {
            Operation operation = (Operation)operations.next();
            this.writeOperation(pw, operation);
        }
    }

    protected void writeOperation(PrintWriter pw, Operation operation) throws IOException {
        this.writeComment(pw, operation.getDocumentationElement(), true);
        Parameters parms = this.bEntry.getParameters(operation);
        pw.println(parms.signature + ";");
    }
}

