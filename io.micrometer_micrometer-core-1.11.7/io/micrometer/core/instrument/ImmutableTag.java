/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  io.micrometer.common.lang.Nullable
 */
package io.micrometer.core.instrument;

import io.micrometer.common.lang.Nullable;
import io.micrometer.core.instrument.Tag;
import java.util.Objects;

public class ImmutableTag
implements Tag {
    private final String key;
    private final String value;

    public ImmutableTag(String key, String value) {
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
        Tag that = (Tag)o;
        return Objects.equals(this.key, that.getKey()) && Objects.equals(this.value, that.getValue());
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    public String toString() {
        return "tag(" + this.key + "=" + this.value + ")";
    }
}

