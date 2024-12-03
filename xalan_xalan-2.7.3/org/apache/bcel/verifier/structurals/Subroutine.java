/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.structurals;

import org.apache.bcel.generic.InstructionHandle;

public interface Subroutine {
    public boolean contains(InstructionHandle var1);

    public int[] getAccessedLocalsIndices();

    public InstructionHandle[] getEnteringJsrInstructions();

    public InstructionHandle[] getInstructions();

    public InstructionHandle getLeavingRET();

    public int[] getRecursivelyAccessedLocalsIndices();

    public Subroutine[] subSubs();
}

