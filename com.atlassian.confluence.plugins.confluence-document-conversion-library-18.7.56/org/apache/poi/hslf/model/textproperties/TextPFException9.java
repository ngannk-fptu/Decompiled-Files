/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.sl.usermodel.AutoNumberingScheme;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndian;

public class TextPFException9
implements GenericRecord {
    private static final AutoNumberingScheme DEFAULT_AUTONUMBER_SCHEME = AutoNumberingScheme.arabicPeriod;
    private static final Short DEFAULT_START_NUMBER = 1;
    private final byte mask3;
    private final byte mask4;
    private final Short bulletBlipRef;
    private final Short fBulletHasAutoNumber;
    private final AutoNumberingScheme autoNumberScheme;
    private final Short autoNumberStartNumber;
    private final int recordLength;

    public TextPFException9(byte[] source, int startIndex) {
        this.mask3 = source[startIndex + 2];
        this.mask4 = source[startIndex + 3];
        int length = 4;
        int index = startIndex + 4;
        if (0 == (this.mask3 & 0xFFFFFF80)) {
            this.bulletBlipRef = null;
        } else {
            this.bulletBlipRef = LittleEndian.getShort(source, index);
            index += 2;
            length = 6;
        }
        if (0 == (this.mask4 & 2)) {
            this.fBulletHasAutoNumber = null;
        } else {
            this.fBulletHasAutoNumber = LittleEndian.getShort(source, index);
            index += 2;
            length += 2;
        }
        if (0 == (this.mask4 & 1)) {
            this.autoNumberScheme = null;
            this.autoNumberStartNumber = null;
        } else {
            this.autoNumberScheme = AutoNumberingScheme.forNativeID(LittleEndian.getShort(source, index));
            this.autoNumberStartNumber = LittleEndian.getShort(source, index += 2);
            index += 2;
            length += 4;
        }
        this.recordLength = length;
    }

    public Short getBulletBlipRef() {
        return this.bulletBlipRef;
    }

    public Short getfBulletHasAutoNumber() {
        return this.fBulletHasAutoNumber;
    }

    public AutoNumberingScheme getAutoNumberScheme() {
        if (this.autoNumberScheme != null) {
            return this.autoNumberScheme;
        }
        return this.hasBulletAutoNumber() ? DEFAULT_AUTONUMBER_SCHEME : null;
    }

    public Short getAutoNumberStartNumber() {
        if (this.autoNumberStartNumber != null) {
            return this.autoNumberStartNumber;
        }
        return this.hasBulletAutoNumber() ? DEFAULT_START_NUMBER : null;
    }

    private boolean hasBulletAutoNumber() {
        Short one = 1;
        return one.equals(this.fBulletHasAutoNumber);
    }

    public int getRecordLength() {
        return this.recordLength;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("Record length: ").append(this.recordLength).append(" bytes\n");
        sb.append("bulletBlipRef: ").append(this.bulletBlipRef).append("\n");
        sb.append("fBulletHasAutoNumber: ").append(this.fBulletHasAutoNumber).append("\n");
        sb.append("autoNumberScheme: ").append((Object)this.autoNumberScheme).append("\n");
        sb.append("autoNumberStartNumber: ").append(this.autoNumberStartNumber).append("\n");
        return sb.toString();
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("bulletBlipRef", this::getBulletBlipRef, "bulletHasAutoNumber", this::hasBulletAutoNumber, "autoNumberScheme", this::getAutoNumberScheme, "autoNumberStartNumber", this::getAutoNumberStartNumber);
    }
}

