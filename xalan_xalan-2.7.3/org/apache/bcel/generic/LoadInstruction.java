/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.Visitor;

public abstract class LoadInstruction
extends LocalVariableInstruction
implements PushInstruction {
    LoadInstruction(short canonTag, short cTag) {
        super(canonTag, cTag);
    }

    protected LoadInstruction(short opcode, short cTag, int n) {
        super(opcode, cTag, n);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitTypedInstruction(this);
        v.visitLocalVariableInstruction(this);
        v.visitLoadInstruction(this);
    }
}

