/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.core.ContentEntityManager
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.setup.settings.SettingsManager
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.user.UserAccessor
 *  com.atlassian.confluence.web.UrlBuilder
 *  com.atlassian.event.api.EventPublisher
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.user.UserKey
 *  com.google.common.collect.Maps
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.emailtracker.impl;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ContentEntityManager;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.plugins.emailtracker.EmailTrackerService;
import com.atlassian.confluence.plugins.emailtracker.EmailUrlValidator;
import com.atlassian.confluence.plugins.emailtracker.InvalidTrackingRequestException;
import com.atlassian.confluence.plugins.emailtracker.api.EmailReadEvent;
import com.atlassian.confluence.setup.settings.SettingsManager;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.web.UrlBuilder;
import com.atlassian.event.api.EventPublisher;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.user.UserKey;
import com.google.common.collect.Maps;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component(value="emailTrackerService")
public class EmailTrackerServiceImpl
implements EmailTrackerService {
    public static final String TRACKING_IMAGE_PATH = "/plugins/servlet/confluence-email-tracker/trackback.png";
    private final EventPublisher eventPublisher;
    private final UserAccessor userAccessor;
    private final ContentEntityManager contentEntityManager;
    private final EmailUrlValidator validator;
    private final SettingsManager settingsManager;

    public EmailTrackerServiceImpl(@ComponentImport EventPublisher eventPublisher, @ComponentImport UserAccessor userAccessor, @ComponentImport ContentEntityManager contentEntityManager, EmailUrlValidator validator, @ComponentImport SettingsManager settingsManager) {
        this.eventPublisher = eventPublisher;
        this.userAccessor = userAccessor;
        this.contentEntityManager = contentEntityManager;
        this.validator = validator;
        this.settingsManager = settingsManager;
    }

    @Override
    public void handleTrackingRequest(String urlToQuery, Map<String, String> requestParams) throws InvalidTrackingRequestException {
        Map<String, String> validParams = this.validator.validateQueryParameters(urlToQuery, requestParams);
        EmailReadEvent emailReadEvent = this.buildEmailReadEvent(validParams);
        this.eventPublisher.publish((Object)emailReadEvent);
    }

    @Override
    public String makeTrackingUrl(Map<String, Object> context) {
        String baseUrl = this.settingsManager.getGlobalSettings().getBaseUrl();
        String urlToQuery = baseUrl + TRACKING_IMAGE_PATH;
        Map<Object, Object> queryParams = Maps.newTreeMap();
        this.addFixedContext(context, (Map<String, String>)queryParams);
        this.addCustomContext((Map)context.get("_webFragmentInnerContext"), (Map<String, String>)queryParams);
        queryParams = this.validator.addValidationDataToQueryParameters(urlToQuery, (Map<String, String>)queryParams);
        UrlBuilder builder = new UrlBuilder(urlToQuery);
        for (Map.Entry<Object, Object> entry : queryParams.entrySet()) {
            builder.add((String)entry.getKey(), (String)entry.getValue());
        }
        return builder.toUrl();
    }

    private EmailReadEvent buildEmailReadEvent(Map<String, String> validParams) {
        ConfluenceUser recipient = null;
        ConfluenceUser actor = null;
        ContentEntityObject content = null;
        Date timestamp = null;
        String action = null;
        HashMap properties = Maps.newHashMap();
        for (Map.Entry<String, String> entry : validParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            if ("timestamp".equals(key)) {
                timestamp = new Date(Long.valueOf(value));
                continue;
            }
            if ("recipientKey".equals(key)) {
                recipient = this.userAccessor.getUserByKey(new UserKey(value));
                continue;
            }
            if ("actorKey".equals(key)) {
                actor = this.userAccessor.getUserByKey(new UserKey(value));
                continue;
            }
            if ("action".equals(key)) {
                action = value;
                continue;
            }
            if ("contentId".equals(key)) {
                ContentId contentId = ContentId.deserialise((String)value);
                content = this.contentEntityManager.getById(contentId.asLong());
                continue;
            }
            properties.put(key, value);
        }
        return new EmailReadEvent(timestamp, recipient, actor, action, content, properties);
    }

    private void addFixedContext(Map<String, Object> context, Map<String, String> queryParams) {
        Date timestamp = new Date();
        UserKey recipientKey = (UserKey)context.get("recipientKey");
        UserKey actorKey = (UserKey)context.get("actorKey");
        String action = (String)context.get("actionType");
        ContentId contentId = this.getContentId(context);
        if (action == null) {
            throw new IllegalArgumentException("Email Tracker web-panel context is missing 'action'");
        }
        queryParams.put("timestamp", String.valueOf(timestamp.getTime()));
        if (recipientKey != null) {
            queryParams.put("recipientKey", recipientKey.getStringValue());
        }
        if (actorKey != null) {
            queryParams.put("actorKey", actorKey.getStringValue());
        }
        queryParams.put("action", action);
        if (contentId != null) {
            queryParams.put("contentId", contentId.serialise());
        }
    }

    private ContentId getContentId(Map<String, Object> context) {
        Object contentIdObj = context.get("contentId");
        if (contentIdObj instanceof ContentId) {
            return (ContentId)contentIdObj;
        }
        if (contentIdObj instanceof String) {
            return ContentId.deserialise((String)((String)contentIdObj));
        }
        if (contentIdObj instanceof Long) {
            return ContentId.of((long)((Long)contentIdObj));
        }
        return null;
    }

    private void addCustomContext(Map<String, Object> innerContext, Map<String, String> queryParams) {
        if (innerContext != null) {
            for (Map.Entry<String, Object> entry : innerContext.entrySet()) {
                queryParams.put(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
    }
}

