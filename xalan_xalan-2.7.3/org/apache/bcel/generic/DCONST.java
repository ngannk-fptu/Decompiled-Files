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

public class DCONST
extends Instruction
implements ConstantPushInstruction {
    private final double value;

    DCONST() {
        this(0.0);
    }

    public DCONST(double f) {
        super((short)14, (short)1);
        if (f == 0.0) {
            super.setOpcode((short)14);
        } else if (f == 1.0) {
            super.setOpcode((short)15);
        } else {
            throw new ClassGenException("DCONST can be used only for 0.0 and 1.0: " + f);
        }
        this.value = f;
    }

    @Override
    public void accept(Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitDCONST(this);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.DOUBLE;
    }

    @Override
    public Number getValue() {
        return this.value;
    }
}

