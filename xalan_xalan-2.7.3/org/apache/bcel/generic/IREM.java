/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Visitor;

public class IREM
extends ArithmeticInstruction
implements ExceptionThrower {
    public IREM() {
        super((short)112);
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitIREM(this);
    }

    @Override
    public Class<?>[] getExceptions() {
        return new Class[]{ExceptionConst.ARITHMETIC_EXCEPTION};
    }
}

