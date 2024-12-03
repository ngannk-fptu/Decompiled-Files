/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.api.event;

import com.atlassian.plugin.notifications.api.TextUtil;
import com.atlassian.plugin.notifications.api.event.NotificationEvent;
import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public abstract class AbstractNotificationEvent<E>
implements NotificationEvent<E> {
    private final Date time = new Date();
    private final E event;

    protected AbstractNotificationEvent(E event) {
        this.event = event;
    }

    @Override
    public Date getTime() {
        return this.time;
    }

    @Override
    public E getOriginalEvent() {
        return this.event;
    }

    @Override
    public Map<String, Object> getParams(I18nResolver i18n, UserKey recipient) {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("textUtil", new TextUtil());
        if (this.getAuthor() != null) {
            map.put("author", this.getAuthor());
        }
        return map;
    }
}

