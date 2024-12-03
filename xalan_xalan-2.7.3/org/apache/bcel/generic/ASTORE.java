/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Visitor;

public class ASTORE
extends StoreInstruction {
    ASTORE() {
        super((short)58, (short)75);
    }

    public ASTORE(int n) {
        super((short)58, (short)75, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitASTORE(this);
    }
}

