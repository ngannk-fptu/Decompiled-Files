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
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u00000\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u000e\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0003\u0012\u0006\u0010\u0005\u001a\u00020\u0006\u0012\u0006\u0010\u0007\u001a\u00020\b\u00a2\u0006\u0002\u0010\tJ\t\u0010\u0011\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0012\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0013\u001a\u00020\u0006H\u00c6\u0003J\t\u0010\u0014\u001a\u00020\bH\u00c6\u0003J1\u0010\u0015\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00032\b\b\u0002\u0010\u0005\u001a\u00020\u00062\b\b\u0002\u0010\u0007\u001a\u00020\bH\u00c6\u0001J\u0013\u0010\u0016\u001a\u00020\u00172\b\u0010\u0018\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0019\u001a\u00020\u001aH\u00d6\u0001J\t\u0010\u001b\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0005\u001a\u00020\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u0007\u001a\u00020\b\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000e\u0010\u000fR\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\u0010\u0010\r\u00a8\u0006\u001c"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/SpaceDetailsDto;", "", "key", "", "name", "createdAt", "Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "link", "Ljava/net/URL;", "(Ljava/lang/String;Ljava/lang/String;Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;Ljava/net/URL;)V", "getCreatedAt", "()Lcom/addonengine/addons/analytics/rest/util/ApiDateTime;", "getKey", "()Ljava/lang/String;", "getLink", "()Ljava/net/URL;", "getName", "component1", "component2", "component3", "component4", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class SpaceDetailsDto {
    @NotNull
    private final String key;
    @NotNull
    private final String name;
    @NotNull
    private final ApiDateTime createdAt;
    @NotNull
    private final URL link;

    public SpaceDetailsDto(@NotNull String key, @NotNull String name, @NotNull ApiDateTime createdAt, @NotNull URL link) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)createdAt, (String)"createdAt");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        this.key = key;
        this.name = name;
        this.createdAt = createdAt;
        this.link = link;
    }

    @NotNull
    public final String getKey() {
        return this.key;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final ApiDateTime getCreatedAt() {
        return this.createdAt;
    }

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    @NotNull
    public final String component1() {
        return this.key;
    }

    @NotNull
    public final String component2() {
        return this.name;
    }

    @NotNull
    public final ApiDateTime component3() {
        return this.createdAt;
    }

    @NotNull
    public final URL component4() {
        return this.link;
    }

    @NotNull
    public final SpaceDetailsDto copy(@NotNull String key, @NotNull String name, @NotNull ApiDateTime createdAt, @NotNull URL link) {
        Intrinsics.checkNotNullParameter((Object)key, (String)"key");
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)createdAt, (String)"createdAt");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        return new SpaceDetailsDto(key, name, createdAt, link);
    }

    public static /* synthetic */ SpaceDetailsDto copy$default(SpaceDetailsDto spaceDetailsDto, String string, String string2, ApiDateTime apiDateTime, URL uRL, int n, Object object) {
        if ((n & 1) != 0) {
            string = spaceDetailsDto.key;
        }
        if ((n & 2) != 0) {
            string2 = spaceDetailsDto.name;
        }
        if ((n & 4) != 0) {
            apiDateTime = spaceDetailsDto.createdAt;
        }
        if ((n & 8) != 0) {
            uRL = spaceDetailsDto.link;
        }
        return spaceDetailsDto.copy(string, string2, apiDateTime, uRL);
    }

    @NotNull
    public String toString() {
        return "SpaceDetailsDto(key=" + this.key + ", name=" + this.name + ", createdAt=" + this.createdAt + ", link=" + this.link + ')';
    }

    public int hashCode() {
        int result = this.key.hashCode();
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.createdAt.hashCode();
        result = result * 31 + this.link.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceDetailsDto)) {
            return false;
        }
        SpaceDetailsDto spaceDetailsDto = (SpaceDetailsDto)other;
        if (!Intrinsics.areEqual((Object)this.key, (Object)spaceDetailsDto.key)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)spaceDetailsDto.name)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.createdAt, (Object)spaceDetailsDto.createdAt)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.link, (Object)spaceDetailsDto.link);
    }
}

