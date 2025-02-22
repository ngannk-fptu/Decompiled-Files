/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.classfile;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ClassVisitor;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.classfile.ConstantUtf8;
import org.aspectj.apache.bcel.classfile.Node;
import org.aspectj.apache.bcel.classfile.Utility;

public final class InnerClass
implements Cloneable,
Node {
    private int inner_class_index;
    private int outer_class_index;
    private int inner_name_index;
    private int inner_access_flags;

    public InnerClass(InnerClass c) {
        this(c.getInnerClassIndex(), c.getOuterClassIndex(), c.getInnerNameIndex(), c.getInnerAccessFlags());
    }

    InnerClass(DataInputStream file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort());
    }

    public InnerClass(int inner_class_index, int outer_class_index, int inner_name_index, int inner_access_flags) {
        this.inner_class_index = inner_class_index;
        this.outer_class_index = outer_class_index;
        this.inner_name_index = inner_name_index;
        this.inner_access_flags = inner_access_flags;
    }

    @Override
    public void accept(ClassVisitor v) {
        v.visitInnerClass(this);
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.inner_class_index);
        file.writeShort(this.outer_class_index);
        file.writeShort(this.inner_name_index);
        file.writeShort(this.inner_access_flags);
    }

    public final int getInnerAccessFlags() {
        return this.inner_access_flags;
    }

    public final int getInnerClassIndex() {
        return this.inner_class_index;
    }

    public final int getInnerNameIndex() {
        return this.inner_name_index;
    }

    public final int getOuterClassIndex() {
        return this.outer_class_index;
    }

    public final void setInnerAccessFlags(int inner_access_flags) {
        this.inner_access_flags = inner_access_flags;
    }

    public final void setInnerClassIndex(int inner_class_index) {
        this.inner_class_index = inner_class_index;
    }

    public final void setInnerNameIndex(int inner_name_index) {
        this.inner_name_index = inner_name_index;
    }

    public final void setOuterClassIndex(int outer_class_index) {
        this.outer_class_index = outer_class_index;
    }

    public final String toString() {
        return "InnerClass(" + this.inner_class_index + ", " + this.outer_class_index + ", " + this.inner_name_index + ", " + this.inner_access_flags + ")";
    }

    public final String toString(ConstantPool constant_pool) {
        String outer_class_name;
        String inner_class_name = constant_pool.getConstantString(this.inner_class_index, (byte)7);
        inner_class_name = Utility.compactClassName(inner_class_name);
        if (this.outer_class_index != 0) {
            outer_class_name = constant_pool.getConstantString(this.outer_class_index, (byte)7);
            outer_class_name = Utility.compactClassName(outer_class_name);
        } else {
            outer_class_name = "<not a member>";
        }
        String inner_name = this.inner_name_index != 0 ? ((ConstantUtf8)constant_pool.getConstant(this.inner_name_index, (byte)1)).getValue() : "<anonymous>";
        String access = Utility.accessToString(this.inner_access_flags, true);
        access = access.equals("") ? "" : access + " ";
        return "InnerClass:" + access + inner_class_name + "(\"" + outer_class_name + "\", \"" + inner_name + "\")";
    }

    public InnerClass copy() {
        try {
            return (InnerClass)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }
}

