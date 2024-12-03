/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.sprm;

import java.util.Arrays;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.hwpf.sprm.SprmIterator;
import org.apache.poi.hwpf.sprm.SprmOperation;
import org.apache.poi.hwpf.sprm.SprmUtils;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndian;

@Internal
public final class SprmBuffer
implements Duplicatable {
    byte[] _buf;
    boolean _istd;
    int _offset;
    private final int _sprmsStartOffset;

    public SprmBuffer(SprmBuffer other) {
        this._buf = other._buf == null ? null : (byte[])other._buf.clone();
        this._istd = other._istd;
        this._offset = other._offset;
        this._sprmsStartOffset = other._sprmsStartOffset;
    }

    public SprmBuffer(byte[] buf, boolean istd, int sprmsStartOffset) {
        this._offset = buf.length;
        this._buf = buf;
        this._istd = istd;
        this._sprmsStartOffset = sprmsStartOffset;
    }

    public SprmBuffer(byte[] buf, int _sprmsStartOffset) {
        this(buf, false, _sprmsStartOffset);
    }

    public SprmBuffer(int sprmsStartOffset) {
        this._buf = IOUtils.safelyAllocate((long)sprmsStartOffset + 4L, SprmUtils.MAX_RECORD_LENGTH);
        this._offset = sprmsStartOffset;
        this._sprmsStartOffset = sprmsStartOffset;
    }

    public void addSprm(short opcode, byte operand) {
        int addition = 3;
        this.ensureCapacity(addition);
        LittleEndian.putShort(this._buf, this._offset, opcode);
        this._offset += 2;
        this._buf[this._offset++] = operand;
    }

    public void addSprm(short opcode, byte[] operand) {
        int addition = 3 + operand.length;
        this.ensureCapacity(addition);
        LittleEndian.putShort(this._buf, this._offset, opcode);
        this._offset += 2;
        this._buf[this._offset++] = (byte)operand.length;
        System.arraycopy(operand, 0, this._buf, this._offset, operand.length);
    }

    public void addSprm(short opcode, int operand) {
        int addition = 6;
        this.ensureCapacity(addition);
        LittleEndian.putShort(this._buf, this._offset, opcode);
        this._offset += 2;
        LittleEndian.putInt(this._buf, this._offset, operand);
        this._offset += 4;
    }

    public void addSprm(short opcode, short operand) {
        int addition = 4;
        this.ensureCapacity(addition);
        LittleEndian.putShort(this._buf, this._offset, opcode);
        this._offset += 2;
        LittleEndian.putShort(this._buf, this._offset, operand);
        this._offset += 2;
    }

    public void append(byte[] grpprl) {
        this.append(grpprl, 0);
    }

    public void append(byte[] grpprl, int offset) {
        this.ensureCapacity(grpprl.length - offset);
        System.arraycopy(grpprl, offset, this._buf, this._offset, grpprl.length - offset);
        this._offset += grpprl.length - offset;
    }

    @Override
    public SprmBuffer copy() {
        return new SprmBuffer(this);
    }

    private void ensureCapacity(int addition) {
        if (this._offset + addition >= this._buf.length) {
            IOUtils.safelyAllocateCheck((long)this._offset + (long)addition, SprmUtils.MAX_RECORD_LENGTH);
            this._buf = Arrays.copyOf(this._buf, this._offset + addition);
        }
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof SprmBuffer)) {
            return false;
        }
        SprmBuffer sprmBuf = (SprmBuffer)obj;
        return Arrays.equals(this._buf, sprmBuf._buf);
    }

    public int hashCode() {
        assert (false) : "hashCode not designed";
        return 42;
    }

    public SprmOperation findSprm(short opcode) {
        int operation = SprmOperation.getOperationFromOpcode(opcode);
        int type = SprmOperation.getTypeFromOpcode(opcode);
        SprmIterator si = new SprmIterator(this._buf, 2);
        while (si.hasNext()) {
            SprmOperation i = si.next();
            if (i.getOperation() != operation || i.getType() != type) continue;
            return i;
        }
        return null;
    }

    private int findSprmOffset(short opcode) {
        SprmOperation sprmOperation = this.findSprm(opcode);
        if (sprmOperation == null) {
            return -1;
        }
        return sprmOperation.getGrpprlOffset();
    }

    public byte[] toByteArray() {
        return this._buf;
    }

    public SprmIterator iterator() {
        return new SprmIterator(this._buf, this._sprmsStartOffset);
    }

    public void updateSprm(short opcode, byte operand) {
        int grpprlOffset = this.findSprmOffset(opcode);
        if (grpprlOffset != -1) {
            this._buf[grpprlOffset] = operand;
            return;
        }
        this.addSprm(opcode, operand);
    }

    public void updateSprm(short opcode, boolean operand) {
        int grpprlOffset = this.findSprmOffset(opcode);
        if (grpprlOffset != -1) {
            this._buf[grpprlOffset] = (byte)(operand ? 1 : 0);
            return;
        }
        this.addSprm(opcode, operand ? 1 : 0);
    }

    public void updateSprm(short opcode, int operand) {
        int grpprlOffset = this.findSprmOffset(opcode);
        if (grpprlOffset != -1) {
            LittleEndian.putInt(this._buf, grpprlOffset, operand);
            return;
        }
        this.addSprm(opcode, operand);
    }

    public void updateSprm(short opcode, short operand) {
        int grpprlOffset = this.findSprmOffset(opcode);
        if (grpprlOffset != -1) {
            LittleEndian.putShort(this._buf, grpprlOffset, operand);
            return;
        }
        this.addSprm(opcode, operand);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Sprms (");
        stringBuilder.append(this._buf.length);
        stringBuilder.append(" byte(s)): ");
        SprmIterator iterator = this.iterator();
        while (iterator.hasNext()) {
            try {
                stringBuilder.append(iterator.next());
            }
            catch (Exception exc) {
                stringBuilder.append("error");
            }
            stringBuilder.append("; ");
        }
        return stringBuilder.toString();
    }
}

