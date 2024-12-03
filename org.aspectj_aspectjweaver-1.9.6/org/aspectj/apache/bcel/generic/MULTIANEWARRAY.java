/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.aspectj.apache.bcel.classfile.ConstantPool;
import org.aspectj.apache.bcel.generic.ArrayType;
import org.aspectj.apache.bcel.generic.InstructionCP;
import org.aspectj.apache.bcel.generic.ObjectType;
import org.aspectj.apache.bcel.generic.Type;

public class MULTIANEWARRAY
extends InstructionCP {
    private short dimensions;

    public MULTIANEWARRAY(int index, short dimensions) {
        super((short)197, index);
        this.dimensions = dimensions;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
        out.writeShort(this.index);
        out.writeByte(this.dimensions);
    }

    public final short getDimensions() {
        return this.dimensions;
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.index + " " + this.dimensions;
    }

    @Override
    public String toString(ConstantPool cp) {
        return super.toString(cp) + " " + this.dimensions;
    }

    @Override
    public int consumeStack(ConstantPool cpg) {
        return this.dimensions;
    }

    public Class[] getExceptions() {
        Class[] cs = new Class[2 + ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length];
        System.arraycopy(ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION, 0, cs, 0, ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length);
        cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length + 1] = ExceptionConstants.NEGATIVE_ARRAY_SIZE_EXCEPTION;
        cs[ExceptionConstants.EXCS_CLASS_AND_INTERFACE_RESOLUTION.length] = ExceptionConstants.ILLEGAL_ACCESS_ERROR;
        return cs;
    }

    @Override
    public ObjectType getLoadClassType(ConstantPool cpg) {
        Type t = this.getType(cpg);
        if (t instanceof ArrayType) {
            t = ((ArrayType)t).getBasicType();
        }
        return t instanceof ObjectType ? (ObjectType)t : null;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof MULTIANEWARRAY)) {
            return false;
        }
        MULTIANEWARRAY o = (MULTIANEWARRAY)other;
        return o.opcode == this.opcode && o.index == this.index && o.dimensions == this.dimensions;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.index * (this.dimensions + 17);
    }
}

