/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IF_ACMPNE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IF_ACMPEQ
extends IfInstruction {
    IF_ACMPEQ() {
    }

    public IF_ACMPEQ(InstructionHandle target) {
        super((short)165, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIF_ACMPEQ(this);
    }

    @Override
    public IfInstruction negate() {
        return new IF_ACMPNE(super.getTarget());
    }
}

