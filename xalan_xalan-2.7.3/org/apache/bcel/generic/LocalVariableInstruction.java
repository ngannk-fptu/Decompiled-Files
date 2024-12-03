/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.IndexedInstruction;
import org.apache.bcel.generic.Instruction;
import org.apache.bcel.generic.Type;
import org.apache.bcel.generic.TypedInstruction;
import org.apache.bcel.util.ByteSequence;

public abstract class LocalVariableInstruction
extends Instruction
implements TypedInstruction,
IndexedInstruction {
    @Deprecated
    protected int n = -1;
    private short cTag = (short)-1;
    private short canonTag = (short)-1;

    LocalVariableInstruction() {
    }

    LocalVariableInstruction(short canonTag, short cTag) {
        this.canonTag = canonTag;
        this.cTag = cTag;
    }

    protected LocalVariableInstruction(short opcode, short cTag, int n) {
        super(opcode, (short)2);
        this.cTag = cTag;
        this.canonTag = opcode;
        this.setIndex(n);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.wide()) {
            out.writeByte(196);
        }
        out.writeByte(super.getOpcode());
        if (super.getLength() > 1) {
            if (this.wide()) {
                out.writeShort(this.n);
            } else {
                out.writeByte(this.n);
            }
        }
    }

    public short getCanonicalTag() {
        return this.canonTag;
    }

    @Override
    public final int getIndex() {
        return this.n;
    }

    @Override
    public Type getType(ConstantPoolGen cp) {
        switch (this.canonTag) {
            case 21: 
            case 54: {
                return Type.INT;
            }
            case 22: 
            case 55: {
                return Type.LONG;
            }
            case 24: 
            case 57: {
                return Type.DOUBLE;
            }
            case 23: 
            case 56: {
                return Type.FLOAT;
            }
            case 25: 
            case 58: {
                return Type.OBJECT;
            }
        }
        throw new ClassGenException("Unknown case in switch" + this.canonTag);
    }

    @Override
    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
        if (wide) {
            this.n = bytes.readUnsignedShort();
            super.setLength(4);
        } else {
            short opcode = super.getOpcode();
            if (opcode >= 21 && opcode <= 25 || opcode >= 54 && opcode <= 58) {
                this.n = bytes.readUnsignedByte();
                super.setLength(2);
            } else {
                this.n = opcode <= 45 ? (opcode - 26) % 4 : (opcode - 59) % 4;
                super.setLength(1);
            }
        }
    }

    @Override
    public void setIndex(int n) {
        if (n < 0 || n > 65535) {
            throw new ClassGenException("Illegal value: " + n);
        }
        this.n = n;
        if (n <= 3) {
            super.setOpcode((short)(this.cTag + n));
            super.setLength(1);
        } else {
            super.setOpcode(this.canonTag);
            if (this.wide()) {
                super.setLength(4);
            } else {
                super.setLength(2);
            }
        }
    }

    final void setIndexOnly(int n) {
        this.n = n;
    }

    @Override
    public String toString(boolean verbose) {
        short opcode = super.getOpcode();
        if (opcode >= 26 && opcode <= 45 || opcode >= 59 && opcode <= 78) {
            return super.toString(verbose);
        }
        return super.toString(verbose) + " " + this.n;
    }

    private boolean wide() {
        return this.n > 255;
    }
}

