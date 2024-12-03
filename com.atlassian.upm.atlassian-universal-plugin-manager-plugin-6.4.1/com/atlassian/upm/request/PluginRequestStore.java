/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.user.UserKey
 */
package com.atlassian.upm.request;

import com.atlassian.sal.api.user.UserKey;
import com.atlassian.upm.api.util.Option;
import com.atlassian.upm.request.PluginRequest;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface PluginRequestStore {
    public List<PluginRequest> getRequests();

    public Map<String, Collection<PluginRequest>> getRequestsByPlugin(Integer var1, Integer var2);

    public Map<String, Collection<PluginRequest>> getRequestsByPluginExcludingUser(Integer var1, Integer var2, UserKey var3);

    public Map<String, PluginRequest> getRequestsByUser(UserKey var1);

    public List<PluginRequest> getRequests(String var1);

    public Option<PluginRequest> getRequest(String var1, UserKey var2);

    public void addRequest(PluginRequest var1);

    public void removeRequests(String var1);

    public void removeAllRequests();
}

