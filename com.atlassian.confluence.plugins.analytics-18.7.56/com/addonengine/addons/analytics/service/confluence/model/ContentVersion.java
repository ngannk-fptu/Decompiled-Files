/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence.model;

import java.net.URL;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000,\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0003\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B\u001f\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0010\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J)\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0003H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0018H\u00d6\u0001R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/ContentVersion;", "", "version", "", "lastModificationDate", "Ljava/time/Instant;", "versionUrl", "Ljava/net/URL;", "(ILjava/time/Instant;Ljava/net/URL;)V", "getLastModificationDate", "()Ljava/time/Instant;", "getVersion", "()I", "getVersionUrl", "()Ljava/net/URL;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "toString", "", "analytics"})
public final class ContentVersion {
    private final int version;
    @Nullable
    private final Instant lastModificationDate;
    @NotNull
    private final URL versionUrl;

    public ContentVersion(int version, @Nullable Instant lastModificationDate, @NotNull URL versionUrl) {
        Intrinsics.checkNotNullParameter((Object)versionUrl, (String)"versionUrl");
        this.version = version;
        this.lastModificationDate = lastModificationDate;
        this.versionUrl = versionUrl;
    }

    public final int getVersion() {
        return this.version;
    }

    @Nullable
    public final Instant getLastModificationDate() {
        return this.lastModificationDate;
    }

    @NotNull
    public final URL getVersionUrl() {
        return this.versionUrl;
    }

    public final int component1() {
        return this.version;
    }

    @Nullable
    public final Instant component2() {
        return this.lastModificationDate;
    }

    @NotNull
    public final URL component3() {
        return this.versionUrl;
    }

    @NotNull
    public final ContentVersion copy(int version, @Nullable Instant lastModificationDate, @NotNull URL versionUrl) {
        Intrinsics.checkNotNullParameter((Object)versionUrl, (String)"versionUrl");
        return new ContentVersion(version, lastModificationDate, versionUrl);
    }

    public static /* synthetic */ ContentVersion copy$default(ContentVersion contentVersion, int n, Instant instant, URL uRL, int n2, Object object) {
        if ((n2 & 1) != 0) {
            n = contentVersion.version;
        }
        if ((n2 & 2) != 0) {
            instant = contentVersion.lastModificationDate;
        }
        if ((n2 & 4) != 0) {
            uRL = contentVersion.versionUrl;
        }
        return contentVersion.copy(n, instant, uRL);
    }

    @NotNull
    public String toString() {
        return "ContentVersion(version=" + this.version + ", lastModificationDate=" + this.lastModificationDate + ", versionUrl=" + this.versionUrl + ')';
    }

    public int hashCode() {
        int result = Integer.hashCode(this.version);
        result = result * 31 + (this.lastModificationDate == null ? 0 : this.lastModificationDate.hashCode());
        result = result * 31 + this.versionUrl.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentVersion)) {
            return false;
        }
        ContentVersion contentVersion = (ContentVersion)other;
        if (this.version != contentVersion.version) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastModificationDate, (Object)contentVersion.lastModificationDate)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.versionUrl, (Object)contentVersion.versionUrl);
    }
}

