/*
 * Decompiled with CFR 0.152.
 */
package org.apache.hc.core5.http.impl.bootstrap;

final class FilterEntry<T> {
    final Position position;
    final String name;
    final T filterHandler;
    final String existing;

    FilterEntry(Position position, String name, T filterHandler, String existing) {
        this.position = position;
        this.name = name;
        this.filterHandler = filterHandler;
        this.existing = existing;
    }

    static enum Position {
        BEFORE,
        AFTER,
        REPLACE,
        FIRST,
        LAST;

    }
}

