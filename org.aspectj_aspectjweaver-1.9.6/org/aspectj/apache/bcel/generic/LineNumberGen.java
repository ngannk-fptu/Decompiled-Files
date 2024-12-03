/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.Serializable;
import org.aspectj.apache.bcel.classfile.LineNumber;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionTargeter;

public class LineNumberGen
implements InstructionTargeter,
Cloneable,
Serializable {
    private InstructionHandle ih;
    private int src_line;

    public LineNumberGen(InstructionHandle ih, int src_line) {
        this.setInstruction(ih);
        this.setSourceLine(src_line);
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return this.ih == ih;
    }

    @Override
    public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
        if (old_ih != this.ih) {
            throw new ClassGenException("Not targeting " + old_ih + ", but " + this.ih + "}");
        }
        this.setInstruction(new_ih);
    }

    public LineNumber getLineNumber() {
        return new LineNumber(this.ih.getPosition(), this.src_line);
    }

    public void setInstruction(InstructionHandle ih) {
        InstructionBranch.notifyTarget(this.ih, ih, this);
        this.ih = ih;
    }

    public Object clone() {
        try {
            return super.clone();
        }
        catch (CloneNotSupportedException e) {
            System.err.println(e);
            return null;
        }
    }

    public InstructionHandle getInstruction() {
        return this.ih;
    }

    public void setSourceLine(int src_line) {
        this.src_line = src_line;
    }

    public int getSourceLine() {
        return this.src_line;
    }
}

