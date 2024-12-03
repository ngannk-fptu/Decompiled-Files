/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xalan.xsltc.compiler.util;

import org.apache.bcel.generic.Instruction;
import org.apache.xalan.xsltc.compiler.util.MarkerInstruction;

class OutlineableChunkStart
extends MarkerInstruction {
    public static final Instruction OUTLINEABLECHUNKSTART = new OutlineableChunkStart();

    private OutlineableChunkStart() {
    }

    @Override
    public String getName() {
        return OutlineableChunkStart.class.getName();
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

