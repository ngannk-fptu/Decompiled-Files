/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.Assert
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.mapping;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

public final class Alias {
    public static final Alias NONE = new Alias(null);
    private final Object value;

    private Alias(Object value) {
        this.value = value;
    }

    public static Alias of(Object alias) {
        Assert.notNull((Object)alias, (String)"Alias must not be null!");
        return new Alias(alias);
    }

    public static Alias ofNullable(@Nullable Object alias) {
        return alias == null ? NONE : new Alias(alias);
    }

    public static Alias empty() {
        return NONE;
    }

    public boolean isPresentButDifferent(Alias other) {
        Assert.notNull((Object)other, (String)"Other alias must not be null!");
        return this.isPresent() && !this.value.equals(other.value);
    }

    public boolean hasValue(Object that) {
        return this.value != null && this.value.equals(that);
    }

    public boolean hasSamePresentValueAs(Alias other) {
        return this.isPresent() && this.value.equals(other.value);
    }

    public boolean isPresent() {
        return this.value != null;
    }

    @Nullable
    public <T> T mapTyped(Class<T> type) {
        Assert.notNull(type, (String)"Type must not be null");
        return (T)(this.isPresent() && type.isInstance(this.value) ? this.value : null);
    }

    public String toString() {
        return this.isPresent() ? this.value.toString() : "NONE";
    }

    public Object getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Alias)) {
            return false;
        }
        Alias alias = (Alias)o;
        return ObjectUtils.nullSafeEquals((Object)this.value, (Object)alias.value);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode((Object)this.value);
    }
}

