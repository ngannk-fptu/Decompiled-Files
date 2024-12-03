/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.Visitor;

public class DUP_X1
extends StackInstruction {
    public DUP_X1() {
        super((short)90);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackInstruction(this);
        v.visitDUP_X1(this);
    }
}

