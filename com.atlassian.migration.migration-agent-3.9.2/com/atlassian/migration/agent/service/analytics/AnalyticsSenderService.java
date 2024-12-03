/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  javax.annotation.ParametersAreNonnullByDefault
 *  okhttp3.Interceptor
 *  okhttp3.MediaType
 *  okhttp3.OkHttpClient
 *  okhttp3.Request
 *  okhttp3.RequestBody
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 *  org.springframework.web.util.UriComponentsBuilder
 */
package com.atlassian.migration.agent.service.analytics;

import com.atlassian.migration.agent.config.MigrationAgentConfiguration;
import com.atlassian.migration.agent.entity.AnalyticsEvent;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import com.atlassian.migration.agent.okhttp.HttpService;
import com.atlassian.migration.agent.okhttp.MediaTypes;
import com.atlassian.migration.agent.okhttp.OKHttpProxyBuilder;
import com.atlassian.migration.agent.okhttp.ServiceErrorCodeHandler;
import com.atlassian.migration.agent.service.analytics.ExternalAnalyticsEventDto;
import com.atlassian.migration.agent.service.analytics.ProcessedAnalyticsEvents;
import com.atlassian.migration.agent.service.impl.StargateHelper;
import com.atlassian.migration.agent.service.impl.UserAgentInterceptor;
import com.google.common.annotations.VisibleForTesting;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import javax.annotation.ParametersAreNonnullByDefault;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.springframework.web.util.UriComponentsBuilder;

@ParametersAreNonnullByDefault
public class AnalyticsSenderService {
    private static final Logger log = ContextLoggerFactory.getLogger(AnalyticsSenderService.class);
    private static final String EVENT_PATH = "/confluenceMigration/event";
    private static final String PLATFORM_EVENT_PATH = "/migrationPlatform/event";
    private static final String ATTRIBUTES = "attributes";
    private final HttpService httpService;
    private final MigrationAgentConfiguration configuration;

    public AnalyticsSenderService(MigrationAgentConfiguration configuration, UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        this(new HttpService(() -> AnalyticsSenderService.buildHttpClient(userAgentInterceptor, okHttpProxyBuilder), new ServiceErrorCodeHandler()), configuration);
    }

    @VisibleForTesting
    AnalyticsSenderService(HttpService httpService, MigrationAgentConfiguration configuration) {
        this.httpService = httpService;
        this.configuration = configuration;
    }

    boolean sendAnalyticsEventBatch(String containerToken, Collection<ExternalAnalyticsEventDto> events, String eventPath) {
        Request request = StargateHelper.requestBuilder(containerToken, this.configuration.isBypassStargate()).addHeader("Accept", "application/json").url(this.getUriBuilderForMigrationAnalytics().path(eventPath).toUriString()).post(RequestBody.create((MediaType)MediaTypes.APPLICATION_JSON_TYPE, (String)Jsons.valueAsString(events))).build();
        return this.httpService.callJson(request, new TypeReference<Boolean>(){});
    }

    public boolean isValidAnalyticsEvent(String rawEvent) {
        if (StringUtils.isBlank((CharSequence)rawEvent)) {
            log.warn("Skipping event with empty payload");
            return false;
        }
        if (rawEvent.length() > this.getConfiguredMaxEventLength()) {
            log.warn("Skipping event with large payload of {} starting with {}", (Object)rawEvent.length(), (Object)this.getAbbreviatedEvent(rawEvent));
            return false;
        }
        return true;
    }

    private void processAnalyticsEvent(AnalyticsEvent event, List<AnalyticsEvent> confluenceMigrationsEventsBatch, List<AnalyticsEvent> migrationPlatformEventsBatch, Collection<ExternalAnalyticsEventDto> confluenceMigrationEvents, Collection<ExternalAnalyticsEventDto> migrationPlatformEvents) {
        Map analyticalEvent = (Map)Jsons.readValue(event.getEvent(), (TypeReference)new TypeReference<Map<String, Object>>(){});
        HashMap<String, Object> finalAnalyticalEvent = new HashMap<String, Object>();
        if (analyticalEvent.getOrDefault(ATTRIBUTES, null) != null && ((Object)((Map)analyticalEvent.getOrDefault(ATTRIBUTES, null)).getOrDefault("platformEvent", false)).equals(true)) {
            ((Map)analyticalEvent.get(ATTRIBUTES)).remove("platformEvent");
            finalAnalyticalEvent.putAll(analyticalEvent);
            migrationPlatformEvents.add(new ExternalAnalyticsEventDto(event.getEventType(), finalAnalyticalEvent));
            migrationPlatformEventsBatch.add(event);
        } else {
            finalAnalyticalEvent.putAll(analyticalEvent);
            confluenceMigrationEvents.add(new ExternalAnalyticsEventDto(event.getEventType(), finalAnalyticalEvent));
            confluenceMigrationsEventsBatch.add(event);
        }
    }

