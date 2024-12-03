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

public class TABLESWITCH
extends Select {
    TABLESWITCH() {
    }

    public TABLESWITCH(int[] match, InstructionHandle[] targets, InstructionHandle defaultTarget) {
        super((short)170, match, targets, defaultTarget);
        short length = (short)(13 + this.getMatchLength() * 4);
        super.setLength(length);
        this.setFixedLength(length);
    }

    @Override
    public void accept(Visitor v) {
        v.visitVariableLengthInstruction(this);
        v.visitStackConsumer(this);
        v.visitBranchInstruction(this);
        v.visitSelect(this);
        v.visitTABLESWITCH(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.dump(out);
        int matchLength = this.getMatchLength();
        int low = matchLength > 0 ? super.getMatch(0) : 0;
        out.writeInt(low);
        int high = matchLength > 0 ? super.getMatch(matchLength - 1) : 0;
        out.writeInt(high);
        for (int i = 0; i < matchLength; ++i) {
            out.writeInt(this.setIndices(i, this.getTargetOffset(super.getTarget(i))));
        }
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        int low = bytes.readInt();
        int high = bytes.readInt();
        int matchLength = high - low + 1;
        this.setMatchLength(matchLength);
        short fixedLength = (short)(13 + matchLength * 4);
        this.setFixedLength(fixedLength);
        super.setLength((short)(fixedLength + super.getPadding()));
        super.setMatches(new int[matchLength]);
        super.setIndices(new int[matchLength]);
        super.setTargets(new InstructionHandle[matchLength]);
        for (int i = 0; i < matchLength; ++i) {
            super.setMatch(i, low + i);
            super.setIndices(i, bytes.readInt());
        }
    }
}

