/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.apache.bcel.generic.Type;
import org.aspectj.weaver.Member;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.bcel.BcelVar;
import org.aspectj.weaver.bcel.BcelWorld;
import org.aspectj.weaver.bcel.Utility;

public class BcelCflowAccessVar
extends BcelVar {
    private Member stackField;
    private int index;

    public BcelCflowAccessVar(ResolvedType type, Member stackField, int index) {
        super(type, 0);
        this.stackField = stackField;
        this.index = index;
    }

    @Override
    public String toString() {
        return "BcelCflowAccessVar(" + this.getType() + " " + this.stackField + "." + this.index + ")";
    }

    @Override
    public Instruction createLoad(InstructionFactory fact) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public Instruction createStore(InstructionFactory fact) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public InstructionList createCopyFrom(InstructionFactory fact, int oldSlot) {
        throw new RuntimeException("unimplemented");
    }

    @Override
    public void appendLoad(InstructionList il, InstructionFactory fact) {
        il.append(this.createLoadInstructions(this.getType(), fact));
    }

    public InstructionList createLoadInstructions(ResolvedType toType, InstructionFactory fact) {
        InstructionList il = new InstructionList();
        il.append(Utility.createGet(fact, this.stackField));
        il.append(Utility.createConstant(fact, this.index));
        il.append(fact.createInvoke("org.aspectj.runtime.internal.CFlowStack", "get", Type.OBJECT, new Type[]{Type.INT}, (short)182));
        il.append(Utility.createConversion(fact, Type.OBJECT, BcelWorld.makeBcelType(toType)));
        return il;
    }

    @Override
    public void appendLoadAndConvert(InstructionList il, InstructionFactory fact, ResolvedType toType) {
        il.append(this.createLoadInstructions(toType, fact));
    }

    @Override
    public void insertLoad(InstructionList il, InstructionFactory fact) {
        il.insert(this.createLoadInstructions(this.getType(), fact));
    }
}

