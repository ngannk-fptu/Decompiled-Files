/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.util.ByteSequence;

public abstract class CPInstruction
extends Instruction
implements TypedInstruction,
IndexedInstruction {
    @Deprecated
    protected int index;

    CPInstruction() {
    }

    protected CPInstruction(short opcode, int index) {
        super(opcode, (short)3);
        this.setIndex(index);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        out.writeShort(this.index);
    }

    @Override
    public final int getIndex() {
        return this.index;
    }

    @Override
    public Type getType(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        String name = cp.getConstantString(this.index, (byte)7);
        if (!name.startsWith("[")) {
            name = "L" + name + ";";
        }
        return Type.getType(name);
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        this.setIndex(bytes.readUnsignedShort());
        super.setLength(3);
    }

    @Override
    public void setIndex(int index) {
        if (index < 0) {
            throw new ClassGenException("Negative index value: " + index);
        }
        this.index = index;
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.index;
    }

    @Override
    public String toString(ConstantPool cp) {
        Object c = cp.getConstant(this.index);
        String str = cp.constantToString((Constant)c);
        if (c instanceof ConstantClass) {
            str = Utility.packageToPath(str);
        }
        return Const.getOpcodeName(super.getOpcode()) + " " + str;
    }
}

