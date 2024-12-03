/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.PopInstruction;
import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.Visitor;

public class POP
extends StackInstruction
implements PopInstruction {
    public POP() {
        super((short)87);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitPopInstruction(this);
        v.visitStackInstruction(this);
        v.visitPOP(this);
    }
}

