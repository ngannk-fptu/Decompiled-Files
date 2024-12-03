/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;

public abstract class ArithmeticInstruction
extends Instruction
implements TypedInstruction,
StackProducer,
StackConsumer {
    ArithmeticInstruction() {
    }

    protected ArithmeticInstruction(short opcode) {
        super(opcode, (short)1);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        short opcode = super.getOpcode();
        switch (opcode) {
            case 99: 
            case 103: 
            case 107: 
            case 111: 
            case 115: 
            case 119: {
                return Type.DOUBLE;
            }
            case 98: 
            case 102: 
            case 106: 
            case 110: 
            case 114: 
            case 118: {
                return Type.FLOAT;
            }
            case 96: 
            case 100: 
            case 104: 
            case 108: 
            case 112: 
            case 116: 
            case 120: 
            case 122: 
            case 124: 
            case 126: 
            case 128: 
            case 130: {
                return Type.INT;
            }
            case 97: 
            case 101: 
            case 105: 
            case 109: 
            case 113: 
            case 117: 
            case 121: 
            case 123: 
            case 125: 
            case 127: 
            case 129: 
            case 131: {
                return Type.LONG;
            }
        }
        throw new ClassGenException("Unknown type " + opcode);
    }
}

