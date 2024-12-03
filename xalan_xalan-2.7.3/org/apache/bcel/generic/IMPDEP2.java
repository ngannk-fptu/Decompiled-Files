/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Visitor;

public class IMPDEP2
extends Instruction {
    public IMPDEP2() {
        super((short)255, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitIMPDEP2(this);
    }
}

