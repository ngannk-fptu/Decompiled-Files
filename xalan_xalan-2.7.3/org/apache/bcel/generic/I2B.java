/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConversionInstruction;
import org.apache.bcel.generic.Visitor;

public class I2B
extends ConversionInstruction {
    public I2B() {
        super((short)145);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitConversionInstruction(this);
        v.visitI2B(this);
    }
}

