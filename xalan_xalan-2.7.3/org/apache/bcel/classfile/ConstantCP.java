/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantPool;

public abstract class ConstantCP
extends Constant {
    @Deprecated
    protected int class_index;
    @Deprecated
    protected int name_and_type_index;

    ConstantCP(byte tag, DataInput file) throws IOException {
        this(tag, file.readUnsignedShort(), file.readUnsignedShort());
    }

    protected ConstantCP(byte tag, int classIndex, int nameAndTypeIndex) {
        super(tag);
        this.class_index = classIndex;
        this.name_and_type_index = nameAndTypeIndex;
    }

    public ConstantCP(ConstantCP c) {
        this(c.getTag(), c.getClassIndex(), c.getNameAndTypeIndex());
    }

    @Override
    public final void dump(DataOutputStream file) throws IOException {
        file.writeByte(super.getTag());
        file.writeShort(this.class_index);
        file.writeShort(this.name_and_type_index);
    }

    public String getClass(ConstantPool cp) {
        return cp.constantToString(this.class_index, (byte)7);
    }

    public final int getClassIndex() {
        return this.class_index;
    }

    public final int getNameAndTypeIndex() {
        return this.name_and_type_index;
    }

    public final void setClassIndex(int classIndex) {
        this.class_index = classIndex;
    }

    public final void setNameAndTypeIndex(int nameAndTypeIndex) {
        this.name_and_type_index = nameAndTypeIndex;
    }

    @Override
    public String toString() {
        return super.toString() + "(class_index = " + this.class_index + ", name_and_type_index = " + this.name_and_type_index + ")";
    }
}

