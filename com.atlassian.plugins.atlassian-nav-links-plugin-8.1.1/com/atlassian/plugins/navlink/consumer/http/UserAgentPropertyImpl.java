/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.ApplicationProperties
 *  com.google.common.base.Preconditions
 *  io.atlassian.util.concurrent.LazyReference
 */
package com.atlassian.plugins.navlink.consumer.http;

import com.atlassian.plugins.navlink.consumer.http.UserAgentProperty;
import com.atlassian.sal.api.ApplicationProperties;
import com.google.common.base.Preconditions;
import io.atlassian.util.concurrent.LazyReference;

public class UserAgentPropertyImpl
implements UserAgentProperty {
    private final ApplicationProperties applicationProperties;
    private final LazyReference<String> userAgent = new LazyReference<String>(){

        protected String create() throws Exception {
            return UserAgentPropertyImpl.this.createUserAgent();
        }
    };

    public UserAgentPropertyImpl(ApplicationProperties applicationProperties) {
        this.applicationProperties = (ApplicationProperties)Preconditions.checkNotNull((Object)applicationProperties);
    }

    @Override
    public String get() {
        return (String)this.userAgent.get();
    }

    private String createUserAgent() {
        return String.format("%s-%s (%s)", this.applicationProperties.getDisplayName(), this.applicationProperties.getVersion(), this.applicationProperties.getBuildNumber());
    }
}

