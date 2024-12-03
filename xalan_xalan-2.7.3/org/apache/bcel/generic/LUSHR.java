/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ArithmeticInstruction;
import org.apache.bcel.generic.Visitor;

public class LUSHR
extends ArithmeticInstruction {
    public LUSHR() {
        super((short)125);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitArithmeticInstruction(this);
        v.visitLUSHR(this);
    }
}

