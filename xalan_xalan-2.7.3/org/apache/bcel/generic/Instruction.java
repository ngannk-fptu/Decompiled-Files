/*
 * Decompiled with CFR 0.152.
 */
package org.apache.bcel.generic;

import java.io.DataOutputStream;
import java.io.IOException;
import org.apache.bcel.Const;
import org.apache.bcel.classfile.ConstantPool;
import org.apache.bcel.generic.ALOAD;
import org.apache.bcel.generic.ANEWARRAY;
import org.apache.bcel.generic.ASTORE;
import org.apache.bcel.generic.BIPUSH;
import org.apache.bcel.generic.BREAKPOINT;
import org.apache.bcel.generic.CHECKCAST;
import org.apache.bcel.generic.ClassGenException;
import org.apache.bcel.generic.ConstantPoolGen;
import org.apache.bcel.generic.DLOAD;
import org.apache.bcel.generic.DSTORE;
import org.apache.bcel.generic.FLOAD;
import org.apache.bcel.generic.FSTORE;
import org.apache.bcel.generic.GETFIELD;
import org.apache.bcel.generic.GETSTATIC;
import org.apache.bcel.generic.GOTO;
import org.apache.bcel.generic.GOTO_W;
import org.apache.bcel.generic.IFEQ;
import org.apache.bcel.generic.IFGE;
import org.apache.bcel.generic.IFGT;
import org.apache.bcel.generic.IFLE;
import org.apache.bcel.generic.IFLT;
import org.apache.bcel.generic.IFNE;
import org.apache.bcel.generic.IFNONNULL;
import org.apache.bcel.generic.IFNULL;
import org.apache.bcel.generic.IF_ACMPEQ;
import org.apache.bcel.generic.IF_ACMPNE;
import org.apache.bcel.generic.IF_ICMPEQ;
import org.apache.bcel.generic.IF_ICMPGE;
import org.apache.bcel.generic.IF_ICMPGT;
import org.apache.bcel.generic.IF_ICMPLE;
import org.apache.bcel.generic.IF_ICMPLT;
import org.apache.bcel.generic.IF_ICMPNE;
import org.apache.bcel.generic.IINC;
import org.apache.bcel.generic.ILOAD;
import org.apache.bcel.generic.IMPDEP1;
import org.apache.bcel.generic.IMPDEP2;
import org.apache.bcel.generic.INSTANCEOF;
import org.apache.bcel.generic.INVOKEDYNAMIC;
import org.apache.bcel.generic.INVOKEINTERFACE;
import org.apache.bcel.generic.INVOKESPECIAL;
import org.apache.bcel.generic.INVOKESTATIC;
import org.apache.bcel.generic.INVOKEVIRTUAL;
import org.apache.bcel.generic.ISTORE;
import org.apache.bcel.generic.InstructionComparator;
import org.apache.bcel.generic.InstructionConst;
import org.apache.bcel.generic.JSR;
import org.apache.bcel.generic.JSR_W;
import org.apache.bcel.generic.LDC;
import org.apache.bcel.generic.LDC2_W;
import org.apache.bcel.generic.LDC_W;
import org.apache.bcel.generic.LLOAD;
import org.apache.bcel.generic.LOOKUPSWITCH;
import org.apache.bcel.generic.LSTORE;
import org.apache.bcel.generic.LocalVariableInstruction;
import org.apache.bcel.generic.MULTIANEWARRAY;
import org.apache.bcel.generic.NEW;
import org.apache.bcel.generic.NEWARRAY;
import org.apache.bcel.generic.PUTFIELD;
import org.apache.bcel.generic.PUTSTATIC;
import org.apache.bcel.generic.RET;
import org.apache.bcel.generic.SIPUSH;
import org.apache.bcel.generic.TABLESWITCH;
import org.apache.bcel.generic.Visitor;
import org.apache.bcel.util.ByteSequence;

