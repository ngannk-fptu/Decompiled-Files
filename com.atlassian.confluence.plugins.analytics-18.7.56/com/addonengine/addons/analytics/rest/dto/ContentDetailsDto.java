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
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0011\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\b\u0010\u0007\u001a\u0004\u0018\u00010\b\u0012\u0006\u0010\t\u001a\u00020\n\u00a2\u0006\u0002\u0010\u000bJ\t\u0010\u0015\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0005H\u00c6\u0003J\u000b\u0010\u0018\u001a\u0004\u0018\u00010\bH\u00c6\u0003J\t\u0010\u0019\u001a\u00020\nH\u00c6\u0003J=\u0010\u001a\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\n\b\u0002\u0010\u0007\u001a\u0004\u0018\u00010\b2\b\b\u0002\u0010\t\u001a\u00020\nH\u00c6\u0001J\u0013\u0010\u001b\u001a\u00020\u001c2\b\u0010\u001d\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001J\t\u0010 \u001a\u00020\u0005H\u00d6\u0001R\u0013\u0010\u0007\u001a\u0004\u0018\u00010\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0013\u00a8\u0006!"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ContentDetailsDto;", "", "id", "", "type", "", "title", "createdAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "link", "Ljava/net/URL;", "(JLjava/lang/String;Ljava/lang/String;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Ljava/net/URL;)V", "getCreatedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getId", "()J", "getLink", "()Ljava/net/URL;", "getTitle", "()Ljava/lang/String;", "getType", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class ContentDetailsDto {
    private final long id;
    @NotNull
    private final String type;
    @NotNull
    private final String title;
    @Nullable
    private final ApiDateTime createdAt;
    @NotNull
    private final URL link;

    public ContentDetailsDto(long id, @NotNull String type, @NotNull String title, @Nullable ApiDateTime createdAt, @NotNull URL link) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        this.id = id;
        this.type = type;
        this.title = title;
        this.createdAt = createdAt;
        this.link = link;
    }

    public final long getId() {
        return this.id;
    }

    @NotNull
    public final String getType() {
        return this.type;
    }

    @NotNull
    public final String getTitle() {
        return this.title;
    }

    @Nullable
    public final ApiDateTime getCreatedAt() {
        return this.createdAt;
    }

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    public final long component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.type;
    }

    @NotNull
    public final String component3() {
        return this.title;
    }

    @Nullable
    public final ApiDateTime component4() {
        return this.createdAt;
    }

    @NotNull
    public final URL component5() {
        return this.link;
    }

    @NotNull
    public final ContentDetailsDto copy(long id, @NotNull String type, @NotNull String title, @Nullable ApiDateTime createdAt, @NotNull URL link) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        return new ContentDetailsDto(id, type, title, createdAt, link);
    }

    public static /* synthetic */ ContentDetailsDto copy$default(ContentDetailsDto contentDetailsDto, long l, String string, String string2, ApiDateTime apiDateTime, URL uRL, int n, Object object) {
        if ((n & 1) != 0) {
            l = contentDetailsDto.id;
        }
        if ((n & 2) != 0) {
            string = contentDetailsDto.type;
        }
        if ((n & 4) != 0) {
            string2 = contentDetailsDto.title;
        }
        if ((n & 8) != 0) {
            apiDateTime = contentDetailsDto.createdAt;
        }
        if ((n & 0x10) != 0) {
            uRL = contentDetailsDto.link;
        }
        return contentDetailsDto.copy(l, string, string2, apiDateTime, uRL);
    }

    @NotNull
    public String toString() {
        return "ContentDetailsDto(id=" + this.id + ", type=" + this.type + ", title=" + this.title + ", createdAt=" + this.createdAt + ", link=" + this.link + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + this.type.hashCode();
        result = result * 31 + this.title.hashCode();
        result = result * 31 + (this.createdAt == null ? 0 : this.createdAt.hashCode());
        result = result * 31 + this.link.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentDetailsDto)) {
            return false;
        }
        ContentDetailsDto contentDetailsDto = (ContentDetailsDto)other;
        if (this.id != contentDetailsDto.id) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.type, (Object)contentDetailsDto.type)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.title, (Object)contentDetailsDto.title)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.createdAt, (Object)contentDetailsDto.createdAt)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.link, (Object)contentDetailsDto.link);
    }
}

