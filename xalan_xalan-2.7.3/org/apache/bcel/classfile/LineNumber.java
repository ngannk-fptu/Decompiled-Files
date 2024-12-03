/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class LineNumber
implements Cloneable,
Node {
    static final LineNumber[] EMPTY_ARRAY = new LineNumber[0];
    private int startPc;
    private int lineNumber;

    LineNumber(DataInput file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }

    public LineNumber(int startPc, int lineNumber) {
        this.startPc = Args.requireU2(startPc, "startPc");
        this.lineNumber = Args.requireU2(lineNumber, "lineNumber");
    }

    public LineNumber(LineNumber c) {
        this(c.getStartPC(), c.getLineNumber());
    }

    @Override
    public void accept(Visitor v) {
        v.visitLineNumber(this);
    }

    public LineNumber copy() {
        try {
            return (LineNumber)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.startPc);
        file.writeShort(this.lineNumber);
    }

    public int getLineNumber() {
        return this.lineNumber & 0xFFFF;
    }

    public int getStartPC() {
        return this.startPc & 0xFFFF;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = (short)lineNumber;
    }

    public void setStartPC(int startPc) {
        this.startPc = (short)startPc;
    }

    public String toString() {
        return "LineNumber(" + this.getStartPC() + ", " + this.getLineNumber() + ")";
    }
}

