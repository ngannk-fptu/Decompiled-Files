/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.Expression;
import org.apache.xalan.xsltc.compiler.SymbolTable;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.Type;
import org.apache.xalan.xsltc.compiler.util.TypeCheckError;

final class BooleanExpr
extends Expression {
    private boolean _value;

    public BooleanExpr(boolean value) {
        this._value = value;
    }

    @Override
    public Type typeCheck(SymbolTable stable) throws TypeCheckError {
        this._type = Type.Boolean;
        return this._type;
    }

    @Override
    public String toString() {
        return this._value ? "true()" : "false()";
    }

    public boolean getValue() {
        return this._value;
    }

    @Override
    public boolean contextDependent() {
        return false;
    }

    @Override
    public void translate(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new PUSH(cpg, this._value));
    }

    @Override
    public void translateDesynthesized(ClassGenerator classGen, MethodGenerator methodGen) {
        InstructionList il = methodGen.getInstructionList();
        if (this._value) {
            il.append(NOP);
        } else {
            this._falseList.add(il.append(new GOTO(null)));
        }
    }
}

