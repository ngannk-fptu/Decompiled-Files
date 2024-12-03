/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.Visitor;

public class MONITORENTER
extends Instruction
implements ExceptionThrower,
StackConsumer {
    public MONITORENTER() {
        super((short)194, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionThrower(this);
        v.visitStackConsumer(this);
        v.visitMONITORENTER(this);
    }

    @Override
    public Class<?>[] getExceptions() {
        return new Class[]{ExceptionConst.NULL_POINTER_EXCEPTION};
    }
}

