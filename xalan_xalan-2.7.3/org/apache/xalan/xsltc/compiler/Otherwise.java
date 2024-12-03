/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class Otherwise
extends Instruction {
    Otherwise() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("Otherwise");
        this.indent(indent + 4);
        this.displayContents(indent + 4);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        Parser parser = this.getParser();
        ErrorMsg err = new ErrorMsg("STRAY_OTHERWISE_ERR", this);
        parser.reportError(3, err);
    }
}

