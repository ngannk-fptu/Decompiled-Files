/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.model;

import com.addonengine.addons.analytics.service.confluence.model.Content;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00002\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0012\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u000e\n\u0000\b\u0086\b\u0018\u00002\u00020\u0001B/\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\b\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\nJ\t\u0010\u0013\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u0014\u001a\u0004\u0018\u00010\u0005H\u00c6\u0003J\t\u0010\u0015\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0016\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\u0017\u001a\u00020\u0007H\u00c6\u0003J=\u0010\u0018\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\n\b\u0002\u0010\u0004\u001a\u0004\u0018\u00010\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0019\u001a\u00020\u001a2\b\u0010\u001b\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u001c\u001a\u00020\u001dH\u00d6\u0001J\t\u0010\u001e\u001a\u00020\u001fH\u00d6\u0001R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000eR\u0013\u0010\u0004\u001a\u0004\u0018\u00010\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\fR\u0011\u0010\t\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\f\u00a8\u0006 "}, d2={"Lcom/addonengine/addons/analytics/service/model/ContentActivity;", "", "content", "Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "lastViewedAt", "Ljava/time/Instant;", "commentActivityCount", "", "usersViewed", "views", "(Lcom/addonengine/addons/analytics/service/confluence/model/Content;Ljava/time/Instant;JJJ)V", "getCommentActivityCount", "()J", "getContent", "()Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "getLastViewedAt", "()Ljava/time/Instant;", "getUsersViewed", "getViews", "component1", "component2", "component3", "component4", "component5", "copy", "equals", "", "other", "hashCode", "", "toString", "", "analytics"})
public final class ContentActivity {
    @NotNull
    private final Content content;
    @Nullable
    private final Instant lastViewedAt;
    private final long commentActivityCount;
    private final long usersViewed;
    private final long views;

    public ContentActivity(@NotNull Content content, @Nullable Instant lastViewedAt, long commentActivityCount, long usersViewed, long views) {
        Intrinsics.checkNotNullParameter((Object)content, (String)"content");
        this.content = content;
        this.lastViewedAt = lastViewedAt;
        this.commentActivityCount = commentActivityCount;
        this.usersViewed = usersViewed;
        this.views = views;
    }

    @NotNull
    public final Content getContent() {
        return this.content;
    }

    @Nullable
    public final Instant getLastViewedAt() {
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

    @NotNull
    public final Content component1() {
        return this.content;
    }

    @Nullable
    public final Instant component2() {
        return this.lastViewedAt;
    }

    public final long component3() {
        return this.commentActivityCount;
    }

    public final long component4() {
        return this.usersViewed;
    }

    public final long component5() {
        return this.views;
    }

    @NotNull
    public final ContentActivity copy(@NotNull Content content, @Nullable Instant lastViewedAt, long commentActivityCount, long usersViewed, long views) {
        Intrinsics.checkNotNullParameter((Object)content, (String)"content");
        return new ContentActivity(content, lastViewedAt, commentActivityCount, usersViewed, views);
    }

    public static /* synthetic */ ContentActivity copy$default(ContentActivity contentActivity, Content content, Instant instant, long l, long l2, long l3, int n, Object object) {
        if ((n & 1) != 0) {
            content = contentActivity.content;
        }
        if ((n & 2) != 0) {
            instant = contentActivity.lastViewedAt;
        }
        if ((n & 4) != 0) {
            l = contentActivity.commentActivityCount;
        }
        if ((n & 8) != 0) {
            l2 = contentActivity.usersViewed;
        }
        if ((n & 0x10) != 0) {
            l3 = contentActivity.views;
        }
        return contentActivity.copy(content, instant, l, l2, l3);
    }

    @NotNull
    public String toString() {
        return "ContentActivity(content=" + this.content + ", lastViewedAt=" + this.lastViewedAt + ", commentActivityCount=" + this.commentActivityCount + ", usersViewed=" + this.usersViewed + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.content.hashCode();
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
        if (!(other instanceof ContentActivity)) {
            return false;
        }
        ContentActivity contentActivity = (ContentActivity)other;
        if (!Intrinsics.areEqual((Object)this.content, (Object)contentActivity.content)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)contentActivity.lastViewedAt)) {
            return false;
        }
        if (this.commentActivityCount != contentActivity.commentActivityCount) {
            return false;
        }
        if (this.usersViewed != contentActivity.usersViewed) {
            return false;
        }
        return this.views == contentActivity.views;
    }
}

