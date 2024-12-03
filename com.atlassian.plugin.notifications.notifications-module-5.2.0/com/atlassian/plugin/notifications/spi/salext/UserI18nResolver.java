/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.message.I18nResolver
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.plugin.notifications.spi.salext;

import com.atlassian.sal.api.message.I18nResolver;
import com.atlassian.sal.api.user.UserKey;

public interface UserI18nResolver
extends I18nResolver {
    public void setUser(UserKey var1);
}

