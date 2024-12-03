/*
 * Decompiled with CFR 0.152.
 */
package io.micrometer.common;

import io.micrometer.common.KeyValue;
import io.micrometer.common.lang.Nullable;
import java.util.Objects;

class ImmutableKeyValue
implements KeyValue {
    private final String key;
    private final String value;

    ImmutableKeyValue(String key, String value) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(value);
        this.key = key;
        this.value = value;
    }

    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        KeyValue that = (KeyValue)o;
        return Objects.equals(this.key, that.getKey()) && Objects.equals(this.value, that.getValue());
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    public String toString() {
        return "keyValue(" + this.key + "=" + this.value + ")";
    }
}

