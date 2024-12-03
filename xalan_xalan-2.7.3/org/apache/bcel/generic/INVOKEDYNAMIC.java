/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.ExceptionConst;
import org.apache.bcel.classfile.ConstantInvokeDynamic;
import org.apache.bcel.classfile.ConstantNameAndType;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class INVOKEDYNAMIC
extends InvokeInstruction {
    INVOKEDYNAMIC() {
    }

    public INVOKEDYNAMIC(int index) {
        super((short)186, index);
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
        v.visitINVOKEDYNAMIC(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        out.writeShort(super.getIndex());
        out.writeByte(0);
        out.writeByte(0);
    }

    @Override
    public String getClassName(ConstantPoolGen cpg) {
        ConstantPool cp = cpg.getConstantPool();
        ConstantInvokeDynamic cid = cp.getConstant(super.getIndex(), (byte)18, ConstantInvokeDynamic.class);
        return cp.getConstant(cid.getNameAndTypeIndex(), ConstantNameAndType.class).getName(cp);
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_INTERFACE_METHOD_RESOLUTION, ExceptionConst.UNSATISFIED_LINK_ERROR, ExceptionConst.ABSTRACT_METHOD_ERROR, ExceptionConst.ILLEGAL_ACCESS_ERROR, ExceptionConst.INCOMPATIBLE_CLASS_CHANGE_ERROR);
    }

    @Override
    public ReferenceType getReferenceType(ConstantPoolGen cpg) {
        return new ObjectType(Object.class.getName());
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.initFromFile(bytes, wide);
        super.setLength(5);
        bytes.readByte();
        bytes.readByte();
    }
}

