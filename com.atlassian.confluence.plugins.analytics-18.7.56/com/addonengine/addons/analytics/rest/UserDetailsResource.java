/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.UserAccessor
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.POST
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

import com.addonengine.addons.analytics.rest.dto.UserDto;
import com.addonengine.addons.analytics.rest.dto.restrictions.UserDetailsRequestDto;
import com.addonengine.addons.analytics.rest.dto.userDetailsDto;
import com.addonengine.addons.analytics.rest.filter.ValidAddonLicenseFilter;
import com.addonengine.addons.analytics.service.confluence.UserService;
import com.addonengine.addons.analytics.service.confluence.model.User;
import com.atlassian.confluence.user.UserAccessor;
import com.sun.jersey.spi.container.ResourceFilters;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.jvm.internal.SourceDebugExtension;
import org.jetbrains.annotations.NotNull;

@Path(value="/rest/getUserDetails")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ResourceFilters(value={ValidAddonLicenseFilter.class})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u0015\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\u0002\u0010\u0006J\u0010\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\nH\u0007R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/rest/UserDetailsResource;", "", "userService", "Lcom/addonengine/addons/analytics/service/confluence/UserService;", "userAccessor", "Lcom/atlassian/confluence/user/UserAccessor;", "(Lcom/addonengine/addons/analytics/service/confluence/UserService;Lcom/atlassian/confluence/user/UserAccessor;)V", "getUserDetails", "Ljavax/ws/rs/core/Response;", "userDetailsDto", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/UserDetailsRequestDto;", "analytics"})
@SourceDebugExtension(value={"SMAP\nUserDetailsResource.kt\nKotlin\n*S Kotlin\n*F\n+ 1 UserDetailsResource.kt\ncom/addonengine/addons/analytics/rest/UserDetailsResource\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,41:1\n1549#2:42\n1620#2,3:43\n*S KotlinDebug\n*F\n+ 1 UserDetailsResource.kt\ncom/addonengine/addons/analytics/rest/UserDetailsResource\n*L\n27#1:42\n27#1:43,3\n*E\n"})
public final class UserDetailsResource {
    @NotNull
    private final UserService userService;
    @NotNull
    private final UserAccessor userAccessor;

    public UserDetailsResource(@NotNull UserService userService, @NotNull UserAccessor userAccessor) {
        Intrinsics.checkNotNullParameter((Object)userService, (String)"userService");
        Intrinsics.checkNotNullParameter((Object)userAccessor, (String)"userAccessor");
        this.userService = userService;
        this.userAccessor = userAccessor;
    }

    /*
     * WARNING - void declaration
     */
    @POST
    @NotNull
    public final Response getUserDetails(@NotNull UserDetailsRequestDto userDetailsDto2) {
        void $this$mapTo$iv$iv;
        Intrinsics.checkNotNullParameter((Object)userDetailsDto2, (String)"userDetailsDto");
        List<User> users = this.userService.getUserDetails(userDetailsDto2.getAccountIds(), userDetailsDto2.getIgnoreIncreasedPrivacyMode());
        Iterable $this$map$iv = users;
        boolean $i$f$map = false;
        Iterable iterable = $this$map$iv;
        Collection destination$iv$iv = new ArrayList(CollectionsKt.collectionSizeOrDefault((Iterable)$this$map$iv, (int)10));
        boolean $i$f$mapTo = false;
        for (Object item$iv$iv : $this$mapTo$iv$iv) {
            void it;
            User user = (User)item$iv$iv;
            Collection collection = destination$iv$iv;
            boolean bl = false;
            collection.add(new UserDto(it.getType(), it.getUserKey(), it.getDisplayName(), it.getEmail(), String.valueOf(it.getProfilePictureUrl())));
        }
        List userDetails = (List)destination$iv$iv;
        Response response = Response.ok((Object)new userDetailsDto(userDetails)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }
}

