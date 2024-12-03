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
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000:\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b \n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B[\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0003\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\n\u0012\u0006\u0010\u000b\u001a\u00020\n\u0012\b\u0010\f\u001a\u0004\u0018\u00010\r\u0012\b\u0010\u000e\u001a\u0004\u0018\u00010\n\u0012\b\u0010\u000f\u001a\u0004\u0018\u00010\n\u00a2\u0006\u0002\u0010\u0010J\t\u0010!\u001a\u00020\u0003H\u00c6\u0003J\u0010\u0010\"\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJ\t\u0010#\u001a\u00020\u0003H\u00c6\u0003J\t\u0010$\u001a\u00020\u0003H\u00c6\u0003J\t\u0010%\u001a\u00020\u0007H\u00c6\u0003J\t\u0010&\u001a\u00020\u0007H\u00c6\u0003J\t\u0010'\u001a\u00020\nH\u00c6\u0003J\t\u0010(\u001a\u00020\nH\u00c6\u0003J\u000b\u0010)\u001a\u0004\u0018\u00010\rH\u00c6\u0003J\u0010\u0010*\u001a\u0004\u0018\u00010\nH\u00c6\u0003\u00a2\u0006\u0002\u0010\u001eJx\u0010+\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00032\b\b\u0002\u0010\u0006\u001a\u00020\u00072\b\b\u0002\u0010\b\u001a\u00020\u00072\b\b\u0002\u0010\t\u001a\u00020\n2\b\b\u0002\u0010\u000b\u001a\u00020\n2\n\b\u0002\u0010\f\u001a\u0004\u0018\u00010\r2\n\b\u0002\u0010\u000e\u001a\u0004\u0018\u00010\n2\n\b\u0002\u0010\u000f\u001a\u0004\u0018\u00010\nH\u00c6\u0001\u00a2\u0006\u0002\u0010,J\u0013\u0010-\u001a\u00020.2\b\u0010/\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u00100\u001a\u000201H\u00d6\u0001J\t\u00102\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\t\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0011\u0010\u0012R\u0011\u0010\b\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0013\u0010\u0014R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0016R\u0013\u0010\f\u001a\u0004\u0018\u00010\r\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0017\u0010\u0018R\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0019\u0010\u0014R\u0011\u0010\u0005\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001a\u0010\u0016R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001b\u0010\u0016R\u0011\u0010\u000b\u001a\u00020\n\u00a2\u0006\b\n\u0000\u001a\u0004\b\u001c\u0010\u0012R\u0015\u0010\u000e\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b\u001d\u0010\u001eR\u0015\u0010\u000f\u001a\u0004\u0018\u00010\n\u00a2\u0006\n\n\u0002\u0010\u001f\u001a\u0004\b \u0010\u001e\u00a8\u00063"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/InstanceSpaceActivityDto;", "", "key", "", "type", "name", "link", "Ljava/net/URL;", "iconUrl", "created", "", "updated", "lastViewedAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "usersViewed", "views", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/net/URL;Ljava/net/URL;JJLcom/addonengine/addons/analytics/rest/util/ApiDateTime;Ljava/lang/Long;Ljava/lang/Long;)V", "getCreated", "()J", "getIconUrl", "()Ljava/net/URL;", "getKey", "()Ljava/lang/String;", "getLastViewedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getLink", "getName", "getType", "getUpdated", "getUsersViewed", "()Ljava/lang/Long;", "Ljava/lang/Long;", "getViews", "component1", "component10", "component2", "component3", "component4", "component5", "component6", "component7", "component8", "component9", "copy", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/net/URL;Ljava/net/URL;JJLcom/addonengine/addons/analytics/rest/util/ApiDateTime;Ljava/lang/Long;Ljava/lang/Long;)Lcom/addonengine/addons/analytics/rest/dto/InstanceSpaceActivityDto;", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class InstanceSpaceActivityDto {
    @NotNull
    private final String key;
    @NotNull
    private final String type;
    @NotNull
    private final String name;
    @NotNull
    private final URL link;
    @NotNull
    private final URL iconUrl;
    private final long created;
    private final long updated;
    @Nullable
    private final ApiDateTime lastViewedAt;
    @Nullable
    private final Long usersViewed;
    @Nullable
    private final Long views;

    public InstanceSpaceActivityDto(@NotNull String key, @NotNull String type, @NotNull String name, @NotNull URL link, @NotNull URL iconUrl, long created, long updated, @Nullable ApiDateTime lastViewedAt, @Nullable Long usersViewed, @Nullable Long views) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        Intrinsics.checkNotNullParameter((Object)iconUrl, (String)"iconUrl");
        this.key = key;
        this.type = type;
        this.name = name;
        this.link = link;
        this.iconUrl = iconUrl;
        this.created = created;
        this.updated = updated;
        this.lastViewedAt = lastViewedAt;
        this.usersViewed = usersViewed;
        this.views = views;
    }

    @NotNull
    public final String getKey() {
        return this.key;
    }

    @NotNull
    public final String getType() {
        return this.type;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    @NotNull
    public final URL getIconUrl() {
        return this.iconUrl;
    }

    public final long getCreated() {
        return this.created;
    }

    public final long getUpdated() {
        return this.updated;
    }

    @Nullable
    public final ApiDateTime getLastViewedAt() {
        return this.lastViewedAt;
    }

    @Nullable
    public final Long getUsersViewed() {
        return this.usersViewed;
    }

    @Nullable
    public final Long getViews() {
        return this.views;
    }

    @NotNull
    public final String component1() {
        return this.key;
    }

    @NotNull
    public final String component2() {
        return this.type;
    }

    @NotNull
    public final String component3() {
        return this.name;
    }

    @NotNull
    public final URL component4() {
        return this.link;
    }

    @NotNull
    public final URL component5() {
        return this.iconUrl;
    }

    public final long component6() {
        return this.created;
    }

    public final long component7() {
        return this.updated;
    }

    @Nullable
    public final ApiDateTime component8() {
        return this.lastViewedAt;
    }

    @Nullable
    public final Long component9() {
        return this.usersViewed;
    }

    @Nullable
    public final Long component10() {
        return this.views;
    }

    @NotNull
    public final InstanceSpaceActivityDto copy(@NotNull String key, @NotNull String type, @NotNull String name, @NotNull URL link, @NotNull URL iconUrl, long created, long updated, @Nullable ApiDateTime lastViewedAt, @Nullable Long usersViewed, @Nullable Long views) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)type, (String)"type");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        Intrinsics.checkNotNullParameter((Object)iconUrl, (String)"iconUrl");
        return new InstanceSpaceActivityDto(key, type, name, link, iconUrl, created, updated, lastViewedAt, usersViewed, views);
    }

    public static /* synthetic */ InstanceSpaceActivityDto copy$default(InstanceSpaceActivityDto instanceSpaceActivityDto, String string, String string2, String string3, URL uRL, URL uRL2, long l, long l2, ApiDateTime apiDateTime, Long l3, Long l4, int n, Object object) {
        if ((n & 1) != 0) {
            string = instanceSpaceActivityDto.key;
        }
        if ((n & 2) != 0) {
            string2 = instanceSpaceActivityDto.type;
        }
        if ((n & 4) != 0) {
            string3 = instanceSpaceActivityDto.name;
        }
        if ((n & 8) != 0) {
            uRL = instanceSpaceActivityDto.link;
        }
        if ((n & 0x10) != 0) {
            uRL2 = instanceSpaceActivityDto.iconUrl;
        }
        if ((n & 0x20) != 0) {
            l = instanceSpaceActivityDto.created;
        }
        if ((n & 0x40) != 0) {
            l2 = instanceSpaceActivityDto.updated;
        }
        if ((n & 0x80) != 0) {
            apiDateTime = instanceSpaceActivityDto.lastViewedAt;
        }
        if ((n & 0x100) != 0) {
            l3 = instanceSpaceActivityDto.usersViewed;
        }
        if ((n & 0x200) != 0) {
            l4 = instanceSpaceActivityDto.views;
        }
        return instanceSpaceActivityDto.copy(string, string2, string3, uRL, uRL2, l, l2, apiDateTime, l3, l4);
    }

    @NotNull
    public String toString() {
        return "InstanceSpaceActivityDto(key=" + this.key + ", type=" + this.type + ", name=" + this.name + ", link=" + this.link + ", iconUrl=" + this.iconUrl + ", created=" + this.created + ", updated=" + this.updated + ", lastViewedAt=" + this.lastViewedAt + ", usersViewed=" + this.usersViewed + ", views=" + this.views + ')';
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = result * 31 + this.type.hashCode();
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.link.hashCode();
        result = result * 31 + this.iconUrl.hashCode();
        result = result * 31 + Long.hashCode(this.created);
        result = result * 31 + Long.hashCode(this.updated);
        result = result * 31 + (this.lastViewedAt == null ? 0 : this.lastViewedAt.hashCode());
        result = result * 31 + (this.usersViewed == null ? 0 : ((Object)this.usersViewed).hashCode());
        result = result * 31 + (this.views == null ? 0 : ((Object)this.views).hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof InstanceSpaceActivityDto)) {
            return false;
        }
        InstanceSpaceActivityDto instanceSpaceActivityDto = (InstanceSpaceActivityDto)other;
        if (!Intrinsics.areEqual((Object)this.key, (Object)instanceSpaceActivityDto.key)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.type, (Object)instanceSpaceActivityDto.type)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)instanceSpaceActivityDto.name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.link, (Object)instanceSpaceActivityDto.link)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.iconUrl, (Object)instanceSpaceActivityDto.iconUrl)) {
            return false;
        }
        if (this.created != instanceSpaceActivityDto.created) {
            return false;
        }
        if (this.updated != instanceSpaceActivityDto.updated) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.lastViewedAt, (Object)instanceSpaceActivityDto.lastViewedAt)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.usersViewed, (Object)instanceSpaceActivityDto.usersViewed)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.views, (Object)instanceSpaceActivityDto.views);
    }
}

