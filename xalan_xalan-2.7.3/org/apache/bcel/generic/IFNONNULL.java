/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFNULL;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFNONNULL
extends IfInstruction {
    IFNONNULL() {
    }

    public IFNONNULL(InstructionHandle target) {
        super((short)199, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFNONNULL(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFNULL(super.getTarget());
    }
}

