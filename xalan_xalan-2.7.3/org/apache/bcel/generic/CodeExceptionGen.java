/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.classfile.CodeException;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.ObjectType;

public final class CodeExceptionGen
implements InstructionTargeter,
Cloneable {
    static final CodeExceptionGen[] EMPTY_ARRAY = new CodeExceptionGen[0];
    private InstructionHandle startPc;
    private InstructionHandle endPc;
    private InstructionHandle handlerPc;
    private ObjectType catchType;

    public CodeExceptionGen(InstructionHandle startPc, InstructionHandle endPc, InstructionHandle handlerPc, ObjectType catchType) {
        this.setStartPC(startPc);
        this.setEndPC(endPc);
        this.setHandlerPC(handlerPc);
        this.catchType = catchType;
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
        return this.startPc == ih || this.endPc == ih || this.handlerPc == ih;
    }

    public ObjectType getCatchType() {
        return this.catchType;
    }

    public CodeException getCodeException(ConstantPoolGen cp) {
        return new CodeException(this.startPc.getPosition(), this.endPc.getPosition() + this.endPc.getInstruction().getLength(), this.handlerPc.getPosition(), this.catchType == null ? 0 : cp.addClass(this.catchType));
    }

    public InstructionHandle getEndPC() {
        return this.endPc;
    }

    public InstructionHandle getHandlerPC() {
        return this.handlerPc;
    }

    public InstructionHandle getStartPC() {
        return this.startPc;
    }

    public void setCatchType(ObjectType catchType) {
        this.catchType = catchType;
    }

    public void setEndPC(InstructionHandle endPc) {
        BranchInstruction.notifyTarget(this.endPc, endPc, this);
        this.endPc = endPc;
    }

    public void setHandlerPC(InstructionHandle handlerPc) {
        BranchInstruction.notifyTarget(this.handlerPc, handlerPc, this);
        this.handlerPc = handlerPc;
    }

    public void setStartPC(InstructionHandle startPc) {
        BranchInstruction.notifyTarget(this.startPc, startPc, this);
        this.startPc = startPc;
    }

    public String toString() {
        return "CodeExceptionGen(" + this.startPc + ", " + this.endPc + ", " + this.handlerPc + ")";
    }

    @Override
    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh) {
        boolean targeted = false;
        if (this.startPc == oldIh) {
            targeted = true;
            this.setStartPC(newIh);
        }
        if (this.endPc == oldIh) {
            targeted = true;
            this.setEndPC(newIh);
        }
        if (this.handlerPc == oldIh) {
            targeted = true;
            this.setHandlerPC(newIh);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + oldIh + ", but {" + this.startPc + ", " + this.endPc + ", " + this.handlerPc + "}");
        }
    }
}

