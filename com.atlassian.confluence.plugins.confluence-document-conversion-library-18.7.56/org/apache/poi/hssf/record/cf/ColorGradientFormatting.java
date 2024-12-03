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
import org.apache.poi.hssf.record.cf.ColorGradientThreshold;
import org.apache.poi.hssf.record.cf.Threshold;
import org.apache.poi.hssf.record.common.ExtendedColor;
import org.apache.poi.util.BitField;
import org.apache.poi.util.BitFieldFactory;
import org.apache.poi.util.GenericRecordJsonWriter;
import org.apache.poi.util.GenericRecordUtil;
import org.apache.poi.util.LittleEndianInput;
import org.apache.poi.util.LittleEndianOutput;

public final class ColorGradientFormatting
implements Duplicatable,
GenericRecord {
    private static final Logger LOGGER = LogManager.getLogger(ColorGradientFormatting.class);
    private static final BitField clamp = BitFieldFactory.getInstance(1);
    private static final BitField background = BitFieldFactory.getInstance(2);
    private final byte options;
    private ColorGradientThreshold[] thresholds;
    private ExtendedColor[] colors;

    public ColorGradientFormatting() {
        this.options = (byte)3;
        this.thresholds = new ColorGradientThreshold[3];
        this.colors = new ExtendedColor[3];
    }

    public ColorGradientFormatting(ColorGradientFormatting other) {
        this.options = other.options;
        if (other.thresholds != null) {
            this.thresholds = (ColorGradientThreshold[])Stream.of(other.thresholds).map(ColorGradientThreshold::copy).toArray(ColorGradientThreshold[]::new);
        }
        if (other.colors != null) {
            this.colors = (ExtendedColor[])Stream.of(other.colors).map(ExtendedColor::copy).toArray(ExtendedColor[]::new);
        }
    }

    public ColorGradientFormatting(LittleEndianInput in) {
        int i;
        in.readShort();
        in.readByte();
        byte numI = in.readByte();
        byte numG = in.readByte();
        if (numI != numG) {
            LOGGER.atWarn().log("Inconsistent Color Gradient definition, found {} vs {} entries", (Object)Unbox.box((int)numI), (Object)Unbox.box((int)numG));
        }
        this.options = in.readByte();
        this.thresholds = new ColorGradientThreshold[numI];
        for (i = 0; i < this.thresholds.length; ++i) {
            this.thresholds[i] = new ColorGradientThreshold(in);
        }
        this.colors = new ExtendedColor[numG];
        for (i = 0; i < this.colors.length; ++i) {
            in.readDouble();
            this.colors[i] = new ExtendedColor(in);
        }
    }

    public int getNumControlPoints() {
        return this.thresholds.length;
    }

    public void setNumControlPoints(int num) {
        if (num != this.thresholds.length) {
            ColorGradientThreshold[] nt = new ColorGradientThreshold[num];
            ExtendedColor[] nc = new ExtendedColor[num];
            int copy = Math.min(this.thresholds.length, num);
            System.arraycopy(this.thresholds, 0, nt, 0, copy);
            System.arraycopy(this.colors, 0, nc, 0, copy);
            this.thresholds = nt;
            this.colors = nc;
            this.updateThresholdPositions();
        }
    }

    public ColorGradientThreshold[] getThresholds() {
        return this.thresholds;
    }

    public void setThresholds(ColorGradientThreshold[] thresholds) {
        this.thresholds = thresholds == null ? null : (ColorGradientThreshold[])thresholds.clone();
        this.updateThresholdPositions();
    }

    public ExtendedColor[] getColors() {
        return this.colors;
    }

    public void setColors(ExtendedColor[] colors) {
        this.colors = colors == null ? null : (ExtendedColor[])colors.clone();
    }

    public boolean isClampToCurve() {
        return this.getOptionFlag(clamp);
    }

    public boolean isAppliesToBackground() {
        return this.getOptionFlag(background);
    }

    private boolean getOptionFlag(BitField field) {
        return field.isSet(this.options);
    }

    private void updateThresholdPositions() {
        double step = 1.0 / (double)(this.thresholds.length - 1);
        for (int i = 0; i < this.thresholds.length; ++i) {
            this.thresholds[i].setPosition(step * (double)i);
        }
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("clampToCurve", this::isClampToCurve, "background", this::isAppliesToBackground, "thresholds", this::getThresholds, "colors", this::getColors);
    }

    public String toString() {
        return GenericRecordJsonWriter.marshal(this);
    }

    @Override
    public ColorGradientFormatting copy() {
        return new ColorGradientFormatting(this);
    }

    public int getDataLength() {
        int len = 6;
        for (ColorGradientThreshold colorGradientThreshold : this.thresholds) {
            len += ((Threshold)colorGradientThreshold).getDataLength();
        }
        for (GenericRecord genericRecord : this.colors) {
            len += ((ExtendedColor)genericRecord).getDataLength();
            len += 8;
        }
        return len;
    }

    public void serialize(LittleEndianOutput out) {
        out.writeShort(0);
        out.writeByte(0);
        out.writeByte(this.thresholds.length);
        out.writeByte(this.thresholds.length);
        out.writeByte(this.options);
        for (ColorGradientThreshold t : this.thresholds) {
            t.serialize(out);
        }
        double step = 1.0 / (double)(this.colors.length - 1);
        for (int i = 0; i < this.colors.length; ++i) {
            out.writeDouble((double)i * step);
            ExtendedColor c = this.colors[i];
            c.serialize(out);
        }
    }
}

