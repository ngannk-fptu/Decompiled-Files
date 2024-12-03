/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantFloat;
import org.apache.bcel.classfile.ConstantInteger;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.ConstantString;
import org.apache.bcel.classfile.ConstantUtf8;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;
import org.apache.bcel.util.Args;

public final class ConstantValue
extends Attribute {
    private int constantValueIndex;

    public ConstantValue(ConstantValue c) {
        this(c.getNameIndex(), c.getLength(), c.getConstantValueIndex(), c.getConstantPool());
    }

    ConstantValue(int nameIndex, int length, DataInput input, ConstantPool constantPool) throws IOException {
        this(nameIndex, length, input.readUnsignedShort(), constantPool);
    }

    public ConstantValue(int nameIndex, int length, int constantValueIndex, ConstantPool constantPool) {
        super((byte)1, nameIndex, Args.require(length, 2, "ConstantValue attribute length"), constantPool);
        this.constantValueIndex = constantValueIndex;
    }

    @Override
    public void accept(Visitor v) {
        v.visitConstantValue(this);
    }

    @Override
    public Attribute copy(ConstantPool constantPool) {
        ConstantValue c = (ConstantValue)this.clone();
        c.setConstantPool(constantPool);
        return c;
    }

    @Override
    public void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.constantValueIndex);
    }

    public int getConstantValueIndex() {
        return this.constantValueIndex;
    }

    public void setConstantValueIndex(int constantValueIndex) {
        this.constantValueIndex = constantValueIndex;
    }

    @Override
    public String toString() {
        String buf;
        Object c = super.getConstantPool().getConstant(this.constantValueIndex);
        switch (((Constant)c).getTag()) {
            case 5: {
                buf = String.valueOf(((ConstantLong)c).getBytes());
                break;
            }
            case 4: {
                buf = String.valueOf(((ConstantFloat)c).getBytes());
                break;
            }
            case 6: {
                buf = String.valueOf(((ConstantDouble)c).getBytes());
                break;
            }
            case 3: {
                buf = String.valueOf(((ConstantInteger)c).getBytes());
                break;
            }
            case 8: {
                int i = ((ConstantString)c).getStringIndex();
                c = super.getConstantPool().getConstantUtf8(i);
                buf = "\"" + Utility.convertString(((ConstantUtf8)c).getBytes()) + "\"";
                break;
            }
            default: {
                throw new IllegalStateException("Type of ConstValue invalid: " + c);
            }
        }
        return buf;
    }
}

