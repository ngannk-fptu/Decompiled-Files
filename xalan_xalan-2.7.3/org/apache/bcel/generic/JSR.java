/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.JsrInstruction;
import org.apache.bcel.generic.VariableLengthInstruction;
import org.apache.bcel.generic.Visitor;

public class JSR
extends JsrInstruction
implements VariableLengthInstruction {
    JSR() {
    }

    public JSR(InstructionHandle target) {
        super((short)168, target);
    }

    @Override
    public void accept(Visitor v) {
        v.visitStackProducer(this);
        v.visitVariableLengthInstruction(this);
        v.visitBranchInstruction(this);
        v.visitJsrInstruction(this);
        v.visitJSR(this);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.setIndex(this.getTargetOffset());
        if (super.getOpcode() == 168) {
            super.dump(out);
        } else {
            super.setIndex(this.getTargetOffset());
            out.writeByte(super.getOpcode());
            out.writeInt(super.getIndex());
        }
    }

    @Override
    protected int updatePosition(int offset, int maxOffset) {
        int i = this.getTargetOffset();
        this.setPosition(this.getPosition() + offset);
        if (Math.abs(i) >= Short.MAX_VALUE - maxOffset) {
            super.setOpcode((short)201);
            short oldLength = (short)super.getLength();
            super.setLength(5);
            return super.getLength() - oldLength;
        }
        return 0;
    }
}

