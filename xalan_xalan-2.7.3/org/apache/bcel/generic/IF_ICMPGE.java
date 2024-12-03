/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ICMPGE
extends IfInstruction {
    IF_ICMPGE() {
    }

    public IF_ICMPGE(InstructionHandle target) {
        super((short)162, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPGE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ICMPLT(super.getTarget());
    }
}

