/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.Arrays;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal(since="3.8 beta 4")
public final class SprmOperation {
    private static final BitField BITFIELD_OP = BitFieldFactory.getInstance(511);
    private static final BitField BITFIELD_SIZECODE = BitFieldFactory.getInstance(57344);
    private static final BitField BITFIELD_SPECIAL = BitFieldFactory.getInstance(512);
    private static final BitField BITFIELD_TYPE = BitFieldFactory.getInstance(7168);
    private static final short SPRM_LONG_PARAGRAPH = -14827;
    private static final short SPRM_LONG_TABLE = -10744;
    public static final int TYPE_PAP = 1;
    public static final int TYPE_CHP = 2;
    public static final int TYPE_PIC = 3;
    public static final int TYPE_SEP = 4;
    public static final int TYPE_TAP = 5;
    private final int _offset;
    private int _gOffset;
    private final byte[] _grpprl;
    private final int _size;
    private final short _value;

    public static int getOperationFromOpcode(short opcode) {
        return BITFIELD_OP.getValue(opcode);
    }

    public static int getTypeFromOpcode(short opcode) {
        return BITFIELD_TYPE.getValue(opcode);
    }

    public SprmOperation(byte[] grpprl, int offset) {
        this._grpprl = grpprl;
        this._value = LittleEndian.getShort(grpprl, offset);
        this._offset = offset;
        this._gOffset = offset + 2;
        this._size = this.initSize(this._value);
    }

    public byte[] toByteArray() {
        return Arrays.copyOfRange(this._grpprl, this._offset, this._offset + this.size());
    }

    public byte[] getGrpprl() {
        return this._grpprl;
    }

    public int getGrpprlOffset() {
        return this._gOffset;
    }

    public int getOperand() {
        switch (this.getSizeCode()) {
            case 0: 
            case 1: {
                return this._grpprl[this._gOffset];
            }
            case 2: 
            case 4: 
            case 5: {
                return LittleEndian.getShort(this._grpprl, this._gOffset);
            }
            case 3: {
                return LittleEndian.getInt(this._grpprl, this._gOffset);
            }
            case 6: {
                int operandLength = this._grpprl[this._gOffset + 1];
                byte[] codeBytes = new byte[4];
                for (int i = 0; i < operandLength; ++i) {
                    if (this._gOffset + i >= this._grpprl.length) continue;
                    codeBytes[i] = this._grpprl[this._gOffset + 1 + i];
                }
                return LittleEndian.getInt(codeBytes, 0);
            }
            case 7: {
                byte[] threeByteInt = new byte[]{this._grpprl[this._gOffset], this._grpprl[this._gOffset + 1], this._grpprl[this._gOffset + 2], 0};
                return LittleEndian.getInt(threeByteInt, 0);
            }
        }
        throw new IllegalArgumentException("SPRM contains an invalid size code");
    }

    public short getOperandShortSigned() {
        int sizeCode = this.getSizeCode();
        if (sizeCode != 2 && sizeCode != 4 && sizeCode != 5) {
            throw new UnsupportedOperationException("Current SPRM doesn't have signed short operand: " + this);
        }
        return LittleEndian.getShort(this._grpprl, this._gOffset);
    }

    public int getOperation() {
        return BITFIELD_OP.getValue(this._value);
    }

    public int getSizeCode() {
        return BITFIELD_SIZECODE.getValue(this._value);
    }

    public int getType() {
        return BITFIELD_TYPE.getValue(this._value);
    }

    private int initSize(short sprm) {
        switch (this.getSizeCode()) {
            case 0: 
            case 1: {
                return 3;
            }
            case 2: 
            case 4: 
            case 5: {
                return 4;
            }
            case 3: {
                return 6;
            }
            case 6: {
                int offset = this._gOffset;
                if (sprm == -10744 || sprm == -14827) {
                    int retVal = (0xFFFF & LittleEndian.getShort(this._grpprl, offset)) + 3;
                    this._gOffset += 2;
                    return retVal;
                }
                return (0xFF & this._grpprl[this._gOffset++]) + 3;
            }
            case 7: {
                return 5;
            }
        }
        throw new IllegalArgumentException("SPRM contains an invalid size code");
    }

    public int size() {
        return this._size;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[SPRM] (0x");
        stringBuilder.append(Integer.toHexString(this._value & 0xFFFF));
        stringBuilder.append("): ");
        try {
            stringBuilder.append(this.getOperand());
        }
        catch (Exception exc) {
            stringBuilder.append("(error)");
        }
        return stringBuilder.toString();
    }
}

