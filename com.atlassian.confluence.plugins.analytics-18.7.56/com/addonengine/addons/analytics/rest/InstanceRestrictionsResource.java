/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.core.Response
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.restrictions.InstanceUserGroupRestrictionDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.NewInstanceUserGroupRestrictionDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.RestrictionsDto;
import com.addonengine.addons.analytics.rest.filter.UserIsAdminFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.service.RestrictionsService;
import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.service.confluence.model.Group;
import com.addonengine.addons.analytics.service.model.restrictions.InstanceRestrictions;
import com.addonengine.addons.analytics.service.model.restrictions.UserGroupRestriction;
import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Path(value="/instance/restrictions")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ReadOnlyAccessAllowed
@ResourceFilters(value={ValidAddonLicenseFilter.class, UserIsAdminFilter.class})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000<\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u000b\u001a\u00020\fH\u0007J\u0016\u0010\r\u001a\b\u0012\u0004\u0012\u00020\u000f0\u000e2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0016\u0010\u0012\u001a\u00020\f2\f\u0010\u0010\u001a\b\u0012\u0004\u0012\u00020\u00130\u000eH\u0007R\u0014\u0010\u0007\u001a\u00020\bX\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/rest/InstanceRestrictionsResource;", "", "restrictionsService", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "(Lcom/addonengine/addons/analytics/service/RestrictionsService;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;)V", "groupProfilePicturePath", "", "getGroupProfilePicturePath", "()Ljava/lang/String;", "getRestrictions", "Ljavax/ws/rs/core/Response;", "mapRestrictionsToDto", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/RestrictionsDto;", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/InstanceUserGroupRestrictionDto;", "restrictions", "Lcom/addonengine/addons/analytics/service/model/restrictions/InstanceRestrictions;", "putRestrictions", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/NewInstanceUserGroupRestrictionDto;", "analytics"})
@SourceDebugExtension(value={"SMAP\nInstanceRestrictionsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 InstanceRestrictionsResource.kt\ncom/addonengine/addons/analytics/rest/InstanceRestrictionsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,59:1\n1549#2:60\n1620#2,3:61\n1549#2:64\n1620#2,3:65\n*S KotlinDebug\n*F\n+ 1 InstanceRestrictionsResource.kt\ncom/addonengine/addons/analytics/rest/InstanceRestrictionsResource\n*L\n39#1:60\n39#1:61,3\n50#1:64\n50#1:65,3\n*E\n"})
public final class InstanceRestrictionsResource {
    @NotNull
    private final RestrictionsService restrictionsService;
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final String groupProfilePicturePath;

    public InstanceRestrictionsResource(@NotNull RestrictionsService restrictionsService, @NotNull UrlBuilder urlBuilder) {
        Intrinsics.checkNotNullParameter((Object)restrictionsService, (String)"restrictionsService");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        this.restrictionsService = restrictionsService;
        this.urlBuilder = urlBuilder;
        this.groupProfilePicturePath = "/images/icons/avatar_group_48.png";
    }

    @NotNull
    public final String getGroupProfilePicturePath() {
        return this.groupProfilePicturePath;
    }

    @GET
    @NotNull
    public final Response getRestrictions() {
        InstanceRestrictions restrictions = this.restrictionsService.getInstanceRestrictions();
        Response response = Response.ok(this.mapRestrictionsToDto(restrictions)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    @PUT
    @NotNull
    public final Response putRestrictions(@NotNull RestrictionsDto<NewInstanceUserGroupRestrictionDto> restrictions) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter(restrictions, (String)"restrictions");
        Iterable $this$map$iv = restrictions.getRestrictions();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            NewInstanceUserGroupRestrictionDto newInstanceUserGroupRestrictionDto = (NewInstanceUserGroupRestrictionDto)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserGroupRestriction(new Group(it.getGroupName()), it.getUseAnalytics()));
        }
        List list = (List)destination$iv$iv;
        InstanceRestrictions userGroupRestrictions = new InstanceRestrictions(list);
        InstanceRestrictions savedRestrictions = this.restrictionsService.saveInstanceRestrictions(userGroupRestrictions);
        Response response = Response.ok(this.mapRestrictionsToDto(savedRestrictions)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    private final RestrictionsDto<InstanceUserGroupRestrictionDto> mapRestrictionsToDto(InstanceRestrictions restrictions) {
        void $this$mapTo$iv$iv;
        Iterable $this$map$iv = restrictions.getUserGroups();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            UserGroupRestriction userGroupRestriction = (UserGroupRestriction)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new InstanceUserGroupRestrictionDto(it.getGroup().getName(), this.urlBuilder.buildHostCanonicalUri(this.groupProfilePicturePath), it.getUseAnalytics()));
        }
        List list = (List)destination$iv$iv;
        return new RestrictionsDto<InstanceUserGroupRestrictionDto>(list);
    }
}

