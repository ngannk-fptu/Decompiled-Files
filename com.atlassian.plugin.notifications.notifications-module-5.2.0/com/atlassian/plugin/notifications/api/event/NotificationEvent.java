/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.api.event;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;
import java.util.Date;
import java.util.Map;

public interface NotificationEvent<E> {
    public Map<String, Object> getParams(I18nResolver var1, UserKey var2);

    public Date getTime();

    public E getOriginalEvent();

    public String getSubject();

    public String getName(I18nResolver var1);

    public String getKey();

    public UserKey getAuthor();
}

