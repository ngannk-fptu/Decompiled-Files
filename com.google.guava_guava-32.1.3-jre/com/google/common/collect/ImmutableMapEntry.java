/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.CheckForNull
 */
package com.google.common.collect;

import com.google.common.annotations.GwtIncompatible;
import com.google.common.collect.CollectPreconditions;
import com.google.common.collect.ElementTypesAreNonnullByDefault;
import com.google.common.collect.ImmutableEntry;
import javax.annotation.CheckForNull;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
class ImmutableMapEntry<K, V>
extends ImmutableEntry<K, V> {
    static <K, V> ImmutableMapEntry<K, V>[] createEntryArray(int size) {
        return new ImmutableMapEntry[size];
    }

    ImmutableMapEntry(K key, V value) {
        super(key, value);
        CollectPreconditions.checkEntryNotNull(key, value);
    }

    ImmutableMapEntry(ImmutableMapEntry<K, V> contents) {
        super(contents.getKey(), contents.getValue());
    }

    @CheckForNull
    ImmutableMapEntry<K, V> getNextInKeyBucket() {
        return null;
    }

    @CheckForNull
    ImmutableMapEntry<K, V> getNextInValueBucket() {
        return null;
    }

    boolean isReusable() {
        return true;
    }

    static final class NonTerminalImmutableBiMapEntry<K, V>
    extends NonTerminalImmutableMapEntry<K, V> {
        @CheckForNull
        private final transient ImmutableMapEntry<K, V> nextInValueBucket;

        NonTerminalImmutableBiMapEntry(K key, V value, @CheckForNull ImmutableMapEntry<K, V> nextInKeyBucket, @CheckForNull ImmutableMapEntry<K, V> nextInValueBucket) {
            super(key, value, nextInKeyBucket);
            this.nextInValueBucket = nextInValueBucket;
        }

        @Override
        @CheckForNull
        ImmutableMapEntry<K, V> getNextInValueBucket() {
            return this.nextInValueBucket;
        }
    }

    static class NonTerminalImmutableMapEntry<K, V>
    extends ImmutableMapEntry<K, V> {
        @CheckForNull
        private final transient ImmutableMapEntry<K, V> nextInKeyBucket;

        NonTerminalImmutableMapEntry(K key, V value, @CheckForNull ImmutableMapEntry<K, V> nextInKeyBucket) {
            super(key, value);
            this.nextInKeyBucket = nextInKeyBucket;
        }

        @Override
        @CheckForNull
        final ImmutableMapEntry<K, V> getNextInKeyBucket() {
            return this.nextInKeyBucket;
        }

        @Override
        final boolean isReusable() {
            return false;
        }
    }
}

