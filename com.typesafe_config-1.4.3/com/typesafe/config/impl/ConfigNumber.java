/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigDouble;
import com.typesafe.config.impl.ConfigInt;
import com.typesafe.config.impl.ConfigLong;
import com.typesafe.config.impl.SerializedConfigValue;
import java.io.ObjectStreamException;
import java.io.Serializable;

abstract class ConfigNumber
extends AbstractConfigValue
implements Serializable {
    private static final long serialVersionUID = 2L;
    protected final String originalText;

    protected ConfigNumber(ConfigOrigin origin, String originalText) {
        super(origin);
        this.originalText = originalText;
    }

    @Override
    public abstract Number unwrapped();

    @Override
    String transformToString() {
        return this.originalText;
    }

    int intValueRangeChecked(String path) {
        long l = this.longValue();
        if (l < Integer.MIN_VALUE || l > Integer.MAX_VALUE) {
            throw new ConfigException.WrongType(this.origin(), path, "32-bit integer", "out-of-range value " + l);
        }
        return (int)l;
    }

    protected abstract long longValue();

    protected abstract double doubleValue();

    private boolean isWhole() {
        long asLong = this.longValue();
        return (double)asLong == this.doubleValue();
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ConfigNumber;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof ConfigNumber && this.canEqual(other)) {
            ConfigNumber n = (ConfigNumber)other;
            if (this.isWhole()) {
                return n.isWhole() && this.longValue() == n.longValue();
            }
            return !n.isWhole() && this.doubleValue() == n.doubleValue();
        }
        return false;
    }

    @Override
    public int hashCode() {
        long asLong = this.isWhole() ? this.longValue() : Double.doubleToLongBits(this.doubleValue());
        return (int)(asLong ^ asLong >>> 32);
    }

    static ConfigNumber newNumber(ConfigOrigin origin, long number, String originalText) {
        if (number <= Integer.MAX_VALUE && number >= Integer.MIN_VALUE) {
            return new ConfigInt(origin, (int)number, originalText);
        }
        return new ConfigLong(origin, number, originalText);
    }

    static ConfigNumber newNumber(ConfigOrigin origin, double number, String originalText) {
        long asLong = (long)number;
        if ((double)asLong == number) {
            return ConfigNumber.newNumber(origin, asLong, originalText);
        }
        return new ConfigDouble(origin, number, originalText);
    }

    private Object writeReplace() throws ObjectStreamException {
        return new SerializedConfigValue(this);
    }
}

