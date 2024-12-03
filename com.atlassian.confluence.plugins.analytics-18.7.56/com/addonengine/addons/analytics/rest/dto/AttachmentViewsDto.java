/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto;

import com.addonengine.addons.analytics.rest.util.ApiDateTime;
import java.net.URL;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\n\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J=\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\n\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001J\t\u0010 \u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\n\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\r\u00a8\u0006!"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/AttachmentViewsDto;", "", "attachmentId", "", "name", "", "url", "Ljava/net/URL;", "lastViewedAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "views", "(JLjava/lang/String;Ljava/net/URL;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;J)V", "getAttachmentId", "()J", "getLastViewedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getName", "()Ljava/lang/String;", "getUrl", "()Ljava/net/URL;", "getViews", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class AttachmentViewsDto {
    private final long attachmentId;
    @NotNull
    private final String name;
    @NotNull
    private final URL url;
    @Nullable
    private final ApiDateTime lastViewedAt;
    private final long views;

    public AttachmentViewsDto(long attachmentId, @NotNull String name, @NotNull URL url, @Nullable ApiDateTime lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)url, (String)"url");
        this.attachmentId = attachmentId;
        this.name = name;
        this.url = url;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    public final long getAttachmentId() {
        return this.attachmentId;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final URL getUrl() {
        return this.url;
    }

    @Nullable
    public final ApiDateTime getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getViews() {
        return this.views;
    }

    public final long component1() {
        return this.attachmentId;
    }

    @NotNull
    public final String component2() {
        return this.name;
    }

    @NotNull
    public final URL component3() {
        return this.url;
    }

    @Nullable
    public final ApiDateTime component4() {
        return this.lastViewedAt;
    }

    public final long component5() {
        return this.views;
    }

    @NotNull
    public final AttachmentViewsDto copy(long attachmentId, @NotNull String name, @NotNull URL url, @Nullable ApiDateTime lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)url, (String)"url");
        return new AttachmentViewsDto(attachmentId, name, url, lastViewedAt, views);
    }

    public static /* synthetic */ AttachmentViewsDto copy$default(AttachmentViewsDto attachmentViewsDto, long l, String string, URL uRL, ApiDateTime apiDateTime, long l2, int n, Object object) {
        if ((n & 1) != 0) {
            l = attachmentViewsDto.attachmentId;
        }
        if ((n & 2) != 0) {
            string = attachmentViewsDto.name;
        }
        if ((n & 4) != 0) {
            uRL = attachmentViewsDto.url;
        }
        if ((n & 8) != 0) {
            apiDateTime = attachmentViewsDto.lastViewedAt;
        }
        if ((n & 0x10) != 0) {
            l2 = attachmentViewsDto.views;
        }
        return attachmentViewsDto.copy(l, string, uRL, apiDateTime, l2);
    }

    @NotNull
    public String toString() {
        return "AttachmentViewsDto(attachmentId=" + this.attachmentId + ", name=" + this.name + ", url=" + this.url + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.attachmentId);
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.url.hashCode();
        result = result * 31 + (this.lastViewedAt == null ? 0 : this.lastViewedAt.hashCode());
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AttachmentViewsDto)) {
            return false;
        }
        AttachmentViewsDto attachmentViewsDto = (AttachmentViewsDto)other;
        if (this.attachmentId != attachmentViewsDto.attachmentId) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)attachmentViewsDto.name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.url, (Object)attachmentViewsDto.url)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)attachmentViewsDto.lastViewedAt)) {
            return false;
        }
        return this.views == attachmentViewsDto.views;
    }
}

