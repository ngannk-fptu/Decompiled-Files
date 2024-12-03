/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hslf.model.textproperties;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.common.usermodel.GenericRecord;
import org.apache.poi.util.GenericRecordUtil;

public class TextProp
implements Duplicatable,
GenericRecord {
    private int sizeOfDataBlock;
    private String propName;
    private int dataValue;
    private int maskInHeader;

    public TextProp(int sizeOfDataBlock, int maskInHeader, String propName) {
        this.sizeOfDataBlock = sizeOfDataBlock;
        this.maskInHeader = maskInHeader;
        this.propName = propName;
        this.dataValue = 0;
    }

    public TextProp(TextProp other) {
        this.sizeOfDataBlock = other.sizeOfDataBlock;
        this.maskInHeader = other.maskInHeader;
        this.propName = other.propName;
        this.dataValue = other.dataValue;
    }

    public String getName() {
        return this.propName;
    }

    public int getSize() {
        return this.sizeOfDataBlock;
    }

    public int getMask() {
        return this.maskInHeader;
    }

    public int getWriteMask() {
        return this.getMask();
    }

    public int getValue() {
        return this.dataValue;
    }

    public void setValue(int val) {
        this.dataValue = val;
    }

    @Override
    public TextProp copy() {
        assert (TextProp.class.equals(this.getClass()));
        return new TextProp(this);
    }

    public int hashCode() {
        return Objects.hash(this.dataValue, this.maskInHeader, this.propName, this.sizeOfDataBlock);
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        TextProp other = (TextProp)obj;
        if (this.dataValue != other.dataValue) {
            return false;
        }
        if (this.maskInHeader != other.maskInHeader) {
            return false;
        }
        if (this.propName == null ? other.propName != null : !this.propName.equals(other.propName)) {
            return false;
        }
        return this.sizeOfDataBlock == other.sizeOfDataBlock;
    }

    public String toString() {
        int len;
        switch (this.getSize()) {
            case 1: {
                len = 4;
                break;
            }
            case 2: {
                len = 6;
                break;
            }
            default: {
                len = 10;
            }
        }
        return String.format(Locale.ROOT, "%s = %d (%0#" + len + "X mask / %d bytes)", this.getName(), this.getValue(), this.getMask(), this.getSize());
    }

    @Override
    public Map<String, Supplier<?>> getGenericProperties() {
        return GenericRecordUtil.getGenericProperties("sizeOfDataBlock", this::getSize, "propName", this::getName, "dataValue", this::getValue, "maskInHeader", this::getMask);
    }
}

