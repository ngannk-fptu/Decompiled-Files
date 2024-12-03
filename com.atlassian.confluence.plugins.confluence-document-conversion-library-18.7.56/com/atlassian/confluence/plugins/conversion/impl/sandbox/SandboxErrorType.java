/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.conversion.impl.sandbox;

public enum SandboxErrorType {
    CRASHED(1001),
    KILLED(2001);

    private final int issueId;

    private SandboxErrorType(int issueId) {
        this.issueId = issueId;
    }

    public int getIssueId() {
        return this.issueId;
    }
}

