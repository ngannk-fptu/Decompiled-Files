/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.AttributeValue;
import org.apache.xalan.xsltc.compiler.CastExpr;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;
import org.apache.xalan.xsltc.compiler.util.Util;

final class TransletOutput
extends Instruction {
    private Expression _filename;
    private boolean _append;

    TransletOutput() {
    }

    @Override
    public void display(int indent) {
        this.indent(indent);
        Util.println("TransletOutput: " + this._filename);
    }

    @Override
    public void parseContents(Parser parser) {
        String filename = this.getAttribute("file");
        String append = this.getAttribute("append");
        if (filename == null || filename.equals("")) {
            this.reportError(this, parser, "REQUIRED_ATTR_ERR", "file");
        }
        this._filename = AttributeValue.create(this, filename, parser);
        this._append = append != null && (append.toLowerCase().equals("yes") || append.toLowerCase().equals("true"));
        this.parseChildren(parser);
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        Type type = this._filename.typeCheck(stable);
        if (!(type instanceof StringType)) {
            this._filename = new CastExpr(this._filename, Type.String);
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        boolean isSecureProcessing = classGen.getParser().getXSLTC().isSecureProcessing();
        if (isSecureProcessing) {
            int index = cpg.addMethodref("org.apache.xalan.xsltc.runtime.BasisLibrary", "unallowed_extension_elementF", "(Ljava/lang/String;)V");
            il.append(new PUSH(cpg, "redirect"));
            il.append(new INVOKESTATIC(index));
            return;
        }
        il.append(methodGen.loadHandler());
        int open = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "openOutputHandler", "(Ljava/lang/String;Z)" + TRANSLET_OUTPUT_SIG);
        int close = cpg.addMethodref("org.apache.xalan.xsltc.runtime.AbstractTranslet", "closeOutputHandler", "(" + TRANSLET_OUTPUT_SIG + ")V");
        il.append(classGen.loadTranslet());
        this._filename.translate(classGen, methodGen);
        il.append(new PUSH(cpg, this._append));
        il.append(new INVOKEVIRTUAL(open));
        il.append(methodGen.storeHandler());
        this.translateContents(classGen, methodGen);
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadHandler());
        il.append(new INVOKEVIRTUAL(close));
        il.append(methodGen.storeHandler());
    }
}

