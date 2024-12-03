/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;

public abstract class ArrayInstruction
extends Instruction
implements ExceptionThrower,
TypedInstruction {
    ArrayInstruction() {
    }

    protected ArrayInstruction(short opcode) {
        super(opcode, (short)1);
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_ARRAY_EXCEPTION, new Class[0]);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        short opcode = super.getOpcode();
        switch (opcode) {
            case 46: 
            case 79: {
                return Type.INT;
            }
            case 52: 
            case 85: {
                return Type.CHAR;
            }
            case 51: 
            case 84: {
                return Type.BYTE;
            }
            case 53: 
            case 86: {
                return Type.SHORT;
            }
            case 47: 
            case 80: {
                return Type.LONG;
            }
            case 49: 
            case 82: {
                return Type.DOUBLE;
            }
            case 48: 
            case 81: {
                return Type.FLOAT;
            }
            case 50: 
            case 83: {
                return Type.OBJECT;
            }
        }
        throw new ClassGenException("Unknown case in switch" + opcode);
    }
}

