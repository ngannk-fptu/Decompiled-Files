/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ACMPNE
extends IfInstruction {
    IF_ACMPNE() {
    }

    public IF_ACMPNE(InstructionHandle target) {
        super((short)166, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ACMPNE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ACMPEQ(super.getTarget());
    }
}

