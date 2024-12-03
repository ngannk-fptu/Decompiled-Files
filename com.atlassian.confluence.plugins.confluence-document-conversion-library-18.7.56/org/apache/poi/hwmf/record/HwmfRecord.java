/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwmf.record;

import java.io.IOException;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hwmf.draw.HwmfGraphics;
import org.apache.poi.hwmf.record.HwmfRecordType;
import org.apache.poi.util.LittleEndianInputStream;

public interface HwmfRecord
extends GenericRecord {
    public HwmfRecordType getWmfRecordType();

    public int init(LittleEndianInputStream var1, long var2, int var4) throws IOException;

    public void draw(HwmfGraphics var1);

    @Override
    default public Enum<?> getGenericRecordType() {
        return this.getWmfRecordType();
    }
}

