/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.manager.emailscan;

public class ValidationResult {
    private final long duplicatedEmailsCount;
    private final long invalidEmailsCount;

    public ValidationResult(long duplicatedEmailsCount, long invalidEmailsCount) {
        this.duplicatedEmailsCount = duplicatedEmailsCount;
        this.invalidEmailsCount = invalidEmailsCount;
    }

    public long getDuplicatedEmailsCount() {
        return this.duplicatedEmailsCount;
    }

    public long getInvalidEmailsCount() {
        return this.invalidEmailsCount;
    }
}

