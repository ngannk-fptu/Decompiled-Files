/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Binding
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import javax.wsdl.Binding;
import org.apache.axis.utils.Messages;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.symbolTable.BindingEntry;
import org.apache.axis.wsdl.symbolTable.PortTypeEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaImplWriter;
import org.apache.axis.wsdl.toJava.JavaInterfaceWriter;
import org.apache.axis.wsdl.toJava.JavaSkelWriter;
import org.apache.axis.wsdl.toJava.JavaStubWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaBindingWriter
implements Generator {
    protected Generator stubWriter = null;
    protected Generator skelWriter = null;
    protected Generator implWriter = null;
    protected Generator interfaceWriter = null;
    protected Emitter emitter;
    protected Binding binding;
    protected SymbolTable symbolTable;
    public static String INTERFACE_NAME = "interface name";

    public JavaBindingWriter(Emitter emitter, Binding binding, SymbolTable symbolTable) {
        this.emitter = emitter;
        this.binding = binding;
        this.symbolTable = symbolTable;
    }

    protected Generator getJavaInterfaceWriter(Emitter emitter, PortTypeEntry ptEntry, BindingEntry bEntry, SymbolTable st) {
        return new JavaInterfaceWriter(emitter, ptEntry, bEntry, st);
    }

    protected Generator getJavaStubWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st) {
        return new JavaStubWriter(emitter, bEntry, st);
    }

    protected Generator getJavaSkelWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st) {
        return new JavaSkelWriter(emitter, bEntry, st);
    }

    protected Generator getJavaImplWriter(Emitter emitter, BindingEntry bEntry, SymbolTable st) {
        return new JavaImplWriter(emitter, bEntry, st);
    }

    public void generate() throws IOException {
        this.setGenerators();
        this.postSetGenerators();
        if (this.interfaceWriter != null) {
            this.interfaceWriter.generate();
        }
        if (this.stubWriter != null) {
            this.stubWriter.generate();
        }
        if (this.skelWriter != null) {
            this.skelWriter.generate();
        }
        if (this.implWriter != null) {
            this.implWriter.generate();
        }
    }

    protected void setGenerators() {
        BindingEntry bEntry = this.symbolTable.getBindingEntry(this.binding.getQName());
        PortTypeEntry ptEntry = this.symbolTable.getPortTypeEntry(this.binding.getPortType().getQName());
        if (ptEntry.isReferenced()) {
            this.interfaceWriter = this.getJavaInterfaceWriter(this.emitter, ptEntry, bEntry, this.symbolTable);
        }
        if (bEntry.isReferenced()) {
            this.stubWriter = this.getJavaStubWriter(this.emitter, bEntry, this.symbolTable);
            if (this.emitter.isServerSide()) {
                String fileName;
                if (this.emitter.isSkeletonWanted()) {
                    this.skelWriter = this.getJavaSkelWriter(this.emitter, bEntry, this.symbolTable);
                }
                fileName = (fileName = this.emitter.getImplementationClassName()) == null ? Utils.getJavaLocalName(bEntry.getName()) + "Impl.java" : Utils.getJavaLocalName(fileName) + ".java";
                try {
                    if (Utils.fileExists(fileName, this.binding.getQName().getNamespaceURI(), this.emitter.getNamespaces())) {
                        if (!this.emitter.isQuiet()) {
                            System.out.println(Messages.getMessage("wontOverwrite", fileName));
                        }
                    } else {
                        this.implWriter = this.getJavaImplWriter(this.emitter, bEntry, this.symbolTable);
                    }
                }
                catch (IOException ioe) {
                    System.err.println(Messages.getMessage("fileExistError00", fileName));
                }
            }
        }
    }

    protected void postSetGenerators() {
        if (this.emitter.isDeploy()) {
            this.interfaceWriter = null;
            this.stubWriter = null;
            this.skelWriter = null;
            this.implWriter = null;
        }
    }
}

