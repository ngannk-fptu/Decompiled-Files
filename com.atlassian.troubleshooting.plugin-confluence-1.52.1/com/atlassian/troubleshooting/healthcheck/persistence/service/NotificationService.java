/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.tx.Transactional
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.troubleshooting.api.healthcheck.HealthCheckStatus;
import java.util.List;

@Transactional
public interface NotificationService {
    public void storeDismissedNotification(UserKey var1, Integer var2, boolean var3);

    public List<HealthCheckStatus> getStatusesForUserNotifications(UserKey var1);

    public void deleteDismissByUser(UserKey var1);

    public Boolean checkIsAutoDismissed(UserKey var1, Integer var2);

    public void deleteDismissById(List<Integer> var1);
}

