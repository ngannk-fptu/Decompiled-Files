/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.sun.jersey.spi.container.ResourceFilters
 *  javax.ws.rs.Consumes
 *  javax.ws.rs.DELETE
 *  javax.ws.rs.GET
 *  javax.ws.rs.PUT
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  javax.ws.rs.QueryParam
 *  javax.ws.rs.core.Response
 *  javax.ws.rs.core.Response$Status
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest;

import com.addonengine.addons.analytics.rest.dto.settings.DataRetentionSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.EventLimitSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.NewDataRetentionSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.NewEventLimitSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.NewPrivacySettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.NewRateLimitSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.PrivacySettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.RateLimitSettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.SettingsDto;
import com.addonengine.addons.analytics.rest.dto.settings.TimeZoneDto;
import com.addonengine.addons.analytics.rest.filter.UserIsAdminFilter;
import com.addonengine.addons.analytics.service.SettingsService;
import com.addonengine.addons.analytics.service.model.settings.DataRetentionSettings;
import com.addonengine.addons.analytics.service.model.settings.EventLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.NewDataRetentionSettings;
import com.addonengine.addons.analytics.service.model.settings.NewEventLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.NewPrivacySettings;
import com.addonengine.addons.analytics.service.model.settings.NewRateLimitSettings;
import com.addonengine.addons.analytics.service.model.settings.PrivacySettings;
import com.addonengine.addons.analytics.service.model.settings.RateLimitSettings;
import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.sun.jersey.spi.container.ResourceFilters;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Path(value="/settings")
@Consumes(value={"application/json"})
@Produces(value={"application/json"})
@ReadOnlyAccessAllowed
@ResourceFilters(value={UserIsAdminFilter.class})
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000l\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\u0018\u0002\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u0012\u0010\u0005\u001a\u00020\u00062\b\b\u0001\u0010\u0007\u001a\u00020\bH\u0007J\b\u0010\t\u001a\u00020\u0006H\u0007J\b\u0010\n\u001a\u00020\u0006H\u0007J\b\u0010\u000b\u001a\u00020\u0006H\u0007J\b\u0010\f\u001a\u00020\u0006H\u0007J\b\u0010\r\u001a\u00020\u0006H\u0007J\u0010\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\u0011H\u0002J\u0010\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0010\u001a\u00020\u0014H\u0002J\u0010\u0010\u0015\u001a\u00020\u00162\u0006\u0010\u0010\u001a\u00020\u0017H\u0002J\u0010\u0010\u0018\u001a\u00020\u00192\u0006\u0010\u0010\u001a\u00020\u001aH\u0002J!\u0010\u001b\u001a\u00020\u00062\n\b\u0003\u0010\u001c\u001a\u0004\u0018\u00010\u001d2\u0006\u0010\u0010\u001a\u00020\u001eH\u0007\u00a2\u0006\u0002\u0010\u001fJ\b\u0010 \u001a\u00020\u0006H\u0007J\u0010\u0010!\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020\"H\u0007J\u0010\u0010#\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020$H\u0007J\u0010\u0010%\u001a\u00020\u00062\u0006\u0010\u0010\u001a\u00020&H\u0007J\b\u0010'\u001a\u00020\u0006H\u0007R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006("}, d2={"Lcom/addonengine/addons/analytics/rest/SettingsResource;", "", "settingsService", "Lcom/addonengine/addons/analytics/service/SettingsService;", "(Lcom/addonengine/addons/analytics/service/SettingsService;)V", "deletePrivacySetting", "Ljavax/ws/rs/core/Response;", "areYouSure", "", "getDataRetentionSetting", "getEventLimitSetting", "getPrivacySetting", "getRateLimitSetting", "getSettings", "mapDataLimitSettings", "Lcom/addonengine/addons/analytics/rest/dto/settings/EventLimitSettingsDto;", "settings", "Lcom/addonengine/addons/analytics/service/model/settings/EventLimitSettings;", "mapDataRetentionSettings", "Lcom/addonengine/addons/analytics/rest/dto/settings/DataRetentionSettingsDto;", "Lcom/addonengine/addons/analytics/service/model/settings/DataRetentionSettings;", "mapPrivacySettings", "Lcom/addonengine/addons/analytics/rest/dto/settings/PrivacySettingsDto;", "Lcom/addonengine/addons/analytics/service/model/settings/PrivacySettings;", "mapRateLimitSettings", "Lcom/addonengine/addons/analytics/rest/dto/settings/RateLimitSettingsDto;", "Lcom/addonengine/addons/analytics/service/model/settings/RateLimitSettings;", "putDataRetentionSetting", "gracePeriod", "", "Lcom/addonengine/addons/analytics/rest/dto/settings/NewDataRetentionSettingsDto;", "(Ljava/lang/Integer;Lcom/addonengine/addons/analytics/rest/dto/settings/NewDataRetentionSettingsDto;)Ljavax/ws/rs/core/Response;", "putDataRetentionSettingConfirmation", "putEventLimitSetting", "Lcom/addonengine/addons/analytics/rest/dto/settings/NewEventLimitSettingsDto;", "putPrivacySetting", "Lcom/addonengine/addons/analytics/rest/dto/settings/NewPrivacySettingsDto;", "putRateLimitSetting", "Lcom/addonengine/addons/analytics/rest/dto/settings/NewRateLimitSettingsDto;", "serverTimezone", "analytics"})
public final class SettingsResource {
    @NotNull
    private final SettingsService settingsService;

