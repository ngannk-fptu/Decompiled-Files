/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ICMPLT
extends IfInstruction {
    IF_ICMPLT() {
    }

    public IF_ICMPLT(InstructionHandle target) {
        super((short)161, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPLT(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ICMPGE(super.getTarget());
    }
}

