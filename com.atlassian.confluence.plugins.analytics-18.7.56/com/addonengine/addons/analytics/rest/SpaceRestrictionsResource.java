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
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  kotlin.Metadata
 *  kotlin.collections.CollectionsKt
 *  kotlin.jvm.internal.Intrinsics
 *  kotlin.jvm.internal.SourceDebugExtension
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.restrictions.NewSpaceRestrictionDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.RestrictionsDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.SpaceRestrictionDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.SpaceUserGroupRestrictionDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.SpaceUserRestrictionDto;
import com.addonengine.addons.analytics.rest.filter.UserIsSpaceAdminFilter;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.service.RestrictionsService;
import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.service.model.restrictions.SpaceRestrictions;
import com.addonengine.addons.analytics.service.model.restrictions.UserGroupRestriction;
import com.addonengine.addons.analytics.service.model.restrictions.UserRestriction;
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
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Path(value="/space/restrictions")
@ReadOnlyAccessAllowed
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={ValidAddonLicenseFilter.class, UserIsSpaceAdminFilter.class})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0012\u0010\u000b\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\bH\u0007J\u0016\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00100\u000f2\u0006\u0010\u0011\u001a\u00020\u0012H\u0002J \u0010\u0013\u001a\u00020\f2\b\b\u0001\u0010\r\u001a\u00020\b2\f\u0010\u0011\u001a\b\u0012\u0004\u0012\u00020\u00140\u000fH\u0007R\u0014\u0010\u0007\u001a\u00020\bX\u0086D\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/rest/SpaceRestrictionsResource;", "", "restrictionsService", "Lcom/addonengine/addons/analytics/service/RestrictionsService;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "(Lcom/addonengine/addons/analytics/service/RestrictionsService;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;)V", "groupProfilePicturePath", "", "getGroupProfilePicturePath", "()Ljava/lang/String;", "getRestrictions", "Ljavax/ws/rs/core/Response;", "spaceKey", "mapRestrictionsToDto", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/RestrictionsDto;", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/SpaceRestrictionDto;", "restrictions", "Lcom/addonengine/addons/analytics/service/model/restrictions/SpaceRestrictions;", "putRestrictions", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/NewSpaceRestrictionDto;", "analytics"})
@SourceDebugExtension(value={"SMAP\nSpaceRestrictionsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 SpaceRestrictionsResource.kt\ncom/addonengine/addons/analytics/rest/SpaceRestrictionsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,63:1\n766#2:64\n857#2,2:65\n1549#2:67\n1620#2,3:68\n766#2:71\n857#2,2:72\n1549#2:74\n1620#2,3:75\n1549#2:78\n1620#2,3:79\n1549#2:82\n1620#2,3:83\n*S KotlinDebug\n*F\n+ 1 SpaceRestrictionsResource.kt\ncom/addonengine/addons/analytics/rest/SpaceRestrictionsResource\n*L\n39#1:64\n39#1:65,2\n39#1:67\n39#1:68,3\n40#1:71\n40#1:72,2\n40#1:74\n40#1:75,3\n46#1:78\n46#1:79,3\n53#1:82\n53#1:83,3\n*E\n"})
public final class SpaceRestrictionsResource {
    @NotNull
    private final RestrictionsService restrictionsService;
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final String groupProfilePicturePath;

