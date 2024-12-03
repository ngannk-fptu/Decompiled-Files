/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.Instruction;
import org.apache.xalan.xsltc.compiler.util.MarkerInstruction;

class OutlineableChunkEnd
extends MarkerInstruction {
    public static final Instruction OUTLINEABLECHUNKEND = new OutlineableChunkEnd();

    private OutlineableChunkEnd() {
    }

    @Override
    public String getName() {
        return OutlineableChunkEnd.class.getName();
    }

    @Override
    public String toString() {
        return this.getName();
    }

    @Override
    public String toString(boolean verbose) {
        return this.getName();
    }
}

