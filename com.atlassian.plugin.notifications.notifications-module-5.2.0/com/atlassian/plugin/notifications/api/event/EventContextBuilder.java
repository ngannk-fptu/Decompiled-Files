/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.google.common.collect.Maps
 */
package com.atlassian.plugin.notifications.api.event;

import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.plugin.notifications.api.medium.ServerConfiguration;
import com.atlassian.plugin.notifications.spi.UserRole;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.google.common.collect.Maps;
import java.util.HashMap;
import java.util.Map;

public class EventContextBuilder {
    public static final String EVENT_TYPE_NAME = "eventTypeName";
    public static final String EVENT_TYPE_KEY = "eventTypeKey";
    private UserManager userManager;

    public static Map<String, Object> buildContext(NotificationEvent event, I18nResolver i18n, UserKey recipient, UserRole role, ServerConfiguration config) {
        HashMap ret = Maps.newHashMap();
        ret.put("i18n", i18n);
        if (event != null) {
            ret.putAll(event.getParams(i18n, recipient));
            ret.put("event", event);
            if (event.getOriginalEvent() != null) {
                ret.put("originalEvent", event.getOriginalEvent());
            }
            ret.put(EVENT_TYPE_NAME, event.getName(i18n));
            ret.put(EVENT_TYPE_KEY, event.getKey());
        }
        if (recipient != null) {
            ret.put("userKey", recipient);
            ret.put("recipientKey", recipient);
        }
        if (role != null) {
            ret.put("role", role);
        }
        if (config != null) {
            ret.put("config", config);
        }
        return ret;
    }
}

