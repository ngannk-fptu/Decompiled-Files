/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.InstructionHandle;

public interface InstructionTargeter {
    public boolean containsTarget(InstructionHandle var1);

    public void updateTarget(InstructionHandle var1, InstructionHandle var2);
}

