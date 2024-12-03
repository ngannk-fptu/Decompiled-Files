/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.StackConsumer;

public abstract class IfInstruction
extends BranchInstruction
implements StackConsumer {
    IfInstruction() {
    }

    protected IfInstruction(short opcode, InstructionHandle target) {
        super(opcode, target);
    }

    public abstract IfInstruction negate();
}

