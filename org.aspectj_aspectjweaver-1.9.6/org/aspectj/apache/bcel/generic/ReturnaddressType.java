/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.Type;

public class ReturnaddressType
extends Type {
    public static final ReturnaddressType NO_TARGET = new ReturnaddressType();
    private InstructionHandle returnTarget;

    private ReturnaddressType() {
        super((byte)16, "<return address>");
    }

    public ReturnaddressType(InstructionHandle returnTarget) {
        super((byte)16, "<return address targeting " + returnTarget + ">");
        this.returnTarget = returnTarget;
    }

    public boolean equals(Object rat) {
        if (!(rat instanceof ReturnaddressType)) {
            return false;
        }
        return ((ReturnaddressType)rat).returnTarget.equals(this.returnTarget);
    }

    public InstructionHandle getTarget() {
        return this.returnTarget;
    }
}

