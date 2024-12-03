/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.NEWARRAY;

public interface InstructionComparator {
    public static final InstructionComparator DEFAULT = (i1, i2) -> {
        if (i1.getOpcode() == i2.getOpcode()) {
            if (i1 instanceof BranchInstruction) {
                return false;
            }
            if (i1 instanceof ConstantPushInstruction) {
                return ((ConstantPushInstruction)((Object)i1)).getValue().equals(((ConstantPushInstruction)((Object)i2)).getValue());
            }
            if (i1 instanceof IndexedInstruction) {
                return ((IndexedInstruction)((Object)i1)).getIndex() == ((IndexedInstruction)((Object)i2)).getIndex();
            }
            if (i1 instanceof NEWARRAY) {
                return ((NEWARRAY)i1).getTypecode() == ((NEWARRAY)i2).getTypecode();
            }
            return true;
        }
        return false;
    };

    public boolean equals(Instruction var1, Instruction var2);
}

