/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.FieldInstruction;
import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.Visitor;

public class PUTSTATIC
extends FieldInstruction
implements ExceptionThrower,
PopInstruction {
    PUTSTATIC() {
    }

    public PUTSTATIC(int index) {
        super((short)179, index);
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionThrower(this);
        v.visitStackConsumer(this);
        v.visitPopInstruction(this);
        v.visitTypedInstruction(this);
        v.visitLoadClass(this);
        v.visitCPInstruction(this);
        v.visitFieldOrMethod(this);
        v.visitFieldInstruction(this);
        v.visitPUTSTATIC(this);
    }

    @Override
    public int consumeStack(ConstantPoolGen cpg) {
        return this.getFieldSize(cpg);
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_FIELD_AND_METHOD_RESOLUTION, ExceptionConst.INCOMPATIBLE_CLASS_CHANGE_ERROR);
    }
}

