/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.StringTokenizer;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantCP;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.FieldOrMethod;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Type;

public abstract class InvokeInstruction
extends FieldOrMethod
implements ExceptionThrower,
StackConsumer,
StackProducer {
    InvokeInstruction() {
    }

    protected InvokeInstruction(short opcode, int index) {
        super(opcode, index);
    }

    @Override
    public int consumeStack(ConstantPoolGen cpg) {
        int sum = super.getOpcode() == 184 || super.getOpcode() == 186 ? 0 : 1;
        String signature = this.getSignature(cpg);
        return sum += Type.getArgumentTypesSize(signature);
    }

    public Type[] getArgumentTypes(ConstantPoolGen cpg) {
        return Type.getArgumentTypes(this.getSignature(cpg));
    }

    @Override
    public String getClassName(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantCP cmr = (ConstantCP)cp.getConstant(super.getIndex());
        String className = cp.getConstantString(cmr.getClassIndex(), (byte)7);
        return Utility.pathToPackage(className);
    }

    public String getMethodName(ConstantPoolGen cpg) {
        return this.getName(cpg);
    }

    public Type getReturnType(ConstantPoolGen cpg) {
        return Type.getReturnType(this.getSignature(cpg));
    }

    @Override
    public Type getType(ConstantPoolGen cpg) {
        return this.getReturnType(cpg);
    }

    @Override
    public int produceStack(ConstantPoolGen cpg) {
        String signature = this.getSignature(cpg);
        return Type.getReturnTypeSize(signature);
    }

    @Override
    public String toString(ConstantPool cp) {
        Object c = cp.getConstant(super.getIndex());
        StringTokenizer tok = new StringTokenizer(cp.constantToString((Constant)c));
        String opcodeName = Const.getOpcodeName(super.getOpcode());
        StringBuilder sb = new StringBuilder(opcodeName);
        if (tok.hasMoreTokens()) {
            sb.append(" ");
            sb.append(Utility.packageToPath(tok.nextToken()));
            if (tok.hasMoreTokens()) {
                sb.append(tok.nextToken());
            }
        }
        return sb.toString();
    }
}

