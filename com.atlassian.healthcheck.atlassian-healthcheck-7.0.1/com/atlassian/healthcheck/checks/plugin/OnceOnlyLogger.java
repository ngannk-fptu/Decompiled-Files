/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.tenancy.TenancyScope
 *  com.atlassian.annotations.tenancy.TenantAware
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.healthcheck.checks.plugin;

import com.atlassian.annotations.tenancy.TenancyScope;
import com.atlassian.annotations.tenancy.TenantAware;
import com.atlassian.healthcheck.core.HealthCheck;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.LoggerFactory;

public class OnceOnlyLogger {
    @TenantAware(value=TenancyScope.TENANTLESS, comment="Healthchecks can be called tenantless; module and plugin state will be the same across tenants")
    private final Map<String, String> lastMessageByOriginator = new ConcurrentHashMap<String, String>(2);

    void logWarningIfDifferentFromLastMessage(Class<? extends HealthCheck> originatorClass, String classifier, String message) {
        String key = this.getMapKey(originatorClass, classifier);
        String lastMessage = this.lastMessageByOriginator.get(key);
        if (lastMessage != null && lastMessage.equals(message)) {
            return;
        }
        LoggerFactory.getLogger(originatorClass).warn(message);
        this.lastMessageByOriginator.put(key, message);
    }

    void clearLastMessage(Class<? extends HealthCheck> originatorClass, String classifier) {
        this.lastMessageByOriginator.remove(this.getMapKey(originatorClass, classifier));
    }

    private String getMapKey(Class<? extends HealthCheck> originatorClass, String classifier) {
        return originatorClass.getName() + ':' + classifier;
    }
}

