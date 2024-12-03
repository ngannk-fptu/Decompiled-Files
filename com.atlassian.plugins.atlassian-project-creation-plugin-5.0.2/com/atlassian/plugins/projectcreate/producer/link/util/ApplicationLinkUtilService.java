/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot
 *  io.atlassian.fugue.Option
 */
package com.atlassian.plugins.projectcreate.producer.link.util;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot;
import com.atlassian.plugins.projectcreate.producer.link.entities.LinkUrlComponents;
import io.atlassian.fugue.Option;

public interface ApplicationLinkUtilService {
    public Option<ApplicationLink> getApplinkForUrl(String var1);

    public Option<ApplicationLink> getApplinkForInstanceId(String var1) throws TypeNotInstalledException;

    public Option<LinkUrlComponents> getLinkUrlComponentsForUrl(String var1, String var2);

    public Option<EntityType> getEntityTypeForTypeId(String var1);

    public RemoteRoot getRemoteRootForEntityLink(EntityLink var1);
}

