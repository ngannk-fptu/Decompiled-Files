/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.user.User
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  io.atlassian.fugue.Either
 */
package com.atlassian.confluence.impl.admin.actions;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.confluence.cluster.ClusterNodeInformation;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.event.events.analytics.MonitoringAnalytics;
import com.atlassian.confluence.event.events.monitoring.AppMonitoringAuditEvent;
import com.atlassian.confluence.event.events.monitoring.IpdMonitoringAuditEvent;
import com.atlassian.confluence.event.events.monitoring.JmxMonitoringAuditEvent;
import com.atlassian.confluence.impl.metrics.ConfluenceJmxConfigService;
import com.atlassian.confluence.impl.profiling.NodeJmxMonitoringConfig;
import com.atlassian.confluence.impl.security.AdminOnly;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.user.User;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import io.atlassian.fugue.Either;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.stream.Collectors;

@WebSudoRequired
@AdminOnly
public class ConfigureMonitoringAction
extends ConfluenceActionSupport {
    private static final long serialVersionUID = 1L;
    private transient ConfluenceJmxConfigService confluenceJmxConfigService;
    private transient EventPublisher eventPublisher;
    private boolean isChecked;
    private boolean wasIpdToggleChecked;
    private boolean wasJmxToggleChecked;
    private boolean wasAppToggleChecked;
    private List<NodeConfigDTO> nodesConfigs;

    @VisibleForTesting
    ConfigureMonitoringAction(ConfluenceJmxConfigService confluenceJmxConfigService, EventPublisher eventPublisher) {
        this.confluenceJmxConfigService = confluenceJmxConfigService;
        this.eventPublisher = eventPublisher;
    }

    public ConfigureMonitoringAction() {
    }

    @PermittedMethods(value={HttpMethod.GET})
    public String execute() {
        this.nodesConfigs = this.fetchNodesConfigs();
        return "success";
    }

    private List<NodeConfigDTO> fetchNodesConfigs() {
        return this.confluenceJmxConfigService.getNodesJmxMonitoringConfigs().stream().map(n -> new NodeConfigDTO(n.getClusterNode(), this.completeToEither(n.getCompletionStage()))).collect(Collectors.toList());
    }

    private <T> Either<Throwable, T> completeToEither(CompletionStage<T> completionStage) {
        try {
            return Either.right(completionStage.toCompletableFuture().get());
        }
        catch (Throwable t) {
            return Either.left((Object)t);
        }
    }

    public boolean isAppMonitoringEnabled() {
        return this.confluenceJmxConfigService.isAppMonitoringEnabled();
    }

    public boolean isIpdMonitoringEnabled() {
        return this.confluenceJmxConfigService.isIpdMonitoringEnabled();
    }

    @VisibleForTesting
    boolean isChecked() {
        return this.isChecked;
    }

    public boolean isJmxEnabled() {
        return this.confluenceJmxConfigService.isJmxEnabledOnCluster();
    }

    @Override
    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.ADMINISTER, PermissionManager.TARGET_APPLICATION);
    }

    public List<NodeConfigDTO> getNodesConfigs() {
        return this.nodesConfigs;
    }

    public boolean isSystemPropertySetOnNodes() {
        return this.nodesConfigs.stream().anyMatch(ns -> (Boolean)ns.config.right().map(s -> s.isSystemPropertySet()).getOrElse((Object)false));
    }

    public void setIsChecked(String isChecked) {
        this.isChecked = "on".equalsIgnoreCase(isChecked);
    }

    public void setConfluenceJmxConfigService(ConfluenceJmxConfigService confluenceJmxConfigService) {
        this.confluenceJmxConfigService = confluenceJmxConfigService;
    }

    public void setEventPublisher(EventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @PermittedMethods(value={HttpMethod.POST})
    public String toggleAppMonitoringEnabled() {
        this.saveTogglesState();
        this.confluenceJmxConfigService.setAppMonitoringEnabled(this.isChecked);
        this.eventPublisher.publish((Object)new AppMonitoringAuditEvent(this.wasJmxToggleChecked, this.wasIpdToggleChecked, this.wasAppToggleChecked));
        this.eventPublisher.publish((Object)new MonitoringAnalytics.AppMonitoringToggledAnalyticsEvent(this.isChecked));
        return "success";
    }

    @PermittedMethods(value={HttpMethod.POST})
    public String toggleIpdMonitoringEnabled() {
        this.saveTogglesState();
        this.confluenceJmxConfigService.setIpdMonitoringEnabled(this.isChecked);
        this.eventPublisher.publish((Object)new IpdMonitoringAuditEvent(this.wasJmxToggleChecked, this.wasIpdToggleChecked, this.wasAppToggleChecked));
        this.eventPublisher.publish((Object)new MonitoringAnalytics.IpdMonitoringToggledAnalyticsEvent(this.isChecked));
        return "success";
    }

    @PermittedMethods(value={HttpMethod.POST})
    public String toggleJmxEnabled() {
        this.saveTogglesState();
        this.confluenceJmxConfigService.setJmxMonitoringEnabled(this.isChecked);
        this.eventPublisher.publish((Object)new JmxMonitoringAuditEvent(this.wasJmxToggleChecked, this.wasIpdToggleChecked, this.wasAppToggleChecked));
        this.eventPublisher.publish((Object)new MonitoringAnalytics.JmxToggledAnalyticsEvent(this.isChecked));
        return "success";
    }

    private void saveTogglesState() {
        this.wasJmxToggleChecked = this.isJmxEnabled();
        this.wasAppToggleChecked = this.isAppMonitoringEnabled();
        this.wasIpdToggleChecked = this.isIpdMonitoringEnabled();
    }

    public static class NodeConfigDTO {
        private final ClusterNodeInformation node;
        private final Either<Throwable, NodeJmxMonitoringConfig> config;

        public NodeConfigDTO(ClusterNodeInformation node, Either<Throwable, NodeJmxMonitoringConfig> config) {
            this.node = node;
            this.config = config;
        }

        public ClusterNodeInformation getNode() {
            return this.node;
        }

        public Either<Throwable, NodeJmxMonitoringConfig> getConfig() {
            return this.config;
        }
    }
}

