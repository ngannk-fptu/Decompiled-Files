/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.TermNumeric;
import cz.vutbr.web.csskit.TermImpl;

public abstract class TermNumericImpl<T extends Number>
extends TermImpl<T>
implements TermNumeric<T> {
    protected TermNumeric.Unit unit;

    @Override
    public TermNumeric.Unit getUnit() {
        return this.unit;
    }

    @Override
    public TermNumeric<T> setUnit(TermNumeric.Unit unit) {
        this.unit = unit;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (this.operator != null) {
            sb.append(this.operator.value());
        }
        if (this.value != null) {
            if ((double)((Number)this.value).intValue() == ((Number)this.value).doubleValue()) {
                sb.append(((Number)this.value).intValue());
            } else {
                sb.append(this.value);
            }
        }
        if (this.unit != null) {
            sb.append(this.unit.value());
        }
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.unit == null ? 0 : this.unit.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof TermNumericImpl)) {
            return false;
        }
        TermNumericImpl other = (TermNumericImpl)obj;
        return !(this.unit == null ? other.unit != null : !this.unit.equals((Object)other.unit));
    }
}

