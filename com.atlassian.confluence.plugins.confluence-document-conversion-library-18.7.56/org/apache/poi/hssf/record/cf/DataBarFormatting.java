/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.cf;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.cf.DataBarThreshold;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class DataBarFormatting
implements Duplicatable,
GenericRecord {
    private static final Logger LOG = LogManager.getLogger(DataBarFormatting.class);
    private static final BitField ICON_ONLY = BitFieldFactory.getInstance(1);
    private static final BitField REVERSED = BitFieldFactory.getInstance(4);
    private byte options;
    private byte percentMin;
    private byte percentMax;
    private ExtendedColor color;
    private DataBarThreshold thresholdMin;
    private DataBarThreshold thresholdMax;

    public DataBarFormatting() {
        this.options = (byte)2;
    }

    public DataBarFormatting(DataBarFormatting other) {
        this.options = other.options;
        this.percentMin = other.percentMin;
        this.percentMax = other.percentMax;
        this.color = other.color == null ? null : other.color.copy();
        this.thresholdMin = other.thresholdMin == null ? null : other.thresholdMin.copy();
        this.thresholdMax = other.thresholdMax == null ? null : other.thresholdMax.copy();
    }

    public DataBarFormatting(LittleEndianInput in) {
        in.readShort();
        in.readByte();
        this.options = in.readByte();
        this.percentMin = in.readByte();
        this.percentMax = in.readByte();
        if (this.percentMin < 0 || this.percentMin > 100) {
            LOG.atWarn().log("Inconsistent Minimum Percentage found {}", (Object)Unbox.box(this.percentMin));
        }
        if (this.percentMax < 0 || this.percentMax > 100) {
            LOG.atWarn().log("Inconsistent Maximum Percentage found {}", (Object)Unbox.box(this.percentMax));
        }
        this.color = new ExtendedColor(in);
        this.thresholdMin = new DataBarThreshold(in);
        this.thresholdMax = new DataBarThreshold(in);
    }

    public boolean isIconOnly() {
        return this.getOptionFlag(ICON_ONLY);
    }

    public void setIconOnly(boolean only) {
        this.setOptionFlag(only, ICON_ONLY);
    }

    public boolean isReversed() {
        return this.getOptionFlag(REVERSED);
    }

    public void setReversed(boolean rev) {
        this.setOptionFlag(rev, REVERSED);
    }

    private boolean getOptionFlag(BitField field) {
        int value = field.getValue(this.options);
        return value != 0;
    }

    private void setOptionFlag(boolean option, BitField field) {
        this.options = field.setByteBoolean(this.options, option);
    }

    public byte getPercentMin() {
        return this.percentMin;
    }

    public void setPercentMin(byte percentMin) {
        this.percentMin = percentMin;
    }

    public byte getPercentMax() {
        return this.percentMax;
    }

    public void setPercentMax(byte percentMax) {
        this.percentMax = percentMax;
    }

    public ExtendedColor getColor() {
        return this.color;
    }

    public void setColor(ExtendedColor color) {
        this.color = color;
    }

    public DataBarThreshold getThresholdMin() {
        return this.thresholdMin;
    }

    public void setThresholdMin(DataBarThreshold thresholdMin) {
        this.thresholdMin = thresholdMin;
    }

    public DataBarThreshold getThresholdMax() {
        return this.thresholdMax;
    }

    public void setThresholdMax(DataBarThreshold thresholdMax) {
        this.thresholdMax = thresholdMax;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("options", GenericRecordUtil.getBitsAsString(() -> this.options, new BitField[]{ICON_ONLY, REVERSED}, new String[]{"ICON_ONLY", "REVERSED"}), "color", this::getColor, "percentMin", this::getPercentMin, "percentMax", this::getPercentMax, "thresholdMin", this::getThresholdMin, "thresholdMax", this::getThresholdMax);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public DataBarFormatting copy() {
        return new DataBarFormatting(this);
    }

    public int getDataLength() {
        return 6 + this.color.getDataLength() + this.thresholdMin.getDataLength() + this.thresholdMax.getDataLength();
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
        out.writeByte(0);
        out.writeByte(this.options);
        out.writeByte(this.percentMin);
        out.writeByte(this.percentMax);
        this.color.serialize(out);
        this.thresholdMin.serialize(out);
        this.thresholdMax.serialize(out);
    }
}

