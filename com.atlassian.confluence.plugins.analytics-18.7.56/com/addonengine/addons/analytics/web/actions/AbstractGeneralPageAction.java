/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.spaces.actions.AbstractSpaceAction
 *  javax.servlet.http.HttpServletRequest
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.actions;

import com.addonengine.addons.analytics.web.context.ConnectHostContextBuilder;
import com.addonengine.addons.analytics.web.context.ConnectHostParams;
import com.addonengine.addons.analytics.web.context.LicenseContext;
import com.atlassian.confluence.spaces.actions.AbstractSpaceAction;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010$\n\u0002\u0010\u0000\n\u0002\b\u0007\b&\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\t\u00a2\u0006\u0002\u0010\nJ\b\u0010\u0013\u001a\u00020\tH\u0016R\u001d\u0010\u000b\u001a\u000e\u0012\u0004\u0012\u00020\t\u0012\u0004\u0012\u00020\r0\f8F\u00a2\u0006\u0006\u001a\u0004\b\u000e\u0010\u000fR\u000e\u0010\u0004\u001a\u00020\u0005X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0006\u001a\u00020\u0007X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000R\u0011\u0010\u0010\u001a\u00020\t8F\u00a2\u0006\u0006\u001a\u0004\b\u0011\u0010\u0012R\u000e\u0010\b\u001a\u00020\tX\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2={"Lcom/addonengine/addons/analytics/web/actions/AbstractGeneralPageAction;", "Lcom/atlassian/confluence/spaces/actions/AbstractSpaceAction;", "licenseContext", "Lcom/addonengine/addons/analytics/web/context/LicenseContext;", "connectHostContextBuilder", "Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;", "connectHostParams", "Lcom/addonengine/addons/analytics/web/context/ConnectHostParams;", "titleKey", "", "(Lcom/addonengine/addons/analytics/web/context/LicenseContext;Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;Lcom/addonengine/addons/analytics/web/context/ConnectHostParams;Ljava/lang/String;)V", "connectHostContext", "", "", "getConnectHostContext", "()Ljava/util/Map;", "title", "getTitle", "()Ljava/lang/String;", "execute", "analytics"})
public abstract class AbstractGeneralPageAction
extends AbstractSpaceAction {
    @NotNull
    private final LicenseContext licenseContext;
    @NotNull
    private final ConnectHostContextBuilder connectHostContextBuilder;
    @NotNull
    private final ConnectHostParams connectHostParams;
    @NotNull
    private final String titleKey;

    public AbstractGeneralPageAction(@NotNull LicenseContext licenseContext, @NotNull ConnectHostContextBuilder connectHostContextBuilder, @NotNull ConnectHostParams connectHostParams, @NotNull String titleKey) {
        Intrinsics.checkNotNullParameter((Object)licenseContext, (String)"licenseContext");
        Intrinsics.checkNotNullParameter((Object)connectHostContextBuilder, (String)"connectHostContextBuilder");
        Intrinsics.checkNotNullParameter((Object)connectHostParams, (String)"connectHostParams");
        Intrinsics.checkNotNullParameter((Object)titleKey, (String)"titleKey");
        this.licenseContext = licenseContext;
        this.connectHostContextBuilder = connectHostContextBuilder;
        this.connectHostParams = connectHostParams;
        this.titleKey = titleKey;
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

