/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Visitor;

public class DSTORE
extends StoreInstruction {
    DSTORE() {
        super((short)57, (short)71);
    }

    public DSTORE(int n) {
        super((short)57, (short)71, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitDSTORE(this);
    }
}

