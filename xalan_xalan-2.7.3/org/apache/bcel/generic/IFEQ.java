/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFEQ
extends IfInstruction {
    IFEQ() {
    }

    public IFEQ(InstructionHandle target) {
        super((short)153, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFEQ(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFNE(super.getTarget());
    }
}

