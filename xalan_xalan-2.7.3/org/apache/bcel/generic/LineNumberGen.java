/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.Objects;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;

public class LineNumberGen
implements InstructionTargeter,
Cloneable {
    static final LineNumberGen[] EMPTY_ARRAY = new LineNumberGen[0];
    private InstructionHandle ih;
    private int srcLine;

    public LineNumberGen(InstructionHandle ih, int srcLine) {
        this.setInstruction(ih);
        this.setSourceLine(srcLine);
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            throw new Error("Clone Not Supported");
        }
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return this.ih == ih;
    }

    public InstructionHandle getInstruction() {
        return this.ih;
    }

    public LineNumber getLineNumber() {
        return new LineNumber(this.ih.getPosition(), this.srcLine);
    }

    public int getSourceLine() {
        return this.srcLine;
    }

    public void setInstruction(InstructionHandle instructionHandle) {
        Objects.requireNonNull(instructionHandle, "instructionHandle");
        BranchInstruction.notifyTarget(this.ih, instructionHandle, this);
        this.ih = instructionHandle;
    }

    public void setSourceLine(int srcLine) {
        this.srcLine = srcLine;
    }

    @Override
    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh) {
        if (oldIh != this.ih) {
            throw new ClassGenException("Not targeting " + oldIh + ", but " + this.ih + "}");
        }
        this.setInstruction(newIh);
    }
}