    private void sendAnalyticsEvents(String containerToken, List<AnalyticsEvent> batch, List<AnalyticsEvent> successfullySentEvents, List<AnalyticsEvent> unsuccessfullySentEvents) {
        ArrayList confluenceMigrationsEventsBatch = new ArrayList();
        ArrayList migrationPlatformEventsBatch = new ArrayList();
        ArrayList<ExternalAnalyticsEventDto> confluenceMigrationEvents = new ArrayList<ExternalAnalyticsEventDto>();
        ArrayList<ExternalAnalyticsEventDto> migrationPlatformEvents = new ArrayList<ExternalAnalyticsEventDto>();
        batch.forEach(event -> {
            String rawEvent = event.getEvent();
            if (!this.isValidAnalyticsEvent(rawEvent)) {
                return;
            }
            this.processAnalyticsEvent((AnalyticsEvent)event, confluenceMigrationsEventsBatch, migrationPlatformEventsBatch, (Collection<ExternalAnalyticsEventDto>)confluenceMigrationEvents, (Collection<ExternalAnalyticsEventDto>)migrationPlatformEvents);
        });
        if (!CollectionUtils.isEmpty(confluenceMigrationEvents)) {
            boolean isConfluenceMigrationsSendEventBatchSuccessful = this.sendAnalyticsEventBatch(containerToken, confluenceMigrationEvents, EVENT_PATH);
            if (isConfluenceMigrationsSendEventBatchSuccessful) {
                successfullySentEvents.addAll(confluenceMigrationsEventsBatch);
            } else {
                unsuccessfullySentEvents.addAll(confluenceMigrationsEventsBatch);
            }
        }
        if (!CollectionUtils.isEmpty(migrationPlatformEvents)) {
            boolean isMigrationPlatformSendEventBatchSuccessful = this.sendAnalyticsEventBatch(containerToken, migrationPlatformEvents, PLATFORM_EVENT_PATH);
            if (isMigrationPlatformSendEventBatchSuccessful) {
                successfullySentEvents.addAll(migrationPlatformEventsBatch);
            } else {
                unsuccessfullySentEvents.addAll(migrationPlatformEventsBatch);
            }
        }
    }

    public ProcessedAnalyticsEvents processAndSendAnalyticsEvents(String containerToken, List<AnalyticsEvent> batch) {
        ArrayList<AnalyticsEvent> successfullySentEvents = new ArrayList<AnalyticsEvent>();
        ArrayList<AnalyticsEvent> unsuccessfullySentEvents = new ArrayList<AnalyticsEvent>();
        this.sendAnalyticsEvents(containerToken, batch, successfullySentEvents, unsuccessfullySentEvents);
        return new ProcessedAnalyticsEvents(successfullySentEvents, unsuccessfullySentEvents);
    }

    private String getAbbreviatedEvent(String event) {
        return StringUtils.abbreviate((String)event, (int)100);
    }

    private int getConfiguredMaxEventLength() {
        return this.configuration.getAnalyticsSenderMaxEventLength();
    }

    private UriComponentsBuilder getUriBuilderForMigrationAnalytics() {
        return UriComponentsBuilder.fromHttpUrl((String)this.configuration.getMigrationAnalyticsServiceBaseUrl());
    }

    private static OkHttpClient buildHttpClient(UserAgentInterceptor userAgentInterceptor, OKHttpProxyBuilder okHttpProxyBuilder) {
        return okHttpProxyBuilder.getProxyBuilder().connectTimeout(5L, TimeUnit.SECONDS).followRedirects(true).followSslRedirects(true).readTimeout(10L, TimeUnit.SECONDS).writeTimeout(20L, TimeUnit.SECONDS).addInterceptor((Interceptor)userAgentInterceptor).build();
    }
}

