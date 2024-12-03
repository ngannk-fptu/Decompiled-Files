/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.ApplicationLinkService
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityLinkService
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.api.TypeNotInstalledException
 *  com.atlassian.applinks.spi.application.TypeId
 *  com.atlassian.applinks.spi.util.TypeAccessor
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot
 *  io.atlassian.fugue.Option
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.projectcreate.producer.link.util;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.ApplicationLinkService;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityLinkService;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.api.TypeNotInstalledException;
import com.atlassian.applinks.spi.application.TypeId;
import com.atlassian.applinks.spi.util.TypeAccessor;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot;
import com.atlassian.plugins.projectcreate.producer.link.entities.LinkUrlComponents;
import com.atlassian.plugins.projectcreate.producer.link.util.ApplicationLinkUtilService;
import com.atlassian.plugins.projectcreate.producer.link.util.LinkingUrlFactory;
import io.atlassian.fugue.Option;
import java.net.URI;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ApplicationLinkUtilServiceImpl
implements ApplicationLinkUtilService {
    private final ApplicationLinkService applicationLinkService;
    private final LinkingUrlFactory linkingUrlFactory;
    private final TypeAccessor typeAccessor;
    private final EntityLinkService entityLinkService;

    @Autowired
    public ApplicationLinkUtilServiceImpl(@ComponentImport ApplicationLinkService applicationLinkService, LinkingUrlFactory linkingUrlFactory, @ComponentImport TypeAccessor typeAccessor, EntityLinkService entityLinkService) {
        this.applicationLinkService = applicationLinkService;
        this.linkingUrlFactory = linkingUrlFactory;
        this.typeAccessor = typeAccessor;
        this.entityLinkService = entityLinkService;
    }

    @Override
    public Option<ApplicationLink> getApplinkForUrl(String remoteUrl) {
        ApplicationLink linkMatch = null;
        int matchSize = 0;
        for (ApplicationLink link : this.applicationLinkService.getApplicationLinks()) {
            String displayUrl = link.getDisplayUrl().toString();
            if (!remoteUrl.startsWith(displayUrl) || displayUrl.length() <= matchSize) continue;
            linkMatch = link;
            matchSize = displayUrl.length();
        }
        return Option.option(linkMatch);
    }

    @Override
    public Option<LinkUrlComponents> getLinkUrlComponentsForUrl(String remoteUrl, String baseUrl) {
        baseUrl = StringUtils.stripEnd((String)baseUrl, (String)"/");
        if (remoteUrl != null && remoteUrl.startsWith(baseUrl)) {
            String[] components = remoteUrl.substring(baseUrl.length()).split("/");
            String entityType = null;
            String entityKey = null;
            if (components.length > 3 && components[0].equals("") && components[1].equals("rest") && components[2].equals("capabilities") && components[3].equals("aggregate-root")) {
                if (components.length > 4) {
                    entityType = components[4];
                }
                if (components.length > 5) {
                    entityKey = components[5];
                }
            }
            return Option.some((Object)new LinkUrlComponents(baseUrl, entityType, entityKey));
        }
        return Option.none();
    }

    @Override
    public Option<ApplicationLink> getApplinkForInstanceId(String appId) throws TypeNotInstalledException {
        for (ApplicationLink appLink : this.applicationLinkService.getApplicationLinks()) {
            if (!this.linkingUrlFactory.getInstanceIdHash(appLink.getDisplayUrl()).equals(appId)) continue;
            return Option.some((Object)appLink);
        }
        return Option.none();
    }

    @Override
    public Option<EntityType> getEntityTypeForTypeId(String typeId) {
        return Option.option((Object)this.typeAccessor.loadEntityType(new TypeId(typeId)));
    }

    @Override
    public RemoteRoot getRemoteRootForEntityLink(EntityLink link) {
        URI uri = link.getApplicationLink().getDisplayUrl();
        String type = TypeId.getTypeId((EntityType)link.getType()).get();
        return new RemoteRoot(uri, type, link.getKey());
    }
}

