/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.util.ByteSequence;

public abstract class InstructionSelect
extends InstructionBranch {
    protected int[] match;
    protected int[] indices;
    protected InstructionHandle[] targets;
    protected int fixedLength;
    protected int matchLength;
    protected int padding = 0;
    protected short length;

    InstructionSelect(short opcode, int[] match, InstructionHandle[] targets, InstructionHandle target) {
        super(opcode, target);
        this.targets = targets;
        for (int i = 0; i < targets.length; ++i) {
            InstructionSelect.notifyTarget(null, targets[i], this);
        }
        this.match = match;
        this.matchLength = match.length;
        if (this.matchLength != targets.length) {
            throw new ClassGenException("Match and target array have not the same length");
        }
        this.indices = new int[this.matchLength];
    }

    protected int getTargetOffset(InstructionHandle target) {
        if (target == null) {
            throw new ClassGenException("Target of " + super.toString(true) + " is invalid null handle");
        }
        int t = target.getPosition();
        if (t < 0) {
            throw new ClassGenException("Invalid branch target position offset for " + super.toString(true) + ":" + t + ":" + target);
        }
        return t - this.positionOfThisInstruction;
    }

    @Override
    protected int updatePosition(int offset, int max_offset) {
        this.positionOfThisInstruction += offset;
        short old_length = this.length;
        this.padding = (4 - (this.positionOfThisInstruction + 1) % 4) % 4;
        this.length = (short)(this.fixedLength + this.padding);
        return this.length - old_length;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        for (int i = 0; i < this.padding; ++i) {
            out.writeByte(0);
        }
        this.targetIndex = this.getTargetOffset();
        out.writeInt(this.targetIndex);
    }

    public InstructionSelect(short opcode, ByteSequence bytes) throws IOException {
        super(opcode);
        this.padding = (4 - bytes.getIndex() % 4) % 4;
        for (int i = 0; i < this.padding; ++i) {
            bytes.readByte();
        }
        this.targetIndex = bytes.readInt();
    }

    @Override
    public String toString(boolean verbose) {
        StringBuffer buf = new StringBuffer(super.toString(verbose));
        if (verbose) {
            for (int i = 0; i < this.matchLength; ++i) {
                String s = "null";
                if (this.targets[i] != null) {
                    s = this.targets[i].getInstruction().toString();
                }
                buf.append("(" + this.match[i] + ", " + s + " = {" + this.indices[i] + "})");
            }
        } else {
            buf.append(" ...");
        }
        return buf.toString();
    }

    public void setTarget(int i, InstructionHandle target) {
        InstructionSelect.notifyTarget(this.targets[i], target, this);
        this.targets[i] = target;
    }

    @Override
    public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
        boolean targeted = false;
        if (this.targetInstruction == old_ih) {
            targeted = true;
            this.setTarget(new_ih);
        }
        for (int i = 0; i < this.targets.length; ++i) {
            if (this.targets[i] != old_ih) continue;
            targeted = true;
            this.setTarget(i, new_ih);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + old_ih);
        }
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        if (this.targetInstruction == ih) {
            return true;
        }
        for (int i = 0; i < this.targets.length; ++i) {
            if (this.targets[i] != ih) continue;
            return true;
        }
        return false;
    }

    @Override
    void dispose() {
        super.dispose();
        for (int i = 0; i < this.targets.length; ++i) {
            this.targets[i].removeTargeter(this);
        }
    }

    public int[] getMatchs() {
        return this.match;
    }

    public int[] getIndices() {
        return this.indices;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37;
    }

    public InstructionHandle[] getTargets() {
        return this.targets;
    }

    @Override
    public int getLength() {
        return this.length;
    }
}