public abstract class Instruction
implements Cloneable {
    static final Instruction[] EMPTY_ARRAY = new Instruction[0];
    private static InstructionComparator cmp = InstructionComparator.DEFAULT;
    @Deprecated
    protected short length = 1;
    @Deprecated
    protected short opcode = (short)-1;

    @Deprecated
    public static InstructionComparator getComparator() {
        return cmp;
    }

    public static boolean isValidByte(int value) {
        return value >= -128 && value <= 127;
    }

    public static boolean isValidShort(int value) {
        return value >= Short.MIN_VALUE && value <= Short.MAX_VALUE;
    }

    public static Instruction readInstruction(ByteSequence bytes) throws IOException {
        Instruction instruction;
        boolean wide = false;
        short opcode = (short)bytes.readUnsignedByte();
        Instruction obj = null;
        if (opcode == 196) {
            wide = true;
            opcode = (short)bytes.readUnsignedByte();
        }
        if ((instruction = InstructionConst.getInstruction(opcode)) != null) {
            return instruction;
        }
        switch (opcode) {
            case 16: {
                obj = new BIPUSH();
                break;
            }
            case 17: {
                obj = new SIPUSH();
                break;
            }
            case 18: {
                obj = new LDC();
                break;
            }
            case 19: {
                obj = new LDC_W();
                break;
            }
            case 20: {
                obj = new LDC2_W();
                break;
            }
            case 21: {
                obj = new ILOAD();
                break;
            }
            case 22: {
                obj = new LLOAD();
                break;
            }
            case 23: {
                obj = new FLOAD();
                break;
            }
            case 24: {
                obj = new DLOAD();
                break;
            }
            case 25: {
                obj = new ALOAD();
                break;
            }
            case 26: {
                obj = new ILOAD(0);
                break;
            }
            case 27: {
                obj = new ILOAD(1);
                break;
            }
            case 28: {
                obj = new ILOAD(2);
                break;
            }
            case 29: {
                obj = new ILOAD(3);
                break;
            }
            case 30: {
                obj = new LLOAD(0);
                break;
            }
            case 31: {
                obj = new LLOAD(1);
                break;
            }
            case 32: {
                obj = new LLOAD(2);
                break;
            }
            case 33: {
                obj = new LLOAD(3);
                break;
            }
            case 34: {
                obj = new FLOAD(0);
                break;
            }
            case 35: {
                obj = new FLOAD(1);
                break;
            }
            case 36: {
                obj = new FLOAD(2);
                break;
            }
            case 37: {
                obj = new FLOAD(3);
                break;
            }
            case 38: {
                obj = new DLOAD(0);
                break;
            }
            case 39: {
                obj = new DLOAD(1);
                break;
            }
            case 40: {
                obj = new DLOAD(2);
                break;
            }
            case 41: {
                obj = new DLOAD(3);
                break;
            }
            case 42: {
                obj = new ALOAD(0);
                break;
            }
            case 43: {
                obj = new ALOAD(1);
                break;
            }
            case 44: {
                obj = new ALOAD(2);
                break;
            }
            case 45: {
                obj = new ALOAD(3);
                break;
            }
            case 54: {
                obj = new ISTORE();
                break;
            }
            case 55: {
                obj = new LSTORE();
                break;
            }
            case 56: {
                obj = new FSTORE();
                break;
            }
            case 57: {
                obj = new DSTORE();
                break;
            }
            case 58: {
                obj = new ASTORE();
                break;
            }
            case 59: {
                obj = new ISTORE(0);
                break;
            }
            case 60: {
                obj = new ISTORE(1);
                break;
            }
            case 61: {
                obj = new ISTORE(2);
                break;
            }
            case 62: {
                obj = new ISTORE(3);
                break;
            }
            case 63: {
                obj = new LSTORE(0);
                break;
            }
            case 64: {
                obj = new LSTORE(1);
                break;
            }
            case 65: {
                obj = new LSTORE(2);
                break;
            }
            case 66: {
                obj = new LSTORE(3);
                break;
            }
            case 67: {
                obj = new FSTORE(0);
                break;
            }
            case 68: {
                obj = new FSTORE(1);
                break;
            }
            case 69: {
                obj = new FSTORE(2);
                break;
            }
            case 70: {
                obj = new FSTORE(3);
                break;
            }
            case 71: {
                obj = new DSTORE(0);
                break;
            }
            case 72: {
                obj = new DSTORE(1);
                break;
            }
            case 73: {
                obj = new DSTORE(2);
                break;
            }
            case 74: {
                obj = new DSTORE(3);
                break;
            }
            case 75: {
                obj = new ASTORE(0);
                break;
            }
            case 76: {
                obj = new ASTORE(1);
                break;
            }
            case 77: {
                obj = new ASTORE(2);
                break;
            }
            case 78: {
                obj = new ASTORE(3);
                break;
            }
            case 132: {
                obj = new IINC();
                break;
            }
            case 153: {
                obj = new IFEQ();
                break;
            }
            case 154: {
                obj = new IFNE();
                break;
            }
            case 155: {
                obj = new IFLT();
                break;
            }
            case 156: {
                obj = new IFGE();
                break;
            }
            case 157: {
                obj = new IFGT();
                break;
            }
            case 158: {
                obj = new IFLE();
                break;
            }
            case 159: {
                obj = new IF_ICMPEQ();
                break;
            }
            case 160: {
                obj = new IF_ICMPNE();
                break;
            }
            case 161: {
                obj = new IF_ICMPLT();
                break;
            }
            case 162: {
                obj = new IF_ICMPGE();
                break;
            }
            case 163: {
                obj = new IF_ICMPGT();
                break;
            }
            case 164: {
                obj = new IF_ICMPLE();
                break;
            }
            case 165: {
                obj = new IF_ACMPEQ();
                break;
            }
            case 166: {
                obj = new IF_ACMPNE();
                break;
            }
            case 167: {
                obj = new GOTO();
                break;
            }
            case 168: {
                obj = new JSR();
                break;
            }
            case 169: {
                obj = new RET();
                break;
            }
            case 170: {
                obj = new TABLESWITCH();
                break;
            }
            case 171: {
                obj = new LOOKUPSWITCH();
                break;
            }
            case 178: {
                obj = new GETSTATIC();
                break;
            }
            case 179: {
                obj = new PUTSTATIC();
                break;
            }
            case 180: {
                obj = new GETFIELD();
                break;
            }
            case 181: {
                obj = new PUTFIELD();
                break;
            }
            case 182: {
                obj = new INVOKEVIRTUAL();
                break;
            }
            case 183: {
                obj = new INVOKESPECIAL();
                break;
            }
            case 184: {
                obj = new INVOKESTATIC();
                break;
            }
            case 185: {
                obj = new INVOKEINTERFACE();
                break;
            }
            case 186: {
                obj = new INVOKEDYNAMIC();
                break;
            }
            case 187: {
                obj = new NEW();
                break;
            }
            case 188: {
                obj = new NEWARRAY();
                break;
            }
            case 189: {
                obj = new ANEWARRAY();
                break;
            }
            case 192: {
                obj = new CHECKCAST();
                break;
            }
            case 193: {
                obj = new INSTANCEOF();
                break;
            }
            case 197: {
                obj = new MULTIANEWARRAY();
                break;
            }
            case 198: {
                obj = new IFNULL();
                break;
            }
            case 199: {
                obj = new IFNONNULL();
                break;
            }
            case 200: {
                obj = new GOTO_W();
                break;
            }
            case 201: {
                obj = new JSR_W();
                break;
            }
            case 202: {
                obj = new BREAKPOINT();
                break;
            }
            case 254: {
                obj = new IMPDEP1();
                break;
            }
            case 255: {
                obj = new IMPDEP2();
                break;
            }
            default: {
                throw new ClassGenException("Illegal opcode detected: " + opcode);
            }
        }
        if (wide && !(obj instanceof LocalVariableInstruction) && !(obj instanceof RET)) {
            throw new ClassGenException("Illegal opcode after wide: " + opcode);
        }
        obj.setOpcode(opcode);
        obj.initFromFile(bytes, wide);
        return obj;
    }

    @Deprecated
    public static void setComparator(InstructionComparator c) {
        cmp = c;
    }

    Instruction() {
    }

    public Instruction(short opcode, short length) {
        this.length = length;
        this.opcode = opcode;
    }

    public abstract void accept(Visitor var1);

    public int consumeStack(ConstantPoolGen cpg) {
        return Const.getConsumeStack(this.opcode);
    }

    public Instruction copy() {
        Instruction i = null;
        if (InstructionConst.getInstruction(this.getOpcode()) != null) {
            i = this;
        } else {
            try {
                i = (Instruction)this.clone();
            }
            catch (CloneNotSupportedException e) {
                System.err.println(e);
            }
        }
        return i;
    }

    void dispose() {
    }

    public void dump(DataOutputStream out) throws IOException {
        out.writeByte(this.opcode);
    }

    public boolean equals(Object that) {
        return that instanceof Instruction && cmp.equals(this, (Instruction)that);
    }

    public int getLength() {
        return this.length;
    }

    public String getName() {
        return Const.getOpcodeName(this.opcode);
    }

    public short getOpcode() {
        return this.opcode;
    }

    public int hashCode() {
        return this.opcode;
    }

    protected void initFromFile(ByteSequence bytes, boolean wide) throws IOException {
    }

    public int produceStack(ConstantPoolGen cpg) {
        return Const.getProduceStack(this.opcode);
    }

    final void setLength(int length) {
        this.length = (short)length;
    }

    final void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public String toString() {
        return this.toString(true);
    }

    public String toString(boolean verbose) {
        if (verbose) {
            return this.getName() + "[" + this.opcode + "](" + this.length + ")";
        }
        return this.getName();
    }

    public String toString(ConstantPool cp) {
        return this.toString(false);
    }
}

