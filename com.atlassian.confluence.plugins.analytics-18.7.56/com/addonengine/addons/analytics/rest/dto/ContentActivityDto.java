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
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00006\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\u0006\u0010\u0007\u001a\u00020\b\u0012\b\u0010\t\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000b\u001a\u0004\u0018\u00010\n\u0012\b\u0010\f\u001a\u0004\u0018\u00010\n\u0012\u0006\u0010\r\u001a\u00020\u0003\u0012\u0006\u0010\u000e\u001a\u00020\u0003\u0012\u0006\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0003H\u00c6\u0003J\t\u0010!\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00c6\u0003J\t\u0010#\u001a\u00020\bH\u00c6\u0003J\u000b\u0010$\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\u000b\u0010%\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\u000b\u0010&\u001a\u0004\u0018\u00010\nH\u00c6\u0003J\t\u0010'\u001a\u00020\u0003H\u00c6\u0003J\t\u0010(\u001a\u00020\u0003H\u00c6\u0003Js\u0010)\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00052\b\b\u0002\u0010\u0007\u001a\u00020\b2\n\b\u0002\u0010\t\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000b\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\n2\b\b\u0002\u0010\r\u001a\u00020\u00032\b\b\u0002\u0010\u000e\u001a\u00020\u00032\b\b\u0002\u0010\u000f\u001a\u00020\u0003H\u00c6\u0001J\u0013\u0010*\u001a\u00020+2\b\u0010,\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010-\u001a\u00020.H\u00d6\u0001J\t\u0010/\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\r\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0013\u0010\t\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0012R\u0013\u0010\u000b\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0014R\u0013\u0010\f\u001a\u0004\u0018\u00010\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0014R\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0019R\u0011\u0010\u0006\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u001bR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u001bR\u0011\u0010\u000e\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u0012R\u0011\u0010\u000f\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001e\u0010\u0012\u00a8\u00060"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/ContentActivityDto;", "", "id", "", "type", "", "title", "link", "Ljava/net/URL;", "createdAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "lastModifiedAt", "lastViewedAt", "commentActivityCount", "usersViewed", "views", "(JLjava/lang/String;Ljava/lang/String;Ljava/net/URL;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;JJJ)V", "getCommentActivityCount", "()J", "getCreatedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getId", "getLastModifiedAt", "getLastViewedAt", "getLink", "()Ljava/net/URL;", "getTitle", "()Ljava/lang/String;", "getType", "getUsersViewed", "getViews", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class ContentActivityDto {
    private final long id;
    @NotNull
    private final String type;
    @NotNull
    private final String title;
    @NotNull
    private final URL link;
    @Nullable
    private final ApiDateTime createdAt;
    @Nullable
    private final ApiDateTime lastModifiedAt;
    @Nullable
    private final ApiDateTime lastViewedAt;
    private final long commentActivityCount;
    private final long usersViewed;
    private final long views;

    public ContentActivityDto(long id, @NotNull String type, @NotNull String title, @NotNull URL link, @Nullable ApiDateTime createdAt, @Nullable ApiDateTime lastModifiedAt, @Nullable ApiDateTime lastViewedAt, long commentActivityCount, long usersViewed, long views) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        this.id = id;
        this.type = type;
        this.title = title;
        this.link = link;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.lastViewedAt = lastViewedAt;
        this.commentActivityCount = commentActivityCount;
        this.usersViewed = usersViewed;
        this.views = views;
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

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    @Nullable
    public final ApiDateTime getCreatedAt() {
        return this.createdAt;
    }

    @Nullable
    public final ApiDateTime getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    @Nullable
    public final ApiDateTime getLastViewedAt() {
        return this.lastViewedAt;
    }

    public final long getCommentActivityCount() {
        return this.commentActivityCount;
    }

    public final long getUsersViewed() {
        return this.usersViewed;
    }

    public final long getViews() {
        return this.views;
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

    @NotNull
    public final URL component4() {
        return this.link;
    }

    @Nullable
    public final ApiDateTime component5() {
        return this.createdAt;
    }

    @Nullable
    public final ApiDateTime component6() {
        return this.lastModifiedAt;
    }

    @Nullable
    public final ApiDateTime component7() {
        return this.lastViewedAt;
    }

    public final long component8() {
        return this.commentActivityCount;
    }

    public final long component9() {
        return this.usersViewed;
    }

    public final long component10() {
        return this.views;
    }

    @NotNull
    public final ContentActivityDto copy(long id, @NotNull String type, @NotNull String title, @NotNull URL link, @Nullable ApiDateTime createdAt, @Nullable ApiDateTime lastModifiedAt, @Nullable ApiDateTime lastViewedAt, long commentActivityCount, long usersViewed, long views) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        return new ContentActivityDto(id, type, title, link, createdAt, lastModifiedAt, lastViewedAt, commentActivityCount, usersViewed, views);
    }

    public static /* synthetic */ ContentActivityDto copy$default(ContentActivityDto contentActivityDto, long l, String string, String string2, URL uRL, ApiDateTime apiDateTime, ApiDateTime apiDateTime2, ApiDateTime apiDateTime3, long l2, long l3, long l4, int n, Object object) {
        if ((n & 1) != 0) {
            l = contentActivityDto.id;
        }
        if ((n & 2) != 0) {
            string = contentActivityDto.type;
        }
        if ((n & 4) != 0) {
            string2 = contentActivityDto.title;
        }
        if ((n & 8) != 0) {
            uRL = contentActivityDto.link;
        }
        if ((n & 0x10) != 0) {
            apiDateTime = contentActivityDto.createdAt;
        }
        if ((n & 0x20) != 0) {
            apiDateTime2 = contentActivityDto.lastModifiedAt;
        }
        if ((n & 0x40) != 0) {
            apiDateTime3 = contentActivityDto.lastViewedAt;
        }
        if ((n & 0x80) != 0) {
            l2 = contentActivityDto.commentActivityCount;
        }
        if ((n & 0x100) != 0) {
            l3 = contentActivityDto.usersViewed;
        }
        if ((n & 0x200) != 0) {
            l4 = contentActivityDto.views;
        }
        return contentActivityDto.copy(l, string, string2, uRL, apiDateTime, apiDateTime2, apiDateTime3, l2, l3, l4);
    }

    @NotNull
    public String toString() {
        return "ContentActivityDto(id=" + this.id + ", type=" + this.type + ", title=" + this.title + ", link=" + this.link + ", createdAt=" + this.createdAt + ", lastModifiedAt=" + this.lastModifiedAt + ", lastViewedAt=" + this.lastViewedAt + ", commentActivityCount=" + this.commentActivityCount + ", usersViewed=" + this.usersViewed + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + this.type.hashCode();
        result = result * 31 + this.title.hashCode();
        result = result * 31 + this.link.hashCode();
        result = result * 31 + (this.createdAt == null ? 0 : this.createdAt.hashCode());
        result = result * 31 + (this.lastModifiedAt == null ? 0 : this.lastModifiedAt.hashCode());
        result = result * 31 + (this.lastViewedAt == null ? 0 : this.lastViewedAt.hashCode());
        result = result * 31 + Long.hashCode(this.commentActivityCount);
        result = result * 31 + Long.hashCode(this.usersViewed);
        result = result * 31 + Long.hashCode(this.views);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ContentActivityDto)) {
            return false;
        }
        ContentActivityDto contentActivityDto = (ContentActivityDto)other;
        if (this.id != contentActivityDto.id) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.type, (Object)contentActivityDto.type)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.title, (Object)contentActivityDto.title)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.link, (Object)contentActivityDto.link)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.createdAt, (Object)contentActivityDto.createdAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastModifiedAt, (Object)contentActivityDto.lastModifiedAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)contentActivityDto.lastViewedAt)) {
            return false;
        }
        if (this.commentActivityCount != contentActivityDto.commentActivityCount) {
            return false;
        }
        if (this.usersViewed != contentActivityDto.usersViewed) {
            return false;
        }
        return this.views == contentActivityDto.views;
    }
}

