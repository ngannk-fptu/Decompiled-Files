/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.generic.InstructionHandle;
import org.aspectj.apache.bcel.generic.InstructionSelect;
import org.aspectj.apache.bcel.util.ByteSequence;

public class LOOKUPSWITCH
extends InstructionSelect {
    public LOOKUPSWITCH(int[] match, InstructionHandle[] targets, InstructionHandle target) {
        super((short)171, match, targets, target);
        this.length = (short)(9 + this.matchLength * 8);
        this.fixedLength = this.length;
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        super.dump(out);
        out.writeInt(this.matchLength);
        for (int i = 0; i < this.matchLength; ++i) {
            out.writeInt(this.match[i]);
            this.indices[i] = this.getTargetOffset(this.targets[i]);
            out.writeInt(this.indices[i]);
        }
    }

    public LOOKUPSWITCH(ByteSequence bytes) throws IOException {
        super((short)171, bytes);
        this.matchLength = bytes.readInt();
        this.fixedLength = (short)(9 + this.matchLength * 8);
        this.length = (short)(this.fixedLength + this.padding);
        this.match = new int[this.matchLength];
        this.indices = new int[this.matchLength];
        this.targets = new InstructionHandle[this.matchLength];
        for (int i = 0; i < this.matchLength; ++i) {
            this.match[i] = bytes.readInt();
            this.indices[i] = bytes.readInt();
        }
    }
}

