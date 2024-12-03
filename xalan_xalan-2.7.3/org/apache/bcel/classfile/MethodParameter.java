/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.classfile;

import java.io.DataInput;
import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Node;
import org.apache.bcel.classfile.Visitor;

public class MethodParameter
implements Cloneable,
Node {
    private int nameIndex;
    private int accessFlags;

    public MethodParameter() {
    }

    MethodParameter(DataInput input) throws IOException {
        this.nameIndex = input.readUnsignedShort();
        this.accessFlags = input.readUnsignedShort();
    }

    @Override
    public void accept(Visitor v) {
        v.visitMethodParameter(this);
    }

    public MethodParameter copy() {
        try {
            return (MethodParameter)this.clone();
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            return null;
        }
    }

    public final void dump(DataOutputStream file) throws IOException {
        file.writeShort(this.nameIndex);
        file.writeShort(this.accessFlags);
    }

    public int getAccessFlags() {
        return this.accessFlags;
    }

    public int getNameIndex() {
        return this.nameIndex;
    }

    public String getParameterName(ConstantPool constantPool) {
        if (this.nameIndex == 0) {
            return null;
        }
        return constantPool.getConstantUtf8(this.nameIndex).getBytes();
    }

    public boolean isFinal() {
        return (this.accessFlags & 0x10) != 0;
    }

    public boolean isMandated() {
        return (this.accessFlags & Short.MIN_VALUE) != 0;
    }

    public boolean isSynthetic() {
        return (this.accessFlags & 0x1000) != 0;
    }

    public void setAccessFlags(int accessFlags) {
        this.accessFlags = accessFlags;
    }

    public void setNameIndex(int nameIndex) {
        this.nameIndex = nameIndex;
    }
}

