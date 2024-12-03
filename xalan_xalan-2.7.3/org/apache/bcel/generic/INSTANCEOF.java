/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.ExceptionConst;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.CPInstruction;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ExceptionThrower;
import org.apache.bcel.generic.LoadClass;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;

public class INSTANCEOF
extends CPInstruction
implements LoadClass,
ExceptionThrower,
StackProducer,
StackConsumer {
    INSTANCEOF() {
    }

    public INSTANCEOF(int index) {
        super((short)193, index);
    }

    @Override
    public void accept(Visitor v) {
        v.visitLoadClass(this);
        v.visitExceptionThrower(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitTypedInstruction(this);
        v.visitCPInstruction(this);
        v.visitINSTANCEOF(this);
    }

    @Override
    public Class<?>[] getExceptions() {
        return ExceptionConst.createExceptions(ExceptionConst.EXCS.EXCS_CLASS_AND_INTERFACE_RESOLUTION, new Class[0]);
    }

    @Override
    public ObjectType getLoadClassType(ConstantPoolGen cpg) {
        Type t = this.getType(cpg);
        if (t instanceof ArrayType) {
            t = ((ArrayType)t).getBasicType();
        }
        return t instanceof ObjectType ? (ObjectType)t : null;
    }
}

