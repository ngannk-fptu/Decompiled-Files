/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.ConstantPushInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class BIPUSH
extends Instruction
implements ConstantPushInstruction {
    private byte b;

    BIPUSH() {
    }

    public BIPUSH(byte b) {
        super((short)16, (short)2);
        this.b = b;
    }

    @Override
    public void accept(Visitor v) {
        v.visitPushInstruction(this);
        v.visitStackProducer(this);
        v.visitTypedInstruction(this);
        v.visitConstantPushInstruction(this);
        v.visitBIPUSH(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.dump(out);
        out.writeByte(this.b);
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        return Type.BYTE;
    }

    @Override
    public Number getValue() {
        return (int)this.b;
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.setLength(2);
        this.b = bytes.readByte();
    }

    @Override
    public String toString(boolean verbose) {
        return super.toString(verbose) + " " + this.b;
    }
}

