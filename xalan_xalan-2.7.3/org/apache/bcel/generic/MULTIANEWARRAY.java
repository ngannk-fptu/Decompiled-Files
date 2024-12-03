/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.ExceptionConst;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.AllocationInstruction;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class MULTIANEWARRAY
extends CPInstruction
implements LoadClass,
AllocationInstruction,
ExceptionThrower {
    private short dimensions;

    MULTIANEWARRAY() {
    }

    public MULTIANEWARRAY(int index, short dimensions) {
        super((short)197, index);
        if (dimensions < 1) {
            throw new ClassGenException("Invalid dimensions value: " + dimensions);
        }
        this.dimensions = dimensions;
        super.setLength(4);
    }

    @Override
    public void accept(Visitor v) {
        v.visitLoadClass(this);
        v.visitAllocationInstruction(this);
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitMULTIANEWARRAY(this);
    }

    @Override
    public int consumeStack(ConstantPoolGen cpg) {
        return this.dimensions;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        out.writeShort(super.getIndex());
        out.writeByte(this.dimensions);
    }

    public final short getDimensions() {
        return this.dimensions;
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_CLASS_AND_INTERFACE_RESOLUTION, ExceptionConst.ILLEGAL_ACCESS_ERROR, ExceptionConst.NEGATIVE_ARRAY_SIZE_EXCEPTION);
    }

    @Override
    public ObjectType getLoadClassType(ConstantPoolGen cpg) {
        Type t = this.getType(cpg);
        if (t instanceof ArrayType) {
            t = ((ArrayType)t).getBasicType();
        }
        return t instanceof ObjectType ? (ObjectType)t : null;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        this.dimensions = bytes.readByte();
        super.setLength(4);
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + super.getIndex() + " " + this.dimensions;
    }

    @Override
    public String toString(ConstantPool cp) {
        return super.toString(cp) + " " + this.dimensions;
    }
}

