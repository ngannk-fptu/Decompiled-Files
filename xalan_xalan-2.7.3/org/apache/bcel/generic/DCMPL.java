/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.StackConsumer;
import org.apache.bcel.generic.StackProducer;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.generic.Visitor;

public class DCMPL
extends Instruction
implements TypedInstruction,
StackProducer,
StackConsumer {
    public DCMPL() {
        super((short)151, (short)1);
    }

    @Override
    public void accept(Visitor v) {
        v.visitTypedInstruction(this);
        v.visitStackProducer(this);
        v.visitStackConsumer(this);
        v.visitDCMPL(this);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.DOUBLE;
    }
}

