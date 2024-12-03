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
import java.util.List;

@Transactional
public interface HealthCheckWatcherService {
    public boolean isWatching(UserKey var1);

    public void watch(UserKey var1);

    public void unwatch(UserKey var1);

    public List<UserKey> getAllWatchers();
}

