/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionList;
import org.apache.xalan.xsltc.compiler.Instruction;
import org.apache.xalan.xsltc.compiler.Parser;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class Fallback
extends Instruction {
    private boolean _active = false;

    Fallback() {
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        if (this._active) {
            return this.typeCheckContents(stable);
        }
        return Type.Void;
    }

    public void activate() {
        this._active = true;
    }

    public String toString() {
        return "fallback";
    }

    @Override
    public void parseContents(Parser parser) {
        if (this._active) {
            this.parseChildren(parser);
        }
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        if (this._active) {
            this.translateContents(classGen, methodGen);
        }
    }
}

