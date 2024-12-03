/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  org.jetbrains.annotations.NotNull
 */
package com.addonengine.addons.analytics.service.confluence;

import java.net.URL;
import kotlin.Metadata;
import org.jetbrains.annotations.NotNull;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\u0018\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0006\bf\u0018\u00002\u00020\u0001J\u0010\u0010\u0002\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0006\u001a\u00020\u00032\u0006\u0010\u0004\u001a\u00020\u0005H&J\u0010\u0010\u0007\u001a\u00020\u00032\u0006\u0010\b\u001a\u00020\u0005H&J\b\u0010\t\u001a\u00020\u0005H&J\b\u0010\n\u001a\u00020\u0005H&\u00a8\u0006\u000b"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/UrlBuilder;", "", "buildHostActionUrl", "Ljava/net/URL;", "actionName", "", "buildHostAdminActionUrl", "buildHostCanonicalUri", "path", "getAnonymousUserProfilePictureUrl", "getBaseUrl", "analytics"})
public interface UrlBuilder {
    @NotNull
    public URL buildHostCanonicalUri(@NotNull String var1);

    @NotNull
    public URL buildHostAdminActionUrl(@NotNull String var1);

    @NotNull
    public URL buildHostActionUrl(@NotNull String var1);

    @NotNull
    public String getBaseUrl();

    @NotNull
    public String getAnonymousUserProfilePictureUrl();
}

