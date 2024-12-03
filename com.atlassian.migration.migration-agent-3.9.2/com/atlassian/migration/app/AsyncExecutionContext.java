/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.migration.app.dto.check.AppPreflightCheckInternalResponse
 *  com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy
 *  kotlin.Metadata
 *  kotlin.jvm.internal.DefaultConstructorMarker
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.atlassian.migration.app;

import com.atlassian.migration.app.dto.check.AppPreflightCheckInternalResponse;
import com.atlassian.migration.app.dto.check.VendorCheckRepositoryProxy;
import java.util.concurrent.Future;
import kotlin.Metadata;
import kotlin.jvm.internal.DefaultConstructorMarker;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 7, 1}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0010\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0082\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\b\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0014\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0006H\u00c6\u0003J\u0011\u0010\u0017\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bH\u00c6\u0003J9\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\u0010\b\u0002\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bH\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\fR\"\u0010\u0007\u001a\n\u0012\u0004\u0012\u00020\t\u0018\u00010\bX\u0086\u000e\u00a2\u0006\u000e\n\u0000\u001a\u0004\b\u000e\u0010\u000f\"\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013\u00a8\u0006\u001f"}, d2={"Lcom/atlassian/migration/app/AsyncExecutionContext;", "", "appKey", "", "checkId", "proxy", "Lcom/atlassian/migration/app/dto/check/VendorCheckRepositoryProxy;", "future", "Ljava/util/concurrent/Future;", "Lcom/atlassian/migration/app/dto/check/AppPreflightCheckInternalResponse;", "(Ljava/lang/String;Ljava/lang/String;Lcom/atlassian/migration/app/dto/check/VendorCheckRepositoryProxy;Ljava/util/concurrent/Future;)V", "getAppKey", "()Ljava/lang/String;", "getCheckId", "getFuture", "()Ljava/util/concurrent/Future;", "setFuture", "(Ljava/util/concurrent/Future;)V", "getProxy", "()Lcom/atlassian/migration/app/dto/check/VendorCheckRepositoryProxy;", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "app-migration-assistant"})
final class AsyncExecutionContext {
    @NotNull
    private final String appKey;
    @NotNull
    private final String checkId;
    @NotNull
    private final VendorCheckRepositoryProxy proxy;
    @Nullable
    private Future<AppPreflightCheckInternalResponse> future;

    public AsyncExecutionContext(@NotNull String appKey, @NotNull String checkId, @NotNull VendorCheckRepositoryProxy proxy, @Nullable Future<AppPreflightCheckInternalResponse> future) {
        Intrinsics.checkNotNullParameter((Object)appKey, (String)"appKey");
        Intrinsics.checkNotNullParameter((Object)checkId, (String)"checkId");
        Intrinsics.checkNotNullParameter((Object)proxy, (String)"proxy");
        this.appKey = appKey;
        this.checkId = checkId;
        this.proxy = proxy;
        this.future = future;
    }

    public /* synthetic */ AsyncExecutionContext(String string, String string2, VendorCheckRepositoryProxy vendorCheckRepositoryProxy, Future future, int n, DefaultConstructorMarker defaultConstructorMarker) {
        if ((n & 8) != 0) {
            future = null;
        }
        this(string, string2, vendorCheckRepositoryProxy, future);
    }

    @NotNull
    public final String getAppKey() {
        return this.appKey;
    }

    @NotNull
    public final String getCheckId() {
        return this.checkId;
    }

    @NotNull
    public final VendorCheckRepositoryProxy getProxy() {
        return this.proxy;
    }

    @Nullable
    public final Future<AppPreflightCheckInternalResponse> getFuture() {
        return this.future;
    }

    public final void setFuture(@Nullable Future<AppPreflightCheckInternalResponse> future) {
        this.future = future;
    }

    @NotNull
    public final String component1() {
        return this.appKey;
    }

    @NotNull
    public final String component2() {
        return this.checkId;
    }

    @NotNull
    public final VendorCheckRepositoryProxy component3() {
        return this.proxy;
    }

    @Nullable
    public final Future<AppPreflightCheckInternalResponse> component4() {
        return this.future;
    }

    @NotNull
    public final AsyncExecutionContext copy(@NotNull String appKey, @NotNull String checkId, @NotNull VendorCheckRepositoryProxy proxy, @Nullable Future<AppPreflightCheckInternalResponse> future) {
        Intrinsics.checkNotNullParameter((Object)appKey, (String)"appKey");
        Intrinsics.checkNotNullParameter((Object)checkId, (String)"checkId");
        Intrinsics.checkNotNullParameter((Object)proxy, (String)"proxy");
        return new AsyncExecutionContext(appKey, checkId, proxy, future);
    }

    public static /* synthetic */ AsyncExecutionContext copy$default(AsyncExecutionContext asyncExecutionContext, String string, String string2, VendorCheckRepositoryProxy vendorCheckRepositoryProxy, Future future, int n, Object object) {
        if ((n & 1) != 0) {
            string = asyncExecutionContext.appKey;
        }
        if ((n & 2) != 0) {
            string2 = asyncExecutionContext.checkId;
        }
        if ((n & 4) != 0) {
            vendorCheckRepositoryProxy = asyncExecutionContext.proxy;
        }
        if ((n & 8) != 0) {
            future = asyncExecutionContext.future;
        }
        return asyncExecutionContext.copy(string, string2, vendorCheckRepositoryProxy, future);
    }

    @NotNull
    public String toString() {
        return "AsyncExecutionContext(appKey=" + this.appKey + ", checkId=" + this.checkId + ", proxy=" + this.proxy + ", future=" + this.future + ')';
    }

    public int hashCode() {
        int result = this.appKey.hashCode();
        result = result * 31 + this.checkId.hashCode();
        result = result * 31 + this.proxy.hashCode();
        result = result * 31 + (this.future == null ? 0 : this.future.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AsyncExecutionContext)) {
            return false;
        }
        AsyncExecutionContext asyncExecutionContext = (AsyncExecutionContext)other;
        if (!Intrinsics.areEqual((Object)this.appKey, (Object)asyncExecutionContext.appKey)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.checkId, (Object)asyncExecutionContext.checkId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.proxy, (Object)asyncExecutionContext.proxy)) {
            return false;
        }
        return Intrinsics.areEqual(this.future, asyncExecutionContext.future);
    }
}

