/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ICMPLE
extends IfInstruction {
    IF_ICMPLE() {
    }

    public IF_ICMPLE(InstructionHandle target) {
        super((short)164, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPLE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ICMPGT(super.getTarget());
    }
}

