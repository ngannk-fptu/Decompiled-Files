/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.cf;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ColorGradientThreshold
extends Threshold
implements Duplicatable,
GenericRecord {
    private double position;

    public ColorGradientThreshold() {
        this.position = 0.0;
    }

    public ColorGradientThreshold(ColorGradientThreshold other) {
        super(other);
        this.position = other.position;
    }

    public ColorGradientThreshold(LittleEndianInput in) {
        super(in);
        this.position = in.readDouble();
    }

    public double getPosition() {
        return this.position;
    }

    public void setPosition(double position) {
        this.position = position;
    }

    @Override
    public int getDataLength() {
        return super.getDataLength() + 8;
    }

    @Override
    public ColorGradientThreshold copy() {
        return new ColorGradientThreshold(this);
    }

    @Override
    public void serialize(LittleEndianOutput out) {
        super.serialize(out);
        out.writeDouble(this.position);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("position", this::getPosition);
    }
}

