/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.ReturnaddressType;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.generic.UnconditionalBranch;

public abstract class JsrInstruction
extends BranchInstruction
implements UnconditionalBranch,
TypedInstruction,
StackProducer {
    JsrInstruction() {
    }

    JsrInstruction(short opcode, InstructionHandle target) {
        super(opcode, target);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return new ReturnaddressType(this.physicalSuccessor());
    }

    public InstructionHandle physicalSuccessor() {
        InstructionHandle ih = super.getTarget();
        while (ih.getPrev() != null) {
            ih = ih.getPrev();
        }
        while (ih.getInstruction() != this) {
            ih = ih.getNext();
        }
        InstructionHandle toThis = ih;
        while (ih != null) {
            if ((ih = ih.getNext()) == null || ih.getInstruction() != this) continue;
            throw new IllegalStateException("physicalSuccessor() called on a shared JsrInstruction.");
        }
        return toThis.getNext();
    }
}

