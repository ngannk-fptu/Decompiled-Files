/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ArrayInstruction;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.Visitor;

public class IASTORE
extends ArrayInstruction
implements StackConsumer {
    public IASTORE() {
        super((short)79);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackConsumer(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitArrayInstruction(this);
        v.visitIASTORE(this);
    }
}

