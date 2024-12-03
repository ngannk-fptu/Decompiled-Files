/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionTargeter;
import org.apache.bcel.generic.NamedAndTyped;
import org.apache.bcel.generic.Type;

public class LocalVariableGen
implements InstructionTargeter,
NamedAndTyped,
Cloneable {
    private int index;
    private String name;
    private Type type;
    private InstructionHandle start;
    private InstructionHandle end;
    private int origIndex;
    private boolean liveToEnd;

    public LocalVariableGen(int index, String name, Type type, InstructionHandle start, InstructionHandle end) {
        if (index < 0 || index > 65535) {
            throw new ClassGenException("Invalid index: " + index);
        }
        this.name = name;
        this.type = type;
        this.index = index;
        this.setStart(start);
        this.setEnd(end);
        this.origIndex = index;
        this.liveToEnd = end == null;
    }

    public LocalVariableGen(int index, String name, Type type, InstructionHandle start, InstructionHandle end, int origIndex) {
        this(index, name, type, start, end);
        this.origIndex = origIndex;
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
        return this.start == ih || this.end == ih;
    }

    void dispose() {
        this.setStart(null);
        this.setEnd(null);
    }

    public boolean equals(Object o) {
        if (!(o instanceof LocalVariableGen)) {
            return false;
        }
        LocalVariableGen l = (LocalVariableGen)o;
        return l.index == this.index && l.start == this.start && l.end == this.end;
    }

    public InstructionHandle getEnd() {
        return this.end;
    }

    public int getIndex() {
        return this.index;
    }

    public boolean getLiveToEnd() {
        return this.liveToEnd;
    }

    public LocalVariable getLocalVariable(ConstantPoolGen cp) {
        int startPc = 0;
        int length = 0;
        if (this.start != null && this.end != null) {
            startPc = this.start.getPosition();
            length = this.end.getPosition() - startPc;
            if (this.end.getNext() == null && this.liveToEnd) {
                length += this.end.getInstruction().getLength();
            }
        }
        int nameIndex = cp.addUtf8(this.name);
        int signatureIndex = cp.addUtf8(this.type.getSignature());
        return new LocalVariable(startPc, length, nameIndex, signatureIndex, this.index, cp.getConstantPool(), this.origIndex);
    }

    @Override
    public String getName() {
        return this.name;
    }

    public int getOrigIndex() {
        return this.origIndex;
    }

    public InstructionHandle getStart() {
        return this.start;
    }

    @Override
    public Type getType() {
        return this.type;
    }

    public int hashCode() {
        return this.name.hashCode() ^ this.type.hashCode();
    }

    public void setEnd(InstructionHandle end) {
        BranchInstruction.notifyTarget(this.end, end, this);
        this.end = end;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setLiveToEnd(boolean liveToEnd) {
        this.liveToEnd = liveToEnd;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    public void setStart(InstructionHandle start) {
        BranchInstruction.notifyTarget(this.start, start, this);
        this.start = start;
    }

    @Override
    public void setType(Type type) {
        this.type = type;
    }

    public String toString() {
        return "LocalVariableGen(" + this.name + ", " + this.type + ", " + this.start + ", " + this.end + ")";
    }

    @Override
    public void updateTarget(InstructionHandle oldIh, InstructionHandle newIh) {
        boolean targeted = false;
        if (this.start == oldIh) {
            targeted = true;
            this.setStart(newIh);
        }
        if (this.end == oldIh) {
            targeted = true;
            this.setEnd(newIh);
        }
        if (!targeted) {
            throw new ClassGenException("Not targeting " + oldIh + ", but {" + this.start + ", " + this.end + "}");
        }
    }
}

