/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.upgrade;

public interface BackupSupport {
    public boolean runOnSpaceImport();

    public boolean breaksBackwardCompatibility();
}

