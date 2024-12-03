/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.actions;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.util.UtilsKt;
import com.addonengine.addons.analytics.web.context.LicenseContext;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000>\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0002\b\u0006\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\u0002\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\u00020\u0001B+\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\b\b\u0001\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\b\u0010\u0011\u001a\u00020\fH\u0016J\u0012\u0010\u0012\u001a\u000e\u0012\u0004\u0012\u00020\f\u0012\u0004\u0012\u00020\u00140\u0013J\b\u0010\u0015\u001a\u00020\fH\u0002J\u0006\u0010\u0016\u001a\u00020\u0017R\u000e\u0010\u000b\u001a\u00020\fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\fX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u000e\u001a\u00020\f8F\u00a2\u0006\u0006\u001a\u0004\b\u000f\u0010\u0010R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/web/actions/AnalyticsAction;", "Lcom/atlassian/confluence/spaces/actions/AbstractSpaceAction;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "licenseContext", "Lcom/addonengine/addons/analytics/web/context/LicenseContext;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "timeZoneManager", "Lcom/atlassian/sal/api/timezone/TimeZoneManager;", "(Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;Lcom/addonengine/addons/analytics/web/context/LicenseContext;Lcom/atlassian/sal/api/user/UserManager;Lcom/atlassian/sal/api/timezone/TimeZoneManager;)V", "addonKey", "", "addonPath", "title", "getTitle", "()Ljava/lang/String;", "execute", "getAppContext", "", "", "getBaseUrl", "getIsDevelopment", "", "analytics"})
public final class AnalyticsAction
extends AbstractSpaceAction {
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final LicenseContext licenseContext;
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final TimeZoneManager timeZoneManager;
    @NotNull
    private final String addonKey;
    @NotNull
    private final String addonPath;

    @Inject
    public AnalyticsAction(@NotNull UrlBuilder urlBuilder, @NotNull LicenseContext licenseContext, @ComponentImport @NotNull UserManager userManager, @ComponentImport @NotNull TimeZoneManager timeZoneManager) {
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        Intrinsics.checkNotNullParameter((Object)licenseContext, (String)"licenseContext");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)timeZoneManager, (String)"timeZoneManager");
        this.urlBuilder = urlBuilder;
        this.licenseContext = licenseContext;
        this.userManager = userManager;
        this.timeZoneManager = timeZoneManager;
        this.addonKey = "com.addonengine.analytics";
        this.addonPath = "confanalytics";
    }

    @NotNull
    public final String getTitle() {
        String string = this.getText("com.addonengine.addons.analytics.page.title");
        Intrinsics.checkNotNull((Object)string);
        return string;
    }

    @NotNull
    public final Map<String, Object> getAppContext() {
        String baseUrl = this.getBaseUrl();
        String string = this.urlBuilder.buildHostActionUrl("analytics").toString();
        Intrinsics.checkNotNullExpressionValue((Object)string, (String)"toString(...)");
        String analyticsActionUrl = string;
        String helpNavLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.nav.link.url");
        String googleAnalyticsHelpLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.google.analytics.help.link.url");
        String increasedPrivacyModeHelpLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.increased.privacy.mode.link.url");
        String permissionHelpLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.permission.link.url");
        String dataRetentionHelpLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.data.retention.link.url");
        String eventLimitHelpLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.event.limit.link.url");
        String rateLimitHelpLinkUrl = this.getDocLink("com.addonengine.addons.analytics.help.rate.limit.link.url");
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"localBaseUrl", (Object)(baseUrl + "/rest/" + this.addonPath + "/1.0")), TuplesKt.to((Object)"hostBaseUrl", (Object)baseUrl), TuplesKt.to((Object)"instanceAnalyticsUrl", (Object)(analyticsActionUrl + "#/analytics/instance/{view}")), TuplesKt.to((Object)"spaceAnalyticsUrl", (Object)(analyticsActionUrl + "?key={space.key}#/analytics/space/{space.key}")), TuplesKt.to((Object)"contentAnalyticsUrl", (Object)(analyticsActionUrl + "#/analytics/content/page/{content.id}")), TuplesKt.to((Object)"spacePermissionsUrl", (Object)(this.urlBuilder.buildHostActionUrl("spacepermissions") + "?key={space.key}#/")), TuplesKt.to((Object)"addonKey", (Object)this.addonKey), TuplesKt.to((Object)"pageKey", (Object)"analytics-page"), TuplesKt.to((Object)"userIsAdmin", (Object)this.userManager.isAdmin(this.getAuthenticatedUser().getKey())), TuplesKt.to((Object)"anonymousUserProfilePictureUrl", (Object)String.valueOf(this.urlBuilder.getAnonymousUserProfilePictureUrl())), TuplesKt.to((Object)"tzId", (Object)this.timeZoneManager.getUserTimeZone().getID()), TuplesKt.to((Object)"googleAnalyticsHelpLinkUrl", (Object)googleAnalyticsHelpLinkUrl), TuplesKt.to((Object)"helpNavLinkUrl", (Object)helpNavLinkUrl), TuplesKt.to((Object)"increasedPrivacyModeHelpLinkUrl", (Object)increasedPrivacyModeHelpLinkUrl), TuplesKt.to((Object)"permissionHelpLinkUrl", (Object)permissionHelpLinkUrl), TuplesKt.to((Object)"eventLimitHelpLinkUrl", (Object)eventLimitHelpLinkUrl), TuplesKt.to((Object)"dataRetentionHelpLinkUrl", (Object)dataRetentionHelpLinkUrl), TuplesKt.to((Object)"rateLimitHelpLinkUrl", (Object)rateLimitHelpLinkUrl)};
        return MapsKt.mapOf((Pair[])pairArray);
    }

    public final boolean getIsDevelopment() {
        return UtilsKt.isAddonDevMode();
    }

    private final String getBaseUrl() {
        return this.urlBuilder.getBaseUrl();
    }

    @NotNull
    public String execute() {
        if (this.isAnonymousUser()) {
            return "accessdenied";
        }
        if (this.licenseContext.isValid()) {
            String string = this.getCurrentRequest().getParameter("result");
            if (string == null) {
                String string2 = super.execute();
                string = string2;
                Intrinsics.checkNotNullExpressionValue((Object)string2, (String)"execute(...)");
            }
            return string;
        }
        return "license";
    }
}

