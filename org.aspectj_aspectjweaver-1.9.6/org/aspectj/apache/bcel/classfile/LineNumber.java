/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Node;

public final class LineNumber
implements Node {
    private int startPC;
    private int lineNumber;

    public LineNumber(LineNumber c) {
        this(c.getStartPC(), c.getLineNumber());
    }

    LineNumber(DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort());
    }

    public LineNumber(int startPC, int lineNumber) {
        this.startPC = startPC;
        this.lineNumber = lineNumber;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitLineNumber(this);
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.startPC);
        file.writeShort(this.lineNumber);
    }

    public final int getLineNumber() {
        return this.lineNumber;
    }

    public final int getStartPC() {
        return this.startPC;
    }

    public final String toString() {
        return "LineNumber(" + this.startPC + ", " + this.lineNumber + ")";
    }

    public LineNumber copy() {
        return new LineNumber(this);
    }
}

