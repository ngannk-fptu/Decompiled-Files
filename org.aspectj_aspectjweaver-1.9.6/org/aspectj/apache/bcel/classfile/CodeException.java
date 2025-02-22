/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Serializable;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.Node;
import org.aspectj.apache.bcel.classfile.Utility;

public final class CodeException
implements Cloneable,
Constants,
Node,
Serializable {
    private int start_pc;
    private int end_pc;
    private int handler_pc;
    private int catch_type;

    public CodeException(CodeException c) {
        this(c.getStartPC(), c.getEndPC(), c.getHandlerPC(), c.getCatchType());
    }

    CodeException(DataInputStream file) throws IOException {
        this.start_pc = file.readUnsignedShort();
        this.end_pc = file.readUnsignedShort();
        this.handler_pc = file.readUnsignedShort();
        this.catch_type = file.readUnsignedShort();
    }

    public CodeException(int start_pc, int end_pc, int handler_pc, int catch_type) {
        this.start_pc = start_pc;
        this.end_pc = end_pc;
        this.handler_pc = handler_pc;
        this.catch_type = catch_type;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitCodeException(this);
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.start_pc);
        file.writeShort(this.end_pc);
        file.writeShort(this.handler_pc);
        file.writeShort(this.catch_type);
    }

    public final int getCatchType() {
        return this.catch_type;
    }

    public final int getEndPC() {
        return this.end_pc;
    }

    public final int getHandlerPC() {
        return this.handler_pc;
    }

    public final int getStartPC() {
        return this.start_pc;
    }

    public final void setCatchType(int catch_type) {
        this.catch_type = catch_type;
    }

    public final void setEndPC(int end_pc) {
        this.end_pc = end_pc;
    }

    public final void setHandlerPC(int handler_pc) {
        this.handler_pc = handler_pc;
    }

    public final void setStartPC(int start_pc) {
        this.start_pc = start_pc;
    }

    public final String toString() {
        return "CodeException(start_pc = " + this.start_pc + ", end_pc = " + this.end_pc + ", handler_pc = " + this.handler_pc + ", catch_type = " + this.catch_type + ")";
    }

    public final String toString(ConstantPool cp, boolean verbose) {
        String str = this.catch_type == 0 ? "<Any exception>(0)" : Utility.compactClassName(cp.getConstantString(this.catch_type, (byte)7), false) + (verbose ? "(" + this.catch_type + ")" : "");
        return this.start_pc + "\t" + this.end_pc + "\t" + this.handler_pc + "\t" + str;
    }

    public final String toString(ConstantPool cp) {
        return this.toString(cp, true);
    }

    public CodeException copy() {
        try {
            return (CodeException)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }
}

