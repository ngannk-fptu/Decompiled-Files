/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.sal.api.timezone.TimeZoneManager
 *  javax.inject.Inject
 *  javax.inject.Named
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.actions;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.web.actions.AbstractSpaceToolsAction;
import com.addonengine.addons.analytics.web.context.ConnectHostContextBuilder;
import com.addonengine.addons.analytics.web.context.ConnectHostParams;
import com.addonengine.addons.analytics.web.context.LicenseContext;
import com.atlassian.confluence.compat.api.service.accessmode.ReadOnlyAccessAllowed;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.sal.api.timezone.TimeZoneManager;
import javax.inject.Inject;
import javax.inject.Named;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@ReadOnlyAccessAllowed
@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000$\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\b\u0007\u0018\u00002\u00020\u0001B)\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\b\u0001\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nR\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/web/actions/SpacePermissionsAction;", "Lcom/addonengine/addons/analytics/web/actions/AbstractSpaceToolsAction;", "connectHostContextBuilder", "Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;", "licenseContext", "Lcom/addonengine/addons/analytics/web/context/LicenseContext;", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "timeZoneManager", "Lcom/atlassian/sal/api/timezone/TimeZoneManager;", "(Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;Lcom/addonengine/addons/analytics/web/context/LicenseContext;Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;Lcom/atlassian/sal/api/timezone/TimeZoneManager;)V", "analytics"})
public final class SpacePermissionsAction
extends AbstractSpaceToolsAction {
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final TimeZoneManager timeZoneManager;

    @Inject
    public SpacePermissionsAction(@NotNull ConnectHostContextBuilder connectHostContextBuilder, @NotNull LicenseContext licenseContext, @NotNull UrlBuilder urlBuilder, @ComponentImport @NotNull TimeZoneManager timeZoneManager) {
        Intrinsics.checkNotNullParameter((Object)connectHostContextBuilder, (String)"connectHostContextBuilder");
        Intrinsics.checkNotNullParameter((Object)licenseContext, (String)"licenseContext");
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        Intrinsics.checkNotNullParameter((Object)timeZoneManager, (String)"timeZoneManager");
        ConnectHostParams connectHostParams = new ConnectHostParams("space-permissions-page", "permissions", "/permissions/space");
        super(connectHostContextBuilder, licenseContext, connectHostParams, "com.addonengine.addons.analytics.spacetools.permissions.page.title", "space-permissions-web-item", urlBuilder, timeZoneManager);
        this.urlBuilder = urlBuilder;
        this.timeZoneManager = timeZoneManager;
    }
}

