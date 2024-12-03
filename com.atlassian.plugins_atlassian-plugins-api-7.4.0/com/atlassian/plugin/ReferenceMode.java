/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin;

public enum ReferenceMode {
    FORBID_REFERENCE(false),
    PERMIT_REFERENCE(true);

    private boolean allowsReference;

    private ReferenceMode(boolean allowsReference) {
        this.allowsReference = allowsReference;
    }

    public boolean allowsReference() {
        return this.allowsReference;
    }
}