    public SettingsResource(@NotNull SettingsService settingsService) {
        Intrinsics.checkNotNullParameter((Object)settingsService, (String)"settingsService");
        this.settingsService = settingsService;
    }

    @GET
    @NotNull
    public final Response getSettings() {
        PrivacySettings privacySettings = this.settingsService.getPrivacySettings();
        DataRetentionSettings dataRetentionSettings = this.settingsService.getDataRetentionSettings();
        EventLimitSettings eventLimitSettings = this.settingsService.getEventLimitSettings();
        RateLimitSettings rateLimitSettings = this.settingsService.getRateLimitSettings();
        Response response = Response.ok((Object)new SettingsDto(this.mapPrivacySettings(privacySettings), this.mapDataRetentionSettings(dataRetentionSettings), this.mapDataLimitSettings(eventLimitSettings), this.mapRateLimitSettings(rateLimitSettings))).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="/privacy")
    @NotNull
    public final Response getPrivacySetting() {
        PrivacySettings settings = this.settingsService.getPrivacySettings();
        Response response = Response.ok((Object)this.mapPrivacySettings(settings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="/dataRetention")
    @NotNull
    public final Response getDataRetentionSetting() {
        DataRetentionSettings settings = this.settingsService.getDataRetentionSettings();
        Response response = Response.ok((Object)this.mapDataRetentionSettings(settings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="/eventLimit")
    @NotNull
    public final Response getEventLimitSetting() {
        EventLimitSettings settings = this.settingsService.getEventLimitSettings();
        Response response = Response.ok((Object)this.mapDataLimitSettings(settings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @GET
    @Path(value="/rateLimit")
    @NotNull
    public final Response getRateLimitSetting() {
        RateLimitSettings settings = this.settingsService.getRateLimitSettings();
        Response response = Response.ok((Object)this.mapRateLimitSettings(settings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @PUT
    @Path(value="/privacy")
    @NotNull
    public final Response putPrivacySetting(@NotNull NewPrivacySettingsDto settings) {
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        PrivacySettings newSettings = this.settingsService.setPrivacySettings(new NewPrivacySettings(settings.getEnabled()));
        Response response = Response.ok((Object)this.mapPrivacySettings(newSettings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @PUT
    @Path(value="/dataRetention")
    @NotNull
    public final Response putDataRetentionSetting(@QueryParam(value="gracePeriod") @Nullable Integer gracePeriod, @NotNull NewDataRetentionSettingsDto settings) {
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        int n = settings.getMonths();
        DataRetentionSettings newSettings = this.settingsService.setDataRetentionSettings(new NewDataRetentionSettings(n, true), gracePeriod);
        Response response = Response.ok((Object)this.mapDataRetentionSettings(newSettings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    public static /* synthetic */ Response putDataRetentionSetting$default(SettingsResource settingsResource, Integer n, NewDataRetentionSettingsDto newDataRetentionSettingsDto, int n2, Object object) {
        if ((n2 & 1) != 0) {
            n = null;
        }
        return settingsResource.putDataRetentionSetting(n, newDataRetentionSettingsDto);
    }

    @PUT
    @Path(value="/eventLimit")
    @NotNull
    public final Response putEventLimitSetting(@NotNull NewEventLimitSettingsDto settings) {
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        EventLimitSettings newSettings = this.settingsService.setEventLimitSettings(new NewEventLimitSettings(settings.getMaxRowCount()));
        Response response = Response.ok((Object)this.mapDataLimitSettings(newSettings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @PUT
    @Path(value="/rateLimit")
    @NotNull
    public final Response putRateLimitSetting(@NotNull NewRateLimitSettingsDto settings) {
        Intrinsics.checkNotNullParameter((Object)settings, (String)"settings");
        RateLimitSettings newSettings = this.settingsService.setRateLimitSettings(new NewRateLimitSettings(settings.getEnabled(), settings.getConcurrentSessions(), settings.getStaleOperationSeconds(), settings.getConcurrentOperationsPerSession()));
        Response response = Response.ok((Object)this.mapRateLimitSettings(newSettings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @PUT
    @ResourceFilters(value={})
    @Path(value="/dataRetention/confirm")
    @NotNull
    public final Response putDataRetentionSettingConfirmation() {
        DataRetentionSettings settings = this.settingsService.getDataRetentionSettings();
        int n = settings.getMonths();
        DataRetentionSettings newSettings = SettingsService.DefaultImpls.setDataRetentionSettings$default(this.settingsService, new NewDataRetentionSettings(n, true), null, 2, null);
        Response response = Response.ok((Object)this.mapDataRetentionSettings(newSettings)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    @DELETE
    @Path(value="/privacy")
    @NotNull
    public final Response deletePrivacySetting(@QueryParam(value="areYouSure") @NotNull String areYouSure) {
        Response response;
        Intrinsics.checkNotNullParameter((Object)areYouSure, (String)"areYouSure");
        if (Intrinsics.areEqual((Object)areYouSure, (Object)"yes")) {
            this.settingsService.clearPrivacySettings();
            Response response2 = Response.ok().build();
            Intrinsics.checkNotNull((Object)response2);
            response = response2;
        } else {
            Response response3 = Response.status((Response.Status)Response.Status.BAD_REQUEST).build();
            Intrinsics.checkNotNull((Object)response3);
            response = response3;
        }
        return response;
    }

    @GET
    @Path(value="/timezone")
    @NotNull
    public final Response serverTimezone() {
        String string = this.settingsService.serverTimezone().getId();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"getId(...)");
        Response response = Response.ok((Object)new TimeZoneDto(string)).build();
        Intrinsics.checkNotNullExpressionValue((Object)response, (String)"build(...)");
        return response;
    }

    private final PrivacySettingsDto mapPrivacySettings(PrivacySettings settings) {
        return new PrivacySettingsDto(settings.getEnabled());
    }

    private final DataRetentionSettingsDto mapDataRetentionSettings(DataRetentionSettings settings) {
        return new DataRetentionSettingsDto(settings.getMonths());
    }

    private final EventLimitSettingsDto mapDataLimitSettings(EventLimitSettings settings) {
        return new EventLimitSettingsDto(settings.getMaxRowCount() / (long)1000000);
    }

    private final RateLimitSettingsDto mapRateLimitSettings(RateLimitSettings settings) {
        return new RateLimitSettingsDto(settings.getEnabled(), settings.getConcurrentSessions(), settings.getStaleOperationSeconds(), settings.getConcurrentOperationsPerSession());
    }
}

