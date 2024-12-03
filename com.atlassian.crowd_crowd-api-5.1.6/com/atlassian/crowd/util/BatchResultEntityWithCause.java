/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.util;

import java.util.Objects;

public class BatchResultEntityWithCause {
    private final String entityName;
    private final String reason;

    public BatchResultEntityWithCause(String entityName, String reason) {
        this.entityName = entityName;
        this.reason = reason;
    }

    public String getEntityName() {
        return this.entityName;
    }

    public String getReason() {
        return this.reason;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BatchResultEntityWithCause that = (BatchResultEntityWithCause)o;
        return Objects.equals(this.entityName, that.entityName) && Objects.equals(this.reason, that.reason);
    }

    public int hashCode() {
        return Objects.hash(this.entityName, this.reason);
    }
}

