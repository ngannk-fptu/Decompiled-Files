/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.classfile.ConstantInvokeDynamic;
import org.aspectj.apache.bcel.classfile.ConstantNameAndType;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.InvokeInstruction;
import org.aspectj.apache.bcel.generic.Type;

public final class InvokeDynamic
extends InvokeInstruction {
    public InvokeDynamic(int index, int zeroes) {
        super((short)186, index);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeShort(this.index);
        out.writeShort(0);
    }

    @Override
    public String toString(ConstantPool cp) {
        return super.toString(cp) + " " + this.index;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InvokeDynamic)) {
            return false;
        }
        InvokeDynamic o = (InvokeDynamic)other;
        return o.opcode == this.opcode && o.index == this.index;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.index;
    }

    @Override
    public Type getReturnType(ConstantPool cp) {
        return Type.getReturnType(this.getSignature(cp));
    }

    @Override
    public Type[] getArgumentTypes(ConstantPool cp) {
        return Type.getArgumentTypes(this.getSignature(cp));
    }

    @Override
    public String getSignature(ConstantPool cp) {
        if (this.signature == null) {
            ConstantInvokeDynamic cid = (ConstantInvokeDynamic)cp.getConstant(this.index);
            ConstantNameAndType cnat = (ConstantNameAndType)cp.getConstant(cid.getNameAndTypeIndex());
            this.signature = cp.getConstantUtf8(cnat.getSignatureIndex()).getValue();
        }
        return this.signature;
    }

    @Override
    public String getName(ConstantPool cp) {
        if (this.name == null) {
            ConstantInvokeDynamic cid = (ConstantInvokeDynamic)cp.getConstant(this.index);
            ConstantNameAndType cnat = (ConstantNameAndType)cp.getConstant(cid.getNameAndTypeIndex());
            this.name = cp.getConstantUtf8(cnat.getNameIndex()).getValue();
        }
        return this.name;
    }

    @Override
    public String getClassName(ConstantPool cp) {
        throw new IllegalStateException("there is no classname for invokedynamic");
    }
}

