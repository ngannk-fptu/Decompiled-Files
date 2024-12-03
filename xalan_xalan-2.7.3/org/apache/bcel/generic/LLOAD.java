/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.Visitor;

public class LLOAD
extends LoadInstruction {
    LLOAD() {
        super((short)22, (short)30);
    }

    public LLOAD(int n) {
        super((short)22, (short)30, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitLLOAD(this);
    }
}

