/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantClass;
import org.aspectj.apache.bcel.classfile.ConstantDouble;
import org.aspectj.apache.bcel.classfile.ConstantFloat;
import org.aspectj.apache.bcel.classfile.ConstantInteger;
import org.aspectj.apache.bcel.classfile.ConstantLong;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantString;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.Type;

public class InstructionCP
extends Instruction {
    protected int index;

    public InstructionCP(short opcode, int index) {
        super(opcode);
        this.index = index;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.opcode == 19 && this.index < 256) {
            out.writeByte(18);
            out.writeByte(this.index);
        } else {
            out.writeByte(this.opcode);
            if (Constants.iLen[this.opcode] == 2) {
                if (this.index > 255) {
                    throw new IllegalStateException();
                }
                out.writeByte(this.index);
            } else {
                out.writeShort(this.index);
            }
        }
    }

    @Override
    public int getLength() {
        if (this.opcode == 19 && this.index < 256) {
            return 2;
        }
        return super.getLength();
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.index;
    }

    public String toString(ConstantPool cp) {
        Constant c = cp.getConstant(this.index);
        String str = cp.constantToString(c);
        if (c instanceof ConstantClass) {
            str = str.replace('.', '/');
        }
        return Constants.OPCODE_NAMES[this.opcode] + " " + str;
    }

    @Override
    public final int getIndex() {
        return this.index;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
        if (this.index > 255 && this.opcode == 18) {
            this.opcode = (short)19;
        }
    }

    @Override
    public Type getType(ConstantPool cpg) {
        switch (cpg.getConstant(this.index).getTag()) {
            case 8: {
                return Type.STRING;
            }
            case 4: {
                return Type.FLOAT;
            }
            case 3: {
                return Type.INT;
            }
            case 5: {
                return Type.LONG;
            }
            case 6: {
                return Type.DOUBLE;
            }
            case 7: {
                String name = cpg.getConstantString_CONSTANTClass(this.index);
                if (!name.startsWith("[")) {
                    StringBuffer sb = new StringBuffer();
                    sb.append("L").append(name).append(";");
                    return Type.getType(sb.toString());
                }
                return Type.getType(name);
            }
        }
        throw new RuntimeException("Unknown or invalid constant type at " + this.index);
    }

    @Override
    public Object getValue(ConstantPool constantPool) {
        Constant constant = constantPool.getConstant(this.index);
        switch (constant.getTag()) {
            case 8: {
                int i = ((ConstantString)constant).getStringIndex();
                constant = constantPool.getConstant(i);
                return ((ConstantUtf8)constant).getValue();
            }
            case 4: {
                return ((ConstantFloat)constant).getValue();
            }
            case 3: {
                return ((ConstantInteger)constant).getValue();
            }
            case 5: {
                return ((ConstantLong)constant).getValue();
            }
            case 6: {
                return ((ConstantDouble)constant).getValue();
            }
        }
        throw new RuntimeException("Unknown or invalid constant type at " + this.index);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InstructionCP)) {
            return false;
        }
        InstructionCP o = (InstructionCP)other;
        return o.opcode == this.opcode && o.index == this.index;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.index;
    }
}

