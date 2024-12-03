/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.atlassian.webresource.spi;

import java.util.Objects;
import javax.annotation.Nonnull;

public class CompilerEntry<T> {
    private final String key;
    private final T value;

    private CompilerEntry(String key, T value) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (value == null) {
            throw new IllegalArgumentException("value is null");
        }
        this.key = key;
        this.value = value;
    }

    public static <T> CompilerEntry<T> ofKeyValue(@Nonnull String key, @Nonnull T value) {
        return new CompilerEntry<T>(key, value);
    }

    public String key() {
        return this.key;
    }

    public T value() {
        return this.value;
    }

    public String toString() {
        return "CompilerEntry{key='" + this.key + "'}";
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CompilerEntry that = (CompilerEntry)o;
        return Objects.equals(this.key, that.key);
    }

    public int hashCode() {
        return Objects.hash(this.key);
    }
}

