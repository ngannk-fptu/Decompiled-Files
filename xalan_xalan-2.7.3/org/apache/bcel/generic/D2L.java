/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.Visitor;

public class D2L
extends ConversionInstruction {
    public D2L() {
        super((short)143);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitD2L(this);
    }
}

