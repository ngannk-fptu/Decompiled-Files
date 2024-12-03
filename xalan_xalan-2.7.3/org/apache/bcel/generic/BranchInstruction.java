/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.util.ByteSequence;

public abstract class BranchInstruction
extends Instruction
implements InstructionTargeter {
    @Deprecated
    protected int index;
    @Deprecated
    protected InstructionHandle target;
    @Deprecated
    protected int position;

    static void notifyTarget(InstructionHandle oldIh, InstructionHandle newIh, InstructionTargeter t) {
        if (oldIh != null) {
            oldIh.removeTargeter(t);
        }
        if (newIh != null) {
            newIh.addTargeter(t);
        }
    }

    BranchInstruction() {
    }

    protected BranchInstruction(short opcode, InstructionHandle target) {
        super(opcode, (short)3);
        this.setTarget(target);
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return this.target == ih;
    }

    @Override
    void dispose() {
        this.setTarget(null);
        this.index = -1;
        this.position = -1;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        this.index = this.getTargetOffset();
        if (!BranchInstruction.isValidShort(this.index)) {
            throw new ClassGenException("Branch target offset too large for short: " + this.index);
        }
        out.writeShort(this.index);
    }

    public final int getIndex() {
        return this.index;
    }

    protected int getPosition() {
        return this.position;
    }

    public InstructionHandle getTarget() {
        return this.target;
    }

    protected int getTargetOffset() {
        return this.getTargetOffset(this.target);
    }

    protected int getTargetOffset(InstructionHandle target) {
        if (target == null) {
            throw new ClassGenException("Target of " + super.toString(true) + " is invalid null handle");
        }
        int t = target.getPosition();
        if (t < 0) {
            throw new ClassGenException("Invalid branch target position offset for " + super.toString(true) + ":" + t + ":" + target);
        }
        return t - this.position;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.setLength(3);
        this.index = bytes.readShort();
    }

    protected void setIndex(int index) {
        this.index = index;
    }

    protected void setPosition(int position) {
        this.position = position;
    }

    public void setTarget(InstructionHandle target) {
        BranchInstruction.notifyTarget(this.target, target, this);
        this.target = target;
    }

    @Override
    public String toString(boolean verbose) {
        String s = super.toString(verbose);
        String t = "null";
        if (this.target != null) {
            if (verbose) {
                t = this.target.getInstruction() == this ? "<points to itself>" : (this.target.getInstruction() == null ? "<null instruction!!!?>" : "" + this.target.getPosition());
            } else {
                this.index = this.target.getPosition();
                t = "" + this.index;
            }
        }
        return s + " -> " + t;
    }

    protected int updatePosition(int offset, int maxOffset) {
        this.position += offset;
        return 0;
    }

    @Override
    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh) {
        if (this.target != oldIh) {
            throw new ClassGenException("Not targeting " + oldIh + ", but " + this.target);
        }
        this.setTarget(newIh);
    }
}

