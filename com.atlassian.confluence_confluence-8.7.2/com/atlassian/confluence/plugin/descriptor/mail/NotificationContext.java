/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.event.Event
 *  com.atlassian.user.User
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  com.google.common.collect.Sets
 *  javax.activation.DataSource
 */
package com.atlassian.confluence.plugin.descriptor.mail;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.core.ConfluenceEntityObject;
import com.atlassian.confluence.mail.notification.Notification;
import com.atlassian.confluence.pages.ContentConvertible;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.event.Event;
import com.atlassian.user.User;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import javax.activation.DataSource;

public class NotificationContext {
    private static final String CONTEXT_KEY_EVENT = "event";
    private static final String CONTEXT_KEY_EVENT_CONTENT = "content";
    private static final String CONTEXT_KEY_EVENT_CONTENT_ID = "contentId";
    private static final String CONTEXT_KEY_SPACE = "space";
    private static final String MANAGE_NOTIFICATIONS_OVERRIDE = "manageNotificationsOverride";
    private static final String WATCH_TYPE = "watchType";
    private static final String CONTEXT_KEY_RECIPIENT = "recipient";
    private static final String CONTEXT_KEY_RECIPIENT_KEY = "recipientKey";
    private static final String CONTEXT_KEY_I18N = "i18n";
    private static final String CONTEXT_KEY_ACTION_TYPE = "actionType";
    private static final String CONTEXT_KEY_ACTOR = "actor";
    private static final String CONTEXT_KEY_ACTOR_KEY = "actorKey";
    private static final String WEB_FRAGMENT_CONTEXT = "webFragmentContext";
    public static final String WEB_FRAGMENT_INNER_CONTEXT = "_webFragmentInnerContext";
    private Map<String, Object> map = Maps.newHashMap();
    private Set<DataSource> templateImageDataSources = Sets.newHashSet();

    public NotificationContext() {
        this(null, null);
    }

    public NotificationContext(NotificationContext context) {
        this(context, null);
    }

    public NotificationContext(NotificationContext context, Set<DataSource> dataSources) {
        if (context != null) {
            this.map.putAll(context.getMap());
            this.templateImageDataSources = Sets.newHashSet(context.getTemplateImageDataSources());
        }
        if (dataSources != null) {
            this.templateImageDataSources.addAll(dataSources);
        }
    }

    public Map<String, Object> getMap() {
        return this.map;
    }

    public Object get(String key) {
        return this.map.get(key);
    }

    public void put(String key, Object value) {
        this.map.put(key, value);
    }

    public void putAll(Map<? extends String, ?> map) {
        this.map.putAll(map);
    }

    public ConfluenceEntityObject getContent() {
        return (ConfluenceEntityObject)this.map.get(CONTEXT_KEY_EVENT_CONTENT);
    }

    public void setContent(ConfluenceEntityObject entity) {
        this.map.put(CONTEXT_KEY_EVENT_CONTENT, entity);
    }

    public ContentId getContentId() {
        Object contentIdValue = this.map.get(CONTEXT_KEY_EVENT_CONTENT_ID);
        if (contentIdValue instanceof ContentId) {
            return (ContentId)contentIdValue;
        }
        ConfluenceEntityObject content = this.getContent();
        if (content instanceof ContentConvertible) {
            return ((ContentConvertible)((Object)content)).getContentId();
        }
        return null;
    }

    public void setContentId(ContentId contentId) {
        this.map.put(CONTEXT_KEY_EVENT_CONTENT_ID, contentId);
    }

    public Event getEvent() {
        return (Event)this.map.get(CONTEXT_KEY_EVENT);
    }

    public void setEvent(Event event) {
        this.map.put(CONTEXT_KEY_EVENT, event);
    }

    public boolean isManageNotificationOverridden() {
        return this.map.get(MANAGE_NOTIFICATIONS_OVERRIDE) == Boolean.TRUE;
    }

    public void setManageNotificationOverridden(boolean isOverridden) {
        this.map.put(MANAGE_NOTIFICATIONS_OVERRIDE, isOverridden);
    }

    public Notification.WatchType getWatchType() {
        return (Notification.WatchType)((Object)this.map.get(WATCH_TYPE));
    }

    public void setWatchType(Notification.WatchType watchType) {
        this.map.put(WATCH_TYPE, (Object)watchType);
    }

    public void setSpace(Space space) {
        this.map.put(CONTEXT_KEY_SPACE, space);
    }

    public void setRecipient(User recipient) {
        this.map.put(CONTEXT_KEY_RECIPIENT, recipient);
        this.map.put("remoteUser", recipient);
        if (recipient instanceof ConfluenceUser) {
            this.map.put(CONTEXT_KEY_RECIPIENT_KEY, ((ConfluenceUser)recipient).getKey());
        }
    }

    public User getRecipient() {
        return (User)this.map.get(CONTEXT_KEY_RECIPIENT);
    }

    public void setActor(User actor) {
        this.map.put(CONTEXT_KEY_ACTOR, actor);
        if (actor instanceof ConfluenceUser) {
            this.map.put(CONTEXT_KEY_ACTOR_KEY, ((ConfluenceUser)actor).getKey());
        }
    }

    public User getActor() {
        return (User)this.map.get(CONTEXT_KEY_ACTOR);
    }

    public void setAction(String action) {
        this.map.put(CONTEXT_KEY_ACTION_TYPE, action);
    }

    public String getAction() {
        return (String)this.map.get(CONTEXT_KEY_ACTION_TYPE);
    }

    public void setI18n(I18NBean i18NBean) {
        this.map.put(CONTEXT_KEY_I18N, i18NBean);
        this.map.put("i18nBean", i18NBean);
    }

    public void addTemplateImage(DataSource dataSource) {
        this.templateImageDataSources.add(dataSource);
    }

    public Collection<DataSource> getTemplateImageDataSources() {
        return Lists.newArrayList(this.templateImageDataSources);
    }

    public void addWebFragmentContext() {
        ContentId contentId;
        HashMap wfContext = Maps.newHashMap();
        this.copyToMapIfPresent(wfContext, CONTEXT_KEY_RECIPIENT_KEY);
        this.copyToMapIfPresent(wfContext, CONTEXT_KEY_ACTOR_KEY);
        String action = this.getAction();
        if (action != null) {
            wfContext.put(CONTEXT_KEY_ACTION_TYPE, action);
        }
        if ((contentId = this.getContentId()) != null) {
            wfContext.put(CONTEXT_KEY_EVENT_CONTENT_ID, contentId);
        }
        this.map.put(WEB_FRAGMENT_CONTEXT, wfContext);
    }

    private void copyToMapIfPresent(Map<String, Object> target, String key) {
        Object value = this.map.get(key);
        if (value != null) {
            target.put(key, value);
        }
    }

    public void addToWebFragmentContext(String key, String value) {
        Map wfContext = (Map)this.map.get(WEB_FRAGMENT_CONTEXT);
        if (wfContext == null) {
            throw new IllegalStateException("addToWebFragmentContext cannot be called before addWebFragmentContext");
        }
        Map innerContext = (Map)this.map.get(WEB_FRAGMENT_INNER_CONTEXT);
        if (innerContext == null) {
            innerContext = Maps.newHashMap();
            wfContext.put(WEB_FRAGMENT_INNER_CONTEXT, innerContext);
        }
        innerContext.put(key, value);
    }
}

