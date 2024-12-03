/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.web.context;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000\"\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0006J\t\u0010\u000b\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J'\u0010\u000e\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u000f\u001a\u00020\u00102\b\u0010\u0011\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0012\u001a\u00020\u0013H\u00d6\u0001J\t\u0010\u0014\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0007\u0010\bR\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\b\u00a8\u0006\u0015"}, d2={"Lcom/addonengine/addons/analytics/web/context/ConnectHostParams;", "", "pageKey", "", "clientAppResult", "defaultClientAppRoute", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", "getClientAppResult", "()Ljava/lang/String;", "getDefaultClientAppRoute", "getPageKey", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class ConnectHostParams {
    @NotNull
    private final String pageKey;
    @NotNull
    private final String clientAppResult;
    @NotNull
    private final String defaultClientAppRoute;

    public ConnectHostParams(@NotNull String pageKey, @NotNull String clientAppResult, @NotNull String defaultClientAppRoute) {
        Intrinsics.checkNotNullParameter((Object)pageKey, (String)"pageKey");
        Intrinsics.checkNotNullParameter((Object)clientAppResult, (String)"clientAppResult");
        Intrinsics.checkNotNullParameter((Object)defaultClientAppRoute, (String)"defaultClientAppRoute");
        this.pageKey = pageKey;
        this.clientAppResult = clientAppResult;
        this.defaultClientAppRoute = defaultClientAppRoute;
    }

    @NotNull
    public final String getPageKey() {
        return this.pageKey;
    }

    @NotNull
    public final String getClientAppResult() {
        return this.clientAppResult;
    }

    @NotNull
    public final String getDefaultClientAppRoute() {
        return this.defaultClientAppRoute;
    }

    @NotNull
    public final String component1() {
        return this.pageKey;
    }

    @NotNull
    public final String component2() {
        return this.clientAppResult;
    }

    @NotNull
    public final String component3() {
        return this.defaultClientAppRoute;
    }

    @NotNull
    public final ConnectHostParams copy(@NotNull String pageKey, @NotNull String clientAppResult, @NotNull String defaultClientAppRoute) {
        Intrinsics.checkNotNullParameter((Object)pageKey, (String)"pageKey");
        Intrinsics.checkNotNullParameter((Object)clientAppResult, (String)"clientAppResult");
        Intrinsics.checkNotNullParameter((Object)defaultClientAppRoute, (String)"defaultClientAppRoute");
        return new ConnectHostParams(pageKey, clientAppResult, defaultClientAppRoute);
    }

    public static /* synthetic */ ConnectHostParams copy$default(ConnectHostParams connectHostParams, String string, String string2, String string3, int n, Object object) {
        if ((n & 1) != 0) {
            string = connectHostParams.pageKey;
        }
        if ((n & 2) != 0) {
            string2 = connectHostParams.clientAppResult;
        }
        if ((n & 4) != 0) {
            string3 = connectHostParams.defaultClientAppRoute;
        }
        return connectHostParams.copy(string, string2, string3);
    }

    @NotNull
    public String toString() {
        return "ConnectHostParams(pageKey=" + this.pageKey + ", clientAppResult=" + this.clientAppResult + ", defaultClientAppRoute=" + this.defaultClientAppRoute + ')';
    }

    public int hashCode() {
        int result = this.pageKey.hashCode();
        result = result * 31 + this.clientAppResult.hashCode();
        result = result * 31 + this.defaultClientAppRoute.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConnectHostParams)) {
            return false;
        }
        ConnectHostParams connectHostParams = (ConnectHostParams)other;
        if (!Intrinsics.areEqual((Object)this.pageKey, (Object)connectHostParams.pageKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.clientAppResult, (Object)connectHostParams.clientAppResult)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.defaultClientAppRoute, (Object)connectHostParams.defaultClientAppRoute);
    }
}

