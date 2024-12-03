/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.verifier.statics;

import java.util.Arrays;
import org.apache.bcel.generic.Type;
import org.apache.bcel.verifier.exc.AssertionViolatedException;
import org.apache.bcel.verifier.exc.LocalVariableInfoInconsistentException;
import org.apache.bcel.verifier.statics.DOUBLE_Upper;
import org.apache.bcel.verifier.statics.LONG_Upper;
import org.apache.bcel.verifier.statics.LocalVariableInfo;

public class LocalVariablesInfo {
    private final LocalVariableInfo[] localVariableInfos;

    LocalVariablesInfo(int maxLocals) {
        this.localVariableInfos = new LocalVariableInfo[maxLocals];
        Arrays.setAll(this.localVariableInfos, i -> new LocalVariableInfo());
    }

    public void add(int slot, String name, int startPc, int length, Type type) throws LocalVariableInfoInconsistentException {
        if (slot < 0 || slot >= this.localVariableInfos.length) {
            throw new AssertionViolatedException("Slot number for local variable information out of range.");
        }
        this.localVariableInfos[slot].add(name, startPc, length, type);
        if (type == Type.LONG) {
            this.localVariableInfos[slot + 1].add(name, startPc, length, LONG_Upper.theInstance());
        }
        if (type == Type.DOUBLE) {
            this.localVariableInfos[slot + 1].add(name, startPc, length, DOUBLE_Upper.theInstance());
        }
    }

    public LocalVariableInfo getLocalVariableInfo(int slot) {
        if (slot < 0 || slot >= this.localVariableInfos.length) {
            throw new AssertionViolatedException("Slot number for local variable information out of range.");
        }
        return this.localVariableInfos[slot];
    }
}

