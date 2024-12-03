/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.ClassGenException;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionTargeter;
import org.aspectj.apache.bcel.generic.ReturnaddressType;
import org.aspectj.apache.bcel.generic.Type;

public class InstructionBranch
extends Instruction
implements InstructionTargeter {
    private static final int UNSET = -1;
    protected int targetIndex = -1;
    protected InstructionHandle targetInstruction;
    protected int positionOfThisInstruction;

    public InstructionBranch(short opcode, InstructionHandle target) {
        super(opcode);
        this.setTarget(target);
    }

    public InstructionBranch(short opcode, int index) {
        super(opcode);
        this.targetIndex = index;
    }

    public InstructionBranch(short opcode) {
        super(opcode);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        int target = this.getTargetOffset();
        if (Math.abs(target) >= Short.MAX_VALUE && this.opcode != 200 && this.opcode != 201) {
            throw new ClassGenException("Branch target offset too large for short.  Instruction: " + this.getName().toUpperCase() + "(" + this.opcode + ")");
        }
        out.writeByte(this.opcode);
        switch (this.opcode) {
            case 200: 
            case 201: {
                out.writeInt(target);
                break;
            }
            case 153: 
            case 154: 
            case 155: 
            case 156: 
            case 157: 
            case 158: 
            case 159: 
            case 160: 
            case 161: 
            case 162: 
            case 163: 
            case 164: 
            case 165: 
            case 166: 
            case 167: 
            case 168: 
            case 198: 
            case 199: {
                out.writeShort(target);
                break;
            }
            default: {
                throw new IllegalStateException("Don't know how to write out " + this.getName().toUpperCase());
            }
        }
    }

    protected int getTargetOffset() {
        if (this.targetInstruction == null && this.targetIndex == -1) {
            throw new ClassGenException("Target of " + super.toString(true) + " is unknown");
        }
        if (this.targetInstruction == null) {
            return this.targetIndex;
        }
        return this.targetInstruction.getPosition() - this.positionOfThisInstruction;
    }

    protected int updatePosition(int offset, int max_offset) {
        int i = this.getTargetOffset();
        this.positionOfThisInstruction += offset;
        if (Math.abs(i) >= Short.MAX_VALUE - max_offset && this.opcode != 201 && this.opcode != 200) {
            if (this.opcode == 168 || this.opcode == 167) {
                this.opcode = this.opcode == 168 ? (short)201 : (short)200;
                return 2;
            }
            throw new IllegalStateException("Unable to pack method, jump (with opcode=" + this.opcode + ") is too far: " + Math.abs(i));
        }
        return 0;
    }

    @Override
    public String toString(boolean verbose) {
        String s = super.toString(verbose);
        String t = "null";
        if (verbose) {
            if (this.targetInstruction != null) {
                t = this.targetInstruction.getInstruction() == this ? "<points to itself>" : (this.targetInstruction.getInstruction() == null ? "<null destination>" : this.targetInstruction.getInstruction().toString(false));
            }
        } else if (this.targetInstruction != null) {
            this.targetIndex = this.getTargetOffset();
            t = "" + (this.targetIndex + this.positionOfThisInstruction);
        }
        return s + " -> " + t;
    }

    @Override
    public final int getIndex() {
        return this.targetIndex;
    }

    public InstructionHandle getTarget() {
        return this.targetInstruction;
    }

    public void setTarget(InstructionHandle target) {
        InstructionBranch.notifyTarget(this.targetInstruction, target, this);
        this.targetInstruction = target;
    }

    static final void notifyTarget(InstructionHandle oldHandle, InstructionHandle newHandle, InstructionTargeter t) {
        if (oldHandle != null) {
            oldHandle.removeTargeter(t);
        }
        if (newHandle != null) {
            newHandle.addTargeter(t);
        }
    }

    @Override
    public void updateTarget(InstructionHandle oldHandle, InstructionHandle newHandle) {
        if (this.targetInstruction != oldHandle) {
            throw new ClassGenException("Not targeting " + oldHandle + ", but " + this.targetInstruction);
        }
        this.setTarget(newHandle);
    }

    @Override
    public boolean containsTarget(InstructionHandle ih) {
        return this.targetInstruction == ih;
    }

    @Override
    void dispose() {
        this.setTarget(null);
        this.targetIndex = -1;
        this.positionOfThisInstruction = -1;
    }

    @Override
    public Type getType(ConstantPool cp) {
        if ((Constants.instFlags[this.opcode] & 0x4000L) != 0L) {
            return new ReturnaddressType(this.physicalSuccessor());
        }
        return super.getType(cp);
    }

    public InstructionHandle physicalSuccessor() {
        InstructionHandle ih = this.targetInstruction;
        while (ih.getPrev() != null) {
            ih = ih.getPrev();
        }
        while (ih.getInstruction() != this) {
            ih = ih.getNext();
        }
        InstructionHandle toThis = ih;
        while (ih != null) {
            if ((ih = ih.getNext()) == null || ih.getInstruction() != this) continue;
            throw new RuntimeException("physicalSuccessor() called on a shared JsrInstruction.");
        }
        return toThis.getNext();
    }

    public boolean isIfInstruction() {
        return (Constants.instFlags[this.opcode] & 0x2000L) != 0L;
    }

    @Override
    public boolean equals(Object other) {
        return this == other;
    }

    @Override
    public int hashCode() {
        int result = 17;
        result = this.opcode * 37 + result;
        return result;
    }
}

