/*
 * Decompiled with CFR 0.152.
 */
package com.microsoft.sqlserver.jdbc;

import java.util.UUID;

class ActivityId {
    private final UUID id = UUID.randomUUID();
    private long sequence = 1L;

    ActivityId() {
    }

    UUID getId() {
        return this.id;
    }

    long getSequence() {
        return this.sequence;
    }

    void increment() {
        this.sequence = this.sequence < 0xFFFFFFFFL ? ++this.sequence : 0L;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.id.toString());
        sb.append("-");
        sb.append(this.sequence);
        return sb.toString();
    }
}

