/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.InstructionHandle;

public final class TargetLostException
extends Exception {
    private InstructionHandle[] targets;

    TargetLostException(InstructionHandle[] t, String mesg) {
        super(mesg);
        this.targets = t;
    }

    public InstructionHandle[] getTargets() {
        return this.targets;
    }
}

