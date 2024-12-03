/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationId
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.PropertySet
 *  com.atlassian.applinks.spi.application.TypeId
 */
package com.atlassian.applinks.core.property;

import com.atlassian.applinks.api.ApplicationId;
import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.PropertySet;
import com.atlassian.applinks.core.property.ApplicationLinkProperties;
import com.atlassian.applinks.core.property.EntityLinkProperties;
import com.atlassian.applinks.spi.application.TypeId;

public interface PropertyService {
    public PropertySet getProperties(ApplicationLink var1);

    public EntityLinkProperties getProperties(EntityLink var1);

    public ApplicationLinkProperties getApplicationLinkProperties(ApplicationId var1);

    public PropertySet getGlobalAdminProperties();

    public PropertySet getLocalEntityProperties(String var1, TypeId var2);
}

