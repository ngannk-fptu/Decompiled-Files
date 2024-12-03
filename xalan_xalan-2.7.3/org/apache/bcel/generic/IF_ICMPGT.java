/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ICMPGT
extends IfInstruction {
    IF_ICMPGT() {
    }

    public IF_ICMPGT(InstructionHandle target) {
        super((short)163, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ICMPGT(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ICMPLE(super.getTarget());
    }
}

