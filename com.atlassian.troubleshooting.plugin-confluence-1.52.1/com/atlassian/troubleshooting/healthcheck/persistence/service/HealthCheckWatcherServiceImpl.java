/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.sal.api.user.UserKey
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.java.ao.DBParam
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.persistence.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.troubleshooting.healthcheck.persistence.service.HealthCheckWatcherService;
import com.atlassian.troubleshooting.stp.persistence.SupportHealthcheckSchema;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.ParametersAreNonnullByDefault;
import net.java.ao.DBParam;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class HealthCheckWatcherServiceImpl
implements HealthCheckWatcherService {
    private final ActiveObjects ao;

    @Autowired
    public HealthCheckWatcherServiceImpl(ActiveObjects ao) {
        this.ao = Objects.requireNonNull(ao);
    }

    @Override
    public boolean isWatching(UserKey userKey) {
        Objects.requireNonNull(userKey);
        return ((SupportHealthcheckSchema.Watcher[])this.ao.find(SupportHealthcheckSchema.Watcher.class, "USER_KEY = ?", new Object[]{userKey.getStringValue()})).length > 0;
    }

    @Override
    public void watch(UserKey userKey) {
        Objects.requireNonNull(userKey);
        if (!this.isWatching(userKey)) {
            ((SupportHealthcheckSchema.Watcher)this.ao.create(SupportHealthcheckSchema.Watcher.class, new DBParam[]{new DBParam("USER_KEY", (Object)userKey.getStringValue())})).save();
        }
    }

    @Override
    public void unwatch(UserKey userKey) {
        Objects.requireNonNull(userKey);
        this.ao.deleteWithSQL(SupportHealthcheckSchema.Watcher.class, "USER_KEY = ?", new Object[]{userKey.getStringValue()});
    }

    @Override
    public List<UserKey> getAllWatchers() {
        return Stream.of(this.ao.find(SupportHealthcheckSchema.Watcher.class)).map(SupportHealthcheckSchema.Watcher::getUserKey).map(UserKey::new).collect(Collectors.toList());
    }
}

