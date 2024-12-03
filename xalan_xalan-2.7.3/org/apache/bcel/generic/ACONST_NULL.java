/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.generic.Visitor;

public class ACONST_NULL
extends Instruction
implements PushInstruction,
TypedInstruction {
    public ACONST_NULL() {
        super((short)1, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitTypedInstruction(this);
        v.visitACONST_NULL(this);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.NULL;
    }
}

