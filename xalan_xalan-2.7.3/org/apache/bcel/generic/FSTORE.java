/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Visitor;

public class FSTORE
extends StoreInstruction {
    FSTORE() {
        super((short)56, (short)67);
    }

    public FSTORE(int n) {
        super((short)56, (short)67, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitFSTORE(this);
    }
}

