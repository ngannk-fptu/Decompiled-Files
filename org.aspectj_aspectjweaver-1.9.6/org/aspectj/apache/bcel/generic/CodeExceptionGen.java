/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.Serializable;
import org.aspectj.apache.bcel.classfile.CodeException;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.InstructionBranch;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.ObjectType;

public final class CodeExceptionGen
implements InstructionTargeter,
Cloneable,
Serializable {
    private InstructionHandle start_pc;
    private InstructionHandle end_pc;
    private InstructionHandle handler_pc;
    private ObjectType catch_type;

    public CodeExceptionGen(InstructionHandle start_pc, InstructionHandle end_pc, InstructionHandle handler_pc, ObjectType catch_type) {
        this.setStartPC(start_pc);
        this.setEndPC(end_pc);
        this.setHandlerPC(handler_pc);
        this.catch_type = catch_type;
    }

    public CodeException getCodeException(ConstantPool cp) {
        return new CodeException(this.start_pc.getPosition(), this.end_pc.getPosition() + this.end_pc.getInstruction().getLength(), this.handler_pc.getPosition(), this.catch_type == null ? 0 : cp.addClass(this.catch_type));
    }

    public void setStartPC(InstructionHandle start_pc) {
        InstructionBranch.notifyTarget(this.start_pc, start_pc, this);
        this.start_pc = start_pc;
    }

    public void setEndPC(InstructionHandle end_pc) {
        InstructionBranch.notifyTarget(this.end_pc, end_pc, this);
        this.end_pc = end_pc;
    }

    public void setHandlerPC(InstructionHandle handler_pc) {
        InstructionBranch.notifyTarget(this.handler_pc, handler_pc, this);
        this.handler_pc = handler_pc;
    }

    @Override
    public void updateTarget(InstructionHandle old_ih, InstructionHandle new_ih) {
        boolean targeted = false;
        if (this.start_pc == old_ih) {
            targeted = true;
            this.setStartPC(new_ih);
        }
        if (this.end_pc == old_ih) {
            targeted = true;
            this.setEndPC(new_ih);
        }
        if (this.handler_pc == old_ih) {
            targeted = true;
            this.setHandlerPC(new_ih);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + old_ih + ", but {" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + "}");
        }
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return this.start_pc == ih || this.end_pc == ih || this.handler_pc == ih;
    }

    public void setCatchType(ObjectType catch_type) {
        this.catch_type = catch_type;
    }

    public ObjectType getCatchType() {
        return this.catch_type;
    }

    public InstructionHandle getStartPC() {
        return this.start_pc;
    }

    public InstructionHandle getEndPC() {
        return this.end_pc;
    }

    public InstructionHandle getHandlerPC() {
        return this.handler_pc;
    }

    public String toString() {
        return "CodeExceptionGen(" + this.start_pc + ", " + this.end_pc + ", " + this.handler_pc + ")";
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
}

