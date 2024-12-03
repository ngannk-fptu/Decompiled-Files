/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.VariableLengthInstruction;
import org.apache.bcel.util.ByteSequence;

public abstract class Select
extends BranchInstruction
implements VariableLengthInstruction,
StackConsumer,
StackProducer {
    @Deprecated
    protected int[] match;
    @Deprecated
    protected int[] indices;
    @Deprecated
    protected InstructionHandle[] targets;
    @Deprecated
    protected int fixed_length;
    @Deprecated
    protected int match_length;
    @Deprecated
    protected int padding;

    Select() {
    }

    Select(short opcode, int[] match, InstructionHandle[] targets, InstructionHandle defaultTarget) {
        super(opcode, null);
        this.match = match;
        this.targets = targets;
        this.setTarget(defaultTarget);
        for (InstructionHandle target2 : targets) {
            Select.notifyTarget(null, target2, this);
        }
        this.match_length = match.length;
        if (this.match_length != targets.length) {
            throw new ClassGenException("Match and target array have not the same length: Match length: " + match.length + " Target length: " + targets.length);
        }
        this.indices = new int[this.match_length];
    }

    protected Object clone() throws CloneNotSupportedException {
        Select copy = (Select)super.clone();
        copy.match = (int[])this.match.clone();
        copy.indices = (int[])this.indices.clone();
        copy.targets = (InstructionHandle[])this.targets.clone();
        return copy;
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        if (super.getTarget() == ih) {
            return true;
        }
        for (InstructionHandle target2 : this.targets) {
            if (target2 != ih) continue;
            return true;
        }
        return false;
    }

    @Override
    void dispose() {
        super.dispose();
        for (InstructionHandle target2 : this.targets) {
            target2.removeTargeter(this);
        }
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        for (int i = 0; i < this.padding; ++i) {
            out.writeByte(0);
        }
        super.setIndex(this.getTargetOffset());
        out.writeInt(super.getIndex());
    }

    final int getFixedLength() {
        return this.fixed_length;
    }

    public int[] getIndices() {
        return this.indices;
    }

    final int getIndices(int index) {
        return this.indices[index];
    }

    final int getMatch(int index) {
        return this.match[index];
    }

    final int getMatchLength() {
        return this.match_length;
    }

    public int[] getMatchs() {
        return this.match;
    }

    final int getPadding() {
        return this.padding;
    }

    final InstructionHandle getTarget(int index) {
        return this.targets[index];
    }

    public InstructionHandle[] getTargets() {
        return this.targets;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        this.padding = (4 - bytes.getIndex() % 4) % 4;
        for (int i = 0; i < this.padding; ++i) {
            bytes.readByte();
        }
        super.setIndex(bytes.readInt());
    }

    final void setFixedLength(int fixedLength) {
        this.fixed_length = fixedLength;
    }

    final int setIndices(int i, int value) {
        this.indices[i] = value;
        return value;
    }

    final void setIndices(int[] array) {
        this.indices = array;
    }

    final void setMatch(int index, int value) {
        this.match[index] = value;
    }

    final void setMatches(int[] array) {
        this.match = array;
    }

    final int setMatchLength(int matchLength) {
        this.match_length = matchLength;
        return matchLength;
    }

    public void setTarget(int i, InstructionHandle target) {
        Select.notifyTarget(this.targets[i], target, this);
        this.targets[i] = target;
    }

    final void setTargets(InstructionHandle[] array) {
        this.targets = array;
    }

    @Override
    public String toString(boolean verbose) {
        StringBuilder buf = new StringBuilder(super.toString(verbose));
        if (verbose) {
            for (int i = 0; i < this.match_length; ++i) {
                String s = "null";
                if (this.targets[i] != null) {
                    s = this.targets[i].getInstruction().toString();
                }
                buf.append("(").append(this.match[i]).append(", ").append(s).append(" = {").append(this.indices[i]).append("})");
            }
        } else {
            buf.append(" ...");
        }
        return buf.toString();
    }

    @Override
    protected int updatePosition(int offset, int maxOffset) {
        this.setPosition(this.getPosition() + offset);
        short oldLength = (short)super.getLength();
        this.padding = (4 - (this.getPosition() + 1) % 4) % 4;
        super.setLength((short)(this.fixed_length + this.padding));
        return super.getLength() - oldLength;
    }

    @Override
    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh) {
        boolean targeted = false;
        if (super.getTarget() == oldIh) {
            targeted = true;
            this.setTarget(newIh);
        }
        for (int i = 0; i < this.targets.length; ++i) {
            if (this.targets[i] != oldIh) continue;
            targeted = true;
            this.setTarget(i, newIh);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + oldIh);
        }
    }
}

