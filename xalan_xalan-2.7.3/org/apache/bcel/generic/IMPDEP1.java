/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Visitor;

public class IMPDEP1
extends Instruction {
    public IMPDEP1() {
        super((short)254, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitIMPDEP1(this);
    }
}

