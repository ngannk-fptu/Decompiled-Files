/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserManager
 *  javax.servlet.http.HttpServletRequest
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.actions;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.web.context.ConnectHostContextBuilder;
import com.addonengine.addons.analytics.web.context.ConnectHostParams;
import com.addonengine.addons.analytics.web.context.LicenseContext;
import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserManager;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\u000b\b&\u0018\u00002\u00020\u0001BE\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u0012\u0006\u0010\u000f\u001a\u00020\u0010\u00a2\u0006\u0002\u0010\u0011J\b\u0010\u001e\u001a\u00020\tH\u0016J\u0012\u0010\u001f\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00160\u0015J\b\u0010 \u001a\u00020\tH\u0002R\u000e\u0010\u0012\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\tX\u0082D\u00a2\u0006\u0002\n\u0000R\u001d\u0010\u0014\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\u00160\u00158F\u00a2\u0006\u0006\u001a\u0004\b\u0017\u0010\u0018R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0019\u001a\u00020\t8F\u00a2\u0006\u0006\u001a\u0004\b\u001a\u0010\u001bR\u000e\u0010\u000f\u001a\u00020\u0010X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u001c\u001a\u00020\t8F\u00a2\u0006\u0006\u001a\u0004\b\u001d\u0010\u001bR\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u000eX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\n\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006!"}, d2={"Lcom/addonengine/addons/analytics/web/actions/AbstractAdminToolsAction;", "Lcom/atlassian/confluence/core/ConfluenceActionSupport;", "connectHostContextBuilder", "Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;", "licenseContext", "Lcom/addonengine/addons/analytics/web/context/LicenseContext;", "connectHostParams", "Lcom/addonengine/addons/analytics/web/context/ConnectHostParams;", "titleKey", "", "webItemKey", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "timeZoneManager", "Lcom/atlassian/sal/api/timezone/TimeZoneManager;", "(Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;Lcom/addonengine/addons/analytics/web/context/LicenseContext;Lcom/addonengine/addons/analytics/web/context/ConnectHostParams;Ljava/lang/String;Ljava/lang/String;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;Lcom/atlassian/sal/api/user/UserManager;Lcom/atlassian/sal/api/timezone/TimeZoneManager;)V", "addonKey", "addonPath", "connectHostContext", "", "", "getConnectHostContext", "()Ljava/util/Map;", "selectedWebItem", "getSelectedWebItem", "()Ljava/lang/String;", "title", "getTitle", "execute", "getAppContext", "getBaseUrl", "analytics"})
public abstract class AbstractAdminToolsAction
extends ConfluenceActionSupport {
    @NotNull
    private final ConnectHostContextBuilder connectHostContextBuilder;
    @NotNull
    private final LicenseContext licenseContext;
    @NotNull
    private final ConnectHostParams connectHostParams;
    @NotNull
    private final String titleKey;
    @NotNull
    private final String webItemKey;
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final UserManager userManager;
    @NotNull
    private final TimeZoneManager timeZoneManager;
    @NotNull
    private final String addonKey;
    @NotNull
    private final String addonPath;

    public AbstractAdminToolsAction(@NotNull ConnectHostContextBuilder connectHostContextBuilder, @NotNull LicenseContext licenseContext, @NotNull ConnectHostParams connectHostParams, @NotNull String titleKey, @NotNull String webItemKey, @NotNull UrlBuilder urlBuilder, @NotNull UserManager userManager, @NotNull TimeZoneManager timeZoneManager) {
        Intrinsics.checkNotNullParameter((Object)connectHostContextBuilder, (String)"connectHostContextBuilder");
        Intrinsics.checkNotNullParameter((Object)licenseContext, (String)"licenseContext");
        Intrinsics.checkNotNullParameter((Object)connectHostParams, (String)"connectHostParams");
        Intrinsics.checkNotNullParameter((Object)titleKey, (String)"titleKey");
        Intrinsics.checkNotNullParameter((Object)webItemKey, (String)"webItemKey");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)timeZoneManager, (String)"timeZoneManager");
        this.connectHostContextBuilder = connectHostContextBuilder;
        this.licenseContext = licenseContext;
        this.connectHostParams = connectHostParams;
        this.titleKey = titleKey;
        this.webItemKey = webItemKey;
        this.urlBuilder = urlBuilder;
        this.userManager = userManager;
        this.timeZoneManager = timeZoneManager;
        this.addonKey = "com.addonengine.analytics";
        this.addonPath = "confanalytics";
    }

    @NotNull
    public final String getTitle() {
        String string = this.getText(this.titleKey);
        Intrinsics.checkNotNull((Object)string);
        return string;
    }

    @NotNull
    public final Map<String, Object> getConnectHostContext() {
        HttpServletRequest httpServletRequest = this.getCurrentRequest();
        Intrinsics.checkNotNullExpressionValue((Object)httpServletRequest, (String)"getCurrentRequest(...)");
        return this.connectHostContextBuilder.buildContext(httpServletRequest, this.connectHostParams);
    }

    @NotNull
    public final String getSelectedWebItem() {
        return this.webItemKey;
    }

    private final String getBaseUrl() {
        return this.urlBuilder.getBaseUrl();
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
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"localBaseUrl", (Object)(baseUrl + "/rest/" + this.addonPath + "/1.0")), TuplesKt.to((Object)"hostBaseUrl", (Object)baseUrl), TuplesKt.to((Object)"instanceAnalyticsUrl", (Object)(analyticsActionUrl + "#/analytics/instance/{view}")), TuplesKt.to((Object)"spaceAnalyticsUrl", (Object)(analyticsActionUrl + "#/analytics/space/{space.key}/{view}")), TuplesKt.to((Object)"contentAnalyticsUrl", (Object)(analyticsActionUrl + "#/analytics/content/page/{content.id}")), TuplesKt.to((Object)"spacePermissionsUrl", (Object)(this.urlBuilder.buildHostActionUrl("spacepermissions") + "?key={space.key}#/")), TuplesKt.to((Object)"addonKey", (Object)this.addonKey), TuplesKt.to((Object)"pageKey", (Object)"analytics-page"), TuplesKt.to((Object)"userIsAdmin", (Object)this.userManager.isAdmin(this.getAuthenticatedUser().getKey())), TuplesKt.to((Object)"anonymousUserProfilePictureUrl", (Object)this.urlBuilder.getAnonymousUserProfilePictureUrl()), TuplesKt.to((Object)"tzId", (Object)this.timeZoneManager.getUserTimeZone().getID()), TuplesKt.to((Object)"googleAnalyticsHelpLinkUrl", (Object)googleAnalyticsHelpLinkUrl), TuplesKt.to((Object)"helpNavLinkUrl", (Object)helpNavLinkUrl), TuplesKt.to((Object)"increasedPrivacyModeHelpLinkUrl", (Object)increasedPrivacyModeHelpLinkUrl), TuplesKt.to((Object)"permissionHelpLinkUrl", (Object)permissionHelpLinkUrl), TuplesKt.to((Object)"eventLimitHelpLinkUrl", (Object)eventLimitHelpLinkUrl), TuplesKt.to((Object)"dataRetentionHelpLinkUrl", (Object)dataRetentionHelpLinkUrl), TuplesKt.to((Object)"rateLimitHelpLinkUrl", (Object)rateLimitHelpLinkUrl)};
        return MapsKt.mapOf((Pair[])pairArray);
    }

    @NotNull
    public String execute() {
        if (this.isAnonymousUser()) {
            return "accessdenied";
        }
        if (this.licenseContext.isValid()) {
            String string = super.execute();
            Intrinsics.checkNotNullExpressionValue((Object)string, (String)"execute(...)");
            return string;
        }
        return "license";
    }
}

