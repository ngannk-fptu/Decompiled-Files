/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  com.atlassian.sal.api.user.UserManager
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.actions;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.web.actions.AbstractAdminToolsAction;
import com.addonengine.addons.analytics.web.context.ConnectHostContextBuilder;
import com.addonengine.addons.analytics.web.context.ConnectHostParams;
import com.addonengine.addons.analytics.web.context.LicenseContext;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import com.atlassian.sal.api.user.UserManager;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B/\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u0012\u0006\u0010\n\u001a\u00020\u000b\u00a2\u0006\u0002\u0010\f\u00a8\u0006\r"}, d2={"Lcom/addonengine/addons/analytics/web/actions/SettingsAction;", "Lcom/addonengine/addons/analytics/web/actions/AbstractAdminToolsAction;", "connectHostContextBuilder", "Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;", "licenseContext", "Lcom/addonengine/addons/analytics/web/context/LicenseContext;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "userManager", "Lcom/atlassian/sal/api/user/UserManager;", "timeZoneManager", "Lcom/atlassian/sal/api/timezone/TimeZoneManager;", "(Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;Lcom/addonengine/addons/analytics/web/context/LicenseContext;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;Lcom/atlassian/sal/api/user/UserManager;Lcom/atlassian/sal/api/timezone/TimeZoneManager;)V", "analytics"})
public final class SettingsAction
extends AbstractAdminToolsAction {
    @Inject
    public SettingsAction(@NotNull ConnectHostContextBuilder connectHostContextBuilder, @NotNull LicenseContext licenseContext, @NotNull UrlBuilder urlBuilder, @NotNull UserManager userManager, @NotNull TimeZoneManager timeZoneManager) {
        Intrinsics.checkNotNullParameter((Object)connectHostContextBuilder, (String)"connectHostContextBuilder");
        Intrinsics.checkNotNullParameter((Object)licenseContext, (String)"licenseContext");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        Intrinsics.checkNotNullParameter((Object)userManager, (String)"userManager");
        Intrinsics.checkNotNullParameter((Object)timeZoneManager, (String)"timeZoneManager");
        super(connectHostContextBuilder, licenseContext, new ConnectHostParams("settings-page", "settings", "/settings/view"), "com.addonengine.addons.analytics.admin.settings.page.title", "analytics-configuration-web-item", urlBuilder, userManager, timeZoneManager);
    }
}

