/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.Type;

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

    @Override
    public boolean equals(Object rat) {
        if (!(rat instanceof ReturnaddressType)) {
            return false;
        }
        ReturnaddressType that = (ReturnaddressType)rat;
        if (this.returnTarget == null || that.returnTarget == null) {
            return that.returnTarget == this.returnTarget;
        }
        return that.returnTarget.equals(this.returnTarget);
    }

    public InstructionHandle getTarget() {
        return this.returnTarget;
    }

    @Override
    public int hashCode() {
        if (this.returnTarget == null) {
            return 0;
        }
        return this.returnTarget.hashCode();
    }
}

