/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.Visitor;

public class FLOAD
extends LoadInstruction {
    FLOAD() {
        super((short)23, (short)34);
    }

    public FLOAD(int n) {
        super((short)23, (short)34, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitFLOAD(this);
    }
}

