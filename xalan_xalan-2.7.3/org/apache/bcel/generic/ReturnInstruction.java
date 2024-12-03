/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;

public abstract class ReturnInstruction
extends Instruction
implements ExceptionThrower,
TypedInstruction,
StackConsumer {
    ReturnInstruction() {
    }

    protected ReturnInstruction(short opcode) {
        super(opcode, (short)1);
    }

    @Override
    public Class<?>[] getExceptions() {
        return new Class[]{ExceptionConst.ILLEGAL_MONITOR_STATE};
    }

    public Type getType() {
        short opcode = super.getOpcode();
        switch (opcode) {
            case 172: {
                return Type.INT;
            }
            case 173: {
                return Type.LONG;
            }
            case 174: {
                return Type.FLOAT;
            }
            case 175: {
                return Type.DOUBLE;
            }
            case 176: {
                return Type.OBJECT;
            }
            case 177: {
                return Type.VOID;
            }
        }
        throw new ClassGenException("Unknown type " + opcode);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return this.getType();
    }
}

