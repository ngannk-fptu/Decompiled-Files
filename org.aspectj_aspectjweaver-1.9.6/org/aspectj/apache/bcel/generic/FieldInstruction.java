/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.FieldOrMethod;
import org.aspectj.apache.bcel.generic.Type;

public class FieldInstruction
extends FieldOrMethod {
    public FieldInstruction(short opcode, int index) {
        super(opcode, index);
    }

    @Override
    public String toString(ConstantPool cp) {
        return Constants.OPCODE_NAMES[this.opcode] + " " + cp.constantToString(this.index, (byte)9);
    }

    protected int getFieldSize(ConstantPool cpg) {
        return Type.getTypeSize(this.getSignature(cpg));
    }

    @Override
    public Type getType(ConstantPool cpg) {
        return this.getFieldType(cpg);
    }

    public Type getFieldType(ConstantPool cpg) {
        return Type.getType(this.getSignature(cpg));
    }

    public String getFieldName(ConstantPool cpg) {
        return this.getName(cpg);
    }

    @Override
    public int produceStack(ConstantPool cpg) {
        if (!this.isStackProducer()) {
            return 0;
        }
        return this.getFieldSize(cpg);
    }

    @Override
    public int consumeStack(ConstantPool cpg) {
        if (!this.isStackConsumer()) {
            return 0;
        }
        if (this.opcode == 180) {
            return 1;
        }
        return this.getFieldSize(cpg) + (this.opcode == 181 ? 1 : 0);
    }
}

