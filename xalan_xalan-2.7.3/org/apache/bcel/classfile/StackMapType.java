/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ConstantPool;

public final class StackMapType
implements Cloneable {
    public static final StackMapType[] EMPTY_ARRAY = new StackMapType[0];
    private byte type;
    private int index = -1;
    private ConstantPool constantPool;

    public StackMapType(byte type, int index, ConstantPool constantPool) {
        this.type = this.checkType(type);
        this.index = index;
        this.constantPool = constantPool;
    }

    StackMapType(DataInput file, ConstantPool constantPool) throws IOException {
        this(file.readByte(), -1, constantPool);
        if (this.hasIndex()) {
            this.index = file.readUnsignedShort();
        }
        this.constantPool = constantPool;
    }

    private byte checkType(byte type) {
        if (type < 0 || type > 8) {
            throw new ClassFormatException("Illegal type for StackMapType: " + type);
        }
        return type;
    }

    public StackMapType copy() {
        try {
            return (StackMapType)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeByte(this.type);
        if (this.hasIndex()) {
            file.writeShort(this.getIndex());
        }
    }

    public ConstantPool getConstantPool() {
        return this.constantPool;
    }

    public int getIndex() {
        return this.index;
    }

    public byte getType() {
        return this.type;
    }

    public boolean hasIndex() {
        return this.type == 7 || this.type == 8;
    }

    private String printIndex() {
        if (this.type == 7) {
            if (this.index < 0) {
                return ", class=<unknown>";
            }
            return ", class=" + this.constantPool.constantToString(this.index, (byte)7);
        }
        if (this.type == 8) {
            return ", offset=" + this.index;
        }
        return "";
    }

    public void setConstantPool(ConstantPool constantPool) {
        this.constantPool = constantPool;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setType(byte type) {
        this.type = this.checkType(type);
    }

    public String toString() {
        return "(type=" + Const.getItemName(this.type) + this.printIndex() + ")";
    }
}

