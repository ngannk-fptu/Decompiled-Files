/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.json.marshal.Jsonable
 *  com.atlassian.sal.api.user.UserKey
 *  com.atlassian.sal.api.user.UserManager
 *  com.atlassian.webresource.api.data.WebResourceDataProvider
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 */
package com.atlassian.applinks.internal.web.data;

import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.internal.common.application.ApplicationTypes;
import com.atlassian.applinks.internal.common.json.JacksonJsonableMarshaller;
import com.atlassian.json.marshal.Jsonable;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.webresource.api.data.WebResourceDataProvider;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class ApplinksContextDataProvider
implements WebResourceDataProvider {
    private static final String CURRENT_USER_KEY = "currentUser";
    private static final String HOST_APPLICATION_KEY = "hostApplication";
    private static final String HOST_APPLICATION_ID_KEY = "id";
    private static final String HOST_APPLICATION_TYPE_KEY = "type";
    private final InternalHostApplication hostApplication;
    private final UserManager userManager;

    public ApplinksContextDataProvider(InternalHostApplication hostApplication, UserManager userManager) {
        this.hostApplication = hostApplication;
        this.userManager = userManager;
    }

    public Jsonable get() {
        return JacksonJsonableMarshaller.INSTANCE.marshal(this.getContextMap());
    }

    private Map<String, Object> getContextMap() {
        ImmutableMap.Builder contextMapBuilder = ImmutableMap.builder();
        this.addCurrentUser((ImmutableMap.Builder<String, Object>)contextMapBuilder);
        this.addHostApplication((ImmutableMap.Builder<String, Object>)contextMapBuilder);
        return contextMapBuilder.build();
    }

    private void addCurrentUser(ImmutableMap.Builder<String, Object> contextMapBuilder) {
        UserKey userKey = this.userManager.getRemoteUserKey();
        if (userKey != null) {
            contextMapBuilder.put((Object)CURRENT_USER_KEY, (Object)userKey.getStringValue());
        }
    }

    private void addHostApplication(ImmutableMap.Builder<String, Object> contextMapBuilder) {
        contextMapBuilder.put((Object)HOST_APPLICATION_KEY, (Object)ImmutableMap.of((Object)HOST_APPLICATION_ID_KEY, (Object)this.hostApplication.getId().toString(), (Object)HOST_APPLICATION_TYPE_KEY, (Object)ApplicationTypes.resolveApplicationTypeId(this.hostApplication.getType())));
    }
}

