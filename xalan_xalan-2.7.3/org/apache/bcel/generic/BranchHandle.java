/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;

public final class BranchHandle
extends InstructionHandle {
    private BranchInstruction bi;

    static BranchHandle getBranchHandle(BranchInstruction i) {
        return new BranchHandle(i);
    }

    private BranchHandle(BranchInstruction i) {
        super(i);
        this.bi = i;
    }

    @Override
    public int getPosition() {
        return this.bi.getPosition();
    }

    public InstructionHandle getTarget() {
        return this.bi.getTarget();
    }

    @Override
    public void setInstruction(Instruction i) {
        super.setInstruction(i);
        if (!(i instanceof BranchInstruction)) {
            throw new ClassGenException("Assigning " + i + " to branch handle which is not a branch instruction");
        }
        this.bi = (BranchInstruction)i;
    }

    @Override
    void setPosition(int pos) {
        this.bi.setPosition(pos);
        super.setPosition(pos);
    }

    public void setTarget(InstructionHandle ih) {
        this.bi.setTarget(ih);
    }

    @Override
    protected int updatePosition(int offset, int maxOffset) {
        int x = this.bi.updatePosition(offset, maxOffset);
        super.setPosition(this.bi.getPosition());
        return x;
    }

    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh) {
        this.bi.updateTarget(oldIh, newIh);
    }
}

