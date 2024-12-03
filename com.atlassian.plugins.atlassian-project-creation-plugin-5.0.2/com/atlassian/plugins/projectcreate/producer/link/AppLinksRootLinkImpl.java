/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.applinks.api.ApplicationLink
 *  com.atlassian.applinks.api.EntityLink
 *  com.atlassian.applinks.api.EntityType
 *  com.atlassian.applinks.host.spi.EntityReference
 *  com.atlassian.applinks.host.spi.InternalHostApplication
 *  com.atlassian.applinks.spi.link.EntityLinkBuilderFactory
 *  com.atlassian.applinks.spi.link.MutatingEntityLinkService
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.projectcreate.linking.spi.AggregateRootLinkType
 *  com.atlassian.plugins.projectcreate.linking.spi.LocalRoot
 *  com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot
 *  com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  io.atlassian.fugue.Either
 *  io.atlassian.fugue.Option
 *  io.atlassian.fugue.Suppliers
 *  javax.ws.rs.core.Response$Status
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.plugins.projectcreate.producer.link;

import com.atlassian.applinks.api.ApplicationLink;
import com.atlassian.applinks.api.EntityLink;
import com.atlassian.applinks.api.EntityType;
import com.atlassian.applinks.host.spi.EntityReference;
import com.atlassian.applinks.host.spi.InternalHostApplication;
import com.atlassian.applinks.spi.link.EntityLinkBuilderFactory;
import com.atlassian.applinks.spi.link.MutatingEntityLinkService;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.projectcreate.linking.spi.AggregateRootLinkType;
import com.atlassian.plugins.projectcreate.linking.spi.LocalRoot;
import com.atlassian.plugins.projectcreate.linking.spi.RemoteRoot;
import com.atlassian.plugins.projectcreate.producer.link.util.ApplicationLinkUtilService;
import com.atlassian.plugins.projectcreate.producer.link.util.InternalHostApplicationAccessor;
import com.atlassian.plugins.projectcreate.spi.ResponseStatusWithMessage;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import io.atlassian.fugue.Either;
import io.atlassian.fugue.Option;
import io.atlassian.fugue.Suppliers;
import java.util.Collections;
import java.util.stream.StreamSupport;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={AggregateRootLinkType.class})
@Component
public class AppLinksRootLinkImpl
implements AggregateRootLinkType {
    private final InternalHostApplication internalHostApplication;
    private final MutatingEntityLinkService mutatingEntityLinkService;
    private final EntityLinkBuilderFactory entityLinkBuilderFactory;
    private final ApplicationLinkUtilService applicationLinkUtilService;

    @Autowired
    public AppLinksRootLinkImpl(InternalHostApplicationAccessor internalHostApplicationAccessor, @ComponentImport MutatingEntityLinkService mutatingEntityLinkService, @ComponentImport EntityLinkBuilderFactory entityLinkBuilderFactory, ApplicationLinkUtilService applicationLinkUtilService) {
        this.internalHostApplication = internalHostApplicationAccessor.get();
        this.mutatingEntityLinkService = mutatingEntityLinkService;
        this.entityLinkBuilderFactory = entityLinkBuilderFactory;
        this.applicationLinkUtilService = applicationLinkUtilService;
    }

    private EntityReference getLocalEntity(EntityType entityType, String key) {
        return this.internalHostApplication.toEntityReference(key, entityType.getClass());
    }

    public boolean canCreateLinkToType(String fromType, String toType) {
        return this.applicationLinkUtilService.getEntityTypeForTypeId(fromType).isDefined() && this.applicationLinkUtilService.getEntityTypeForTypeId(toType).isDefined();
    }

    public int getWeight() {
        return 0x1000000;
    }

    public Either<ResponseStatusWithMessage, Iterable<RemoteRoot>> getRemoteLinkedRootsForLinkedRoot(final LocalRoot localRoot) {
        return Either.right((Object)this.applicationLinkUtilService.getEntityTypeForTypeId(localRoot.getRootType()).fold(Suppliers.ofInstance(Collections.emptyList()), (java.util.function.Function)new Function<EntityType, Iterable<RemoteRoot>>(){

            public Iterable<RemoteRoot> apply(EntityType input) {
                EntityReference localEntity = AppLinksRootLinkImpl.this.getLocalEntity(input, localRoot.getRootKey());
                return (Iterable)StreamSupport.stream(AppLinksRootLinkImpl.this.mutatingEntityLinkService.getEntityLinksForKey(localEntity.getKey(), localEntity.getType().getClass()).spliterator(), false).map(AppLinksRootLinkImpl.this.applicationLinkUtilService::getRemoteRootForEntityLink).collect(ImmutableList.toImmutableList());
            }
        }));
    }

    private EntityLink getEntityLink(LocalRoot localRoot, RemoteRoot remoteRoot) {
        Option<ApplicationLink> appLink = this.applicationLinkUtilService.getApplinkForUrl(remoteRoot.getRemoteUrl().toString());
        return this.mutatingEntityLinkService.getEntityLink(localRoot.getRootKey(), ((EntityType)this.applicationLinkUtilService.getEntityTypeForTypeId(localRoot.getRootType()).get()).getClass(), remoteRoot.getRootKey(), ((EntityType)this.applicationLinkUtilService.getEntityTypeForTypeId(remoteRoot.getRootType()).get()).getClass(), ((ApplicationLink)appLink.get()).getId());
    }

    protected EntityLink buildEntityLink(ApplicationLink appLink, EntityType entityType, String key) {
        try {
            return this.entityLinkBuilderFactory.builder().applicationLink(appLink).key(key).type(entityType).name(key).primary(true).build();
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public boolean doesLinkExist(LocalRoot localRoot, RemoteRoot remoteRoot) {
        return this.getEntityLink(localRoot, remoteRoot) != null;
    }

    public Response.Status createLink(LocalRoot localRoot, RemoteRoot remoteRoot) {
        Option<ApplicationLink> appLink = this.applicationLinkUtilService.getApplinkForUrl(remoteRoot.getRemoteUrl().toString());
        if (appLink.isEmpty()) {
            return Response.Status.NOT_FOUND;
        }
        Option<EntityType> remoteEntityType = this.applicationLinkUtilService.getEntityTypeForTypeId(remoteRoot.getRootType());
        if (remoteEntityType.isEmpty()) {
            return Response.Status.NOT_FOUND;
        }
        Option<EntityType> localEntityType = this.applicationLinkUtilService.getEntityTypeForTypeId(localRoot.getRootType());
        if (localEntityType.isEmpty()) {
            return Response.Status.NOT_FOUND;
        }
        EntityLink newLink = this.buildEntityLink((ApplicationLink)appLink.get(), (EntityType)remoteEntityType.get(), remoteRoot.getRootKey());
        this.mutatingEntityLinkService.addEntityLink(localRoot.getRootKey(), ((EntityType)localEntityType.get()).getClass(), newLink);
        return Response.Status.CREATED;
    }

    public Response.Status deleteLink(LocalRoot localRoot, RemoteRoot remoteRoot) {
        EntityLink entityLink = this.getEntityLink(localRoot, remoteRoot);
        EntityType localType = (EntityType)this.applicationLinkUtilService.getEntityTypeForTypeId(localRoot.getRootType()).get();
        if (entityLink != null) {
            this.mutatingEntityLinkService.deleteEntityLink(localRoot.getRootKey(), localType.getClass(), entityLink);
            return Response.Status.NO_CONTENT;
        }
        return Response.Status.NOT_FOUND;
    }
}

