/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.atlassian.confluence.cache.ehcache;

import org.checkerframework.checker.nullness.qual.Nullable;

class ValueWrapper<T> {
    private final String cacheName;
    private final T value;

    ValueWrapper(String cacheName, @Nullable T value) {
        this.cacheName = cacheName;
        this.value = value;
    }

    T getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ValueWrapper that = (ValueWrapper)o;
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    public int hashCode() {
        return this.value != null ? this.value.hashCode() : 0;
    }

    public String toString() {
        return "ValueWrapper{cacheName='" + this.cacheName + "', value=" + this.value + "}";
    }
}

