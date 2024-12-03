/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFNULL
extends IfInstruction {
    IFNULL() {
    }

    public IFNULL(InstructionHandle target) {
        super((short)198, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFNULL(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFNONNULL(super.getTarget());
    }
}

