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
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00004\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0016\n\u0002\u0010\u000b\n\u0002\b\u0004\b\u0087\b\u0018\u00002\u00020\u0001B;\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u0012\b\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u00a2\u0006\u0002\u0010\rJ\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u001b\u001a\u0004\u0018\u00010\u0003H\u00c6\u0003J\u0010\u0010\u001c\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003\u00a2\u0006\u0002\u0010\u000fJ\u000b\u0010\u001d\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010\u001e\u001a\u00020\nH\u00c6\u0003J\t\u0010\u001f\u001a\u00020\fH\u00c6\u0003JP\u0010 \u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00032\n\b\u0002\u0010\u0005\u001a\u0004\u0018\u00010\u00062\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\fH\u00c6\u0001\u00a2\u0006\u0002\u0010!J\u0013\u0010\"\u001a\u00020#2\b\u0010$\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010%\u001a\u00020\u0006H\u00d6\u0001J\t\u0010&\u001a\u00020\u0003H\u00d6\u0001R\u0015\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\n\n\u0002\u0010\u0010\u001a\u0004\b\u000e\u0010\u000fR\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019\u00a8\u0006'"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ContentUserViewsDto;", "", "type", "", "userId", "lastVersionViewed", "", "lastVersionViewedUrl", "Ljava/net/URL;", "lastViewedAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "views", "", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/net/URL;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;J)V", "getLastVersionViewed", "()Ljava/lang/Integer;", "Ljava/lang/Integer;", "getLastVersionViewedUrl", "()Ljava/net/URL;", "getLastViewedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getType", "()Ljava/lang/String;", "getUserId", "getViews", "()J", "component1", "component2", "component3", "component4", "component5", "component6", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/Integer;Ljava/net/URL;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;J)Lcom/addonengine/addons/analytics/rest/dto/ContentUserViewsDto;", "equals", "", "other", "hashCode", "toString", "analytics"})
public final class ContentUserViewsDto {
    @NotNull
    private final String type;
    @Nullable
    private final String userId;
    @Nullable
    private final Integer lastVersionViewed;
    @Nullable
    private final URL lastVersionViewedUrl;
    @NotNull
    private final ApiDateTime lastViewedAt;
    private final long views;

    public ContentUserViewsDto(@NotNull String type, @Nullable String userId, @Nullable Integer lastVersionViewed, @Nullable URL lastVersionViewedUrl, @NotNull ApiDateTime lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        this.type = type;
        this.userId = userId;
        this.lastVersionViewed = lastVersionViewed;
        this.lastVersionViewedUrl = lastVersionViewedUrl;
        this.lastViewedAt = lastViewedAt;
        this.views = views;
    }

    @NotNull
    public final String getType() {
        return this.type;
    }

    @Nullable
    public final String getUserId() {
        return this.userId;
    }

    @Nullable
    public final Integer getLastVersionViewed() {
        return this.lastVersionViewed;
    }

    @Nullable
    public final URL getLastVersionViewedUrl() {
        return this.lastVersionViewedUrl;
    }

    @NotNull
    public final ApiDateTime getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getViews() {
        return this.views;
    }

    @NotNull
    public final String component1() {
        return this.type;
    }

    @Nullable
    public final String component2() {
        return this.userId;
    }

    @Nullable
    public final Integer component3() {
        return this.lastVersionViewed;
    }

    @Nullable
    public final URL component4() {
        return this.lastVersionViewedUrl;
    }

    @NotNull
    public final ApiDateTime component5() {
        return this.lastViewedAt;
    }

    public final long component6() {
        return this.views;
    }

    @NotNull
    public final ContentUserViewsDto copy(@NotNull String type, @Nullable String userId, @Nullable Integer lastVersionViewed, @Nullable URL lastVersionViewedUrl, @NotNull ApiDateTime lastViewedAt, long views) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)lastViewedAt, (String)"lastViewedAt");
        return new ContentUserViewsDto(type, userId, lastVersionViewed, lastVersionViewedUrl, lastViewedAt, views);
    }

    public static /* synthetic */ ContentUserViewsDto copy$default(ContentUserViewsDto contentUserViewsDto, String string, String string2, Integer n, URL uRL, ApiDateTime apiDateTime, long l, int n2, Object object) {
        if ((n2 & 1) != 0) {
            string = contentUserViewsDto.type;
        }
        if ((n2 & 2) != 0) {
            string2 = contentUserViewsDto.userId;
        }
        if ((n2 & 4) != 0) {
            n = contentUserViewsDto.lastVersionViewed;
        }
        if ((n2 & 8) != 0) {
            uRL = contentUserViewsDto.lastVersionViewedUrl;
        }
        if ((n2 & 0x10) != 0) {
            apiDateTime = contentUserViewsDto.lastViewedAt;
        }
        if ((n2 & 0x20) != 0) {
            l = contentUserViewsDto.views;
        }
        return contentUserViewsDto.copy(string, string2, n, uRL, apiDateTime, l);
    }

    @NotNull
    public String toString() {
        return "ContentUserViewsDto(type=" + this.type + ", userId=" + this.userId + ", lastVersionViewed=" + this.lastVersionViewed + ", lastVersionViewedUrl=" + this.lastVersionViewedUrl + ", lastViewedAt=" + this.lastViewedAt + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + (this.userId == null ? 0 : this.userId.hashCode());
        result = result * 31 + (this.lastVersionViewed == null ? 0 : ((Object)this.lastVersionViewed).hashCode());
        result = result * 31 + (this.lastVersionViewedUrl == null ? 0 : this.lastVersionViewedUrl.hashCode());
        result = result * 31 + this.lastViewedAt.hashCode();
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentUserViewsDto)) {
            return false;
        }
        ContentUserViewsDto contentUserViewsDto = (ContentUserViewsDto)other;
        if (!Intrinsics.areEqual((Object)this.type, (Object)contentUserViewsDto.type)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userId, (Object)contentUserViewsDto.userId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastVersionViewed, (Object)contentUserViewsDto.lastVersionViewed)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastVersionViewedUrl, (Object)contentUserViewsDto.lastVersionViewedUrl)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)contentUserViewsDto.lastViewedAt)) {
            return false;
        }
        return this.views == contentUserViewsDto.views;
    }
}

