/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Constants;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class CodeException
implements Cloneable,
Node,
Constants {
    static final CodeException[] EMPTY_CODE_EXCEPTION_ARRAY = new CodeException[0];
    private int startPc;
    private int endPc;
    private int handlerPc;
    private int catchType;

    public CodeException(CodeException c) {
        this(c.getStartPC(), c.getEndPC(), c.getHandlerPC(), c.getCatchType());
    }

    CodeException(DataInput file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort());
    }

    public CodeException(int startPc, int endPc, int handlerPc, int catchType) {
        this.startPc = Args.requireU2(startPc, "startPc");
        this.endPc = Args.requireU2(endPc, "endPc");
        this.handlerPc = Args.requireU2(handlerPc, "handlerPc");
        this.catchType = Args.requireU2(catchType, "catchType");
    }

    @Override
    public void accept(Visitor v) {
        v.visitCodeException(this);
    }

    public CodeException copy() {
        try {
            return (CodeException)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.startPc);
        file.writeShort(this.endPc);
        file.writeShort(this.handlerPc);
        file.writeShort(this.catchType);
    }

    public int getCatchType() {
        return this.catchType;
    }

    public int getEndPC() {
        return this.endPc;
    }

    public int getHandlerPC() {
        return this.handlerPc;
    }

    public int getStartPC() {
        return this.startPc;
    }

    public void setCatchType(int catchType) {
        this.catchType = catchType;
    }

    public void setEndPC(int endPc) {
        this.endPc = endPc;
    }

    public void setHandlerPC(int handlerPc) {
        this.handlerPc = handlerPc;
    }

    public void setStartPC(int startPc) {
        this.startPc = startPc;
    }

    public String toString() {
        return "CodeException(startPc = " + this.startPc + ", endPc = " + this.endPc + ", handlerPc = " + this.handlerPc + ", catchType = " + this.catchType + ")";
    }

    public String toString(ConstantPool cp) {
        return this.toString(cp, true);
    }

    public String toString(ConstantPool cp, boolean verbose) {
        String str = this.catchType == 0 ? "<Any exception>(0)" : Utility.compactClassName(cp.getConstantString(this.catchType, (byte)7), false) + (verbose ? "(" + this.catchType + ")" : "");
        return this.startPc + "\t" + this.endPc + "\t" + this.handlerPc + "\t" + str;
    }
}

