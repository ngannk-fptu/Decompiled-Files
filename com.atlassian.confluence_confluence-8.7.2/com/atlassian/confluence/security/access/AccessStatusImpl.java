/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.security.access;

import com.atlassian.confluence.security.access.AccessStatus;

enum AccessStatusImpl implements AccessStatus
{
    LICENSED_ACCESS,
    UNLICENSED_AUTHENTICATED_ACCESS,
    ANONYMOUS_ACCESS,
    NOT_PERMITTED;


    @Override
    public boolean hasLicensedAccess() {
        return this == LICENSED_ACCESS;
    }

    @Override
    public boolean hasUnlicensedAuthenticatedAccess() {
        return this == UNLICENSED_AUTHENTICATED_ACCESS;
    }

    @Override
    public boolean hasAnonymousAccess() {
        return this == ANONYMOUS_ACCESS;
    }

    @Override
    public boolean canUseConfluence() {
        return this == LICENSED_ACCESS || this == UNLICENSED_AUTHENTICATED_ACCESS || this == ANONYMOUS_ACCESS;
    }
}

