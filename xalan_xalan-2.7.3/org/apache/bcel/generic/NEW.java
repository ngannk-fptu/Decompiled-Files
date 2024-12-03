/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.AllocationInstruction;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Visitor;

public class NEW
extends CPInstruction
implements LoadClass,
AllocationInstruction,
ExceptionThrower,
StackProducer {
    NEW() {
    }

    public NEW(int index) {
        super((short)187, index);
    }

    @Override
    public void accept(Visitor v) {
        v.visitLoadClass(this);
        v.visitAllocationInstruction(this);
        v.visitExceptionThrower(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitNEW(this);
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_CLASS_AND_INTERFACE_RESOLUTION, ExceptionConst.ILLEGAL_ACCESS_ERROR, ExceptionConst.INSTANTIATION_ERROR);
    }

    @Override
    public ObjectType getLoadClassType(ConstantPoolGen cpg) {
        return (ObjectType)this.getType(cpg);
    }
}

