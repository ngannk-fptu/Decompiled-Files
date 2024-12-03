/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.common.properties.SystemProperties
 *  com.google.common.collect.ImmutableList
 *  com.sun.jersey.api.model.AbstractMethod
 *  com.sun.jersey.spi.container.ResourceFilter
 *  com.sun.jersey.spi.container.ResourceFilterFactory
 */
package com.atlassian.crowd.plugin.rest.provider;

import com.atlassian.crowd.common.properties.SystemProperties;
import com.atlassian.crowd.plugin.rest.provider.RestUsernameHeaderFilter;
import com.google.common.collect.ImmutableList;
import com.sun.jersey.api.model.AbstractMethod;
import com.sun.jersey.spi.container.ResourceFilter;
import com.sun.jersey.spi.container.ResourceFilterFactory;
import java.util.Collections;
import java.util.List;

public class RestUsernameHeaderFilterFactory
implements ResourceFilterFactory {
    public List<ResourceFilter> create(AbstractMethod abstractMethod) {
        if (((Boolean)SystemProperties.INCLUDE_USERNAME_HEADER_IN_RESPONSES.getValue()).booleanValue()) {
            return ImmutableList.of((Object)new RestUsernameHeaderFilter());
        }
        return Collections.emptyList();
    }
}

