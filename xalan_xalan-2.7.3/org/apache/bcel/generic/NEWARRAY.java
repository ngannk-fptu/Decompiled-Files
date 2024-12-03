/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Const;
import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.AllocationInstruction;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BasicType;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class NEWARRAY
extends Instruction
implements AllocationInstruction,
ExceptionThrower,
StackProducer {
    private byte type;

    NEWARRAY() {
    }

    public NEWARRAY(BasicType type) {
        this(type.getType());
    }

    public NEWARRAY(byte type) {
        super((short)188, (short)2);
        this.type = type;
    }

    @Override
    public void accept(Visitor v) {
        v.visitAllocationInstruction(this);
        v.visitExceptionThrower(this);
        v.visitStackProducer(this);
        v.visitNEWARRAY(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        out.writeByte(this.type);
    }

    @Override
    public Class<?>[] getExceptions() {
        return new Class[]{ExceptionConst.NEGATIVE_ARRAY_SIZE_EXCEPTION};
    }

    public final Type getType() {
        return new ArrayType(BasicType.getType(this.type), 1);
    }

    public final byte getTypecode() {
        return this.type;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        this.type = bytes.readByte();
        super.setLength(2);
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + Const.getTypeName(this.type);
    }
}

