/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.codehaus.jackson.annotate.JsonAutoDetect
 *  org.codehaus.jackson.annotate.JsonProperty
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.rest.dto.restrictions;

import com.addonengine.addons.analytics.rest.dto.restrictions.SpaceRestrictionDto;
import java.net.URL;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonAutoDetect
@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u000b\n\u0002\u0010\u000b\n\u0000\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\b\u0087\b\u0018\u00002\u00020\u0001B%\u0012\b\b\u0001\u0010\u0002\u001a\u00020\u0003\u0012\b\b\u0001\u0010\u0004\u001a\u00020\u0003\u0012\n\b\u0001\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\u0002\u0010\u0007J\t\u0010\r\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u000e\u001a\u00020\u0003H\u00c6\u0003J\u000b\u0010\u000f\u001a\u0004\u0018\u00010\u0006H\u00c6\u0003J)\u0010\u0010\u001a\u00020\u00002\b\b\u0003\u0010\u0002\u001a\u00020\u00032\b\b\u0003\u0010\u0004\u001a\u00020\u00032\n\b\u0003\u0010\u0005\u001a\u0004\u0018\u00010\u0006H\u00c6\u0001J\u0013\u0010\u0011\u001a\u00020\u00122\b\u0010\u0013\u001a\u0004\u0018\u00010\u0014H\u00d6\u0003J\t\u0010\u0015\u001a\u00020\u0016H\u00d6\u0001J\t\u0010\u0017\u001a\u00020\u0003H\u00d6\u0001R\u0011\u0010\u0004\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\tR\u0013\u0010\u0005\u001a\u0004\u0018\u00010\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\n\u0010\u000bR\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\f\u0010\t\u00a8\u0006\u0018"}, d2={"Lcom/addonengine/addons/analytics/rest/dto/restrictions/SpaceUserRestrictionDto;", "Lcom/addonengine/addons/analytics/rest/dto/restrictions/SpaceRestrictionDto;", "userId", "", "displayName", "profilePictureUrl", "Ljava/net/URL;", "(Ljava/lang/String;Ljava/lang/String;Ljava/net/URL;)V", "getDisplayName", "()Ljava/lang/String;", "getProfilePictureUrl", "()Ljava/net/URL;", "getUserId", "component1", "component2", "component3", "copy", "equals", "", "other", "", "hashCode", "", "toString", "analytics"})
public final class SpaceUserRestrictionDto
implements SpaceRestrictionDto {
    @NotNull
    private final String userId;
    @NotNull
    private final String displayName;
    @Nullable
    private final URL profilePictureUrl;

    public SpaceUserRestrictionDto(@JsonProperty(value="userId") @NotNull String userId, @JsonProperty(value="displayName") @NotNull String displayName, @JsonProperty(value="profilePictureUrl") @Nullable URL profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)userId, (String)"userId");
        Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
        this.userId = userId;
        this.displayName = displayName;
        this.profilePictureUrl = profilePictureUrl;
    }

    @NotNull
    public final String getUserId() {
        return this.userId;
    }

    @NotNull
    public final String getDisplayName() {
        return this.displayName;
    }

    @Nullable
    public final URL getProfilePictureUrl() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final String component1() {
        return this.userId;
    }

    @NotNull
    public final String component2() {
        return this.displayName;
    }

    @Nullable
    public final URL component3() {
        return this.profilePictureUrl;
    }

    @NotNull
    public final SpaceUserRestrictionDto copy(@JsonProperty(value="userId") @NotNull String userId, @JsonProperty(value="displayName") @NotNull String displayName, @JsonProperty(value="profilePictureUrl") @Nullable URL profilePictureUrl) {
        Intrinsics.checkNotNullParameter((Object)userId, (String)"userId");
        Intrinsics.checkNotNullParameter((Object)displayName, (String)"displayName");
        return new SpaceUserRestrictionDto(userId, displayName, profilePictureUrl);
    }

    public static /* synthetic */ SpaceUserRestrictionDto copy$default(SpaceUserRestrictionDto spaceUserRestrictionDto, String string, String string2, URL uRL, int n, Object object) {
        if ((n & 1) != 0) {
            string = spaceUserRestrictionDto.userId;
        }
        if ((n & 2) != 0) {
            string2 = spaceUserRestrictionDto.displayName;
        }
        if ((n & 4) != 0) {
            uRL = spaceUserRestrictionDto.profilePictureUrl;
        }
        return spaceUserRestrictionDto.copy(string, string2, uRL);
    }

    @NotNull
    public String toString() {
        return "SpaceUserRestrictionDto(userId=" + this.userId + ", displayName=" + this.displayName + ", profilePictureUrl=" + this.profilePictureUrl + ')';
    }

    public int hashCode() {
        int result = this.userId.hashCode();
        result = result * 31 + this.displayName.hashCode();
        result = result * 31 + (this.profilePictureUrl == null ? 0 : this.profilePictureUrl.hashCode());
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SpaceUserRestrictionDto)) {
            return false;
        }
        SpaceUserRestrictionDto spaceUserRestrictionDto = (SpaceUserRestrictionDto)other;
        if (!Intrinsics.areEqual((Object)this.userId, (Object)spaceUserRestrictionDto.userId)) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.displayName, (Object)spaceUserRestrictionDto.displayName)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.profilePictureUrl, (Object)spaceUserRestrictionDto.profilePictureUrl);
    }
}

