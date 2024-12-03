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

import com.addonengine.addons.analytics.service.model.SpaceType;
import java.net.URL;
import java.time.Instant;
import java.util.List;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000D\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\b\u0019\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001BK\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0005\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\f\u0012\u0006\u0010\r\u001a\u00020\f\u0012\f\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000f\u00a2\u0006\u0002\u0010\u0010J\t\u0010\u001f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010 \u001a\u00020\u0005H\u00c6\u0003J\t\u0010!\u001a\u00020\u0007H\u00c6\u0003J\t\u0010\"\u001a\u00020\u0005H\u00c6\u0003J\t\u0010#\u001a\u00020\nH\u00c6\u0003J\t\u0010$\u001a\u00020\fH\u00c6\u0003J\t\u0010%\u001a\u00020\fH\u00c6\u0003J\u000f\u0010&\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fH\u00c6\u0003J_\u0010'\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00052\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\f2\b\b\u0002\u0010\r\u001a\u00020\f2\u000e\b\u0002\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000fH\u00c6\u0001J\u0013\u0010(\u001a\u00020)2\b\u0010*\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010+\u001a\u00020,H\u00d6\u0001J\t\u0010-\u001a\u00020\u0005H\u00d6\u0001R\u0017\u0010\u000e\u001a\b\u0012\u0004\u0012\u00020\u00050\u000f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u000b\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u001aR\u0011\u0010\r\u001a\u00020\f\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u001aR\u0011\u0010\b\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001d\u0010\u001e\u00a8\u0006."}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/Space;", "", "id", "", "key", "", "type", "Lcom/addonengine/addons/analytics/service/model/SpaceType;", "name", "createdAt", "Ljava/time/Instant;", "link", "Ljava/net/URL;", "logoUrl", "categories", "", "(JLjava/lang/String;Lcom/addonengine/addons/analytics/service/model/SpaceType;Ljava/lang/String;Ljava/time/Instant;Ljava/net/URL;Ljava/net/URL;Ljava/util/List;)V", "getCategories", "()Ljava/util/List;", "getCreatedAt", "()Ljava/time/Instant;", "getId", "()J", "getKey", "()Ljava/lang/String;", "getLink", "()Ljava/net/URL;", "getLogoUrl", "getName", "getType", "()Lcom/addonengine/addons/analytics/service/model/SpaceType;", "component1", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class Space {
    private final long id;
    @NotNull
    private final String key;
    @NotNull
    private final SpaceType type;
    @NotNull
    private final String name;
    @NotNull
    private final Instant createdAt;
    @NotNull
    private final URL link;
    @NotNull
    private final URL logoUrl;
    @NotNull
    private final List<String> categories;

    public Space(long id, @NotNull String key, @NotNull SpaceType type, @NotNull String name, @NotNull Instant createdAt, @NotNull URL link, @NotNull URL logoUrl, @NotNull List<String> categories) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)createdAt, (String)"createdAt");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        Intrinsics.checkNotNullParameter((Object)logoUrl, (String)"logoUrl");
        Intrinsics.checkNotNullParameter(categories, (String)"categories");
        this.id = id;
        this.key = key;
        this.type = type;
        this.name = name;
        this.createdAt = createdAt;
        this.link = link;
        this.logoUrl = logoUrl;
        this.categories = categories;
    }

    public final long getId() {
        return this.id;
    }

    @NotNull
    public final String getKey() {
        return this.key;
    }

    @NotNull
    public final SpaceType getType() {
        return this.type;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final Instant getCreatedAt() {
        return this.createdAt;
    }

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    @NotNull
    public final URL getLogoUrl() {
        return this.logoUrl;
    }

    @NotNull
    public final List<String> getCategories() {
        return this.categories;
    }

    public final long component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.key;
    }

    @NotNull
    public final SpaceType component3() {
        return this.type;
    }

    @NotNull
    public final String component4() {
        return this.name;
    }

    @NotNull
    public final Instant component5() {
        return this.createdAt;
    }

    @NotNull
    public final URL component6() {
        return this.link;
    }

    @NotNull
    public final URL component7() {
        return this.logoUrl;
    }

    @NotNull
    public final List<String> component8() {
        return this.categories;
    }

    @NotNull
    public final Space copy(long id, @NotNull String key, @NotNull SpaceType type, @NotNull String name, @NotNull Instant createdAt, @NotNull URL link, @NotNull URL logoUrl, @NotNull List<String> categories) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)((Object)type), (String)"type");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)createdAt, (String)"createdAt");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        Intrinsics.checkNotNullParameter((Object)logoUrl, (String)"logoUrl");
        Intrinsics.checkNotNullParameter(categories, (String)"categories");
        return new Space(id, key, type, name, createdAt, link, logoUrl, categories);
    }

    public static /* synthetic */ Space copy$default(Space space, long l, String string, SpaceType spaceType, String string2, Instant instant, URL uRL, URL uRL2, List list, int n, Object object) {
        if ((n & 1) != 0) {
            l = space.id;
        }
        if ((n & 2) != 0) {
            string = space.key;
        }
        if ((n & 4) != 0) {
            spaceType = space.type;
        }
        if ((n & 8) != 0) {
            string2 = space.name;
        }
        if ((n & 0x10) != 0) {
            instant = space.createdAt;
        }
        if ((n & 0x20) != 0) {
            uRL = space.link;
        }
        if ((n & 0x40) != 0) {
            uRL2 = space.logoUrl;
        }
        if ((n & 0x80) != 0) {
            list = space.categories;
        }
        return space.copy(l, string, spaceType, string2, instant, uRL, uRL2, list);
    }

    @NotNull
    public String toString() {
        return "Space(id=" + this.id + ", key=" + this.key + ", type=" + (Object)((Object)this.type) + ", name=" + this.name + ", createdAt=" + this.createdAt + ", link=" + this.link + ", logoUrl=" + this.logoUrl + ", categories=" + this.categories + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + this.key.hashCode();
        result = result * 31 + this.type.hashCode();
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.createdAt.hashCode();
        result = result * 31 + this.link.hashCode();
        result = result * 31 + this.logoUrl.hashCode();
        result = result * 31 + ((Object)this.categories).hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Space)) {
            return false;
        }
        Space space = (Space)other;
        if (this.id != space.id) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.key, (Object)space.key)) {
            return false;
        }
        if (this.type != space.type) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)space.name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.createdAt, (Object)space.createdAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.link, (Object)space.link)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.logoUrl, (Object)space.logoUrl)) {
            return false;
        }
        return Intrinsics.areEqual(this.categories, space.categories);
    }
}

