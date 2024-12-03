/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.Attribute;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantDouble;
import org.aspectj.apache.bcel.classfile.ConstantFloat;
import org.aspectj.apache.bcel.classfile.ConstantInteger;
import org.aspectj.apache.bcel.classfile.ConstantLong;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantString;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Utility;

public final class ConstantValue
extends Attribute {
    private int constantvalue_index;

    ConstantValue(int name_index, int length, DataInputStream file, ConstantPool constant_pool) throws IOException {
        this(name_index, length, file.readUnsignedShort(), constant_pool);
    }

    public ConstantValue(int name_index, int length, int constantvalue_index, ConstantPool constant_pool) {
        super((byte)1, name_index, length, constant_pool);
        this.constantvalue_index = constantvalue_index;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitConstantValue(this);
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        super.dump(file);
        file.writeShort(this.constantvalue_index);
    }

    public final int getConstantValueIndex() {
        return this.constantvalue_index;
    }

    @Override
    public final String toString() {
        String buf;
        Constant c = this.cpool.getConstant(this.constantvalue_index);
        switch (c.getTag()) {
            case 5: {
                buf = "" + ((ConstantLong)c).getValue();
                break;
            }
            case 4: {
                buf = "" + ((ConstantFloat)c).getValue();
                break;
            }
            case 6: {
                buf = "" + ((ConstantDouble)c).getValue();
                break;
            }
            case 3: {
                buf = "" + ((ConstantInteger)c).getValue();
                break;
            }
            case 8: {
                int i = ((ConstantString)c).getStringIndex();
                c = this.cpool.getConstant(i, (byte)1);
                buf = "\"" + Utility.convertString(((ConstantUtf8)c).getValue()) + "\"";
                break;
            }
            default: {
                throw new IllegalStateException("Type of ConstValue invalid: " + c);
            }
        }
        return buf;
    }
}

