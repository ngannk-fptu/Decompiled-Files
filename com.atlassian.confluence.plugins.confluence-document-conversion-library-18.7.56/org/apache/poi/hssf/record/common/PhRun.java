/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.common;

import java.util.Objects;
import org.apache.poi.hssf.record.cont.ContinuableRecordOutput;
import org.apache.poi.util.Internal;
import org.apache.poi.util.LittleEndianInput;

@Internal
public class PhRun {
    final int phoneticTextFirstCharacterOffset;
    final int realTextFirstCharacterOffset;
    final int realTextLength;

    public PhRun(PhRun other) {
        this.phoneticTextFirstCharacterOffset = other.phoneticTextFirstCharacterOffset;
        this.realTextFirstCharacterOffset = other.realTextFirstCharacterOffset;
        this.realTextLength = other.realTextLength;
    }

    public PhRun(int phoneticTextFirstCharacterOffset, int realTextFirstCharacterOffset, int realTextLength) {
        this.phoneticTextFirstCharacterOffset = phoneticTextFirstCharacterOffset;
        this.realTextFirstCharacterOffset = realTextFirstCharacterOffset;
        this.realTextLength = realTextLength;
    }

    PhRun(LittleEndianInput in) {
        this.phoneticTextFirstCharacterOffset = in.readUShort();
        this.realTextFirstCharacterOffset = in.readUShort();
        this.realTextLength = in.readUShort();
    }

    void serialize(ContinuableRecordOutput out) {
        out.writeContinueIfRequired(6);
        out.writeShort(this.phoneticTextFirstCharacterOffset);
        out.writeShort(this.realTextFirstCharacterOffset);
        out.writeShort(this.realTextLength);
    }

    public int hashCode() {
        return Objects.hash(this.phoneticTextFirstCharacterOffset, this.realTextFirstCharacterOffset, this.realTextLength);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        PhRun phRun = (PhRun)o;
        return this.phoneticTextFirstCharacterOffset == phRun.phoneticTextFirstCharacterOffset && this.realTextFirstCharacterOffset == phRun.realTextFirstCharacterOffset && this.realTextLength == phRun.realTextLength;
    }
}

