/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.StackInstruction;
import org.apache.bcel.generic.Visitor;

public class DUP2
extends StackInstruction
implements PushInstruction {
    public DUP2() {
        super((short)92);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitStackInstruction(this);
        v.visitDUP2(this);
    }
}

