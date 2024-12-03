/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.util.Objects;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.CompoundInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.SIPUSH;
import org.apache.bcel.generic.VariableLengthInstruction;

public final class PUSH
implements CompoundInstruction,
VariableLengthInstruction,
InstructionConstants {
    private final Instruction instruction;

    public PUSH(ConstantPoolGen cp, ArrayType value) {
        this.instruction = value == null ? InstructionConst.ACONST_NULL : new LDC(cp.addArrayClass(value));
    }

    public PUSH(ConstantPoolGen cp, boolean value) {
        Objects.requireNonNull(cp, "cp");
        this.instruction = InstructionConst.getInstruction(3 + (value ? 1 : 0));
    }

    public PUSH(ConstantPoolGen cp, Boolean value) {
        this(cp, (boolean)value);
    }

    public PUSH(ConstantPoolGen cp, Character value) {
        this(cp, (int)value.charValue());
    }

    public PUSH(ConstantPoolGen cp, double value) {
        this.instruction = value == 0.0 ? InstructionConst.DCONST_0 : (value == 1.0 ? InstructionConst.DCONST_1 : new LDC2_W(cp.addDouble(value)));
    }

    public PUSH(ConstantPoolGen cp, float value) {
        this.instruction = (double)value == 0.0 ? InstructionConst.FCONST_0 : ((double)value == 1.0 ? InstructionConst.FCONST_1 : ((double)value == 2.0 ? InstructionConst.FCONST_2 : new LDC(cp.addFloat(value))));
    }

    public PUSH(ConstantPoolGen cp, int value) {
        this.instruction = value >= -1 && value <= 5 ? InstructionConst.getInstruction(3 + value) : (Instruction.isValidByte(value) ? new BIPUSH((byte)value) : (Instruction.isValidShort(value) ? new SIPUSH((short)value) : new LDC(cp.addInteger(value))));
    }

    public PUSH(ConstantPoolGen cp, long value) {
        this.instruction = value == 0L ? InstructionConst.LCONST_0 : (value == 1L ? InstructionConst.LCONST_1 : new LDC2_W(cp.addLong(value)));
    }

    public PUSH(ConstantPoolGen cp, Number value) {
        if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
            this.instruction = new PUSH((ConstantPoolGen)cp, (int)value.intValue()).instruction;
        } else if (value instanceof Double) {
            this.instruction = new PUSH((ConstantPoolGen)cp, (double)value.doubleValue()).instruction;
        } else if (value instanceof Float) {
            this.instruction = new PUSH((ConstantPoolGen)cp, (float)value.floatValue()).instruction;
        } else if (value instanceof Long) {
            this.instruction = new PUSH((ConstantPoolGen)cp, (long)value.longValue()).instruction;
        } else {
            throw new ClassGenException("What's this: " + value);
        }
    }

    public PUSH(ConstantPoolGen cp, ObjectType value) {
        this.instruction = value == null ? InstructionConst.ACONST_NULL : new LDC(cp.addClass(value));
    }

    public PUSH(ConstantPoolGen cp, String value) {
        this.instruction = value == null ? InstructionConst.ACONST_NULL : new LDC(cp.addString(value));
    }

    public Instruction getInstruction() {
        return this.instruction;
    }

    @Override
    public InstructionList getInstructionList() {
        return new InstructionList(this.instruction);
    }

    public String toString() {
        return this.instruction + " (PUSH)";
    }
}

