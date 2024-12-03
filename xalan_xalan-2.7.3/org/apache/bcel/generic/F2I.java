/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.Visitor;

public class F2I
extends ConversionInstruction {
    public F2I() {
        super((short)139);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitF2I(this);
    }
}

