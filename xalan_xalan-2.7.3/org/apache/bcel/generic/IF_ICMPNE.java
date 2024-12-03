/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ICMPNE
extends IfInstruction {
    IF_ICMPNE() {
    }

    public IF_ICMPNE(InstructionHandle target) {
        super((short)160, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPNE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ICMPEQ(super.getTarget());
    }
}

