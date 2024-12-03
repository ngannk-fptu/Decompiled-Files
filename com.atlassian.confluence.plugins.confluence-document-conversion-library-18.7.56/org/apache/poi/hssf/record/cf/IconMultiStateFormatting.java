/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hssf.record.cf;

import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.hssf.record.cf.IconMultiStateThreshold;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.ss.usermodel.IconMultiStateFormatting;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class IconMultiStateFormatting
implements Duplicatable,
GenericRecord {
    private static final Logger LOG = LogManager.getLogger(IconMultiStateFormatting.class);
    private static BitField ICON_ONLY = BitFieldFactory.getInstance(1);
    private static BitField REVERSED = BitFieldFactory.getInstance(4);
    private IconMultiStateFormatting.IconSet iconSet;
    private byte options;
    private Threshold[] thresholds;

    public IconMultiStateFormatting() {
        this.iconSet = IconMultiStateFormatting.IconSet.GYR_3_TRAFFIC_LIGHTS;
        this.options = 0;
        this.thresholds = new Threshold[this.iconSet.num];
    }

    public IconMultiStateFormatting(IconMultiStateFormatting other) {
        this.iconSet = other.iconSet;
        this.options = other.options;
        if (other.thresholds != null) {
            this.thresholds = (Threshold[])Stream.of(other.thresholds).map(Threshold::copy).toArray(Threshold[]::new);
        }
    }

    public IconMultiStateFormatting(LittleEndianInput in) {
        in.readShort();
        in.readByte();
        byte num = in.readByte();
        byte set = in.readByte();
        this.iconSet = IconMultiStateFormatting.IconSet.byId(set);
        if (this.iconSet.num != num) {
            LOG.atWarn().log("Inconsistent Icon Set definition, found {} but defined as {} entries", (Object)this.iconSet, (Object)Unbox.box((int)num));
        }
        this.options = in.readByte();
        this.thresholds = new Threshold[this.iconSet.num];
        for (int i = 0; i < this.thresholds.length; ++i) {
            this.thresholds[i] = new IconMultiStateThreshold(in);
        }
    }

    public IconMultiStateFormatting.IconSet getIconSet() {
        return this.iconSet;
    }

    public void setIconSet(IconMultiStateFormatting.IconSet set) {
        this.iconSet = set;
    }

    public Threshold[] getThresholds() {
        return this.thresholds;
    }

    public void setThresholds(Threshold[] thresholds) {
        this.thresholds = thresholds == null ? null : (Threshold[])thresholds.clone();
    }

    public boolean isIconOnly() {
        return ICON_ONLY.isSet(this.options);
    }

    public void setIconOnly(boolean only) {
        this.options = ICON_ONLY.setByteBoolean(this.options, only);
    }

    public boolean isReversed() {
        return REVERSED.isSet(this.options);
    }

    public void setReversed(boolean rev) {
        this.options = REVERSED.setByteBoolean(this.options, rev);
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("iconSet", this::getIconSet, "iconOnly", this::isIconOnly, "reversed", this::isReversed, "thresholds", this::getThresholds);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public IconMultiStateFormatting copy() {
        return new IconMultiStateFormatting(this);
    }

    public int getDataLength() {
        int len = 6;
        for (Threshold t : this.thresholds) {
            len += t.getDataLength();
        }
        return len;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
        out.writeByte(0);
        out.writeByte(this.iconSet.num);
        out.writeByte(this.iconSet.id);
        out.writeByte(this.options);
        for (Threshold t : this.thresholds) {
            t.serialize(out);
        }
    }
}

