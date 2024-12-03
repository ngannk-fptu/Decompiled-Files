/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Visitor;

public class LSTORE
extends StoreInstruction {
    LSTORE() {
        super((short)55, (short)63);
    }

    public LSTORE(int n) {
        super((short)55, (short)63, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitLSTORE(this);
    }
}

