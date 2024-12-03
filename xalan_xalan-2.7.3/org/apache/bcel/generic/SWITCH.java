/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.Arrays;
import org.apache.bcel.generic.CompoundInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LOOKUPSWITCH;
import org.apache.bcel.generic.Select;
import org.apache.bcel.generic.TABLESWITCH;

public final class SWITCH
implements CompoundInstruction {
    private final Select instruction;

    private static boolean matchIsOrdered(int[] match, int matchLength, int maxGap) {
        for (int i = 1; i < matchLength; ++i) {
            if (match[i] - match[i - 1] <= maxGap) continue;
            return false;
        }
        return true;
    }

    private static void sort(int l, int r, int[] match, InstructionHandle[] targets) {
        int i = l;
        int j = r;
        int m = match[l + r >>> 1];
        while (true) {
            if (match[i] < m) {
                ++i;
                continue;
            }
            while (m < match[j]) {
                --j;
            }
            if (i <= j) {
                int h = match[i];
                match[i] = match[j];
                match[j] = h;
                InstructionHandle h2 = targets[i];
                targets[i] = targets[j];
                targets[j] = h2;
                ++i;
                --j;
            }
            if (i > j) break;
        }
        if (l < j) {
            SWITCH.sort(l, j, match, targets);
        }
        if (i < r) {
            SWITCH.sort(i, r, match, targets);
        }
    }

    public SWITCH(int[] match, InstructionHandle[] targets, InstructionHandle target) {
        this(match, targets, target, 1);
    }

    public SWITCH(int[] match, InstructionHandle[] targets, InstructionHandle target, int maxGap) {
        int[] matchClone = (int[])match.clone();
        InstructionHandle[] targetsClone = (InstructionHandle[])targets.clone();
        int matchLength = match.length;
        if (matchLength < 2) {
            this.instruction = new TABLESWITCH(match, targets, target);
        } else {
            SWITCH.sort(0, matchLength - 1, matchClone, targetsClone);
            if (SWITCH.matchIsOrdered(matchClone, matchLength, maxGap)) {
                int maxSize = matchLength + matchLength * maxGap;
                int[] mVec = new int[maxSize];
                InstructionHandle[] tVec = new InstructionHandle[maxSize];
                int count = 1;
                mVec[0] = match[0];
                tVec[0] = targets[0];
                for (int i = 1; i < matchLength; ++i) {
                    int prev = match[i - 1];
                    int gap = match[i] - prev;
                    for (int j = 1; j < gap; ++j) {
                        mVec[count] = prev + j;
                        tVec[count] = target;
                        ++count;
                    }
                    mVec[count] = match[i];
                    tVec[count] = targets[i];
                    ++count;
                }
                this.instruction = new TABLESWITCH(Arrays.copyOf(mVec, count), Arrays.copyOf(tVec, count), target);
            } else {
                this.instruction = new LOOKUPSWITCH(matchClone, targetsClone, target);
            }
        }
    }

    public Instruction getInstruction() {
        return this.instruction;
    }

    @Override
    public InstructionList getInstructionList() {
        return new InstructionList(this.instruction);
    }
}

