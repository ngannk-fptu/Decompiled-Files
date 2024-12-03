/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.upm.notification;

import com.atlassian.upm.pac.AvailableAddonWithVersion;

public interface ManualUpdateRequiredNotificationService {
    public void sendFreeToPaidNotification(AvailableAddonWithVersion var1);

    public void clearEmailRecords(String var1);
}

