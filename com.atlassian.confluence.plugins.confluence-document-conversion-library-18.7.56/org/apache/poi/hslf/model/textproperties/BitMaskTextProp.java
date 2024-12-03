/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Map;
import java.util.function.Supplier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.message.SimpleMessage;
import org.apache.poi.hslf.model.textproperties.TextProp;
import org.apache.poi.util.GenericRecordUtil;

public abstract class BitMaskTextProp
extends TextProp {
    protected static final Logger LOG = LogManager.getLogger(BitMaskTextProp.class);
    private final String[] subPropNames;
    private final int[] subPropMasks;
    private final boolean[] subPropMatches;

    public String[] getSubPropNames() {
        return this.subPropNames;
    }

    public boolean[] getSubPropMatches() {
        return this.subPropMatches;
    }

    protected BitMaskTextProp(BitMaskTextProp other) {
        super(other);
        this.subPropNames = other.subPropNames == null ? null : (String[])other.subPropNames.clone();
        this.subPropMasks = other.subPropMasks == null ? null : (int[])other.subPropMasks.clone();
        this.subPropMatches = other.subPropMatches == null ? null : (boolean[])other.subPropMatches.clone();
    }

    protected BitMaskTextProp(int sizeOfDataBlock, int maskInHeader, String overallName, String ... subPropNames) {
        super(sizeOfDataBlock, maskInHeader, overallName);
        this.subPropNames = subPropNames;
        this.subPropMasks = new int[subPropNames.length];
        this.subPropMatches = new boolean[subPropNames.length];
        int LSB = Integer.lowestOneBit(maskInHeader);
        for (int i = 0; i < this.subPropMasks.length; ++i) {
            this.subPropMasks[i] = LSB << i;
        }
    }

    @Override
    public int getWriteMask() {
        int mask = 0;
        int i = 0;
        for (int subMask : this.subPropMasks) {
            if (!this.subPropMatches[i++]) continue;
            mask |= subMask;
        }
        return mask;
    }

    public void setWriteMask(int writeMask) {
        int i = 0;
        for (int subMask : this.subPropMasks) {
            this.subPropMatches[i++] = (writeMask & subMask) != 0;
        }
    }

    @Override
    public int getValue() {
        return this.maskValue(super.getValue());
    }

    private int maskValue(int pVal) {
        int val = pVal;
        int i = 0;
        for (int mask : this.subPropMasks) {
            if (this.subPropMatches[i++]) continue;
            val &= ~mask;
        }
        return val;
    }

    @Override
    public void setValue(int val) {
        super.setValue(val);
        int i = 0;
        for (int mask : this.subPropMasks) {
            this.subPropMatches[i++] = (val & mask) != 0;
        }
    }

    public void setValueWithMask(int val, int writeMask) {
        this.setWriteMask(writeMask);
        super.setValue(this.maskValue(val));
        if (val != super.getValue()) {
            LOG.atWarn().log("Style properties of '{}' don't match mask - output will be sanitized", (Object)this.getName());
            LOG.atDebug().log(() -> {
                StringBuilder sb = new StringBuilder("The following style attributes of the '").append(this.getName()).append("' property will be ignored:\n");
                int i = 0;
                for (int mask : this.subPropMasks) {
                    if (!this.subPropMatches[i] && (val & mask) != 0) {
                        sb.append(this.subPropNames[i]).append(",");
                    }
                    ++i;
                }
                return new SimpleMessage(sb);
            });
        }
    }

    public boolean getSubValue(int idx) {
        return this.subPropMatches[idx] && (super.getValue() & this.subPropMasks[idx]) != 0;
    }

    public void setSubValue(boolean value, int idx) {
        this.subPropMatches[idx] = true;
        int newVal = super.getValue();
        newVal = value ? (newVal |= this.subPropMasks[idx]) : (newVal &= ~this.subPropMasks[idx]);
        super.setValue(newVal);
    }

    public BitMaskTextProp cloneAll() {
        BitMaskTextProp bmtp = this.copy();
        if (this.subPropMatches != null) {
            System.arraycopy(this.subPropMatches, 0, bmtp.subPropMatches, 0, this.subPropMatches.length);
        }
        return bmtp;
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("base", () -> super.getGenericProperties(), "flags", GenericRecordUtil.getBitsAsString(this::getValue, this.subPropMasks, this.subPropNames));
    }

    @Override
    public abstract BitMaskTextProp copy();
}