    public SpaceRestrictionsResource(@NotNull RestrictionsService restrictionsService, @NotNull UrlBuilder urlBuilder) {
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
    public final Response getRestrictions(@QueryParam(value="spaceKey") @NotNull String spaceKey) {
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        SpaceRestrictions restrictions = this.restrictionsService.getSpaceRestrictions(spaceKey);
        Response response = Response.ok(this.mapRestrictionsToDto(restrictions)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    @PUT
    @NotNull
    public final Response putRestrictions(@QueryParam(value="spaceKey") @NotNull String spaceKey, @NotNull RestrictionsDto<NewSpaceRestrictionDto> restrictions) {
        void $this$mapTo$iv$iv;
        void $this$map$iv;
        void $this$filterTo$iv$iv;
        Collection collection;
        void $this$mapTo$iv$iv2;
        void $this$map$iv2;
        NewSpaceRestrictionDto it;
        void $this$filterTo$iv$iv2;
        Intrinsics.checkNotNullParameter((Object)spaceKey, (String)"spaceKey");
        Intrinsics.checkNotNullParameter(restrictions, (String)"restrictions");
        Iterable $this$filter$iv = restrictions.getRestrictions();
        boolean $i$f$filter = false;
        Iterable iterable = $this$filter$iv;
        Iterable destination$iv$iv = new ArrayList();
        boolean $i$f$filterTo = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv2) {
            it = (NewSpaceRestrictionDto)element$iv$iv;
            boolean bl = false;
            if (!(it.getGroupName() != null)) continue;
            destination$iv$iv.add(element$iv$iv);
        }
        $this$filter$iv = (List)destination$iv$iv;
        boolean $i$f$map = false;
        $this$filterTo$iv$iv2 = $this$map$iv2;
        destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv2) {
            it = (NewSpaceRestrictionDto)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            String string = it.getGroupName();
            Intrinsics.checkNotNull((Object)string);
            collection.add(string);
        }
        List userGroupRestrictions = (List)destination$iv$iv;
        Iterable $this$filter$iv2 = restrictions.getRestrictions();
        boolean $i$f$filter2 = false;
        destination$iv$iv = $this$filter$iv2;
        Collection destination$iv$iv2 = new ArrayList();
        boolean $i$f$filterTo2 = false;
        for (Object element$iv$iv : $this$filterTo$iv$iv) {
            NewSpaceRestrictionDto it2 = (NewSpaceRestrictionDto)element$iv$iv;
            boolean bl = false;
            if (!(it2.getUserId() != null)) continue;
            destination$iv$iv2.add(element$iv$iv);
        }
        $this$filter$iv2 = (List)destination$iv$iv2;
        boolean $i$f$map2 = false;
        $this$filterTo$iv$iv = $this$map$iv;
        destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo2 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            NewSpaceRestrictionDto it2 = (NewSpaceRestrictionDto)item$iv$iv;
            collection = destination$iv$iv2;
            boolean bl = false;
            String string = it2.getUserId();
            Intrinsics.checkNotNull((Object)string);
            collection.add(string);
        }
        List userRestrictions = (List)destination$iv$iv2;
        SpaceRestrictions savedRestrictions = this.restrictionsService.saveSpaceRestrictions(spaceKey, userRestrictions, userGroupRestrictions);
        Response response = Response.ok(this.mapRestrictionsToDto(savedRestrictions)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    /*
     * WARNING - void declaration
     */
    private final RestrictionsDto<SpaceRestrictionDto> mapRestrictionsToDto(SpaceRestrictions restrictions) {
        void $this$mapTo$iv$iv;
        Collection collection;
        void $this$mapTo$iv$iv2;
        Iterable $this$map$iv = restrictions.getUserGroups();
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Iterable destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv2) {
            void it;
            UserGroupRestriction userGroupRestriction = (UserGroupRestriction)item$iv$iv;
            collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new SpaceUserGroupRestrictionDto(it.getGroup().getName(), this.urlBuilder.buildHostCanonicalUri(this.groupProfilePicturePath)));
        }
        List userGroupRestrictions = (List)destination$iv$iv;
        Iterable $this$map$iv2 = restrictions.getUsers();
        boolean $i$f$map2 = false;
        destination$iv$iv = $this$map$iv2;
        Collection destination$iv$iv2 = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv2, (int)10));
        boolean $i$f$mapTo2 = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            UserRestriction bl = (UserRestriction)item$iv$iv;
            collection = destination$iv$iv2;
            boolean bl2 = false;
            String string = it.getUser().getUserKey();
            Intrinsics.checkNotNull((Object)string);
            collection.add(new SpaceUserRestrictionDto(string, it.getUser().getDisplayName(), it.getUser().getProfilePictureUrl()));
        }
        List userRestrictions = (List)destination$iv$iv2;
        return new RestrictionsDto<SpaceRestrictionDto>(CollectionsKt.plus((Collection)userGroupRestrictions, (Iterable)userRestrictions));
    }
}

