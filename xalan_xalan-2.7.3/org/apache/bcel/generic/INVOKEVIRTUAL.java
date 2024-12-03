/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.InvokeInstruction;
import org.apache.bcel.generic.Visitor;

public class INVOKEVIRTUAL
extends InvokeInstruction {
    INVOKEVIRTUAL() {
    }

    public INVOKEVIRTUAL(int index) {
        super((short)182, index);
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
        v.visitINVOKEVIRTUAL(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(super.getOpcode());
        out.writeShort(super.getIndex());
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_FIELD_AND_METHOD_RESOLUTION, ExceptionConst.NULL_POINTER_EXCEPTION, ExceptionConst.INCOMPATIBLE_CLASS_CHANGE_ERROR, ExceptionConst.ABSTRACT_METHOD_ERROR, ExceptionConst.UNSATISFIED_LINK_ERROR);
    }
}

