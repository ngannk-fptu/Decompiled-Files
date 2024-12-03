/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.permission;

public interface UpmVisibility {
    public boolean isManageExistingVisible();

    public boolean isInstallVisible();

    public boolean isOsgiVisible();

    public boolean isPurchasedAddonsVisible();

    public boolean isNotificationDropdownVisible();
}

