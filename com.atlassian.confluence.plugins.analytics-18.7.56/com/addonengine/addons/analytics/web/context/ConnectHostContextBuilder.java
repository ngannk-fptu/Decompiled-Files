/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  javax.inject.Inject
 *  javax.inject.Named
 *  javax.servlet.http.HttpServletRequest
 *  kotlin.Metadata
 *  kotlin.Pair
 *  kotlin.TuplesKt
 *  kotlin.collections.MapsKt
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.web.context;

import com.addonengine.addons.analytics.service.confluence.UrlBuilder;
import com.addonengine.addons.analytics.web.context.ConnectHostParams;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import kotlin.Metadata;
import kotlin.Pair;
import kotlin.TuplesKt;
import kotlin.collections.MapsKt;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;

@Named
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u000e\n\u0000\n\u0002\u0010$\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\b\u0007\u0018\u00002\u00020\u0001B\u000f\b\u0007\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\"\u0010\u0007\u001a\u000e\u0012\u0004\u0012\u00020\u0006\u0012\u0004\u0012\u00020\u00010\b2\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\fR\u000e\u0010\u0005\u001a\u00020\u0006X\u0082D\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u0002\u001a\u00020\u0003X\u0082\u0004\u00a2\u0006\u0002\n\u0000\u00a8\u0006\r"}, d2={"Lcom/addonengine/addons/analytics/web/context/ConnectHostContextBuilder;", "", "urlBuilder", "Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "(Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;)V", "addonKey", "", "buildContext", "", "request", "Ljavax/servlet/http/HttpServletRequest;", "connectHostParams", "Lcom/addonengine/addons/analytics/web/context/ConnectHostParams;", "analytics"})
public final class ConnectHostContextBuilder {
    @NotNull
    private final UrlBuilder urlBuilder;
    @NotNull
    private final String addonKey;

    @Inject
    public ConnectHostContextBuilder(@NotNull UrlBuilder urlBuilder) {
        Intrinsics.checkNotNullParameter((Object)urlBuilder, (String)"urlBuilder");
        this.urlBuilder = urlBuilder;
        this.addonKey = "com.addonengine.analytics";
    }

    @NotNull
    public final Map<String, Object> buildContext(@NotNull HttpServletRequest request, @NotNull ConnectHostParams connectHostParams) {
        Intrinsics.checkNotNullParameter((Object)request, (String)"request");
        Intrinsics.checkNotNullParameter((Object)connectHostParams, (String)"connectHostParams");
        String string = request.getParameter("route");
        if (string == null) {
            string = connectHostParams.getDefaultClientAppRoute();
        }
        String clientRoute = string;
        String iframeUrl = this.urlBuilder.buildHostActionUrl("clientapp") + "?result=" + connectHostParams.getClientAppResult() + '#' + clientRoute;
        ConfluenceUser user = AuthenticatedUserThreadLocal.get();
        Pair[] pairArray = new Pair[]{TuplesKt.to((Object)"addonKey", (Object)this.addonKey), TuplesKt.to((Object)"pageKey", (Object)connectHostParams.getPageKey()), TuplesKt.to((Object)"iframeUrl", (Object)iframeUrl), TuplesKt.to((Object)"userName", (Object)user.getName()), TuplesKt.to((Object)"userKey", (Object)user.getKey())};
        return MapsKt.mapOf((Pair[])pairArray);
    }
}

