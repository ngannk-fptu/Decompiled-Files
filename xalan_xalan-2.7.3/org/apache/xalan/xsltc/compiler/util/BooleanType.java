/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.BranchHandle;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGE;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IFLE;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.PUSH;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;

public final class BooleanType
extends Type {
    protected BooleanType() {
    }

    @Override
    public String toString() {
        return "boolean";
    }

    @Override
    public boolean identicalTo(Type other) {
        return this == other;
    }

    @Override
    public String toSignature() {
        return "Z";
    }

    @Override
    public boolean isSimple() {
        return true;
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return org.apache.bcel.generic.Type.BOOLEAN;
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        } else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        BranchHandle falsec = il.append(new IFEQ(null));
        il.append(new PUSH(cpg, "true"));
        BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(new PUSH(cpg, "false")));
        truec.setTarget(il.append(NOP));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
        methodGen.getInstructionList().append(I2D);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("java.lang.Boolean")));
        il.append(DUP_X1);
        il.append(SWAP);
        il.append(new INVOKESPECIAL(cpg.addMethodref("java.lang.Boolean", "<init>", "(Z)V")));
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        if (clazz == java.lang.Boolean.TYPE) {
            methodGen.getInstructionList().append(NOP);
        } else if (clazz.isAssignableFrom(Boolean.class)) {
            this.translateTo(classGen, methodGen, Type.Reference);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }

    @Override
    public void translateFrom(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        this.translateTo(classGen, methodGen, clazz);
    }

    @Override
    public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
        this.translateTo(classGen, methodGen, Type.Reference);
    }

    @Override
    public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new CHECKCAST(cpg.addClass("java.lang.Boolean")));
        il.append(new INVOKEVIRTUAL(cpg.addMethodref("java.lang.Boolean", "booleanValue", "()Z")));
    }

    @Override
    public Instruction LOAD(int slot) {
        return new ILOAD(slot);
    }

    @Override
    public Instruction STORE(int slot) {
        return new ISTORE(slot);
    }

    @Override
    public BranchInstruction GT(boolean tozero) {
        return tozero ? new IFGT(null) : new IF_ICMPGT(null);
    }

    @Override
    public BranchInstruction GE(boolean tozero) {
        return tozero ? new IFGE(null) : new IF_ICMPGE(null);
    }

    @Override
    public BranchInstruction LT(boolean tozero) {
        return tozero ? new IFLT(null) : new IF_ICMPLT(null);
    }

    @Override
    public BranchInstruction LE(boolean tozero) {
        return tozero ? new IFLE(null) : new IF_ICMPLE(null);
    }
}

