/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.Visitor;

public class DLOAD
extends LoadInstruction {
    DLOAD() {
        super((short)24, (short)38);
    }

    public DLOAD(int n) {
        super((short)24, (short)38, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitDLOAD(this);
    }
}

