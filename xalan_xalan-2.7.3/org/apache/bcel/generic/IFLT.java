/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFGE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFLT
extends IfInstruction {
    IFLT() {
    }

    public IFLT(InstructionHandle target) {
        super((short)155, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFLT(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFGE(super.getTarget());
    }
}

