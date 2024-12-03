/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.classfile.Visitor;

public final class InnerClass
implements Cloneable,
Node {
    private int innerClassIndex;
    private int outerClassIndex;
    private int innerNameIndex;
    private int innerAccessFlags;

    InnerClass(DataInput file) throws IOException {
        this(file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort(), file.readUnsignedShort());
    }

    public InnerClass(InnerClass c) {
        this(c.getInnerClassIndex(), c.getOuterClassIndex(), c.getInnerNameIndex(), c.getInnerAccessFlags());
    }

    public InnerClass(int innerClassIndex, int outerClassIndex, int innerNameIndex, int innerAccessFlags) {
        this.innerClassIndex = innerClassIndex;
        this.outerClassIndex = outerClassIndex;
        this.innerNameIndex = innerNameIndex;
        this.innerAccessFlags = innerAccessFlags;
    }

    @Override
    public void accept(Visitor v) {
        v.visitInnerClass(this);
    }

    public InnerClass copy() {
        try {
            return (InnerClass)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.innerClassIndex);
        file.writeShort(this.outerClassIndex);
        file.writeShort(this.innerNameIndex);
        file.writeShort(this.innerAccessFlags);
    }

    public int getInnerAccessFlags() {
        return this.innerAccessFlags;
    }

    public int getInnerClassIndex() {
        return this.innerClassIndex;
    }

    public int getInnerNameIndex() {
        return this.innerNameIndex;
    }

    public int getOuterClassIndex() {
        return this.outerClassIndex;
    }

    public void setInnerAccessFlags(int innerAccessFlags) {
        this.innerAccessFlags = innerAccessFlags;
    }

    public void setInnerClassIndex(int innerClassIndex) {
        this.innerClassIndex = innerClassIndex;
    }

    public void setInnerNameIndex(int innerNameIndex) {
        this.innerNameIndex = innerNameIndex;
    }

    public void setOuterClassIndex(int outerClassIndex) {
        this.outerClassIndex = outerClassIndex;
    }

    public String toString() {
        return "InnerClass(" + this.innerClassIndex + ", " + this.outerClassIndex + ", " + this.innerNameIndex + ", " + this.innerAccessFlags + ")";
    }

    public String toString(ConstantPool constantPool) {
        String outerClassName;
        String innerClassName = constantPool.getConstantString(this.innerClassIndex, (byte)7);
        innerClassName = Utility.compactClassName(innerClassName, false);
        if (this.outerClassIndex != 0) {
            outerClassName = constantPool.getConstantString(this.outerClassIndex, (byte)7);
            outerClassName = " of class " + Utility.compactClassName(outerClassName, false);
        } else {
            outerClassName = "";
        }
        String innerName = this.innerNameIndex != 0 ? constantPool.getConstantUtf8(this.innerNameIndex).getBytes() : "(anonymous)";
        String access = Utility.accessToString(this.innerAccessFlags, true);
        access = access.isEmpty() ? "" : access + " ";
        return "  " + access + innerName + "=class " + innerClassName + outerClassName;
    }
}

