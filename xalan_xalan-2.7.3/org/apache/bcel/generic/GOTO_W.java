/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class GOTO_W
extends GotoInstruction {
    GOTO_W() {
    }

    public GOTO_W(InstructionHandle target) {
        super((short)200, target);
        super.setLength(5);
    }

    @Override
    public void accept(Visitor v) {
        v.visitUnconditionalBranch(this);
        v.visitBranchInstruction(this);
        v.visitGotoInstruction(this);
        v.visitGOTO_W(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.setIndex(this.getTargetOffset());
        out.writeByte(super.getOpcode());
        out.writeInt(super.getIndex());
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.setIndex(bytes.readInt());
        super.setLength(5);
    }
}

