/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.configuration;

import com.atlassian.user.configuration.CacheConfiguration;
import java.util.Map;

public class DefaultCacheConfiguration
implements CacheConfiguration {
    private final Map componentClassNames;

    public DefaultCacheConfiguration(Map componentClassNames) {
        this.componentClassNames = componentClassNames;
    }

    public String getUserManagerClassName() {
        return (String)this.componentClassNames.get("userManager");
    }

    public String getGroupManagerClassName() {
        return (String)this.componentClassNames.get("groupManager");
    }

    public String getPropertySetFactoryClassName() {
        return (String)this.componentClassNames.get("propertySetFactory");
    }
}

