/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.common;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.hssf.record.RecordInputStream;
import org.apache.poi.hssf.record.common.SharedFeature;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianOutput;

public final class FeatSmartTag
implements SharedFeature {
    private byte[] data;

    public FeatSmartTag() {
        this.data = new byte[0];
    }

    public FeatSmartTag(FeatSmartTag other) {
        this.data = other.data == null ? null : (byte[])other.data.clone();
    }

    public FeatSmartTag(RecordInputStream in) {
        this.data = in.readRemainder();
    }

    @Override
    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public int getDataSize() {
        return this.data.length;
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        out.write(this.data);
    }

    @Override
    public FeatSmartTag copy() {
        return new FeatSmartTag(this);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("data", () -> this.data);
    }
}

