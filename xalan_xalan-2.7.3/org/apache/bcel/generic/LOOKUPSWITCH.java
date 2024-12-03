/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class LOOKUPSWITCH
extends Select {
    LOOKUPSWITCH() {
    }

    public LOOKUPSWITCH(int[] match, InstructionHandle[] targets, InstructionHandle defaultTarget) {
        super((short)171, match, targets, defaultTarget);
        short length = (short)(9 + this.getMatchLength() * 8);
        super.setLength(length);
        this.setFixedLength(length);
    }

    @Override
    public void accept(Visitor v) {
        v.visitVariableLengthInstruction(this);
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitSelect(this);
        v.visitLOOKUPSWITCH(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.dump(out);
        int matchLength = this.getMatchLength();
        out.writeInt(matchLength);
        for (int i = 0; i < matchLength; ++i) {
            out.writeInt(super.getMatch(i));
            out.writeInt(this.setIndices(i, this.getTargetOffset(super.getTarget(i))));
        }
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        int matchLength = bytes.readInt();
        this.setMatchLength(matchLength);
        short fixedLength = (short)(9 + matchLength * 8);
        this.setFixedLength(fixedLength);
        short length = (short)(matchLength + super.getPadding());
        super.setLength(length);
        super.setMatches(new int[matchLength]);
        super.setIndices(new int[matchLength]);
        super.setTargets(new InstructionHandle[matchLength]);
        for (int i = 0; i < matchLength; ++i) {
            super.setMatch(i, bytes.readInt());
            super.setIndices(i, bytes.readInt());
        }
    }
}

