/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.activeobjects.external.ActiveObjects
 *  com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask
 *  com.atlassian.activeobjects.external.ModelVersion
 *  com.atlassian.webhooks.WebhookEvent
 *  com.atlassian.webhooks.WebhookScope
 *  com.google.common.base.Preconditions
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  javax.annotation.Nonnull
 *  org.apache.commons.lang3.StringUtils
 *  org.json.JSONArray
 *  org.json.JSONException
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.webhooks.internal.dao;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.external.ActiveObjectsUpgradeTask;
import com.atlassian.activeobjects.external.ModelVersion;
import com.atlassian.webhooks.WebhookEvent;
import com.atlassian.webhooks.WebhookScope;
import com.atlassian.webhooks.internal.dao.ao.AoWebhook;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookConfigurationEntry;
import com.atlassian.webhooks.internal.dao.ao.AoWebhookEvent;
import com.atlassian.webhooks.internal.dao.ao.v0.WebHookListenerAOV0;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Map;
import javax.annotation.Nonnull;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MigrateToV1Task
implements ActiveObjectsUpgradeTask {
    private static final Logger log = LoggerFactory.getLogger(MigrateToV1Task.class);

    public ModelVersion getModelVersion() {
        return ModelVersion.valueOf((String)"1");
    }

    public void upgrade(ModelVersion currentVersion, ActiveObjects ao) {
        Preconditions.checkState((boolean)currentVersion.isSame(ModelVersion.valueOf((String)"0")), (Object)"Can only migrate from v0 to v1");
        log.info("Migrating webhooks to version 1");
        ao.migrate(new Class[]{WebHookListenerAOV0.class, AoWebhook.class, AoWebhookConfigurationEntry.class, AoWebhookEvent.class});
        ao.stream(WebHookListenerAOV0.class, hook -> this.migrate(ao, (WebHookListenerAOV0)hook));
        log.info("Migration is complete");
    }

    private Map<String, String> createConfiguration(WebHookListenerAOV0 webHookListenerAOV0) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        if (StringUtils.isNotEmpty((CharSequence)webHookListenerAOV0.getFilters())) {
            builder.put((Object)"FILTERS", (Object)webHookListenerAOV0.getFilters());
        }
        if (StringUtils.isNotEmpty((CharSequence)webHookListenerAOV0.getRegistrationMethod())) {
            builder.put((Object)"REGISTRATION_METHOD", (Object)webHookListenerAOV0.getRegistrationMethod());
        }
        if (StringUtils.isNotEmpty((CharSequence)webHookListenerAOV0.getParameters())) {
            builder.put((Object)"PARAMETERS", (Object)webHookListenerAOV0.getParameters());
        }
        if (StringUtils.isNotEmpty((CharSequence)webHookListenerAOV0.getDescription())) {
            builder.put((Object)"DESCRIPTION", (Object)webHookListenerAOV0.getDescription());
        }
        if (StringUtils.isNotEmpty((CharSequence)webHookListenerAOV0.getLastUpdatedUser())) {
            builder.put((Object)"LAST_UPDATED_USER", (Object)webHookListenerAOV0.getLastUpdatedUser());
        }
        if (webHookListenerAOV0.getLastUpdated() != null) {
            builder.put((Object)"LAST_UPDATED", (Object)webHookListenerAOV0.getLastUpdated().toInstant().atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        }
        builder.put((Object)"EXCLUDE_BODY", (Object)Boolean.toString(webHookListenerAOV0.isExcludeBody()));
        return builder.build();
    }

    private void migrate(ActiveObjects ao, WebHookListenerAOV0 webHookListenerAOV0) {
        Date now = new Date();
        ImmutableMap hook = ImmutableMap.builder().put((Object)"CREATED", (Object)now).put((Object)"UPDATED", (Object)now).put((Object)"URL", (Object)webHookListenerAOV0.getUrl()).put((Object)"NAME", (Object)webHookListenerAOV0.getName()).put((Object)"ACTIVE", (Object)webHookListenerAOV0.isEnabled()).put((Object)"SCOPE_TYPE", (Object)WebhookScope.GLOBAL.getType()).build();
        AoWebhook aoWebhook = (AoWebhook)ao.create(AoWebhook.class, (Map)hook);
        this.createConfiguration(webHookListenerAOV0).forEach((key, value) -> {
            AoWebhookConfigurationEntry cfr_ignored_0 = (AoWebhookConfigurationEntry)ao.create(AoWebhookConfigurationEntry.class, (Map)ImmutableMap.of((Object)"KEY", (Object)key, (Object)"WEBHOOKID", (Object)aoWebhook.getID(), (Object)"VALUE", (Object)value));
        });
        this.getEventsFor(webHookListenerAOV0).forEach(event -> {
            AoWebhookEvent cfr_ignored_0 = (AoWebhookEvent)ao.create(AoWebhookEvent.class, (Map)ImmutableMap.of((Object)"EVENT_ID", (Object)event.getId(), (Object)"WEBHOOKID", (Object)aoWebhook.getID()));
        });
    }

    private Iterable<WebhookEvent> getEventsFor(WebHookListenerAOV0 webhook) {
        if (StringUtils.isEmpty((CharSequence)webhook.getEvents())) {
            return ImmutableList.of();
        }
        ImmutableList.Builder builder = ImmutableList.builder();
        try {
            JSONArray jsonArray = new JSONArray(webhook.getEvents());
            for (int i = 0; i < jsonArray.length(); ++i) {
                String event = jsonArray.getString(i);
                builder.add((Object)new MigrateWebhookEvent(event));
            }
        }
        catch (JSONException e) {
            log.warn("A webhook was unable to migrate the events for id:[{}]", (Object)webhook.getID());
        }
        return builder.build();
    }

    private static class MigrateWebhookEvent
    implements WebhookEvent {
        private final String id;

        MigrateWebhookEvent(String id) {
            this.id = id;
        }

        @Nonnull
        public String getId() {
            return this.id;
        }

        @Nonnull
        public String getI18nKey() {
            return this.id;
        }
    }
}

