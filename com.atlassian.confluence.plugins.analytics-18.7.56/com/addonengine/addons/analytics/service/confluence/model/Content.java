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

import com.addonengine.addons.analytics.service.confluence.model.Space;
import com.addonengine.addons.analytics.service.model.ContentType;
import java.net.URL;
import java.time.Instant;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000B\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0017\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BA\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\b\u0010\b\u001a\u0004\u0018\u00010\t\u0012\b\u0010\n\u001a\u0004\u0018\u00010\t\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\u000e\u00a2\u0006\u0002\u0010\u000fJ\t\u0010\u001d\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u001e\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u001f\u001a\u00020\u0007H\u00c6\u0003J\u000b\u0010 \u001a\u0004\u0018\u00010\tH\u00c6\u0003J\u000b\u0010!\u001a\u0004\u0018\u00010\tH\u00c6\u0003J\t\u0010\"\u001a\u00020\fH\u00c6\u0003J\t\u0010#\u001a\u00020\u000eH\u00c6\u0003JS\u0010$\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\n\b\u0002\u0010\b\u001a\u0004\u0018\u00010\t2\n\b\u0002\u0010\n\u001a\u0004\u0018\u00010\t2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\u000eH\u00c6\u0001J\u0013\u0010%\u001a\u00020&2\b\u0010'\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010(\u001a\u00020)H\u00d6\u0001J\t\u0010*\u001a\u00020\u0007H\u00d6\u0001R\u0013\u0010\b\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\u0011R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0013\u0010\n\u001a\u0004\u0018\u00010\t\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0014\u0010\u0011R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\r\u001a\u00020\u000e\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001c\u00a8\u0006+"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/Content;", "", "type", "Lcom/addonengine/addons/analytics/service/model/ContentType;", "id", "", "title", "", "createdAt", "Ljava/time/Instant;", "lastModifiedAt", "link", "Ljava/net/URL;", "space", "Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "(Lcom/addonengine/addons/analytics/service/model/ContentType;JLjava/lang/String;Ljava/time/Instant;Ljava/time/Instant;Ljava/net/URL;Lcom/addonengine/addons/analytics/service/confluence/model/Space;)V", "getCreatedAt", "()Ljava/time/Instant;", "getId", "()J", "getLastModifiedAt", "getLink", "()Ljava/net/URL;", "getSpace", "()Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "getTitle", "()Ljava/lang/String;", "getType", "()Lcom/addonengine/addons/analytics/service/model/ContentType;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class Content {
    @NotNull
    private final ContentType type;
    private final long id;
    @NotNull
    private final String title;
    @Nullable
    private final Instant createdAt;
    @Nullable
    private final Instant lastModifiedAt;
    @NotNull
    private final URL link;
    @NotNull
    private final Space space;

    public Content(@NotNull ContentType type, long id, @NotNull String title, @Nullable Instant createdAt, @Nullable Instant lastModifiedAt, @NotNull URL link, @NotNull Space space) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        this.type = type;
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
        this.link = link;
        this.space = space;
    }

    @NotNull
    public final ContentType getType() {
        return this.type;
    }

    public final long getId() {
        return this.id;
    }

    @NotNull
    public final String getTitle() {
        return this.title;
    }

    @Nullable
    public final Instant getCreatedAt() {
        return this.createdAt;
    }

    @Nullable
    public final Instant getLastModifiedAt() {
        return this.lastModifiedAt;
    }

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    @NotNull
    public final Space getSpace() {
        return this.space;
    }

    @NotNull
    public final ContentType component1() {
        return this.type;
    }

    public final long component2() {
        return this.id;
    }

    @NotNull
    public final String component3() {
        return this.title;
    }

    @Nullable
    public final Instant component4() {
        return this.createdAt;
    }

    @Nullable
    public final Instant component5() {
        return this.lastModifiedAt;
    }

    @NotNull
    public final URL component6() {
        return this.link;
    }

    @NotNull
    public final Space component7() {
        return this.space;
    }

    @NotNull
    public final Content copy(@NotNull ContentType type, long id, @NotNull String title, @Nullable Instant createdAt, @Nullable Instant lastModifiedAt, @NotNull URL link, @NotNull Space space) {
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)title, (String)"title");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        Intrinsics.checkNotNullParameter((Object)space, (String)"space");
        return new Content(type, id, title, createdAt, lastModifiedAt, link, space);
    }

    public static /* synthetic */ Content copy$default(Content content, ContentType contentType, long l, String string, Instant instant, Instant instant2, URL uRL, Space space, int n, Object object) {
        if ((n & 1) != 0) {
            contentType = content.type;
        }
        if ((n & 2) != 0) {
            l = content.id;
        }
        if ((n & 4) != 0) {
            string = content.title;
        }
        if ((n & 8) != 0) {
            instant = content.createdAt;
        }
        if ((n & 0x10) != 0) {
            instant2 = content.lastModifiedAt;
        }
        if ((n & 0x20) != 0) {
            uRL = content.link;
        }
        if ((n & 0x40) != 0) {
            space = content.space;
        }
        return content.copy(contentType, l, string, instant, instant2, uRL, space);
    }

    @NotNull
    public String toString() {
        return "Content(type=" + (Object)((Object)this.type) + ", id=" + this.id + ", title=" + this.title + ", createdAt=" + this.createdAt + ", lastModifiedAt=" + this.lastModifiedAt + ", link=" + this.link + ", space=" + this.space + ')';
    }

    public int hashCode() {
        int result = this.type.hashCode();
        result = result * 31 + Long.hashCode(this.id);
        result = result * 31 + this.title.hashCode();
        result = result * 31 + (this.createdAt == null ? 0 : this.createdAt.hashCode());
        result = result * 31 + (this.lastModifiedAt == null ? 0 : this.lastModifiedAt.hashCode());
        result = result * 31 + this.link.hashCode();
        result = result * 31 + this.space.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Content)) {
            return false;
        }
        Content content = (Content)other;
        if (this.type != content.type) {
            return false;
        }
        if (this.id != content.id) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.title, (Object)content.title)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.createdAt, (Object)content.createdAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastModifiedAt, (Object)content.lastModifiedAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.link, (Object)content.link)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.space, (Object)content.space);
    }
}

