/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.wsdl.Service
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import javax.wsdl.Service;
import org.apache.axis.wsdl.gen.Generator;
import org.apache.axis.wsdl.symbolTable.ServiceEntry;
import org.apache.axis.wsdl.symbolTable.SymbolTable;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaServiceIfaceWriter;
import org.apache.axis.wsdl.toJava.JavaServiceImplWriter;
import org.apache.axis.wsdl.toJava.JavaTestCaseWriter;

public class JavaServiceWriter
implements Generator {
    protected Generator serviceIfaceWriter = null;
    protected Generator serviceImplWriter = null;
    protected Generator testCaseWriter = null;
    public static final String PORT_NAME = "port name";
    protected Emitter emitter;
    protected Service service;
    protected SymbolTable symbolTable;

    public JavaServiceWriter(Emitter emitter, Service service, SymbolTable symbolTable) {
        this.emitter = emitter;
        this.service = service;
        this.symbolTable = symbolTable;
    }

    protected void setGenerators() {
        ServiceEntry sEntry = this.symbolTable.getServiceEntry(this.service.getQName());
        if (sEntry.isReferenced()) {
            this.serviceIfaceWriter = new JavaServiceIfaceWriter(this.emitter, sEntry, this.symbolTable);
            this.serviceImplWriter = new JavaServiceImplWriter(this.emitter, sEntry, this.symbolTable);
            if (this.emitter.isTestCaseWanted()) {
                this.testCaseWriter = new JavaTestCaseWriter(this.emitter, sEntry, this.symbolTable);
            }
        }
    }

    protected void postSetGenerators() {
        if (this.emitter.isDeploy()) {
            this.serviceIfaceWriter = null;
            this.serviceImplWriter = null;
        }
    }

    public void generate() throws IOException {
        this.setGenerators();
        this.postSetGenerators();
        if (this.serviceIfaceWriter != null) {
            this.serviceIfaceWriter.generate();
        }
        if (this.serviceImplWriter != null) {
            this.serviceImplWriter.generate();
        }
        if (this.testCaseWriter != null) {
            this.testCaseWriter.generate();
        }
    }
}

