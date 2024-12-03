/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.event.api.EventListener
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  javax.annotation.PostConstruct
 *  javax.annotation.PreDestroy
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.authentication.impl.basicauth.audit;

import com.atlassian.event.api.EventListener;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.authentication.impl.basicauth.BasicAuthConfig;
import com.atlassian.plugins.authentication.impl.basicauth.audit.BasicAuthAuditLogHandler;
import com.atlassian.plugins.authentication.impl.basicauth.event.BasicAuthUpdatedEvent;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.stereotype.Component;

@Component
public class BasicAuthAuditEventListener {
    private final BasicAuthAuditLogHandler basicAuthAuditLogHandler;
    private final EventPublisher eventPublisher;

    public BasicAuthAuditEventListener(BasicAuthAuditLogHandler basicAuthAuditLogHandler, @ComponentImport EventPublisher eventPublisher) {
        this.basicAuthAuditLogHandler = basicAuthAuditLogHandler;
        this.eventPublisher = eventPublisher;
    }

    @PostConstruct
    public void setup() {
        this.eventPublisher.register((Object)this);
    }

    @PreDestroy
    public void destroy() {
        this.eventPublisher.unregister((Object)this);
    }

    @EventListener
    public void onAuditEvent(BasicAuthUpdatedEvent authUpdatedEvent) {
        this.generateAuditEvents(authUpdatedEvent.getOldBasicAuthConfig(), authUpdatedEvent.getNewBasicAuthConfig());
    }

    private void generateAuditEvents(BasicAuthConfig oldConfig, BasicAuthConfig newConfig) {
        this.logAllowlistAuditEvent(oldConfig.getAllowedPaths(), newConfig.getAllowedPaths(), this.basicAuthAuditLogHandler::logAllowedPathsChange);
        this.logAllowlistAuditEvent(oldConfig.getAllowedUsers(), newConfig.getAllowedUsers(), this.basicAuthAuditLogHandler::logAllowedUsersChange);
        this.logBasicAuthEnabledOrDisabledAuditEvent(oldConfig.isBlockRequests(), newConfig.isBlockRequests());
    }

    private void logBasicAuthEnabledOrDisabledAuditEvent(boolean oldIsBlockingBasicAuth, boolean isBlockingBasicAuth) {
        boolean hasSettingChange;
        boolean bl = hasSettingChange = oldIsBlockingBasicAuth != isBlockingBasicAuth;
        if (hasSettingChange) {
            if (isBlockingBasicAuth) {
                this.basicAuthAuditLogHandler.logBlockingBasicAuthRequests();
            } else {
                this.basicAuthAuditLogHandler.logDoNotBlockBasicAuthRequests();
            }
        }
    }

    private void logAllowlistAuditEvent(Set<String> oldAllowlist, Set<String> newAllowlist, BiConsumer<Set<String>, Set<String>> auditLogHandler) {
        if (!oldAllowlist.equals(newAllowlist)) {
            auditLogHandler.accept(oldAllowlist, newAllowlist);
        }
    }
}

