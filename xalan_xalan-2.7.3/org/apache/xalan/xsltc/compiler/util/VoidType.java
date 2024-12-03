/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;

public final class VoidType
extends Type {
    protected VoidType() {
    }

    @Override
    public String toString() {
        return "void";
    }

    @Override
    public boolean identicalTo(Type other) {
        return this == other;
    }

    @Override
    public String toSignature() {
        return "V";
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return null;
    }

    @Override
    public Instruction POP() {
        return NOP;
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        InstructionList il = methodGen.getInstructionList();
        il.append(new PUSH(classGen.getConstantPool(), ""));
    }

    @Override
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        if (!clazz.getName().equals("void")) {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }
}

