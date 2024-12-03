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

public class LCONST
extends Instruction
implements ConstantPushInstruction {
    private final long value;

    LCONST() {
        this(0L);
    }

    public LCONST(long l) {
        super((short)9, (short)1);
        if (l == 0L) {
            super.setOpcode((short)9);
        } else if (l == 1L) {
            super.setOpcode((short)10);
        } else {
            throw new ClassGenException("LCONST can be used only for 0 and 1: " + l);
        }
        this.value = l;
    }

    @Override
    public void accept(Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitLCONST(this);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.LONG;
    }

    @Override
    public Number getValue() {
        return this.value;
    }
}

