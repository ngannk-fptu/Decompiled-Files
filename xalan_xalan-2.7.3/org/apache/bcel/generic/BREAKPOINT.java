/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Visitor;

public class BREAKPOINT
extends Instruction {
    public BREAKPOINT() {
        super((short)202, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitBREAKPOINT(this);
    }
}

