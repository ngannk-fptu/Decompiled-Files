/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.Visitor;

public class MONITOREXIT
extends Instruction
implements ExceptionThrower,
StackConsumer {
    public MONITOREXIT() {
        super((short)195, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionThrower(this);
        v.visitStackConsumer(this);
        v.visitMONITOREXIT(this);
    }

    @Override
    public Class<?>[] getExceptions() {
        return new Class[]{ExceptionConst.NULL_POINTER_EXCEPTION};
    }
}

