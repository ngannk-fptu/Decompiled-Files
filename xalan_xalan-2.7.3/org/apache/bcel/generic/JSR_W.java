/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public class JSR_W
extends JsrInstruction {
    JSR_W() {
    }

    public JSR_W(InstructionHandle target) {
        super((short)201, target);
        super.setLength(5);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitBranchInstruction(this);
        v.visitJsrInstruction(this);
        v.visitJSR_W(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.setIndex(this.getTargetOffset());
        out.writeByte(super.getOpcode());
        out.writeInt(super.getIndex());
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        super.setIndex(bytes.readInt());
        super.setLength(5);
    }
}

