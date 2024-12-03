/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.Const;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.bcel.generic.Type;

public abstract class FieldInstruction
extends FieldOrMethod {
    FieldInstruction() {
    }

    protected FieldInstruction(short opcode, int index) {
        super(opcode, index);
    }

    public String getFieldName(ConstantPoolGen cpg) {
        return this.getName(cpg);
    }

    protected int getFieldSize(ConstantPoolGen cpg) {
        return Type.size(Type.getTypeSize(this.getSignature(cpg)));
    }

    public Type getFieldType(ConstantPoolGen cpg) {
        return Type.getType(this.getSignature(cpg));
    }

    @Override
    public Type getType(ConstantPoolGen cpg) {
        return this.getFieldType(cpg);
    }

    @Override
    public String toString(ConstantPool cp) {
        return Const.getOpcodeName(super.getOpcode()) + " " + cp.constantToString(super.getIndex(), (byte)9);
    }
}

