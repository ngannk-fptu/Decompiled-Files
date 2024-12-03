/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.atlassian.sal.api.user.UserProfile
 *  javax.annotation.Nullable
 *  javax.ws.rs.core.UriBuilder
 */
package com.atlassian.upm.core.rest;

import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.user.UserProfile;
import com.atlassian.upm.core.rest.CoreUriBuilder;
import com.atlassian.upm.core.rest.UpmUriEscaper;
import com.atlassian.upm.core.rest.async.AsyncTaskResource;
import com.atlassian.upm.core.rest.async.LegacyAsyncTaskResource;
import com.atlassian.upm.core.rest.resources.AuditLogSyndicationResource;
import com.atlassian.upm.core.rest.resources.ChangeRequiringRestartCollectionResource;
import com.atlassian.upm.core.rest.resources.ChangeRequiringRestartResource;
import com.atlassian.upm.core.rest.resources.PluginCollectionResource;
import com.atlassian.upm.core.rest.resources.PluginMediaResource;
import com.atlassian.upm.core.rest.resources.PluginModuleResource;
import com.atlassian.upm.core.rest.resources.PluginResource;
import com.atlassian.upm.core.rest.resources.PluginSummaryResource;
import com.atlassian.upm.core.rest.resources.PluginUninstallResource;
import com.atlassian.upm.core.test.rest.resources.SysResource;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Optional;
import javax.annotation.Nullable;
import javax.ws.rs.core.UriBuilder;

public abstract class BaseUriBuilder
extends CoreUriBuilder {
    protected BaseUriBuilder(ApplicationProperties applicationProperties, String baseUrl) {
        super(applicationProperties, baseUrl);
    }

    public final URI buildPluginUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildPluginSummaryUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginSummaryResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildChangeRequiringRestart(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(ChangeRequiringRestartResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildPluginIconLocationUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginMediaResource.class).path("plugin-icon").build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildPluginLogoLocationUri(String pluginKey) {
        return this.newPluginBaseUriBuilder().path(PluginMediaResource.class).path("plugin-logo").build(new Object[]{UpmUriEscaper.escape(pluginKey)});
    }

    public final URI buildPluginModuleUri(String pluginKey, String key) {
        return this.newPluginBaseUriBuilder().path(PluginModuleResource.class).build(new Object[]{UpmUriEscaper.escape(pluginKey), UpmUriEscaper.escape(key)});
    }

    public final URI buildPluginCollectionUri() {
        return this.newPluginBaseUriBuilder().path(PluginCollectionResource.class).build(new Object[0]);
    }

    public final URI buildBulkPluginUninstallationUri() {
        return this.newPluginBaseUriBuilder().path(PluginUninstallResource.class).build(new Object[0]);
    }

    public final URI buildLegacyPendingTasksUri() {
        return this.newPluginBaseUriBuilder().path(LegacyAsyncTaskResource.class).build(new Object[0]);
    }

    public final URI buildLegacyPendingTaskUri(String taskId) {
        return this.newPluginBaseUriBuilder().path(LegacyAsyncTaskResource.class).path(taskId).build(new Object[0]);
    }

    public final URI buildAbsoluteLegacyPendingTaskUri(String taskId) {
        return this.makeAbsolute(this.buildLegacyPendingTaskUri(taskId));
    }

    public final URI buildPendingTasksUri() {
        return this.newPluginBaseUriBuilder().path(AsyncTaskResource.class).build(new Object[0]);
    }

    public final URI buildPendingTaskUri(String taskId) {
        return this.newPluginBaseUriBuilder().path(AsyncTaskResource.class).path(taskId).build(new Object[0]);
    }

    public URI buildChangesRequiringRestartUri() {
        return this.newPluginBaseUriBuilder().path(ChangeRequiringRestartCollectionResource.class).build(new Object[0]);
    }

    public final URI buildBillingNotificationsUri(String addonKey) {
        return UriBuilder.fromPath((String)"/instance/notifications/paid-addon").build(new Object[0]);
    }

    public final URI buildIsDevModeUri() {
        return this.newPluginBaseUriBuilder().path(SysResource.class).path("dev-mode").build(new Object[0]);
    }

    public final URI buildAuditLogFeedUri() {
        return this.newPluginBaseUriBuilder().path(AuditLogSyndicationResource.class).build(new Object[0]);
    }

    public final URI buildAuditLogFeedUri(int maxResults, int startIndex) {
        return this.newPluginBaseUriBuilder().path(AuditLogSyndicationResource.class).queryParam("max-results", new Object[]{maxResults}).queryParam("start-index", new Object[]{startIndex}).build(new Object[0]);
    }

    public final URI buildAuditLogMaxEntriesUri() {
        return this.newPluginBaseUriBuilder().path(AuditLogSyndicationResource.class).path("max-entries").build(new Object[0]);
    }

    public final URI buildAuditLogPurgeAfterUri() {
        return this.newPluginBaseUriBuilder().path(AuditLogSyndicationResource.class).path("purge-after").build(new Object[0]);
    }

    public final URI buildUpmUri() {
        return this.makeAbsolute(this.newApplicationBaseUriBuilder().path("/plugins/servlet/upm").build(new Object[0]));
    }

    public final URI buildUpmUri(String pluginKey) {
        return this.addFragment(this.buildUpmUri(), "manage/" + pluginKey);
    }

    @Nullable
    public final URI buildAbsoluteProfileUri(UserProfile userProfile) {
        return Optional.ofNullable(userProfile).map(UserProfile::getProfilePageUri).map(uri -> uri.isAbsolute() ? uri : this.makeAbsolute(this.newApplicationBaseUriBuilder().path(uri.getPath()).replaceQuery(uri.getQuery()).build(new Object[0]))).orElse(null);
    }

    protected final URI addFragment(URI uri, String fragment) {
        try {
            return UriBuilder.fromUri((URI)uri).queryParam("fragment", new Object[]{URLEncoder.encode(fragment, "UTF-8")}).build(new Object[0]);
        }
        catch (UnsupportedEncodingException e) {
            return UriBuilder.fromUri((URI)uri).queryParam("fragment", new Object[]{fragment}).build(new Object[0]);
        }
    }
}

