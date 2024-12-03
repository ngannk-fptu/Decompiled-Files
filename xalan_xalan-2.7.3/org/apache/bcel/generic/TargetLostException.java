/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.InstructionHandle;

public final class TargetLostException
extends Exception {
    private static final long serialVersionUID = -6857272667645328384L;
    private final InstructionHandle[] targets;

    TargetLostException(InstructionHandle[] t, String mesg) {
        super(mesg);
        this.targets = t;
    }

    public InstructionHandle[] getTargets() {
        return this.targets;
    }
}

