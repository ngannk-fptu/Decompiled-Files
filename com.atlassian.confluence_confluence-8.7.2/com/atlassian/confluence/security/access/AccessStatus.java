/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.access;

public interface AccessStatus {
    public boolean hasLicensedAccess();

    public boolean hasUnlicensedAuthenticatedAccess();

    public boolean hasAnonymousAccess();

    public boolean canUseConfluence();
}

