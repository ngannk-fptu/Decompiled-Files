/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.Visitor;

public abstract class StoreInstruction
extends LocalVariableInstruction
implements PopInstruction {
    StoreInstruction(short canonTag, short cTag) {
        super(canonTag, cTag);
    }

    protected StoreInstruction(short opcode, short cTag, int n) {
        super(opcode, cTag, n);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitPopInstruction(this);
        v.visitTypedInstruction(this);
        v.visitLocalVariableInstruction(this);
        v.visitStoreInstruction(this);
    }
}

