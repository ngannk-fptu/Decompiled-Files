/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IfInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;

public class IFLE
extends IfInstruction {
    IFLE() {
    }

    public IFLE(InstructionHandle target) {
        super((short)158, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitIfInstruction(this);
        v.visitIFLE(this);
    }

    @Override
    public IfInstruction negate() {
        return new IFGT(super.getTarget());
    }
}

