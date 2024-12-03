/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.util.StringTokenizer;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.classfile.Constant;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.FieldOrMethod;
import org.aspectj.apache.bcel.generic.Type;

public class InvokeInstruction
extends FieldOrMethod {
    public InvokeInstruction(short opcode, int index) {
        super(opcode, index);
    }

    @Override
    public String toString(ConstantPool cp) {
        Constant c = cp.getConstant(this.index);
        StringTokenizer tok = new StringTokenizer(cp.constantToString(c));
        return Constants.OPCODE_NAMES[this.opcode] + " " + tok.nextToken().replace('.', '/') + tok.nextToken();
    }

    @Override
    public int consumeStack(ConstantPool cpg) {
        String signature = this.getSignature(cpg);
        int sum = Type.getArgumentSizes(signature);
        if (this.opcode != 184) {
            ++sum;
        }
        return sum;
    }

    @Override
    public int produceStack(ConstantPool cpg) {
        return this.getReturnType(cpg).getSize();
    }

    @Override
    public Type getType(ConstantPool cpg) {
        return this.getReturnType(cpg);
    }

    public String getMethodName(ConstantPool cpg) {
        return this.getName(cpg);
    }

    public Type getReturnType(ConstantPool cpg) {
        return Type.getReturnType(this.getSignature(cpg));
    }

    public Type[] getArgumentTypes(ConstantPool cpg) {
        return Type.getArgumentTypes(this.getSignature(cpg));
    }
}

