/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.UnconditionalBranch;
import org.apache.bcel.generic.Visitor;

public class ATHROW
extends Instruction
implements UnconditionalBranch,
ExceptionThrower {
    public ATHROW() {
        super((short)191, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitUnconditionalBranch(this);
        v.visitExceptionThrower(this);
        v.visitATHROW(this);
    }

    @Override
    public Class<?>[] getExceptions() {
        return new Class[]{ExceptionConst.THROWABLE};
    }
}

