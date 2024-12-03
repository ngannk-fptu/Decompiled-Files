/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Visitor;

public class FALOAD
extends ArrayInstruction
implements StackProducer {
    public FALOAD() {
        super((short)48);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitFALOAD(this);
    }
}

