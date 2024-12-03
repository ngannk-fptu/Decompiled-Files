/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.LoadInstruction;
import org.apache.bcel.generic.Visitor;

public class ILOAD
extends LoadInstruction {
    ILOAD() {
        super((short)21, (short)26);
    }

    public ILOAD(int n) {
        super((short)21, (short)26, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitILOAD(this);
    }
}

