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

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000*\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u001d\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001BM\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\u0006\u0012\u0006\u0010\b\u001a\u00020\u0006\u0012\u0006\u0010\t\u001a\u00020\u0006\u0012\u0006\u0010\n\u001a\u00020\u0006\u0012\u0006\u0010\u000b\u001a\u00020\u0006\u0012\u0006\u0010\f\u001a\u00020\u0006\u00a2\u0006\u0002\u0010\rJ\t\u0010\u0019\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001a\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001b\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001c\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001d\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0006H\u00c6\u0003J\t\u0010 \u001a\u00020\u0006H\u00c6\u0003J\t\u0010!\u001a\u00020\u0006H\u00c6\u0003Jc\u0010\"\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\u00062\b\b\u0002\u0010\b\u001a\u00020\u00062\b\b\u0002\u0010\t\u001a\u00020\u00062\b\b\u0002\u0010\n\u001a\u00020\u00062\b\b\u0002\u0010\u000b\u001a\u00020\u00062\b\b\u0002\u0010\f\u001a\u00020\u0006H\u00c6\u0001J\u0013\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010&\u001a\u00020'H\u00d6\u0001J\t\u0010(\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\n\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u000fR\u0011\u0010\u000b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u000fR\u0011\u0010\f\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u000fR\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u000fR\u0011\u0010\u0007\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u000fR\u0011\u0010\b\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u000fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0016\u0010\u0017R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0018\u0010\u0017\u00a8\u0006)"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/UserActivityDto;", "", "type", "", "userId", "pageCreated", "", "pageUpdated", "pageViewed", "blogCreated", "blogUpdated", "blogViewed", "commentCreated", "(Ljava/lang/String;Ljava/lang/String;JJJJJJJ)V", "getBlogCreated", "()J", "getBlogUpdated", "getBlogViewed", "getCommentCreated", "getPageCreated", "getPageUpdated", "getPageViewed", "getType", "()Ljava/lang/String;", "getUserId", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class UserActivityDto {
    @NotNull
    private final String type;
    @NotNull
    private final String userId;
    private final long pageCreated;
    private final long pageUpdated;
    private final long pageViewed;
    private final long blogCreated;
    private final long blogUpdated;
    private final long blogViewed;
    private final long commentCreated;

    public UserActivityDto(@NotNull String type, @NotNull String userId, long pageCreated, long pageUpdated, long pageViewed, long blogCreated, long blogUpdated, long blogViewed, long commentCreated) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)userId, (String)"userId");
        this.type = type;
        this.userId = userId;
        this.pageCreated = pageCreated;
        this.pageUpdated = pageUpdated;
        this.pageViewed = pageViewed;
        this.blogCreated = blogCreated;
        this.blogUpdated = blogUpdated;
        this.blogViewed = blogViewed;
        this.commentCreated = commentCreated;
    }

    @NotNull
    public final String getType() {
        return this.type;
    }

    @NotNull
    public final String getUserId() {
        return this.userId;
    }

    public final long getPageCreated() {
        return this.pageCreated;
    }

    public final long getPageUpdated() {
        return this.pageUpdated;
    }

    public final long getPageViewed() {
        return this.pageViewed;
    }

    public final long getBlogCreated() {
        return this.blogCreated;
    }

    public final long getBlogUpdated() {
        return this.blogUpdated;
    }

    public final long getBlogViewed() {
        return this.blogViewed;
    }

    public final long getCommentCreated() {
        return this.commentCreated;
    }

    @NotNull
    public final String component1() {
        return this.type;
    }

    @NotNull
    public final String component2() {
        return this.userId;
    }

    public final long component3() {
        return this.pageCreated;
    }

    public final long component4() {
        return this.pageUpdated;
    }

    public final long component5() {
        return this.pageViewed;
    }

    public final long component6() {
        return this.blogCreated;
    }

    public final long component7() {
        return this.blogUpdated;
    }

    public final long component8() {
        return this.blogViewed;
    }

    public final long component9() {
        return this.commentCreated;
    }

    @NotNull
    public final UserActivityDto copy(@NotNull String type, @NotNull String userId, long pageCreated, long pageUpdated, long pageViewed, long blogCreated, long blogUpdated, long blogViewed, long commentCreated) {
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)userId, (String)"userId");
        return new UserActivityDto(type, userId, pageCreated, pageUpdated, pageViewed, blogCreated, blogUpdated, blogViewed, commentCreated);
    }

    public static /* synthetic */ UserActivityDto copy$default(UserActivityDto userActivityDto, String string, String string2, long l, long l2, long l3, long l4, long l5, long l6, long l7, int n, Object object) {
        if ((n & 1) != 0) {
            string = userActivityDto.type;
        }
        if ((n & 2) != 0) {
            string2 = userActivityDto.userId;
        }
        if ((n & 4) != 0) {
            l = userActivityDto.pageCreated;
        }
        if ((n & 8) != 0) {
            l2 = userActivityDto.pageUpdated;
        }
        if ((n & 0x10) != 0) {
            l3 = userActivityDto.pageViewed;
        }
        if ((n & 0x20) != 0) {
            l4 = userActivityDto.blogCreated;
        }
        if ((n & 0x40) != 0) {
            l5 = userActivityDto.blogUpdated;
        }
        if ((n & 0x80) != 0) {
            l6 = userActivityDto.blogViewed;
        }
        if ((n & 0x100) != 0) {
            l7 = userActivityDto.commentCreated;
        }
        return userActivityDto.copy(string, string2, l, l2, l3, l4, l5, l6, l7);
    }

    @NotNull
    public String toString() {
        return "UserActivityDto(type=" + this.type + ", userId=" + this.userId + ", pageCreated=" + this.pageCreated + ", pageUpdated=" + this.pageUpdated + ", pageViewed=" + this.pageViewed + ", blogCreated=" + this.blogCreated + ", blogUpdated=" + this.blogUpdated + ", blogViewed=" + this.blogViewed + ", commentCreated=" + this.commentCreated + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + this.userId.hashCode();
        result = result * 31 + Long.hashCode(this.pageCreated);
        result = result * 31 + Long.hashCode(this.pageUpdated);
        result = result * 31 + Long.hashCode(this.pageViewed);
        result = result * 31 + Long.hashCode(this.blogCreated);
        result = result * 31 + Long.hashCode(this.blogUpdated);
        result = result * 31 + Long.hashCode(this.blogViewed);
        result = result * 31 + Long.hashCode(this.commentCreated);
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof UserActivityDto)) {
            return false;
        }
        UserActivityDto userActivityDto = (UserActivityDto)other;
        if (!Intrinsics.areEqual((Object)this.type, (Object)userActivityDto.type)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.userId, (Object)userActivityDto.userId)) {
            return false;
        }
        if (this.pageCreated != userActivityDto.pageCreated) {
            return false;
        }
        if (this.pageUpdated != userActivityDto.pageUpdated) {
            return false;
        }
        if (this.pageViewed != userActivityDto.pageViewed) {
            return false;
        }
        if (this.blogCreated != userActivityDto.blogCreated) {
            return false;
        }
        if (this.blogUpdated != userActivityDto.blogUpdated) {
            return false;
        }
        if (this.blogViewed != userActivityDto.blogViewed) {
            return false;
        }
        return this.commentCreated == userActivityDto.commentCreated;
    }
}

