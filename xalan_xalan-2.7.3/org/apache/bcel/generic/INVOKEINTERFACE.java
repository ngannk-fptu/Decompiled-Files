/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.ExceptionConst;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public final class INVOKEINTERFACE
extends InvokeInstruction {
    private int nargs;

    INVOKEINTERFACE() {
    }

    public INVOKEINTERFACE(int index, int nargs) {
        super((short)185, index);
        super.setLength(5);
        if (nargs < 1) {
            throw new ClassGenException("Number of arguments must be > 0 " + nargs);
        }
        this.nargs = nargs;
    }

    @Override
    public void accept(Visitor v) {
        v.visitExceptionThrower(this);
        v.visitTypedInstruction(this);
        v.visitStackConsumer(this);
        v.visitStackProducer(this);
        v.visitLoadClass(this);
        v.visitCPInstruction(this);
        v.visitFieldOrMethod(this);
        v.visitInvokeInstruction(this);
        v.visitINVOKEINTERFACE(this);
    }

    @Override
    public int consumeStack(ConstantPoolGen cpg) {
        return this.nargs;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        out.writeShort(super.getIndex());
        out.writeByte(this.nargs);
        out.writeByte(0);
    }

    public int getCount() {
        return this.nargs;
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_INTERFACE_METHOD_RESOLUTION, ExceptionConst.UNSATISFIED_LINK_ERROR, ExceptionConst.ABSTRACT_METHOD_ERROR, ExceptionConst.ILLEGAL_ACCESS_ERROR, ExceptionConst.INCOMPATIBLE_CLASS_CHANGE_ERROR);
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        super.setLength(5);
        this.nargs = bytes.readUnsignedByte();
        bytes.readByte();
    }

    @Override
    public String toString(ConstantPool cp) {
        return super.toString(cp) + " " + this.nargs;
    }
}

