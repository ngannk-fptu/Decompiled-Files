/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.bcel.BcelVar;

public class AspectInstanceVar
extends BcelVar {
    public AspectInstanceVar(ResolvedType type) {
        super(type, -1);
    }

    @Override
    public Instruction createLoad(InstructionFactory fact) {
        throw new IllegalStateException();
    }

    @Override
    public Instruction createStore(InstructionFactory fact) {
        throw new IllegalStateException();
    }

    @Override
    public void appendStore(InstructionList il, InstructionFactory fact) {
        throw new IllegalStateException();
    }

    @Override
    public void appendLoad(InstructionList il, InstructionFactory fact) {
        throw new IllegalStateException();
    }

    @Override
    public void appendLoadAndConvert(InstructionList il, InstructionFactory fact, ResolvedType toType) {
        throw new IllegalStateException();
    }

    @Override
    public void insertLoad(InstructionList il, InstructionFactory fact) {
        InstructionList loadInstructions = new InstructionList();
        loadInstructions.append(fact.createInvoke(this.getType().getName(), "aspectOf", "()" + this.getType().getSignature(), (short)184));
        il.insert(loadInstructions);
    }

    @Override
    public InstructionList createCopyFrom(InstructionFactory fact, int oldSlot) {
        throw new IllegalStateException();
    }

    @Override
    void appendConvertableArrayLoad(InstructionList il, InstructionFactory fact, int index, ResolvedType convertTo) {
        throw new IllegalStateException();
    }

    @Override
    void appendConvertableArrayStore(InstructionList il, InstructionFactory fact, int index, BcelVar storee) {
        throw new IllegalStateException();
    }

    @Override
    InstructionList createConvertableArrayStore(InstructionFactory fact, int index, BcelVar storee) {
        throw new IllegalStateException();
    }

    @Override
    InstructionList createConvertableArrayLoad(InstructionFactory fact, int index, ResolvedType convertTo) {
        throw new IllegalStateException();
    }

    @Override
    public int getPositionInAroundState() {
        throw new IllegalStateException();
    }

    @Override
    public void setPositionInAroundState(int positionInAroundState) {
        throw new IllegalStateException();
    }
}

