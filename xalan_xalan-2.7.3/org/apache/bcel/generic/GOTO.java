/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.GotoInstruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.VariableLengthInstruction;
import org.apache.bcel.generic.Visitor;

public class GOTO
extends GotoInstruction
implements VariableLengthInstruction {
    GOTO() {
    }

    public GOTO(InstructionHandle target) {
        super((short)167, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitVariableLengthInstruction(this);
        v.visitUnconditionalBranch(this);
        v.visitBranchInstruction(this);
        v.visitGotoInstruction(this);
        v.visitGOTO(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.setIndex(this.getTargetOffset());
        short opcode = this.getOpcode();
        if (opcode == 167) {
            super.dump(out);
        } else {
            super.setIndex(this.getTargetOffset());
            out.writeByte(opcode);
            out.writeInt(super.getIndex());
        }
    }

    @Override
    protected int updatePosition(int offset, int maxOffset) {
        int i = this.getTargetOffset();
        this.setPosition(this.getPosition() + offset);
        if (Math.abs(i) >= Short.MAX_VALUE - maxOffset) {
            super.setOpcode((short)200);
            short oldLength = (short)super.getLength();
            super.setLength(5);
            return super.getLength() - oldLength;
        }
        return 0;
    }
}

