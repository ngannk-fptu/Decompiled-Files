/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.hwpf.model;

import java.util.Objects;
import org.apache.poi.common.Duplicatable;
import org.apache.poi.util.BitField;
import org.apache.poi.util.Internal;

@Internal
public final class PropertyModifier
implements Duplicatable {
    private static final BitField _fComplex = new BitField(1);
    private static final BitField _figrpprl = new BitField(65534);
    private static final BitField _fisprm = new BitField(254);
    private static final BitField _fval = new BitField(65280);
    private short value;

    public PropertyModifier(short value) {
        this.value = value;
    }

    public PropertyModifier(PropertyModifier other) {
        this.value = other.value;
    }

    @Override
    public PropertyModifier copy() {
        return new PropertyModifier(this);
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
        PropertyModifier other = (PropertyModifier)obj;
        return this.value == other.value;
    }

    public short getIgrpprl() {
        if (!this.isComplex()) {
            throw new IllegalStateException("Not complex");
        }
        return _figrpprl.getShortValue(this.value);
    }

    public short getIsprm() {
        if (this.isComplex()) {
            throw new IllegalStateException("Not simple");
        }
        return _fisprm.getShortValue(this.value);
    }

    public short getVal() {
        if (this.isComplex()) {
            throw new IllegalStateException("Not simple");
        }
        return _fval.getShortValue(this.value);
    }

    public short getValue() {
        return this.value;
    }

    public int hashCode() {
        return Objects.hash(this.value);
    }

    public boolean isComplex() {
        return _fComplex.isSet(this.value);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[PRM] (complex: ");
        stringBuilder.append(this.isComplex());
        stringBuilder.append("; ");
        if (this.isComplex()) {
            stringBuilder.append("igrpprl: ");
            stringBuilder.append(this.getIgrpprl());
            stringBuilder.append("; ");
        } else {
            stringBuilder.append("isprm: ");
            stringBuilder.append(this.getIsprm());
            stringBuilder.append("; ");
            stringBuilder.append("val: ");
            stringBuilder.append(this.getVal());
            stringBuilder.append("; ");
        }
        stringBuilder.append(")");
        return stringBuilder.toString();
    }
}

