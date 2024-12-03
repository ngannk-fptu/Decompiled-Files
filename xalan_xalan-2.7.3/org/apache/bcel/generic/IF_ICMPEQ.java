/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ICMPEQ
extends IfInstruction {
    IF_ICMPEQ() {
    }

    public IF_ICMPEQ(InstructionHandle target) {
        super((short)159, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPEQ(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ICMPNE(super.getTarget());
    }
}

