/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantDouble;
import org.apache.bcel.classfile.ConstantLong;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.PushInstruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;

public class LDC2_W
extends CPInstruction
implements PushInstruction {
    LDC2_W() {
    }

    public LDC2_W(int index) {
        super((short)20, index);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitPushInstruction(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitLDC2_W(this);
    }

    @Override
    public Type getType(ConstantPoolGen cpg) {
        switch (((Constant)cpg.getConstantPool().getConstant(super.getIndex())).getTag()) {
            case 5: {
                return Type.LONG;
            }
            case 6: {
                return Type.DOUBLE;
            }
        }
        throw new IllegalArgumentException("Unknown constant type " + super.getOpcode());
    }

    public Number getValue(ConstantPoolGen cpg) {
        Object c = cpg.getConstantPool().getConstant(super.getIndex());
        switch (((Constant)c).getTag()) {
            case 5: {
                return ((ConstantLong)c).getBytes();
            }
            case 6: {
                return ((ConstantDouble)c).getBytes();
            }
        }
        throw new IllegalArgumentException("Unknown or invalid constant type at " + super.getIndex());
    }
}

