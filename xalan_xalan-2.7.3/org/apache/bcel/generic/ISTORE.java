/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.StoreInstruction;
import org.apache.bcel.generic.Visitor;

public class ISTORE
extends StoreInstruction {
    ISTORE() {
        super((short)54, (short)59);
    }

    public ISTORE(int n) {
        super((short)54, (short)59, n);
    }

    @Override
    public void accept(Visitor v) {
        super.accept(v);
        v.visitISTORE(this);
    }
}

