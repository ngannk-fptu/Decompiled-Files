/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.aspectj.apache.bcel.Constants;
import org.aspectj.apache.bcel.generic.Instruction;
import org.aspectj.apache.bcel.generic.InstructionConstants;

public class InstructionLV
extends Instruction {
    protected int lvar = -1;

    public InstructionLV(short opcode, int lvar) {
        super(opcode);
        this.lvar = lvar;
    }

    public InstructionLV(short opcode) {
        super(opcode);
    }

    @Override
    public void dump(DataOutputStream out) throws IOException {
        if (this.lvar == -1) {
            out.writeByte(this.opcode);
        } else if (this.lvar < 4) {
            if (this.opcode == 25) {
                out.writeByte(42 + this.lvar);
            } else if (this.opcode == 58) {
                out.writeByte(75 + this.lvar);
            } else if (this.opcode == 21) {
                out.writeByte(26 + this.lvar);
            } else if (this.opcode == 54) {
                out.writeByte(59 + this.lvar);
            } else if (this.opcode == 24) {
                out.writeByte(38 + this.lvar);
            } else if (this.opcode == 57) {
                out.writeByte(71 + this.lvar);
            } else if (this.opcode == 23) {
                out.writeByte(34 + this.lvar);
            } else if (this.opcode == 56) {
                out.writeByte(67 + this.lvar);
            } else if (this.opcode == 22) {
                out.writeByte(30 + this.lvar);
            } else if (this.opcode == 55) {
                out.writeByte(63 + this.lvar);
            } else {
                if (this.wide()) {
                    out.writeByte(196);
                }
                out.writeByte(this.opcode);
                if (this.wide()) {
                    out.writeShort(this.lvar);
                } else {
                    out.writeByte(this.lvar);
                }
            }
        } else {
            if (this.wide()) {
                out.writeByte(196);
            }
            out.writeByte(this.opcode);
            if (this.wide()) {
                out.writeShort(this.lvar);
            } else {
                out.writeByte(this.lvar);
            }
        }
    }

    @Override
    public String toString(boolean verbose) {
        if (this.opcode >= 26 && this.opcode <= 45 || this.opcode >= 59 && this.opcode <= 78) {
            return super.toString(verbose);
        }
        return super.toString(verbose) + (this.lvar != -1 && this.lvar < 4 ? "_" : " ") + this.lvar;
    }

    @Override
    public boolean isALOAD() {
        return this.opcode == 25 || this.opcode >= 42 && this.opcode <= 45;
    }

    @Override
    public boolean isASTORE() {
        return this.opcode == 58 || this.opcode >= 75 && this.opcode <= 78;
    }

    public int getBaseOpcode() {
        if (this.opcode >= 21 && this.opcode <= 25 || this.opcode >= 54 && this.opcode <= 58) {
            return this.opcode;
        }
        if (this.opcode >= 26 && this.opcode <= 45) {
            int ret = this.opcode - 26;
            ret -= ret % 4;
            return (ret /= 4) + 21;
        }
        int ret = this.opcode - 59;
        ret -= ret % 4;
        return (ret /= 4) + 54;
    }

    @Override
    public final int getIndex() {
        if (this.lvar != -1) {
            return this.lvar;
        }
        if (this.opcode >= 26 && this.opcode <= 45) {
            return (this.opcode - 26) % 4;
        }
        if (this.opcode >= 59 && this.opcode <= 78) {
            return (this.opcode - 59) % 4;
        }
        return -1;
    }

    @Override
    public void setIndex(int i) {
        if (this.getIndex() != i) {
            if (this.opcode >= 26 && this.opcode <= 45) {
                this.opcode = (short)(21 + (this.opcode - 26) / 4);
            } else if (this.opcode >= 59 && this.opcode <= 78) {
                this.opcode = (short)(54 + (this.opcode - 59) / 4);
            }
            this.lvar = i;
        }
    }

    public boolean canSetIndex() {
        return true;
    }

    public InstructionLV setIndexAndCopyIfNecessary(int newIndex) {
        if (this.canSetIndex()) {
            this.setIndex(newIndex);
            return this;
        }
        if (this.getIndex() == newIndex) {
            return this;
        }
        InstructionLV newInstruction = null;
        int baseOpCode = this.getBaseOpcode();
        newInstruction = newIndex < 4 ? (this.isStoreInstruction() ? (InstructionLV)InstructionConstants.INSTRUCTIONS[(baseOpCode - 54) * 4 + 59 + newIndex] : (InstructionLV)InstructionConstants.INSTRUCTIONS[(baseOpCode - 21) * 4 + 26 + newIndex]) : new InstructionLV((short)baseOpCode, newIndex);
        return newInstruction;
    }

    @Override
    public int getLength() {
        byte size = Constants.iLen[this.opcode];
        if (this.lvar == -1) {
            return size;
        }
        if (this.lvar < 4) {
            if (this.opcode == 25 || this.opcode == 58) {
                return 1;
            }
            if (this.opcode == 21 || this.opcode == 54) {
                return 1;
            }
            if (this.opcode == 24 || this.opcode == 57) {
                return 1;
            }
            if (this.opcode == 23 || this.opcode == 56) {
                return 1;
            }
            if (this.opcode == 22 || this.opcode == 55) {
                return 1;
            }
            if (this.wide()) {
                return size + 2;
            }
            return size;
        }
        if (this.wide()) {
            return size + 2;
        }
        return size;
    }

    private final boolean wide() {
        return this.lvar > 255;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof InstructionLV)) {
            return false;
        }
        InstructionLV o = (InstructionLV)other;
        return o.opcode == this.opcode && o.lvar == this.lvar;
    }

    @Override
    public int hashCode() {
        return this.opcode * 37 + this.lvar;
    }
}

