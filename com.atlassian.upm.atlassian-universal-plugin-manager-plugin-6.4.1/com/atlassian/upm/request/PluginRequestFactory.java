/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 *  org.joda.time.DateTime
 */
package com.atlassian.upm.request;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.request.PluginRequest;
import org.joda.time.DateTime;

public interface PluginRequestFactory {
    public PluginRequest getPluginRequest(UserKey var1, String var2, String var3, DateTime var4, Option<String> var5);
}

