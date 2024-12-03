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
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.NEW;
import org.apache.xalan.xsltc.compiler.FlowList;
import org.apache.xalan.xsltc.compiler.util.BooleanType;
import org.apache.xalan.xsltc.compiler.util.ClassGenerator;
import org.apache.xalan.xsltc.compiler.util.ErrorMsg;
import org.apache.xalan.xsltc.compiler.util.MethodGenerator;
import org.apache.xalan.xsltc.compiler.util.NumberType;
import org.apache.xalan.xsltc.compiler.util.RealType;
import org.apache.xalan.xsltc.compiler.util.ReferenceType;
import org.apache.xalan.xsltc.compiler.util.StringType;
import org.apache.xalan.xsltc.compiler.util.Type;

public final class IntType
extends NumberType {
    protected IntType() {
    }

    @Override
    public String toString() {
        return "int";
    }

    @Override
    public boolean identicalTo(Type other) {
        return this == other;
    }

    @Override
    public String toSignature() {
        return "I";
    }

    @Override
    public org.apache.bcel.generic.Type toJCType() {
        return org.apache.bcel.generic.Type.INT;
    }

    @Override
    public int distanceTo(Type type) {
        if (type == this) {
            return 0;
        }
        if (type == Type.Real) {
            return 1;
        }
        return Integer.MAX_VALUE;
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Type type) {
        if (type == Type.Real) {
            this.translateTo(classGen, methodGen, (RealType)type);
        } else if (type == Type.String) {
            this.translateTo(classGen, methodGen, (StringType)type);
        } else if (type == Type.Boolean) {
            this.translateTo(classGen, methodGen, (BooleanType)type);
        } else if (type == Type.Reference) {
            this.translateTo(classGen, methodGen, (ReferenceType)type);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)type.toString());
            classGen.getParser().reportError(2, err);
        }
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, RealType type) {
        methodGen.getInstructionList().append(I2D);
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, StringType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new INVOKESTATIC(cpg.addMethodref("java.lang.Integer", "toString", "(I)Ljava/lang/String;")));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        InstructionList il = methodGen.getInstructionList();
        BranchHandle falsec = il.append(new IFEQ(null));
        il.append(ICONST_1);
        BranchHandle truec = il.append(new GOTO(null));
        falsec.setTarget(il.append(ICONST_0));
        truec.setTarget(il.append(NOP));
    }

    @Override
    public FlowList translateToDesynthesized(ClassGenerator classGen, MethodGenerator methodGen, BooleanType type) {
        InstructionList il = methodGen.getInstructionList();
        return new FlowList(il.append(new IFEQ(null)));
    }

    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, ReferenceType type) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new NEW(cpg.addClass("java.lang.Integer")));
        il.append(DUP_X1);
        il.append(SWAP);
        il.append(new INVOKESPECIAL(cpg.addMethodref("java.lang.Integer", "<init>", "(I)V")));
    }

    @Override
    public void translateTo(ClassGenerator classGen, MethodGenerator methodGen, Class clazz) {
        InstructionList il = methodGen.getInstructionList();
        if (clazz == Character.TYPE) {
            il.append(I2C);
        } else if (clazz == Byte.TYPE) {
            il.append(I2B);
        } else if (clazz == Short.TYPE) {
            il.append(I2S);
        } else if (clazz == Integer.TYPE) {
            il.append(NOP);
        } else if (clazz == Long.TYPE) {
            il.append(I2L);
        } else if (clazz == Float.TYPE) {
            il.append(I2F);
        } else if (clazz == Double.TYPE) {
            il.append(I2D);
        } else if (clazz.isAssignableFrom(Double.class)) {
            il.append(I2D);
            Type.Real.translateTo(classGen, methodGen, Type.Reference);
        } else {
            ErrorMsg err = new ErrorMsg("DATA_CONVERSION_ERR", (Object)this.toString(), (Object)clazz.getName());
            classGen.getParser().reportError(2, err);
        }
    }

    @Override
    public void translateBox(ClassGenerator classGen, MethodGenerator methodGen) {
        this.translateTo(classGen, methodGen, Type.Reference);
    }

    @Override
    public void translateUnBox(ClassGenerator classGen, MethodGenerator methodGen) {
        ConstantPoolGen cpg = classGen.getConstantPool();
        InstructionList il = methodGen.getInstructionList();
        il.append(new CHECKCAST(cpg.addClass("java.lang.Integer")));
        int index = cpg.addMethodref("java.lang.Integer", "intValue", "()I");
        il.append(new INVOKEVIRTUAL(index));
    }

    @Override
    public Instruction ADD() {
        return InstructionConstants.IADD;
    }

    @Override
    public Instruction SUB() {
        return InstructionConstants.ISUB;
    }

    @Override
    public Instruction MUL() {
        return InstructionConstants.IMUL;
    }

    @Override
    public Instruction DIV() {
        return InstructionConstants.IDIV;
    }

    @Override
    public Instruction REM() {
        return InstructionConstants.IREM;
    }

    @Override
    public Instruction NEG() {
        return InstructionConstants.INEG;
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

