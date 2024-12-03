/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ReturnInstruction;
import org.apache.bcel.generic.Visitor;

public class RETURN
extends ReturnInstruction {
    public RETURN() {
        super((short)177);
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackConsumer(this);
        v.visitReturnInstruction(this);
        v.visitRETURN(this);
    }
}

