/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.UnconditionalBranch;

public abstract class GotoInstruction
extends BranchInstruction
implements UnconditionalBranch {
    GotoInstruction() {
    }

    GotoInstruction(short opcode, InstructionHandle target) {
        super(opcode, target);
    }
}

