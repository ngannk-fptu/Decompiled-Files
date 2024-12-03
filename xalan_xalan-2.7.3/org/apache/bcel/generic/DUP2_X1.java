/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.Visitor;

public class DUP2_X1
extends StackInstruction {
    public DUP2_X1() {
        super((short)93);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackInstruction(this);
        v.visitDUP2_X1(this);
    }
}

