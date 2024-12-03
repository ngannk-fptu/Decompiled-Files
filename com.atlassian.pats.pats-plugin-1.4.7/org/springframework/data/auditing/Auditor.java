/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 *  org.springframework.util.ObjectUtils
 */
package org.springframework.data.auditing;

import java.util.Optional;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

class Auditor<T> {
    private static final Auditor NONE = new Auditor((Object)null){

        @Override
        public boolean isPresent() {
            return false;
        }
    };
    @Nullable
    private final T value;

    private Auditor(@Nullable T value) {
        this.value = value;
    }

    @Nullable
    public T getValue() {
        return this.value;
    }

    public static <T> Auditor<T> of(@Nullable T source) {
        if (source instanceof Auditor) {
            return (Auditor)source;
        }
        return source == null ? Auditor.none() : new Auditor<T>(source);
    }

    public static <T> Auditor<T> ofOptional(@Nullable Optional<T> source) {
        return Auditor.of(source.orElse(null));
    }

    public static <T> Auditor<T> none() {
        return NONE;
    }

    public boolean isPresent() {
        return this.getValue() != null;
    }

    public String toString() {
        return this.value != null ? this.value.toString() : "Auditor.none()";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Auditor auditor = (Auditor)o;
        return ObjectUtils.nullSafeEquals(this.value, auditor.value);
    }

    public int hashCode() {
        return ObjectUtils.nullSafeHashCode(this.value);
    }
}

