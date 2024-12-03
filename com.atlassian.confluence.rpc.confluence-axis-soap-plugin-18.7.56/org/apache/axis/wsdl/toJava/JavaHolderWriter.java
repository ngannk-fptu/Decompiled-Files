/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.wsdl.toJava;

import java.io.IOException;
import java.io.PrintWriter;
import org.apache.axis.wsdl.symbolTable.TypeEntry;
import org.apache.axis.wsdl.toJava.Emitter;
import org.apache.axis.wsdl.toJava.JavaClassWriter;
import org.apache.axis.wsdl.toJava.Utils;

public class JavaHolderWriter
extends JavaClassWriter {
    private TypeEntry type;

    protected JavaHolderWriter(Emitter emitter, TypeEntry type) {
        super(emitter, Utils.holder(type, emitter), "holder");
        this.type = type;
    }

    protected String getClassModifiers() {
        return super.getClassModifiers() + "final ";
    }

    protected String getImplementsText() {
        return "implements javax.xml.rpc.holders.Holder ";
    }

    protected void writeFileBody(PrintWriter pw) throws IOException {
        String holderType = this.type.getName();
        pw.println("    public " + holderType + " value;");
        pw.println();
        pw.println("    public " + this.className + "() {");
        pw.println("    }");
        pw.println();
        pw.println("    public " + this.className + "(" + holderType + " value) {");
        pw.println("        this.value = value;");
        pw.println("    }");
        pw.println();
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

