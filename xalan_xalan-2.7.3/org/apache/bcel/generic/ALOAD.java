/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.Visitor;

public class ALOAD
extends LoadInstruction {
    ALOAD() {
        super((short)25, (short)42);
    }

    public ALOAD(int n) {
        super((short)25, (short)42, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitALOAD(this);
    }
}

