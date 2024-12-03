/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.ExceptionConst;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class LDC
extends CPInstruction
implements PushInstruction,
ExceptionThrower {
    LDC() {
    }

    public LDC(int index) {
        super((short)19, index);
        this.setSize();
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitLDC(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        if (super.getLength() == 2) {
            out.writeByte(super.getIndex());
        } else {
            out.writeShort(super.getIndex());
        }
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_STRING_RESOLUTION, new Class[0]);
    }

    @Override
    public Type getType(ConstantPoolGen cpg) {
        switch (((Constant)cpg.getConstantPool().getConstant(super.getIndex())).getTag()) {
            case 8: {
                return Type.STRING;
            }
            case 4: {
                return Type.FLOAT;
            }
            case 3: {
                return Type.INT;
            }
            case 7: {
                return Type.CLASS;
            }
        }
        throw new IllegalArgumentException("Unknown or invalid constant type at " + super.getIndex());
    }

    public Object getValue(ConstantPoolGen cpg) {
        Object c = cpg.getConstantPool().getConstant(super.getIndex());
        switch (((Constant)c).getTag()) {
            case 8: {
                int i = ((ConstantString)c).getStringIndex();
                c = cpg.getConstantPool().getConstant(i);
                return ((ConstantUtf8)c).getBytes();
            }
            case 4: {
                return Float.valueOf(((ConstantFloat)c).getBytes());
            }
            case 3: {
                return ((ConstantInteger)c).getBytes();
            }
            case 7: {
                int nameIndex = ((ConstantClass)c).getNameIndex();
                c = cpg.getConstantPool().getConstant(nameIndex);
                return Type.getType(((ConstantUtf8)c).getBytes());
            }
        }
        throw new IllegalArgumentException("Unknown or invalid constant type at " + super.getIndex());
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.setLength(2);
        super.setIndex(bytes.readUnsignedByte());
    }

    @Override
    public final void setIndex(int index) {
        super.setIndex(index);
        this.setSize();
    }

    protected final void setSize() {
        if (super.getIndex() <= 255) {
            super.setOpcode((short)18);
            super.setLength(2);
        } else {
            super.setOpcode((short)19);
            super.setLength(3);
        }
    }
}

