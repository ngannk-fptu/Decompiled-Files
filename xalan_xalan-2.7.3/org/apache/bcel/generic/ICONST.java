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

public class ICONST
extends Instruction
implements ConstantPushInstruction {
    private final int value;

    ICONST() {
        this(0);
    }

    public ICONST(int i) {
        super((short)3, (short)1);
        if (i < -1 || i > 5) {
            throw new ClassGenException("ICONST can be used only for value between -1 and 5: " + i);
        }
        super.setOpcode((short)(3 + i));
        this.value = i;
    }

    @Override
    public void accept(Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitICONST(this);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.INT;
    }

    @Override
    public Number getValue() {
        return this.value;
    }
}

