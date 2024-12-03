/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFNE
extends IfInstruction {
    IFNE() {
    }

    public IFNE(InstructionHandle target) {
        super((short)154, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFNE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFEQ(super.getTarget());
    }
}

