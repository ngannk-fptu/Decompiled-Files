/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFGE
extends IfInstruction {
    IFGE() {
    }

    public IFGE(InstructionHandle target) {
        super((short)156, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFGE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFLT(super.getTarget());
    }
}

