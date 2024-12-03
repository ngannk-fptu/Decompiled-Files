/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;

public class FCONST
extends Instruction
implements ConstantPushInstruction {
    private final float value;

    FCONST() {
        this(0.0f);
    }

    public FCONST(float f) {
        super((short)11, (short)1);
        if ((double)f == 0.0) {
            super.setOpcode((short)11);
        } else if ((double)f == 1.0) {
            super.setOpcode((short)12);
        } else if ((double)f == 2.0) {
            super.setOpcode((short)13);
        } else {
            throw new ClassGenException("FCONST can be used only for 0.0, 1.0 and 2.0: " + f);
        }
        this.value = f;
    }

    @Override
    public void accept(Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitFCONST(this);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.FLOAT;
    }

    @Override
    public Number getValue() {
        return Float.valueOf(this.value);
    }
}

