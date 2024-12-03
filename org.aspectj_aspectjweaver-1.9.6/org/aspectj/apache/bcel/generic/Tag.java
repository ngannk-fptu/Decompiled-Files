/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionTargeter;

public abstract class Tag
implements InstructionTargeter,
Cloneable {
    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return false;
    }

    @Override
    public void updateTarget(InstructionHandle oldHandle, InstructionHandle newHandle) {
        oldHandle.removeTargeter(this);
        if (newHandle != null) {
            newHandle.addTargeter(this);
        }
    }

    public Tag copy() {
        try {
            return (Tag)this.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("Sanity check, can't clone me");
        }
    }
}

