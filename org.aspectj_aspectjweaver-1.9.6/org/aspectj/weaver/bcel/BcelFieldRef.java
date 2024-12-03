/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.bcel;

import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionFactory;
import org.aspectj.apache.bcel.generic.InstructionList;
import org.aspectj.weaver.ResolvedType;
import org.aspectj.weaver.bcel.BcelVar;
import org.aspectj.weaver.bcel.BcelWorld;

public class BcelFieldRef
extends BcelVar {
    private String className;
    private String fieldName;

    public BcelFieldRef(ResolvedType type, String className, String fieldName) {
        super(type, 0);
        this.className = className;
        this.fieldName = fieldName;
    }

    @Override
    public String toString() {
        return "BcelFieldRef(" + this.getType() + " " + this.className + "." + this.fieldName + ")";
    }

    @Override
    public Instruction createLoad(InstructionFactory fact) {
        return fact.createFieldAccess(this.className, this.fieldName, BcelWorld.makeBcelType(this.getType()), (short)178);
    }

    @Override
    public Instruction createStore(InstructionFactory fact) {
        return fact.createFieldAccess(this.className, this.fieldName, BcelWorld.makeBcelType(this.getType()), (short)179);
    }

    @Override
    public InstructionList createCopyFrom(InstructionFactory fact, int oldSlot) {
        throw new RuntimeException("unimplemented");
    }
}

